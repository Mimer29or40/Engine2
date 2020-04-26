package engine.gui;

import engine.gui.interfaces.IUIContainerLike;
import engine.gui.util.Rect;
import engine.gui.util.Rectc;
import engine.input.Keyboard;
import engine.input.Mouse;
import engine.render.Texture;

import static engine.Engine.*;

public abstract class UIElement
{
    protected final UIContainer container;
    
    protected int layer          = 0;
    protected int requestedLayer = 0;
    
    protected final Rect rect = new Rect();
    
    protected Texture texture;
    
    // ----- Theme Stuffs -----
    protected String[] objectIDs;
    protected String[] elementIDs;
    protected int borderWidth = 1;
    
    protected boolean alive = true;
    
    protected boolean visible = true;
    
    protected boolean focused = false;
    protected boolean hover   = false;
    protected boolean enabled = false;
    
    public UIElement(Rectc rect, IUIContainerLike container, String objectID)
    {
        this.container = container != null ? container.getContainer() : null;
        
        if (this.container != null)
        {
            this.container.addElement(this);
        }
        else
        {
            GUI.INSTANCE.elements.add(this);
        }
        
        this.rect.set(rect);
        
        rebuild();
    }
    
    public UIElement addThemeInformation(UIElement parent, String objectID)
    {
        return this;
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
            GUI.INSTANCE.elements.remove(this);
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
        return (this.container != null ? this.container.absX() : 0) + this.rect.left();
    }
    
    public int absY()
    {
        return (this.container != null ? this.container.absY() : 0) + this.rect.top();
    }
    
    public UIElement position(int x, int y)
    {
        if (this.rect.x() != x || this.rect.y() != y)
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
            return top != null ? top : mouseOver(mouseX, mouseY) ? this : null;
        }
        return null;
    }
    
    public boolean mouseOver(int mouseX, int mouseY)
    {
        return this.rect.collide(mouseX - absX(), mouseY - absY());
    }
    
    // --------------------
    // ----- THEMEING -----
    // --------------------
    
    public void rebuildTheme()
    {
        boolean anyChange = false;
        
        if (checkThemeSizeChange(1)) anyChange = true;
        
        if (anyChange) rebuild();
    }
    
    protected boolean checkThemeSizeChange(int defaultBorderWidth)
    {
        boolean anyChange = false;
    
        String borderWidthString = GUI.theme().getMiscData(this.objectIDs, this.elementIDs, "border_width");
    
        int borderWidth = defaultBorderWidth;
        if (borderWidthString != null)
        {
            try
            {
                borderWidth = Integer.parseInt(borderWidthString);
            }
            catch (NumberFormatException ignored) { }
        }
        if (this.borderWidth != borderWidth)
        {
            this.borderWidth = borderWidth;
            anyChange        = true;
        }
        
        return anyChange;
    }
    
    // -------------------
    // ----- UPDATES -----
    // -------------------
    
    /**
     * Updates the element.
     * @param elapsedTime The amount of time in seconds since the last update.
     * @return If the GUI should be redrawn.
     */
    public boolean update(double elapsedTime, int mouseX, int mouseY)
    {
        if (mouseX != this.rect.x() || mouseY != this.rect.y())
        {
            this.rect.pos(mouseX, mouseY);
            return true;
        }
        return false;
    }
    
    // -------------------
    // ----- Drawing -----
    // -------------------
    
    public void draw(double elapsedTime, int mouseX, int mouseY)
    {
        push();
        
        target(this.texture);
        
        // clear(Color.BLANK);
        
        fill(GUI.theme().getColor(this.objectIDs, this.elementIDs, "normal_border"));
        fillRect(0, 0, this.rect.width(), this.rect.height());
        
        fill(GUI.theme().getColor(this.objectIDs, this.elementIDs, "normal_bg"));
        fillRect(this.borderWidth, this.borderWidth, this.rect.width() - (this.borderWidth << 1), this.rect.height() - (this.borderWidth << 1));
        
        pop();
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
        return false;
    }
    
    public boolean onMouseButtonHeld(Mouse.Button button, double x, double y)
    {
        return false;
    }
    
    public boolean onMouseButtonRepeated(Mouse.Button button, double x, double y)
    {
        return false;
    }
    
    public boolean onMouseButtonClicked(Mouse.Button button, double x, double y, boolean doubleClicked)
    {
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
    
    public boolean onKeyboardKeyDown(Keyboard.Key key)
    {
        return false;
    }
    
    public boolean onKeyboardKeyUp(Keyboard.Key key)
    {
        return false;
    }
    
    public boolean onKeyboardKeyHeld(Keyboard.Key key)
    {
        return false;
    }
    
    public boolean onKeyboardKeyRepeated(Keyboard.Key key)
    {
        return false;
    }
    
    public boolean onKeyboardKeyPressed(Keyboard.Key key, boolean doublePressed)
    {
        return false;
    }
    
    public boolean onKeyboardKeyTyped(char key)
    {
        return false;
    }
}
