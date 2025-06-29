package net.cacpixel.rtmmetro.client.gui;

import net.cacpixel.rtmmetro.client.gui.widgets.GuiWidget;
import net.cacpixel.rtmmetro.client.gui.widgets.GuiWidgetDummy;
import net.cacpixel.rtmmetro.client.gui.widgets.IWidgetHolder;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class GuiLayoutFlex extends GuiLayoutBase
{
    public FlexFlow flow = FlexFlow.ROW;
    public FlexAlign primaryAlign = FlexAlign.ALIGN_START;
    public FlexAlign secondaryAlign = FlexAlign.ALIGN_START;

    public GuiLayoutFlex(IWidgetHolder holder)
    {
        super(holder);
    }

    @Override
    public void makeLayout()
    {
        for (int i = 0; i < 2; i++) // 做两次的原因如下
        {
            makeLayoutFlex(); // i=1: onMakeLayoutFinish时Scroll可能缩减ActualWidth、height，所以再次makeLayoutFlex。
            holder.onMakeLayoutFinish(); // i=1: 布局改变还会有可能需要再次让Scroll expandMaxValue，所以必须再次通知
            holder.getWidgets().stream().filter(it -> !(it instanceof IWidgetHolder)).forEach(GuiWidget::onMakeLayoutFinish);
        }
    }

    private void makeLayoutFlex()
    {
        Queue<GuiWidget> widgets;
        if (flow.isReverse())
        {
            List<GuiWidget> list = new ArrayList<>(holder.getWidgets());
            Collections.reverse(list);
            widgets = new ArrayDeque<>(list);
        }
        else
        {
            widgets = new ArrayDeque<>(holder.getWidgets());
        }

        List<List<GuiWidget>> rowOrColumns = new ArrayList<>();
        int primaryJdgVal = flow.isColumn() ? holder.getActualHeight() : holder.getActualWidth();
        int secondaryJdgVal = flow.isColumn() ? holder.getActualWidth() : holder.getActualHeight();

        if (flow.isWrap())
        {
            List<GuiWidget> currentRowOrColumn = new ArrayList<>();
            while (!widgets.isEmpty())
            {
                GuiWidget w = widgets.peek();       // 暂时不把它移出队列
                if (currentRowOrColumn.isEmpty())   // 无论如何一行或者一列内至少有一个widget
                {
                    currentRowOrColumn.add(w);
                    widgets.poll();                 // 现在把它移出队列
                }
                else
                {
                    AtomicInteger currentRowOrColumnLength = new AtomicInteger(0);    // 当前行或者列的所有元素的primaryVal长度（宽或者高）全部加起来
                    // 用于判断是否超出当前行或列能否放得下下一个widget
                    currentRowOrColumn.forEach(x -> currentRowOrColumnLength.getAndAdd(flow.isColumn() ? x.height : x.width));
                    int currentWidgetPrimaryVal = flow.isColumn() ? w.height : w.width;
                    if (currentRowOrColumnLength.get() + currentWidgetPrimaryVal <= primaryJdgVal)
                    {
                        currentRowOrColumn.add(w);
                        widgets.poll();             // 现在把它移出队列
                    }
                    else    // 处理当前行或者列（不会从队列中poll，w会被留给下一次循环）
                    {
                        rowOrColumns.add(currentRowOrColumn);
                        currentRowOrColumn = new ArrayList<>(); // 此处不能clear，currentRowOrColumn还是在的，只是被添加到rowOrColumns内等待后续处理
                    }
                }
            }
            if (!currentRowOrColumn.isEmpty())
            {
                rowOrColumns.add(currentRowOrColumn);
            }
        }
        else // 非wrap即为1行或者1列，无论widget多少个
        {
            rowOrColumns.add(new ArrayList<>(widgets));
        }

        AtomicInteger secondaryLength = new AtomicInteger(0);    // 所有行或者列里面那个最大的长度（宽或者高）全部加起来，用于计算secondary间隙
        rowOrColumns.forEach(list -> list.stream()
                .max(Comparator.comparingInt(this::getSecondarySize))
                .ifPresent(x -> secondaryLength.getAndAdd(flow.isColumn() ? x.height : x.width)));

        int prevSecondaryMaxLen;            // 记录上行或者列的位置
        float currentPosSecondary = 0;        // 记录上行或者列的位置
        // 处理所有的 row Or Columns
        for (List<GuiWidget> list : rowOrColumns)
        {
            GuiWidget prev = new GuiWidgetDummy(holder, 0, 0, 0, 0, 0); // 记录上一个物品的位置
            float currentPos = 0;                                                        // 记录上一个物品的位置

            prevSecondaryMaxLen = list.stream().max(Comparator.comparingInt(this::getSecondarySize))
                    .map(this::getSecondarySize)
                    .orElse(0);
            AtomicInteger currentRowOrColumnLength = new AtomicInteger(0);    // 当前行或者列的所有元素的primaryVal长度（宽或者高）全部加起来
            list.forEach(x -> currentRowOrColumnLength.getAndAdd(flow.isColumn() ? x.height : x.width));
            float dist = primaryJdgVal - currentRowOrColumnLength.get();
            float dist2 = secondaryJdgVal - secondaryLength.get();
            float a = primaryAlign.getDistanceSide(dist, list.size());
            float b = primaryAlign.getDistanceMid(dist, list.size());
            float as = secondaryAlign.getDistanceSide(dist2, rowOrColumns.size());
            float bs = secondaryAlign.getDistanceMid(dist2, rowOrColumns.size());

            currentPosSecondary = (rowOrColumns.indexOf(list) == 0) ? currentPosSecondary + as :
                    currentPosSecondary + prevSecondaryMaxLen + bs;

            for (GuiWidget curr : list)
            {
                currentPos = (list.indexOf(curr) == 0) ? currentPos + a : currentPos + getPrimarySize(prev) + b;

                setPrimaryPos(curr, Math.round(currentPos));
                setSecondaryPos(curr, Math.round(currentPosSecondary));
                prev = curr;
            }
        }
    }

    public GuiLayoutFlex setFlow(FlexFlow flow)
    {
        this.flow = flow;
        return this;
    }

    public GuiLayoutFlex setPrimaryAlign(FlexAlign primaryAlign)
    {
        this.primaryAlign = primaryAlign;
        return this;
    }

    public GuiLayoutFlex setSecondaryAlign(FlexAlign secondaryAlign)
    {
        this.secondaryAlign = secondaryAlign;
        return this;
    }

    /* Start primary/secondary getter */
    private int getPrimaryPos(GuiWidget widget)
    {
        return flow.isColumn() ? widget.y : widget.x;
    }

    private int getSecondaryPos(GuiWidget widget)
    {
        return flow.isColumn() ? widget.x : widget.y;
    }

    private int getPrimarySize(GuiWidget widget)
    {
        return flow.isColumn() ? widget.height : widget.width;
    }

    private int getSecondarySize(GuiWidget widget)
    {
        return flow.isColumn() ? widget.width : widget.height;
    }
    /* End primary/secondary getter */

    /* Start primary/secondary setter */
    private void setPrimaryPos(GuiWidget widget, int val)
    {
        if (flow.isColumn()) widget.y = val;
        else widget.x = val;
    }

    private void setSecondaryPos(GuiWidget widget, int val)
    {
        if (flow.isColumn()) widget.x = val;
        else widget.y = val;
    }

    private void setPrimarySize(GuiWidget widget, int val)
    {
        if (flow.isColumn()) widget.height = val;
        else widget.width = val;
    }

    private void setSecondarySize(GuiWidget widget, int val)
    {
        if (flow.isColumn()) widget.width = val;
        else widget.height = val;
    }
    /* End primary/secondary setter */

    public enum FlexFlow
    {
        ROW(0),                 // 行，无折叠，正向
        ROW_WRAP(2),            // 行，折叠，正向
        ROW_REVERSE(4),         // 行，无折叠，反向
        ROW_WRAP_REVERSE(6),    // 行，折叠，反向
        COLUMN(1),              // 列，无折叠，正向
        COLUMN_WRAP(3),         // 列，折叠，正向
        COLUMN_REVERSE(5),      // 列，无折叠，反向
        COLUMN_WRAP_REVERSE(7); // 列，折叠，反向

        private final int val;
        public static final int COLUMN_MASK = 0x00000001;
        public static final int WRAP_MASK = 0x00000002;
        public static final int REVERSE_MASK = 0x00000004;

        FlexFlow(int val)
        {
            this.val = val;
        }

        public boolean hasProperty(int mask)
        {
            return (val & mask) > 0;
        }

        public boolean isColumn()
        {
            return hasProperty(COLUMN_MASK);
        }

        public boolean isWrap()
        {
            return hasProperty(WRAP_MASK);
        }

        public boolean isReverse()
        {
            return hasProperty(REVERSE_MASK);
        }
    }

    public enum FlexAlign
    {
        ALIGN_START,
        ALIGN_END,
        ALIGN_CENTER,
        ALIGN_SPACE_EVENLY,
        ALIGN_SPACE_AROUND,
        ALIGN_SPACE_BETWEEN;

        public float getDistanceSide(float dist, int size)
        {
            float a = 0;
            switch (this)
            {
            case ALIGN_START:       // a=0, b=0
                break;
            case ALIGN_CENTER:      // a=剩下的间距/2, b=0
                a = (dist) / 2;
                break;
            case ALIGN_END:         // a=剩下的间距，b=0
                a = dist;
                break;
            case ALIGN_SPACE_EVENLY:    // a=b=剩下的间距/(元素个数+1)
                a = (dist) / (size + 1);
                break;
            case ALIGN_SPACE_AROUND:    // a=b/2, b=剩下的间距/(元素个数)
                float b = dist / (size);
                a = b / 2.0f;
                break;
            case ALIGN_SPACE_BETWEEN:   // a=0, b=剩下的间距/(元素个数-1)
                break;
            default:
                break;
            }
            return Math.max(a, 0);
        }

        public float getDistanceMid(float dist, int size)
        {
            float b = 0;
            switch (this)
            {
            case ALIGN_START:       // a=0, b=0
                break;
            case ALIGN_CENTER:      // a=剩下的间距/2, b=0
                break;
            case ALIGN_END:         // a=剩下的间距，b=0
                break;
            case ALIGN_SPACE_EVENLY:    // a=b=剩下的间距/(元素个数+1)
                b = (dist) / (size + 1);
                break;
            case ALIGN_SPACE_AROUND:    // a=b/2, b=剩下的间距/(元素个数)
                b = dist / (size);
                break;
            case ALIGN_SPACE_BETWEEN:   // a=0, b=剩下的间距/(元素个数-1)
                b = (dist) / Math.max(size - 1, 1);
                break;
            default:
                break;
            }
            return Math.max(b, 0);
        }
    }
}
