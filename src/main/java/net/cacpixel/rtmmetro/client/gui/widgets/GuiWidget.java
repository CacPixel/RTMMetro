package net.cacpixel.rtmmetro.client.gui.widgets;

import net.cacpixel.rtmmetro.client.gui.CacGuiUtils;
import net.cacpixel.rtmmetro.client.gui.GuiMouseEvent;
import net.cacpixel.rtmmetro.client.gui.GuiScreenAdvanced;
import net.cacpixel.rtmmetro.client.gui.MouseScissorManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;

import java.util.function.IntSupplier;

public abstract class GuiWidget
{
    private final GuiScreenAdvanced screen;
    private final IWidgetHolder holder;
    public int x;
    public int y;
    public int width;
    public int height;
    public float zLevel; // From Gui
    private int layer = 0;
    public int id;
    private boolean enabled = true;
    private boolean visible = true;
    private DragStatus dragStatus = DragStatus.NOT_DRAGGING;
    private boolean hasValueUpdated;    //  GuiScreen 通知用
    private IActionListener<? extends GuiWidget> listener;
    public IntSupplier xSupplier = this::getX;
    public IntSupplier ySupplier = this::getY;
    public IntSupplier widthSupplier = this::getWidth;
    public IntSupplier heightSupplier = this::getHeight;
    public static final IntSupplier ZERO = () -> 0;
    private final GuiMouseEvent eventClick = new GuiMouseEvent("Click", false, false);
    private final GuiMouseEvent eventLastClick = new GuiMouseEvent("LastClick", false, false);
    private final GuiMouseEvent eventDrag = new GuiMouseEvent("Drag", false, false);
    private final GuiMouseEvent eventRelease = new GuiMouseEvent("Release", false, false);
    private final GuiMouseEvent eventScroll = new GuiMouseEvent("Scroll", false, false);

    public GuiWidget(IWidgetHolder holder, int x, int y, int width, int height)
    {
        this.holder = holder;
        this.screen = holder.getScreen();
        this.id = screen.getNextWidgetId();
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        getEventScroll().setEventPass(true);
    }

    public GuiWidget(IWidgetHolder holder, IntSupplier xSupplier, IntSupplier ySupplier,
                     IntSupplier widthSupplier, IntSupplier heightSupplier)
    {
        this.holder = holder;
        this.screen = holder.getScreen();
        this.id = screen.getNextWidgetId();
        if (xSupplier != null) this.xSupplier = xSupplier;
        if (ySupplier != null) this.ySupplier = ySupplier;
        if (widthSupplier != null) this.widthSupplier = widthSupplier;
        if (heightSupplier != null) this.heightSupplier = heightSupplier;
        this.updatePosAndSize();
        getEventScroll().setEventPass(true);
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName() + "@" + Integer.toHexString(hashCode()));
        sb.append(" | ");
        sb.append(" ").append("x: ").append(x);
        sb.append(" ").append("y: ").append(y);
        sb.append(" ").append("w: ").append(width);
        sb.append(" ").append("h: ").append(height);
        sb.append(" | ");
        if (eventClick.canInteract())
        {
            sb.append("Clickable ");
        }
        if (eventLastClick.canInteract())
        {
            sb.append("LastClickable ");
        }
        if (eventDrag.canInteract())
        {
            sb.append("Draggable ");
        }
        if (eventRelease.canInteract())
        {
            sb.append("Releasable ");
        }
        if (eventScroll.canInteract())
        {
            sb.append("Scrollable ");
        }
        attachString(sb);
        return sb.toString();
    }

    public void attachString(StringBuilder sb)
    {
    }

    @SuppressWarnings("unchecked")
    public <T extends GuiWidget> T setListener(IActionListener<T> listener)
    {
        this.listener = listener;
        return (T) this;
    }

    public IActionListener<? extends GuiWidget> getListener()
    {
        return listener;
    }

    public void onScreenResize()
    {
    }

    public void onLeftClick(int mouseX, int mouseY)
    {
        if (this.isEnabled() && this.isVisible() && this.getEventClick().canInteract())
        {
            dragStatus = DragStatus.MOUSE_HOLD;
            this.holder.addWidgetToActionQueue(this);
        }
    }

    public void onClick(int mouseX, int mouseY, int button)
    {
        switch (button)
        {
        case 0:
            this.onLeftClick(mouseX, mouseY);
            break;
        case 1:
            this.onRightClick(mouseX, mouseY);
            break;
        case 2:
            this.onMiddleClick(mouseX, mouseY);
            break;
        default:
            break;
        }
    }

    public void onRightClick(int mouseX, int mouseY)
    {
    }

    public void onMiddleClick(int mouseX, int mouseY)
    {
    }

    public void onClickAndDrag(int mouseX, int mouseY, int mouseButton, long timeSinceLastClick)
    {
        switch (mouseButton)
        {
        case 0:
            this.onLeftClickAndDrag(mouseX, mouseY, timeSinceLastClick);
            break;
        case 1:
        case 2:
        default:
            break;
        }
    }

    public void onLeftClickAndDrag(int mouseX, int mouseY, long timeSinceLastClick)
    {
        if (dragStatus == DragStatus.MOUSE_HOLD && (Math.abs(mouseX - getLastClickedX()) > 1 || Math.abs(mouseY - getLastClickedY()) > 1))
        {
            dragStatus = DragStatus.DRAGGING;
        }
    }

    public void onRelease(int mouseX, int mouseY, int state)
    {
        this.dragStatus = DragStatus.NOT_DRAGGING;
    }

    public void onScroll(int mouseX, int mouseY, int scroll)
    {
    }

    public void onKeyTyped(char typedChar, int keyCode)
    {
    }

    public void onWidgetUpdate()
    {
    }

    public boolean isMouseInside()
    {
        int dx = holder.shiftMouseX();
        int dy = holder.shiftMouseY();
        MouseScissorManager msm = screen.getMouseScissorManager();
        boolean flag1 = msm.isMouseInside(x + dx, y + dy, width, height, CacGuiUtils.getMouseX(), CacGuiUtils.getMouseY());
        boolean flag2 = true;// holder.isMouseInside();
        return flag1 && flag2;
    }

    public boolean isLastClickInside()
    {
        int dx = holder.shiftMouseX();
        int dy = holder.shiftMouseY();
        MouseScissorManager msm = screen.getMouseScissorManager();
        boolean flag1 = msm.isMouseInside(x + dx, y + dy, width, height, getLastClickedX(), getLastClickedY());
        boolean flag2 = true;// holder.isMouseInside();
        return flag1 && flag2;
    }

    public void drawBefore(int mouseX, int mouseY, float partialTicks)
    {
    }

    public abstract void draw(int mouseX, int mouseY, float partialTicks);

    public void drawAfter(int mouseX, int mouseY, float partialTicks)
    {
    }

    public void drawCustom(int mouseX, int mouseY, float partialTicks)
    {
    }

    public void drawDebugLayer(int mouseX, int mouseY, float partialTicks)
    {
        boolean flag = (GuiScreenAdvanced.debugType == GuiScreenAdvanced.DEBUG_EVENT_CLICK && getEventClick().canInteract())
                || (GuiScreenAdvanced.debugType == GuiScreenAdvanced.DEBUG_EVENT_LAST_CLICK && getEventLastClick().canInteract())
                || (GuiScreenAdvanced.debugType == GuiScreenAdvanced.DEBUG_EVENT_DRAG && getEventDrag().canInteract())
                || (GuiScreenAdvanced.debugType == GuiScreenAdvanced.DEBUG_EVENT_RELEASE && getEventRelease().canInteract())
                || (GuiScreenAdvanced.debugType == GuiScreenAdvanced.DEBUG_EVENT_SCROLL && getEventScroll().canInteract());
        if (flag && GuiScreenAdvanced.debugMode && Minecraft.getMinecraft().currentScreen == screen)
        {
            int boxColor;
            if (!isEnabled())
                boxColor = 0x50D2042D;
            else if (!isVisible())
                boxColor = 0x50FFAC1C;
            else if (isDragging())
                boxColor = 0x507CFC00;
            else if (isMouseHolding())
                boxColor = 0x507DF9FF;
            else
                boxColor = 0x50DA70D6;
            int size = screen.getScreenScissorManager().disableAll();
            CacGuiUtils.drawRect(x, y, x + width, y + height, 100, boxColor);

            screen.glPushMatrix();
            GlStateManager.translate(0, 0, 101);
            CacGuiUtils.drawString(screen.mc.fontRenderer, toString(), x, y, 0xFFFFFFFF);// todo 跟随鼠标 等待get屏幕范围坐标做完
            screen.glPopMatrix();
            screen.getScreenScissorManager().enable(size);
        }
    }

    public int getX() {return x;}

    public int getY() {return y;}

    public int getWidth() {return width;}

    public int getHeight() {return height;}

    public int getEndX() {return x + width;}

    public int getEndY() {return y + height;}

    public int getXOfScreen()
    {
        return x + getHolder().getXOfScreen();
    }

    public int getYOfScreen()
    {
        return y + getHolder().getYOfScreen();
    }

    public final void updatePosAndSize()
    {
        updatePos();
        updateSize();
    }

    public final void updatePos()
    {
        x = xSupplier.getAsInt();
        y = ySupplier.getAsInt();
    }

    public final void updateSize()
    {
        width = widthSupplier.getAsInt();
        height = heightSupplier.getAsInt();
    }

    public IntSupplier getXSupplier()
    {
        return this.xSupplier;
    }

    public IntSupplier getYSupplier()
    {
        return this.ySupplier;
    }

    public IntSupplier getWidthSupplier()
    {
        return this.widthSupplier;
    }

    public IntSupplier getHeightSupplier()
    {
        return this.heightSupplier;
    }

    public boolean isEnabled()
    {
        return enabled;
    }

    @SuppressWarnings("unchecked")
    public <T extends GuiWidget> T setEnabled(boolean enabled)
    {
        this.enabled = enabled;
        return (T) this;
    }

    public boolean isVisible()
    {
        return visible;
    }

    @SuppressWarnings("unchecked")
    public <T extends GuiWidget> T setVisible(boolean visible)
    {
        this.visible = visible;
        return (T) this;
    }

    public boolean isPositionIndependent()
    {
        return x <= 0 && y <= 0 && width <= 0 && height <= 0;
    }

    public float getzLevel()
    {
        return zLevel;
    }

    public boolean isDragging()
    {
        return dragStatus == DragStatus.DRAGGING;
    }

    public boolean isMouseHolding()
    {
        return dragStatus == DragStatus.MOUSE_HOLD || dragStatus == DragStatus.DRAGGING;
    }

    public void onMakeLayoutStart() {}

    public void onMakeLayoutFinish() {}

    public boolean checkValueUpdated()
    {
        if (hasValueUpdated)
        {
            hasValueUpdated = false;
            return true;
        }
        else
        {
            return false;
        }
    }

    public void setValueUpdated(boolean hasValueUpdated)
    {
        this.hasValueUpdated = hasValueUpdated;
    }

    public int getLastClickedX()
    {
        return screen.getLastClickedX();
    }

    public int getLastClickedY()
    {
        return screen.getLastClickedY();
    }

    public GuiScreenAdvanced getScreen()
    {
        return screen;
    }

    public IWidgetHolder getHolder()
    {
        return holder;
    }

    public int getLayer()
    {
        return layer;
    }

    @SuppressWarnings("unchecked")
    public <T extends GuiWidget> T setLayer(int layer)
    {
        this.layer = layer;
        return (T) this;
    }

    public void mouseInteractJudge()
    {
    }

    public GuiMouseEvent getEventClick()
    {
        return eventClick;
    }

    public GuiMouseEvent getEventLastClick()
    {
        return eventLastClick;
    }

    public GuiMouseEvent getEventDrag()
    {
        return eventDrag;
    }

    public GuiMouseEvent getEventRelease()
    {
        return eventRelease;
    }

    public GuiMouseEvent getEventScroll()
    {
        return eventScroll;
    }

    public GuiMouseEvent[] getGuiMouseEvents()
    {
        return new GuiMouseEvent[]{eventClick, eventLastClick, eventDrag, eventRelease, eventScroll};
    }

    public enum DragStatus
    {
        NOT_DRAGGING,
        MOUSE_HOLD,
        DRAGGING,
    }
}
