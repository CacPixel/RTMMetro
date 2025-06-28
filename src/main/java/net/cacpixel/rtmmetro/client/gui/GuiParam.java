package net.cacpixel.rtmmetro.client.gui;

import java.util.function.DoubleSupplier;
import java.util.function.IntFunction;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

public interface GuiParam extends IntSupplier
{
    static GuiParam from(int i)
    {
        return () -> i;
    }

    default GuiParam thenAdd(int i)
    {
        return () -> this.getAsInt() + i;
    }

    default GuiParam thenMinusBy(int i)
    {
        return () -> this.getAsInt() - i;
    }

    default GuiParam thenMinusFrom(int i)
    {
        return () -> i - this.getAsInt();
    }

    default GuiParam thenMultiply(double i)
    {
        return () -> (int) (this.getAsInt() * i);
    }

    default GuiParam thenDivideBy(int i)
    {
        return () -> this.getAsInt() / i;
    }

    default GuiParam thenDividend(float i)
    {
        return () -> (int) (i / this.getAsInt());
    }

    // Supplier

    default GuiParam thenAdd(IntSupplier i)
    {
        return () -> this.getAsInt() + i.getAsInt();
    }

    default GuiParam thenMinusBy(IntSupplier i)
    {
        return () -> this.getAsInt() - i.getAsInt();
    }

    default GuiParam thenMinusFrom(IntSupplier i)
    {
        return () -> i.getAsInt() - this.getAsInt();
    }

    default GuiParam thenMultiply(DoubleSupplier i)
    {
        return () -> (int) (this.getAsInt() * i.getAsDouble());
    }

    default GuiParam thenDivideBy(IntSupplier i)
    {
        return () -> this.getAsInt() / i.getAsInt();
    }

    default GuiParam thenDividend(DoubleSupplier i)
    {
        return () -> (int) (i.getAsDouble() / this.getAsInt());
    }


    default GuiParam thenApply(IntFunction<Integer> block)
    {
        return () -> block.apply(this.getAsInt());
    }
}
