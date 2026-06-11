package com.diamondwalker.cloudlayers;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexBuffer;
import net.minecraft.client.CloudStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.*;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

public class CloudSky extends DimensionSpecialEffects.OverworldEffects {
    private int layer = 0;
    private CloudLayerCache[] layers = new CloudLayerCache[0];

    @Override
    public float getCloudHeight() {
        float height = super.getCloudHeight();

        // for each extra layer, add to the height
        for (int i = 1; i < layer; i++) {
            height += Config.getSpacingAboveLayer(i - 1);
        }

        return height;
    }

    public double modifyCloudLayerPos(double pos) {
        pos *= (getCloudHeight() / super.getCloudHeight());
        pos += (layer - 1) * 10_000;
        return pos;
    }

    public boolean forceFlatClouds() {
        return Config.isFastCloudLayer(layer - 1); // we subtract one because we need the index, not the layer number
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

                /*double total = ((double)partialTick + ticks) * (getCloudHeight() / super.getCloudHeight()); // scale speed by the height
                total += (layer - 1) * 10_000; // add an offset so they don't all start lined up

                renderer.ticks = (int)Math.floor(total);
                float partial = (float)(total - renderer.ticks);*/

                float alpha = Config.getAlphaForLayer(index);
                if (alpha < 1.0f) {
                    float[] shaderColor = RenderSystem.getShaderColor();
                    RenderSystem.setShaderColor(shaderColor[0], shaderColor[1], shaderColor[2], alpha);

                    renderer.renderClouds(poseStack, projectionMatrix, partialTick, camX, camY, camZ);

                    // Restore full opacity before next layer / after last layer
                    RenderSystem.setShaderColor(shaderColor[0], shaderColor[1], shaderColor[2], shaderColor[3]);
                } else {
                    renderer.renderClouds(poseStack, projectionMatrix, partialTick, camX, camY, camZ);
                }

                layers[index].copy(renderer);
            }
            layer = 0;
            //renderer.ticks = ticks;

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
        private CloudStatus prevCloudsType;
        private Vec3 prevCloudColor;
        private VertexBuffer buffer;

        void copy(LevelRenderer renderer) {
            prevCloudX = renderer.prevCloudX;
            prevCloudY = renderer.prevCloudY;
            prevCloudZ = renderer.prevCloudZ;
            prevCloudsType = renderer.prevCloudsType;
            prevCloudColor = renderer.prevCloudColor;
            buffer = renderer.cloudBuffer;
        }

        void load(LevelRenderer renderer) {
            renderer.prevCloudX = prevCloudX;
            renderer.prevCloudY = prevCloudY;
            renderer.prevCloudZ = prevCloudZ;
            renderer.prevCloudsType = prevCloudsType;
            renderer.prevCloudColor = prevCloudColor;
            renderer.cloudBuffer = buffer;
        }
    }
}
