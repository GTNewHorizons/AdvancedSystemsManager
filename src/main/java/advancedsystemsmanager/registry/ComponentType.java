package advancedsystemsmanager.registry;


import advancedsystemsmanager.flow.menus.*;
import advancedsystemsmanager.helpers.Localization;

public enum ComponentType
{
    TRIGGER(0, Localization.TRIGGER_SHORT, Localization.TRIGGER_LONG,
            new ConnectionSet[]{ConnectionSet.CONTINUOUSLY, ConnectionSet.REDSTONE, ConnectionSet.BUD},
            MenuReceivers.class, MenuBUDs.class, MenuInterval.class, MenuRedstoneSidesTrigger.class, MenuRedstoneStrength.class, MenuUpdateBlock.class, MenuResult.class),
    INPUT(1, Localization.INPUT_SHORT, Localization.INPUT_LONG,
            new ConnectionSet[]{ConnectionSet.STANDARD},
            MenuInventory.class, MenuTargetInventory.class, MenuItem.class, MenuResult.class),
    OUTPUT(2, Localization.OUTPUT_SHORT, Localization.OUTPUT_LONG,
            new ConnectionSet[]{ConnectionSet.STANDARD},
            MenuInventory.class, MenuTargetInventory.class, MenuItemOutput.class, MenuResult.class),
    CONDITION(3, Localization.CONDITION_SHORT, Localization.CONDITION_LONG,
            new ConnectionSet[]{ConnectionSet.STANDARD_CONDITION},
            MenuInventoryCondition.class, MenuTargetInventory.class, MenuItemCondition.class, MenuResult.class),
    FLOW_CONTROL(4, Localization.FLOW_CONTROL_SHORT, Localization.FLOW_CONTROL_LONG,
            new ConnectionSet[]{ConnectionSet.MULTIPLE_INPUT_2, ConnectionSet.MULTIPLE_INPUT_5, ConnectionSet.MULTIPLE_OUTPUT_2, ConnectionSet.MULTIPLE_OUTPUT_5},
            MenuSplit.class, MenuResult.class),
    LIQUID_INPUT(5, Localization.LIQUID_INPUT_SHORT, Localization.LIQUID_INPUT_LONG,
            new ConnectionSet[]{ConnectionSet.STANDARD},
            MenuTank.class, MenuTargetTank.class, MenuLiquid.class, MenuResult.class),
    LIQUID_OUTPUT(6, Localization.LIQUID_OUTPUT_SHORT, Localization.LIQUID_OUTPUT_LONG,
            new ConnectionSet[]{ConnectionSet.STANDARD},
            MenuTank.class, MenuTargetTank.class, MenuLiquidOutput.class, MenuResult.class),
    LIQUID_CONDITION(7, Localization.LIQUID_CONDITION_SHORT, Localization.LIQUID_CONDITION_LONG,
            new ConnectionSet[]{ConnectionSet.STANDARD_CONDITION},
            MenuTankCondition.class, MenuTargetTank.class, MenuLiquidCondition.class, MenuResult.class),
    REDSTONE_EMITTER(8, Localization.REDSTONE_EMITTER_SHORT, Localization.REDSTONE_EMITTER_LONG,
            new ConnectionSet[]{ConnectionSet.STANDARD},
            MenuEmitters.class, MenuRedstoneSidesEmitter.class, MenuRedstoneOutput.class, MenuPulse.class, MenuResult.class),
    REDSTONE_CONDITION(9, Localization.REDSTONE_CONDITION_SHORT, Localization.REDSTONE_CONDITION_LONG,
            new ConnectionSet[]{ConnectionSet.STANDARD_CONDITION},
            MenuNodes.class, MenuRedstoneSidesNodes.class, MenuRedstoneStrengthNodes.class, MenuResult.class),
    VARIABLE(10, Localization.CONTAINER_VARIABLE_SHORT, Localization.CONTAINER_VARIABLE_LONG,
            new ConnectionSet[]{ConnectionSet.EMPTY, ConnectionSet.STANDARD},
            MenuVariable.class, MenuContainerTypesVariable.class, MenuVariableContainers.class, MenuListOrderVariable.class, MenuResult.class),
    FOR_EACH(11, Localization.FOR_EACH_LOOP_SHORT, Localization.FOR_EACH_LOOP_LONG,
            new ConnectionSet[]{ConnectionSet.FOR_EACH},
            MenuVariableLoop.class, MenuContainerTypes.class, MenuListOrder.class, MenuResult.class),
    AUTO_CRAFTING(12, Localization.AUTO_CRAFTER_SHORT, Localization.AUTO_CRAFTER_LONG,
            new ConnectionSet[]{ConnectionSet.STANDARD},
            MenuCrafting.class, MenuCraftingPriority.class, MenuContainerScrap.class, MenuResult.class),
    GROUP(13, Localization.GROUP_SHORT, Localization.GROUP_LONG,
            new ConnectionSet[]{ConnectionSet.DYNAMIC},
            MenuGroup.class, MenuResult.class),
    NODE(14, Localization.NODE_SHORT, Localization.NODE_LONG,
            new ConnectionSet[]{ConnectionSet.INPUT_NODE, ConnectionSet.OUTPUT_NODE},
            MenuResult.class),
    CAMOUFLAGE(15, Localization.CAMOUFLAGE_SHORT, Localization.CAMOUFLAGE_LONG,
            new ConnectionSet[]{ConnectionSet.STANDARD},
            MenuCamouflage.class, MenuCamouflageShape.class, MenuCamouflageInside.class, MenuCamouflageSides.class, MenuCamouflageItems.class, MenuResult.class),
    SIGN(16, Localization.SIGN_SHORT, Localization.SIGN_LONG,
            new ConnectionSet[]{ConnectionSet.STANDARD},
            MenuSigns.class, MenuSignText.class, MenuResult.class);


    public Class<? extends Menu>[] classes;
    public int id;
    public ConnectionSet[] sets;
    public Localization name;
    public Localization longName;

    ComponentType(int id, Localization name, Localization longName, ConnectionSet[] sets, Class<? extends Menu>... classes)
    {
        this.classes = classes;
        this.id = id;
        this.sets = sets;
        this.name = name;
        this.longName = longName;
    }

    public Class<? extends Menu>[] getClasses()
    {
        return classes;
    }

    public int getId()
    {
        return id;
    }

    public static ComponentType getTypeFromId(int id)
    {
        for (ComponentType componentType : values())
        {
            if (id == componentType.id)
            {
                return componentType;
            }
        }
        return null;
    }

    public ConnectionSet[] getSets()
    {
        return sets;
    }


    public String getName()
    {
        return name.toString();
    }

    public String getLongName()
    {
        return longName.toString();
    }

    @Override
    public String toString()
    {
        return getName() + "[" + getLongName() + "]";
    }

    public Localization getLongUnLocalizedName()
    {
        return longName;
    }
}
