package net.syrupstudios.fortunecookie.data;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class FortuneScreen extends Screen {
    private static final ResourceLocation TEXTURE = new ResourceLocation("fortunecookie", "textures/gui/fortune_screen.png");
    private final String fortune;
    private static final int PANEL_WIDTH = 276;
    private static final int PANEL_HEIGHT = 166;

    public FortuneScreen(String fortune) {
        super(Component.literal("Fortune Cookie"));
        this.fortune = fortune;
    }

    @Override
    protected void init() {
        super.init();

        int x = (this.width - PANEL_WIDTH) / 2;
        int y = (this.height - PANEL_HEIGHT) / 2;

        this.addRenderableWidget(Button.builder(Component.literal("Ok thanks!"), button -> {
            this.onClose();
        }).bounds(x + PANEL_WIDTH / 2 - 75, y + PANEL_HEIGHT - 35, 150, 20).build());
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);

        int x = (this.width - PANEL_WIDTH) / 2;
        int y = (this.height - PANEL_HEIGHT) / 2;

        // Draw panel background
        context.fill(x, y, x + PANEL_WIDTH, y + PANEL_HEIGHT, 0xDD3C2A1E);
        context.fill(x + 2, y + 2, x + PANEL_WIDTH - 2, y + PANEL_HEIGHT - 2, 0xFFD7CDB5);

        // Draw title
        Component title = Component.literal("Your Fortune");
        int titleWidth = this.font.width(title);
        context.drawString(this.font, title, x + (PANEL_WIDTH - titleWidth) / 2, y + 15, 0x3C2A1E, false);

        // Draw fortune text with word wrapping
        int textY = y + 40;
        int maxWidth = PANEL_WIDTH - 40;
        int lineHeight = 12;

        String[] words = fortune.split(" ");
        StringBuilder currentLine = new StringBuilder();

        for (String word : words) {
            String testLine = currentLine.length() == 0 ? word : currentLine + " " + word;
            int testWidth = this.font.width(testLine);

            if (testWidth > maxWidth && currentLine.length() > 0) {
                String line = currentLine.toString();
                int lineWidth = this.font.width(line);
                context.drawString(this.font, line, x + (PANEL_WIDTH - lineWidth) / 2, textY, 0x5C4A3E, false);
                textY += lineHeight;
                currentLine = new StringBuilder(word);
            } else {
                currentLine = new StringBuilder(testLine);
            }
        }

        // Draw remaining text
        if (currentLine.length() > 0) {
            String line = currentLine.toString();
            int lineWidth = this.font.width(line);
            context.drawString(this.font, line, x + (PANEL_WIDTH - lineWidth) / 2, textY, 0x5C4A3E, false);
        }

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}