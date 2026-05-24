package com.diamondwalker.cloudlayers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.*;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;

public class CloudSky extends DimensionSpecialEffects.OverworldEffects {
    private int layer = 0;

    @Override
    public float getCloudHeight() {
        return super.getCloudHeight() + Config.LAYER_SPACING.get().floatValue() * (layer - 1);
    }

    @Override
    public boolean renderClouds(ClientLevel level, int ticks, float partialTick, PoseStack poseStack, double camX, double camY, double camZ, Matrix4f projectionMatrix) {
        LevelRenderer renderer = Minecraft.getInstance().levelRenderer;

        if (layer == 0) {
            while (layer < Config.LAYER_COUNT.get()) {
                layer++;

                double total = ((double)partialTick + ticks) * (getCloudHeight() / super.getCloudHeight()); // scale speed by the height
                total += (layer - 1) * 10_000; // add an offset so they don't all start lined up

                renderer.ticks = (int)Math.floor(total);
                float partial = (float)(total - renderer.ticks);
                renderer.renderClouds(poseStack, projectionMatrix, partial, camX, camY, camZ);
            }
            layer = 0;
            renderer.ticks = ticks;

            return true;
        }

        return false;
    }
}
