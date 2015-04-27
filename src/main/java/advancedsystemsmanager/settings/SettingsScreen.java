package advancedsystemsmanager.settings;

import advancedsystemsmanager.flow.elements.CheckBox;
import advancedsystemsmanager.flow.elements.CheckBoxList;
import advancedsystemsmanager.helpers.CollisionHelper;
import advancedsystemsmanager.helpers.Localization;
import advancedsystemsmanager.gui.GuiManager;
import advancedsystemsmanager.gui.IInterfaceRenderer;
import advancedsystemsmanager.registry.CommandRegistry;
import advancedsystemsmanager.tileentities.manager.TileEntityManager;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.util.StatCollector;

import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
public class SettingsScreen implements IInterfaceRenderer
{

    private TileEntityManager manager;
    private List<Button> buttons;

    public SettingsScreen(final TileEntityManager manager)
    {
        this.manager = manager;

        buttons = new ArrayList<Button>();
        buttons.add(new Button(493, 5, Localization.GO_BACK, 231, 12 * CommandRegistry.getComponents().size() + 1)
        {
            @Override
            protected void onClick()
            {
                manager.specialRenderer = null;
            }
        });
    }

    private static final int CHECK_BOX_WIDTH = 100;
    private static final int START_X = 10;
    private static final int START_SETTINGS_X = 380;
    private static final int MARGIN_X = 30;
    private static final int START_Y = 20;
    private static final int MAX_Y = 250;

    private class CheckBoxSetting extends CheckBox
    {
        private String key;
        private String name;
        private CheckBoxSetting(String name)
        {
            super(null, getXAndGenerateY(StatCollector.translateToLocal("gui.asm.Settings." + name)), currentY);
            key = "gui.asm.Settings." + name;
            this.name = name;
            setTextWidth(CHECK_BOX_WIDTH);
        }

        @Override
        public boolean getValue()
        {
            return Settings.getSetting(name);
        }

        @Override
        public void setValue(boolean val)
        {
            Settings.setSetting(name, val);
        }

        @Override
        public String getName()
        {
            return StatCollector.translateToLocal(key);
        }

        @Override
        public void onUpdate()
        {
        }
    }

    private int getXAndGenerateY(String str)
    {
        currentY += offsetY;

        List<String> lines = cachedGui.getLinesFromText(str, CHECK_BOX_WIDTH);
        int height = (int)((lines.size() + 1) * cachedGui.getFontHeight() * 0.7F);
        offsetY = height;

        if (currentY + height > MAX_Y)
        {
            currentY = START_Y;
            currentX += CHECK_BOX_WIDTH + MARGIN_X;
        }

        return currentX;
    }

    private CheckBoxList checkBoxes;
    private String cachedString;
    private Localization localization = Localization.CLOSE_GROUP_LABEL;
    private int currentX;
    private int currentY;
    private int offsetY;
    private GuiManager cachedGui;

    private void addCheckboxes(GuiManager gui)
    {
        cachedGui = gui;
        cachedString = localization.toString();
        checkBoxes = new CheckBoxList();
        currentX = START_X;
        currentY = START_Y;
        offsetY = 0;

        for (String setting : Settings.settingsRegistry.keySet())
        {
            checkBoxes.addCheckBox(new CheckBoxSetting(setting));
        }


        currentX = START_SETTINGS_X;
        currentY = START_Y;
        offsetY = 0;

        checkBoxes.addCheckBox(new CheckBoxSetting("limitless")
        {
            @Override
            public void setValue(boolean val)
            {
                Settings.setLimitless(manager, val);
            }

            @Override
            public boolean getValue()
            {
                return Settings.isLimitless(manager);
            }

            @Override
            public boolean isVisible()
            {
                return Minecraft.getMinecraft().thePlayer.capabilities.isCreativeMode;
            }
        });
    }

    @Override
    public void draw(GuiManager gui, int mX, int mY)
    {
        if (cachedString == null || !localization.toString().equals(cachedString))
        {
            addCheckboxes(gui);
        }

        gui.drawString(Localization.PREFERENCES.toString(), START_X - 2, 6, 0x404040);
        if (Minecraft.getMinecraft().thePlayer.capabilities.isCreativeMode)
        {
            gui.drawString(Localization.SETTINGS.toString(), START_SETTINGS_X - 2, 6, 0x404040);
        }
        checkBoxes.draw(gui, mX, mY);
        for (Button button : buttons)
        {
            button.draw(gui, mX, mY);
        }
    }

    @Override
    public void drawMouseOver(GuiManager gui, int mX, int mY)
    {
        for (Button button : buttons)
        {
            button.drawMouseOver(gui, mX, mY);
        }
    }

    @Override
    public void onClick(GuiManager gui, int mX, int mY, int b)
    {
        checkBoxes.onClick(mX, mY);
        for (Button button : buttons)
        {
            if (button.inBounds(mX, mY))
            {
                button.onClick();
                break;
            }
        }
    }

    @Override
    public void onDrag(GuiManager gui, int mX, int mY)
    {

    }

    @Override
    public void onRelease(GuiManager gui, int mX, int mY)
    {

    }

    @Override
    public void onKeyTyped(GuiManager gui, char c, int k)
    {

    }

    @Override
    public void onScroll(int scroll)
    {

    }


    private static final int BUTTON_SRC_X = 242;
    private static final int BUTTON_SRC_Y = 0;
    private static final int BUTTON_SIZE = 14;
    private static final int BUTTON_SIZE_INNER = 12;

    private abstract class Button
    {
        private int x;
        private int y;
        private Localization name;
        private int srcX;
        private int srcY;

        private Button(int x, int y, Localization name, int srcX, int srcY)
        {
            this.x = x;
            this.y = y;
            this.name = name;
            this.srcX = srcX;
            this.srcY = srcY;
        }

        private void draw(GuiManager gui, int mX, int mY)
        {
            int srcYButton = inBounds(mX, mY) ? 1 : 0;

            gui.drawTexture(x, y, BUTTON_SRC_X, BUTTON_SRC_Y + srcYButton * BUTTON_SIZE, BUTTON_SIZE, BUTTON_SIZE);
            gui.drawTexture(x + 2, y + 2, srcX, srcY, BUTTON_SIZE_INNER, BUTTON_SIZE_INNER);
        }

        private boolean inBounds(int mX, int mY)
        {
            return CollisionHelper.inBounds(x, y, BUTTON_SIZE, BUTTON_SIZE, mX, mY);
        }

        private void drawMouseOver(GuiManager gui, int mX, int mY)
        {
            if (inBounds(mX, mY))
            {
                gui.drawMouseOver(name.toString(), mX, mY);
            }
        }

        protected abstract void onClick();
    }
}
