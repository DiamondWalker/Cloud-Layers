package com.diamondwalker.cloudlayers;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.*;
import org.joml.Matrix4f;
import org.joml.Vector4f;

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

            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(
                GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                GlStateManager.SourceFactor.ONE,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA
            );

            while (layer < Config.LAYER_COUNT.get()) {
                int index = layer++; // index starts at 0, layer starts at 1

                if (layers[index] == null) {
                    layers[index] = new CloudLayerCache();
                    layers[index].copy(renderer);
                }

                layers[index].load(renderer);

                double total = ((double)partialTick + ticks) * (getCloudHeight() / super.getCloudHeight());
                total += (layer - 1) * 10_000;

                renderer.ticks = (int)Math.floor(total);
                float partial = (float)(total - renderer.ticks);

                float alpha = Config.getAlphaForLayer(index);
                float[] shaderColor = RenderSystem.getShaderColor();
                RenderSystem.setShaderColor(shaderColor[0], shaderColor[1], shaderColor[2], alpha);

                renderer.renderClouds(poseStack, projectionMatrix, partial, camX, camY, camZ);

                // Restore full opacity before next layer / after last layer
                RenderSystem.setShaderColor(shaderColor[0], shaderColor[1], shaderColor[2], 1.0f);

                layers[index].copy(renderer);
            }
            layer = 0;
            renderer.ticks = ticks;

            // Restore blend state to vanilla default
            RenderSystem.defaultBlendFunc();

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
