package com.gamerofpro.advancerepair;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.neoforged.bus.api.SubscribeEvent;
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

        // Check equipped armor first.
        for (ItemStack armor : player.getArmorSlots()) {
            if (isValidTarget(armor, mendingII)) {
                candidates.add(armor);
            }
        }

        // Also check the item currently held in either hand.
        ItemStack mainHand = player.getMainHandItem();
        if (isValidTarget(mainHand, mendingII)) {
            candidates.add(mainHand);
        }

        ItemStack offHand = player.getOffhandItem();
        if (isValidTarget(offHand, mendingII)) {
            candidates.add(offHand);
        }

        if (candidates.isEmpty()) {
            return;
        }

        ItemStack target = findLowestDurability(candidates);

        if (target.isEmpty()) {
            return;
        }

        // Vanilla Mending: 1 XP = 2 durability.
        // Mending II: 1 XP = 4 durability (2x normal Mending).
        final int DURABILITY_PER_XP = 4;

        int damage = target.getDamageValue();
        if (damage <= 0) {
            return;
        }

        int possibleRepair = xp * DURABILITY_PER_XP;
        int actualRepair = Math.min(damage, possibleRepair);

        target.setDamageValue(damage - actualRepair);

        int xpUsed = (actualRepair + DURABILITY_PER_XP - 1) / DURABILITY_PER_XP;
        xpUsed = Math.min(xpUsed, xp);

        int remainingXp = xp - xpUsed;

        event.setCanceled(true);
        orb.discard();

        if (remainingXp > 0) {
            player.giveExperiencePoints(remainingXp);
        }
    }

    private static boolean isValidTarget(
            ItemStack stack,
            Holder<net.minecraft.world.item.enchantment.Enchantment> mendingII
    ) {
        if (stack.isEmpty()) {
            return false;
        }

        if (!stack.isDamageableItem() || !stack.isDamaged()) {
            return false;
        }

        return stack.getEnchantmentLevel(mendingII) > 0;
    }

    private static ItemStack findLowestDurability(List<ItemStack> candidates) {
        ItemStack best = ItemStack.EMPTY;
        double lowestRemainingPercentage = Double.MAX_VALUE;

        for (ItemStack stack : candidates) {
            int maxDamage = stack.getMaxDamage();
            int damage = stack.getDamageValue();

            if (maxDamage <= 0 || damage <= 0) {
                continue;
            }

            double remainingPercentage =
                    (double) (maxDamage - damage) / (double) maxDamage;

            if (remainingPercentage < lowestRemainingPercentage) {
                lowestRemainingPercentage = remainingPercentage;
                best = stack;
            }
        }

        return best;
    }
}
