package net.cacpixel.rtmmetro.rail.block;

import jp.ngt.rtm.rail.TileEntityLargeRailSwitchBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockLargeRailSwitchBaseAdvanced extends BlockLargeRailBaseAdvanced {

    public BlockLargeRailSwitchBaseAdvanced(){
        super();
    }

    @Override
    public TileEntity createNewTileEntity(World world, int par2)
    {
        return new TileEntityLargeRailSwitchBase();
    }

}
