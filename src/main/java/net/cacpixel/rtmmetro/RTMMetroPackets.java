package net.cacpixel.rtmmetro;

import net.cacpixel.rtmmetro.network.PacketMarkerClient;
import net.cacpixel.rtmmetro.network.PacketMarkerData;
import net.cacpixel.rtmmetro.network.PacketMarkerServer;
import net.cacpixel.rtmmetro.network.PacketRigidCatenarySettings;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.relauncher.Side;

public final class RTMMetroPackets
{

    private static short packetID;

    public static void init()
    {
        registerPacket(PacketMarkerServer.class, PacketMarkerServer.class, Side.CLIENT); // 这里和NGT原本的传输方向不一样
        registerPacket(PacketMarkerClient.class, PacketMarkerClient.class, Side.SERVER);
        registerPacket(PacketRigidCatenarySettings.class, PacketRigidCatenarySettings.class, Side.SERVER);
        registerPacket(PacketMarkerData.class, PacketMarkerData.class, Side.CLIENT);
    }

    public static <REQ extends IMessage, REPLY extends IMessage> void registerPacket(
            Class<? extends IMessageHandler<REQ, REPLY>> messageHandler,
            Class<REQ> requestMessageType,
            Side sideSendTo)
    {
        RTMMetro.NETWORK_WRAPPER.registerMessage(messageHandler, requestMessageType, packetID++, sideSendTo);
    }

}
