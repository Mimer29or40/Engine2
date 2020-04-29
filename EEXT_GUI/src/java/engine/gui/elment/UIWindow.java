package engine.gui.elment;

import engine.gui.UIContainer;
import engine.gui.UIElement;
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
    
    public UIWindow(Rectc rect, String title, boolean resizable, String objectID, String elementID)
    {
        super(rect, null, null, objectID, elementID != null ? elementID : "window");
        
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
    public void position(int x, int y)
    {
        super.position(x, y);
        
        // if (this.rootContainer != null)
        // {
        //     this.rootContainer.position(this.rect.x() + this.shadowSize, this.rect.y() + this.shadowWidth);
        // }
    }
    
    @Override
    public void dimensions(int width, int height)
    {
        // width = Math.min(this.container.rect.width(), Math.max(this.minWidth, width));
        // height = Math.min(this.container.rect.height(), Math.max(this.minHeight, height));
        
        super.dimensions(width, height);
        
        if (this.rootContainer != null)
        {
            // int newWidth  = this.rect.width() - (2 * this.shadowWidth);
            // int newHeight = this.rect.height() - (2 * this.shadowWidth);
            // if (this.rootContainer.rect.width() != newWidth || this.rootContainer.rect.height() != newHeight)
            // {
            //     this.rootContainer.dimensions(newWidth, newHeight);
            //     this.rootContainer.position(this.rect.x() + this.shadowWidth, this.rect.y() + this.shadowWidth);
            // }
        }
    }
    
    @Override
    public void rebuild()
    {
        super.rebuild();
        
        if (this.rootContainer == null)
        {
            this.rootContainer = new UIContainer(this.rect.copy(), null, this, "#window_root_container");
        }
        if (this.elementContainer == null)
        {
            Rect rect = new Rect(borderWidth(),
                                 this.titleHeight,
                                 this.rootContainer.rect().width() - (2 * borderWidth()),
                                 this.rootContainer.rect().height() - (this.titleHeight + borderWidth()));
            this.elementContainer = new UIContainer(rect, this.rootContainer, this, "#window_element_container");
        }
    }
    
    @Override
    public UIContainer getContainer()
    {
        return this.elementContainer;
    }
}
