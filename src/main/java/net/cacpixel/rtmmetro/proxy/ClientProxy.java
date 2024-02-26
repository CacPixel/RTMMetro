package net.cacpixel.rtmmetro.proxy;

import jp.ngt.ngtlib.util.NGTUtil;
import net.cacpixel.rtmmetro.RTMMetroBlock;
import net.cacpixel.rtmmetro.RTMMetroItems;
import net.cacpixel.rtmmetro.rail.util.MarkerManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {
    private final MarkerManager mmClient = new MarkerManager(true);

    public ClientProxy() {
    }

    @Override
    public void preInit() {
        RTMMetroBlock.initClient();
        RTMMetroItems.initClient();
    }

    @Override
    public void init() {
        RTMMetroBlock.initClient2();
        RTMMetroItems.initClient2();
    }

    @Override
    public void complete() {
    }

    @Override
    public MarkerManager getMarkerManager() {
        return NGTUtil.isServer() ? super.getMarkerManager() : this.mmClient;
    }

}
