package net.cacpixel.rtmmetro.proxy;

import jp.ngt.ngtlib.util.NGTUtil;
import jp.ngt.ngtlib.util.NGTUtilClient;
import net.cacpixel.rtmmetro.RTMMetroBlock;
import net.cacpixel.rtmmetro.RTMMetroItems;
import net.cacpixel.rtmmetro.client.gui.CacGuiUtils;
import net.cacpixel.rtmmetro.client.rtmtoolbox.RTMToolBox;
import net.cacpixel.rtmmetro.rail.util.MarkerManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy
{
    private final MarkerManager mmClient = new MarkerManager(true);

    public ClientProxy()
    {
    }

    @Override
    public void preInit()
    {
        RTMToolBox.getInstance().init();
        CacGuiUtils.init();
        RTMMetroBlock.initClient();
        RTMMetroItems.initClient();
    }

    @Override
    public void init()
    {
        RTMMetroBlock.initClient2();
        RTMMetroItems.initClient2();
    }

    @Override
    public void complete()
    {
    }

    @Override
    public MarkerManager getMarkerManager()
    {
        return NGTUtil.isServer() ? super.getMarkerManager() : this.mmClient;
    }

    @Override
    public int getViewDistance()
    {
        return NGTUtilClient.getMinecraft().gameSettings.renderDistanceChunks;
    }
}
