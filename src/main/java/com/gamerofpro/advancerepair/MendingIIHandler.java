package com.gamerofpro.advancerepair;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerXpEvent;

public class MendingIIHandler {

    private static final ResourceKey<Enchantment> MENDING_II =
            ResourceKey.create(
                    Registries.ENCHANTMENT,
                    ResourceLocation.fromNamespaceAndPath(
                            AdvanceRepairMod.MODID,
                            "mending_ii"
                    )
            );

    @SubscribeEvent
    public static void onXpPickup(PlayerXpEvent.PickupXp event) {
        if (!AdvanceRepairMod.MOD_ENABLED.get()
                || !AdvanceRepairMod.QUALITY_OF_LIFE.get()) {
            return;
        }

        Player player = event.getEntity();

        if (player.level().isClientSide()) {
            return;
        }

        ExperienceOrb orb = event.getOrb();
        int xp = orb.getValue();

        if (xp <= 0) {
            return;
        }

        Holder<Enchantment> mendingII =
                player.registryAccess()
                        .registryOrThrow(Registries.ENCHANTMENT)
                        .getHolderOrThrow(MENDING_II);

        ItemStack target = findLowestDurabilityTarget(player, mendingII);

        if (target.isEmpty()) {
            return;
        }

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

    private static ItemStack findLowestDurabilityTarget(
            Player player,
            Holder<Enchantment> mendingII
    ) {
        ItemStack best = ItemStack.EMPTY;
        double lowestRemainingPercentage = Double.MAX_VALUE;

        for (ItemStack armor : player.getArmorSlots()) {
            if (isBetterTarget(armor, mendingII, lowestRemainingPercentage)) {
                best = armor;
                lowestRemainingPercentage = remainingPercentage(armor);
            }
        }

        ItemStack mainHand = player.getMainHandItem();
        if (isBetterTarget(mainHand, mendingII, lowestRemainingPercentage)) {
            best = mainHand;
            lowestRemainingPercentage = remainingPercentage(mainHand);
        }

        ItemStack offHand = player.getOffhandItem();
        if (isBetterTarget(offHand, mendingII, lowestRemainingPercentage)) {
            best = offHand;
        }

        return best;
    }

    private static boolean isBetterTarget(
            ItemStack stack,
            Holder<Enchantment> mendingII,
            double currentLowestPercentage
    ) {
        if (!isValidTarget(stack, mendingII)) {
            return false;
        }

        return remainingPercentage(stack) < currentLowestPercentage;
    }

    private static boolean isValidTarget(
            ItemStack stack,
            Holder<Enchantment> mendingII
    ) {
        return !stack.isEmpty()
                && stack.isDamageableItem()
                && stack.isDamaged()
                && stack.getEnchantmentLevel(mendingII) > 0;
    }

    private static double remainingPercentage(ItemStack stack) {
        int maxDamage = stack.getMaxDamage();
        int damage = stack.getDamageValue();

        if (maxDamage <= 0 || damage <= 0) {
            return Double.MAX_VALUE;
        }

        return (double) (maxDamage - damage) / (double) maxDamage;
    }
}
