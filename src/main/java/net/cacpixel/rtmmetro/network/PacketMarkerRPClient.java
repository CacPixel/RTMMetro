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

public class PacketMarkerRPClient extends PacketCustom implements IMessageHandler<PacketMarkerRPClient, IMessage> {
    private RailPosition[] railPositions;

    public PacketMarkerRPClient() {
    }

    public PacketMarkerRPClient(TileEntityMarkerAdvanced marker) {
        super(marker);
        this.railPositions = marker.getAllRP();
    }

    public void toBytes(ByteBuf buffer) {
        super.toBytes(buffer);
        buffer.writeByte(this.railPositions.length);

        for(RailPosition railposition : this.railPositions) {
            ByteBufUtils.writeTag(buffer, railposition.writeToNBT());
        }

    }

    public void fromBytes(ByteBuf buffer) {
        super.fromBytes(buffer);
        byte b0 = buffer.readByte();
        if (b0 > 0) {
            this.railPositions = new RailPosition[b0];

            for(int i = 0; i < b0; ++i) {
                NBTTagCompound nbttagcompound = ByteBufUtils.readTag(buffer);
                this.railPositions[i] = RailPosition.readFromNBT(nbttagcompound);
            }
        }

    }

    public IMessage onMessage(PacketMarkerRPClient message, MessageContext ctx) {
        World world = ctx.getServerHandler().player.world;

        for(RailPosition railposition : message.railPositions) {
            TileEntity tileentity = BlockUtil.getTileEntity(world, railposition.blockX, railposition.blockY, railposition.blockZ);
            if (tileentity instanceof TileEntityMarkerAdvanced) {
                ((TileEntityMarkerAdvanced)tileentity).setMarkerRP(railposition);
                ((TileEntityMarkerAdvanced)tileentity).shouldUpdateClientLines = true;
//                NGTLog.debug("send Client pack" + tileentity.getPos());
            }
        }

        TileEntity tileentity1 = message.getTileEntity(world);
        if (tileentity1 instanceof TileEntityMarkerAdvanced) {
            TileEntityMarkerAdvanced marker = (TileEntityMarkerAdvanced)tileentity1;
            RTMMetroBlock.MARKER_ADVANCED.onMarkerActivated(world, marker.getPos().getX(), marker.getPos().getY(), marker.getPos().getZ(), ctx.getServerHandler().player, false);
        }

        return null;
    }
}