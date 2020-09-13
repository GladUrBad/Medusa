package com.gladurbad.medusa.util;

import net.minecraft.server.v1_8_R3.MathHelper;
import org.bukkit.GameMode;
import org.bukkit.entity.*;

import java.util.List;

public class RaycastUtils {

    //lazy implementation

    public static Entity getLookingEntity(Player player) {
        Entity pointedEntity = null;
        float rotationPitch = player.getLocation().getPitch();
        float rotationYaw = player.getLocation().getYaw();
        //MovingObjectPosition objectMouseOver = null;

        if (player.getWorld() != null) {
            double var3 = 5;
            //objectMouseOver = rayTrace(var3, prevRotationPitch, prevRotationYaw, rotationPitch, rotationYaw);
            double var5 = var3;
            Vec3 var7 = func_174824_e(player);

            if (player.getGameMode().equals(GameMode.CREATIVE)) {
                var3 = 3 * 2;
                var5 = 3 * 2;
            } else {
                var5 = 3;

                var3 = var5;
            }

            /*if (objectMouseOver != null) {
                var5 = objectMouseOver.hitVec.distanceTo(var7);
            }*/

            Vec3 var8 = getLook(/*prevRotationPitch, prevRotationYaw, */rotationPitch, rotationYaw);
            Vec3 var9 = var7.addVector(var8.xCoord * var3, var8.yCoord * var3, var8.zCoord * var3);
            List<LivingEntity> var12 = player.getWorld().getLivingEntities()/*(var2, var2.getEntityBoundingBox().addCoord(var8.xCoord * var3, var8.yCoord * var3, var8.zCoord * var3).expand(var11, var11, var11))*/;
            double var13 = var5;

            for (LivingEntity o : var12) {
                if (!o.equals(player)) {
                    //if (((Entity) o).canBeCollidedWith()) {
                    /*float var17 = ((Entity) o).getCollisionBorderSize() 0F;*/
                    AABB var18 = AABB.getEntityBoundingBox(o)/*.expand(var17, var17, var17)*/;
                    Vec3 var19 = var18.calculateIntercept(var7, var9);

                    if (var18.isVecInside(var7)) {
                        if (0.0D < var13 || var13 == 0.0D) {
                            pointedEntity = o;
                            var13 = 0.0D;
                        }
                    } else if (var19 != null) {
                        double var20 = var7.distanceTo(var19);

                        if (var20 < var13 || var13 == 0.0D) {
                            if (o != player.getVehicle()) {
                                pointedEntity = o;
                                var13 = var20;
                            }
                        }
                    }
                }
                //}
            }
        }
        return pointedEntity;
    }

    public static Vec3 getLook(float pitch, float yaw) {
        //if (partialTicksThingy == 1.0F) {
        return func_174806_f(pitch, yaw);
        /*} else {
            float var2 = prevPitch + (pitch - prevPitch) * partialTicksThingy;
            float var3 = prevYaw + (yaw - prevYaw) * partialTicksThingy;
            return func_174806_f(var2, var3);
        }*/
    }

    public static Vec3 func_174806_f(float p_174806_1_, float p_174806_2_) {
        float var3 = MathHelper.cos(-p_174806_2_ * 0.017453292F - (float) Math.PI);
        float var4 = MathHelper.sin(-p_174806_2_ * 0.017453292F - (float) Math.PI);
        float var5 = -MathHelper.cos(-p_174806_1_ * 0.017453292F);
        float var6 = MathHelper.sin(-p_174806_1_ * 0.017453292F);
        return new Vec3(var4 * var5, var6, var3 * var5);
    }

    public static Vec3 func_174824_e(Player player) {
        //if (p_174824_1_ == 1.0F) {
        return new Vec3(player.getLocation().getX(), player.getLocation().getY() + (double) player.getEyeHeight(), player.getLocation().getZ());
        /*} else {
            double var2 = player.prevPosX + (player.posX - player.prevPosX) * (double) p_174824_1_;
            double var4 = player.prevPosY + (player.posY - player.prevPosY) * (double) p_174824_1_ + (double) player.getEyeHeight();
            double var6 = player.prevPosZ + (player.posZ - player.prevPosZ) * (double) p_174824_1_;
            return new Vec3(var2, var4, var6);
        }*/
    }
}
