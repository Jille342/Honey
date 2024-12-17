
package client.event.listeners;


import client.event.Event;

public class EventMoveInput extends Event<EventMoveInput> {
    private float forward, strafe;
    private boolean jump, sneak;
    private double sneakSlowDownMultiplier;
    public EventMoveInput(float forward, float strafe, boolean jump, boolean sneak, double sneakSlowDownMultiplier) {
        this.forward = forward;
        this.strafe = strafe;
        this.jump = jump;
        this.sneak = sneak;
        this.sneakSlowDownMultiplier = sneakSlowDownMultiplier;
    }

    public float getForward() {
        return forward;
    }
    public void setForward(float forward){
        this.forward = forward;
    }
    public float getStrafe(){
        return strafe;
    }
    public void setStrafe(float strafe){
        this.strafe = strafe;
    }
    public void setJump(boolean jump){
        this.jump = jump;
    }
    public boolean getJump(){
        return jump;
    }
    public boolean getSneak(){
        return sneak;
    }
    public void setSneak(boolean sneak){
        this.sneak = sneak;
    }
    public double getSneakSlowDownMultiplier(){
        return sneakSlowDownMultiplier;
    }
    public void setSneakSlowDownMultiplier(double sneakSlowDownMultiplier){
        this.sneakSlowDownMultiplier = sneakSlowDownMultiplier;
    }
}
