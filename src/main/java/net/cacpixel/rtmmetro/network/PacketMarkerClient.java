package net.cacpixel.rtmmetro.network;

import io.netty.buffer.ByteBuf;
import jp.ngt.ngtlib.block.BlockUtil;
import jp.ngt.ngtlib.network.PacketCustom;
import jp.ngt.rtm.rail.util.RailPosition;
import net.cacpixel.rtmmetro.RTMMetroBlock;
import net.cacpixel.rtmmetro.rail.tileentity.TileEntityMarkerAdvanced;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * 修改自原先的 PacketMarkerRPClient
 */
public class PacketMarkerClient extends PacketCustom implements IMessage, IMessageHandler<PacketMarkerClient, IMessage>
{
    private NBTTagCompound markerTag;
    private RailPosition[] railPositions;

    public PacketMarkerClient()
    {
    }

    public PacketMarkerClient(TileEntityMarkerAdvanced marker)
    {
        super(marker);
        this.markerTag = marker.writeToNBT(new NBTTagCompound());
        this.railPositions = marker.getAllRP();
    }

    public void toBytes(ByteBuf buffer)
    {
        super.toBytes(buffer);
        ByteBufUtils.writeTag(buffer, this.markerTag);

        buffer.writeByte(this.railPositions.length);
        for (RailPosition railposition : this.railPositions)
        {
            ByteBufUtils.writeTag(buffer, railposition.writeToNBT());
        }
    }

    public void fromBytes(ByteBuf buffer)
    {
        super.fromBytes(buffer);
        this.markerTag = ByteBufUtils.readTag(buffer);

        byte b0 = buffer.readByte();
        if (b0 > 0)
        {
            this.railPositions = new RailPosition[b0];
            for (int i = 0; i < b0; ++i)
            {
                NBTTagCompound nbttagcompound = ByteBufUtils.readTag(buffer);
                if (nbttagcompound != null)
                {
                    this.railPositions[i] = RailPosition.readFromNBT(nbttagcompound);
                }
            }
        }
    }

    public IMessage onMessage(PacketMarkerClient message, MessageContext ctx)
    {
        World world = ctx.getServerHandler().player.world;
        TileEntity te = message.getTileEntity(world);
        // 如果不是coreMarker，railPositions只包含自身的rp
        for (RailPosition railposition : message.railPositions)
        {
            TileEntity tileentity = BlockUtil.getTileEntity(world, railposition.blockX, railposition.blockY, railposition.blockZ);
            if (tileentity instanceof TileEntityMarkerAdvanced)
            {
                TileEntityMarkerAdvanced marker = (TileEntityMarkerAdvanced) tileentity;
                marker.setMarkerRP(railposition);
                marker.shouldUpdateClientLines = true;
            }
        }
        if (te instanceof TileEntityMarkerAdvanced)
        {
            TileEntityMarkerAdvanced marker = (TileEntityMarkerAdvanced) te;
            marker.readFromNBT(message.markerTag);
            if (marker.isCoreMarker() || marker.getRailMaps() == null || marker.getRailMaps().length < 1)
            {
                RTMMetroBlock.MARKER_ADVANCED.onMarkerActivated(world, marker.getX(), marker.getY(), marker.getZ(),
                        ctx.getServerHandler().player, false);
            }
        }
        return null;
    }
}