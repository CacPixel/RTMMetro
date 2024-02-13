package net.cacpixel.rtmmetro.client.gui;

import net.cacpixel.rtmmetro.RTMMetro;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class RTMMetroGUIHandler implements IGuiHandler {

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {

        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if (ID == RTMMetro.guiIdRigidCatenary) {
            return new GUIRigidCatenarySettings(player, world, x, y, z);
        } else if(ID == RTMMetro.guiIdRailAdvanced){
            return new GUIRailAdvanced();
        }
        return null;
    }
}
