package net.cacpixel.rtmmetro.client.gui;

import jp.ngt.ngtlib.block.BlockUtil;
import net.cacpixel.rtmmetro.client.gui.screens.GuiMarkerAdvanced;
import net.cacpixel.rtmmetro.client.gui.screens.GuiRailAdvanced;
import net.cacpixel.rtmmetro.client.gui.screens.GuiRigidCatenarySettings;
import net.cacpixel.rtmmetro.rail.tileentity.TileEntityMarkerAdvanced;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class RTMMetroGUIHandler implements IGuiHandler
{
    public static short guiIdRigidCatenary = getNextGuiID();
    public static short guiIdMarkerAdvanced = getNextGuiID();
    public static short guiIdRailAdvanced = getNextGuiID();
    private static short guiId;

    private static short getNextGuiID()
    {
        return guiId++;
    }

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
            if (ID == guiIdRigidCatenary)
            {
                return new GuiRigidCatenarySettings(player, world, x, y, z);
            }
            else if (ID == guiIdRailAdvanced)
            {
                return new GuiRailAdvanced();
            }
            else if (ID == guiIdMarkerAdvanced)
            {
                return new GuiMarkerAdvanced((TileEntityMarkerAdvanced) BlockUtil.getTileEntity(world, x, y, z));
            }
        }
        catch (ClassCastException e)
        {
            e.printStackTrace();
        }
        return null;
    }
}
