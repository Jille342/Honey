package com.morekilleffects.killEffect;

public class Location {
   public double x;
   public double y;
   public double z;

   public Location() {
      this.x = 0.0;
      this.y = 0.0;
      this.z = 0.0;
   }

   public Location(double x, double y, double z) {
      this.x = x;
      this.y = y;
      this.z = z;
   }

   public Location add(double x, double y, double z) {
      return new Location(this.x + x, this.y + y, this.z + z);
   }
}
