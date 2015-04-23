package advancedsystemsmanager.flow.menus;

import advancedsystemsmanager.flow.elements.TextBoxNumber;
import advancedsystemsmanager.flow.elements.TextBoxNumberList;
import advancedsystemsmanager.helpers.Localization;
import advancedsystemsmanager.gui.ContainerManager;
import advancedsystemsmanager.gui.GuiManager;
import advancedsystemsmanager.flow.elements.CheckBox;
import advancedsystemsmanager.flow.elements.CheckBoxList;
import advancedsystemsmanager.registry.ConnectionSet;
import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.network.DataBitHelper;
import advancedsystemsmanager.network.DataReader;
import advancedsystemsmanager.network.DataWriter;
import advancedsystemsmanager.network.PacketHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagCompound;

import java.util.List;


public class MenuRedstoneStrength extends Menu
{
    public MenuRedstoneStrength(FlowComponent parent)
    {
        super(parent);

        checkBoxes = new CheckBoxList();
        checkBoxes.addCheckBox(new CheckBox(Localization.INVERT_SELECTION, CHECK_BOX_X, CHECK_BOX_Y)
        {

            @Override
            public void setValue(boolean val)
            {
                inverted = val;
            }

            @Override
            public boolean getValue()
            {
                return inverted;
            }

            @Override
            public void onUpdate()
            {
                sendServerData(2);
            }
        });

        textBoxes = new TextBoxNumberList();
        textBoxes.addTextBox(lowTextBox = new TextBoxNumber(TEXT_BOX_X_LEFT, TEXT_BOX_Y, 2, true)
        {
            @Override
            public void onNumberChanged()
            {
                sendServerData(0);
            }

            @Override
            public int getMaxNumber()
            {
                return 15;
            }
        });

        textBoxes.addTextBox(highTextBox = new TextBoxNumber(TEXT_BOX_X_RIGHT, TEXT_BOX_Y, 2, true)
        {
            @Override
            public void onNumberChanged()
            {
                sendServerData(1);
            }

            @Override
            public int getMaxNumber()
            {
                return 15;
            }
        });

        lowTextBox.setNumber(1);
        highTextBox.setNumber(15);
    }

    public CheckBoxList checkBoxes;
    public TextBoxNumberList textBoxes;
    public boolean inverted;
    public TextBoxNumber lowTextBox;
    public TextBoxNumber highTextBox;

    public static final int CHECK_BOX_X = 5;
    public static final int CHECK_BOX_Y = 50;

    public static final int TEXT_BOX_X_LEFT = 10;
    public static final int TEXT_BOX_X_RIGHT = 77;
    public static final int TEXT_BOX_Y = 30;

    public static final int TEXT_BOX_TEXT_X = 46;
    public static final int TEXT_BOX_TEXT_Y = 33;

    public static final int MENU_WIDTH = 120;
    public static final int TEXT_MARGIN_X = 5;
    public static final int TEXT_Y = 5;

    @Override
    public String getName()
    {
        return Localization.REDSTONE_STRENGTH_MENU.toString();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void draw(GuiManager gui, int mX, int mY)
    {
        gui.drawSplitString(Localization.REDSTONE_STRENGTH_INFO.toString(), TEXT_MARGIN_X, TEXT_Y, MENU_WIDTH - 2 * TEXT_MARGIN_X, 0.7F, 0x404040);
        gui.drawString(Localization.THROUGH.toString(), TEXT_BOX_TEXT_X, TEXT_BOX_TEXT_Y, 0.7F, 0x404040);

        checkBoxes.draw(gui, mX, mY);
        textBoxes.draw(gui, mX, mY);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void drawMouseOver(GuiManager gui, int mX, int mY)
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onClick(int mX, int mY, int button)
    {
        checkBoxes.onClick(mX, mY);
        textBoxes.onClick(mX, mY, button);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean onKeyStroke(GuiManager gui, char c, int k)
    {
        return textBoxes.onKeyStroke(gui, c, k);
    }

    @Override
    public void onDrag(int mX, int mY, boolean isMenuOpen)
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onRelease(int mX, int mY, boolean isMenuOpen)
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void writeData(DataWriter dw)
    {
        dw.writeData(lowTextBox.getNumber(), DataBitHelper.MENU_REDSTONE_ANALOG);
        dw.writeData(highTextBox.getNumber(), DataBitHelper.MENU_REDSTONE_ANALOG);
        dw.writeBoolean(inverted);
    }

    @Override
    public void readData(DataReader dr)
    {
        lowTextBox.setNumber(dr.readData(DataBitHelper.MENU_REDSTONE_ANALOG));
        highTextBox.setNumber(dr.readData(DataBitHelper.MENU_REDSTONE_ANALOG));
        inverted = dr.readBoolean();
    }

    @Override
    public void copyFrom(Menu menu)
    {
        MenuRedstoneStrength menuStrength = (MenuRedstoneStrength)menu;

        lowTextBox.setNumber(menuStrength.lowTextBox.getNumber());
        highTextBox.setNumber(menuStrength.highTextBox.getNumber());
        inverted = menuStrength.inverted;
    }

    @Override
    public void refreshData(ContainerManager container, Menu newData)
    {
        MenuRedstoneStrength newDataStrength = (MenuRedstoneStrength)newData;

        if (lowTextBox.getNumber() != newDataStrength.lowTextBox.getNumber())
        {
            lowTextBox.setNumber(newDataStrength.lowTextBox.getNumber());

            sendClientData(container, 0);
        }

        if (highTextBox.getNumber() != newDataStrength.highTextBox.getNumber())
        {
            highTextBox.setNumber(newDataStrength.highTextBox.getNumber());

            sendClientData(container, 1);
        }

        if (inverted != newDataStrength.inverted)
        {
            inverted = newDataStrength.inverted;

            sendClientData(container, 2);
        }
    }

    public void sendServerData(int id)
    {
        DataWriter dw = getWriterForServerComponentPacket();
        writeData(dw, id);
        PacketHandler.sendDataToServer(dw);
    }

    public void sendClientData(ContainerManager container, int id)
    {
        DataWriter dw = getWriterForClientComponentPacket(container);
        writeData(dw, id);
        PacketHandler.sendDataToListeningClients(container, dw);
    }

    public void writeData(DataWriter dw, int id)
    {
        boolean isTextBox = id != 2;
        dw.writeBoolean(isTextBox);
        if (isTextBox)
        {
            boolean isHigh = id == 1;
            dw.writeBoolean(isHigh);
            TextBoxNumber textBox = isHigh ? highTextBox : lowTextBox;
            dw.writeData(textBox.getNumber(), DataBitHelper.MENU_REDSTONE_ANALOG);
        } else
        {
            dw.writeBoolean(inverted);
        }
    }

    public static final String NBT_LOW = "LowRange";
    public static final String NBT_HIGH = "HighRange";
    public static final String NBT_INVERTED = "Inverted";

    @Override
    public void readFromNBT(NBTTagCompound nbtTagCompound, int version, boolean pickup)
    {
        lowTextBox.setNumber(nbtTagCompound.getByte(NBT_LOW));
        highTextBox.setNumber(nbtTagCompound.getByte(NBT_HIGH));
        inverted = nbtTagCompound.getBoolean(NBT_INVERTED);
    }

    @Override
    public void writeToNBT(NBTTagCompound nbtTagCompound, boolean pickup)
    {
        nbtTagCompound.setByte(NBT_LOW, (byte)lowTextBox.getNumber());
        nbtTagCompound.setByte(NBT_HIGH, (byte)highTextBox.getNumber());
        nbtTagCompound.setBoolean(NBT_INVERTED, inverted);
    }

    @Override
    public boolean isVisible()
    {
        return getParent().getConnectionSet() == ConnectionSet.REDSTONE;
    }

    @Override
    public void readNetworkComponent(DataReader dr)
    {
        if (dr.readBoolean())
        {
            TextBoxNumber textBox = dr.readBoolean() ? highTextBox : lowTextBox;
            textBox.setNumber(dr.readData(DataBitHelper.MENU_REDSTONE_ANALOG));
        } else
        {
            inverted = dr.readBoolean();
        }
    }


    public boolean isInverted()
    {
        return inverted;
    }

    public int getLow()
    {
        return lowTextBox.getNumber();
    }

    public int getHigh()
    {
        return highTextBox.getNumber();
    }

    @Override
    public void addErrors(List<String> errors)
    {
        if (getLow() > getHigh())
        {
            errors.add(Localization.INVALID_REDSTONE_RANGE_ERROR.toString());
        } else if (getLow() == 0 && getHigh() == 15)
        {
            errors.add(Localization.REDUNDANT_REDSTONE_RANGE_ERROR.toString());
        }
    }
}