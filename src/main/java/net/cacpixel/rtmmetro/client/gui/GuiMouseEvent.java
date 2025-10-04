package net.cacpixel.rtmmetro.client.gui;

import java.util.function.BooleanSupplier;

public class GuiMouseEvent
{
    public String name;
    private boolean canInteract;
    private boolean canEventPass;
    private BooleanSupplier judgeEventPassCallback;

    public GuiMouseEvent(String name, boolean canInteract, boolean canEventPass)
    {
        this.name = name;
        this.canInteract = canInteract;
        this.canEventPass = canEventPass;
    }

    @Override
    public String toString()
    {
        return name + ":" + getClass().getSimpleName() + "@" + Integer.toHexString(hashCode());
    }

    public boolean canInteract()
    {
        return canInteract;
    }

    public void setInteract(boolean canInteract)
    {
        this.canInteract = canInteract;
    }

    public boolean canEventPass()
    {
        if (judgeEventPassCallback != null)
        {
            return judgeEventPassCallback.getAsBoolean();
        }
        else
        {
            return canEventPass;
        }
    }

    public boolean canEventPassGetRaw()
    {
        return canEventPass;
    }

    public void setEventPass(boolean canEventPass)
    {
        this.canEventPass = canEventPass;
    }

    public void setJudgeEventPassCallback(BooleanSupplier judgeEventPassCallback)
    {
        this.judgeEventPassCallback = judgeEventPassCallback;
    }
}
