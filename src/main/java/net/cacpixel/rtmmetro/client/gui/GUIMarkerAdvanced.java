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
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.client.config.GuiUnicodeGlyphButton;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

import java.io.IOException;

@SideOnly(Side.CLIENT)
public class GUIMarkerAdvanced extends GuiScreenAdvanced
{
    public final TileEntityMarkerAdvanced marker;
    private final TileEntityMarkerAdvanced.MarkerCriticalValues undoValues;
    private RailPosition currentRP;
    private GuiTextFieldAdvanced fieldMarkerName;
    private GuiTextFieldAdvancedInt fieldGroup;
    private GuiTextFieldAdvancedInt fieldRailHeight;
    private GuiTextFieldAdvancedFloat fieldAnchorLengthHorizontal;
    private GuiTextFieldAdvancedFloat fieldAnchorYaw;
    private GuiTextFieldAdvancedFloat fieldAnchorLengthVertical;
    private GuiTextFieldAdvancedFloat fieldAnchorPitch;
    private GuiTextFieldAdvancedFloat fieldCantEdge;
    private GuiTextFieldAdvancedFloat fieldCantCenter;
    private GuiTextFieldAdvancedFloat fieldCantRandom;
    private GuiButton buttonOK;
    private GuiButton buttonCancel;
    private GuiUnicodeGlyphButton buttonResetHeight;
    private GuiUnicodeGlyphButton buttonResetLengthH;
    private GuiUnicodeGlyphButton buttonResetAnchorYaw;
    private GuiUnicodeGlyphButton buttonResetLengthV;
    private GuiUnicodeGlyphButton buttonResetAnchorPitch;
    private GuiUnicodeGlyphButton buttonResetCantEdge;
    private GuiUnicodeGlyphButton buttonResetCantCenter;
    private GuiUnicodeGlyphButton buttonResetCantRandom;

    public GUIMarkerAdvanced(TileEntityMarkerAdvanced marker)
    {
        super();
        this.marker = marker;
        this.undoValues = new TileEntityMarkerAdvanced.MarkerCriticalValues(marker).clone();
        this.currentRP = this.marker.getMarkerRP();
        this.pLastScreen = null;
    }

    public GUIMarkerAdvanced(TileEntityMarkerAdvanced marker, GuiScreen pLastScreen)
    {
        this(marker);
        this.pLastScreen = pLastScreen;
    }

    @Override
    public void initGui()
    {
        int fieldWidth = 75;
        int fieldHeight = 14;
        int fieldXpos = 120;
        int fieldYpos = 50;

        super.initGui();
        int hw = this.width / 2;
        //groupId
        this.fieldGroup = this.setTextField(fieldXpos, fieldYpos, fieldWidth, fieldHeight, this.marker.getGroupId(), 1,
                1000, false);
        fieldYpos += 18;
        //name
        this.fieldMarkerName = this.setTextField(fieldXpos, fieldYpos, fieldWidth, fieldHeight, this.marker.getName());
        fieldYpos += 18;
        //rail height
        this.fieldRailHeight = this.setTextField(fieldXpos, fieldYpos, fieldWidth, fieldHeight, this.currentRP.height, 0, 15, false);
        this.buttonResetHeight = this.addUnicodeGlyphButton(fieldXpos + fieldWidth + 2, fieldYpos - 2, fieldHeight + 4, fieldHeight + 4,
                GuiUtils.UNDO_CHAR,
                2.0F);
        fieldYpos += 18;
        //anchor length horizontal
        this.fieldAnchorLengthHorizontal = this.setTextField(fieldXpos, fieldYpos, fieldWidth, fieldHeight,
                this.currentRP.anchorLengthHorizontal, 0.0f, (float) ModConfig.railGeneratingDistance, false);
        this.buttonResetLengthH = this.addUnicodeGlyphButton(fieldXpos + fieldWidth + 2, fieldYpos - 2, fieldHeight + 4, fieldHeight + 4,
                GuiUtils.UNDO_CHAR,
                2.0F);
        fieldYpos += 18;
        //anchor yaw
        this.fieldAnchorYaw = this.setTextField(fieldXpos, fieldYpos, fieldWidth, fieldHeight, this.currentRP.anchorYaw, -180.0f, 180.0f,
                true);
        this.buttonResetAnchorYaw = this.addUnicodeGlyphButton(fieldXpos + fieldWidth + 2, fieldYpos - 2, fieldHeight + 4, fieldHeight + 4,
                GuiUtils.UNDO_CHAR,
                2.0F);
        fieldYpos += 18;
        //anchor length vertical
        this.fieldAnchorLengthVertical = this.setTextField(fieldXpos, fieldYpos, fieldWidth, fieldHeight,
                this.currentRP.anchorLengthVertical, 0.0f, (float) ModConfig.railGeneratingDistance, false);
        this.buttonResetLengthV = this.addUnicodeGlyphButton(fieldXpos + fieldWidth + 2, fieldYpos - 2, fieldHeight + 4, fieldHeight + 4,
                GuiUtils.UNDO_CHAR,
                2.0F);
        fieldYpos += 18;
        //anchor pitch
        this.fieldAnchorPitch = this.setTextField(fieldXpos, fieldYpos, fieldWidth, fieldHeight, this.currentRP.anchorPitch, -90.0f, 90.0f,
                false);
        this.buttonResetAnchorPitch = this.addUnicodeGlyphButton(fieldXpos + fieldWidth + 2, fieldYpos - 2, fieldHeight + 4,
                fieldHeight + 4, GuiUtils.UNDO_CHAR,
                2.0F);
        fieldYpos += 18;
        //cant edge
        this.fieldCantEdge = this.setTextField(fieldXpos, fieldYpos, fieldWidth, fieldHeight, this.currentRP.cantEdge, -90.0f, 90.0f,
                false);
        this.buttonResetCantEdge = this.addUnicodeGlyphButton(fieldXpos + fieldWidth + 2, fieldYpos - 2, fieldHeight + 4, fieldHeight + 4,
                GuiUtils.UNDO_CHAR,
                2.0F);
        fieldYpos += 18;
        //cant center
        this.fieldCantCenter = this.setTextField(fieldXpos, fieldYpos, fieldWidth, fieldHeight, this.currentRP.cantCenter, -90.0f, 90.0f,
                false);
        this.buttonResetCantCenter = this.addUnicodeGlyphButton(fieldXpos + fieldWidth + 2, fieldYpos - 2, fieldHeight + 4, fieldHeight + 4,
                GuiUtils.UNDO_CHAR,
                2.0F);
        fieldYpos += 18;
        //cant random
        this.fieldCantRandom = this.setTextField(fieldXpos, fieldYpos, fieldWidth, fieldHeight, this.currentRP.cantRandom, 0.0f, 100.0f,
                false);
        this.buttonResetCantRandom = this.addUnicodeGlyphButton(fieldXpos + fieldWidth + 2, fieldYpos - 2, fieldHeight + 4, fieldHeight + 4,
                GuiUtils.UNDO_CHAR,
                2.0F);
        fieldYpos += 18;

        //ok
        this.buttonOK = this.addButton(hw - 80 + 90, this.height - 30, 160, 20, I18n.format("gui.done"));
        //cancel
        this.buttonCancel = this.addButton(hw - 80 - 90, this.height - 30, 160, 20, I18n.format("gui.cancel"));
        if (this.marker.getBlockType() == RTMMetroBlock.MARKER_ADVANCED_SWITCH)
        {
            this.fieldAnchorPitch.setEnabled(false);
            this.fieldAnchorLengthVertical.setEnabled(false);
            this.fieldCantCenter.setEnabled(false);
            this.fieldCantEdge.setEnabled(false);
        }
        else if (!this.marker.isCoreMarker())
        {
            this.fieldCantCenter.setEnabled(false);
            this.fieldCantRandom.setEnabled(false);
        }
    }

    @Override
    public void drawScreen(int par1, int par2, float par3)
    {
        int stringXpos = 10;
        int stringYpos = 8;

        // 这么操作其实不好，但是目前只能想到这个办法，就先这样吧
        if (!(marker.getWorld().getBlockState(new BlockPos(marker.getX(), marker.getY(), marker.getZ())).getBlock() instanceof BlockMarker))
        {
            this.mc.displayGuiScreen(null);
        }

        this.drawGradientRect(0, 0, this.width, this.height, 0xC0101010, 0xD0101010);
        super.drawScreen(par1, par2, par3);

        //title line
        this.drawString(this.fontRenderer,
                String.format(TextFormatting.BOLD + "Editing values of rail marker" + TextFormatting.RESET + " \"%s\" (%d, %d, %d)",
                        this.marker.getName(), this.marker.getX(), this.marker.getY(), this.marker.getZ()), stringXpos, stringYpos, 0xFFFFFF);
        stringYpos += 12;
        this.drawString(this.fontRenderer,
                TextFormatting.GRAY + TextFormatting.ITALIC.toString() + "Tips: You can use arrow keys or mouse wheel to adjust values.",
                stringXpos, stringYpos, 0xFFFFFF);
        stringYpos += 12;
        this.drawString(this.fontRenderer,
                TextFormatting.GRAY + TextFormatting.ITALIC.toString() + "LShift increases step(x10), LAlt decreases step(x0.1).",
                stringXpos, stringYpos, 0xFFFFFF);
        stringYpos += 20;
        stringXpos += 15;
        //groupId
        this.drawString(this.fontRenderer, "Marker Group", stringXpos, stringYpos, 0xFFFFFF);
        this.drawGradientRect(stringXpos - 13, stringYpos, stringXpos - 5, stringYpos + 8, 0xFFFFFFFF, 0xFFFFFFFF);
        stringYpos += 18;
        //name
        this.drawString(this.fontRenderer, "Marker Name", stringXpos, stringYpos, 0xFFFFFF);
        this.drawGradientRect(stringXpos - 13, stringYpos, stringXpos - 5, stringYpos + 8, 0xFFFFFFFF, 0xFFFFFFFF);
        stringYpos += 18;
        //rail height
        this.drawString(this.fontRenderer, "Rail Height", stringXpos, stringYpos, 0xFFFFFF);
        this.drawGradientRect(stringXpos - 13, stringYpos, stringXpos - 5, stringYpos + 8,
                RenderMarkerBlock.MarkerElement.HEIGHT.getColor() | 0xFF000000,
                RenderMarkerBlock.MarkerElement.HEIGHT.getColor() | 0xFF000000);
        stringYpos += 18;
        //anchor length horizontal
        this.drawString(this.fontRenderer, "Horizontal Length", stringXpos, stringYpos, 0xFFFFFF);
        this.drawGradientRect(stringXpos - 13, stringYpos, stringXpos - 5, stringYpos + 8,
                RenderMarkerBlock.MarkerElement.HORIZONTIAL.getColor() | 0xFF000000,
                RenderMarkerBlock.MarkerElement.HORIZONTIAL.getColor() | 0xFF000000);
        stringYpos += 18;
        //anchor yaw
        this.drawString(this.fontRenderer, "Anchor Yaw", stringXpos, stringYpos, 0xFFFFFF);
        this.drawGradientRect(stringXpos - 13, stringYpos, stringXpos - 5, stringYpos + 8,
                RenderMarkerBlock.MarkerElement.HORIZONTIAL.getColor() | 0xFF000000,
                RenderMarkerBlock.MarkerElement.HORIZONTIAL.getColor() | 0xFF000000);
        stringYpos += 18;
        //anchor length vertical
        this.drawString(this.fontRenderer, "Vertical Length", stringXpos, stringYpos, 0xFFFFFF);
        this.drawGradientRect(stringXpos - 13, stringYpos, stringXpos - 5, stringYpos + 8,
                RenderMarkerBlock.MarkerElement.VERTICAL.getColor() | 0xFF000000,
                RenderMarkerBlock.MarkerElement.VERTICAL.getColor() | 0xFF000000);
        stringYpos += 18;
        //anchor pitch
        this.drawString(this.fontRenderer, "Anchor Pitch", stringXpos, stringYpos, 0xFFFFFF);
        this.drawGradientRect(stringXpos - 13, stringYpos, stringXpos - 5, stringYpos + 8,
                RenderMarkerBlock.MarkerElement.VERTICAL.getColor() | 0xFF000000,
                RenderMarkerBlock.MarkerElement.VERTICAL.getColor() | 0xFF000000);
        stringYpos += 18;
        //cant edge
        this.drawString(this.fontRenderer, "Cant Edge", stringXpos, stringYpos, 0xFFFFFF);
        this.drawGradientRect(stringXpos - 13, stringYpos, stringXpos - 5, stringYpos + 8,
                RenderMarkerBlock.MarkerElement.CANT_EDGE.getColor() | 0xFF000000,
                RenderMarkerBlock.MarkerElement.CANT_EDGE.getColor() | 0xFF000000);
        stringYpos += 18;
        //cant center
        this.drawString(this.fontRenderer, "Cant Center", stringXpos, stringYpos, 0xFFFFFF);
        this.drawGradientRect(stringXpos - 13, stringYpos, stringXpos - 5, stringYpos + 8,
                RenderMarkerBlock.MarkerElement.CANT_CENTER.getColor() | 0xFF000000,
                RenderMarkerBlock.MarkerElement.CANT_CENTER.getColor() | 0xFF000000);
        stringYpos += 18;
        //cant random
        this.drawString(this.fontRenderer, "Cant Random", stringXpos, stringYpos, 0xFFFFFF);
        this.drawGradientRect(stringXpos - 13, stringYpos, stringXpos - 5, stringYpos + 8,
                RenderMarkerBlock.MarkerElement.CANT_CENTER.getColor() | 0xFF000000,
                RenderMarkerBlock.MarkerElement.CANT_CENTER.getColor() | 0xFF000000);
        stringYpos += 18;

    }

    @Override
    protected void actionPerformed(GuiButton button)
    {
        if (button.id == buttonOK.id)
        {
            this.updateValues();
            this.sendPacket();
            if (pLastScreen == null)
                this.mc.displayGuiScreen(null);
            else
                this.mc.displayGuiScreen(this.pLastScreen);
        }
        else if (button.id == buttonCancel.id)
        {
            this.restoreValues();
            this.sendPacket();
            if (pLastScreen == null)
                this.mc.displayGuiScreen(null);
            else
                this.mc.displayGuiScreen(this.pLastScreen);
        }
        else if (button.id == buttonResetHeight.id)
        {
            fieldRailHeight.fieldValue = 0;
            fieldRailHeight.checkValue();
        }
        else if (button.id == buttonResetLengthH.id)
        {

            fieldAnchorLengthHorizontal.checkValue();
        }
        else if (button.id == buttonResetAnchorYaw.id)
        {

            fieldAnchorYaw.checkValue();
        }
        else if (button.id == buttonResetLengthV.id)
        {

            fieldAnchorLengthVertical.checkValue();
        }
        else if (button.id == buttonResetAnchorPitch.id)
        {

            fieldAnchorPitch.checkValue();
        }
        else if (button.id == buttonResetCantEdge.id)
        {
            fieldCantEdge.fieldValue = 0;
            fieldCantEdge.checkValue();
        }
        else if (button.id == buttonResetCantCenter.id)
        {
            fieldCantCenter.fieldValue = 0;
            fieldCantCenter.checkValue();
        }
        else if (button.id == buttonResetCantRandom.id)
        {
            fieldCantRandom.fieldValue = 0;
            fieldCantRandom.checkValue();
        }
        else
            ;
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
            if (field instanceof GuiTextFieldAdvanced && ((GuiTextFieldAdvanced) field).isMouseInside() &&
                    ((GuiTextFieldAdvanced) field).isEnabled() && field.getVisible())
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
            if (field instanceof GuiTextFieldAdvanced && field.getVisible() && field.isFocused() &&
                    ((GuiTextFieldAdvanced) field).isEnabled())
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
        RTMMetro.NETWORK_WRAPPER.sendToServer(new PacketMarkerClient(marker));
    }

    private void updateValues()
    {
        this.marker.setGroupId(this.fieldGroup.fieldValue);
        this.marker.setName(this.fieldMarkerName.getText());
        this.currentRP.height = (byte) this.fieldRailHeight.fieldValue;
        this.currentRP.anchorLengthHorizontal = this.fieldAnchorLengthHorizontal.fieldValue;
        this.currentRP.anchorLengthVertical = this.fieldAnchorLengthVertical.fieldValue;
        this.currentRP.anchorYaw = this.fieldAnchorYaw.fieldValue;
        this.currentRP.anchorPitch = this.fieldAnchorPitch.fieldValue;
        this.currentRP.cantCenter = this.fieldCantCenter.fieldValue;
        this.currentRP.cantEdge = this.fieldCantEdge.fieldValue;
        this.currentRP.cantRandom = this.fieldCantRandom.fieldValue;
    }

    private void restoreValues()
    {
        this.marker.setGroupId(this.undoValues.groupId);
        this.marker.setName(this.undoValues.name);
        this.marker.getMarkerRP().height = this.undoValues.rps[0].height;
        this.marker.getMarkerRP().anchorLengthHorizontal = this.undoValues.rps[0].anchorLengthHorizontal;
        this.marker.getMarkerRP().anchorLengthVertical = this.undoValues.rps[0].anchorLengthVertical;
        this.marker.getMarkerRP().anchorYaw = this.undoValues.rps[0].anchorYaw;
        this.marker.getMarkerRP().anchorPitch = this.undoValues.rps[0].anchorPitch;
        this.marker.getMarkerRP().cantCenter = this.undoValues.rps[0].cantCenter;
        this.marker.getMarkerRP().cantEdge = this.undoValues.rps[0].cantEdge;
        this.marker.getMarkerRP().cantRandom = this.undoValues.rps[0].cantRandom;
    }
}

