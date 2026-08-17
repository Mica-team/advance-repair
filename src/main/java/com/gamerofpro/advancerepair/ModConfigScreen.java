package com.gamerofpro.advancerepair;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.fml.ModList;

public class ModConfigScreen extends Screen {

    private final Screen parentScreen;

    private boolean tempEnabled;

    private EditBox modIdInput;

    private Button saveButton;

    private boolean isModIdValid;

    public ModConfigScreen(Screen parentScreen) {
        super(Component.literal("Advance Repair Configuration"));

        this.isModIdValid = true;
        this.parentScreen = parentScreen;
    }

    @Override
    protected void init() {
        super.init();

        this.tempEnabled = AdvanceRepairMod.MOD_ENABLED.get();

        this.addRenderableWidget(
                CycleButton.onOffBuilder(this.tempEnabled)
                        .create(
                                this.width / 2 - 100,
                                60,
                                200,
                                20,
                                Component.literal("Mod Status"),
                                (button, value) -> this.tempEnabled = value
                        )
        );

        this.modIdInput = new EditBox(
                this.font,
                this.width / 2 - 100,
                100,
                200,
                20,
                Component.literal("Mod ID")
        );

        this.modIdInput.setValue(
                AdvanceRepairMod.TARGET_MOD_ID.get()
        );

        this.modIdInput.setResponder(value -> {
            String id = value.trim().toLowerCase();

            this.isModIdValid =
                    id.equals("minecraft")
                            || ModList.get().isLoaded(id);

            if (this.saveButton != null) {
                this.saveButton.active = this.isModIdValid;
            }
        });

        this.addRenderableWidget(this.modIdInput);

        this.saveButton = Button.builder(
                        Component.literal("Save & Exit"),
                        button -> {
                            AdvanceRepairMod.MOD_ENABLED.set(
                                    this.tempEnabled
                            );

                            AdvanceRepairMod.TARGET_MOD_ID.set(
                                    this.modIdInput.getValue()
                                            .trim()
                                            .toLowerCase()
                            );

                            AdvanceRepairMod.SPEC.save();

                            this.minecraft.setScreen(
                                    this.parentScreen
                            );
                        }
                )
                .bounds(
                        this.width / 2 - 100,
                        this.height - 40,
                        200,
                        20
                )
                .build();

        String id = this.modIdInput.getValue();

        this.isModIdValid =
                id.equals("minecraft")
                        || ModList.get().isLoaded(id);

        this.saveButton.active = this.isModIdValid;

        this.addRenderableWidget(this.saveButton);
    }

    @Override
    public void render(
            GuiGraphics guiGraphics,
            int mouseX,
            int mouseY,
            float partialTick
    ) {
        this.renderBackground(
                guiGraphics,
                mouseX,
                mouseY,
                partialTick
        );

        guiGraphics.drawCenteredString(
                this.font,
                this.title,
                this.width / 2,
                20,
                0xFFFFFF
        );

        guiGraphics.drawString(
                this.font,
                "Target Mod ID Namespace:",
                this.width / 2 - 100,
                88,
                0xA0A0A0
        );

        if (!this.isModIdValid) {
            guiGraphics.drawString(
                    this.font,
                    "?? Invalid or Unloaded Mod ID!",
                    this.width / 2 - 100,
                    125,
                    ChatFormatting.RED.getColor()
            );
        } else {
            guiGraphics.drawString(
                    this.font,
                    "? Valid Loaded Namespace",
                    this.width / 2 - 100,
                    125,
                    ChatFormatting.GREEN.getColor()
            );
        }

        super.render(
                guiGraphics,
                mouseX,
                mouseY,
                partialTick
        );
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.parentScreen);
    }
          }
