package net.cacpixel.rtmmetro;

import jp.ngt.rtm.modelpack.ResourceType;
import net.cacpixel.rtmmetro.modelpack.config.RigidCatenaryConfig;
import net.cacpixel.rtmmetro.modelpack.modelset.ModelSetRigidCatenary;

public class RTMMetroResource
{
    public static ResourceType<RigidCatenaryConfig, ModelSetRigidCatenary> RIGID_CATENARY;


    public static void init()
    {
        RIGID_CATENARY = (new ResourceType<>("ModelRigidCatenary", RigidCatenaryConfig.class,
                ModelSetRigidCatenary.class, false)).setDefault("dc1500v_PiBusBar_Grounded");
    }

}
