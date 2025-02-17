package net.cacpixel.rtmmetro.client.gui.screens;

import jp.ngt.ngtlib.block.BlockUtil;
import jp.ngt.ngtlib.math.NGTMath;
import jp.ngt.rtm.rail.BlockMarker;
import jp.ngt.rtm.rail.RenderMarkerBlock;
import jp.ngt.rtm.rail.util.RailPosition;
import net.cacpixel.rtmmetro.ModConfig;
import net.cacpixel.rtmmetro.RTMMetro;
import net.cacpixel.rtmmetro.RTMMetroBlock;
import net.cacpixel.rtmmetro.client.gui.CacGuiUtils;
import net.cacpixel.rtmmetro.client.gui.GuiFullScreen;
import net.cacpixel.rtmmetro.client.gui.GuiScreenAdvanced;
import net.cacpixel.rtmmetro.client.gui.widgets.*;
import net.cacpixel.rtmmetro.network.PacketMarkerClient;
import net.cacpixel.rtmmetro.rail.tileentity.TileEntityMarkerAdvanced;
import net.cacpixel.rtmmetro.rail.util.AnchorEditStatus;
import net.cacpixel.rtmmetro.rail.util.RailDrawingScheme;
import net.cacpixel.rtmmetro.rail.util.RailMapAdvanced;
import net.cacpixel.rtmmetro.util.BlockUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.toasts.GuiToast;
import net.minecraft.client.gui.toasts.SystemToast;
import net.minecraft.client.resources.I18n;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.IntSupplier;

@SideOnly(Side.CLIENT)
public class GuiMarkerAdvanced extends GuiFullScreen
{
    public final TileEntityMarkerAdvanced marker;
    private final List<TileEntityMarkerAdvanced.MarkerCriticalValues> undoValues = new ArrayList<>();
    private final TileEntityMarkerAdvanced.MarkerCriticalValues currentMarkerUndoValue;
    private final List<TileEntityMarkerAdvanced.MarkerCriticalValues> currentValues = new ArrayList<>();
    private final TileEntityMarkerAdvanced.MarkerCriticalValues currentMarkerValue;
    private final RailPosition currentRP;
    private GuiCalculateCant guiCalculateCant;
    private GuiScroll mainScroll;

    // Field
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

    // Button
    private GuiButtonAdvanced buttonOK;
    private GuiButtonAdvanced buttonCancel;
    private GuiButtonAdvanced buttonResetHeight;
    private GuiButtonAdvanced buttonResetLengthH;
    private GuiButtonAdvanced buttonResetAnchorYaw;
    private GuiButtonAdvanced buttonResetLengthV;
    private GuiButtonAdvanced buttonResetAnchorPitch;
    private GuiButtonAdvanced buttonResetCantEdge;
    private GuiButtonAdvanced buttonResetCantCenter;
    private GuiButtonAdvanced buttonResetCantRandom;
    private GuiButtonAdvanced buttonMagicNumberH;
    private GuiButtonAdvanced buttonMagicNumberV;
    private GuiButtonAdvanced buttonMagicNumberYaw;
    private GuiButtonAdvanced buttonStraightLineH;
    private GuiButtonAdvanced buttonStraightLineV;
    private GuiButtonAdvanced buttonCopyNeighborYaw;
    private GuiButtonAdvanced buttonCopyNeighborPitch;
    private GuiButtonAdvanced buttonCalcCantCenter;
    private GuiButtonAdvanced buttonCalcCantEdge;
    private GuiButtonAdvanced buttonFlipCantCenter;
    private GuiButtonAdvanced buttonFlipCantEdge;
    private GuiButtonAdvanced buttonCopyNeighborCantEdge;
    private GuiButtonAdvanced buttonRotateYaw;
    private GuiOptionButton<AnchorEditStatus> buttonEditStatusH;
    private GuiOptionButton<AnchorEditStatus> buttonEditStatusV;
    private GuiOptionButton<RailDrawingScheme> buttonDrawingScheme;
    private GuiButtonAdvanced buttonRedraw;

    public GuiMarkerAdvanced(TileEntityMarkerAdvanced marker)
    {
        super();
        this.marker = marker;
        this.currentMarkerUndoValue = new TileEntityMarkerAdvanced.MarkerCriticalValues(marker).clone();
        this.currentMarkerValue = new TileEntityMarkerAdvanced.MarkerCriticalValues(marker);
        this.currentRP = marker.getMarkerRP();
        this.parentScreen = null;
    }

    public GuiMarkerAdvanced(GuiScreenAdvanced parentScreen, TileEntityMarkerAdvanced marker)
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
        int fieldY = 8;//50;
        int buttW = 80;
        int buttH = fieldH + 4;
        int buttX = fieldX + fieldW + 2;
        int lineHeight = 18;

        super.initGui();
        this.mainScroll = new Scroll(this, this.getNextWidgetId(), () -> 0, () -> 30, () -> this.width,
                () -> this.height - 40 - 30);
        this.add(mainScroll);

        //groupId
        this.fieldGroup = WidgetFactory.addTextField(mainScroll, fieldX, fieldY, fieldW, fieldH,
                this.currentMarkerValue.groupId, 1, 1000,
                false);
        fieldY += lineHeight;

        //name
        this.fieldMarkerName = WidgetFactory.addTextField(mainScroll, fieldX, fieldY, fieldW, fieldH,
                this.currentMarkerValue.name);
        fieldY += lineHeight;

        //rail height
        this.fieldRailHeight = WidgetFactory.addTextField(mainScroll, fieldX, fieldY, fieldW, fieldH,
                this.currentRP.height, 0, 15, false);
        this.buttonResetHeight = WidgetFactory.addButton(mainScroll, buttX, fieldY - 2, buttH, buttH, "0")
                .setListener((w) -> {
                    this.fieldRailHeight.fieldValue = 0;
                    this.fieldRailHeight.checkValueAndSetText();
                });
        fieldY += lineHeight;

        //anchor length horizontal
        this.fieldAnchorLengthHorizontal = WidgetFactory.addTextField(mainScroll, fieldX, fieldY, fieldW, fieldH,
                this.currentRP.anchorLengthHorizontal, 0.0f,
                (float) ModConfig.railGeneratingDistance, false);
        this.buttonResetLengthH = WidgetFactory.addButton(mainScroll, buttX, fieldY - 2, buttH, buttH, "0")
                .setListener((w) -> {
                    fieldAnchorLengthHorizontal.fieldValue = 0;
                    fieldAnchorLengthHorizontal.checkValueAndSetText();
                });
        this.buttonStraightLineH = WidgetFactory.addButton(mainScroll, buttX + buttH, fieldY - 2, buttW, buttH,
                        "Straight Line")
                .setListener((w) -> {
                    this.currentValues.forEach(v -> v.rp.anchorLengthHorizontal = 0);
                    fieldAnchorLengthHorizontal.fieldValue = 0;
                    fieldAnchorLengthHorizontal.checkValueAndSetText();
                });
        this.buttonMagicNumberH = WidgetFactory.addButton(mainScroll, buttX + buttH + buttW, fieldY - 2, buttW, buttH,
                        "Magic Number")
                .setListener((w) -> {
                    this.currentMarkerValue.markerPosList.stream().filter(m -> !BlockUtils.isPosEqual(m, currentRP))
                            .findFirst()
                            .ifPresent(pos -> {
                                TileEntity te = BlockUtil.getTileEntity(marker.getWorld(), pos);
                                if (te instanceof TileEntityMarkerAdvanced)
                                {
                                    fieldAnchorLengthHorizontal.fieldValue = RailMapAdvanced.getDefaultHorizontal(
                                            currentRP,
                                            ((TileEntityMarkerAdvanced) te).getMarkerRP(),
                                            this.currentMarkerValue.drawingScheme);
                                    fieldAnchorLengthHorizontal.checkValueAndSetText();
                                }
                            });
                });
        fieldY += lineHeight;

        //anchor yaw
        this.fieldAnchorYaw = WidgetFactory.addTextField(mainScroll, fieldX, fieldY, fieldW, fieldH,
                this.currentRP.anchorYaw, -180.0f,
                180.0f,
                true);
        this.buttonResetAnchorYaw = WidgetFactory.addButton(mainScroll, buttX, fieldY - 2, buttH, buttH, "0")
                .setListener((w) -> {
                    fieldAnchorYaw.fieldValue = NGTMath.wrapAngle(currentRP.direction * 45.0F);
                    fieldAnchorYaw.checkValueAndSetText();
                });
        this.buttonCopyNeighborYaw = WidgetFactory.addButton(mainScroll, buttX + buttH, fieldY - 2, buttW, buttH,
                        "=Neighbor")
                .setListener((w) -> {
                    RailPosition rp = TileEntityMarkerAdvanced.getNeighborRP(this.marker);
                    if (rp != null)
                    {
                        this.fieldAnchorYaw.fieldValue = MathHelper.wrapDegrees(rp.anchorYaw + 180.0F);
                        this.fieldAnchorYaw.checkValueAndSetText();
                    }
                });
        this.buttonMagicNumberYaw = WidgetFactory.addButton(mainScroll, buttX + buttH + buttW, fieldY - 2, buttW, buttH,
                        "Magic Number")
                .setListener((w) -> {
                    this.currentMarkerValue.markerPosList.stream().filter(m -> !BlockUtils.isPosEqual(m, currentRP))
                            .findFirst()
                            .ifPresent(pos -> {
                                TileEntity te = BlockUtil.getTileEntity(marker.getWorld(), pos);
                                if (te instanceof TileEntityMarkerAdvanced)
                                {
                                    fieldAnchorYaw.fieldValue = RailMapAdvanced.getDefaultYaw(currentRP,
                                            ((TileEntityMarkerAdvanced) te).getMarkerRP(),
                                            this.currentMarkerValue.drawingScheme);
                                    fieldAnchorYaw.checkValueAndSetText();
                                }
                            });
                });
        this.buttonRotateYaw = WidgetFactory.addUnicodeGlyphButton(mainScroll, buttX - fieldW - buttH - 4, fieldY - 2,
                buttH, buttH,
                GuiUtils.UNDO_CHAR, 2.0F).setListener((w) -> {
            this.fieldAnchorYaw.fieldValue += 45.0F;
            this.fieldAnchorYaw.checkValueAndSetText();
        });
        fieldY += lineHeight;

        //anchor length vertical
        this.fieldAnchorLengthVertical = WidgetFactory.addTextField(mainScroll, fieldX, fieldY, fieldW, fieldH,
                this.currentRP.anchorLengthVertical, 0.0f,
                (float) ModConfig.railGeneratingDistance, false);
        this.buttonResetLengthV = WidgetFactory.addButton(mainScroll, buttX, fieldY - 2, buttH, buttH, "0")
                .setListener((w) -> {
                    fieldAnchorLengthVertical.fieldValue = 0;
                    fieldAnchorLengthVertical.checkValueAndSetText();
                });
        this.buttonStraightLineV = WidgetFactory.addButton(mainScroll, buttX + buttH, fieldY - 2, buttW, buttH,
                        "Straight Line")
                .setListener((w) -> {
                    this.currentValues.forEach(v -> v.rp.anchorLengthVertical = 0);
                    fieldAnchorLengthVertical.fieldValue = 0;
                    fieldAnchorLengthVertical.checkValueAndSetText();
                });
        this.buttonMagicNumberV = WidgetFactory.addButton(mainScroll, buttX + buttH + buttW, fieldY - 2, buttW, buttH,
                        "Magic Number")
                .setListener((w) -> {
                    fieldAnchorLengthVertical.fieldValue = RailMapAdvanced.getDefaultVertical(
                            this.marker.getOriginalRailMap());
                    fieldAnchorLengthVertical.checkValueAndSetText();
                });
        fieldY += lineHeight;

        //anchor pitch
        this.fieldAnchorPitch = WidgetFactory.addTextField(mainScroll, fieldX, fieldY, fieldW, fieldH,
                this.currentRP.anchorPitch, -90.0f,
                90.0f,
                false);
        this.buttonResetAnchorPitch = WidgetFactory.addButton(mainScroll, buttX, fieldY - 2, buttH, buttH, "0")
                .setListener((w) -> {
                    fieldAnchorPitch.fieldValue = 0.0F;
                    fieldAnchorPitch.checkValueAndSetText();
                });
        this.buttonCopyNeighborPitch = WidgetFactory.addButton(mainScroll, buttX + buttH, fieldY - 2, buttW, buttH,
                        "=Neighbor")
                .setListener((w) -> {
                    RailPosition rp = TileEntityMarkerAdvanced.getNeighborRP(this.marker);
                    if (rp != null)
                    {
                        this.fieldAnchorPitch.fieldValue = MathHelper.wrapDegrees(-rp.anchorPitch);
                        this.fieldAnchorPitch.checkValueAndSetText();
                    }
                });
        fieldY += lineHeight;

        //cant edge
        this.fieldCantEdge = WidgetFactory.addTextField(mainScroll, fieldX, fieldY, fieldW, fieldH,
                this.currentRP.cantEdge, -90.0f, 90.0f,
                false);
        this.buttonResetCantEdge = WidgetFactory.addButton(mainScroll, buttX, fieldY - 2, buttH, buttH, "0")
                .setListener((w) -> {
                    fieldCantEdge.fieldValue = 0;
                    fieldCantEdge.checkValueAndSetText();
                });
        this.buttonCalcCantEdge = WidgetFactory.addButton(mainScroll, buttX + buttH, fieldY - 2, buttW, buttH,
                        "Calculate")
                .setListener((w) -> {
                    guiCalculateCant = new GuiCalculateCant(this, x -> {
                        fieldCantEdge.fieldValue = (float) x;
                        fieldCantEdge.checkValueAndSetText();
                    });
                    this.mc.displayGuiScreen(guiCalculateCant);
                });
        this.buttonCopyNeighborCantEdge = WidgetFactory.addButton(mainScroll, buttX + buttH + buttW, fieldY - 2, buttW,
                        buttH, "=Neighbor")
                .setListener((w) -> {
                    RailPosition rp = TileEntityMarkerAdvanced.getNeighborRP(this.marker);
                    if (rp != null)
                    {
                        this.fieldCantEdge.fieldValue = MathHelper.wrapDegrees(-rp.cantEdge);
                        this.fieldCantEdge.checkValueAndSetText();
                    }
                });
        this.buttonFlipCantEdge = WidgetFactory.addButton(mainScroll, buttX - fieldW - buttH - 4, fieldY - 2, buttH,
                        buttH, "-")
                .setListener((w) -> {
                    this.fieldCantEdge.fieldValue = -this.fieldCantEdge.fieldValue;
                    this.fieldCantEdge.checkValueAndSetText();
                });
        fieldY += lineHeight;

        //cant center
        this.fieldCantCenter = WidgetFactory.addTextField(mainScroll, fieldX, fieldY, fieldW, fieldH,
                this.currentRP.cantCenter, -90.0f,
                90.0f,
                false);
        this.buttonResetCantCenter = WidgetFactory.addButton(mainScroll, buttX, fieldY - 2, buttH, buttH, "0")
                .setListener((w) -> {
                    fieldCantCenter.fieldValue = 0;
                    fieldCantCenter.checkValueAndSetText();
                });
        this.buttonCalcCantCenter = WidgetFactory.addButton(mainScroll, buttX + buttH, fieldY - 2, buttW, buttH,
                        "Calculate")
                .setListener((w) -> {
                    guiCalculateCant = new GuiCalculateCant(this, x -> {
                        fieldCantCenter.fieldValue = (float) x;
                        fieldCantCenter.checkValueAndSetText();
                    });
                    this.mc.displayGuiScreen(guiCalculateCant);
                });
        this.buttonFlipCantCenter = WidgetFactory.addButton(mainScroll, buttX - fieldW - buttH - 4, fieldY - 2, buttH,
                        buttH, "-")
                .setListener((w) -> {
                    this.fieldCantCenter.fieldValue = -this.fieldCantCenter.fieldValue;
                    this.fieldCantCenter.checkValueAndSetText();
                });
        fieldY += lineHeight;

        //cant random
        this.fieldCantRandom = WidgetFactory.addTextField(mainScroll, fieldX, fieldY, fieldW, fieldH,
                this.currentRP.cantRandom, 0.0f,
                100.0f,
                false);
        this.buttonResetCantRandom = WidgetFactory.addButton(mainScroll, buttX, fieldY - 2, buttH, buttH, "0")
                .setListener((w) -> {
                    fieldCantRandom.fieldValue = 0;
                    fieldCantRandom.checkValueAndSetText();
                });
        fieldY += lineHeight + 2 + 10;

        //horizontal edit status
        this.buttonEditStatusH = WidgetFactory.addOptionButton(mainScroll, buttX - fieldW / 2, fieldY, 160, 20, "",
                AnchorEditStatus.values(),
                this.currentMarkerValue.editStatusH);
        fieldY += lineHeight + 2;
        //vertical edit status
        this.buttonEditStatusV = WidgetFactory.addOptionButton(mainScroll, buttX - fieldW / 2, fieldY, 160, 20, "",
                AnchorEditStatus.values(),
                this.currentMarkerValue.editStatusV);
        fieldY += lineHeight + 2;
        //rail drawing scheme
        this.buttonDrawingScheme = WidgetFactory.addOptionButton(mainScroll, buttX - fieldW / 2, fieldY, 160, 20, "",
                RailDrawingScheme.values(),
                this.currentMarkerValue.drawingScheme);
        this.buttonRedraw = WidgetFactory.addUnicodeGlyphButton(mainScroll, buttX - fieldW / 2 + 160, fieldY, 80, 20,
                "Redraw",
                GuiUtils.UNDO_CHAR, 2.0F).setListener((w) -> {
            this.currentMarkerValue.markerPosList.stream().filter(m -> !BlockUtils.isPosEqual(m, currentRP)).findFirst()
                    .ifPresent(pos -> {
                        TileEntityMarkerAdvanced te = BlockUtils.getMarkerFromPos(marker.getWorld(), pos);
                        if (te != null)
                        {
                            // this marker
                            fieldAnchorYaw.fieldValue = RailMapAdvanced.getDefaultYaw(currentRP, te.getMarkerRP(),
                                    this.currentMarkerValue.drawingScheme);
                            fieldAnchorYaw.checkValueAndSetText();
                            this.updateValueFromWidgets();
                            fieldAnchorLengthHorizontal.fieldValue = RailMapAdvanced.getDefaultHorizontal(currentRP,
                                    te.getMarkerRP(),
                                    this.currentMarkerValue.drawingScheme);
                            fieldAnchorLengthHorizontal.checkValueAndSetText();
                            //another marker
                            this.currentValues.stream()
                                    .filter(v -> BlockUtils.getMarkerFromPos(this.marker.getWorld(), v.rp) == te)
                                    .findFirst().ifPresent(v -> {
                                        // yaw跟着这个marker来的，再次计算也不会变，忽略，只需要确认长度。
                                        v.rp.anchorLengthHorizontal =
                                                RailMapAdvanced.getDefaultHorizontal(te.getMarkerRP(),
                                                currentRP,
                                                this.currentMarkerValue.drawingScheme);
                                    });
                        }
                    });
        });
        fieldY += lineHeight + 2;
        //ok
        this.buttonOK = WidgetFactory.addButton(this, () -> this.getHalfWidth() - 80 + 90, () -> this.height - 30,
                () -> 160, () -> 20,
                I18n.format("gui.done")).setListener((w) -> {
            this.updateValueFromWidgets();
            this.sendPacket();
            this.displayPrevScreen();
            GuiToast guitoast = Minecraft.getMinecraft().getToastGui();
            SystemToast.addOrUpdate(guitoast, SystemToast.Type.TUTORIAL_HINT,
                    new TextComponentString("Success"),
                    new TextComponentString("Parameters saved."));
        });

        //cancel
        this.buttonCancel = WidgetFactory.addButton(this, () -> this.getHalfWidth() - 80 - 90, () -> this.height - 30,
                        () -> 160, () -> 20,
                        I18n.format("gui.cancel"))
                .setListener((w) -> {
                    this.restoreValues();
                    this.sendPacket();
                    this.displayPrevScreen();
                });
        this.controlEnable();
    }

    public void controlEnable()
    {
        this.getAllWidgets().forEach(x -> x.setEnabled(true));

        boolean isSwitch = this.marker.getBlockType() == RTMMetroBlock.MARKER_ADVANCED_SWITCH ||
                this.marker.getOriginalRailMap() == null;
        boolean isCore = this.marker.isCoreMarker();

        this.fieldAnchorPitch.setEnabled(!isSwitch);
        this.buttonResetAnchorPitch.setEnabled(!isSwitch);
        this.buttonCopyNeighborPitch.setEnabled(!isSwitch);

        this.fieldAnchorLengthVertical.setEnabled(!isSwitch);
        this.buttonStraightLineV.setEnabled(!isSwitch);
        this.buttonResetLengthV.setEnabled(!isSwitch);
        this.buttonMagicNumberV.setEnabled(!isSwitch);
        this.buttonEditStatusV.setEnabled(!isSwitch);

        this.fieldCantCenter.setEnabled(!isSwitch);
        this.buttonCalcCantCenter.setEnabled(!isSwitch);
        this.buttonFlipCantCenter.setEnabled(!isSwitch);
        this.buttonResetCantCenter.setEnabled(!isSwitch);

        this.fieldCantEdge.setEnabled(!isSwitch);
        this.buttonCalcCantEdge.setEnabled(!isSwitch);
        this.buttonResetCantEdge.setEnabled(!isSwitch);
        this.buttonFlipCantEdge.setEnabled(!isSwitch);
        this.buttonCopyNeighborCantEdge.setEnabled(!isSwitch);

        this.fieldCantCenter.setEnabled(isCore && !isSwitch);
        this.buttonResetCantCenter.setEnabled(isCore && !isSwitch);
        this.buttonFlipCantCenter.setEnabled(isCore && !isSwitch);
        this.buttonCalcCantCenter.setEnabled(isCore && !isSwitch);

        this.fieldCantRandom.setEnabled(isCore);
        this.buttonResetCantRandom.setEnabled(isCore);

        if (this.currentMarkerValue.drawingScheme != RailDrawingScheme.DRAW_CIRCLE)
        {
            this.buttonRedraw.setEnabled(false);
        }
    }

    @Override
    public void updateScreen()
    {
        super.updateScreen();
        this.controlEnable();

        // 根据世界数据进行增添
        if (!(marker.getWorld().getBlockState(new BlockPos(marker.getX(), marker.getY(), marker.getZ()))
                .getBlock() instanceof BlockMarker))
        {
            this.displayPrevScreen();
            return;
        }
        this.currentValues.removeIf(v -> this.currentMarkerValue.markerPosList.stream().noneMatch(markerPos ->
                BlockUtils.getMarkerFromPos(marker.getWorld(), markerPos) ==
                        BlockUtils.getMarkerFromPos(marker.getWorld(), v.rp))
        );
        this.undoValues.removeIf(v -> this.currentMarkerUndoValue.markerPosList.stream().noneMatch(markerPos ->
                BlockUtils.getMarkerFromPos(marker.getWorld(), markerPos) ==
                        BlockUtils.getMarkerFromPos(marker.getWorld(), v.rp))
        );
        this.currentMarkerValue.markerPosList.stream().filter(p -> !BlockUtils.isPosEqual(p, this.currentRP))
                .forEach(pos -> {
                    TileEntity te = BlockUtil.getTileEntity(this.marker.getWorld(), pos);
                    if (te instanceof TileEntityMarkerAdvanced && this.currentValues.stream().noneMatch(x ->
                            BlockUtils.getMarkerFromPos(marker.getWorld(), x.rp) ==
                                    BlockUtils.getMarkerFromPos(marker.getWorld(), pos)))
                        this.currentValues.add(
                                new TileEntityMarkerAdvanced.MarkerCriticalValues((TileEntityMarkerAdvanced) te));
                });
        this.currentMarkerUndoValue.markerPosList.stream().filter(p -> !BlockUtils.isPosEqual(p, this.currentRP))
                .forEach(pos -> {
                    TileEntity te = BlockUtil.getTileEntity(this.marker.getWorld(), pos);
                    if (te instanceof TileEntityMarkerAdvanced && this.undoValues.stream().noneMatch(x ->
                            BlockUtils.getMarkerFromPos(marker.getWorld(), x.rp) ==
                                    BlockUtils.getMarkerFromPos(marker.getWorld(), pos)))
                        this.undoValues.add(new TileEntityMarkerAdvanced.MarkerCriticalValues(
                                (TileEntityMarkerAdvanced) te).clone());
                });

        if (this.hasValueUpdated)
        {
            this.updateValueFromWidgets();
            this.hasValueUpdated = false;
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        this.drawScreenBefore(mouseX, mouseY, partialTicks);
        super.drawScreen(mouseX, mouseY, partialTicks);
        GuiMarkerAdvanced pScr = this;
        int stringXpos = this.getHalfWidth();
        int stringYpos = 12;
        int fontColor = 0xE0E0E0 | pScr.getAlphaInt(0xFF);
        int fontColorGrey = 0xA0A0A0 | pScr.getAlphaInt(0xFF);

        //title line
        CacGuiUtils.drawCenteredString(pScr.fontRenderer,
                String.format(TextFormatting.BOLD + "Editing rail marker" + TextFormatting.RESET + " \"%s\" " +
                                TextFormatting.YELLOW + "(%d, %d, %d)",
                        pScr.marker.getName(), pScr.marker.getX(), pScr.marker.getY(), pScr.marker.getZ()),
                stringXpos, stringYpos, fontColor);
        stringYpos += 12;
//        pScr.drawString(pScr.fontRenderer,
//                TextFormatting.GRAY + TextFormatting.ITALIC.toString() +
//                        "Tips: You can use arrow keys or mouse wheel to adjust values.",
//                stringXpos, stringYpos, fontColorGrey);
//        stringYpos += 12;
//        pScr.drawString(pScr.fontRenderer,
//                TextFormatting.GRAY + TextFormatting.ITALIC.toString() + "LShift increases step(x10), LAlt
//                decreases step(x0.1).",
//                stringXpos, stringYpos, fontColorGrey);
        this.drawScreenAfter(mouseX, mouseY, partialTicks);
    }

    @Override
    public void onButtonAction(GuiButtonAdvanced b)
    {
        super.onButtonAction(b);
    }

    @Override
    protected void onPressingEsc()
    {
        super.onPressingEsc();
        this.restoreValues();
        this.sendPacket();
    }

    @Override
    public void handleMouseInput() throws IOException
    {
        super.handleMouseInput();
        if (this.hasValueUpdated)
        {
            this.updateValueFromWidgets();
            this.hasValueUpdated = false;
        }
    }

    @Override
    public void handleKeyboardInput() throws IOException
    {
        super.handleKeyboardInput();
        if (this.hasValueUpdated)
        {
            this.updateValueFromWidgets();
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

    public void updateValueFromWidgets()
    {
        this.currentMarkerValue.groupId = this.fieldGroup.fieldValue;
        this.currentMarkerValue.name = this.fieldMarkerName.getText();
        this.currentMarkerValue.rp.height = (byte) this.fieldRailHeight.fieldValue;
        this.currentMarkerValue.rp.anchorLengthHorizontal = this.fieldAnchorLengthHorizontal.fieldValue;
        this.currentMarkerValue.rp.anchorLengthVertical = this.fieldAnchorLengthVertical.fieldValue;
        this.currentMarkerValue.rp.anchorYaw = this.fieldAnchorYaw.fieldValue;
        this.currentMarkerValue.rp.anchorPitch = this.fieldAnchorPitch.fieldValue;
        this.currentMarkerValue.rp.cantCenter = this.fieldCantCenter.fieldValue;
        this.currentMarkerValue.rp.cantEdge = this.fieldCantEdge.fieldValue;
        this.currentMarkerValue.rp.cantRandom = this.fieldCantRandom.fieldValue;
        this.currentMarkerValue.editStatusH = this.buttonEditStatusH.getSelectedOption();
        this.currentMarkerValue.editStatusV = this.buttonEditStatusV.getSelectedOption();
        this.currentMarkerValue.drawingScheme = this.buttonDrawingScheme.getSelectedOption();
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
                marker.editStatusH = v.editStatusH;
                marker.editStatusV = v.editStatusV;
                marker.drawingScheme = v.drawingScheme;
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

    private static class Scroll extends GuiScroll
    {
        public Scroll(IWidgetHolder holder, int id, IntSupplier x, IntSupplier y, IntSupplier width, IntSupplier height,
                      GuiWidget... widgets)
        {
            super(holder, id, x, y, width, height, widgets);
        }

        @Override
        public void drawCustom(int mouseX, int mouseY, float partialTicks)
        {
            if (!(pScr instanceof GuiMarkerAdvanced)) return;
            GuiMarkerAdvanced pScr = ((GuiMarkerAdvanced) this.pScr);
            int stringXpos = 10;
            int stringYpos = 3 + 8;//8;
            int fontColor = 0xE0E0E0 | pScr.getAlphaInt(0xFF);
            int fontColorGrey = 0xA0A0A0 | pScr.getAlphaInt(0xFF);

            //title line
//            pScr.drawString(pScr.fontRenderer,
//                    String.format(TextFormatting.BOLD + "Editing rail marker" + TextFormatting.RESET + " \"%s\"
//                    (%d, %d, %d)",
//                            pScr.marker.getName(), pScr.marker.getX(), pScr.marker.getY(), pScr.marker.getZ()),
//                            stringXpos, stringYpos,
//                    fontColor);
//            stringYpos += 12;
//            pScr.drawString(pScr.fontRenderer,
//                    TextFormatting.GRAY + TextFormatting.ITALIC.toString() +
//                            "Tips: You can use arrow keys or mouse wheel to adjust values.",
//                    stringXpos, stringYpos, fontColorGrey);
//            stringYpos += 12;
//            pScr.drawString(pScr.fontRenderer,
//                    TextFormatting.GRAY + TextFormatting.ITALIC.toString() + "LShift increases step(x10), LAlt
//                    decreases step(x0.1).",
//                    stringXpos, stringYpos, fontColorGrey);
//            stringYpos += 20;
            stringXpos += 15;
            //groupId
            pScr.drawString(pScr.fontRenderer, "Marker Group", stringXpos, stringYpos, fontColor);
            CacGuiUtils.drawRect(stringXpos - 13, stringYpos, stringXpos - 5, stringYpos + 8,
                    0xFFFFFF | pScr.getAlphaInt(0xFF));
            stringYpos += 18;
            //name
            pScr.drawString(pScr.fontRenderer, "Marker Name", stringXpos, stringYpos, fontColor);
            CacGuiUtils.drawRect(stringXpos - 13, stringYpos, stringXpos - 5, stringYpos + 8,
                    0xFFFFFF | pScr.getAlphaInt(0xFF));
            stringYpos += 18;
            //rail height
            pScr.drawString(pScr.fontRenderer, "Rail Height", stringXpos, stringYpos, fontColor);
            CacGuiUtils.drawRect(stringXpos - 13, stringYpos, stringXpos - 5, stringYpos + 8,
                    RenderMarkerBlock.MarkerElement.HEIGHT.getColor() | pScr.getAlphaInt(0xFF));
            stringYpos += 18;
            //anchor length horizontal
            pScr.drawString(pScr.fontRenderer, "Horizontal Length", stringXpos, stringYpos, fontColor);
            CacGuiUtils.drawRect(stringXpos - 13, stringYpos, stringXpos - 5, stringYpos + 8,
                    RenderMarkerBlock.MarkerElement.HORIZONTIAL.getColor() | pScr.getAlphaInt(0xFF));
            stringYpos += 18;
            //anchor yaw
            pScr.drawString(pScr.fontRenderer, "Anchor Yaw", stringXpos, stringYpos, fontColor);
            CacGuiUtils.drawRect(stringXpos - 13, stringYpos, stringXpos - 5, stringYpos + 8,
                    RenderMarkerBlock.MarkerElement.HORIZONTIAL.getColor() | pScr.getAlphaInt(0xFF));
            stringYpos += 18;
            //anchor length vertical
            pScr.drawString(pScr.fontRenderer, "Vertical Length", stringXpos, stringYpos, fontColor);
            CacGuiUtils.drawRect(stringXpos - 13, stringYpos, stringXpos - 5, stringYpos + 8,
                    RenderMarkerBlock.MarkerElement.VERTICAL.getColor() | pScr.getAlphaInt(0xFF));
            stringYpos += 18;
            //anchor pitch
            pScr.drawString(pScr.fontRenderer, "Anchor Pitch", stringXpos, stringYpos, fontColor);
            CacGuiUtils.drawRect(stringXpos - 13, stringYpos, stringXpos - 5, stringYpos + 8,
                    RenderMarkerBlock.MarkerElement.VERTICAL.getColor() | pScr.getAlphaInt(0xFF));
            stringYpos += 18;
            //cant edge
            pScr.drawString(pScr.fontRenderer, "Cant Edge", stringXpos, stringYpos, fontColor);
            CacGuiUtils.drawRect(stringXpos - 13, stringYpos, stringXpos - 5, stringYpos + 8,
                    RenderMarkerBlock.MarkerElement.CANT_EDGE.getColor() | pScr.getAlphaInt(0xFF));
            stringYpos += 18;
            //cant center
            pScr.drawString(pScr.fontRenderer, "Cant Center", stringXpos, stringYpos, fontColor);
            CacGuiUtils.drawRect(stringXpos - 13, stringYpos, stringXpos - 5, stringYpos + 8,
                    RenderMarkerBlock.MarkerElement.CANT_CENTER.getColor() | pScr.getAlphaInt(0xFF));
            stringYpos += 18;
            //cant random
            pScr.drawString(pScr.fontRenderer, "Cant Random", stringXpos, stringYpos, fontColor);
            CacGuiUtils.drawRect(stringXpos - 13, stringYpos, stringXpos - 5, stringYpos + 8,
                    RenderMarkerBlock.MarkerElement.CANT_CENTER.getColor() | pScr.getAlphaInt(0xFF));
            stringYpos += 24;
//        pScr.drawHorizontalLine(10, 400, stringYpos - 4, 0x333333 | pScr.getAlphaInt(0xFF));
            CacGuiUtils.drawRect(10, stringYpos - 4, 400, stringYpos - 4 + 1, 0x333333 | pScr.getAlphaInt(0xFF));
            stringYpos += 10;
            //horizontal edit status
            pScr.drawString(pScr.fontRenderer, "Edit Status of Horizontal:", stringXpos, stringYpos, fontColor);
            CacGuiUtils.drawRect(stringXpos - 13, stringYpos, stringXpos - 5, stringYpos + 8,
                    RenderMarkerBlock.MarkerElement.HORIZONTIAL.getColor() | pScr.getAlphaInt(0xFF));
            stringYpos += 20;
            //vertical edit status
            pScr.drawString(pScr.fontRenderer, "Edit Status of Vertical:", stringXpos, stringYpos, fontColor);
            CacGuiUtils.drawRect(stringXpos - 13, stringYpos, stringXpos - 5, stringYpos + 8,
                    RenderMarkerBlock.MarkerElement.VERTICAL.getColor() | pScr.getAlphaInt(0xFF));
            stringYpos += 20;
            //rail drawing scheme
            pScr.drawString(pScr.fontRenderer, "Rail Drawing Scheme:", stringXpos, stringYpos, fontColor);
            CacGuiUtils.drawRect(stringXpos - 13, stringYpos, stringXpos - 5, stringYpos + 8,
                    RenderMarkerBlock.MarkerElement.HORIZONTIAL.getColor() | pScr.getAlphaInt(0xFF));
            stringYpos += 20;
        }
    }
}