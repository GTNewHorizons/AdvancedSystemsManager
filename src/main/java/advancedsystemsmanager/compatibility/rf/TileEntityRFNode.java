package advancedsystemsmanager.compatibility.rf;

import advancedsystemsmanager.api.network.IPacketBlock;
import advancedsystemsmanager.network.ASMPacket;
import advancedsystemsmanager.tileentities.TileEntityCluster;
import advancedsystemsmanager.tileentities.TileEntityElementBase;
import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEntityRFNode extends TileEntityElementBase implements IEnergyProvider, IEnergyReceiver, IPacketBlock
{
    private static final int SIDES = 6;
    public static int MAX_BUFFER = 96000;
    private static final String STORED = "Stored";
    private static final String SIDES_TAG = "Sides";
    private short sides = 0xFFF;
    private int stored;

    @Override
    public void updateEntity()
    {
        super.updateEntity();
        if (!worldObj.isRemote)
        {
            for (int i = 0; i < SIDES; i++)
            {
                ForgeDirection dir = ForgeDirection.getOrientation(i);
                TileEntity te = getTileEntity(dir);
                if (te != null && !(te instanceof TileEntityRFNode || te instanceof TileEntityCluster))
                {
                    if (isInput(i) && te instanceof IEnergyProvider)
                    {
                        int amount = ((IEnergyProvider) te).extractEnergy(dir.getOpposite(), MAX_BUFFER - stored, false);
                        this.receiveEnergy(dir, amount, false);
                    }
                    if (isOutput(i) && te instanceof IEnergyReceiver)
                    {
                        int amount = ((IEnergyReceiver) te).receiveEnergy(dir.getOpposite(), stored, false);
                        this.extractEnergy(dir, amount, false);
                    }
                }
            }
        }
    }

    private TileEntity getTileEntity(ForgeDirection dir)
    {
        return worldObj.getTileEntity(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ);
    }

    public void cycleSide(int side)
    {
        side *= 2;
        int cur = sides >> side & 3;
        cur += 3;
        cur %= 4;
        sides &= ~(3 << side);
        sides |= cur << side;
        markForRenderUpdate();
    }

    public int getIconIndex(int side)
    {
        return (sides >> side * 2) & 3;
    }

    @Override
    public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate)
    {
        if (isInput(from.ordinal()))
        {
            int toReceive = Math.min(maxReceive, MAX_BUFFER - stored);
            if (!simulate) stored += toReceive;
            return toReceive;
        }
        return 0;
    }

    @Override
    public void writeToTileNBT(NBTTagCompound tagCompound)
    {
        tagCompound.setInteger(STORED, stored);
        tagCompound.setShort(SIDES_TAG, sides);
    }

    @Override
    public void readFromTileNBT(NBTTagCompound tagCompound)
    {
        stored = tagCompound.getInteger(STORED);
        sides = tagCompound.getShort(SIDES_TAG);
    }

    @Override
    public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate)
    {
        if (isOutput(from.ordinal()))
        {
            int toExtract = Math.min(maxExtract, stored);
            if (!simulate) stored -= toExtract;
            return toExtract;
        }
        return 0;
    }

    @Override
    public int getEnergyStored(ForgeDirection from)
    {
        return stored;
    }

    @Override
    public int getMaxEnergyStored(ForgeDirection from)
    {
        return MAX_BUFFER;
    }

    @Override
    public boolean canConnectEnergy(ForgeDirection from)
    {
        return (sides & (3 << from.ordinal() * 2)) != 0;
    }

    public boolean isInput(int side)
    {
        return (sides & (2 << side * 2)) != 0;
    }

    public boolean isOutput(int side)
    {
        return (sides & (1 << side * 2)) != 0;
    }

    @Override
    public void writeClientSyncData(ASMPacket packet)
    {
        super.writeClientSyncData(packet);
        packet.writeShort(sides);
    }

    @Override
    public void readClientSyncData(ASMPacket packet)
    {
        super.readClientSyncData(packet);
        sides = packet.readShort();
    }
}
