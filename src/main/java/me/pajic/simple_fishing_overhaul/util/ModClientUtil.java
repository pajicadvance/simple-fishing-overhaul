package me.pajic.simple_fishing_overhaul.util;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

public class ModClientUtil {

    public static void rotate(PoseStack poseStack, Axis axis, float angle) {
        //? <26.3 {
        /*poseStack.mulPose(axis.rotationDegrees(angle));
        *///?} else {
        poseStack.rotateDegrees(axis, angle);
        //?}
    }
}
