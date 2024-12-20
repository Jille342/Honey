package com.sirapixel.supersheep;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityLargeFireball;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.lang.reflect.Field;
import java.util.Random;

public class SuperSheep {

   public static boolean supersheep = true;
   public static boolean dragonrider = false;

//   @SidedProxy(clientSide = "com.sirapixel.supersheep.SuperSheep.ClientProxy", serverSide = "com.sirapixel.supersheep.SuperSheep.ServerProxy")
//   public static CommonProxy proxy;

  /*
   @Mod.EventHandler
   public void init(FMLInitializationEvent event) {
      ClientCommandHandler.instance.registerCommand(new CommandSpawnSuperSheep());
      ClientCommandHandler.instance.registerCommand(new CommandSpawnDragonRider());
      MinecraftForge.EVENT_BUS.register(new VictorySpawner());
      MinecraftForge.EVENT_BUS.register(new PositionPacketCancel());
//      proxy.registerRenderers();
   }

   */

   // クライアントサイドでコマンドを実行するクラス
   public static class CommandSpawnSuperSheep extends CommandBase {

      @Override
      public String getCommandName() {
         return "supersheep";  // コマンド名を指定
      }

      @Override
      public String getCommandUsage(ICommandSender sender) {
         return "/supersheep - Super Sheepを出す";
      }

      @Override
      public void processCommand(ICommandSender sender, String[] args) {
         if (sender instanceof EntityPlayer) {
            supersheep = true;
            dragonrider = false;
            Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentText("Victory DanceをSuper Sheepに変更しました！"));
         }
      }

      @Override
      public int getRequiredPermissionLevel() {
         return 0;  // 0 はオペレーター権限なしで実行可能（1 ならオペレーター以上）
      }
   }

   // クライアントサイドでコマンドを実行するクラス
   public static class CommandSpawnDragonRider extends CommandBase {

      @Override
      public String getCommandName() {
         return "dragonrider";  // コマンド名を指定
      }

      @Override
      public String getCommandUsage(ICommandSender sender) {
         return "/dragonrider - Dragon Riderを出す";
      }

      @Override
      public void processCommand(ICommandSender sender, String[] args) {
         if (sender instanceof EntityPlayer) {
            supersheep = false;
            dragonrider = true;
            Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentText("Victory DanceをDragon Riderに変更しました！"));
         }
      }

      @Override
      public int getRequiredPermissionLevel() {
         return 0;  // 0 はオペレーター権限なしで実行可能（1 ならオペレーター以上）
      }
   }

   // カスタムの飛べるひつじクラス
   public static class FlyingSheep extends EntitySheep {

      private float currentYaw = Minecraft.getMinecraft().thePlayer.rotationYaw;      // 現在のYaw
      private float currentPitch = Minecraft.getMinecraft().thePlayer.rotationPitch;    // 現在のPitch
      private float targetYaw = Minecraft.getMinecraft().thePlayer.rotationYaw;       // 目標のYaw
      private float targetPitch = Minecraft.getMinecraft().thePlayer.rotationPitch;     // 目標のPitch

      private static final float SMOOTH_FACTOR = 0.2f;  // 補間の速さ（0.1fで少しずつ変化

      public static final long COLOR_CHANGE_INTERVAL = 4;  // 色変更の間隔（0.2秒 = 4ティック）
      public int currentColorIndex = 0; // 現在の色のインデックス
      public static final EnumDyeColor[] COLORS = {
              EnumDyeColor.RED,
              EnumDyeColor.BLUE,
              EnumDyeColor.YELLOW,
              EnumDyeColor.PURPLE,
              EnumDyeColor.CYAN,
              EnumDyeColor.ORANGE,
              EnumDyeColor.PINK,
              EnumDyeColor.GREEN
      };

      public float legSwingProgress;

      public FlyingSheep(World worldIn) {
         super(worldIn);
         legSwingProgress = 0.0F;
//         this.setNoGravity(true); // 重力を無効に
      }

      @Override
      public void onLivingUpdate() {
         super.onLivingUpdate();

         if (this.riddenByEntity instanceof EntityPlayer) {

            // NoClipするかどうか
            this.noClip = true;  //正直trueかfalseどっちでもいい

            // 0.2秒ごとに色を変更
            if (this.worldObj.getTotalWorldTime() % COLOR_CHANGE_INTERVAL == 0) {
               changeSheepColor();
            }

            // ひつじが移動する前に周囲のブロックを消す
            destroyNearbyBlocks();

            // ひつじに煙のパーティクルを発生させる
            spawnSmoke();

            // 足をパタパタさせるための進行度
            legSwingProgress += 0.1F;  // アニメーションの速度を調整

            // 足の振り幅を変化させる
            if (legSwingProgress > 2) {
               legSwingProgress = 0.0F;
            }

            // 足のアニメーションを調整（振り幅を指定）
            this.limbSwingAmount = (float) Math.sin(legSwingProgress * Math.PI * 2) * 1.1F;

            EntityPlayer player = Minecraft.getMinecraft().thePlayer;

            long currentTime = System.currentTimeMillis();

            // プレイヤーのYawとPitchを取得
            float playerYaw = player.rotationYaw;
            float playerPitch = player.rotationPitch;

            // 目標のYawとPitchを更新
            this.targetYaw = playerYaw;
            this.targetPitch = playerPitch;

            // 現在のYawとPitchを目標値に向けて補間（徐々に変更）
            this.currentYaw = lerp(this.currentYaw, this.targetYaw, SMOOTH_FACTOR);
            this.currentPitch = lerp(this.currentPitch, this.targetPitch, 0.1f);

            // 最終的な動きを適用
            this.rotationYaw = this.currentYaw;
//            this.rotationPitch = this.currentPitch;
            this.rotationYawHead = player.rotationYawHead;

            // motionYの計算（ピッチに基づく）
            this.motionY = -MathHelper.sin(this.currentPitch * (float) Math.PI / 180.0F);

            this.motionX = this.worldObj.getTotalWorldTime() % 5 == 0 ? -MathHelper.sin(this.rotationYaw * (float) Math.PI / 180.0F) * 0.6 : -MathHelper.sin(this.rotationYaw * (float) Math.PI / 180.0F) * 1.1;
            this.motionZ = this.worldObj.getTotalWorldTime() % 5 == 0 ? MathHelper.cos(this.rotationYaw * (float) Math.PI / 180.0F) * 0.6 : MathHelper.cos(this.rotationYaw * (float) Math.PI / 180.0F) * 1.1;

            this.moveEntity(this.motionX, this.motionY, this.motionZ);
         }
      }

      // 線形補間（Lerp）
      private float lerp(float start, float end, float factor) {
         return start + (end - start) * factor;
      }

      public void spawnSmoke() {
         float yaw = this.rotationYaw;  // ひつじの向き（rotationYaw）
         double particleY = this.posY;  // ひつじの少し下の位置

         // ひつじの進行方向ベクトルを取得
         Vec3 lookVec = this.getLook(1.0F);

         // 進行方向に対する横方向のオフセットを計算
         double offsetBaseX = -lookVec.zCoord;  // 横方向のX成分（進行方向に直交）
         double offsetBaseZ = lookVec.xCoord;   // 横方向のZ成分（進行方向に直交）

         // 横に5列に並べる
         for (int i = -2; i <= 2; i++) {  // 中央基準で左から右に5つ配置
            double offsetX = -Math.sin(Math.toRadians(yaw)) * 1 + i * 0.3 * offsetBaseX;  // 横方向基準にスケール
            double offsetZ = Math.cos(Math.toRadians(yaw)) * 1 + i * 0.3 * offsetBaseZ;  // 横方向基準にスケール

            double particleX = this.posX + offsetX;
            double particleZ = this.posZ + offsetZ;

            // 煙のパーティクルを発生させる
            this.worldObj.spawnParticle(
                    EnumParticleTypes.REDSTONE,   // パーティクル名（煙）
                    particleX,                    // X座標
                    particleY,                    // Y座標
                    particleZ,                    // Z座標
                    5,                          // R (255とかではないらしい)
                    0.0,                          // G (255とかではないらしい)
                    5                           // B (255とかではないらしい)
            );
         }
      }

      // ひつじの色を順番に変更するメソッド
      public void changeSheepColor() {
         // 順番に色を変更
         this.setFleeceColor(COLORS[currentColorIndex]);

         // インデックスを更新
         currentColorIndex = (currentColorIndex + 1) % COLORS.length;  // インデックスを循環させる
      }

      public void destroyNearbyBlocks() {
         double sheepX = this.posX;  // ひつじのX座標
         double sheepY = this.posY;  // ひつじのY座標
         double sheepZ = this.posZ;  // ひつじのZ座標
         float sheepYaw = this.rotationYaw; // ひつじのYaw

         // 前方5ブロック、横方向±3ブロックを調べる
         for (int forward = 0; forward <= 5; forward++) { // 前方5ブロック
            for (int sideways = (int) -1.5f; sideways <= 1.5f; sideways++) { // 横方向±1.5ブロック
               for (int yOffset = -2; yOffset <= 2; yOffset++) { // Y方向（高さ）は±2ブロック
                  // ひつじのyawに基づいて位置を計算
                  double offsetX = -Math.sin(Math.toRadians(sheepYaw)) * forward + Math.cos(Math.toRadians(sheepYaw)) * sideways;
                  double offsetZ = Math.cos(Math.toRadians(sheepYaw)) * forward + Math.sin(Math.toRadians(sheepYaw)) * sideways;

                  // 座標を補正して、誤差を最小限に
                  int targetX = MathHelper.floor_double(sheepX + offsetX);
                  int targetY = MathHelper.floor_double(sheepY + yOffset);
                  int targetZ = MathHelper.floor_double(sheepZ + offsetZ);

                  BlockPos blockPos = new BlockPos(targetX, targetY, targetZ);
                  Block block = this.worldObj.getBlockState(blockPos).getBlock();

                  // ブロックを削除
                  if (!block.isAir(this.worldObj, blockPos)) {
                     this.worldObj.setBlockToAir(blockPos);

                     // 効果音とパーティクルを追加
                     if (supersheep) {
                        this.worldObj.playSound(
                                this.posX, this.posY, this.posZ,
                                "mob.chicken.plop", 1F, 0F, true
                        );
                        this.worldObj.spawnParticle(
                                EnumParticleTypes.REDSTONE,
                                targetX + 0.5, targetY + 1, targetZ + 0.5,
                                0.2, 0.2, 0.2
                        );
                     }
                  }
               }
            }
         }
      }
   }

   public static class DragonRider extends EntityDragon {

      public static final double RADIUS = 3; // 半径（ブロックの範囲）

      public static final long INTERVAL = 6;

      public DragonRider(World worldIn) {
         super(worldIn);
      }

      @Override
      public void onLivingUpdate() {
         super.onLivingUpdate();

         if (this.riddenByEntity instanceof EntityPlayer) {

            EntityPlayer player = (EntityPlayer) this.riddenByEntity;

            // NoClipするかどうか
            this.noClip = true;  //正直trueかfalseどっちでもいい

            destroyNearbyBlocks();

            if (this.worldObj.getTotalWorldTime() % INTERVAL == 0) {
               shootFireball(player);
            }

            // プレイヤーのピッチに基づいてmotionYを変更
            float pitch = player.rotationPitch;
            this.rotationPitch = 0;
            this.motionY = -MathHelper.sin(pitch * (float) Math.PI / 180.0F) * 0.5;

            // プレイヤーの方向に前進
            this.rotationYaw = player.rotationYaw - 180;
//            this.rotationYawHead = player.rotationYawHead;
            this.motionX = -MathHelper.sin(player.rotationYaw * (float) Math.PI / 180.0F) * 0.5;
            this.motionZ = MathHelper.cos(player.rotationYaw * (float) Math.PI / 180.0F) * 0.5;

            // 移動を適用
            this.moveEntity(this.motionX, this.motionY, this.motionZ);

            this.animTime += 0.18; // 通常より速く進行するために増加率を上げる
         }
      }

      private void shootFireball(EntityPlayer player) {
         double posX = this.posX;
         double posY = this.posY + this.getEyeHeight();
         double posZ = this.posZ;

         // プレイヤーの視線の角度
         float yaw = player.rotationYaw;
         float pitch = player.rotationPitch;

         // 初速の増幅（デフォルトの2倍の速度に設定）
         double speed = 7.0;
         double motionX = -MathHelper.sin(yaw * (float) Math.PI / 180.0F) * MathHelper.cos(pitch * (float) Math.PI / 180.0F) * speed;
         double motionY = -MathHelper.sin(pitch * (float) Math.PI / 180.0F) * speed;
         double motionZ = MathHelper.cos(yaw * (float) Math.PI / 180.0F) * MathHelper.cos(pitch * (float) Math.PI / 180.0F) * speed;

         CustomLargeFireball fireball = new CustomLargeFireball(this.worldObj, posX + motionX, posY, posZ + motionZ, motionX, motionY, motionZ, 1F);

         fireball.explosionPower = (int) 0.5F;

         // スポーン時にさらに速度を加算
         fireball.accelerationX = motionX * 1.5 + 1; // 加速値を調整（0.1などで速度を強化）
         fireball.accelerationY = motionY * 1.1;
         fireball.accelerationZ = motionZ * 1.5 + 1;

         this.worldObj.spawnEntityInWorld(fireball);
      }

      public void destroyNearbyBlocks() {
         Minecraft mc = Minecraft.getMinecraft();
         double xPos = this.dragonPartBody.posX;
         double yPos = this.dragonPartBody.posY;
         double zPos = this.dragonPartBody.posZ;

         // 半径RADIUS内のブロックを調べて削除
         for (int x = (int) (xPos - RADIUS); x <= (int) (xPos + RADIUS); x++) {
            for (int y = (int) (yPos - RADIUS); y <= (int) (yPos + RADIUS) + 2; y++) {
               for (int z = (int) (zPos - RADIUS); z <= (int) (zPos + RADIUS); z++) {
                  BlockPos blockPos = new BlockPos(x, y, z);
                  Block block = this.worldObj.getBlockState(blockPos).getBlock();

                  double distance = this.getDistanceSq(xPos, yPos, zPos); // 距離を計算

                  // 距離がRADIUS以内のブロックを破壊
                  if (distance <= RADIUS * RADIUS) {
                     if (!block.isAir(this.worldObj, blockPos)) {
                        this.worldObj.setBlockToAir(blockPos);  // ブロックを消す

//                        this.worldObj.createExplosion(this, blockPos.getX(), blockPos.getY(), blockPos.getZ(), 0.5F, true);
                     }
                  }
               }
            }
         }
      }
   }

   public static class VictorySpawner {

      public final Minecraft mc = Minecraft.getMinecraft();
      public World lastWorld = null;  // 最後に接続していたワールド

      public VictorySpawner() {
         MinecraftForge.EVENT_BUS.register(this);
      }
      private boolean delayExecuted = false;  // 遅延処理が実行されたかどうか
      private long delayTime = 400; // 遅延時間（ミリ秒、1秒）
      private long startTime; // 開始時間
      private boolean trigger = false;

      @SubscribeEvent(priority = EventPriority.NORMAL)
      public void chatReader(ClientChatReceivedEvent e) {
         if (e.type == 0) {
            String msg = e.message.getUnformattedText();
            if ((msg.contains("You won! Want to play again? Click here!") || msg.contains("Winner - " + mc.thePlayer.getName()))) {
               // 遅延を開始
               startTime = System.currentTimeMillis();
               delayExecuted = false;
               trigger = true;
            }
         }
      }

      @SubscribeEvent
      public void onTick(TickEvent.ClientTickEvent event) {
//         if (getDisplayedTitle(mc.ingameGUI).contains("VICTORY!")){
//            startTime = System.currentTimeMillis();
//            delayExecuted = false;
//            trigger = true;
//         }

         if (!delayExecuted && System.currentTimeMillis() - startTime >= delayTime && mc.thePlayer != null) {
            delayExecuted = true;

            EntityPlayer player = mc.thePlayer;
            World world = player.worldObj;

             if (trigger) {
               if (supersheep) {
                  FlyingSheep flyingSheep = new FlyingSheep(world);
                  flyingSheep.setPosition(player.posX, player.posY, player.posZ); // プレイヤーの近くにスポーン
                  world.spawnEntityInWorld(flyingSheep);
                  player.mountEntity(flyingSheep);
               } else if (dragonrider) {
                  DragonRider dragon = new DragonRider(world);
                  dragon.setPosition(player.posX, player.posY, player.posZ); // プレイヤーの近くにスポーン
                  world.spawnEntityInWorld(dragon);
                  player.mountEntity(dragon);
                  dragon.setCustomNameTag(EnumChatFormatting.GREEN + mc.thePlayer.getName() + "'s Dragon");
                  mc.theWorld.playSound(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, "mob.enderdragon.growl", 2.0F, 2.0F, false);
               }
            }
         }
      }

      public String getDisplayedTitle(GuiIngame guiIngame) {
         try {
            Field titleField = GuiIngame.class.getDeclaredField("displayedTitle");
            titleField.setAccessible(true); // アクセス可能にする
            return (String) titleField.get(guiIngame);
         } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
         }
      }

      @SubscribeEvent
      public void onWorldChange(TickEvent.ClientTickEvent event) {
         if (mc.theWorld != null && mc.theWorld != lastWorld) {
            lastWorld = mc.theWorld;  // 現在のワールドを保存
            trigger = false;
         }
      }
   }

   public static class PositionPacketCancel {

      public final Minecraft mc = Minecraft.getMinecraft();

      public PositionPacketCancel() {
         // イベントバスに登録
         MinecraftForge.EVENT_BUS.register(this);
      }

      @SubscribeEvent
      public void onClientTick(TickEvent.ClientTickEvent event) {
         if (mc.thePlayer != null && (mc.thePlayer.ridingEntity instanceof FlyingSheep || mc.thePlayer.ridingEntity instanceof DragonRider)) {
            mc.thePlayer.motionX = 0;
            mc.thePlayer.motionY = 0;
            mc.thePlayer.motionZ = 0;
         }
      }

      @SubscribeEvent
      public void onPacketSend(FMLNetworkEvent.ClientCustomPacketEvent event) {
         // FMLProxyPacket を取得
         FMLProxyPacket packet = event.packet;

         // FMLProxyPacket からバイトバッファを取得
         ByteBuf buf = packet.payload();  // FMLProxyPacket からペイロードを取得

         try {
            // バイトバッファからパケットデータを読み取る
            int packetId = buf.readByte();  // 最初のバイトはパケットID（C03PacketPlayerの場合は0x03）

            if (packetId == 0x03) {  // C03PacketPlayerのIDをチェック
               // C03PacketPlayerを読み取る
               C03PacketPlayer playerPacket = new C03PacketPlayer();
               playerPacket.readPacketData(new PacketBuffer(buf));

               if (mc.thePlayer != null && (mc.thePlayer.ridingEntity instanceof FlyingSheep || mc.thePlayer.ridingEntity instanceof DragonRider)) {
                  event.setCanceled(true);  // パケットの送信をキャンセル
               }
            }
         } catch (Exception e) {
            e.printStackTrace();
         } finally {
            buf.release();  // バッファを解放
         }
      }
   }

   public static class CustomLargeFireball extends EntityLargeFireball {

      private float oscillationAmplitude = 1f; // 左右の揺れ幅（大きくすると振れが大きくなる）
      private float oscillationSpeed = 0.5f;    // 振れの速さ（小さくするとゆっくり揺れる）

      private int ticksAlive = 0; // 生存時間を追跡

      public CustomLargeFireball(World worldIn, double x, double y, double z, double motionX, double motionY, double motionZ, float size) {
         super(worldIn, x, y, z, motionX, motionY, motionZ);
         this.setSize(size, size);
      }

      @Override
      public void onUpdate() {
         super.onUpdate();
      }

   // ファイアボールがブロックやエンティティに触れたときの処理
      @Override
      protected void onImpact(MovingObjectPosition result) {
         if(!result.typeOfHit.equals(result.entityHit) && !(result.entityHit == this)) {
            this.worldObj.createExplosion(this, this.posX - 2, this.posY, this.posZ - 2, this.explosionPower, true);

            // ファイアボールを削除
            this.setDead();

            this.worldObj.playSound(
                    this.posX,   // ひつじのX座標
                    this.posY,   // ひつじのY座標
                    this.posZ,   // ひつじのZ座標
                    "random.explode",  // サウンド名
                    2F,        // 音量
                    0.6F,        // ピッチ
                    false         // 遠距離音かどうか（通常はfalse）
            );
         }
      }
   }

   public class RenderCustomLargeFireBall extends Render<EntityLargeFireball> {
      private final ResourceLocation FIREBALL_TEXTURE = new ResourceLocation("textures/entity/fireball.png");

      public RenderCustomLargeFireBall(RenderManager renderManager) {
         super(renderManager);
      }

      @Override
      public void doRender(EntityLargeFireball entity, double x, double y, double z, float entityYaw, float partialTicks){
         double offsetX = 1.0;
         super.doRender(entity, x + offsetX, y, z, entityYaw, partialTicks);
      }

      @Override
      protected ResourceLocation getEntityTexture(EntityLargeFireball entity){ return FIREBALL_TEXTURE; }
   }
}
