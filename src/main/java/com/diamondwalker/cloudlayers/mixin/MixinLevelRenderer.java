package com.diamondwalker.cloudlayers.mixin;

import com.diamondwalker.cloudlayers.CloudSky;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.CloudStatus;
import net.minecraft.client.Options;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LevelRenderer.class)
public class MixinLevelRenderer {
    @Shadow
    private ClientLevel level;

    @WrapOperation(
            method = "renderClouds",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Options;getCloudsType()Lnet/minecraft/client/CloudStatus;")
    )
    private CloudStatus changeCloudType(Options instance, Operation<CloudStatus> original) {
        CloudStatus status = original.call(instance);

        if (status == CloudStatus.FANCY && level.effects() instanceof CloudSky sky) {
            if (sky.forceFlatClouds()) {
                status = CloudStatus.FAST;
            }
        }

        return status;
    }

    @ModifyVariable(
            method = "renderClouds",
            at = @At("STORE"),
            name = "d1"
    )
    private double ah(double value) {
        if (level.effects() instanceof CloudSky sky) {
            return sky.modifyCloudLayerPos(value);
        }

        return value;
    }
}
