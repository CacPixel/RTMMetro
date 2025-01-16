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

import java.nio.charset.StandardCharsets;

public class PacketMarkerClient extends PacketCustom implements IMessage, IMessageHandler<PacketMarkerClient, IMessage>
{
    private int groupId;
    private String name;
    private RailPosition[] railPositions;

    public PacketMarkerClient()
    {
    }

    public PacketMarkerClient(TileEntityMarkerAdvanced marker)
    {
        super(marker);
        this.name = marker.getName();
        this.groupId = marker.getGroupId();
        this.railPositions = marker.getAllRP();
    }

    public void toBytes(ByteBuf buffer)
    {
        super.toBytes(buffer);
        buffer.writeInt(this.groupId);
        buffer.writeInt(this.name.length());
        buffer.writeBytes(this.name.getBytes(StandardCharsets.UTF_8));

        buffer.writeByte(this.railPositions.length);

        for (RailPosition railposition : this.railPositions)
        {
            ByteBufUtils.writeTag(buffer, railposition.writeToNBT());
        }

    }

    public void fromBytes(ByteBuf buffer)
    {
        super.fromBytes(buffer);
        this.groupId = buffer.readInt();
        int strlen = buffer.readInt();
        this.name = buffer.readBytes(strlen).toString(0, strlen, StandardCharsets.UTF_8);
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
        TileEntity core = message.getTileEntity(world);
        if (core instanceof TileEntityMarkerAdvanced)
        {
            TileEntityMarkerAdvanced marker = (TileEntityMarkerAdvanced) core;
            marker.setGroupId(message.groupId);
            marker.setName(message.name);
        }

        // each Marker
        // 如果不是coreMarker，railPositions只包含自身的rp
        for (RailPosition railposition : message.railPositions)
        {
            TileEntity tileentity = BlockUtil.getTileEntity(world, railposition.blockX, railposition.blockY,
                    railposition.blockZ);
            if (tileentity instanceof TileEntityMarkerAdvanced)
            {
                TileEntityMarkerAdvanced marker = (TileEntityMarkerAdvanced) tileentity;
                marker.setMarkerRP(railposition);
                marker.shouldUpdateClientLines = true;
            }
        }

        // core Marker
        if (core instanceof TileEntityMarkerAdvanced)
        {
            TileEntityMarkerAdvanced marker = (TileEntityMarkerAdvanced) core;
            RTMMetroBlock.MARKER_ADVANCED.onMarkerActivated(world, marker.getPos().getX(), marker.getPos().getY(),
                    marker.getPos().getZ(), ctx.getServerHandler().player, false);
        }

        return null;
    }
}