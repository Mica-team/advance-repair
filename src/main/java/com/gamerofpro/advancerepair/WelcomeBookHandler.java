package com.gamerofpro.advancerepair;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.WrittenBookContent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import java.util.List;

public class WelcomeBookHandler {

    private static final String BOOK_GIVEN_TAG =
            "advancerepair_welcome_book_given";

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {

        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        // Only give the book once per player.
        if (player.getPersistentData().getBoolean(BOOK_GIVEN_TAG)) {
            return;
        }

        ItemStack book = new ItemStack(Items.WRITTEN_BOOK);

        List<Component> pages = List.of(

                // PAGE 1
                Component.literal(
                        "Dear player,\n\n"
                        + "We know it has been a while since "
                        + "Advance Repair received an update.\n\n"
                        + "We haven't forgotten about the mod."
                ),

                // PAGE 2
                Component.literal(
                        "We've been working behind the scenes "
                        + "on new ideas and improvements.\n\n"
                        + "Thank you for still playing.\n\n"
                        + "We are sorry for the long wait.\n\n"
                        + "— HR"
                ),

                // PAGE 3 — EASTER EGG
                Component.empty()
                        .append(
                                Component.literal("Psst...\n\n")
                                        .withStyle(ChatFormatting.DARK_GRAY)
                        )
                        .append(
                                Component.literal(
                                        "You found an Easter Egg.\n\n"
                                        + "Or maybe...\n"
                                        + "the game found you."
                                )
                                .withStyle(ChatFormatting.OBFUSCATED)
                        )
                        .append(
                                Component.literal(
                                        "\n\nSomething isn't quite right..."
                                )
                                .withStyle(ChatFormatting.DARK_RED)
                        )
        );

        /*
         * Minecraft 1.21.1 written-book component.
         *
         * Title: A Message from HR
         * Author: HR
         * Generation: 0 (original)
         */
        WrittenBookContent content = new WrittenBookContent(
                "A Message from HR",
                "HR",
                0,
                pages,
                true
        );

        book.set(
                DataComponents.WRITTEN_BOOK_CONTENT,
                content
        );

        // Give the book to the player.
        player.getInventory().add(book);

        // Remember that this player received it.
        player.getPersistentData().putBoolean(
                BOOK_GIVEN_TAG,
                true
        );
    }
}
