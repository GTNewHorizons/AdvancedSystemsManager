package advancedsystemsmanager.tileentities;

import advancedsystemsmanager.api.tileentities.ISystemListener;
import advancedsystemsmanager.api.tileentities.ITriggerNode;
import advancedsystemsmanager.api.tileentities.IBUDListener;
import advancedsystemsmanager.tileentities.manager.TileEntityManager;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.ArrayList;
import java.util.List;

public class TileEntityBUD extends TileEntityElementBase implements ISystemListener, ITriggerNode, IBUDListener
{
    private static final String NBT_SIDES = "Sides";
    private static final String NBT_DATA = "Data";
    private List<TileEntityManager> managerList = new ArrayList<TileEntityManager>();
    private int[] oldData = new int[ForgeDirection.VALID_DIRECTIONS.length];
    private int[] data = new int[ForgeDirection.VALID_DIRECTIONS.length];

    @Override
    public void added(TileEntityManager owner)
    {
        if (!managerList.contains(owner))
        {
            managerList.add(owner);
        }
    }

    @Override
    public void removed(TileEntityManager owner)
    {
        managerList.remove(owner);
    }



    public void onTrigger()
    {
        updateData();

        for (int i = managerList.size() - 1; i >= 0; i--)
        {
            managerList.get(i).triggerBUD(this);
        }


        makeOld();
    }

    public void updateData()
    {
        if (worldObj != null)
        {
            data = new int[data.length];
            for (int i = 0; i < data.length; i++)
            {
                ForgeDirection direction = ForgeDirection.VALID_DIRECTIONS[i];
                int x = direction.offsetX + this.xCoord;
                int y = direction.offsetY + this.yCoord;
                int z = direction.offsetZ + this.zCoord;

                data[i] = (Block.getIdFromBlock(worldObj.getBlock(x, y, z)) << 4) | (worldObj.getBlockMetadata(x, y, z) & 15);
            }
        }
    }

    public void makeOld()
    {
        oldData = data;
    }

    @Override
    public int[] getData()
    {
        return data;
    }

    @Override
    public int[] getOldData()
    {
        return oldData;
    }

    @Override
    public void writeToTileNBT(NBTTagCompound nbtTagCompound)
    {
        NBTTagList sidesTag = new NBTTagList();
        for (int aData : data)
        {
            NBTTagCompound sideTag = new NBTTagCompound();
            sideTag.setShort(NBT_DATA, (short)aData);
            sidesTag.appendTag(sideTag);
        }


        nbtTagCompound.setTag(NBT_SIDES, sidesTag);
    }

    @Override
    public void readFromTileNBT(NBTTagCompound nbtTagCompound)
    {
        NBTTagList sidesTag = nbtTagCompound.getTagList(NBT_SIDES, 10);
        for (int i = 0; i < sidesTag.tagCount(); i++)
        {

            NBTTagCompound sideTag = sidesTag.getCompoundTagAt(i);

            oldData[i] = data[i] = sideTag.getShort(NBT_DATA);
        }
    }

    @Override
    public void onNeighborBlockChange()
    {
        updateData();
    }
}
