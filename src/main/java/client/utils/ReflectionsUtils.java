package client.utils;

import net.minecraftforge.fml.relauncher.ReflectionHelper;

import java.lang.reflect.Field;

public class ReflectionsUtils {
    public static void setFinalField(Object object, String string, Object object2, boolean setValue) {
        try {
            Field var4 = ReflectionHelper.findField(object.getClass(), new String[]{string});
            var4.setAccessible(true);
            Field var5 = Field.class.getDeclaredField("modifiers");
            var5.setAccessible(true);
            var5.setInt(var4, var4.getModifiers() & -17);
            var4.set(object2, setValue);
        } catch (Exception var6) {
            var6.printStackTrace();
        }

    }
}
