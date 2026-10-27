package com.gamerofpro.advancerepair;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.Filterable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.WrittenBookContent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import java.util.List;

public class WelcomeBookHandler {
    private static final String BOOK_GIVEN_TAG = "advancerepair_welcome_book_given";

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (player.getPersistentData().getBoolean(BOOK_GIVEN_TAG)) return;

        ItemStack book = new ItemStack(Items.WRITTEN_BOOK);
        Component page1 = Component.translatable("advancerepair.book.page1");
        Component page2 = Component.translatable("advancerepair.book.page2");
        Component page3 = Component.translatable("advancerepair.book.page3");

        List<Filterable<Component>> pages = List.of(
                Filterable.passThrough(page1),
                Filterable.passThrough(page2),
                Filterable.passThrough(page3)
        );
        WrittenBookContent content = new WrittenBookContent(
                Filterable.passThrough("A Message from HR"),
                "HR",
                0,
                pages,
                true
        );
        book.set(DataComponents.WRITTEN_BOOK_CONTENT, content);
        player.getInventory().add(book);
        player.getPersistentData().putBoolean(BOOK_GIVEN_TAG, true);
    }
}
