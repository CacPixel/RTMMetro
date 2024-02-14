package net.cacpixel.rtmmetro.render;

import jp.ngt.ngtlib.math.NGTMath;
import jp.ngt.ngtlib.renderer.GLHelper;
import jp.ngt.ngtlib.renderer.NGTRenderer;
import jp.ngt.ngtlib.renderer.NGTTessellator;
import jp.ngt.ngtlib.util.ColorUtil;
import jp.ngt.ngtlib.util.NGTUtilClient;
import jp.ngt.rtm.RTMCore;
import jp.ngt.rtm.gui.InternalButton;
import jp.ngt.rtm.gui.InternalGUI;
import jp.ngt.rtm.rail.TileEntityLargeRailBase;
import jp.ngt.rtm.rail.TileEntityLargeRailCore;
import jp.ngt.rtm.rail.util.MarkerState;
import jp.ngt.rtm.rail.util.RailMap;
import jp.ngt.rtm.rail.util.RailPosition;
import net.cacpixel.rtmmetro.RTMMetro;
import net.cacpixel.rtmmetro.RTMMetroBlock;
import net.cacpixel.rtmmetro.network.PacketMarkerRPClient;
import net.cacpixel.rtmmetro.rail.block.BlockMarkerAdvanced;
import net.cacpixel.rtmmetro.rail.tileentity.TileEntityMarkerAdvanced;
import net.cacpixel.rtmmetro.util.WorldUtils;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;


public class RenderMarkerBlockAdvanced extends TileEntitySpecialRenderer<TileEntityMarkerAdvanced> {

    public static final RenderMarkerBlockAdvanced INSTANCE = new RenderMarkerBlockAdvanced();
    private static final double FIT_RANGE_SQ = 4.0D;
    private String[] displayStrings = new String[RTMCore.markerDisplayDistance / 10];

    private RenderMarkerBlockAdvanced() {
        for (int i = 0; i < this.displayStrings.length; ++i) {
            this.displayStrings[i] = (i + 1) * 10 + "m";
        }

    }

    public boolean isGlobalRenderer(TileEntityMarkerAdvanced tileEntity) {
        return true;
    }

    public void render(TileEntityMarkerAdvanced marker, double par2, double par4, double par6, float par8, int par9, float par10) {
        if (marker.getMarkerRP() != null) {
            GL11.glPushMatrix();
            GL11.glEnable(32826);
            GL11.glDisable(2884);
            GLHelper.disableLighting();
            GL11.glTranslatef((float) par2, (float) par4, (float) par6);
            this.renderGUI(marker);
            GL11.glDisable(3553);
            if (marker.getState(MarkerState.GRID) && marker.getGrid() != null) {
                try {
                    this.renderGrid(marker);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }

            if ((marker.getState(MarkerState.LINE1) || marker.getState(MarkerState.LINE2))) {
                RailPosition railposition = marker.getMarkerRP();
                float f = (float) (railposition.posX - (double) railposition.blockX);
                float f1 = (float) (railposition.posY - (double) railposition.blockY);
                float f2 = (float) (railposition.posZ - (double) railposition.blockZ);
                if (marker.getState(MarkerState.LINE1) && marker.getPrevRailMaps() != null && marker.getPrevRailMaps().length > 0) {
                    try {
                        this.renderLine(marker, f, f1, f2);
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }

                if (marker.getCoreMarker() != null && marker.getPrevRailMaps() != null && marker.getPrevRailMaps().length > 0) {
                    this.renderAnchor(marker, f, f1, f2);
                }
            }

            if (marker.getState(MarkerState.DISTANCE)) {
                this.renderDistanceMark(marker);
            }

            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GLHelper.enableLighting();
            GL11.glEnable(3553);
            GL11.glEnable(2884);
            GL11.glPopMatrix();
        }
    }

    private void renderGUI(TileEntityMarkerAdvanced marker) {
        if (marker.gui == null) {
            int i = 61440;
            float f = 2.8F;
            float f1 = 0.5F;
            float f2 = (f1 + 0.1F) * 5.0F + 0.1F;
            float f3 = 0.5F;
            float f4 = -(f + 0.2F) / 2.0F;
            marker.gui = (new InternalGUI(f4, f3, f + 0.2F, f2)).setColor(65535);
            marker.buttons = new InternalButton[5];
            f4 = f4 + 0.1F;
            f3 = f3 + 0.1F;
            marker.buttons[0] = (new InternalButton(f4, f3, f, f1)).setColor(i).setListner((button) -> marker.flipState(MarkerState.ANCHOR21));
            f3 = f3 + f1 + 0.1F;
            marker.buttons[1] = (new InternalButton(f4, f3, f, f1)).setColor(i).setListner((button) -> marker.flipState(MarkerState.LINE2));
            f3 = f3 + f1 + 0.1F;
            marker.buttons[2] = (new InternalButton(f4, f3, f, f1)).setColor(i).setListner((button) -> marker.flipState(MarkerState.LINE1));
            f3 = f3 + f1 + 0.1F;
            marker.buttons[3] = (new InternalButton(f4, f3, f, f1)).setColor(i).setListner((button) -> marker.flipState(MarkerState.GRID));
            f3 = f3 + f1 + 0.1F;
            marker.buttons[4] = (new InternalButton(f4, f3, f, f1)).setColor(i).setListner((button) -> marker.flipState(MarkerState.DISTANCE));

            for (int j = 0; j < marker.buttons.length; ++j) {
                marker.gui.addButton(marker.buttons[j]);
            }
        }

        marker.buttons[0].setText(marker.getStateString(MarkerState.ANCHOR21), 16777215, 0.05F);
        marker.buttons[1].setText(marker.getStateString(MarkerState.LINE2), 16777215, 0.05F);
        marker.buttons[2].setText(marker.getStateString(MarkerState.LINE1), 16777215, 0.05F);
        marker.buttons[3].setText(marker.getStateString(MarkerState.GRID), 16777215, 0.05F);
        marker.buttons[4].setText(marker.getStateString(MarkerState.DISTANCE), 16777215, 0.05F);
        GL11.glPushMatrix();
        float f5 = 0.5F;
        if (marker.getState(MarkerState.LINE1)) {
            f5 = 1.0F;
        }

        if (marker.getState(MarkerState.LINE2) && f5 < marker.getMarkerRP().constLimitHP) {
            f5 = marker.getMarkerRP().constLimitHP;
        }

        GL11.glTranslatef(0.5F, f5, 0.5F);
        GL11.glRotatef(-NGTUtilClient.getMinecraft().getRenderManager().playerViewY + 180.0F, 0.0F, 1.0F, 0.0F);
        marker.gui.render();
        GL11.glPopMatrix();
    }

    private void renderGrid(TileEntityMarkerAdvanced marker) {
        GL11.glPushMatrix();
        NGTTessellator ngttessellator = NGTTessellator.instance;
        ngttessellator.startDrawing(1);
        ngttessellator.setColorOpaque_I(0);
        for (int[] aint : marker.getPrevGrid()) {
            BlockPos blockpos = marker.getPos();
            NGTRenderer.addFrame(ngttessellator, (float) (aint[0] - blockpos.getX()), (float) (aint[1] - blockpos.getY()), (float) (aint[2] - blockpos.getZ()), 1.0F, 1.0F, 1.0F);
        }
        ngttessellator.draw();
        GL11.glPopMatrix();
    }

    private void renderDistanceMark(TileEntityMarkerAdvanced marker) {
        GL11.glPushMatrix();
        GL11.glTranslatef(0.5F, 0.0625F, 0.5F);
        int i = marker.getBlockMetadata();
        Block block = marker.getBlockType();
        int j = block == RTMMetroBlock.MARKER_ADVANCED ? 16711680 : 255;
        float f = (float) BlockMarkerAdvanced.getMarkerDir(marker.getBlockType(), i) * 45.0F;
        GL11.glRotatef(f, 0.0F, 1.0F, 0.0F);
        GL11.glDisable(3553);
        float f1 = 0.4F;
        NGTTessellator ngttessellator = NGTTessellator.instance;
        ngttessellator.startDrawingQuads();
        ngttessellator.setColorOpaque_I(j);

        for (int k = 1; k < this.displayStrings.length; ++k) {
            float f2 = (float) k * 10.0F;

            for (int l = -1; l <= 1; ++l) {
                float f3 = f2 * (float) l;
                ngttessellator.addVertex(-0.4F + f3, 0.0F, 0.4F + f2);
                ngttessellator.addVertex(-0.4F + f3, 0.0F, -0.4F + f2);
                ngttessellator.addVertex(0.4F + f3, 0.0F, -0.4F + f2);
                ngttessellator.addVertex(0.4F + f3, 0.0F, 0.4F + f2);
            }
        }

        ngttessellator.draw();
        GL11.glEnable(3553);
        FontRenderer fontrenderer = NGTUtilClient.getMinecraft().getRenderManager().getFontRenderer();

        for (int j1 = 0; j1 < this.displayStrings.length; ++j1) {
            float f5 = (float) (j1 + 1) * 10.0F;

            for (int k1 = -1; k1 <= 1; ++k1) {
                float f4 = f5 * (float) k1;
                GL11.glPushMatrix();
                GL11.glTranslatef(f4, 0.0F, f5);
                GL11.glRotatef(-NGTUtilClient.getMinecraft().getRenderManager().playerViewY - f, 0.0F, 1.0F, 0.0F);
                GL11.glScalef(-0.25F, -0.25F, 0.25F);
                String s = this.displayStrings[j1];
                int i1 = fontrenderer.getStringWidth(s) / 2;
                fontrenderer.drawString(s, -i1 / 2, -10, j);
                GL11.glPopMatrix();
            }
        }

        GL11.glPopMatrix();
    }

    private void renderLine(TileEntityMarkerAdvanced marker, float x, float y, float z) {
        // ArrayIndexOutOfBoundsException修复
//        if (marker.linePos == null) {
        marker.linePos = new float[marker.getPrevRailMaps().length][][];

        for (int i = 0; i < marker.linePos.length; ++i) {
            RailMap railmap = marker.getPrevRailMaps()[i];
            RailPosition railposition = railmap.getStartRP();
            if (marker.getMarkerRP().equals(railposition)) {
                int j = (int) ((float) railmap.getLength() * 2.0F);
                double[] adouble = railmap.getRailPos(j, 0);
                double d0 = railmap.getRailHeight(j, 0);
                float[][] afloat = new float[j + 1][5];
                marker.linePos[i] = new float[j + 1][];

                for (int l = 0; l < marker.linePos[i].length; ++l) {
                    double[] adouble1 = railmap.getRailPos(j, l);
                    marker.linePos[i][l] = new float[]{(float) (adouble1[1] - adouble[1]), (float) (railmap.getRailHeight(j, l) - d0), (float) (adouble1[0] - adouble[0])};
                }
            }
        }
//        }

        GL11.glPushMatrix();
        GL11.glTranslatef(x, y, z);
        NGTTessellator ngttessellator = NGTTessellator.instance;
        try {
            for (int i1 = 0; i1 < marker.linePos.length; ++i1) {
//        for(int i1 = 0; i1 < marker.getPrevRailMaps().length; ++i1) {
                if (marker.linePos[i1] != null) {
                    GL11.glPushMatrix();

                    float lineWidth = (float) NGTUtilClient.getMinecraft().displayHeight * 0.005F;
                    GL11.glDisable(GL11.GL_TEXTURE_2D);
                    float prevLineWidth = GL11.glGetFloat(GL11.GL_LINE_WIDTH);
                    GL11.glLineWidth(lineWidth);

                    RailMap railmap1 = marker.getPrevRailMaps()[i1];
                    float f = (float) (railmap1.getStartRP().posX - marker.getMarkerRP().posX);
                    float f1 = (float) (railmap1.getStartRP().posY - marker.getMarkerRP().posY);
                    float f2 = (float) (railmap1.getStartRP().posZ - marker.getMarkerRP().posZ);
                    GL11.glTranslatef(f, f1, f2);
                    ngttessellator.startDrawing(3);
                    ngttessellator.setColorOpaque_I(16384);

                    for (int k = 0; k < marker.linePos[i1].length; ++k) {
                        ngttessellator.addVertex(marker.linePos[i1][k][0], marker.linePos[i1][k][1], marker.linePos[i1][k][2]);
                    }
                    ngttessellator.draw();
                    GL11.glLineWidth(prevLineWidth);
                    GL11.glEnable(GL11.GL_TEXTURE_2D);
                    GL11.glPopMatrix();
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }

        GL11.glPopMatrix();
    }

    private void renderAnchor(TileEntityMarkerAdvanced marker, float x, float y, float z) {
        this.changeAnchor(marker);
        GL11.glPushMatrix();
        GL11.glTranslatef(x, y, z);
        MarkerElement element = MarkerElement.values()[marker.editMode];
        if (marker.editMode == 0) {
            element = this.renderAnchorLine(marker, true, (MarkerElement) null);
        }

        if (marker.editMode == 0 && element != MarkerElement.NONE && Mouse.isButtonDown(1)) {
            marker.editMode = element.ordinal();
            marker.startPlayerPitch = NGTUtilClient.getMinecraft().player.rotationPitch;
            marker.startPlayerYaw = NGTUtilClient.getMinecraft().player.rotationYawHead;
            marker.startMarkerHeight = marker.getMarkerRP().height;
        }

        this.renderAnchorLine(marker, false, element);
        GL11.glPopMatrix();
    }

    private MarkerElement renderAnchorLine(TileEntityMarkerAdvanced marker, boolean isPickMode, MarkerElement hoveredElement) {
        float f = (float) NGTUtilClient.getMinecraft().displayHeight * 0.01F;
        if (isPickMode) {
            GLHelper.startMousePicking(f * 2.0F);
        }

        GL11.glDisable(3553);
        float f1 = GL11.glGetFloat(2833);
        float f2 = GL11.glGetFloat(2849);
        GL11.glPointSize(f * 3.0F);
        GL11.glLineWidth(f);
        RailPosition railposition = marker.getMarkerRP();
        int i = 12632256;
        if (marker.getState(MarkerState.LINE2) && marker.isCoreMarker()) {
            GL11.glPushMatrix();
            GL11.glRotatef(railposition.anchorYaw, 0.0F, 1.0F, 0.0F);
            if (isPickMode) {
                GL11.glLoadName(MarkerElement.CONST_LIMIT_WP.ordinal());
            }

            int j = MarkerElement.CONST_LIMIT_WP.getColor();
            j = hoveredElement == MarkerElement.CONST_LIMIT_WP ? ColorUtil.multiplicating(j, i) : j;
            renderLine(railposition.constLimitWP, railposition.constLimitHN, 0.0F, railposition.constLimitWP, railposition.constLimitHP, 0.0F, j);
            if (isPickMode) {
                GL11.glLoadName(MarkerElement.CONST_LIMIT_WN.ordinal());
            }

            j = MarkerElement.CONST_LIMIT_WN.getColor();
            j = hoveredElement == MarkerElement.CONST_LIMIT_WN ? ColorUtil.multiplicating(j, i) : j;
            renderLine(railposition.constLimitWN, railposition.constLimitHP, 0.0F, railposition.constLimitWN, railposition.constLimitHN, 0.0F, j);
            if (isPickMode) {
                GL11.glLoadName(MarkerElement.CONST_LIMIT_HP.ordinal());
            }

            j = MarkerElement.CONST_LIMIT_HP.getColor();
            j = hoveredElement == MarkerElement.CONST_LIMIT_HP ? ColorUtil.multiplicating(j, i) : j;
            renderLine(railposition.constLimitWP, railposition.constLimitHP, 0.0F, railposition.constLimitWN, railposition.constLimitHP, 0.0F, j);
            if (isPickMode) {
                GL11.glLoadName(MarkerElement.CONST_LIMIT_HN.ordinal());
            }

            j = MarkerElement.CONST_LIMIT_HN.getColor();
            j = hoveredElement == MarkerElement.CONST_LIMIT_HN ? ColorUtil.multiplicating(j, i) : j;
            renderLine(railposition.constLimitWN, railposition.constLimitHN, 0.0F, railposition.constLimitWP, railposition.constLimitHN, 0.0F, j);
            GL11.glPopMatrix();
        }

        if (marker.getState(MarkerState.LINE1)) {
            GL11.glPushMatrix();
            if (isPickMode) {
                GL11.glLoadName(MarkerElement.HEIGHT.ordinal());
            }

            int i1 = MarkerElement.HEIGHT.getColor();
            i1 = hoveredElement == MarkerElement.HEIGHT ? ColorUtil.multiplicating(i1, i) : i1;
            renderLine(0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, i1);
            GL11.glRotatef(railposition.anchorYaw, 0.0F, 1.0F, 0.0F);
            if (isPickMode) {
                GL11.glLoadName(MarkerElement.HORIZONTIAL.ordinal());
            }

            i1 = MarkerElement.HORIZONTIAL.getColor();
            i1 = hoveredElement == MarkerElement.HORIZONTIAL ? ColorUtil.multiplicating(i1, i) : i1;
            renderLine(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, railposition.anchorLengthHorizontal, i1);
            GL11.glPushMatrix();
            GL11.glRotatef(-railposition.anchorPitch, 1.0F, 0.0F, 0.0F);
            if (isPickMode) {
                GL11.glLoadName(MarkerElement.VERTICAL.ordinal());
            }

            i1 = MarkerElement.VERTICAL.getColor();
            i1 = hoveredElement == MarkerElement.VERTICAL ? ColorUtil.multiplicating(i1, i) : i1;
            renderLine(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, railposition.anchorLengthVertical, i1);
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            float f3 = 1.0F;
            GL11.glRotatef(railposition.cantEdge, 0.0F, 0.0F, 1.0F);
            if (isPickMode) {
                GL11.glLoadName(MarkerElement.CANT_EDGE.ordinal());
            }

            i1 = MarkerElement.CANT_EDGE.getColor();
            i1 = hoveredElement == MarkerElement.CANT_EDGE ? ColorUtil.multiplicating(i1, i) : i1;
            renderLine(0.0F, 0.0F, 0.0F, f3, 0.0F, 0.0F, i1);
            renderLine(0.0F, 0.0F, 0.0F, -f3, 0.0F, 0.0F, i1);
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            if (marker.isCoreMarker() && marker.getPrevRailMaps() != null && marker.getPrevRailMaps().length == 1) {
                RailMap railmap = marker.getPrevRailMaps()[0];
                int k = (int) ((float) railmap.getLength() * 2.0F);
                int l = k / 2;
                double[] adouble = railmap.getRailPos(k, 0);
                double[] adouble1 = railmap.getRailPos(k, l);
                double d0 = railmap.getRailHeight(k, 0);
                double d1 = railmap.getRailHeight(k, l);
                float f4 = railmap.getRailRotation(k, 0);
                float f5 = railmap.getRailRotation(k, l);
                GL11.glRotatef(-railposition.anchorYaw, 0.0F, 1.0F, 0.0F);
                GL11.glTranslatef((float) (adouble1[1] - adouble[1]), (float) (d1 - d0), (float) (adouble1[0] - adouble[0]));
                GL11.glRotatef(railposition.anchorYaw - (f4 - f5), 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(railposition.cantCenter, 0.0F, 0.0F, 1.0F);
                if (isPickMode) {
                    GL11.glLoadName(MarkerElement.CANT_CENTER.ordinal());
                }

                i1 = MarkerElement.CANT_CENTER.getColor();
                i1 = hoveredElement == MarkerElement.CANT_CENTER ? ColorUtil.multiplicating(i1, i) : i1;
                renderLine(0.0F, 0.0F, 0.0F, f3, 0.0F, 0.0F, i1);
                renderLine(0.0F, 0.0F, 0.0F, -f3, 0.0F, 0.0F, i1);
            }

            GL11.glPopMatrix();
            GL11.glPopMatrix();
        }

        GL11.glPointSize(f1);
        GL11.glLineWidth(f2);
        GL11.glEnable(3553);
        if (marker.getState(MarkerState.LINE1) && !isPickMode) {
            FontRenderer fontrenderer = NGTUtilClient.getMinecraft().getRenderManager().getFontRenderer();
            float f6 = 0.04F;
            GL11.glPushMatrix();
            GL11.glRotatef(-NGTUtilClient.getMinecraft().getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
            GL11.glScalef(-f6, -f6, f6);
            float f7 = 3.0F;
            float f8 = -34.0F;
            fontrenderer.drawString(String.valueOf((int) railposition.height), f7, f8, MarkerElement.HEIGHT.getColor(), false);
            f8 = f8 + 6.0F;
            fontrenderer.drawString(String.valueOf(railposition.anchorYaw), f7, f8, MarkerElement.HORIZONTIAL.getColor(), false);
            f8 = f8 + 6.0F;
            fontrenderer.drawString(String.valueOf(railposition.anchorPitch), f7, f8, MarkerElement.VERTICAL.getColor(), false);
            f8 = f8 + 6.0F;
            fontrenderer.drawString(String.valueOf(railposition.cantEdge), f7, f8, MarkerElement.CANT_EDGE.getColor(), false);
            f8 = f8 + 6.0F;
            fontrenderer.drawString(String.valueOf(railposition.cantCenter), f7, f8, MarkerElement.CANT_CENTER.getColor(), false);
            GL11.glPopMatrix();
        }

        if (isPickMode) {
            int j1 = GLHelper.finishMousePicking();
            if (j1 > 0) {
                int k1 = GLHelper.getPickedObjId(0);
                return MarkerElement.values()[k1];
            }
        }

        return MarkerElement.NONE;
    }

    public static void renderLine(float startX, float startY, float startZ, float endX, float endY, float endZ, int color) {
        NGTTessellator ngttessellator = NGTTessellator.instance;
        ngttessellator.startDrawing(1);
        ngttessellator.setColorOpaque_I(color);
        ngttessellator.addVertex(startX, startY, startZ);
        ngttessellator.addVertex(endX, endY, endZ);
        ngttessellator.draw();
        ngttessellator.startDrawing(0);
        ngttessellator.setColorOpaque_I(color);
        ngttessellator.addVertex(endX, endY, endZ);
        ngttessellator.draw();
    }

    private boolean changeAnchor(TileEntityMarkerAdvanced marker) {
        if (marker.editMode != 0 && marker.getCoreMarker() != null) {
            if (marker.editMode > 0 && Mouse.isButtonDown(1)) {
                marker.editMode = 0;
                RTMMetro.NETWORK_WRAPPER.sendToServer(new PacketMarkerRPClient(marker.getCoreMarker()));
            }

            MarkerElement element = MarkerElement.values()[marker.editMode];
            Minecraft minecraft = NGTUtilClient.getMinecraft();
            RailPosition railposition = marker.getMarkerRP();
            float pitch = minecraft.player.rotationPitch - marker.startPlayerPitch;
            float yaw = minecraft.player.rotationYawHead - marker.startPlayerYaw;
            if (marker.getState(MarkerState.LINE1)) {
                if (element == MarkerElement.HEIGHT) {
                    int i = marker.startMarkerHeight + (int) (-pitch / 1.0F);
                    i = i < 0 ? 0 : (i > 15 ? 15 : i);
                    if (i != marker.getMarkerRP().height) {
                        railposition.height = (byte) i;
                        railposition.init();
                        // marker.onChangeRailShape();
                        return true;
                    }

                    return false;
                }

                if (element == MarkerElement.CANT_EDGE) {
                    float f6 = 80.0F;
                    float f12 = pitch < -f6 ? -f6 : (pitch > f6 ? f6 : pitch);
                    RailPosition railposition2 = this.getNeighborRail(marker);
                    if (railposition2 != null) {
                        f12 = -railposition2.cantEdge;
                    }

                    railposition.cantEdge = f12;
                    // marker.onChangeRailShape();
                    return true;
                }

                if (element == MarkerElement.CANT_CENTER) {
                    float f5 = 80.0F;
                    float f11 = pitch < -f5 ? -f5 : (pitch > f5 ? f5 : pitch);
                    railposition.cantCenter = f11;
                    // marker.onChangeRailShape();
                    return true;
                }
            }

            if (marker.getState(MarkerState.LINE2)) {
                RailMap railmap = marker.getPrevRailMaps()[0];  // ArrayIndexOutOfBoundsException
                if (element == MarkerElement.CONST_LIMIT_HP) {
                    float f10 = 3.0F + -pitch / 10.0F;
                    f10 = f10 < 1.9F ? 1.9F : f10;
                    railmap.getStartRP().constLimitHP = railmap.getEndRP().constLimitHP = f10;
                    // marker.onChangeRailShape();
                    return true;
                }

                if (element == MarkerElement.CONST_LIMIT_HN) {
                    float f9 = -pitch / 10.0F;
                    f9 = f9 > 0.0F ? 0.0F : f9;
                    railmap.getStartRP().constLimitHN = railmap.getEndRP().constLimitHN = f9;
                    // marker.onChangeRailShape();
                    return true;
                }

                if (element == MarkerElement.CONST_LIMIT_WP) {
                    float f8 = 1.5F + -yaw / 10.0F;
                    f8 = f8 < 0.49F ? 0.49F : f8;
                    railmap.getStartRP().constLimitWP = railmap.getEndRP().constLimitWP = f8;
                    // marker.onChangeRailShape();
                    return true;
                }

                if (element == MarkerElement.CONST_LIMIT_WN) {
                    float f7 = -1.5F + -yaw / 10.0F;
                    f7 = f7 > -0.49F ? -0.49F : f7;
                    railmap.getStartRP().constLimitWN = railmap.getEndRP().constLimitWN = f7;
                    // marker.onChangeRailShape();
                    return true;
                }
            }
            if (marker.getState(MarkerState.LINE1)) {
                float ADD_FIX = 1.6f;
                int height = (int) Math.floor(NGTUtilClient.getMinecraft().player.posY) + 1;
                int length = (int) Math.floor((NGTUtilClient.getMinecraft().gameSettings.renderDistanceChunks * 16.0D) * (double) MathHelper.SQRT_2) + 1;
                int distance = (int) Math.sqrt(height * height + length * length);
                RayTraceResult raytraceresult = WorldUtils.getMOPFromPlayer(minecraft.player, (distance > 128.0D) ? ADD_FIX * distance : 128.0D, true);
                if (raytraceresult == null || raytraceresult.typeOfHit != Type.BLOCK) {
                    return false;
                }

                Vec3d vec3d = raytraceresult.hitVec;
                boolean flag = false;
                RailPosition railposition1 = this.getOppositeRail(marker);
                if (railposition1 != null) {
                    double d0 = NGTMath.getDistanceSq(vec3d.x, vec3d.z, railposition1.posX, railposition1.posZ);
                    if (d0 <= 4.0D) {
                        vec3d = new Vec3d(railposition1.posX, railposition1.posY, railposition1.posZ);
                        flag = true;
                    }
                }

                if (marker.getState(MarkerState.ANCHOR21)) {
                    double d5 = 0.6666666666666666D;
                    double d1 = (vec3d.x - railposition.posX) * d5 + railposition.posX;
                    double d2 = (vec3d.y - railposition.posY) * d5 + railposition.posY;
                    double d3 = (vec3d.z - railposition.posZ) * d5 + railposition.posZ;
                    vec3d = new Vec3d(d1, d2, d3);
                }

                double d6 = vec3d.x - railposition.posX;
                double d7 = vec3d.z - railposition.posZ;
                if (d6 != 0.0D && d7 != 0.0D) {
                    RailPosition railposition3 = this.getNeighborRail(marker);
                    float f2 = (float) Math.atan2(d6, d7);
                    float f13 = (float) (d6 / (double) MathHelper.sin(f2));
                    float f3 = NGTMath.toDegrees(f2);
                    if (element == MarkerElement.HORIZONTIAL) {
                        if (railposition3 != null) {
                            f3 = MathHelper.wrapDegrees(railposition3.anchorYaw + 180.0F);
                        }

                        railposition.anchorYaw = f3;
                        railposition.anchorLengthHorizontal = f13;
                    } else if (element == MarkerElement.VERTICAL) {
                        float f4 = MathHelper.wrapDegrees(f3 - railposition.anchorYaw);
                        if (railposition3 != null) {
                            f4 = -railposition3.anchorPitch;
                        } else if (flag) {
                            double d4 = vec3d.y - railposition.posY;
                            f4 = (float) NGTMath.toDegrees(Math.atan2(d4, NGTMath.firstSqrt(d6 * d6 + d7 * d7)));
                        }

                        railposition.anchorPitch = f4;
                        railposition.anchorLengthVertical = f13;
                    }

                    // marker.onChangeRailShape();
                    return true;
                }
            }

            return false;
        } else {
            return false;
        }
    }

    private RailPosition getOppositeRail(TileEntityMarkerAdvanced tileEntity) {
        if (tileEntity.getPrevRailMaps() == null) {
            return null;
        } else {
            RailPosition railposition = tileEntity.getMarkerRP();
            RailPosition railposition1 = null;

            for (RailMap railmap : tileEntity.getPrevRailMaps()) {
                if (railmap.getStartRP().equals(railposition)) {
                    railposition1 = railmap.getEndRP();
                    break;
                }

                if (railmap.getEndRP().equals(railposition)) {
                    railposition1 = railmap.getStartRP();
                    break;
                }
            }

            return railposition1;
        }
    }

    private RailPosition getNeighborRail(TileEntityMarkerAdvanced tileEntity) {
        int i = tileEntity.getMarkerRP().direction;
        BlockPos blockpos = tileEntity.getMarkerRP().getNeighborBlockPos();
        TileEntity tileentity = tileEntity.getWorld().getTileEntity(blockpos);
        if (!(tileentity instanceof TileEntityLargeRailBase)) {
            return null;
        } else {
            TileEntityLargeRailCore tileentitylargerailcore = ((TileEntityLargeRailBase) tileentity).getRailCore();
            if (tileentitylargerailcore == null) {
                return null;
            } else {
                double d0 = Double.MAX_VALUE;
                RailPosition railposition = null;

                for (RailMap railmap : tileentitylargerailcore.getAllRailMaps()) {
                    double d1 = NGTMath.getDistanceSq(tileEntity.getMarkerRP().posX, tileEntity.getMarkerRP().posZ, railmap.getStartRP().posX, railmap.getStartRP().posZ);
                    if (d1 < d0) {
                        d0 = d1;
                        railposition = railmap.getStartRP();
                    }

                    d1 = NGTMath.getDistanceSq(tileEntity.getMarkerRP().posX, tileEntity.getMarkerRP().posZ, railmap.getEndRP().posX, railmap.getEndRP().posZ);
                    if (d1 < d0) {
                        d0 = d1;
                        railposition = railmap.getEndRP();
                    }
                }

                return railposition;
            }
        }
    }

    public enum MarkerElement {
        NONE(0),
        HORIZONTIAL(65312),
        VERTICAL(16746496),
        CANT_EDGE(16711935),
        CANT_CENTER(16711935),
        HEIGHT(16715776),
        CONST_LIMIT_HP(1073407),
        CONST_LIMIT_HN(1073407),
        CONST_LIMIT_WP(1073407),
        CONST_LIMIT_WN(1073407);

        public final int color;

        private MarkerElement(int par2) {
            this.color = par2;
        }

        public int getColor() {
            return this.color;
        }
    }

}
