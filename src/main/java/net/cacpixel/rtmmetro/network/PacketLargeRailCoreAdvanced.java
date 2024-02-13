package net.cacpixel.rtmmetro.network;


import io.netty.buffer.ByteBuf;
import jp.ngt.ngtlib.event.TickProcessEntry;
import jp.ngt.ngtlib.event.TickProcessQueue;
import jp.ngt.ngtlib.network.PacketCustom;
import jp.ngt.ngtlib.util.NGTUtil;
import jp.ngt.rtm.rail.TileEntityLargeRailCore;
import jp.ngt.rtm.rail.TileEntityLargeRailNormalCore;
import jp.ngt.rtm.rail.TileEntityLargeRailSwitchCore;
import jp.ngt.rtm.rail.util.RailPosition;
import jp.ngt.rtm.rail.util.SwitchType;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class PacketLargeRailCoreAdvanced extends PacketCustom implements IMessageHandler<PacketLargeRailCoreAdvanced, IMessage> {
    public static final byte TYPE_NORMAL = 0;
    public static final byte TYPE_SWITCH = 2;
    private byte dataType;
    private int sX;
    private int sY;
    private int sZ;
    private NBTTagCompound property;
    private byte type;
    private RailPosition[] railPositions;

    public PacketLargeRailCoreAdvanced() {
    }

    public PacketLargeRailCoreAdvanced(TileEntityLargeRailCore tile, byte par2Type) {
        super(tile);
        this.dataType = par2Type;
        this.sX = tile.getStartPoint()[0];
        this.sY = tile.getStartPoint()[1];
        this.sZ = tile.getStartPoint()[2];
        NBTTagCompound nbt = new NBTTagCompound();
        tile.writeRailStates(nbt);
        this.property = nbt;
        this.railPositions = tile.getRailPositions();
        switch (par2Type) {
            case 2:
                TileEntityLargeRailSwitchCore tile1 = (TileEntityLargeRailSwitchCore)tile;
                SwitchType st = tile1.getSwitch();
                this.type = st != null ? st.id : -1;
            case 0:
            default:
        }
    }

    public void toBytes(ByteBuf buffer) {
        super.toBytes(buffer);
        buffer.writeByte(this.dataType);
        buffer.writeInt(this.sX);
        buffer.writeInt(this.sY);
        buffer.writeInt(this.sZ);
        ByteBufUtils.writeTag(buffer, this.property);
        buffer.writeByte(this.type);
        buffer.writeByte(this.railPositions.length);
        RailPosition[] var2 = this.railPositions;
        int var3 = var2.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            RailPosition rp = var2[var4];
            ByteBufUtils.writeTag(buffer, rp.writeToNBT());
        }

    }

    public void fromBytes(ByteBuf buffer) {
        super.fromBytes(buffer);
        this.dataType = buffer.readByte();
        this.sX = buffer.readInt();
        this.sY = buffer.readInt();
        this.sZ = buffer.readInt();
        this.property = ByteBufUtils.readTag(buffer);
        this.type = buffer.readByte();
        byte size = buffer.readByte();
        if (size > 0) {
            this.railPositions = new RailPosition[size];

            for(int i = 0; i < size; ++i) {
                NBTTagCompound nbt = ByteBufUtils.readTag(buffer);
                this.railPositions[i] = RailPosition.readFromNBT(nbt);
            }
        }

    }

    public IMessage onMessage(final PacketLargeRailCoreAdvanced message, MessageContext ctx) {
        TickProcessQueue.getInstance(Side.CLIENT).add(new TickProcessEntry() {
            public boolean process(World world) {
                return PacketLargeRailCoreAdvanced.this.processPacket(message);
            }
        }, 50, 5);
        return null;
    }

    public boolean processPacket(PacketLargeRailCoreAdvanced message) {
        World world = NGTUtil.getClientWorld();
        TileEntity tile = message.getTileEntity(world);
        if (!(tile instanceof TileEntityLargeRailCore)) {
            return false;
        } else {
            TileEntityLargeRailCore tile0 = (TileEntityLargeRailCore)tile;
            tile0.setStartPoint(message.sX, message.sY, message.sZ);
            tile0.readRailStates(message.property);
            tile0.setRailPositions(message.railPositions);
            if ((message.dataType != 0 || !(tile instanceof TileEntityLargeRailNormalCore)) && message.dataType == 2 && tile instanceof TileEntityLargeRailSwitchCore) {
                TileEntityLargeRailSwitchCore var5 = (TileEntityLargeRailSwitchCore)tile;
            }

            tile0.updateResourceState();
            return true;
        }
    }
}

