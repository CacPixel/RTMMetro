package net.cacpixel.rtmmetro.client.gui.widgets;

import net.cacpixel.rtmmetro.client.gui.CacGuiUtils;
import net.cacpixel.rtmmetro.client.gui.GuiScreenAdvanced;

import java.util.function.IntSupplier;

public abstract class GuiWidget
{
    public GuiScreenAdvanced pScr;
    public IWidgetHolder holder;
    public int x;
    public int y;
    public int width;
    public int height;
    public float zLevel; // From Gui
    public int id;
    private boolean enabled = true;
    private boolean visible = true;
    private boolean dragging = false;
    public int lastClickedX;
    public int lastClickedY;
    private IActionListener<? extends GuiWidget> listener;
    public IntSupplier xSupplier = this::getX;
    public IntSupplier ySupplier = this::getY;
    public IntSupplier widthSupplier = this::getWidth;
    public IntSupplier heightSupplier = this::getHeight;
    public static final IntSupplier ZERO = () -> 0;

    public GuiWidget(IWidgetHolder holder, int id, int x, int y, int width, int height)
    {
        this.holder = holder;
        this.pScr = holder.getScreen();
        this.id = id;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public GuiWidget(IWidgetHolder holder, int id, IntSupplier xSupplier, IntSupplier ySupplier,
                     IntSupplier widthSupplier, IntSupplier heightSupplier)
    {
        this.holder = holder;
        this.pScr = holder.getScreen();
        this.id = id;
        if (xSupplier != null) this.xSupplier = xSupplier;
        if (ySupplier != null) this.ySupplier = ySupplier;
        if (widthSupplier != null) this.widthSupplier = widthSupplier;
        if (heightSupplier != null) this.heightSupplier = heightSupplier;
        this.updatePosAndSize(this.getXSupplier().getAsInt(),
                this.getYSupplier().getAsInt(),
                this.getWidthSupplier().getAsInt(),
                this.getHeightSupplier().getAsInt());
    }

    public GuiWidget(GuiScreenAdvanced pScr, IntSupplier xSupplier, IntSupplier ySupplier,
                     IntSupplier widthSupplier, IntSupplier heightSupplier)
    {
        this(pScr, pScr.getNextWidgetId(), xSupplier, ySupplier, widthSupplier, heightSupplier);
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

    public void onLeftClick(int mouseX, int mouseY)
    {
        if (this.isEnabled() && this.isVisible() && this.isMouseInside())
        {
            this.holder.addWidgetToActionQueue(this);
            this.dragging = true;
            this.lastClickedX = mouseX;
            this.lastClickedY = mouseY;
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
    }

    public void onMouseReleased(int mouseX, int mouseY, int state)
    {
        this.dragging = false;
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
        return CacGuiUtils.isMouseInside(x + dx, y + dy, width, height) && holder.isMouseInside();
    }

    public abstract void draw(int mouseX, int mouseY, float partialTicks);

    public int getX() {return x;}

    public int getY() {return y;}

    public int getWidth() {return width;}

    public int getHeight() {return height;}

    public int getActualWidth() {return width;}

    public int getActualHeight() {return height;}

    public final GuiWidget updatePosAndSize(int x, int y, int w, int h)
    {
        this.x = x;
        this.y = y;
        this.width = w;
        this.height = h;
        return this;
    }

    public GuiWidget updatePosAndSize()
    {
        this.updatePosAndSize(this.getXSupplier().getAsInt(),
                this.getYSupplier().getAsInt(),
                this.getWidthSupplier().getAsInt(),
                this.getHeightSupplier().getAsInt());
        return this;
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
        return dragging;
    }

    public void setDragging(boolean dragging)
    {
        this.dragging = dragging;
    }

    public void onMakeLayoutFinish() {}
}
