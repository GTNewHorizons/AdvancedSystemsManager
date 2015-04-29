package advancedsystemsmanager.flow.execution.buffers.elements;

import advancedsystemsmanager.api.execution.IBufferElement;
import advancedsystemsmanager.api.execution.Key;
import advancedsystemsmanager.flow.setting.Setting;

public abstract class BufferElementBase<Type> implements IBufferElement<Type>
{
    protected int id;
    protected int amount;
    protected Type content;
    protected Setting<Type> setting;
    protected boolean whitelist;

    public BufferElementBase(int id)
    {
        this.id = id;
    }

    @Override
    public int getCommandID()
    {
        return this.id;
    }

    @Override
    public Key<Type> getKey()
    {
        return new Key<Type>(content);
    }

    @Override
    public Type getContent()
    {
        return content;
    }

    public int getMaxWithSetting(int amount)
    {
        if (setting != null && setting.isLimitedByAmount()) return whitelist ? Math.min(amount, setting.getAmount()) : amount - setting.getAmount();
        return amount;
    }
}