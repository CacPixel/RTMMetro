package net.cacpixel.rtmmetro.rail.block;

import net.cacpixel.rtmmetro.rail.tileentity.TileEntityLargeRailSwitchCoreAdvanced;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockLargeRailSwitchCoreAdvanced extends BlockLargeRailBaseAdvanced
{

    public BlockLargeRailSwitchCoreAdvanced()
    {
        super();
    }

    @Override
    public TileEntity createNewTileEntity(World world, int par2)
    {
        return new TileEntityLargeRailSwitchCoreAdvanced();
    }
}
