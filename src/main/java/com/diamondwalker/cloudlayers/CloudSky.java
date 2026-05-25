package com.diamondwalker.cloudlayers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.*;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;

public class CloudSky extends DimensionSpecialEffects.OverworldEffects {
    private int layer = 0;
    private CloudLayerCache[] layers = new CloudLayerCache[0];

    @Override
    public float getCloudHeight() {
        return super.getCloudHeight() + Config.LAYER_SPACING.get().floatValue() * (layer - 1);
    }

    @Override
    public boolean renderClouds(ClientLevel level, int ticks, float partialTick, PoseStack poseStack, double camX, double camY, double camZ, Matrix4f projectionMatrix) {
        LevelRenderer renderer = Minecraft.getInstance().levelRenderer;

        if (layer == 0) {
            if (Config.LAYER_COUNT.get() != layers.length) {
                layers = new CloudLayerCache[Config.LAYER_COUNT.get()];
            }

            while (layer < Config.LAYER_COUNT.get()) {
                int index = layer++; // index starts at 0, layer starts at 1

                if (layers[index] == null) {
                    layers[index] = new CloudLayerCache();
                    layers[index].copy(renderer);
                }

                layers[index].load(renderer);

                double total = ((double)partialTick + ticks) * (getCloudHeight() / super.getCloudHeight()); // scale speed by the height
                total += (layer - 1) * 10_000; // add an offset so they don't all start lined up

                renderer.ticks = (int)Math.floor(total);
                float partial = (float)(total - renderer.ticks);
                renderer.renderClouds(poseStack, projectionMatrix, partial, camX, camY, camZ); // TODO: I think this may affect cloud color due to the different partialTicks

                layers[index].copy(renderer);
            }
            layer = 0;
            renderer.ticks = ticks;

            layers[0].load(renderer);

            return true;
        }

        return false;
    }

    private static class CloudLayerCache {
        private int prevCloudX;
        private int prevCloudY;
        private int prevCloudZ;
        private VertexBuffer buffer;

        void copy(LevelRenderer renderer) {
            prevCloudX = renderer.prevCloudX;
            prevCloudY = renderer.prevCloudY;
            prevCloudZ = renderer.prevCloudZ;
            buffer = renderer.cloudBuffer;
        }

        void load(LevelRenderer renderer) {
            renderer.prevCloudX = prevCloudX;
            renderer.prevCloudY = prevCloudY;
            renderer.prevCloudZ = prevCloudZ;
            renderer.cloudBuffer = buffer;
        }
    }
}
