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
            .defineInRange("cloudLayers", 5, 1, Integer.MAX_VALUE);

    public static final ModConfigSpec.DoubleValue LAYER_SPACING = BUILDER
            .comment("The amount of space between cloud layers")
            .defineInRange("cloudSpacing", 64, 0, Float.MAX_VALUE);

    static final ModConfigSpec SPEC = BUILDER.build();
}
