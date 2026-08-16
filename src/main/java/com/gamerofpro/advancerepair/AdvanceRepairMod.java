package com.gamerofpro.advancerepair;

import net.minecraft.client.gui.screens.Screen;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.NeoForge;

@Mod(AdvanceRepairMod.MODID)
public class AdvanceRepairMod {

    public static final String MODID = "advancerepair";

    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.ConfigValue<Boolean> MOD_ENABLED;
    public static final ModConfigSpec.ConfigValue<String> TARGET_MOD_ID;

    public AdvanceRepairMod(IEventBus modEventBus, ModContainer modContainer) {

        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.push("Advance Repair Settings");

        MOD_ENABLED = builder
                .define("modEnabled", true);

        TARGET_MOD_ID = builder
                .define("targetModId", "minecraft");

        builder.pop();

        SPEC = builder.build();

        modContainer.registerConfig(ModConfig.Type.COMMON, SPEC);

        modContainer.registerExtensionPoint(
                IConfigScreenFactory.class,
                (container, parentScreen) ->
                        new ModConfigScreen(parentScreen)
        );

        NeoForge.EVENT_BUS.register(new AnvilHandler());
        NeoForge.EVENT_BUS.register(new TooltipHandler());
    }
}
