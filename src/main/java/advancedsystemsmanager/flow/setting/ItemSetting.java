package advancedsystemsmanager.flow.setting;


import advancedsystemsmanager.helpers.Localization;
import advancedsystemsmanager.flow.menus.MenuItem;
import advancedsystemsmanager.network.DataBitHelper;
import advancedsystemsmanager.network.DataReader;
import advancedsystemsmanager.network.DataWriter;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.List;

public class ItemSetting extends Setting<ItemStack>
{
    public FuzzyMode fuzzyMode;
    public ItemStack item;

    public ItemSetting(int id)
    {
        super(id);
    }

    @Override
    public List<String> getMouseOver()
    {
        if (item != null && GuiScreen.isShiftKeyDown())
        {
            return MenuItem.getToolTip(item);
        }

        List<String> ret = new ArrayList<String>();

        if (item == null)
        {
            ret.add(Localization.NO_ITEM_SELECTED.toString());
        } else
        {
            ret.add(MenuItem.getDisplayName(item));
        }

        ret.add("");
        ret.add(Localization.CHANGE_ITEM.toString());
        if (item != null)
        {
            ret.add(Localization.EDIT_SETTING.toString());
            ret.add(Localization.FULL_DESCRIPTION.toString());
        }

        return ret;
    }

    @Override
    public void clear()
    {
        super.clear();

        fuzzyMode = FuzzyMode.PRECISE;
        item = null;
    }

    @Override
    public int getAmount()
    {
        return item == null ? 0 : item.stackSize;
    }

    @Override
    public void setAmount(int val)
    {
        if (item != null)
        {
            item.stackSize = val;
        }
    }

    @Override
    public boolean isValid()
    {
        return item != null;
    }

    public FuzzyMode getFuzzyMode()
    {
        return fuzzyMode;
    }

    public void setFuzzyMode(FuzzyMode fuzzy)
    {
        this.fuzzyMode = fuzzy;
    }

    public ItemStack getItem()
    {
        return item;
    }

    @Override
    public void writeData(DataWriter dw)
    {
        dw.writeData(Item.getIdFromItem(item.getItem()), DataBitHelper.MENU_ITEM_ID);
        dw.writeData(fuzzyMode.ordinal(), DataBitHelper.FUZZY_MODE);
        dw.writeData(item.getItemDamage(), DataBitHelper.MENU_ITEM_META);
        dw.writeNBT(item.getTagCompound());
    }

    @Override
    public void readData(DataReader dr)
    {
        int id = dr.readData(DataBitHelper.MENU_ITEM_ID);
        fuzzyMode = FuzzyMode.values()[dr.readData(DataBitHelper.FUZZY_MODE)];
        int meta = dr.readData(DataBitHelper.MENU_ITEM_META);
        item = new ItemStack(Item.getItemById(id), 1, meta);
        item.setTagCompound(dr.readNBT());
    }

    @Override
    public void copyFrom(Setting setting)
    {
        item = ((ItemSetting)setting).getItem().copy();
        fuzzyMode = ((ItemSetting)setting).fuzzyMode;
    }

    @Override
    public int getDefaultAmount()
    {
        return 1;
    }

    public static final String NBT_SETTING_FUZZY = "FuzzyMode";

    @Override
    public void load(NBTTagCompound settingTag)
    {
        item = ItemStack.loadItemStackFromNBT(settingTag);
        fuzzyMode = FuzzyMode.values()[settingTag.getByte(NBT_SETTING_FUZZY)];
    }

    @Override
    public void save(NBTTagCompound settingTag)
    {
        if (item != null) item.writeToNBT(settingTag);
        settingTag.setByte(NBT_SETTING_FUZZY, (byte)fuzzyMode.ordinal());
    }

    @Override
    public boolean isContentEqual(Setting otherSetting)
    {
        return Item.getIdFromItem(item.getItem()) == Item.getIdFromItem(((ItemSetting)otherSetting).item.getItem()) && ItemStack.areItemStackTagsEqual(item, ((ItemSetting)otherSetting).item);
    }

    @Override
    public void setContent(Object obj)
    {
        item = ((ItemStack)obj).copy();
    }

    @Override
    public boolean isContentEqual(ItemStack check)
    {
        return isEqualForCommandExecutor(check);
    }

    public void setItem(ItemStack item)
    {
        this.item = item;
    }

    public boolean isEqualForCommandExecutor(ItemStack other)
    {
        if (!isValid() || other == null)
        {
            return false;
        } else
        {
            switch (fuzzyMode)
            {
                case ORE_DICTIONARY:
                    int[] ids = OreDictionary.getOreIDs(this.getItem());
                    if (ids.length > 0)
                    {
                        int[] otherIds = OreDictionary.getOreIDs(other);
                        for (int id : ids)
                        {
                            for (int oId : otherIds)
                            {
                                if (id == oId) return true;
                            }
                        }
                    }
                    //note that this falls through into the precise one, this is on purpose
                case PRECISE:
                    return this.getItem().getItem() == other.getItem() && this.getItem().getItemDamage() == other.getItemDamage() && ItemStack.areItemStackTagsEqual(getItem(), other);
                case NBT_FUZZY:
                    return this.getItem().getItem() == other.getItem() && this.getItem().getItemDamage() == other.getItemDamage();
                case FUZZY:
                    return this.getItem().getItem() == other.getItem();
                case MOD_GROUPING:
                    return ModItemHelper.areItemsFromSameMod(this.getItem().getItem(), other.getItem());
                case ALL:
                    return true;
                default:
                    return false;
            }
        }
    }

    public boolean canChangeMetaData()
    {
        return true;
    }
}
