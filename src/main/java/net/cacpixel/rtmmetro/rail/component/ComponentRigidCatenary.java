package net.cacpixel.rtmmetro.rail.component;

import jp.ngt.rtm.render.TileEntityPartsRenderer;
import net.cacpixel.rtmmetro.modelpack.modelset.ModelSetRigidCatenary;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ComponentRigidCatenary implements IRailComponent {
    public float offsetStart, offsetEnd;
    public EnumOverlapType overlapType;
    public float angleHorizontal, angleVertical;
    @SideOnly(Side.CLIENT)
    public TileEntityPartsRenderer<ModelSetRigidCatenary> renderer;

    public ComponentRigidCatenary() {
        this.offsetStart = 0;
        this.offsetEnd = 0;
        this.overlapType = EnumOverlapType.NONE;
        this.angleHorizontal = 0.0f;
        this.angleVertical = 0.0f;
    }


    public enum EnumOverlapType {
        NONE,
        NORMAL,
        INSULATED,
    }
}
