package engine.gui.elment;

import engine.gui.UIElement;
import engine.gui.interfaces.IUIContainerLike;
import engine.gui.util.Rectc;
import engine.input.Mouse;

public class UIButton extends UIElement
{
    protected String text, tooltipText;
    
    protected boolean selected;
    
    public UIButton(Rectc rect, IUIContainerLike container, String text, String toolTipText, UIElement parent, String objectID)
    {
        super(rect, container, parent, objectID, "button");
        
        this.text = text;
        this.tooltipText = toolTipText;
    }
    
    public void select()
    {
        this.selected = true;
        setState("selected");
    }
    
    public void unselect()
    {
        this.selected = false;
        setState(canHover() && hovered() ? "hovered" : "normal");
    }
    
    // ----------------------
    // ----- PROPERTIES -----
    // ----------------------
    
    @Override
    protected void enabledChanged(boolean prevState, boolean newState)
    {
        pushed(false);
        setState(!newState ? "disabled" : canHover() && hovered() ? "hovered" : "normal");
        redraw();
    }
    
    public boolean pushed;
    
    public boolean pushed()
    {
        return this.pushed;
    }
    
    public void pushed(boolean pushed)
    {
        if (this.pushed != pushed)
        {
            this.pushed = pushed;
            setState(pushed() ? "active" : canHover() && hovered() ? "hovered" : "normal");
            redraw();
        }
    }
    
    public void togglePushed()
    {
        pushed(!pushed());
    }
    
    private boolean toggleable = false;
    
    public boolean toggleable()
    {
        return this.toggleable;
    }
    
    public void toggleable(boolean toggleable)
    {
        if (this.toggleable != toggleable)
        {
            this.toggleable = toggleable;
            pushed(false);
        }
    }
    
    public void toggleToggleable()
    {
        toggleable(!toggleable());
    }
    
    // -------------------
    // ----- UPDATES -----
    // -------------------
    
    /**
     * Updates the element. This is called and past along to its children as long as it is alive.
     *
     * @param elapsedTime The amount of time in seconds since the last update.
     * @param mouseX      The x position of the mouse.
     * @param mouseY      The y position of the mouse.
     * @return If the element should be redrawn.
     */
    @Override
    public boolean update(double elapsedTime, double mouseX, double mouseY)
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
        
        if (enabled() && canHover() && !pushed()) setState("hovered");
    }
    
    @Override
    public void onMouseExit()
    {
        super.onMouseExit();
    
        if (enabled() && (!toggleable() || !pushed()))
        {
            setState("normal");
            pushed(false);
        }
        
        // if (this.tooltip != null)
        // {
        //     this.tooltip.kill();
        //     this.tooltip = null;
        // }
    }
    
    @Override
    protected void onMouseHover(double hoverTime, double elementX, double elementY)
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
    public boolean onMouseButtonDown(Mouse.Button button, double elementX, double elementY)
    {
        if (!super.onMouseButtonDown(button, elementX, elementY) && enabled())
        {
            if (toggleable())
            {
                togglePushed();
                // onButtonDown(button, elementX, elementY);
                // onButtonToggled(button, elementX, elementY, pushed());
            }
            else if (!pushed())
            {
                pushed(true);
                // onButtonDown(button, elementX, elementY);
            }
            // if (this.tooltip != null)
            // {
            //     this.tooltip.kill();
            //     this.tooltip = null;
            // }
            return true;
        }
        
        return false;
    }
    
    @Override
    public boolean onMouseButtonUp(Mouse.Button button, double elementX, double elementY)
    {
        if (!super.onMouseButtonUp(button, elementX, elementY) && enabled())
        {
            if (toggleable())
            {
                // onButtonUp(button, elementX, elementY);
            }
            else if (pushed())
            {
                pushed(false);
                // onButtonUp(button, elementX, elementY);
            }
            // if (this.tooltip != null)
            // {
            //     this.tooltip.kill();
            //     this.tooltip = null;
            // }
            return true;
        }
        
        return false;
    }
}
