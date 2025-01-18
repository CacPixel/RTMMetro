package net.cacpixel.rtmmetro.client.gui;

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
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.client.config.GuiUnicodeGlyphButton;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.IOException;
import java.util.Arrays;

@SideOnly(Side.CLIENT)
public class GUIMarkerAdvanced extends GuiScreenAdvanced
{
    public final TileEntityMarkerAdvanced marker;
    private final TileEntityMarkerAdvanced.MarkerCriticalValues undoValues, currentValues;
    private RailPosition currentRP;
    private GuiCalculateCant guiCalculateCant;
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
    private GuiButton buttonZeroLengthH;
    private GuiButton buttonZeroLengthV;
    private GuiButton buttonStraightLineH;
    private GuiButton buttonStraightLineV;
    private GuiButton buttonMagicNumberH;
    private GuiButton buttonMagicNumberV;
    private GuiButton buttonCopyCant;
    private GuiButton buttonCalcCantCenter;
    private GuiButton buttonCalcCantEdge;

    public GUIMarkerAdvanced(TileEntityMarkerAdvanced marker)
    {
        super();
        this.marker = marker;
        this.undoValues = new TileEntityMarkerAdvanced.MarkerCriticalValues(marker).clone();
        this.currentValues = new TileEntityMarkerAdvanced.MarkerCriticalValues(marker);
        this.currentRP = Arrays.stream(this.currentValues.rps).filter(rp -> rp == this.marker.rp).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Marker RailPosition invalid. Could not match marker rp."));
        this.parentScreen = null;
    }

    public GUIMarkerAdvanced(GuiScreen parentScreen, TileEntityMarkerAdvanced marker)
    {
        this(marker);
        this.parentScreen = parentScreen;
    }

    @Override
    public void initGui()
    {
        int fieldW = 75;
        int fieldH = 14;
        int fieldX = 120;
        int fieldY = 50;
        int buttW = 60;
        int buttH = fieldH + 4;
        int buttX = fieldX + fieldW + 2;
        int lineHeight = 18;

        super.initGui();
        int hw = this.width / 2;
        //groupId
        this.fieldGroup = this.setTextField(fieldX, fieldY, fieldW, fieldH, this.currentValues.groupId, 1,
                1000, false);
        fieldY += lineHeight;
        //name
        this.fieldMarkerName = this.setTextField(fieldX, fieldY, fieldW, fieldH, this.currentValues.name);
        fieldY += lineHeight;
        //rail height
        this.fieldRailHeight = this.setTextField(fieldX, fieldY, fieldW, fieldH, this.currentRP.height, 0, 15, false);
        this.buttonResetHeight = this.addUnicodeGlyphButton(buttX, fieldY - 2, buttH, buttH,
                GuiUtils.UNDO_CHAR,
                2.0F);
        fieldY += lineHeight;
        //anchor length horizontal
        this.fieldAnchorLengthHorizontal = this.setTextField(fieldX, fieldY, fieldW, fieldH,
                this.currentRP.anchorLengthHorizontal, 0.0f, (float) ModConfig.railGeneratingDistance, false);
        this.buttonResetLengthH = this.addUnicodeGlyphButton(buttX, fieldY - 2, buttH, buttH,
                GuiUtils.UNDO_CHAR,
                2.0F);
        fieldY += lineHeight;
        //anchor yaw
        this.fieldAnchorYaw = this.setTextField(fieldX, fieldY, fieldW, fieldH, this.currentRP.anchorYaw, -180.0f, 180.0f,
                true);
        this.buttonResetAnchorYaw = this.addUnicodeGlyphButton(buttX, fieldY - 2, buttH, buttH,
                GuiUtils.UNDO_CHAR,
                2.0F);
        fieldY += lineHeight;
        //anchor length vertical
        this.fieldAnchorLengthVertical = this.setTextField(fieldX, fieldY, fieldW, fieldH,
                this.currentRP.anchorLengthVertical, 0.0f, (float) ModConfig.railGeneratingDistance, false);
        this.buttonResetLengthV = this.addUnicodeGlyphButton(buttX, fieldY - 2, buttH, buttH,
                GuiUtils.UNDO_CHAR,
                2.0F);
        fieldY += lineHeight;
        //anchor pitch
        this.fieldAnchorPitch = this.setTextField(fieldX, fieldY, fieldW, fieldH, this.currentRP.anchorPitch, -90.0f, 90.0f,
                false);
        this.buttonResetAnchorPitch = this.addUnicodeGlyphButton(buttX, fieldY - 2, buttH,
                buttH, GuiUtils.UNDO_CHAR,
                2.0F);
        fieldY += lineHeight;
        //cant edge
        this.fieldCantEdge = this.setTextField(fieldX, fieldY, fieldW, fieldH, this.currentRP.cantEdge, -90.0f, 90.0f,
                false);
        this.buttonResetCantEdge = this.addUnicodeGlyphButton(buttX, fieldY - 2, buttH, buttH,
                GuiUtils.UNDO_CHAR,
                2.0F);
        this.buttonCalcCantEdge = this.addButton(buttX + buttH, fieldY - 2, buttW, buttH, "Calculate");
        fieldY += lineHeight;
        //cant center
        this.fieldCantCenter = this.setTextField(fieldX, fieldY, fieldW, fieldH, this.currentRP.cantCenter, -90.0f, 90.0f,
                false);
        this.buttonResetCantCenter = this.addUnicodeGlyphButton(buttX, fieldY - 2, buttH, buttH,
                GuiUtils.UNDO_CHAR,
                2.0F);
        this.buttonCalcCantCenter = this.addButton(buttX + buttH, fieldY - 2, buttW, buttH, "Calculate");
        fieldY += lineHeight;
        //cant random
        this.fieldCantRandom = this.setTextField(fieldX, fieldY, fieldW, fieldH, this.currentRP.cantRandom, 0.0f, 100.0f,
                false);
        this.buttonResetCantRandom = this.addUnicodeGlyphButton(buttX, fieldY - 2, buttH, buttH,
                GuiUtils.UNDO_CHAR,
                2.0F);
        fieldY += lineHeight;

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

        if (marker.getBlockType() != RTMMetroBlock.MARKER_ADVANCED && marker.getBlockType() != RTMMetroBlock.MARKER_ADVANCED_SWITCH)
        {
            this.mc.displayGuiScreen(null);
            return;
        }

        this.drawGradientRect(0, 0, this.width, this.height, 0xC0101010, 0xD0101010);
        super.drawScreen(par1, par2, par3);

        //title line
        this.drawString(this.fontRenderer,
                String.format(TextFormatting.BOLD + "Editing values of rail marker" + TextFormatting.RESET + " \"%s\" (%d, %d, %d)",
                        this.marker.getName(), this.marker.getX(), this.marker.getY(), this.marker.getZ()), stringXpos, stringYpos,
                0xFFFFFF);
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
            this.displayPrevScreen();
        }
        else if (button.id == buttonCancel.id)
        {
            this.restoreValues();
            this.sendPacket();
            this.displayPrevScreen();
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
        else if (button.id == buttonCalcCantEdge.id)
        {
            guiCalculateCant = new GuiCalculateCant(this, x -> currentValues.rps[0].cantEdge = (float) x);
            this.mc.displayGuiScreen(guiCalculateCant);
        }
        else if (button.id == buttonCalcCantCenter.id)
        {
            guiCalculateCant = new GuiCalculateCant(this, x -> currentValues.rps[0].cantCenter = (float) x);
            this.mc.displayGuiScreen(guiCalculateCant);
        }
        super.actionPerformed(button);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
//        this.updateValues();
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    protected void onPressingEsc()
    {
        super.onPressingEsc();
        this.sendPacket();
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
        this.currentValues.groupId = this.fieldGroup.fieldValue;
        this.currentValues.name = this.fieldMarkerName.getText();
        this.currentValues.rps[0].height = (byte) this.fieldRailHeight.fieldValue;
        this.currentValues.rps[0].anchorLengthHorizontal = this.fieldAnchorLengthHorizontal.fieldValue;
        this.currentValues.rps[0].anchorLengthVertical = this.fieldAnchorLengthVertical.fieldValue;
        this.currentValues.rps[0].anchorYaw = this.fieldAnchorYaw.fieldValue;
        this.currentValues.rps[0].anchorPitch = this.fieldAnchorPitch.fieldValue;
        this.currentValues.rps[0].cantCenter = this.fieldCantCenter.fieldValue;
        this.currentValues.rps[0].cantEdge = this.fieldCantEdge.fieldValue;
        this.currentValues.rps[0].cantRandom = this.fieldCantRandom.fieldValue;
        this.updateValues(this.currentValues);
    }

    private void updateValues(TileEntityMarkerAdvanced.MarkerCriticalValues values)
    {
        this.marker.setGroupId(values.groupId);
        this.marker.setName(values.name);
        this.marker.getMarkerRP().height = values.rps[0].height;
        this.marker.getMarkerRP().anchorLengthHorizontal = values.rps[0].anchorLengthHorizontal;
        this.marker.getMarkerRP().anchorLengthVertical = values.rps[0].anchorLengthVertical;
        this.marker.getMarkerRP().anchorYaw = values.rps[0].anchorYaw;
        this.marker.getMarkerRP().anchorPitch = values.rps[0].anchorPitch;
        this.marker.getMarkerRP().cantCenter = values.rps[0].cantCenter;
        this.marker.getMarkerRP().cantEdge = values.rps[0].cantEdge;
        this.marker.getMarkerRP().cantRandom = values.rps[0].cantRandom;
    }

    private void restoreValues()
    {
        this.updateValues(this.undoValues);
    }
}

