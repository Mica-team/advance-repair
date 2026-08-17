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

        String targetModId = AdvanceRepairMod.TARGET_MOD_ID.get();

        int repairAmount = 0;

        if (targetModId.equalsIgnoreCase("minecraft")
                && namespace.equalsIgnoreCase("minecraft")) {

            if (isNetherite(left)) {
                if (right.is(Items.DIAMOND)) {
                    repairAmount = (int) (maxDamage * 0.8);
                } else if (right.is(Items.IRON_INGOT)) {
                    repairAmount = (int) (maxDamage * 0.5);
                } else if (right.is(Items.GOLD_INGOT)) {
                    repairAmount = (int) (maxDamage * 0.4);
                } else if (right.is(Items.COPPER_INGOT)) {
                    repairAmount = (int) (maxDamage * 0.4);
                }
            } else if (isDiamond(left)) {
                if (right.is(Items.IRON_INGOT)) {
                    repairAmount = (int) (maxDamage * 0.6);
                } else if (right.is(Items.GOLD_INGOT)) {
                    repairAmount = (int) (maxDamage * 0.45);
                } else if (right.is(Items.COPPER_INGOT)) {
                    repairAmount = (int) (maxDamage * 0.4);
                }
            } else if (isIron(left)) {
                if (right.is(Items.GOLD_INGOT)) {
                    repairAmount = (int) (maxDamage * 0.7);
                } else if (right.is(Items.COPPER_INGOT)) {
                    repairAmount = (int) (maxDamage * 0.4);
                }
            } else if (isLeather(left)) {
                if (right.is(Items.LEATHER)) {
                    repairAmount = maxDamage;
                }
            } else if (isChainmail(left)) {
                if (right.is(Items.CHAIN)) {
                    repairAmount = (int) (maxDamage * 0.6);
                } else if (right.is(Items.IRON_INGOT)) {
                    repairAmount = (int) (maxDamage * 0.5);
                }
            }

        } else if (namespace.equalsIgnoreCase(targetModId)) {

            double percentage = 0.0;

            ItemStack item = left;
            var itemType = item.getItem();

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
                repairAmount = (int) (maxDamage * percentage);
            }
        }

        if (repairAmount <= 0) {
            return;
        }

        ItemStack output = left.copy();

        int newDamage = Math.max(0, currentDamage - repairAmount);
        output.setDamageValue(newDamage);

        boolean hasTrim = left.has(DataComponents.TRIM);
        boolean isEnchanted = left.isEnchanted();

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

        event.setOutput(output);
        event.setCost(cost);
        event.setMaterialCost(1);
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
          }
