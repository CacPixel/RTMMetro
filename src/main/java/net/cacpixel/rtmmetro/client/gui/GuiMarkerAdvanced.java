package net.cacpixel.rtmmetro.client.gui;

import jp.ngt.ngtlib.block.BlockUtil;
import jp.ngt.ngtlib.math.NGTMath;
import jp.ngt.rtm.rail.BlockMarker;
import jp.ngt.rtm.rail.RenderMarkerBlock;
import jp.ngt.rtm.rail.util.RailPosition;
import net.cacpixel.rtmmetro.ModConfig;
import net.cacpixel.rtmmetro.RTMMetro;
import net.cacpixel.rtmmetro.RTMMetroBlock;
import net.cacpixel.rtmmetro.network.PacketMarkerClient;
import net.cacpixel.rtmmetro.rail.tileentity.TileEntityMarkerAdvanced;
import net.cacpixel.rtmmetro.rail.util.RailDrawingScheme;
import net.cacpixel.rtmmetro.rail.util.RailMapAdvanced;
import net.cacpixel.rtmmetro.util.BlockUtils;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
public class GuiMarkerAdvanced extends GuiScreenAdvanced
{
    public final TileEntityMarkerAdvanced marker;
    private final List<TileEntityMarkerAdvanced.MarkerCriticalValues> undoValues = new ArrayList<>();
    private final TileEntityMarkerAdvanced.MarkerCriticalValues currentMarkerUndoValue;
    private final List<TileEntityMarkerAdvanced.MarkerCriticalValues> currentValues = new ArrayList<>();
    private final TileEntityMarkerAdvanced.MarkerCriticalValues currentMarkerValue;
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
    private GuiButton buttonResetHeight;
    private GuiButton buttonResetLengthH;
    private GuiButton buttonResetAnchorYaw;
    private GuiButton buttonResetLengthV;
    private GuiButton buttonResetAnchorPitch;
    private GuiButton buttonResetCantEdge;
    private GuiButton buttonResetCantCenter;
    private GuiButton buttonResetCantRandom;
    private GuiButton buttonMagicNumberH;
    private GuiButton buttonMagicNumberV;
    private GuiButton buttonMagicNumberYaw;
    private GuiButton buttonStraightLineH;
    private GuiButton buttonStraightLineV;
    private GuiButton buttonCopyNeighborYaw;
    private GuiButton buttonCopyNeighborPitch;
    private GuiButton buttonCalcCantCenter;
    private GuiButton buttonCalcCantEdge;
    private GuiButton buttonFlipCantCenter;
    private GuiButton buttonFlipCantEdge;
    private GuiButton getButtonCopyNeighborCant;
    private GuiButton buttonRotateYaw;

    public GuiMarkerAdvanced(TileEntityMarkerAdvanced marker)
    {
        super();
        this.marker = marker;
        this.currentMarkerUndoValue = new TileEntityMarkerAdvanced.MarkerCriticalValues(marker).clone();
        this.currentMarkerValue = new TileEntityMarkerAdvanced.MarkerCriticalValues(marker);
        this.currentRP = marker.getMarkerRP();
        this.parentScreen = null;
    }

    public GuiMarkerAdvanced(GuiScreen parentScreen, TileEntityMarkerAdvanced marker)
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
        int buttW = 80;
        int buttH = fieldH + 4;
        int buttX = fieldX + fieldW + 2;
        int lineHeight = 18;

        super.initGui();
        int hw = this.width / 2;
        //groupId
        this.fieldGroup = this.setTextField(fieldX, fieldY, fieldW, fieldH, this.getCurrentMarkerValue().groupId, 1, 1000, false);
        fieldY += lineHeight;

        //name
        this.fieldMarkerName = this.setTextField(fieldX, fieldY, fieldW, fieldH, this.getCurrentMarkerValue().name);
        fieldY += lineHeight;

        //rail height
        this.fieldRailHeight = this.setTextField(fieldX, fieldY, fieldW, fieldH, this.currentRP.height, 0, 15, false);
        this.buttonResetHeight = this.addButton(buttX, fieldY - 2, buttH, buttH, "0", b -> {
            this.fieldRailHeight.fieldValue = 0;
            this.fieldRailHeight.checkValueAndSetText();
        });
        fieldY += lineHeight;

        //anchor length horizontal
        this.fieldAnchorLengthHorizontal = this.setTextField(fieldX, fieldY, fieldW, fieldH, this.currentRP.anchorLengthHorizontal, 0.0f,
                (float) ModConfig.railGeneratingDistance, false);
        this.buttonResetLengthH = this.addButton(buttX, fieldY - 2, buttH, buttH, "0", b -> {
            fieldAnchorLengthHorizontal.fieldValue = 0;
            fieldAnchorLengthHorizontal.checkValueAndSetText();
        });
        this.buttonStraightLineH = this.addButton(buttX + buttH, fieldY - 2, buttW, buttH, "Straight Line", b -> {
            this.currentValues.forEach(v -> v.rp.anchorLengthHorizontal = 0);
            fieldAnchorLengthHorizontal.fieldValue = 0;
            fieldAnchorLengthHorizontal.checkValueAndSetText();
        });
        this.buttonMagicNumberH = this.addButton(buttX + buttH + buttW, fieldY - 2, buttW, buttH, "Magic Number", b -> {
            this.currentMarkerValue.markerPosList.stream().filter(m -> !BlockUtils.isPosEqual(m, currentRP)).findFirst().ifPresent(pos -> {
                TileEntity te = BlockUtil.getTileEntity(marker.getWorld(), pos);
                if (te instanceof TileEntityMarkerAdvanced)
                {
                    fieldAnchorLengthHorizontal.fieldValue = RailMapAdvanced.getDefaultHorizontal(currentRP,
                            ((TileEntityMarkerAdvanced) te).getMarkerRP(),
                            RailDrawingScheme.DRAW_CIRCLE);
                    fieldAnchorLengthHorizontal.checkValueAndSetText();
                }
            });
        });
        fieldY += lineHeight;

        //anchor yaw
        this.fieldAnchorYaw = this.setTextField(fieldX, fieldY, fieldW, fieldH, this.currentRP.anchorYaw, -180.0f, 180.0f, true);
        this.buttonResetAnchorYaw = this.addButton(buttX, fieldY - 2, buttH, buttH, "0", b -> {
            fieldAnchorYaw.fieldValue = NGTMath.wrapAngle(currentRP.direction * 45.0F);
            fieldAnchorYaw.checkValueAndSetText();
        });
        this.buttonCopyNeighborYaw = this.addButton(buttX + buttH, fieldY - 2, buttW, buttH, "=Neighbor", b -> {
            RailPosition rp = TileEntityMarkerAdvanced.getNeighborRail(this.marker);
            if (rp != null)
            {
                this.fieldAnchorYaw.fieldValue = MathHelper.wrapDegrees(rp.anchorYaw + 180.0F);
                this.fieldAnchorYaw.checkValueAndSetText();
                return;
            }
            TileEntityMarkerAdvanced marker = TileEntityMarkerAdvanced.getNeighborMarker(this.marker);
            if (marker != null && marker.getMarkerRP() != null)
            {
                this.fieldAnchorYaw.fieldValue = MathHelper.wrapDegrees(marker.getMarkerRP().anchorYaw + 180.0F);
                this.fieldAnchorYaw.checkValueAndSetText();
                return;
            }
        });
        this.buttonMagicNumberYaw = this.addButton(buttX + buttH + buttW, fieldY - 2, buttW, buttH, "Magic Number", b -> {
            this.currentMarkerValue.markerPosList.stream().filter(m -> !BlockUtils.isPosEqual(m, currentRP)).findFirst().ifPresent(pos -> {
                TileEntity te = BlockUtil.getTileEntity(marker.getWorld(), pos);
                if (te instanceof TileEntityMarkerAdvanced)
                {
                    fieldAnchorYaw.fieldValue = RailMapAdvanced.getDefaultYaw(currentRP,
                            ((TileEntityMarkerAdvanced) te).getMarkerRP(),
                            RailDrawingScheme.DRAW_CIRCLE);
                    fieldAnchorYaw.checkValueAndSetText();
                }
            });
        });
        this.buttonRotateYaw = this.addUnicodeGlyphButton(buttX - fieldW - buttH - 4, fieldY - 2, buttH, buttH,
                GuiUtils.UNDO_CHAR, 2.0F, b -> {
                    this.fieldAnchorYaw.fieldValue += 45.0F;
                    this.fieldAnchorYaw.checkValueAndSetText();
                });
        fieldY += lineHeight;

        //anchor length vertical
        this.fieldAnchorLengthVertical = this.setTextField(fieldX, fieldY, fieldW, fieldH, this.currentRP.anchorLengthVertical, 0.0f,
                (float) ModConfig.railGeneratingDistance, false);
        this.buttonResetLengthV = this.addButton(buttX, fieldY - 2, buttH, buttH, "0", b -> {
            fieldAnchorLengthVertical.fieldValue = 0;
            fieldAnchorLengthVertical.checkValueAndSetText();
        });
        this.buttonStraightLineV = this.addButton(buttX + buttH, fieldY - 2, buttW, buttH, "Straight Line", b -> {
            this.currentValues.forEach(v -> v.rp.anchorLengthVertical = 0);
            fieldAnchorLengthVertical.fieldValue = 0;
            fieldAnchorLengthVertical.checkValueAndSetText();
        });
        this.buttonMagicNumberV = this.addButton(buttX + buttH + buttW, fieldY - 2, buttW, buttH, "Magic Number", b -> {
            fieldAnchorLengthVertical.fieldValue = RailMapAdvanced.getDefaultVertical(this.marker.getOriginalRailMap());
            fieldAnchorLengthVertical.checkValueAndSetText();
        });
        fieldY += lineHeight;

        //anchor pitch
        this.fieldAnchorPitch = this.setTextField(fieldX, fieldY, fieldW, fieldH, this.currentRP.anchorPitch, -90.0f, 90.0f, false);
        this.buttonResetAnchorPitch = this.addButton(buttX, fieldY - 2, buttH, buttH, "0", b -> {
            fieldAnchorPitch.fieldValue = 0.0F;
            fieldAnchorPitch.checkValueAndSetText();
        });
        this.buttonCopyNeighborPitch = this.addButton(buttX + buttH, fieldY - 2, buttW, buttH, "=Neighbor", b -> {
            RailPosition rp = TileEntityMarkerAdvanced.getNeighborRail(this.marker);
            if (rp != null)
            {
                this.fieldAnchorPitch.fieldValue = MathHelper.wrapDegrees(-rp.anchorPitch);
                this.fieldAnchorPitch.checkValueAndSetText();
                return;
            }
            TileEntityMarkerAdvanced marker = TileEntityMarkerAdvanced.getNeighborMarker(this.marker);
            if (marker != null && marker.getMarkerRP() != null)
            {
                this.fieldAnchorPitch.fieldValue = MathHelper.wrapDegrees(-marker.getMarkerRP().anchorPitch);
                this.fieldAnchorPitch.checkValueAndSetText();
                return;
            }
        });
        fieldY += lineHeight;

        //cant edge
        this.fieldCantEdge = this.setTextField(fieldX, fieldY, fieldW, fieldH, this.currentRP.cantEdge, -90.0f, 90.0f, false);
        this.buttonResetCantEdge = this.addButton(buttX, fieldY - 2, buttH, buttH, "0", b -> {
            fieldCantEdge.fieldValue = 0;
            fieldCantEdge.checkValueAndSetText();
        });
        this.buttonCalcCantEdge = this.addButton(buttX + buttH, fieldY - 2, buttW, buttH, "Calculate", b -> {
            guiCalculateCant = new GuiCalculateCant(this, x -> getCurrentMarkerValue().rp.cantEdge = (float) x);
            this.mc.displayGuiScreen(guiCalculateCant);
        });
        this.getButtonCopyNeighborCant = this.addButton(buttX + buttH + buttW, fieldY - 2, buttW, buttH, "=Neighbor", b -> {
            RailPosition rp = TileEntityMarkerAdvanced.getNeighborRail(this.marker);
            if (rp != null)
            {
                this.fieldCantEdge.fieldValue = MathHelper.wrapDegrees(-rp.cantEdge);
                this.fieldCantEdge.checkValueAndSetText();
                return;
            }
            TileEntityMarkerAdvanced marker = TileEntityMarkerAdvanced.getNeighborMarker(this.marker);
            if (marker != null && marker.getMarkerRP() != null)
            {
                this.fieldCantEdge.fieldValue = MathHelper.wrapDegrees(-marker.getMarkerRP().cantEdge);
                this.fieldCantEdge.checkValueAndSetText();
                return;
            }
        });
        this.buttonFlipCantEdge = this.addButton(buttX - fieldW - buttH - 4, fieldY - 2, buttH, buttH, "-", b -> {
            this.fieldCantEdge.fieldValue = -this.fieldCantEdge.fieldValue;
            this.fieldCantEdge.checkValueAndSetText();
        });
        fieldY += lineHeight;

        //cant center
        this.fieldCantCenter = this.setTextField(fieldX, fieldY, fieldW, fieldH, this.currentRP.cantCenter, -90.0f, 90.0f, false);
        this.buttonResetCantCenter = this.addButton(buttX, fieldY - 2, buttH, buttH, "0", b -> {
            fieldCantCenter.fieldValue = 0;
            fieldCantCenter.checkValueAndSetText();
        });
        this.buttonCalcCantCenter = this.addButton(buttX + buttH, fieldY - 2, buttW, buttH, "Calculate", b -> {
            guiCalculateCant = new GuiCalculateCant(this, x -> getCurrentMarkerValue().rp.cantCenter = (float) x);
            this.mc.displayGuiScreen(guiCalculateCant);
        });
        this.buttonFlipCantCenter = this.addButton(buttX - fieldW - buttH - 4, fieldY - 2, buttH, buttH, "-", b -> {
            this.fieldCantCenter.fieldValue = -this.fieldCantCenter.fieldValue;
            this.fieldCantCenter.checkValueAndSetText();
        });
        fieldY += lineHeight;

        //cant random
        this.fieldCantRandom = this.setTextField(fieldX, fieldY, fieldW, fieldH, this.currentRP.cantRandom, 0.0f, 100.0f, false);
        this.buttonResetCantRandom = this.addButton(buttX, fieldY - 2, buttH, buttH, "0", b -> {
            fieldCantRandom.fieldValue = 0;
            fieldCantRandom.checkValueAndSetText();
        });
        fieldY += lineHeight;

        //ok
        this.buttonOK = this.addButton(hw - 80 + 90, this.height - 30, 160, 20, I18n.format("gui.done"), b -> {
            this.updateFromFields();
            this.sendPacket();
            this.displayPrevScreen();
        });

        //cancel
        this.buttonCancel = this.addButton(hw - 80 - 90, this.height - 30, 160, 20, I18n.format("gui.cancel"), b -> {
            this.restoreValues();
            this.sendPacket();
            this.displayPrevScreen();
        });

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
    public void updateScreen()
    {
        super.updateScreen();
        boolean isSwitch = this.marker.getBlockType() == RTMMetroBlock.MARKER_ADVANCED_SWITCH;
        boolean isCore = this.marker.isCoreMarker();
        this.fieldAnchorPitch.setEnabled(!isSwitch);
        this.fieldAnchorLengthVertical.setEnabled(!isSwitch);
        this.fieldCantCenter.setEnabled(!isSwitch);
        this.fieldCantEdge.setEnabled(!isSwitch);
        this.fieldCantCenter.setEnabled(isCore);
        this.fieldCantRandom.setEnabled(isCore);
        if (!(marker.getWorld().getBlockState(new BlockPos(marker.getX(), marker.getY(), marker.getZ())).getBlock() instanceof BlockMarker))
        {
            this.displayPrevScreen();
            return;
        }
        this.currentValues.removeIf(v -> this.getCurrentMarkerValue().markerPosList.stream().noneMatch(markerPos ->
                BlockUtils.getMarkerFromPos(marker.getWorld(), markerPos) == BlockUtils.getMarkerFromPos(marker.getWorld(), v.rp))
        );
        this.undoValues.removeIf(v -> this.getCurrentMarkerUndoValue().markerPosList.stream().noneMatch(markerPos ->
                BlockUtils.getMarkerFromPos(marker.getWorld(), markerPos) == BlockUtils.getMarkerFromPos(marker.getWorld(), v.rp))
        );
        this.getCurrentMarkerValue().markerPosList.stream().filter(p -> !BlockUtils.isPosEqual(p, this.currentRP)).forEach(pos -> {
            TileEntity te = BlockUtil.getTileEntity(this.marker.getWorld(), pos);
            if (te instanceof TileEntityMarkerAdvanced && this.currentValues.stream().noneMatch(x ->
                    BlockUtils.getMarkerFromPos(marker.getWorld(), x.rp) == BlockUtils.getMarkerFromPos(marker.getWorld(), pos)))
                this.currentValues.add(new TileEntityMarkerAdvanced.MarkerCriticalValues((TileEntityMarkerAdvanced) te));
        });
        this.getCurrentMarkerUndoValue().markerPosList.stream().filter(p -> !BlockUtils.isPosEqual(p, this.currentRP)).forEach(pos -> {
            TileEntity te = BlockUtil.getTileEntity(this.marker.getWorld(), pos);
            if (te instanceof TileEntityMarkerAdvanced && this.undoValues.stream().noneMatch(x ->
                    BlockUtils.getMarkerFromPos(marker.getWorld(), x.rp) == BlockUtils.getMarkerFromPos(marker.getWorld(), pos)))
                this.undoValues.add(new TileEntityMarkerAdvanced.MarkerCriticalValues((TileEntityMarkerAdvanced) te).clone());
        });
    }

    @Override
    public void onGuiClosed()
    {
        super.onGuiClosed();
    }

    @Override
    public void drawScreen(int par1, int par2, float par3)
    {
        int stringXpos = 10;
        int stringYpos = 8;

        this.drawGradientRect(0, 0, this.width, this.height, 0xC0101010, 0xD0101010);
        super.drawScreen(par1, par2, par3);

        //title line
        this.drawString(this.fontRenderer,
                String.format(TextFormatting.BOLD + "Editing rail marker" + TextFormatting.RESET + " \"%s\" (%d, %d, %d)",
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
        super.actionPerformed(button);
        if (this.hasValueUpdated)
        {
            this.updateFromFields();
            this.hasValueUpdated = false;
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    protected void onPressingEsc()
    {
        super.onPressingEsc();
        this.restoreValues();
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
            this.updateFromFields();
            this.hasValueUpdated = false;
        }
    }

    @Override
    public void handleKeyboardInput() throws IOException
    {
        super.handleKeyboardInput();
        if (this.hasValueUpdated)
        {
            this.updateFromFields();
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
        List<TileEntityMarkerAdvanced.MarkerCriticalValues> list = new ArrayList<>();
        list.add(this.currentMarkerValue);
        list.addAll(this.currentValues);
        list.forEach(v -> {
            TileEntityMarkerAdvanced marker = BlockUtils.getMarkerFromPos(this.marker.getWorld(), v.rp);
            if (marker != null)
                RTMMetro.NETWORK_WRAPPER.sendToServer(new PacketMarkerClient(marker));
        });
    }

    private void updateFromFields()
    {
        this.getCurrentMarkerValue().groupId = this.fieldGroup.fieldValue;
        this.getCurrentMarkerValue().name = this.fieldMarkerName.getText();
        this.getCurrentMarkerValue().rp.height = (byte) this.fieldRailHeight.fieldValue;
        this.getCurrentMarkerValue().rp.anchorLengthHorizontal = this.fieldAnchorLengthHorizontal.fieldValue;
        this.getCurrentMarkerValue().rp.anchorLengthVertical = this.fieldAnchorLengthVertical.fieldValue;
        this.getCurrentMarkerValue().rp.anchorYaw = this.fieldAnchorYaw.fieldValue;
        this.getCurrentMarkerValue().rp.anchorPitch = this.fieldAnchorPitch.fieldValue;
        this.getCurrentMarkerValue().rp.cantCenter = this.fieldCantCenter.fieldValue;
        this.getCurrentMarkerValue().rp.cantEdge = this.fieldCantEdge.fieldValue;
        this.getCurrentMarkerValue().rp.cantRandom = this.fieldCantRandom.fieldValue;
        List<TileEntityMarkerAdvanced.MarkerCriticalValues> list = new ArrayList<>();
        list.add(this.currentMarkerValue);
        list.addAll(this.currentValues);
        this.updateValues(list);
    }

    private void updateValues(List<TileEntityMarkerAdvanced.MarkerCriticalValues> values)
    {
        values.forEach(v -> {
            TileEntityMarkerAdvanced marker = BlockUtils.getMarkerFromPos(this.marker.getWorld(), v.rp);
            if (marker != null)
            {
                marker.setGroupId(v.groupId);
                marker.setName(v.name);
                marker.getMarkerRP().height = v.rp.height;
                marker.getMarkerRP().anchorLengthHorizontal = v.rp.anchorLengthHorizontal;
                marker.getMarkerRP().anchorLengthVertical = v.rp.anchorLengthVertical;
                marker.getMarkerRP().anchorYaw = v.rp.anchorYaw;
                marker.getMarkerRP().anchorPitch = v.rp.anchorPitch;
                marker.getMarkerRP().cantCenter = v.rp.cantCenter;
                marker.getMarkerRP().cantEdge = v.rp.cantEdge;
                marker.getMarkerRP().cantRandom = v.rp.cantRandom;
            }
        });
    }

    private void restoreValues()
    {
        List<TileEntityMarkerAdvanced.MarkerCriticalValues> list = new ArrayList<>();
        list.add(this.currentMarkerUndoValue);
        list.addAll(this.undoValues);
        this.updateValues(list);
    }

    private TileEntityMarkerAdvanced.MarkerCriticalValues getCurrentMarkerValue()
    {
        return this.currentMarkerValue;
    }

    private TileEntityMarkerAdvanced.MarkerCriticalValues getCurrentMarkerUndoValue()
    {
        return this.currentMarkerUndoValue;
    }
}