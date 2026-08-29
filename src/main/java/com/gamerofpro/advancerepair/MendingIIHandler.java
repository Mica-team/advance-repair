package com.gamerofpro.advancerepair;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.AnvilUpdateEvent;
import net.neoforged.neoforge.event.entity.player.PlayerXpEvent;

import java.util.ArrayList;
import java.util.List;

public class MendingIIHandler {

    private static final ResourceKey<net.minecraft.world.item.enchantment.Enchantment> MENDING_II =
            ResourceKey.create(
                    Registries.ENCHANTMENT,
                    ResourceLocation.fromNamespaceAndPath(
                            AdvanceRepairMod.MODID,
                            "mending_ii"
                    )
            );

    @SubscribeEvent
    public static void onAnvilUpdate(AnvilUpdateEvent event) {
        ItemStack left = event.getLeft();
        ItemStack right = event.getRight();

        if (left.isEmpty() || right.isEmpty()) {
            return;
        }

        // Mending II is armor-only. Explicitly block the enchanted book
        // from being applied to tools, weapons, or other non-armor items.
        if (hasMendingII(right) && !isArmor(left)) {
            event.setOutput(ItemStack.EMPTY);
            event.setCost(0);
            event.setMaterialCost(0);
        }
    }

    @SubscribeEvent
    public static void onXpPickup(PlayerXpEvent.PickupXp event) {
        Player player = event.getEntity();

        if (player.level().isClientSide()) {
            return;
        }

        ExperienceOrb orb = event.getOrb();
        int xp = orb.getValue();

        if (xp <= 0) {
            return;
        }

        Holder<net.minecraft.world.item.enchantment.Enchantment> mendingII =
                player.registryAccess()
                        .registryOrThrow(Registries.ENCHANTMENT)
                        .getHolderOrThrow(MENDING_II);

        List<ItemStack> candidates = new ArrayList<>();

        // Mending II only repairs equipped armor.
        for (ItemStack armor : player.getArmorSlots()) {
            if (isValidTarget(armor, mendingII)) {
                candidates.add(armor);
            }
        }

        if (candidates.isEmpty()) {
            return;
        }

        // Repair the armor with the lowest remaining durability percentage.
        ItemStack target = findLowestDurability(candidates);

        if (target.isEmpty()) {
            return;
        }

        // Vanilla Mending: 1 XP = 2 durability.
        // Mending II: 1 XP = 4 durability (2x Mending).
        final int DURABILITY_PER_XP = 4;

        int damage = target.getDamageValue();

        if (damage <= 0) {
            return;
        }

        int possibleRepair = xp * DURABILITY_PER_XP;
        int actualRepair = Math.min(damage, possibleRepair);

        target.setDamageValue(damage - actualRepair);

        int xpUsed =
                (actualRepair + DURABILITY_PER_XP - 1)
                        / DURABILITY_PER_XP;

        xpUsed = Math.min(xpUsed, xp);

        int remainingXp = xp - xpUsed;

        event.setCanceled(true);
        orb.discard();

        if (remainingXp > 0) {
            player.giveExperiencePoints(remainingXp);
        }
    }

    private static boolean hasMendingII(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }

        return stack.getItem() == net.minecraft.world.item.Items.ENCHANTED_BOOK
                && stack.get(DataComponents.ENCHANTMENTS) != null
                && EnchantmentHelper.getItemEnchantmentLevel(
                        MENDING_II,
                        stack
                ) > 0;
    }

    private static boolean isArmor(ItemStack stack) {
        return stack.getItem() instanceof ArmorItem;
    }

    private static boolean isValidTarget(
            ItemStack stack,
            Holder<net.minecraft.world.item.enchantment.Enchantment> mendingII
    ) {
        if (stack.isEmpty()) {
            return false;
        }

        if (!(stack.getItem() instanceof ArmorItem)) {
            return false;
        }

        if (!stack.isDamageableItem()) {
            return false;
        }

        if (!stack.isDamaged()) {
            return false;
        }

        return EnchantmentHelper.getItemEnchantmentLevel(
                mendingII,
                stack
        ) > 0;
    }

    private static ItemStack findLowestDurability(
            List<ItemStack> candidates
    ) {
        ItemStack best = ItemStack.EMPTY;
        double lowestRemainingPercentage = Double.MAX_VALUE;

        for (ItemStack stack : candidates) {
            int maxDamage = stack.getMaxDamage();
            int damage = stack.getDamageValue();

            if (maxDamage <= 0 || damage <= 0) {
                continue;
            }

            double remainingPercentage =
                    (double) (maxDamage - damage)
                            / (double) maxDamage;

            if (remainingPercentage < lowestRemainingPercentage) {
                lowestRemainingPercentage = remainingPercentage;
                best = stack;
            }
        }

        return best;
    }
}
