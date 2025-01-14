package net.cacpixel.rtmmetro.client.gui;

import jp.ngt.ngtlib.gui.GuiTextFieldCustom;
import jp.ngt.rtm.rail.BlockMarker;
import jp.ngt.rtm.rail.RenderMarkerBlock;
import jp.ngt.rtm.rail.util.RailPosition;
import net.cacpixel.rtmmetro.ModConfig;
import net.cacpixel.rtmmetro.RTMMetro;
import net.cacpixel.rtmmetro.RTMMetroBlock;
import net.cacpixel.rtmmetro.network.PacketMarkerClient;
import net.cacpixel.rtmmetro.rail.tileentity.TileEntityMarkerAdvanced;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

import java.io.IOException;

@SideOnly(Side.CLIENT)
public class GUIMarkerAdvanced extends GuiScreenAdvanced
{
    public final TileEntityMarkerAdvanced marker;
    private RailPosition currentRP;
    private GuiTextFieldAdvancedInt fieldGroup;
    private GuiTextFieldAdvancedInt fieldRailHeight;
    private GuiTextFieldAdvancedFloat fieldAnchorLengthHorizontal;
    private GuiTextFieldAdvancedFloat fieldAnchorPitch;
    private GuiTextFieldAdvancedFloat fieldAnchorLengthVertical;
    private GuiTextFieldAdvancedFloat fieldAnchorYaw;
    private GuiTextFieldAdvancedFloat fieldCantCenter;
    private GuiTextFieldAdvancedFloat fieldCantEdge;
    private GuiTextFieldAdvancedFloat fieldCantRandom;
    private static final int BUTTON_OK = 0;
    private static final int BUTTON_CANCEL = 1;

    public GUIMarkerAdvanced(TileEntityMarkerAdvanced marker)
    {
        this.marker = marker;
    }

    @Override
    public void initGui()
    {
        int fieldWidth = 75;
        int fieldHeight = 16;
        int stringXpos = this.width - fieldWidth - 50;
        int fieldYpos = 9;

        super.initGui();
        this.currentRP = this.marker.getMarkerRP();

        int hw = this.width / 2;

        this.buttonList.clear();
        this.buttonList.add(new GuiButton(BUTTON_OK, this.width - 180, this.height - 30,
                120, 20, I18n.format("gui.done")));

        this.fieldGroup = this.setTextField(stringXpos, fieldYpos, fieldWidth, fieldHeight,
                this.marker.groupNumber, Integer.MIN_VALUE, Integer.MAX_VALUE, false);
        fieldYpos += 20;
        this.fieldRailHeight = this.setTextField(stringXpos, fieldYpos, fieldWidth, fieldHeight,
                this.currentRP.height, 0, 15, false);
        fieldYpos += 20;
        this.fieldAnchorLengthHorizontal = this.setTextField(stringXpos, fieldYpos, fieldWidth, fieldHeight,
                this.currentRP.anchorLengthHorizontal, 0.0f, (float) ModConfig.railGeneratingDistance, false);
        fieldYpos += 20;
        this.fieldAnchorYaw = this.setTextField(stringXpos, fieldYpos, fieldWidth, fieldHeight,
                this.currentRP.anchorYaw, -180.0f, 180.0f, true);
        fieldYpos += 20;
        this.fieldAnchorLengthVertical = this.setTextField(stringXpos, fieldYpos, fieldWidth, fieldHeight,
                this.currentRP.anchorLengthVertical, 0.0f, (float) ModConfig.railGeneratingDistance, false);
        fieldYpos += 20;
        this.fieldAnchorPitch = this.setTextField(stringXpos, fieldYpos, fieldWidth, fieldHeight,
                this.currentRP.anchorPitch, -90.0f, 90.0f, false);
        fieldYpos += 20;
        this.fieldCantCenter = this.setTextField(stringXpos, fieldYpos, fieldWidth, fieldHeight,
                this.currentRP.cantCenter, -90.0f, 90.0f, false);
        fieldYpos += 20;
        this.fieldCantEdge = this.setTextField(stringXpos, fieldYpos, fieldWidth, fieldHeight,
                this.currentRP.cantEdge, -90.0f, 90.0f, false);
        fieldYpos += 20;
        this.fieldCantRandom = this.setTextField(stringXpos, fieldYpos, fieldWidth, fieldHeight,
                this.currentRP.cantRandom, 0.0f, 100.0f, false);

        if (this.marker.getBlockType() == RTMMetroBlock.MARKER_ADVANCED_SWITCH) {
            this.fieldAnchorPitch.setEnabled(false);
            this.fieldAnchorLengthVertical.setEnabled(false);
            this.fieldCantCenter.setEnabled(false);
            this.fieldCantEdge.setEnabled(false);
        } else if (!this.marker.isCoreMarker()) {
            this.fieldCantCenter.setEnabled(false);
            this.fieldCantRandom.setEnabled(false);
        }
    }

    @Override
    public void drawScreen(int par1, int par2, float par3)
    {
        int stringXpos = this.width - 220;
        int stringYpos = 13;

        // 这么操作其实不好，但是目前只能想到这个办法，就先这样吧
        if (!(marker.getWorld().getBlockState(new BlockPos(marker.getX(),marker.getY(),marker.getZ()))
                .getBlock() instanceof BlockMarker))
        {
            this.mc.displayGuiScreen(null);
        }

        this.drawGradientRect(Math.max(0, stringXpos - 20), 0, this.width, this.height,
                0xC0101010, 0xD0101010);
        super.drawScreen(par1, par2, par3);

        this.drawString(this.fontRenderer, "Marker Group", stringXpos, stringYpos, 0xFFFFFF);
        this.drawGradientRect(stringXpos - 13, stringYpos, stringXpos - 5, stringYpos + 8,
                0xFFFFFFFF, 0xFFFFFFFF);
        stringYpos += 20;
        this.drawString(this.fontRenderer, "Rail Height", stringXpos, stringYpos, 0xFFFFFF);
        this.drawGradientRect(stringXpos - 13, stringYpos, stringXpos - 5, stringYpos + 8,
                RenderMarkerBlock.MarkerElement.HEIGHT.getColor() | 0xFF000000,
                RenderMarkerBlock.MarkerElement.HEIGHT.getColor() | 0xFF000000);
        stringYpos += 20;
        this.drawString(this.fontRenderer, "Horizontal Length", stringXpos, stringYpos, 0xFFFFFF);
        this.drawGradientRect(stringXpos - 13, stringYpos, stringXpos - 5, stringYpos + 8,
                RenderMarkerBlock.MarkerElement.HORIZONTIAL.getColor() | 0xFF000000,
                RenderMarkerBlock.MarkerElement.HORIZONTIAL.getColor() | 0xFF000000);
        stringYpos += 20;
        this.drawString(this.fontRenderer, "Anchor Yaw", stringXpos, stringYpos, 0xFFFFFF);
        this.drawGradientRect(stringXpos - 13, stringYpos, stringXpos - 5, stringYpos + 8,
                RenderMarkerBlock.MarkerElement.HORIZONTIAL.getColor() | 0xFF000000,
                RenderMarkerBlock.MarkerElement.HORIZONTIAL.getColor() | 0xFF000000);
        stringYpos += 20;
        this.drawString(this.fontRenderer, "Vertical Length", stringXpos, stringYpos, 0xFFFFFF);
        this.drawGradientRect(stringXpos - 13, stringYpos, stringXpos - 5, stringYpos + 8,
                RenderMarkerBlock.MarkerElement.VERTICAL.getColor() | 0xFF000000,
                RenderMarkerBlock.MarkerElement.VERTICAL.getColor() | 0xFF000000);
        stringYpos += 20;
        this.drawString(this.fontRenderer, "Anchor Pitch", stringXpos, stringYpos, 0xFFFFFF);
        this.drawGradientRect(stringXpos - 13, stringYpos, stringXpos - 5, stringYpos + 8,
                RenderMarkerBlock.MarkerElement.VERTICAL.getColor() | 0xFF000000,
                RenderMarkerBlock.MarkerElement.VERTICAL.getColor() | 0xFF000000);
        stringYpos += 20;
        this.drawString(this.fontRenderer, "Cant Center", stringXpos, stringYpos, 0xFFFFFF);
        this.drawGradientRect(stringXpos - 13, stringYpos, stringXpos - 5, stringYpos + 8,
                RenderMarkerBlock.MarkerElement.CANT_CENTER.getColor() | 0xFF000000,
                RenderMarkerBlock.MarkerElement.CANT_CENTER.getColor() | 0xFF000000);
        stringYpos += 20;
        this.drawString(this.fontRenderer, "Cant Edge", stringXpos, stringYpos, 0xFFFFFF);
        this.drawGradientRect(stringXpos - 13, stringYpos, stringXpos - 5, stringYpos + 8,
                RenderMarkerBlock.MarkerElement.CANT_EDGE.getColor() | 0xFF000000,
                RenderMarkerBlock.MarkerElement.CANT_EDGE.getColor() | 0xFF000000);
        stringYpos += 20;
        this.drawString(this.fontRenderer, "Cant Random", stringXpos, stringYpos, 0xFFFFFF);
        this.drawGradientRect(stringXpos - 13, stringYpos, stringXpos - 5, stringYpos + 8,
                RenderMarkerBlock.MarkerElement.CANT_CENTER.getColor() | 0xFF000000,
                RenderMarkerBlock.MarkerElement.CANT_CENTER.getColor() | 0xFF000000);
    }

    @Override
    protected void actionPerformed(GuiButton button)
    {
        switch (button.id)
        {
        case BUTTON_OK:
            this.sendPacket();
            this.mc.setIngameFocus();
            break;
        case BUTTON_CANCEL:
            this.mc.setIngameFocus();
            break;
        default:
            break;
        }
        try
        {
            super.actionPerformed(button);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
        super.keyTyped(typedChar, keyCode);
        if (Keyboard.getEventKey() == Keyboard.KEY_ESCAPE)
        {
            this.sendPacket();
        }
        else
        {
//            this.updateValues();
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void handleMouseInput() throws IOException
    {
        super.handleMouseInput();
        for (GuiTextFieldCustom field : this.textFields)
        {
            if (field instanceof GuiTextFieldAdvanced && ((GuiTextFieldAdvanced) field).isMouseInside()
                && ((GuiTextFieldAdvanced) field).isEnabled() && field.getVisible())
            {
                ((GuiTextFieldAdvanced) field).handleMouseInput();
            }
        }
        if (this.hasValueUpdated)
        {
            this.updateValues();
            this.hasValueUpdated = false;
        }
    }

    @Override
    public void handleKeyboardInput() throws IOException
    {
        super.handleKeyboardInput();
        for (GuiTextFieldCustom field : this.textFields)
        {
            if (field instanceof GuiTextFieldAdvanced && field.getVisible() && field.isFocused()
                    && ((GuiTextFieldAdvanced) field).isEnabled())
            {
                ((GuiTextFieldAdvanced) field).handleKeyboardInput();
            }
        }
        if (this.hasValueUpdated)
        {
            this.updateValues();
            this.hasValueUpdated = false;
        }
    }

    @Override
    public boolean doesGuiPauseGame()
    {
        return false;
    }

    private void sendPacket()
    {
        this.updateValues();
        RTMMetro.NETWORK_WRAPPER.sendToServer(new PacketMarkerClient(marker));
    }

    private void updateValues()
    {
        this.marker.groupNumber = GUIHelper.getFieldValue(this.fieldGroup, this.marker.groupNumber);
        this.currentRP.height = GUIHelper.getFieldValue(this.fieldRailHeight, this.currentRP.height);
        this.currentRP.anchorLengthHorizontal = GUIHelper.getFieldValue(this.fieldAnchorLengthHorizontal,
                this.currentRP.anchorLengthHorizontal);
        this.currentRP.anchorLengthVertical = GUIHelper.getFieldValue(this.fieldAnchorLengthVertical,
                this.currentRP.anchorLengthVertical);
        this.currentRP.anchorYaw = GUIHelper.getFieldValue(this.fieldAnchorYaw, this.currentRP.anchorYaw);
        this.currentRP.anchorPitch = GUIHelper.getFieldValue(this.fieldAnchorPitch, this.currentRP.anchorPitch);
        this.currentRP.cantCenter = GUIHelper.getFieldValue(this.fieldCantCenter, this.currentRP.cantCenter);
        this.currentRP.cantEdge = GUIHelper.getFieldValue(this.fieldCantEdge, this.currentRP.cantEdge);
        this.currentRP.cantRandom = GUIHelper.getFieldValue(this.fieldCantRandom, this.currentRP.cantRandom);
    }
}
