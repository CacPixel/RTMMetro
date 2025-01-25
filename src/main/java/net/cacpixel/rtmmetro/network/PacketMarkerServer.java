package net.cacpixel.rtmmetro.network;

import io.netty.buffer.ByteBuf;
import jp.ngt.ngtlib.network.PacketCustom;
import jp.ngt.ngtlib.util.NGTUtil;
import jp.ngt.rtm.rail.util.RailPosition;
import net.cacpixel.rtmmetro.rail.tileentity.TileEntityMarkerAdvanced;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketMarkerServer extends PacketCustom implements IMessage, IMessageHandler<PacketMarkerServer, IMessage>
{
    private NBTTagCompound markerTag;

    public PacketMarkerServer()
    {
        super();
    }

    public PacketMarkerServer(TileEntityMarkerAdvanced marker, RailPosition rp)
    {
        super(marker);
        this.markerTag = marker.writeToNBT(new NBTTagCompound());
    }

    @Override
    public void toBytes(ByteBuf buffer)
    {
        super.toBytes(buffer);
        ByteBufUtils.writeTag(buffer, this.markerTag);
    }

    @Override
    public void fromBytes(ByteBuf buffer)
    {
        super.fromBytes(buffer);
        this.markerTag = ByteBufUtils.readTag(buffer);
    }

    @Override
    public IMessage onMessage(PacketMarkerServer message, MessageContext ctx)
    {
        World world = NGTUtil.getClientWorld();
        TileEntity tile = message.getTileEntity(world);
        if (tile instanceof TileEntityMarkerAdvanced)
        {
            TileEntityMarkerAdvanced marker = (TileEntityMarkerAdvanced) tile;
            marker.readFromNBT(message.markerTag);
        }
        return null;
    }
}
