package engine.gui.elment;

import engine.gui.UIElement;
import engine.gui.interfaces.IUIContainerLike;
import engine.gui.util.Rectc;
import engine.input.Mouse;

public class UIButton extends UIElement
{
    protected String text, tooltipText;
    
    protected boolean down;
    protected boolean selected;
    
    public UIButton(Rectc rect, IUIContainerLike container, String text, String toolTipText, UIElement parent, String objectID)
    {
        super(rect, container, parent, objectID, "button");
        
        this.text = text;
        this.tooltipText = toolTipText;
    }
    
    public void setActive()
    {
        setState("active");
    }
    
    public void setInactive()
    {
        setState(canHover() && this.hovered ? "hovered" : "normal");
    }
    
    public void select()
    {
        this.selected = true;
        setState("selected");
    }
    
    public void unselect()
    {
        this.selected = false;
        setState(canHover() && this.hovered ? "hovered" : "normal");
    }
    
    // -------------------
    // ----- UPDATES -----
    // -------------------
    
    /**
     * Updates the element.
     *
     * @param elapsedTime The amount of time in seconds since the last update.
     * @param mouseX
     * @param mouseY
     * @return If the GUI should be redrawn.
     */
    @Override
    public boolean update(double elapsedTime, int mouseX, int mouseY)
    {
        boolean redraw = super.update(elapsedTime, mouseX, mouseY);
        
        
        
        return redraw;
    }
    
    
    // ------------------
    // ----- EVENTS -----
    // ------------------
    
    @Override
    public void onMouseEnter()
    {
        super.onMouseEnter();
        
        if (canHover()) setState("hovered");
    }
    
    @Override
    public void onMouseExit()
    {
        super.onMouseExit();
    
        setState("normal");
        
        // if (this.tooltip != null)
        // {
        //     this.tooltip.kill();
        //     this.tooltip = null;
        // }
    }
    
    @Override
    protected void onMouseHover(double hoverTime, int elementX, int elementY)
    {
        super.onMouseHover(hoverTime, elementX, elementY);
    
        // if (this.toolTip == null && this.tooltipText != null && hoverTime > this.toolTipDelay)
        // {
        //     int hoverHeight = this.rect.height() >> 1;
        //     self.tooltip = self.ui_manager.create_tool_tip(text = this.tooltipText,
        //                                                     position = (mouse_pos[0], self.rect.centery),
        //                                                     hover_distance = (0, hoverHeight));
        // }
    }
    
    @Override
    public boolean onMouseButtonDown(Mouse.Button button, double x, double y)
    {
        boolean consumed = super.onMouseButtonDown(button, x, y);
        
        if (!consumed)
        {
            setActive();
            // if (this.tooltip != null)
            // {
            //     this.tooltip.kill();
            //     this.tooltip = null;
            // }
        }
        
        return consumed;
    }
    
    @Override
    public boolean onMouseButtonUp(Mouse.Button button, double x, double y)
    {
        boolean consumed = super.onMouseButtonUp(button, x, y);
    
        if (!consumed)
        {
            setInactive();
            // if (this.tooltip != null)
            // {
            //     this.tooltip.kill();
            //     this.tooltip = null;
            // }
        }
        
        return consumed;
    }
}
