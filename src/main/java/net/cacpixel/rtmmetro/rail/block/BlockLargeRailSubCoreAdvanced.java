package net.cacpixel.rtmmetro.rail.block;

import net.cacpixel.rtmmetro.rail.tileentity.TileEntityLargeRailSubCoreAdvanced;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockLargeRailSubCoreAdvanced extends BlockLargeRailBaseAdvanced {

    public BlockLargeRailSubCoreAdvanced(){
        super();
    }

    @Override
    public TileEntity createNewTileEntity(World world, int par2)
    {
        return new TileEntityLargeRailSubCoreAdvanced();
    }

    
}
