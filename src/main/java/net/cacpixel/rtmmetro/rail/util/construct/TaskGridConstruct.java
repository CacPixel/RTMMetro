package net.cacpixel.rtmmetro.rail.util.construct;

import jp.ngt.rtm.modelpack.state.ResourceStateRail;
import net.cacpixel.rtmmetro.rail.util.RailMapAdvanced;

public class TaskGridConstruct extends RailConstructTask {
    public RailMapAdvanced rm;
    public ResourceStateRail prop;
    public int order;

    public TaskGridConstruct(RailMapAdvanced rm, ResourceStateRail prop, int order) {
        super();
        this.rm = rm;
        this.prop = prop;
        this.order = order;
    }

    @Override
    public void runTask() {
        rm.createRailList0(this.prop, this.order);
    }
}
