package net.cacpixel.rtmmetro.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;
import net.cacpixel.rtmmetro.rail.util.MarkerData;
import net.cacpixel.rtmmetro.rail.util.MarkerManager;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketMarkerData implements IMessage, IMessageHandler<PacketMarkerData, IMessage> {
    private int x;
    private int y;
    private int z;
    private int worldNameLen;
    private ByteBuf worldName;

    private boolean readyToRemove;

    public PacketMarkerData() {
    }

    public PacketMarkerData(MarkerData marker, boolean readyToRemove) {
        this.x = marker.getX();
        this.y = marker.getY();
        this.z = marker.getZ();
        String str = marker.getWorldName();
        this.worldNameLen = str.getBytes(CharsetUtil.UTF_8).length;
        this.worldName = Unpooled.wrappedBuffer(str.getBytes(CharsetUtil.UTF_8));
        this.readyToRemove = readyToRemove;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.x);
        buf.writeInt(this.y);
        buf.writeInt(this.z);
        buf.writeInt(this.worldNameLen);
        buf.writeBytes(this.worldName, this.worldNameLen);
        buf.writeBoolean(this.readyToRemove);

    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.x = buf.readInt();
        this.y = buf.readInt();
        this.z = buf.readInt();
        this.worldNameLen = buf.readInt();
        this.worldName = buf.readBytes(this.worldNameLen);
        this.readyToRemove = buf.readBoolean();
    }

    @Override
    public IMessage onMessage(PacketMarkerData message, MessageContext ctx) {
        if (message.readyToRemove) {
            MarkerManager.getInstance().removeMarker(
                    message.worldName.toString(0, message.worldNameLen, CharsetUtil.UTF_8),
                    new BlockPos(message.x, message.y, message.z
                    ));
        } else {
            MarkerManager.getInstance().createMarker(
                    message.worldName.toString(0, message.worldNameLen, CharsetUtil.UTF_8),
                    new BlockPos(message.x, message.y, message.z
                    ));
        }
        return null;
    }

}
