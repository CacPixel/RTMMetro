package net.cacpixel.rtmmetro.math;

public final class LinePointWithOrder
{
    private final ILineAdvanced line;
    private final double[] point;
    private final int order;

    public LinePointWithOrder(double x, double y, int order, ILineAdvanced line)
    {
        this.point = new double[]{x, y};
        this.order = order;
        this.line = line;
    }

    public LinePointWithOrder(double[] point, int order, ILineAdvanced line)
    {
        this.point = point;
        this.order = order;
        this.line = line;
    }

    public ILineAdvanced getLine()
    {
        return line;
    }

    public double[] getPoint()
    {
        return point;
    }

    public int getOrder()
    {
        return order;
    }
}
