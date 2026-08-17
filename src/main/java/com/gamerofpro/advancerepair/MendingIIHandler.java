package com.gamerofpro.advancerepair;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerXpEvent;

import java.util.ArrayList;
import java.util.List;

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
        Player player = event.getEntity();
        ExperienceOrb orb = event.getOrb();

        if (player.level().isClientSide()) {
            return;
        }

        List<ItemStack> candidates = getMendingIIItems(player);

        if (candidates.isEmpty()) {
            return;
        }

        ItemStack target = findLowestDurability(candidates);

        if (target.isEmpty()) {
            return;
        }

        int xp = orb.getValue();

        if (xp <= 0) {
            return;
        }

        /*
         * Vanilla Mending:
         * 1 XP = 2 durability
         *
         * Mending II:
         * 1 XP = 4 durability
         */
        int repairPerXp = 4;

        int durabilityNeeded = target.getDamageValue();

        if (durabilityNeeded <= 0) {
            return;
        }

        int maxRepair = xp * repairPerXp;
        int actualRepair = Math.min(durabilityNeeded, maxRepair);

        target.setDamageValue(target.getDamageValue() - actualRepair);

        /*
         * Work out how much XP was actually consumed.
         *
         * Example:
         * 10 XP = 40 durability.
         * If the item only needs 15 durability,
         * only 4 XP is consumed and the remaining 6 XP
         * goes to the player's normal XP.
         */
        int xpUsed = (actualRepair + repairPerXp - 1) / repairPerXp;
        int remainingXp = xp - xpUsed;

        /*
         * Cancel normal XP pickup so vanilla Mending does not
         * process the same orb a second time.
         */
        event.setCanceled(true);

        if (remainingXp > 0) {
            player.giveExperiencePoints(remainingXp);
        }

        orb.discard();
    }

    private static List<ItemStack> getMendingIIItems(Player player) {
        List<ItemStack> items = new ArrayList<>();

        // Currently held item.
        ItemStack mainHand = player.getItemBySlot(EquipmentSlot.MAINHAND);

        if (hasMendingII(mainHand) && mainHand.isDamaged()) {
            items.add(mainHand);
        }

        // Armor.
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (!slot.isArmor()) {
                continue;
            }

            ItemStack armor = player.getItemBySlot(slot);

            if (hasMendingII(armor) && armor.isDamaged()) {
                items.add(armor);
            }
        }

        return items;
    }

    private static ItemStack findLowestDurability(List<ItemStack> items) {
        ItemStack best = ItemStack.EMPTY;

        double lowestPercentage = Double.MAX_VALUE;

        for (ItemStack stack : items) {
            int maxDamage = stack.getMaxDamage();
            int damage = stack.getDamageValue();

            if (maxDamage <= 0 || damage <= 0) {
                continue;
            }

            /*
             * Convert damage into remaining durability percentage.
             *
             * 10% remaining = more damaged than 50% remaining.
             */
            double remainingPercentage =
                    (double) (maxDamage - damage) / maxDamage;

            if (remainingPercentage < lowestPercentage) {
                lowestPercentage = remainingPercentage;
                best = stack;
            }
        }

        return best;
    }

    private static boolean hasMendingII(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }

        ItemEnchantments enchantments =
                stack.get(net.minecraft.core.component.DataComponents.ENCHANTMENTS);

        if (enchantments == null) {
            return false;
        }

        return enchantments.getLevel(MENDING_II_HOLDER) > 0;
    }

    private static Holder<Enchantment> MENDING_II_HOLDER;

    public static void initialize(net.minecraft.core.RegistryAccess registryAccess) {
        MENDING_II_HOLDER =
                registryAccess.registryOrThrow(Registries.ENCHANTMENT)
                        .getHolderOrThrow(MENDING_II);
    }
            }
