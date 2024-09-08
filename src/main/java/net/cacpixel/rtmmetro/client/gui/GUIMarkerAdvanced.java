package net.cacpixel.rtmmetro.client.gui;

import jp.ngt.ngtlib.gui.GuiTextFieldCustom;
import jp.ngt.rtm.rail.util.RailPosition;
import net.cacpixel.rtmmetro.ModConfig;
import net.cacpixel.rtmmetro.rail.tileentity.TileEntityMarkerAdvanced;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.IOException;

@SideOnly(Side.CLIENT)
public class GUIMarkerAdvanced extends GuiScreenAdvanced
{
    public final TileEntityMarkerAdvanced marker;
    private RailPosition currentRP;
    private GuiTextFieldAdvanced fieldGroup;
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
        int stringXpos = this.width - fieldWidth - 10;
        int fieldYpos = 9;

        super.initGui();
        this.currentRP = this.marker.getMarkerRP();

        int hw = this.width / 2;

        this.buttonList.clear();
        this.buttonList.add(new GuiButton(BUTTON_OK, this.width - 150, this.height - 30, 120, 20, I18n.format("gui.done")));

        this.fieldGroup = this.setTextField(stringXpos, fieldYpos, fieldWidth, fieldHeight,
                String.valueOf(this.marker.groupNumber));
        fieldYpos += 20;
        this.fieldAnchorLengthHorizontal = this.setTextField(stringXpos, fieldYpos, fieldWidth, fieldHeight,
                this.currentRP.anchorLengthHorizontal, 0.0f, (float) ModConfig.railGeneratingDistance);
        fieldYpos += 20;
        this.fieldAnchorYaw = this.setTextField(stringXpos, fieldYpos, fieldWidth, fieldHeight,
                this.currentRP.anchorYaw, -180.0f, 180.0f);
        fieldYpos += 20;
        this.fieldAnchorLengthVertical = this.setTextField(stringXpos, fieldYpos, fieldWidth, fieldHeight,
                this.currentRP.anchorLengthVertical, 0.0f, (float) ModConfig.railGeneratingDistance);
        fieldYpos += 20;
        this.fieldAnchorPitch = this.setTextField(stringXpos, fieldYpos, fieldWidth, fieldHeight,
                this.currentRP.anchorPitch, -180.0f, 180.0f);
        fieldYpos += 20;
        this.fieldCantCenter = this.setTextField(stringXpos, fieldYpos, fieldWidth, fieldHeight,
                this.currentRP.cantCenter, -90.0f, 90.0f);
        fieldYpos += 20;
        this.fieldCantEdge = this.setTextField(stringXpos, fieldYpos, fieldWidth, fieldHeight,
                this.currentRP.cantEdge, -90.0f, 90.0f);
        fieldYpos += 20;
        this.fieldCantRandom = this.setTextField(stringXpos, fieldYpos, fieldWidth, fieldHeight,
                this.currentRP.cantRandom, -90.0f, 90.0f);
    }

    @Override
    public void drawScreen(int par1, int par2, float par3)
    {
        int stringXpos = this.width - 180;
        int stringYpos = 13;

//        this.drawDefaultBackground();
        this.drawGradientRect(Math.max(0, stringXpos - 10), 0, this.width, this.height, -1072689136, -804253680);

        super.drawScreen(par1, par2, par3);
        this.drawString(this.fontRenderer, "Marker Group", stringXpos, stringYpos, 0xFFFFFF);
        stringYpos += 20;
        this.drawString(this.fontRenderer, "Horizontal Length", stringXpos, stringYpos, 0xFFFFFF);
        stringYpos += 20;
        this.drawString(this.fontRenderer, "Anchor Yaw", stringXpos, stringYpos, 0xFFFFFF);
        stringYpos += 20;
        this.drawString(this.fontRenderer, "Vertical Length", stringXpos, stringYpos, 0xFFFFFF);
        stringYpos += 20;
        this.drawString(this.fontRenderer, "Anchor Pitch", stringXpos, stringYpos, 0xFFFFFF);
        stringYpos += 20;
        this.drawString(this.fontRenderer, "Cant Center", stringXpos, stringYpos, 0xFFFFFF);
        stringYpos += 20;
        this.drawString(this.fontRenderer, "Cant Edge", stringXpos, stringYpos, 0xFFFFFF);
        stringYpos += 20;
        this.drawString(this.fontRenderer, "Cant Random", stringXpos, stringYpos, 0xFFFFFF);
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
        this.updateValues();
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
            if (field instanceof GuiTextFieldAdvanced && ((GuiTextFieldAdvanced) field).isMouseInside())
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
            if (field instanceof GuiTextFieldAdvanced && field.getVisible() && field.isFocused())
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
        //TODO: send packet to server
    }

    private void updateValues()
    {
        this.marker.groupNumber = GUIHelper.getFieldValue(this.fieldGroup, this.marker.groupNumber);
        this.currentRP.anchorLengthHorizontal = GUIHelper.getFieldValue(this.fieldAnchorLengthHorizontal,
                this.currentRP.anchorLengthHorizontal);
        this.currentRP.anchorLengthVertical = GUIHelper.getFieldValue(this.fieldAnchorLengthVertical,
                this.currentRP.anchorLengthVertical);
        this.currentRP.anchorYaw = GUIHelper.getFieldValue(this.fieldAnchorYaw, this.currentRP.anchorYaw);
        this.currentRP.anchorPitch = GUIHelper.getFieldValue(this.fieldAnchorPitch, this.currentRP.anchorPitch);
        this.currentRP.cantCenter = GUIHelper.getFieldValue(this.fieldCantCenter, this.currentRP.cantCenter);
        this.currentRP.cantEdge = GUIHelper.getFieldValue(this.fieldCantEdge, this.currentRP.cantEdge);
        this.currentRP.cantRandom = GUIHelper.getFieldValue(this.fieldCantRandom, this.currentRP.cantRandom);
        /*
        this.currentRP.anchorLengthHorizontal = fieldAnchorLengthHorizontal.fieldValue;
        this.currentRP.anchorLengthVertical = fieldAnchorLengthVertical.fieldValue;
        this.currentRP.anchorYaw = fieldAnchorYaw.fieldValue;
        this.currentRP.anchorPitch = fieldAnchorPitch.fieldValue;
        this.currentRP.cantCenter = fieldCantCenter.fieldValue;
        this.currentRP.cantEdge = fieldCantEdge.fieldValue;
        this.currentRP.cantRandom = fieldCantRandom.fieldValue;
        */
    }
}
