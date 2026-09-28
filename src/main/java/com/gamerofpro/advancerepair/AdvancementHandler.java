package com.gamerofpro.advancerepair;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.AnvilRepairEvent;

public class AdvancementHandler {

    private static final ResourceLocation FIRST_REPAIR =
            ResourceLocation.fromNamespaceAndPath(
                    AdvanceRepairMod.MODID,
                    "first_repair"
            );

    @SubscribeEvent
    public static void onAnvilRepair(AnvilRepairEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        if (!isAdvanceRepair(event.getLeft(), event.getRight(), event.getOutput())) {
            return;
        }

        player.getAdvancements().award(
                player.server.getAdvancements().get(FIRST_REPAIR),
                "repair"
        );
    }

    private static boolean isAdvanceRepair(
            ItemStack left,
            ItemStack right,
            ItemStack output
    ) {
        if (left.isEmpty() || right.isEmpty() || output.isEmpty()) {
            return false;
        }

        if (!left.isDamageableItem()) {
            return false;
        }

        if (output.getDamageValue() >= left.getDamageValue()) {
            return false;
        }

        if (right.is(Items.NETHERITE_INGOT)
                && isNetherite(left)) {
            return false;
        }

        if (right.is(Items.DIAMOND)
                && isDiamond(left)) {
            return false;
        }

        if (right.is(Items.IRON_INGOT)
                && isIron(left)) {
            return false;
        }

        if (right.is(Items.GOLD_INGOT)
                && isGold(left)) {
            return false;
        }

        if (right.is(Items.COPPER_INGOT)
                && isCopperRepairTarget(left)) {
            return false;
        }

        if (right.is(Items.LEATHER)
                && isLeather(left)) {
            return false;
        }

        if (right.is(Items.CHAIN)
                && isChainmail(left)) {
            return false;
        }

        return true;
    }

    private static boolean isNetherite(ItemStack stack) {
        return stack.is(Items.NETHERITE_HELMET)
                || stack.is(Items.NETHERITE_CHESTPLATE)
                || stack.is(Items.NETHERITE_LEGGINGS)
                || stack.is(Items.NETHERITE_BOOTS)
                || stack.is(Items.NETHERITE_SWORD)
                || stack.is(Items.NETHERITE_PICKAXE)
                || stack.is(Items.NETHERITE_AXE)
                || stack.is(Items.NETHERITE_SHOVEL)
                || stack.is(Items.NETHERITE_HOE);
    }

    private static boolean isDiamond(ItemStack stack) {
        return stack.is(Items.DIAMOND_HELMET)
                || stack.is(Items.DIAMOND_CHESTPLATE)
                || stack.is(Items.DIAMOND_LEGGINGS)
                || stack.is(Items.DIAMOND_BOOTS)
                || stack.is(Items.DIAMOND_SWORD)
                || stack.is(Items.DIAMOND_PICKAXE)
                || stack.is(Items.DIAMOND_AXE)
                || stack.is(Items.DIAMOND_SHOVEL)
                || stack.is(Items.DIAMOND_HOE);
    }

    private static boolean isIron(ItemStack stack) {
        return stack.is(Items.IRON_HELMET)
                || stack.is(Items.IRON_CHESTPLATE)
                || stack.is(Items.IRON_LEGGINGS)
                || stack.is(Items.IRON_BOOTS)
                || stack.is(Items.IRON_SWORD)
                || stack.is(Items.IRON_PICKAXE)
                || stack.is(Items.IRON_AXE)
                || stack.is(Items.IRON_SHOVEL)
                || stack.is(Items.IRON_HOE);
    }

    private static boolean isGold(ItemStack stack) {
        return stack.is(Items.GOLDEN_HELMET)
                || stack.is(Items.GOLDEN_CHESTPLATE)
                || stack.is(Items.GOLDEN_LEGGINGS)
                || stack.is(Items.GOLDEN_BOOTS)
                || stack.is(Items.GOLDEN_SWORD)
                || stack.is(Items.GOLDEN_PICKAXE)
                || stack.is(Items.GOLDEN_AXE)
                || stack.is(Items.GOLDEN_SHOVEL)
                || stack.is(Items.GOLDEN_HOE);
    }

    private static boolean isLeather(ItemStack stack) {
        return stack.is(Items.LEATHER_HELMET)
                || stack.is(Items.LEATHER_CHESTPLATE)
                || stack.is(Items.LEATHER_LEGGINGS)
                || stack.is(Items.LEATHER_BOOTS);
    }

    private static boolean isChainmail(ItemStack stack) {
        return stack.is(Items.CHAINMAIL_HELMET)
                || stack.is(Items.CHAINMAIL_CHESTPLATE)
                || stack.is(Items.CHAINMAIL_LEGGINGS)
                || stack.is(Items.CHAINMAIL_BOOTS);
    }

    private static boolean isCopperRepairTarget(ItemStack stack) {
        return stack.isDamageableItem();
    }
}