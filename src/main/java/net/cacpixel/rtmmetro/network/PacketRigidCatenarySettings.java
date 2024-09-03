package net.cacpixel.rtmmetro.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketRigidCatenarySettings implements IMessage, IMessageHandler<PacketRigidCatenarySettings, IMessage>
{

    @Override
    public IMessage onMessage(PacketRigidCatenarySettings message, MessageContext ctx)
    {
        return null;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {

    }

    @Override
    public void toBytes(ByteBuf buf)
    {

    }
}
