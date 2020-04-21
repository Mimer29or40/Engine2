package engine.gui;

import engine.gui.interfaces.IUIContainerLike;
import engine.gui.util.Rect;
import engine.gui.util.Rectc;
import engine.input.Mouse;
import engine.render.Texture;

public abstract class UIElement
{
    protected final UIContainer container;
    
    protected int layer          = 0;
    protected int requestedLayer = 0;
    
    protected final Rect rect = new Rect();
    
    protected Texture texture;
    
    protected int shadowWidth;
    protected int borderWidth;
    protected int shapeCornerWidth;
    
    protected boolean alive = true;
    
    protected boolean visible = true;
    
    protected boolean focused = false;
    protected boolean hover   = false;
    protected boolean enabled = false;
    
    public UIElement(Rectc rect, IUIContainerLike container)
    {
        this.container = container != null ? container.getContainer() : null;
        
        if (this.container != null)
        {
            this.container.addElement(this);
        }
        else
        {
            EEXT_GUI.INSTANCE.elements.add(this);
        }
        
        this.rect.set(rect);
        
        rebuild();
    }
    
    public boolean alive()
    {
        return this.alive;
    }
    
    public void kill()
    {
        if (this.container != null)
        {
            this.container.removeElement(this);
        }
        else
        {
            EEXT_GUI.INSTANCE.elements.remove(this);
        }
    }
    
    public UIContainer container()
    {
        return this.container;
    }
    
    public Rectc rect()
    {
        return this.rect;
    }
    
    public int absX()
    {
        return (this.container != null ? this.container.absX() : 0) + this.rect.x1();
    }
    
    public int absY()
    {
        return (this.container != null ? this.container.absY() : 0) + this.rect.y1();
    }
    
    public UIElement position(int x, int y)
    {
        if (this.rect.left() != x || this.rect.top() != y)
        {
            this.rect.pos(x, y);
            
            if (this.container != null) this.container.recalculateLayers();
        }
        
        return this;
    }
    
    public UIElement dimensions(int width, int height)
    {
        if (this.rect.width() != width || this.rect.height() != height)
        {
            this.rect.size(width, height);
            
            if (this.container != null) this.container.recalculateLayers();
            
            // if (width > 0 && height > 0)
            // {
            //
            // }
        }
        
        return this;
    }
    
    public void rebuild()
    {
        this.texture = new Texture(this.rect.width(), this.rect.height(), 4);
    }
    
    public UIElement getTopElement(int mouseX, int mouseY)
    {
        UIElement top = null;
        if (this.alive && this.visible)
        {
            if (this instanceof UIContainer)
            {
                for (UIElement widget : ((UIContainer) this).elements)
                {
                    UIElement topChild = widget.getTopElement(mouseX, mouseY);
                    if (top == null || (topChild != null && topChild.layer >= top.layer)) top = topChild;
                }
            }
            // int widgetX = mouseX - getAbsX(), widgetY = mouseY - getAbsY();
            // return top != null ? top : 0 <= widgetX && widgetX < this.rect.width() && 0 <= widgetY && widgetY < this.rect.height() ? this : null;
            return top != null ? top : this.rect.collide(mouseX - absX(), mouseY - absY()) ? this : null;
        }
        return null;
    }
    
    // ------------------
    // ----- EVENTS -----
    // ------------------
    
    public void onFocus()
    {
        this.focused = true;
    }
    
    public void onUnfocus()
    {
        this.focused = false;
    }
    
    public void onMouseEnter()
    {
        this.hover = true;
    }
    
    public void onMouseExit()
    {
        this.hover = false;
    }
    
    public void onMouseHover(double hoverTime)
    {
    
    }
    
    public boolean onMouseButtonDown(Mouse.Button button, double x, double y)
    {
        // return this.mBDown != null && this.mBDown.fire(button, widgetX, widgetY);
        return false;
    }
    
    public boolean onMouseButtonUp(Mouse.Button button, double x, double y)
    {
        // return this.mBDown != null && this.mBDown.fire(button, widgetX, widgetY);
        return false;
    }
    
    public boolean onMouseButtonClicked(Mouse.Button button, double x, double y, boolean doubleClicked)
    {
        // return this.mBDown != null && this.mBDown.fire(button, widgetX, widgetY);
        return false;
    }
    
    public boolean onMouseButtonHeld(Mouse.Button button, double x, double y)
    {
        // return this.mBDown != null && this.mBDown.fire(button, widgetX, widgetY);
        return false;
    }
    
    public boolean onMouseButtonRepeated(Mouse.Button button, double x, double y)
    {
        // return this.mBDown != null && this.mBDown.fire(button, widgetX, widgetY);
        return false;
    }
    
    public boolean onMouseButtonDragged(Mouse.Button button, double x, double y, double dragX, double dragY, double relX, double relY)
    {
        return false;
    }
    
    public boolean onMouseScrolled(double x, double y)
    {
        return false;
    }
}
