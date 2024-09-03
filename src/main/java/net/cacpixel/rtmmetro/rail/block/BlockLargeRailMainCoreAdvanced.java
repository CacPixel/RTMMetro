package net.cacpixel.rtmmetro.rail.block;

import net.cacpixel.rtmmetro.rail.tileentity.TileEntityLargeRailMainCoreAdvanced;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockLargeRailMainCoreAdvanced extends BlockLargeRailBaseAdvanced
{

    public BlockLargeRailMainCoreAdvanced()
    {
        super();
    }

    @Override
    public TileEntity createNewTileEntity(World world, int par2)
    {
        return new TileEntityLargeRailMainCoreAdvanced();
    }

}
