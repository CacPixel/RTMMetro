package net.cacpixel.rtmmetro.client.gui.screens;

import jp.ngt.ngtlib.block.BlockUtil;
import jp.ngt.ngtlib.math.NGTMath;
import jp.ngt.rtm.rail.BlockMarker;
import jp.ngt.rtm.rail.RenderMarkerBlock;
import jp.ngt.rtm.rail.util.RailPosition;
import net.cacpixel.rtmmetro.ModConfig;
import net.cacpixel.rtmmetro.RTMMetro;
import net.cacpixel.rtmmetro.RTMMetroBlock;
import net.cacpixel.rtmmetro.client.gui.*;
import net.cacpixel.rtmmetro.client.gui.toast.CacToast;
import net.cacpixel.rtmmetro.client.gui.toast.GuiToastAdvanced;
import net.cacpixel.rtmmetro.client.gui.widgets.*;
import net.cacpixel.rtmmetro.network.PacketMarkerClient;
import net.cacpixel.rtmmetro.rail.tileentity.TileEntityMarkerAdvanced;
import net.cacpixel.rtmmetro.rail.util.AnchorEditStatus;
import net.cacpixel.rtmmetro.rail.util.RailDrawingScheme;
import net.cacpixel.rtmmetro.rail.util.RailMapAdvanced;
import net.cacpixel.rtmmetro.util.BlockUtils;
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
import java.util.stream.Stream;

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

    @Override
    public void initGui()
    {
        int fieldW = 75;
        int fieldH = 14;
        int fieldX = 140;
        int fieldY = 8;//50;
        int buttW = 80;
        int buttH = fieldH + 4;
        int buttX = fieldX + fieldW + 2;
        int lineHeight = 18;
        int stringXpos = 10;
        int fontColor = 0xE0E0E0 | this.getAlphaInt(0xFF);

        super.initGui();
        this.labelTitle.setText(String.format(
                TextFormatting.BOLD + I18n.format("gui.marker.title") + TextFormatting.RESET + " \"%s\" " +
                        TextFormatting.YELLOW + "(%d, %d, %d)",
                this.marker.getName(), this.marker.getX(), this.marker.getY(), this.marker.getZ()));
        this.mainScroll = new Scroll(this, this.getNextWidgetId(),
                () -> 0,
                () -> 30,
                () -> this.width,
                () -> Math.max(1, this.height - 40 - 30));
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
                        I18n.format("gui.marker.straight_line"))
                .setListener((w) -> {
                    this.currentValues.forEach(v -> v.rp.anchorLengthHorizontal = 0);
                    fieldAnchorLengthHorizontal.fieldValue = 0;
                    fieldAnchorLengthHorizontal.checkValueAndSetText();
                });
        this.buttonMagicNumberH = WidgetFactory.addButton(mainScroll, buttX + buttH + buttW, fieldY - 2, buttW, buttH,
                        I18n.format("gui.marker.magic_number"))
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
                        I18n.format("gui.marker.equ_neighbor"))
                .setListener((w) -> {
                    RailPosition rp = TileEntityMarkerAdvanced.getNeighborRP(this.marker);
                    if (rp != null)
                    {
                        this.fieldAnchorYaw.fieldValue = MathHelper.wrapDegrees(rp.anchorYaw + 180.0F);
                        this.fieldAnchorYaw.checkValueAndSetText();
                    }
                });
        this.buttonMagicNumberYaw = WidgetFactory.addButton(mainScroll, buttX + buttH + buttW, fieldY - 2, buttW, buttH,
                        I18n.format("gui.marker.magic_number"))
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
                        I18n.format("gui.marker.straight_line"))
                .setListener((w) -> {
                    this.currentValues.forEach(v -> v.rp.anchorLengthVertical = 0);
                    fieldAnchorLengthVertical.fieldValue = 0;
                    fieldAnchorLengthVertical.checkValueAndSetText();
                });
        this.buttonMagicNumberV = WidgetFactory.addButton(mainScroll, buttX + buttH + buttW, fieldY - 2, buttW, buttH,
                        I18n.format("gui.marker.magic_number"))
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
                        I18n.format("gui.marker.equ_neighbor"))
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
                        I18n.format("gui.marker.calculate"))
                .setListener((w) -> {
                    guiCalculateCant = new GuiCalculateCant(x -> {
                        fieldCantEdge.fieldValue = (float) x;
                        fieldCantEdge.checkValueAndSetText();
                    }).setWindowSize(250, 200).setParent(this);
//                    displayGuiScreen(guiCalculateCant);
                    displayGuiScreen(new GuiMarkerAdvanced(this.marker).setParent(this));
                });
        this.buttonCopyNeighborCantEdge = WidgetFactory.addButton(mainScroll, buttX + buttH + buttW, fieldY - 2, buttW,
                        buttH, I18n.format("gui.marker.equ_neighbor"))
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
                        I18n.format("gui.marker.calculate"))
                .setListener((w) -> {
                    guiCalculateCant = new GuiCalculateCant(x -> {
                        fieldCantCenter.fieldValue = (float) x;
                        fieldCantCenter.checkValueAndSetText();
                    }).setWindowSize(250, 200).setParent(this);
                    displayGuiScreen(guiCalculateCant);
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
                I18n.format("gui.marker.redraw"),
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
        this.buttonOK = WidgetFactory.addButton(this, () -> this.width - 160 - 10, () -> this.height - 30,
                        () -> 160, () -> 20,
                        I18n.format("gui.save")).setIcon(GuiButtonAdvanced.getSaveIcon())
                .setListener((w) -> {
                    this.updateValueFromWidgets();
                    this.sendPacket();
                    this.displayPrevScreen();
                    GuiToastAdvanced.INSTANCE.add(
                            new CacToast(new TextComponentString(I18n.format("gui.marker.toast.success")),
                                    new TextComponentString(I18n.format("gui.marker.toast.parameter_saved"))));
                });

        int labelY = 6;
        mainScroll.addWidget(GuiLabelAdvanced.class, stringXpos, labelY, 120, 18, fontColor)
                .setText(I18n.format("gui.marker.marker_group")).setAlignY(Align.CENTERED)
                .setIcon(new Image(GuiTheme.getCurrentResourceLocation("icon/marker_group")).setColor(0xFFFFFF));
        labelY += lineHeight;
        mainScroll.addWidget(GuiLabelAdvanced.class, stringXpos, labelY, 120, 18, fontColor)
                .setText(I18n.format("gui.marker.marker_name")).setAlignY(Align.CENTERED)
                .setIcon(new Image(GuiTheme.getCurrentResourceLocation("icon/marker_name")).setColor(0xFFFFFF));
        labelY += lineHeight;
        mainScroll.addWidget(GuiLabelAdvanced.class, stringXpos, labelY, 120, 18, fontColor)
                .setText(I18n.format("gui.marker.rail_height")).setAlignY(Align.CENTERED)
                .setIcon(new Image(GuiTheme.getCurrentResourceLocation("icon/marker_edit_line"))
                        .setColor(RenderMarkerBlock.MarkerElement.HEIGHT.getColor()));
        labelY += lineHeight;
        mainScroll.addWidget(GuiLabelAdvanced.class, stringXpos, labelY, 120, 18, fontColor)
                .setText(I18n.format("gui.marker.horizontal_length")).setAlignY(Align.CENTERED)
                .setIcon(new Image(GuiTheme.getCurrentResourceLocation("icon/marker_edit_line"))
                        .setColor(RenderMarkerBlock.MarkerElement.HORIZONTIAL.getColor()));
        labelY += lineHeight;
        mainScroll.addWidget(GuiLabelAdvanced.class, stringXpos, labelY, 120, 18, fontColor)
                .setText(I18n.format("gui.marker.anchor_yaw")).setAlignY(Align.CENTERED)
                .setIcon(new Image(GuiTheme.getCurrentResourceLocation("icon/marker_edit_line"))
                        .setColor(RenderMarkerBlock.MarkerElement.HORIZONTIAL.getColor()));
        labelY += lineHeight;
        mainScroll.addWidget(GuiLabelAdvanced.class, stringXpos, labelY, 120, 18, fontColor)
                .setText(I18n.format("gui.marker.vertical_length")).setAlignY(Align.CENTERED)
                .setIcon(new Image(GuiTheme.getCurrentResourceLocation("icon/marker_edit_line"))
                        .setColor(RenderMarkerBlock.MarkerElement.VERTICAL.getColor()));
        labelY += lineHeight;
        mainScroll.addWidget(GuiLabelAdvanced.class, stringXpos, labelY, 120, 18, fontColor)
                .setText(I18n.format("gui.marker.anchor_pitch")).setAlignY(Align.CENTERED)
                .setIcon(new Image(GuiTheme.getCurrentResourceLocation("icon/marker_edit_line"))
                        .setColor(RenderMarkerBlock.MarkerElement.VERTICAL.getColor()));
        labelY += lineHeight;
        mainScroll.addWidget(GuiLabelAdvanced.class, stringXpos, labelY, 120, 18, fontColor)
                .setText(I18n.format("gui.marker.cant_edge")).setAlignY(Align.CENTERED)
                .setIcon(new Image(GuiTheme.getCurrentResourceLocation("icon/marker_edit_line"))
                        .setColor(RenderMarkerBlock.MarkerElement.CANT_EDGE.getColor()));
        labelY += lineHeight;
        mainScroll.addWidget(GuiLabelAdvanced.class, stringXpos, labelY, 120, 18, fontColor)
                .setText(I18n.format("gui.marker.cant_center")).setAlignY(Align.CENTERED)
                .setIcon(new Image(GuiTheme.getCurrentResourceLocation("icon/marker_edit_line"))
                        .setColor(RenderMarkerBlock.MarkerElement.CANT_CENTER.getColor()));
        labelY += lineHeight;
        mainScroll.addWidget(GuiLabelAdvanced.class, stringXpos, labelY, 120, 18, fontColor)
                .setText(I18n.format("gui.marker.cant_random")).setAlignY(Align.CENTERED)
                .setIcon(new Image(GuiTheme.getCurrentResourceLocation("icon/marker_edit_line"))
                        .setColor(RenderMarkerBlock.MarkerElement.CANT_EDGE.getColor()));
        labelY += 34;

        mainScroll.addWidget(GuiLabelAdvanced.class, stringXpos, labelY, 150, 18, fontColor)
                .setText(I18n.format("gui.marker.edit_status_h")).setAlignY(Align.CENTERED)
                .setIcon(new Image(GuiTheme.getCurrentResourceLocation("icon/marker_edit_line"))
                        .setColor(RenderMarkerBlock.MarkerElement.HORIZONTIAL.getColor()));
        labelY += 20;
        mainScroll.addWidget(GuiLabelAdvanced.class, stringXpos, labelY, 150, 18, fontColor)
                .setText(I18n.format("gui.marker.edit_status_v")).setAlignY(Align.CENTERED)
                .setIcon(new Image(GuiTheme.getCurrentResourceLocation("icon/marker_edit_line"))
                        .setColor(RenderMarkerBlock.MarkerElement.VERTICAL.getColor()));
        labelY += 20;
        mainScroll.addWidget(GuiLabelAdvanced.class, stringXpos, labelY, 150, 18, fontColor)
                .setText(I18n.format("gui.marker.rail_drawing_scheme")).setAlignY(Align.CENTERED)
                .setIcon(new Image(GuiTheme.getCurrentResourceLocation("icon/marker_edit_line"))
                        .setColor(RenderMarkerBlock.MarkerElement.HORIZONTIAL.getColor()));
        labelY += 20;

        this.controlEnable();
    }

    @Override
    public void closeButtonCallback(GuiWidget w)
    {
        this.restoreValues();
        this.sendPacket();
        this.displayPrevScreen();
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
    public void draw(int mouseX, int mouseY, float partialTicks)
    {
        this.drawScreenBefore(mouseX, mouseY, partialTicks);
        this.drawDefaultBackground();
        super.draw(mouseX, mouseY, partialTicks);
        this.drawScreenAfter(mouseX, mouseY, partialTicks);
    }

    @Override
    public void drawScreenBefore(int mouseX, int mouseY, float partialTicks)
    {
        Stream.of(fieldAnchorLengthHorizontal, fieldAnchorLengthVertical).forEach(f -> {
            if (f.fieldValue < 0 && !f.isFocused())
            {
                f.setText(I18n.format("gui.marker.textbox_straight_line"));
                f.prefixTextFormatting = TextFormatting.GRAY + TextFormatting.ITALIC.toString();
            }
            if (f.fieldValue < 0 && f.isFocused())
            {
                f.setText("");
                f.prefixTextFormatting = "";
            }
        });
        this.drawDefaultBackgroundBefore();
        super.drawScreenBefore(mouseX, mouseY, partialTicks);
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
        public Scroll(IWidgetHolder holder, int id, IntSupplier x, IntSupplier y, IntSupplier width, IntSupplier height)
        {
            super(holder, id, x, y, width, height);
        }

        @Override
        public void drawCustom(int mouseX, int mouseY, float partialTicks)
        {
            if (!(pScr instanceof GuiMarkerAdvanced)) return;
            GuiMarkerAdvanced pScr = ((GuiMarkerAdvanced) this.pScr);
            int stringYpos = 192;
            CacGuiUtils.drawRect(10, stringYpos , 400, stringYpos + 1, 0x555555 | pScr.getAlphaInt(0xFF));

        }
    }
}