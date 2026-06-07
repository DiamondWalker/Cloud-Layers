package com.diamondwalker.cloudlayers;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = CloudLayers.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config
{
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec.IntValue LAYER_COUNT = BUILDER
            .comment("The number of cloud layers to render")
            .defineInRange("cloudLayers", 2, 1, Integer.MAX_VALUE);

    public static final ForgeConfigSpec.DoubleValue LAYER_SPACING = BUILDER
            .comment("The amount of space between cloud layers")
            .defineInRange("cloudSpacing", 64, 0, Float.MAX_VALUE);

    public static final ForgeConfigSpec.ConfigValue<List<? extends Double>> LAYER_ALPHAS = BUILDER
            .comment(
                    "Per-layer alpha (transparency) values. Each entry controls one cloud layer from bottom to top.",
                    "Values range from 0.0 (fully transparent) to 1.0 (fully opaque).",
                    "If fewer values are provided than cloudLayers, the last value is repeated for remaining layers.",
                    "Example for 5 layers, fading out toward the top: [1.0, 0.85, 0.65, 0.45, 0.25]"
            )
            .defineList("layerAlphas", List.of(1.0, 0.5),
                    obj -> obj instanceof Double d && d >= 0.0 && d <= 1.0);

    static final ForgeConfigSpec SPEC = BUILDER.build();

    public static float getAlphaForLayer(int layerIndex) {
        List<? extends Double> alphas = LAYER_ALPHAS.get();
        if (alphas == null || alphas.isEmpty()) return 1.0f;
        int clampedIndex = Math.min(layerIndex, alphas.size() - 1);
        Double val = alphas.get(clampedIndex);
        return val == null ? 1.0f : val.floatValue();
    }
}