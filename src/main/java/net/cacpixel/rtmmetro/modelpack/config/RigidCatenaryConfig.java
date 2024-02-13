package net.cacpixel.rtmmetro.modelpack.config;

import jp.ngt.rtm.modelpack.cfg.ModelConfig;

public class RigidCatenaryConfig extends ModelConfig {
    private String rigidCatenaryName;
    public ModelConfig.ModelSource model;

    @Override
    public String getName() {
        return this.rigidCatenaryName;
    }

    public void init() {
        super.init();
        if(this.model == null){
            this.model = new ModelConfig.ModelSource();
            this.model.modelFile = "Model_none";
            this.model.textures = new String[][]{{"default", "textures/rail/button_1067mm_PC.png"}};
            this.model.rendererPath = null;
        }
    }

    public static RigidCatenaryConfig getDummy() {
        RigidCatenaryConfig cfg = new RigidCatenaryConfig();
        cfg.rigidCatenaryName = "dummy";
        return cfg;
    }

}
