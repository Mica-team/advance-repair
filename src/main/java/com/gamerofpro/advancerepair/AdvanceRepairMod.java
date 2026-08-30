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
    public static final ModConfigSpec.ConfigValue<Boolean> QUALITY_OF_LIFE;
    public static final ModConfigSpec.ConfigValue<Boolean> TOOLTIP;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.push("Advance Repair Settings");

        MOD_ENABLED = builder.define("modEnabled", true);
        TARGET_MOD_ID = builder.define("targetModId", "minecraft");

        builder.push("Quality of Life");
        QUALITY_OF_LIFE = builder.define("enabled", true);
        builder.pop();

        builder.push("Tooltip");
        TOOLTIP = builder.define("enabled", true);
        builder.pop();

        builder.pop();

        SPEC = builder.build();
    }

    public AdvanceRepairMod(
            IEventBus modEventBus,
            ModContainer modContainer
    ) {
        modContainer.registerConfig(
                ModConfig.Type.COMMON,
                SPEC
        );

        modContainer.registerExtensionPoint(
                IConfigScreenFactory.class,
                (container, parentScreen) ->
                        new ModConfigScreen(parentScreen)
        );

        NeoForge.EVENT_BUS.register(AnvilHandler.class);
        NeoForge.EVENT_BUS.register(AnvilRepairHandler.class);
        NeoForge.EVENT_BUS.register(TooltipHandler.class);
        NeoForge.EVENT_BUS.register(MendingIIHandler.class);
    }
}
