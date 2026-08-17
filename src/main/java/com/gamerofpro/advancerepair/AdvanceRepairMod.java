package com.gamerofpro.advancerepair;

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

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.push("Advance Repair Settings");

        MOD_ENABLED = builder.define("modEnabled", true);
        TARGET_MOD_ID = builder.define("targetModId", "minecraft");

        builder.pop();

        SPEC = builder.build();
    }

    public AdvanceRepairMod(IEventBus modEventBus, ModContainer modContainer) {

        // Register configuration
        modContainer.registerConfig(
                ModConfig.Type.COMMON,
                SPEC
        );

        // Register config screen
        modContainer.registerExtensionPoint(
                IConfigScreenFactory.class,
                (container, parentScreen) ->
                        new ModConfigScreen(parentScreen)
        );

        /*
         * IMPORTANT:
         *
         * These handlers use static @SubscribeEvent methods,
         * so they MUST be registered using .class.
         *
         * This fixes the exact crash we saw:
         *
         * "Expected @SubscribeEvent method ... to NOT be static
         * because register() was called with an instance type."
         */

        NeoForge.EVENT_BUS.register(AnvilHandler.class);

        NeoForge.EVENT_BUS.register(TooltipHandler.class);

        NeoForge.EVENT_BUS.register(WelcomeBookHandler.class);
    }
}
