package net.cacpixel.rtmmetro.client.gui;

import jp.ngt.rtm.rail.util.RailPosition;
import net.cacpixel.rtmmetro.rail.tileentity.TileEntityMarkerAdvanced;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
public class GUIMarkerAdvanced extends GuiScreenAdvanced
{
    public final TileEntityMarkerAdvanced marker;
    private RailPosition currentRP;
    private GuiTextFieldAdvanced fieldGroup;
    private GuiTextFieldAdvanced fieldAnchorLengthHorizontal;
    private GuiTextFieldAdvanced fieldAnchorPitch;
    private GuiTextFieldAdvanced fieldAnchorLengthVertical;
    private GuiTextFieldAdvanced fieldAnchorYaw;
    private GuiTextFieldAdvanced fieldCantCenter;
    private GuiTextFieldAdvanced fieldCantEdge;
    private GuiTextFieldAdvanced fieldCantRandom;
    private List<GuiTextFieldAdvanced> textFields = new ArrayList<>();
    private static final int BUTTON_OK = 0;
    private static final int BUTTON_CANCEL = 1;

    public GUIMarkerAdvanced(TileEntityMarkerAdvanced marker)
    {
        this.marker = marker;
    }

    @Override
    public void initGui()
    {
        int stringXpos = this.width - 200;
        int fieldYpos = 16;
        int fieldWidth = 80;
        int fieldHeight = 16;

        super.initGui();
        this.currentRP = this.marker.getMarkerRP();

        int hw = this.width / 2;

        this.buttonList.clear();
        this.buttonList.add(new GuiButton(BUTTON_OK, hw - 75, this.height - 28, 150, 20, I18n.format("gui.done")));

        this.fieldGroup = this.setTextField(stringXpos, fieldYpos, fieldWidth, fieldHeight,
                String.valueOf(this.marker.groupNumber));
        fieldYpos += 20;
        this.fieldAnchorLengthHorizontal = this.setTextField(stringXpos, fieldYpos, fieldWidth, fieldHeight,
                String.valueOf(this.currentRP.anchorLengthHorizontal));
        fieldYpos += 20;
        this.fieldAnchorYaw = this.setTextField(stringXpos, fieldYpos, fieldWidth, fieldHeight,
                String.valueOf(this.currentRP.anchorYaw));
        fieldYpos += 20;
        this.fieldAnchorLengthVertical = this.setTextField(stringXpos, fieldYpos, fieldWidth, fieldHeight,
                String.valueOf(this.currentRP.anchorLengthVertical));
        fieldYpos += 20;
        this.fieldAnchorPitch = this.setTextField(stringXpos, fieldYpos, fieldWidth, fieldHeight,
                String.valueOf(this.currentRP.anchorPitch));
        fieldYpos += 20;
        this.fieldCantCenter = this.setTextField(stringXpos, fieldYpos, fieldWidth, fieldHeight,
                String.valueOf(this.currentRP.cantCenter));
        fieldYpos += 20;
        this.fieldCantEdge = this.setTextField(stringXpos, fieldYpos, fieldWidth, fieldHeight,
                String.valueOf(this.currentRP.cantEdge));
        fieldYpos += 20;
        this.fieldCantRandom = this.setTextField(stringXpos, fieldYpos, fieldWidth, fieldHeight,
                String.valueOf(this.currentRP.cantRandom));

        this.textFields.add(this.fieldGroup);
        this.textFields.add(this.fieldAnchorLengthHorizontal);
        this.textFields.add(this.fieldAnchorYaw);
        this.textFields.add(this.fieldAnchorLengthVertical);
        this.textFields.add(this.fieldAnchorPitch);
        this.textFields.add(this.fieldCantCenter);
        this.textFields.add(this.fieldCantEdge);
        this.textFields.add(this.fieldCantRandom);
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
        this.updateValues();
        for (GuiTextFieldAdvanced field : this.textFields)
        {
            field.handleMouseInput();
        }
    }

    @Override
    public void drawScreen(int par1, int par2, float par3)
    {
        int stringXpos = this.width - 300;
        int stringYpos = 20;

        this.drawDefaultBackground();
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
        this.marker.groupNumber = GUIHelper.getFieldValueInt(this.fieldGroup, this.marker.groupNumber);
        this.currentRP.anchorLengthHorizontal = GUIHelper.getFieldValueFloat(this.fieldAnchorLengthHorizontal,
                this.currentRP.anchorLengthHorizontal);
        this.currentRP.anchorLengthVertical = GUIHelper.getFieldValueFloat(this.fieldAnchorLengthVertical,
                this.currentRP.anchorLengthVertical);
        this.currentRP.anchorYaw = GUIHelper.getFieldValueFloat(this.fieldAnchorYaw, this.currentRP.anchorYaw);
        this.currentRP.anchorPitch = GUIHelper.getFieldValueFloat(this.fieldAnchorPitch, this.currentRP.anchorPitch);
        this.currentRP.cantCenter = GUIHelper.getFieldValueFloat(this.fieldCantCenter, this.currentRP.cantCenter);
        this.currentRP.cantEdge = GUIHelper.getFieldValueFloat(this.fieldCantEdge, this.currentRP.cantEdge);
        this.currentRP.cantRandom = GUIHelper.getFieldValueFloat(this.fieldCantRandom, this.currentRP.cantRandom);
    }
}
