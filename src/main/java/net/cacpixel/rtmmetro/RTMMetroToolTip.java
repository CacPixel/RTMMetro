package net.cacpixel.rtmmetro;

import jp.ngt.ngtlib.util.Usage;

public class RTMMetroToolTip {
    public static void init(){
        Usage.INSTANCE.add(RTMMetroItems.rigidCatenary, -1,
                "usage.item.rigid_catenary_1",
                "usage.item.rigid_catenary_2",
                "usage.item.rigid_catenary_3"
        );

        Usage.INSTANCE.add(RTMMetroItems.railAdvanced, -1,
                "usage.item.rail_advanced_1",
                "usage.item.rail_advanced_2"
        );

    }

}
