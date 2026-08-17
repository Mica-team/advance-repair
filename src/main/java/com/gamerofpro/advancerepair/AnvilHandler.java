package com.gamerofpro.advancerepair;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.Tiers;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.AnvilUpdateEvent;

public class AnvilHandler {

    @SubscribeEvent
    public static void onAnvilUpdate(AnvilUpdateEvent event) {

        if (!AdvanceRepairMod.MOD_ENABLED.get()) {
            return;
        }

        ItemStack left = event.getLeft();
        ItemStack right = event.getRight();

        if (left.isEmpty() || right.isEmpty()) {
            return;
        }

        int maxDamage = left.getMaxDamage();
        int currentDamage = left.getDamageValue();

        if (maxDamage <= 0) {
            return;
        }

        ResourceLocation itemKey =
                BuiltInRegistries.ITEM.getKey(left.getItem());

        String namespace = itemKey.getNamespace();
        String path = itemKey.getPath().toLowerCase();

        String targetModId =
                AdvanceRepairMod.TARGET_MOD_ID.get();

        int repairAmount = 0;

        /*
         * VANILLA MINECRAFT
         */
        if (targetModId.equalsIgnoreCase("minecraft")
                && namespace.equalsIgnoreCase("minecraft")) {

            /*
             * NETHERITE
             *
             * Netherite Ingot = 100%
             * Diamond = 80%
             * Iron = 50%
             * Gold = 40%
             * Copper = 40%
             */
            if (isNetherite(left)) {

                if (right.is(Items.NETHERITE_INGOT)) {
                    repairAmount = maxDamage;

                } else if (right.is(Items.DIAMOND)) {
                    repairAmount = (int) (maxDamage * 0.8);

                } else if (right.is(Items.IRON_INGOT)) {
                    repairAmount = (int) (maxDamage * 0.5);

                } else if (right.is(Items.GOLD_INGOT)) {
                    repairAmount = (int) (maxDamage * 0.4);

                } else if (right.is(Items.COPPER_INGOT)) {
                    repairAmount = (int) (maxDamage * 0.4);
                }

            /*
             * DIAMOND
             *
             * Diamond = 100%
             * Iron = 60%
             * Gold = 45%
             * Copper = 40%
             */
            } else if (isDiamond(left)) {

                if (right.is(Items.DIAMOND)) {
                    repairAmount = maxDamage;

                } else if (right.is(Items.IRON_INGOT)) {
                    repairAmount = (int) (maxDamage * 0.6);

                } else if (right.is(Items.GOLD_INGOT)) {
                    repairAmount = (int) (maxDamage * 0.45);

                } else if (right.is(Items.COPPER_INGOT)) {
                    repairAmount = (int) (maxDamage * 0.4);
                }

            /*
             * IRON
             *
             * Iron = 100%
             * Gold = 70%
             * Copper = 40%
             */
            } else if (isIron(left)) {

                if (right.is(Items.IRON_INGOT)) {
                    repairAmount = maxDamage;

                } else if (right.is(Items.GOLD_INGOT)) {
                    repairAmount = (int) (maxDamage * 0.7);

                } else if (right.is(Items.COPPER_INGOT)) {
                    repairAmount = (int) (maxDamage * 0.4);
                }

            /*
             * GOLD
             *
             * Gold = 100%
             * Copper = 40%
             */
            } else if (isGold(left)) {

                if (right.is(Items.GOLD_INGOT)) {
                    repairAmount = maxDamage;

                } else if (right.is(Items.COPPER_INGOT)) {
                    repairAmount = (int) (maxDamage * 0.4);
                }

            /*
             * LEATHER
             *
             * Leather = 100%
             */
            } else if (isLeather(left)) {

                if (right.is(Items.LEATHER)) {
                    repairAmount = maxDamage;
                }

            /*
             * CHAINMAIL
             *
             * Intentionally unchanged:
             * Chain = 60%
             * Iron = 50%
             */
            } else if (isChainmail(left)) {

                if (right.is(Items.CHAIN)) {
                    repairAmount = (int) (maxDamage * 0.6);

                } else if (right.is(Items.IRON_INGOT)) {
                    repairAmount = (int) (maxDamage * 0.5);
                }
            }

        /*
         * MODDED EQUIPMENT
         */
        } else if (namespace.equalsIgnoreCase(targetModId)) {

            double percentage = 0.0;

            var itemType = left.getItem();

            if (itemType instanceof TieredItem tieredItem) {

                Tier tier = tieredItem.getTier();

                if (tier == Tiers.NETHERITE) {
                    percentage = 1.0;

                } else if (tier == Tiers.DIAMOND) {
                    percentage = 0.8;

                } else if (tier == Tiers.IRON) {
                    percentage = 0.6;

                } else if (tier == Tiers.GOLD) {
                    percentage = 0.4;
                }

            } else if (itemType instanceof ArmorItem armorItem) {

                Holder<?> material = armorItem.getMaterial();

                if (material == ArmorMaterials.NETHERITE) {
                    percentage = 1.0;

                } else if (material == ArmorMaterials.DIAMOND) {
                    percentage = 0.8;

                } else if (material == ArmorMaterials.IRON) {
                    percentage = 0.6;

                } else if (material == ArmorMaterials.GOLD) {
                    percentage = 0.4;
                }
            }

            /*
             * Fallback based on item name
             */
            if (percentage == 0.0) {

                if (path.contains("netherite")) {
                    percentage = 1.0;

                } else if (path.contains("diamond")) {
                    percentage = 0.8;

                } else if (path.contains("iron")) {
                    percentage = 0.6;

                } else if (path.contains("gold")
                        || path.contains("copper")) {
                    percentage = 0.4;
                }
            }

            if (percentage > 0.0) {
                repairAmount =
                        (int) (maxDamage * percentage);
            }
        }

        /*
         * No valid repair
         */
        if (repairAmount <= 0) {
            return;
        }

        /*
         * Create repaired output
         */
        ItemStack output = left.copy();

        int newDamage =
                Math.max(0, currentDamage - repairAmount);

        output.setDamageValue(newDamage);

        /*
         * Calculate XP cost
         */
        boolean hasTrim =
                left.has(DataComponents.TRIM);

        boolean isEnchanted =
                left.isEnchanted();

        int cost;

        if (hasTrim && isEnchanted) {
            cost = 12;

        } else if (isEnchanted) {
            cost = 10;

        } else if (hasTrim) {
            cost = 8;

        } else {
            cost = 5;
        }

        /*
         * Set anvil result
         */
        event.setOutput(output);
        event.setCost(cost);
        event.setMaterialCost(1);
    }

    /*
     * NETHERITE
     */
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

    /*
     * DIAMOND
     */
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

    /*
     * IRON
     */
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

    /*
     * GOLD
     */
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

    /*
     * LEATHER
     */
    private static boolean isLeather(ItemStack stack) {

        return stack.is(Items.LEATHER_HELMET)
                || stack.is(Items.LEATHER_CHESTPLATE)
                || stack.is(Items.LEATHER_LEGGINGS)
                || stack.is(Items.LEATHER_BOOTS);
    }

    /*
     * CHAINMAIL
     */
    private static boolean isChainmail(ItemStack stack) {

        return stack.is(Items.CHAINMAIL_HELMET)
                || stack.is(Items.CHAINMAIL_CHESTPLATE)
                || stack.is(Items.CHAINMAIL_LEGGINGS)
                || stack.is(Items.CHAINMAIL_BOOTS);
    }
                }
