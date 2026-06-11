package com.diamondwalker.cloudlayers;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Neo's config APIs
public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.IntValue LAYER_COUNT = BUILDER
            .comment("The number of cloud layers to render")
            .defineInRange("cloudLayers", 3, 1, Integer.MAX_VALUE);

    public static final ModConfigSpec.ConfigValue<List<? extends Double>> LAYER_SPACING = BUILDER
            .comment(
                    "Per-layer cloud spacing values. Each entry controls the size of a gap between cloud layers from bottom to top.",
                    "If too few values are provided, the last value is repeated for remaining layers.",
                    "Example for 3 layers where the 1st and 2nd layers have a spacing of 64 and the 2nd and 3rd layers have a spacing of 32: [64.0, 32.0]"
            )
            .defineList("layerSpacings", List.of(48.0, 48.0),
                    obj -> obj instanceof Double);

    public static final ModConfigSpec.ConfigValue<List<? extends Double>> LAYER_ALPHAS = BUILDER
            .comment(
                    "Per-layer alpha (opacity) values. Each entry controls one cloud layer from bottom to top.",
                    "Values range from 0.0 (fully transparent) to 1.0 (fully opaque).",
                    "If fewer values are provided than cloudLayers, the last value is repeated for remaining layers.",
                    "Example for 5 layers, fading out toward the top: [1.0, 0.85, 0.65, 0.45, 0.25]"
            )
            .defineList("layerOpacity", List.of(1.0, 0.5, 0.25),
                    obj -> obj instanceof Double d && d >= 0.0 && d <= 1.0);

    public static final ModConfigSpec.ConfigValue<List<? extends Boolean>> FLAT_CLOUDS = BUILDER
            .comment(
                    "Whether each cloud layer, from bottom to top, should render in the 2D \"Fast\" style, even if clouds are set to \"Fancy\" in the video settings.",
                    "True values mean this layer will always render in the Fast style. False values will make this layer dependent on the user video settings.",
                    "Example for 3 layers, where the first 2 layers are Fancy and the 3rd is Fast: [false, false, true]"
            )
            .defineList("forceFastClouds", List.of(false, false, true),
                    obj -> obj instanceof Boolean);

    static final ModConfigSpec SPEC = BUILDER.build();

    public static boolean isFastCloudLayer(int layerIndex) {
        List<? extends Boolean> fastLayers = FLAT_CLOUDS.get();
        if (fastLayers == null || fastLayers.isEmpty()) return false;
        int clampedIndex = Math.min(layerIndex, fastLayers.size() - 1);
        Boolean val = fastLayers.get(clampedIndex);
        return val != null && val;
    }

    public static float getSpacingAboveLayer(int layerIndex) {
        List<? extends Double> spacings = LAYER_SPACING.get();
        if (spacings == null || spacings.isEmpty()) return 64;
        int clampedIndex = Math.min(layerIndex, spacings.size() - 1);
        Double val = spacings.get(clampedIndex);
        return val == null ? 64 : val.floatValue();
    }

    public static float getAlphaForLayer(int layerIndex) {
        List<? extends Double> alphas = LAYER_ALPHAS.get();
        if (alphas == null || alphas.isEmpty()) return 1.0f;
        int clampedIndex = Math.min(layerIndex, alphas.size() - 1);
        Double val = alphas.get(clampedIndex);
        return val == null ? 1.0f : val.floatValue();
    }
}
