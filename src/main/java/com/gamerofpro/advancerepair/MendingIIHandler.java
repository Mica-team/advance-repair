package com.gamerofpro.advancerepair;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
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

        // Server only.
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

        // Currently held item.
        ItemStack mainHand = player.getItemBySlot(EquipmentSlot.MAINHAND);

        if (isValidTarget(mainHand, mendingII)) {
            candidates.add(mainHand);
        }

        // Equipped armor.
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (!slot.isArmor()) {
                continue;
            }

            ItemStack armor = player.getItemBySlot(slot);

            if (isValidTarget(armor, mendingII)) {
                candidates.add(armor);
            }
        }

        // No Mending II item to repair.
        if (candidates.isEmpty()) {
            return;
        }

        ItemStack target = findLowestDurability(candidates);

        if (target.isEmpty()) {
            return;
        }

        /*
         * Vanilla Mending:
         * 1 XP = 2 durability
         *
         * Mending II:
         * 1 XP = 4 durability
         */
        final int DURABILITY_PER_XP = 4;

        int damage = target.getDamageValue();

        if (damage <= 0) {
            return;
        }

        int possibleRepair = xp * DURABILITY_PER_XP;
        int actualRepair = Math.min(damage, possibleRepair);

        target.setDamageValue(damage - actualRepair);

        /*
         * Calculate how much XP was actually needed.
         */
        int xpUsed =
                (actualRepair + DURABILITY_PER_XP - 1)
                        / DURABILITY_PER_XP;

        xpUsed = Math.min(xpUsed, xp);

        int remainingXp = xp - xpUsed;

        /*
         * Prevent vanilla from processing this XP orb.
         */
        event.setCanceled(true);

        /*
         * The orb has been consumed.
         */
        orb.discard();

        /*
         * Give any unused XP back to the player.
         */
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

            /*
             * Example:
             *
             * 1000 / 2000 remaining = 50%
             * 100 / 500 remaining   = 20%
             *
             * The 20% item wins because it is in worse condition.
             */
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
