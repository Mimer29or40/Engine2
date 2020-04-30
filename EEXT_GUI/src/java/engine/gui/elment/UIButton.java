package engine.gui.elment;

import engine.gui.GUI;
import engine.gui.UIElement;
import engine.gui.interfaces.*;
import engine.gui.util.Rectc;
import engine.input.Mouse;
import engine.render.Texture;

import static engine.Engine.*;

/**
 * A push button, a lot of the appearance of the button, including images to be displayed, is
 * setup via the theme file.
 * <p>
 * The button element is reused throughout the UI as part of other elements as it happens to be a
 * very flexible interactive element.
 */
public class UIButton extends UIElement
{
    protected String tooltipText;
    
    public UIButton(Rectc rect, IUIContainerLike container, String text, String toolTipText, UIElement parent, String objectID)
    {
        super(rect, container, parent, objectID, "button");
        
        this.text        = text;
        this.tooltipText = toolTipText;
        
        rebuildTheme();
    }
    
    public void press()
    {
        onMouseButtonDown(mouse().NONE, 0, 0);
    }
    
    public void release()
    {
        onMouseButtonUp(mouse().NONE, 0, 0);
    }
    
    public void click()
    {
        onMouseButtonClicked(mouse().NONE, 0, 0, false);
    }
    
    public void doubleClick()
    {
        onMouseButtonClicked(mouse().NONE, 0, 0, true);
    }
    
    protected boolean selected;
    
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
    
    private String text = "";
    
    public  boolean pushed     = false;
    private boolean toggleable = false;
    
    /**
     * @return The text in the label.
     */
    public String text()
    {
        return this.text;
    }
    
    /**
     * Sets the text in the label.
     *
     * @param text The new text.
     */
    public void text(String text)
    {
        if (text != null && !this.text.equals(text))
        {
            String prev = this.text;
            this.text = text;
            textChanged(prev, this.text);
        }
    }
    
    /**
     * Called whenever the label text is changed.
     *
     * @param prevText The previous text.
     * @param text     The new text.
     */
    protected void textChanged(String prevText, String text)
    {
        redrawStates();
    }
    
    /**
     * @return If the button is pushed or not.
     */
    public boolean pushed()
    {
        return this.pushed;
    }
    
    /**
     * Sets the button to be pushed or not.
     *
     * @param pushed The new state.
     */
    public void pushed(boolean pushed)
    {
        if (this.pushed != pushed)
        {
            boolean prev = this.pushed;
            this.pushed = pushed;
            pushedChanged(prev, this.pushed);
        }
    }
    
    /**
     * Toggles the pushed state of the button.
     */
    public void togglePushed()
    {
        pushed(!pushed());
    }
    
    /**
     * Called when the pushed state of the button is changed.
     *
     * @param prevState The prev state.
     * @param pushed    The new state.
     */
    protected void pushedChanged(boolean prevState, boolean pushed)
    {
        setState(pushed ? "active" : canHover() && hovered() ? "hovered" : "normal");
        redraw();
    }
    
    /**
     * @return If the button is toggleable or not.
     */
    public boolean toggleable()
    {
        return this.toggleable;
    }
    
    /**
     * Sets the button to be toggleable or not.
     *
     * @param toggleable The new state.
     */
    public void toggleable(boolean toggleable)
    {
        if (this.toggleable != toggleable)
        {
            this.toggleable = toggleable;
            pushed(false);
        }
    }
    
    /**
     * Toggles the toggle state of the button.
     */
    public void toggleToggleable()
    {
        toggleable(!toggleable());
    }
    
    /**
     * Called when the toggleable state of the button is changed.
     *
     * @param prevState  The prev state.
     * @param toggleable The new state.
     */
    protected void toggleChanged(boolean prevState, boolean toggleable)
    {
        pushed(false);
    }
    
    /**
     * Called whenever the enabled state is changed.
     *
     * @param prevState The previous enabled state
     * @param enabled   The new enabled state
     */
    @Override
    protected void enabledChanged(boolean prevState, boolean enabled)
    {
        pushed(false);
        setState(!enabled ? "disabled" : canHover() && hovered() ? "hovered" : "normal");
        redraw();
    }
    
    // --------------------
    // ----- THEMEING -----
    // --------------------
    
    private Texture normalImage;
    private Texture hoveredImage;
    private Texture selectedImage;
    private Texture disabledImage;
    
    public Texture normalImage()
    {
        return this.normalImage;
    }
    
    public Texture hoveredImage()
    {
        return this.hoveredImage;
    }
    
    public Texture selectedImage()
    {
        return this.selectedImage;
    }
    
    public Texture disabledImage()
    {
        return this.disabledImage;
    }
    
    public boolean rebuildImages()
    {
        boolean changed = false;
        
        Texture normalImage = GUI.theme().getImage(this.objectIDs, this.elementIDs, "normal_image");
        if (normalImage != null && !normalImage.equals(this.normalImage))
        {
            this.normalImage   = normalImage;
            this.hoveredImage  = normalImage;
            this.selectedImage = normalImage;
            this.disabledImage = normalImage;
            changed            = true;
        }
        
        Texture hoveredImage = GUI.theme().getImage(this.objectIDs, this.elementIDs, "hovered_image");
        if (hoveredImage != null && !hoveredImage.equals(this.hoveredImage))
        {
            this.hoveredImage = hoveredImage;
            changed           = true;
        }
        
        Texture selectedImage = GUI.theme().getImage(this.objectIDs, this.elementIDs, "selected_image");
        if (selectedImage != null && !selectedImage.equals(this.selectedImage))
        {
            this.selectedImage = selectedImage;
            changed            = true;
        }
        
        Texture disabledImage = GUI.theme().getImage(this.objectIDs, this.elementIDs, "disabled_image");
        if (disabledImage != null && !disabledImage.equals(this.disabledImage))
        {
            this.disabledImage = disabledImage;
            changed            = true;
        }
        
        return changed;
    }
    
    /**
     * Rebuilds all theme information. This method can be overridden by elements.
     *
     * @param anyChanged pass-through
     * @return anyChanged pass-through
     */
    @Override
    public boolean rebuildTheme(boolean anyChanged)
    {
        anyChanged = super.rebuildTheme(anyChanged);
        
        if (rebuildImages()) anyChanged = true;
        
        return anyChanged;
    }
    
    // -------------------
    // ----- UPDATES -----
    // -------------------
    
    // -------------------
    // ----- Drawing -----
    // -------------------
    
    /**
     * Draws the requested state with theme information.
     *
     * @param state The state.
     */
    @Override
    public void drawState(String state)
    {
        super.drawState(state);
        
        drawImageAndText(state);
        
        fill(255, 200, 100, 100);
        fillRect(0, 0, 10, 10);
    }
    
    // ------------------
    // ----- EVENTS -----
    // ------------------
    
    private IButtonDown    bDown;
    private IButtonUp      bUp;
    private IButtonHeld    bHeld;
    private IButtonClicked bClicked;
    private IButtonToggled bToggled;
    
    /**
     * Sets the function that is called in onButtonDown.
     *
     * @param buttonDown The IButtonDown like object.
     */
    public void onButtonDown(IButtonDown buttonDown)
    {
        this.bDown = buttonDown;
    }
    
    /**
     * Called when the button is pressed.
     *
     * @param button   The button that was pressed.
     * @param elementX The x position relative to the top left corner of the UIElement.
     * @param elementY The y position relative to the top left corner of the UIElement.
     */
    protected void onButtonDown(Mouse.Button button, double elementX, double elementY)
    {
        if (this.bDown != null) this.bDown.fire(button, elementX, elementY);
    }
    
    /**
     * Sets the function that is called in onButtonUp.
     *
     * @param buttonUp The IButtonUp like object.
     */
    public void onButtonUp(IButtonUp buttonUp)
    {
        this.bUp = buttonUp;
    }
    
    /**
     * Called when the button is released.
     *
     * @param button   The button that was released.
     * @param elementX The x position relative to the top left corner of the UIElement.
     * @param elementY The y position relative to the top left corner of the UIElement.
     */
    protected void onButtonUp(Mouse.Button button, double elementX, double elementY)
    {
        if (this.bUp != null) this.bUp.fire(button, elementX, elementY);
    }
    
    /**
     * Sets the function that is called in onButtonHeld.
     *
     * @param buttonHeld The IButtonHeld like object.
     */
    public void onButtonHeld(IButtonHeld buttonHeld)
    {
        this.bHeld = buttonHeld;
    }
    
    /**
     * Called when the button is held.
     *
     * @param button   The button that was held.
     * @param elementX The x position relative to the top left corner of the UIElement.
     * @param elementY The y position relative to the top left corner of the UIElement.
     */
    protected void onButtonHeld(Mouse.Button button, double elementX, double elementY)
    {
        if (this.bHeld != null) this.bHeld.fire(button, elementX, elementY);
    }
    
    /**
     * Sets the function that is called in onButtonClicked.
     *
     * @param buttonClicked The IButtonClicked like object.
     */
    public void onButtonClicked(IButtonClicked buttonClicked)
    {
        this.bClicked = buttonClicked;
    }
    
    /**
     * Called when the button is held.
     *
     * @param button        The button that was held.
     * @param elementX      The x position relative to the top left corner of the UIElement.
     * @param elementY      The y position relative to the top left corner of the UIElement.
     * @param doubleClicked If the button was double clicked.
     */
    protected void onButtonClicked(Mouse.Button button, double elementX, double elementY, boolean doubleClicked)
    {
        if (this.bClicked != null) this.bClicked.fire(button, elementX, elementY, doubleClicked);
    }
    
    /**
     * Sets the function that is called in onButtonToggled.
     *
     * @param buttonToggled The IButtonToggled like object.
     */
    public void onButtonToggled(IButtonToggled buttonToggled)
    {
        this.bToggled = buttonToggled;
    }
    
    /**
     * Called when the button is toggled.
     *
     * @param button   The button that was toggled.
     * @param elementX The x position relative to the top left corner of the UIElement.
     * @param elementY The y position relative to the top left corner of the UIElement.
     * @param pushed   If the button is pushed or not.
     */
    protected void onButtonToggled(Mouse.Button button, double elementX, double elementY, boolean pushed)
    {
        if (this.bToggled != null) this.bToggled.fire(button, elementX, elementY, pushed);
    }
    
    /**
     * Called when the mouse entered the element and it not covered by another element under the mouse.
     */
    @Override
    public void onMouseEnter()
    {
        super.onMouseEnter();
        
        if (enabled() && canHover() && !pushed()) setState("hovered");
    }
    
    /**
     * Called whenever the mouse has left the element or covered by another element.
     */
    @Override
    public void onMouseExit()
    {
        super.onMouseExit();
        
        if (enabled() && !toggleable() || !pushed())
        {
            pushed(false);
            setState("normal");
        }
        
        // if (this.tooltip != null)
        // {
        //     this.tooltip.kill();
        //     this.tooltip = null;
        // }
    }
    
    /**
     * Called when the mouse is over an element and is hover-able.
     *
     * @param hoverTime The time in seconds that the mouse has been over the element.
     * @param elementX  The x position relative to the top left corner of the UIElement.
     * @param elementY  The y position relative to the top left corner of the UIElement.
     */
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
    
    /**
     * Called when a mouse button is pressed.
     *
     * @param button   The key that was pressed.
     * @param elementX The x position relative to the top left corner of the UIElement.
     * @param elementY The y position relative to the top left corner of the UIElement.
     * @return If the event should be consumed.
     */
    @Override
    public boolean onMouseButtonDown(Mouse.Button button, double elementX, double elementY)
    {
        if (!super.onMouseButtonDown(button, elementX, elementY) && enabled())
        {
            if (enabled())
            {
                if (!toggleable()) pushed(true);
                if (pushed()) onButtonDown(button, elementX, elementY);
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
    
    /**
     * Called when a mouse button is released.
     *
     * @param button   The key that was released.
     * @param elementX The x position relative to the top left corner of the UIElement.
     * @param elementY The y position relative to the top left corner of the UIElement.
     * @return If the event should be consumed.
     */
    @Override
    public boolean onMouseButtonUp(Mouse.Button button, double elementX, double elementY)
    {
        if (!super.onMouseButtonUp(button, elementX, elementY) && enabled())
        {
            if (enabled())
            {
                boolean prev = pushed();
                if (toggleable()) { togglePushed(); }
                else { pushed(false); }
                if (!pushed()) onButtonUp(button, elementX, elementY);
                if (prev != pushed()) onButtonToggled(button, elementX, elementY, pushed());
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
    
    /**
     * Called when a mouse button is held.
     *
     * @param button   The key that was held.
     * @param elementX The x position relative to the top left corner of the UIElement.
     * @param elementY The y position relative to the top left corner of the UIElement.
     * @return If the event should be consumed.
     */
    @Override
    protected boolean onMouseButtonHeld(Mouse.Button button, double elementX, double elementY)
    {
        if (!super.onMouseButtonHeld(button, elementX, elementY))
        {
            if (enabled())
            {
                if (!toggleable()) pushed(true);
                onButtonHeld(button, elementX, elementY);
            }
            return true;
        }
        return false;
    }
    
    /**
     * Called when a mouse button is pressed, then released in an amount of time.
     *
     * @param button        The key that was pressed, then released.
     * @param elementX      The x position relative to the top left corner of the UIElement.
     * @param elementY      The y position relative to the top left corner of the UIElement.
     * @param doubleClicked If the button was double clicked
     * @return If the event should be consumed.
     */
    @Override
    protected boolean onMouseButtonClicked(Mouse.Button button, double elementX, double elementY, boolean doubleClicked)
    {
        if (!super.onMouseButtonClicked(button, elementX, elementY, doubleClicked))
        {
            if (enabled())
            {
                if (!toggleable()) pushed(true);
                onButtonClicked(button, elementX, elementY, doubleClicked);
            }
            return true;
        }
        return false;
    }
}
