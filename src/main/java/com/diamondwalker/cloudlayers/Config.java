package com.diamondwalker.cloudlayers;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Forge's config APIs
@Mod.EventBusSubscriber(modid = CloudLayers.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config
{
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec.IntValue LAYER_COUNT = BUILDER
            .comment("The number of cloud layers to render")
            .defineInRange("cloudLayers", 5, 1, Integer.MAX_VALUE);

    public static final ForgeConfigSpec.DoubleValue LAYER_SPACING = BUILDER
            .comment("The amount of space between cloud layers")
            .defineInRange("cloudSpacing", 64, 0, Float.MAX_VALUE);

    static final ForgeConfigSpec SPEC = BUILDER.build();
}
