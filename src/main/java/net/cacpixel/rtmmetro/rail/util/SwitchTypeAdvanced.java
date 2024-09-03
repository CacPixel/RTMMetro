package net.cacpixel.rtmmetro.rail.util;

import jp.ngt.rtm.rail.util.RailMap;
import jp.ngt.rtm.rail.util.SwitchType;
import net.minecraft.entity.Entity;

public abstract class SwitchTypeAdvanced extends SwitchType
{
    protected SwitchTypeAdvanced(int par1)
    {
        super(par1);
    }

    /**
     * 单开道岔
     */
    public static class SwitchBasicAdvanced extends SwitchType.SwitchBasic
    {
        protected RailMapSwitchAdvanced[] railMapsAdvanced;

        public RailMap getRailMap(Entity entity)
        {
            return this.points[0].getActiveRailMap(entity.world);
        }

        public String getName()
        {
            return "Simple";
        }
    }

    /**
     * 单渡线
     */
    public static class SwitchSingleCrossAdvanced extends SwitchType.SwitchSingleCross
    {
    }

    /**
     * 交叉渡线
     */
    public static class SwitchScissorsCrossAdvanced extends SwitchType.SwitchScissorsCross
    {
    }

    /**
     * 十字交叉（不是道岔）
     */
    public static class SwitchDiamondCrossAdvanced extends SwitchType.SwitchDiamondCross
    {
    }

    /* 以下是新增道岔 */

    /**
     * 三开道岔 (To-do)
     */
    public static class SwitchThreeThrowTurnout
    {
    }


}
