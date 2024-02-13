package net.cacpixel.rtmmetro.modelpack.modelset;

import jp.ngt.rtm.entity.util.CollisionObj;
import jp.ngt.rtm.modelpack.ModelPackManager;
import jp.ngt.rtm.modelpack.modelset.ModelSetBase;
import jp.ngt.rtm.render.ModelObject;
import jp.ngt.rtm.render.PartsRenderer;
import net.cacpixel.rtmmetro.modelpack.config.RigidCatenaryConfig;
import net.cacpixel.rtmmetro.render.RigidCatenaryRenderer;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModelSetRigidCatenary extends ModelSetBase<RigidCatenaryConfig> {

    public ModelSetRigidCatenary() {
    }

    public ModelSetRigidCatenary(RigidCatenaryConfig cfg) {
        super(cfg);
    }

    @SideOnly(Side.CLIENT)
    public void constructOnClient() {
        super.constructOnClient();
        if (this.isDummy()) {
            this.modelObj = ModelObject.getDummy();
            this.buttonTexture = ModelPackManager.INSTANCE.getResource("textures/signal/button_4cB.png");
        } else {
            RigidCatenaryConfig cfg = this.getConfig();
            PartsRenderer renderer = new RigidCatenaryRenderer();
            this.modelObj = new ModelObject(cfg.model, this, renderer);
            this.buttonTexture = ModelPackManager.INSTANCE.getResource(cfg.buttonTexture);
        }

    }

    public RigidCatenaryConfig getDummyConfig() {
        return RigidCatenaryConfig.getDummy();
    }

    @SideOnly(Side.CLIENT)
    public void renderModelInGui(Minecraft par1) {
//        ModelObject mo = this.modelObj;
//        NGTUtilClient.bindTexture(mo.textures[0].material.texture);
//        mo.model.renderAll(false);
    }

    @Override
    public CollisionObj getCollisionObj() {
        return null;
    }


}
