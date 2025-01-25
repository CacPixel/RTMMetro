package net.cacpixel.rtmmetro.rail.block;

import net.cacpixel.rtmmetro.rail.tileentity.TileEntityLargeRailCoreAdvanced;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockLargeRailCoreAdvanced extends BlockLargeRailBaseAdvanced
{

    public BlockLargeRailCoreAdvanced()
    {
        super();
    }

    @Override
    public TileEntity createNewTileEntity(World world, int par2)
    {
        return new TileEntityLargeRailCoreAdvanced();
    }

}
