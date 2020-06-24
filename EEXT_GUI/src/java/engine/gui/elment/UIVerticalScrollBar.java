package engine.gui.elment;

import engine.gui.GUI;
import engine.gui.UIElement;
import engine.gui.interfaces.IFocus;
import engine.gui.util.Rect;
import engine.gui.util.Rectc;
import engine.input.Mouse;
import engine.render.Texture;
import org.joml.Vector2i;
import org.joml.Vector2ic;

import static engine.util.Util.clamp;

/**
 * A vertical scroll bar allows users to position a smaller visible area within a vertically
 * larger area.
 */
public class UIVerticalScrollBar extends UIElement
{
    private UIContainer buttonContainer;
    private UIButton    slidingButton;
    private UIButton    topButton;
    private UIButton    bottomButton;
    
    public UIVerticalScrollBar(double visiblePercentage, Rectc rect, UIElement parent, UIElement themeParent, String objectID)
    {
        super(rect, parent, themeParent, objectID, "vertical_scroll_bar", new String[] {"normal"});
        
        this.arrowButtonHeight = 20;
        
        this.startingGrabYDifference = 0.0;
        
        this.startPercentage   = 0.0;
        this.visiblePercentage = clamp(visiblePercentage, 1.0);
        
        // this.grabbedSlider    = false;
        // this.hasMovedRecently = false;
        // this.scrollWheelUp    = false;
        // this.scrollWheelDown  = false;
        
        rebuildTheme();
    }
    
    protected boolean wasLastFocused()
    {
        UIElement lastFocused = GUI.focusedVScrollbar();
        return lastFocused != null && (lastFocused == this || lastFocused == this.slidingButton || lastFocused == this.topButton || lastFocused == this.bottomButton);
    }
    
    /**
     * Removes the element from its container and prevents further updates.
     */
    @Override
    public void kill()
    {
        GUI.clearFocusedVScrollbar(this);
        GUI.clearFocusedVScrollbar(this.slidingButton);
        GUI.clearFocusedVScrollbar(this.topButton);
        GUI.clearFocusedVScrollbar(this.bottomButton);
        
        super.kill();
    }
    
    /**
     * Rebuilds the necessary textures and sub elements of the element.
     */
    @Override
    public void rebuild()
    {
        this.texture      = new Texture(rect().width(), rect().height(), 3);
        this.stateTexture = new Texture(rect().width(), rect().height(), 3);
        
        this.backgroundRect.set(borderWidth(), borderWidth(), rect().width() - (borderWidth() << 1), rect().height() - (borderWidth() << 1));
        
        if (this.buttonContainer == null)
        {
            this.buttonContainer = new UIContainer(backgroundRect(), this, this, "#vert_scrollbar_buttons_container");
        }
        else
        {
            this.buttonContainer.dimensions(backgroundRect().width(), backgroundRect().height());
            this.buttonContainer.position(backgroundRect().x(), backgroundRect().y());
        }
        
        if (arrowButtonsEnabled())
        {
            this.arrowButtonHeight = 20;
            
            if (this.topButton == null)
            {
                this.topButton = new UIButton("▲",
                                              new Rect(0, 0, backgroundRect().width(), this.arrowButtonHeight),
                                              this.buttonContainer,
                                              null,
                                              this,
                                              "#top_button")
                {
                    @Override
                    protected void onButtonHeld(Mouse.Button button, double elementX, double elementY)
                    {
                        UIVerticalScrollBar.this.nextScroll -= 1;
                    }
                };
            }
            
            if (this.bottomButton == null)
            {
                this.bottomButton = new UIButton("▼",
                                                 new Rect(0, backgroundRect().height() - this.arrowButtonHeight, backgroundRect().width(), this.arrowButtonHeight),
                                                 this.buttonContainer,
                                                 null,
                                                 this,
                                                 "#bottom_button")
                {
                    @Override
                    protected void onButtonHeld(Mouse.Button button, double elementX, double elementY)
                    {
                        UIVerticalScrollBar.this.nextScroll += 1;
                    }
                };
            }
        }
        else
        {
            this.arrowButtonHeight = 0;
            if (this.topButton != null)
            {
                this.topButton.kill();
                this.topButton = null;
            }
            if (this.bottomButton != null)
            {
                this.bottomButton.kill();
                this.bottomButton = null;
            }
        }
        
        this.scrollHeight    = backgroundRect().height() - (arrowButtonHeight() << 1);
        this.scrollBarHeight = Math.max(5, (int) (scrollHeight() * visiblePercentage()));
        
        this.topLimit    = 0;
        this.bottomLimit = topLimit() + scrollHeight();
        
        this.scrollPosition = clamp(scrollPosition(), topLimit(), bottomLimit() - scrollBarHeight());
        
        this.slidingRectPosition.set(0, scrollPosition() + arrowButtonHeight());
        
        if (this.slidingButton == null)
        {
            this.slidingButton = new UIButton("",
                                              new Rect(slidingRectPosition().x(), slidingRectPosition().y(), backgroundRect().width(), scrollBarHeight()),
                                              this.buttonContainer,
                                              null,
                                              this,
                                              "#sliding_button");
        }
        else
        {
            this.slidingButton.position(slidingRectPosition().x(), slidingRectPosition().y());
            this.slidingButton.dimensions(backgroundRect().width(), scrollHeight());
        }
        this.slidingButton.holdRange(100, backgroundRect().height());
        
        redrawStates();
    }
    
    // ----------------------
    // ----- PROPERTIES -----
    // ----------------------
    
    private final Rect backgroundRect = new Rect();
    
    private boolean arrowButtonsEnabled = true;
    private int     arrowButtonHeight;
    
    private final double visiblePercentage;
    private       double startPercentage;
    
    private double nextScroll = 0;
    
    private int scrollHeight;
    private int scrollBarHeight;
    private int topLimit;
    private int bottomLimit;
    private int scrollPosition;
    
    private final double startingGrabYDifference;
    
    private final Vector2i slidingRectPosition = new Vector2i();
    
    public Rectc backgroundRect()
    {
        return this.backgroundRect;
    }
    
    public boolean arrowButtonsEnabled()
    {
        return this.arrowButtonsEnabled;
    }
    
    public void arrowButtonsEnabled(boolean arrowButtonsEnabled)
    {
        this.arrowButtonsEnabled = arrowButtonsEnabled;
    }
    
    public int arrowButtonHeight()
    {
        return this.arrowButtonHeight;
    }
    
    public double visiblePercentage()
    {
        return this.visiblePercentage;
    }
    
    public double startPercentage()
    {
        return this.startPercentage;
    }
    
    public int scrollHeight()
    {
        return this.scrollHeight;
    }
    
    public int scrollBarHeight()
    {
        return this.scrollBarHeight;
    }
    
    public int topLimit()
    {
        return this.topLimit;
    }
    
    public int bottomLimit()
    {
        return this.bottomLimit;
    }
    
    public int scrollPosition()
    {
        return this.scrollPosition;
    }
    
    public double startingGrabYDifference()
    {
        return this.startingGrabYDifference;
    }
    
    public Vector2ic slidingRectPosition()
    {
        return this.slidingRectPosition;
    }
    
    // --------------------
    // ----- THEMEING -----
    // --------------------
    
    // -------------------
    // ----- UPDATES -----
    // -------------------
    
    /**
     * Updates the element. This is called and past along to its children as long as it is alive.
     * <p>
     * Can be overridden.
     *
     * @param elapsedTime The amount of time in seconds since the last update.
     * @param mouseX      The x position of the mouse.
     * @param mouseY      The y position of the mouse.
     * @return If the GUI window should be redrawn.
     */
    @Override
    protected boolean updateElement(double elapsedTime, double mouseX, double mouseY)
    {
        boolean movedThisFrame = false;
        
        if (this.nextScroll != 0)
        {
            this.scrollPosition += this.nextScroll;
            this.scrollPosition = clamp(scrollPosition(), topLimit(), bottomLimit() - scrollBarHeight());
    
            this.slidingRectPosition.set(0, scrollPosition() + arrowButtonHeight());
            this.slidingButton.position(slidingRectPosition().x(), slidingRectPosition().y());
            
            this.nextScroll = 0;
            movedThisFrame  = true;
        }
        
        if (movedThisFrame)
        {
            this.startPercentage = (double) scrollPosition() / (double) scrollHeight();
            // this.movedRecently = true;
            
            return true;
        }
        
        return false;
    }
    
    
    // -------------------
    // ----- Drawing -----
    // -------------------
    
    // ------------------
    // ----- EVENTS -----
    // ------------------
    
    
    /**
     * Sets the function that is called in onFocus.
     *
     * @param focus The IFocus like object.
     */
    @Override
    public void onFocus(IFocus focus)
    {
        super.onFocus(focus);
        
        if (this.slidingButton != null) GUI.setFocused(this.slidingButton);
    }
    
    /**
     * Called whenever the scroll wheel is scrolled while the mouse is over the UIElement and not covered by another UIElement.
     *
     * @param scrollX The x direction that the scroll wheel was moved.
     * @param scrollY The y direction that the scroll wheel was moved.
     * @return If the event should be consumed.
     */
    @Override
    protected boolean onMouseScrolled(double scrollX, double scrollY)
    {
        if (!super.onMouseScrolled(scrollX, scrollY))
        {
            if (enabled())
            {
                this.nextScroll -= scrollY;
            }
            return true;
        }
        return false;
    }
}
