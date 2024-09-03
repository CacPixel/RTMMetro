package net.cacpixel.rtmmetro.client.gui;

import jp.ngt.ngtlib.block.BlockUtil;
import net.cacpixel.rtmmetro.RTMMetro;
import net.cacpixel.rtmmetro.rail.tileentity.TileEntityMarkerAdvanced;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class RTMMetroGUIHandler implements IGuiHandler
{

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {

        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        try
        {
            if (ID == RTMMetro.guiIdRigidCatenary)
            {
                return new GUIRigidCatenarySettings(player, world, x, y, z);
            }
            else if (ID == RTMMetro.guiIdRailAdvanced)
            {
                return new GUIRailAdvanced();
            }
            else if (ID == RTMMetro.guiIdMarkerAdvanced)
            {
                return new GUIMarkerAdvanced((TileEntityMarkerAdvanced) BlockUtil.getTileEntity(world, x, y, z));
            }
        }
        catch (ClassCastException e)
        {
            e.printStackTrace();
        }
        return null;
    }
}
