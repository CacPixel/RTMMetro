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

/**
 * 注意：原先RTM里面PacketMarker不再使用，这个类我们可完全自定义，玩家点击完毕标记后服务端向客户端发送此数据包同步Line的位置
 * 这里应该只需要把RailPosition封装进去即可
 */
public class PacketMarkerRPServer extends PacketCustom
        implements IMessage, IMessageHandler<PacketMarkerRPServer, IMessage>
{
    private NBTTagCompound railPosition;

    public PacketMarkerRPServer()
    {
        super();
    }

    public PacketMarkerRPServer(TileEntityMarkerAdvanced marker, RailPosition rp)
    {
        super(marker);
        this.railPosition = rp.writeToNBT();
    }

    @Override
    public void toBytes(ByteBuf buffer)
    {
        super.toBytes(buffer);
        ByteBufUtils.writeTag(buffer, this.railPosition);
    }

    @Override
    public void fromBytes(ByteBuf buffer)
    {
        super.fromBytes(buffer);
        this.railPosition = ByteBufUtils.readTag(buffer);
    }

    @Override
    public IMessage onMessage(PacketMarkerRPServer message, MessageContext ctx)
    {
        World world = NGTUtil.getClientWorld();
        TileEntity tile = message.getTileEntity(world);
        if (tile instanceof TileEntityMarkerAdvanced)
        {
            TileEntityMarkerAdvanced marker = (TileEntityMarkerAdvanced) tile;
            marker.rp = RailPosition.readFromNBT(message.railPosition);
//            NGTLog.debug("receive" + marker.getPos());
        }
        return null;
    }
}
