package modzatsudan.ezstorage.util;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.ModelManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;

import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

/** Render items in the storage core GUI */
public class EZItemRenderer extends RenderItem {

    public EZItemRenderer(TextureManager textureManager, ModelManager modelManager) {
        super(textureManager, modelManager, null);
    }

    @Override
    public void renderItemOverlayIntoGUI(FontRenderer fr, ItemStack stack, int xPosition, int yPosition,
                                         String amtString) {
        ItemStack fakeItem = stack.copy();
        fakeItem.setCount(1);
        super.renderItemOverlayIntoGUI(fr, fakeItem, xPosition, yPosition, null);
        if (!stack.isEmpty()) {
            float ScaleFactor = 0.5f;
            float RScaleFactor = 1.0f / ScaleFactor;
            int offset = 0;

            boolean unicodeFlag = fr.getUnicodeFlag();
            fr.setUnicodeFlag(false);

            long amount = Long.parseLong(amtString);

            if (amount != 0) {

                String var6 = getSIMeasure(amount);

                GL11.glDisable(GL11.GL_LIGHTING);
                GL11.glDisable(GL11.GL_DEPTH_TEST);
                GL11.glPushMatrix();
                GL11.glScaled(ScaleFactor, ScaleFactor, ScaleFactor);
                int X = (int) (((float) xPosition + offset + 16.0f - fr.getStringWidth(var6) * ScaleFactor) *
                        RScaleFactor);
                int Y = (int) (((float) yPosition + offset + 16.0f - 7.0f * ScaleFactor) * RScaleFactor);
                fr.drawStringWithShadow(var6, X, Y, 16777215);
                GL11.glPopMatrix();
                GL11.glEnable(GL11.GL_LIGHTING);
                GL11.glEnable(GL11.GL_DEPTH_TEST);
            }

            fr.setUnicodeFlag(unicodeFlag);
        }
    }

    @NotNull
    private static String getSIMeasure(long amount) {
        String var6 = String.valueOf(Math.abs(amount));

        if (amount > 999999999999L) {
            var6 = String.valueOf((int) Math.floor(amount / 1000000000000.0)) + 'T';
        } else if (amount > 9999999999L) {
            var6 = "." + String.valueOf((int) Math.floor(amount / 1000000000000.0)) + 'T';
        } else if (amount > 999999999L) {
            var6 = String.valueOf((int) Math.floor(amount / 1000000000.0)) + 'B';
        } else if (amount > 99999999L) {
            var6 = "." + (int) Math.floor(amount / 100000000.0) + 'B';
        } else if (amount > 999999L) {
            var6 = String.valueOf((int) Math.floor(amount / 1000000.0)) + 'M';
        } else if (amount > 99999L) {
            var6 = "." + (int) Math.floor(amount / 100000.0) + 'M';
        } else if (amount > 9999L) {
            var6 = String.valueOf((int) Math.floor(amount / 1000.0)) + 'K';
        }
        return var6;
    }

    /** Draw with the WorldRenderer */
    private void draw(BufferBuilder renderer, int x, int y, int width, int height, int red, int green, int blue,
                      int alpha) {
        renderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        renderer.pos(x + 0, y + 0, 0.0D).color(red, green, blue, alpha).endVertex();
        renderer.pos(x + 0, y + height, 0.0D).color(red, green, blue, alpha).endVertex();
        renderer.pos(x + width, y + height, 0.0D).color(red, green, blue, alpha).endVertex();
        renderer.pos(x + width, y + 0, 0.0D).color(red, green, blue, alpha).endVertex();
        Tessellator.getInstance().draw();
    }
}
