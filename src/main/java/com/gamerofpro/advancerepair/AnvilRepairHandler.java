package com.gamerofpro.advancerepair;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.AnvilBlock;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

public class AnvilRepairHandler {

    @SubscribeEvent
    public static void onRightClickAnvil(PlayerInteractEvent.RightClickBlock event) {
        if (!AdvanceRepairMod.MOD_ENABLED.get()
                || !AdvanceRepairMod.QUALITY_OF_LIFE.get()) {
            return;
        }

        if (event.getLevel().isClientSide()) {
            return;
        }

        if (!event.getItemStack().is(Items.IRON_BLOCK)) {
            return;
        }

        var level = event.getLevel();
        var pos = event.getPos();
        var state = level.getBlockState(pos);

        if (!state.is(Blocks.ANVIL)) {
            return;
        }

        int damage = state.getValue(AnvilBlock.DAMAGE);

        if (damage <= 0) {
            return;
        }

        level.setBlock(
                pos,
                state.setValue(AnvilBlock.DAMAGE, damage - 1),
                3
        );

        ItemStack held = event.getItemStack();
        held.shrink(1);

        event.setCancellationResult(InteractionResult.SUCCESS);
        event.setCanceled(true);
    }
}
