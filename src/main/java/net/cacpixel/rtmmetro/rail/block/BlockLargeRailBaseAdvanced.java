package net.cacpixel.rtmmetro.rail.block;

import jp.ngt.rtm.rail.BlockLargeRailBase;
import jp.ngt.rtm.rail.TileEntityLargeRailBase;
import jp.ngt.rtm.rail.TileEntityLargeRailCore;
import jp.ngt.rtm.rail.util.RailMap;
import net.cacpixel.rtmmetro.items.ItemRailAdvanced;
import net.cacpixel.rtmmetro.rail.tileentity.TileEntityLargeRailSubCoreAdvanced;
import net.cacpixel.rtmmetro.rail.tileentity.TileEntityLargeRailSwitchCoreAdvanced;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class BlockLargeRailBaseAdvanced extends BlockLargeRailBase
{
    public BlockLargeRailBaseAdvanced()
    {
        super();
        this.setBlockUnbreakable();
        this.setResistance(6000001.0F);
        this.disableStats();
        this.setLightOpacity(0);
        this.setSoundType(SoundType.METAL);
        this.setAABB(new AxisAlignedBB(0.0F, 0.0F, 0.0F, 1.0F, THICKNESS, 1.0F));
    }

    @Override
    public TileEntity createNewTileEntity(World world, int par2)
    {
        return new TileEntityLargeRailBase();
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state)
    {
        TileEntityLargeRailCore core1 = getCore(world, pos);
        if (core1 instanceof TileEntityLargeRailSubCoreAdvanced)
        {
            TileEntityLargeRailSubCoreAdvanced core = ((TileEntityLargeRailSubCoreAdvanced) core1);
            if (!world.isRemote && !core.isRailBreaking)
            {
                core.isRailBreaking = true;
                RailMap[] railmaps = core.getAllRailMaps();
                if (railmaps != null)
                {
                    for (RailMap rm : railmaps)
                    {
                        rm.breakRail(world, core.getResourceState(), core);
                        super.breakBlock(world, pos, state);
                    }
                }
            }
        }
        else if (core1 instanceof TileEntityLargeRailSwitchCoreAdvanced)
        {
            TileEntityLargeRailSwitchCoreAdvanced core = ((TileEntityLargeRailSwitchCoreAdvanced) core1);
            if (!world.isRemote && !core.isRailBreaking)
            {
                core.isRailBreaking = true;
                RailMap[] railmaps = core.getAllRailMaps();
                if (railmaps != null)
                {
                    for (RailMap rm : railmaps)
                    {
                        rm.breakRail(world, core.getResourceState(), core);
                        super.breakBlock(world, pos, state);
                    }
                }
            }
        }
        else
        {
            super.breakBlock(world, pos, state);
        }
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos,
                                  EntityPlayer player)
    {
        TileEntityLargeRailCore core = this.getCore(world, pos);
        if (core != null)
        {
            return ItemRailAdvanced.copyItemFromRail(core);
        }
        //return null;
        return new ItemStack(Items.AIR, 0);
    }

}
