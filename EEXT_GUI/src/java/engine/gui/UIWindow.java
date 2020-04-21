package engine.gui;

import engine.gui.interfaces.IUIContainerLike;
import engine.gui.util.Rect;
import engine.gui.util.Rectc;

public class UIWindow extends UIElement implements IUIContainerLike
{
    protected String  title;
    protected boolean resizable;
    
    protected UIContainer rootContainer;
    protected UIContainer elementContainer;
    
    protected int titleHeight = 28;
    
    protected boolean blocking = false;
    
    public UIWindow(Rectc rect, String title, boolean resizable)
    {
        super(rect, null);
        
        this.title     = title;
        this.resizable = resizable;
    }
    
    public boolean blocking()
    {
        return this.blocking;
    }
    
    public UIWindow blocking(boolean blocking)
    {
        this.blocking = blocking;
        return this;
    }
    
    @Override
    public UIElement position(int x, int y)
    {
        super.position(x, y);
        
        if (this.rootContainer != null)
        {
            this.rootContainer.position(this.rect.x1() + this.shadowWidth, this.rect.y1() + this.shadowWidth);
        }
        
        return this;
    }
    
    @Override
    public UIElement dimensions(int width, int height)
    {
        // width = Math.min(this.container.rect.width(), Math.max(this.minWidth, width));
        // height = Math.min(this.container.rect.height(), Math.max(this.minHeight, height));
        
        super.dimensions(width, height);
        
        if (this.rootContainer != null)
        {
            int newWidth = this.rect.width() - (2 * this.shadowWidth);
            int newHeight = this.rect.height() - (2 * this.shadowWidth);
            if (this.rootContainer.rect.width() != newWidth || this.rootContainer.rect.height() != newHeight)
            {
                this.rootContainer.dimensions(newWidth, newHeight);
                this.rootContainer.position(this.rect.x1() + this.shadowWidth, this.rect.y1() + this.shadowWidth);
            }
        }
        
        return this;
    }
    
    @Override
    public void rebuild()
    {
        if (this.rootContainer == null)
        {
            this.rootContainer = new UIContainer(new Rect(this.rect.x1() + this.shadowWidth,
                                                          this.rect.y1() + this.shadowWidth,
                                                          this.rect.width() - (2 * this.shadowWidth),
                                                          this.rect.height() - (2 * this.shadowWidth)),
                                                 null);
        }
        if (this.elementContainer == null)
        {
            this.elementContainer = new UIContainer(new Rect(this.borderWidth,
                                                             this.titleHeight,
                                                             this.rootContainer.rect.width() - (2 * this.borderWidth),
                                                             this.rootContainer.rect.height() - (this.titleHeight + this.borderWidth)),
                                                    this.rootContainer);
        }
    }
    
    @Override
    public UIContainer getContainer()
    {
        return this.elementContainer;
    }
}
