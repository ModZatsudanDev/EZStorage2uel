package modzatsudan.ezstorage.gui.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import modzatsudan.ezstorage.ref.RefStrings;

/** Custom buttons for the security block GUI and other stuff */
public class ButtonBlue extends GuiButton {

    private static final ResourceLocation tex = new ResourceLocation(RefStrings.MODID, "textures/gui/custom_gui.png");

    public ButtonBlue(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText) {
        super(buttonId, x, y, widthIn, heightIn, buttonText);
        this.visible = false;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        if (this.visible) {
            FontRenderer fontrenderer = mc.fontRenderer;
            mc.getTextureManager().bindTexture(tex);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width &&
                    mouseY < this.y + this.height;
            int i = this.getHoverState(this.hovered);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
                    GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                    GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA,
                    GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            this.drawTexturedModalRect(this.x, this.y, 0, i * 14, this.width / 2, this.height);
            this.drawTexturedModalRect(this.x + this.width / 2, this.y, 256 - this.width / 2, i * 14, this.width / 2,
                    this.height);
            this.mouseDragged(mc, mouseX, mouseY);
            int j = 0xE0E0E0;

            if (packedFGColour != 0) {
                j = packedFGColour;
            } else if (!this.enabled) {
                j = 0xA0A0A0;
            } else if (this.hovered) {
                j = 0xFFFFFF;
            }

            this.drawCenteredString(fontrenderer, this.displayString, this.x + this.width / 2,
                    this.y + (this.height - 8) / 2, j);
        }
    }
}
