package com.gamerofpro.advancerepair;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.Tiers;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

import java.util.List;

public class TooltipHandler {

    @SubscribeEvent
    public static void onTooltip(ItemTooltipEvent event) {
        if (!AdvanceRepairMod.MOD_ENABLED.get()
                || !AdvanceRepairMod.TOOLTIP.get()) {
            return;
        }

        ItemStack stack = event.getItemStack();
        List<Component> tooltip = event.getToolTip();

        ResourceLocation itemKey =
                BuiltInRegistries.ITEM.getKey(stack.getItem());

        String itemNamespace = itemKey.getNamespace();
        String itemPath = itemKey.getPath().toLowerCase();
        String targetModId = AdvanceRepairMod.TARGET_MOD_ID.get();

        if (stack.isDamageableItem() && stack.isDamaged()) {
            int maxDamage = stack.getMaxDamage();
            int damage = stack.getDamageValue();
            int remaining = maxDamage - damage;

            tooltip.add(Component.literal(
                    "Durability: " + remaining + " / " + maxDamage)
                    .withStyle(ChatFormatting.GRAY));
        }

        if (targetModId.equalsIgnoreCase("minecraft")
                && itemNamespace.equalsIgnoreCase("minecraft")) {

            if (isNetherite(stack)) {
                addRepair(tooltip, "Repair with Diamond: 80%", ChatFormatting.GREEN);
                addRepair(tooltip, "Repair with Iron: 50%", ChatFormatting.YELLOW);
                addRepair(tooltip, "Repair with Gold: 40%", ChatFormatting.GOLD);
                addRepair(tooltip, "Repair with Copper: 40%", ChatFormatting.GRAY);
                addRepair(tooltip, "XP Cost: 5–12 levels", ChatFormatting.DARK_PURPLE);
            } else if (isDiamond(stack)) {
                addRepair(tooltip, "Repair with Iron: 60%", ChatFormatting.YELLOW);
                addRepair(tooltip, "Repair with Gold: 45%", ChatFormatting.GOLD);
                addRepair(tooltip, "Repair with Copper: 40%", ChatFormatting.GRAY);
                addRepair(tooltip, "XP Cost: 5–12 levels", ChatFormatting.DARK_PURPLE);
            } else if (isIron(stack)) {
                addRepair(tooltip, "Repair with Gold: 70%", ChatFormatting.GOLD);
                addRepair(tooltip, "Repair with Copper: 40%", ChatFormatting.GRAY);
                addRepair(tooltip, "XP Cost: 5–12 levels", ChatFormatting.DARK_PURPLE);
            } else if (isLeather(stack)) {
                addRepair(tooltip, "Repair with Leather: 100%", ChatFormatting.GREEN);
                addRepair(tooltip, "XP Cost: 5–12 levels", ChatFormatting.DARK_PURPLE);
            } else if (isChainmail(stack)) {
                addRepair(tooltip, "Repair with Chain: 60%", ChatFormatting.GRAY);
                addRepair(tooltip, "Repair with Iron: 50%", ChatFormatting.YELLOW);
                addRepair(tooltip, "XP Cost: 5–12 levels", ChatFormatting.DARK_PURPLE);
            }

            return;
        }

        if (!itemNamespace.equalsIgnoreCase(targetModId)) {
            return;
        }

        String repairPercentageString = null;
        ChatFormatting color = ChatFormatting.GRAY;
        Object item = stack.getItem();

        if (item instanceof TieredItem tieredItem) {
            Tier tier = tieredItem.getTier();

            if (tier == Tiers.NETHERITE) {
                repairPercentageString = "100%";
                color = ChatFormatting.GREEN;
            } else if (tier == Tiers.DIAMOND) {
                repairPercentageString = "80%";
                color = ChatFormatting.AQUA;
            } else if (tier == Tiers.IRON) {
                repairPercentageString = "60%";
                color = ChatFormatting.WHITE;
            } else if (tier == Tiers.GOLD) {
                repairPercentageString = "40%";
                color = ChatFormatting.GOLD;
            }
        } else if (item instanceof ArmorItem armorItem) {
            Holder<?> material = armorItem.getMaterial();

            if (material == ArmorMaterials.NETHERITE) {
                repairPercentageString = "100%";
                color = ChatFormatting.GREEN;
            } else if (material == ArmorMaterials.DIAMOND) {
                repairPercentageString = "80%";
                color = ChatFormatting.AQUA;
            } else if (material == ArmorMaterials.IRON) {
                repairPercentageString = "60%";
                color = ChatFormatting.WHITE;
            } else if (material == ArmorMaterials.GOLD) {
                repairPercentageString = "40%";
                color = ChatFormatting.GOLD;
            }
        }

        if (repairPercentageString == null) {
            if (itemPath.contains("netherite")) {
                repairPercentageString = "100%";
                color = ChatFormatting.GREEN;
            } else if (itemPath.contains("diamond")) {
                repairPercentageString = "80%";
                color = ChatFormatting.AQUA;
            } else if (itemPath.contains("iron")) {
                repairPercentageString = "60%";
                color = ChatFormatting.WHITE;
            } else if (itemPath.contains("gold") || itemPath.contains("copper")) {
                repairPercentageString = "40%";
                color = ChatFormatting.GOLD;
            }
        }

        if (repairPercentageString != null) {
            addRepair(tooltip,
                    "Custom Mod Repair Value: " + repairPercentageString,
                    color);
            addRepair(tooltip, "XP Cost: 5–12 levels", ChatFormatting.DARK_PURPLE);
        }
    }

    private static void addRepair(
            List<Component> tooltip,
            String text,
            ChatFormatting color
    ) {
        tooltip.add(Component.literal(text).withStyle(color));
    }

    private static boolean isNetherite(ItemStack stack) {
        return stack.is(Items.NETHERITE_HELMET) || stack.is(Items.NETHERITE_CHESTPLATE)
                || stack.is(Items.NETHERITE_LEGGINGS) || stack.is(Items.NETHERITE_BOOTS)
                || stack.is(Items.NETHERITE_SWORD) || stack.is(Items.NETHERITE_PICKAXE)
                || stack.is(Items.NETHERITE_AXE) || stack.is(Items.NETHERITE_SHOVEL)
                || stack.is(Items.NETHERITE_HOE);
    }

    private static boolean isDiamond(ItemStack stack) {
        return stack.is(Items.DIAMOND_HELMET) || stack.is(Items.DIAMOND_CHESTPLATE)
                || stack.is(Items.DIAMOND_LEGGINGS) || stack.is(Items.DIAMOND_BOOTS)
                || stack.is(Items.DIAMOND_SWORD) || stack.is(Items.DIAMOND_PICKAXE)
                || stack.is(Items.DIAMOND_AXE) || stack.is(Items.DIAMOND_SHOVEL)
                || stack.is(Items.DIAMOND_HOE);
    }

    private static boolean isIron(ItemStack stack) {
        return stack.is(Items.IRON_HELMET) || stack.is(Items.IRON_CHESTPLATE)
                || stack.is(Items.IRON_LEGGINGS) || stack.is(Items.IRON_BOOTS)
                || stack.is(Items.IRON_SWORD) || stack.is(Items.IRON_PICKAXE)
                || stack.is(Items.IRON_AXE) || stack.is(Items.IRON_SHOVEL)
                || stack.is(Items.IRON_HOE);
    }

    private static boolean isLeather(ItemStack stack) {
        return stack.is(Items.LEATHER_HELMET) || stack.is(Items.LEATHER_CHESTPLATE)
                || stack.is(Items.LEATHER_LEGGINGS) || stack.is(Items.LEATHER_BOOTS);
    }

    private static boolean isChainmail(ItemStack stack) {
        return stack.is(Items.CHAINMAIL_HELMET) || stack.is(Items.CHAINMAIL_CHESTPLATE)
                || stack.is(Items.CHAINMAIL_LEGGINGS) || stack.is(Items.CHAINMAIL_BOOTS);
    }
}
