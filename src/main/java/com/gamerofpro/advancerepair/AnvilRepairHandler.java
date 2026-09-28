package com.gamerofpro.advancerepair;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
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

        var repairedBlock = state.is(Blocks.DAMAGED_ANVIL)
                ? Blocks.CHIPPED_ANVIL
                : state.is(Blocks.CHIPPED_ANVIL)
                        ? Blocks.ANVIL
                        : null;

        if (repairedBlock == null) {
            return;
        }

        level.setBlock(pos, repairedBlock.defaultBlockState()
                .setValue(
                        net.minecraft.world.level.block.AnvilBlock.FACING,
                        state.getValue(net.minecraft.world.level.block.AnvilBlock.FACING)
                ), 3);

        ItemStack held = event.getItemStack();
        held.shrink(1);

        event.setCancellationResult(InteractionResult.SUCCESS);
        event.setCanceled(true);
    }
}
