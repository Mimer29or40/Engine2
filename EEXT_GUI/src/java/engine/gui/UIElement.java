package engine.gui;

import engine.gui.interfaces.*;
import engine.gui.util.Rect;
import engine.gui.util.Rectc;
import engine.input.Keyboard;
import engine.input.Mouse;
import engine.render.Texture;
import engine.util.PairS;

import java.util.HashMap;

import static engine.Engine.*;

public abstract class UIElement
{
    protected int layer          = 0;
    protected int requestedLayer = 0;
    
    protected final Rect rect = new Rect();
    
    protected final UIContainer container;
    
    // ----- Theme Stuffs -----
    protected String state, prevState;
    protected Texture stateTexture, prevStateTexture;
    protected boolean redrawStates;
    
    
    protected       Texture                transitionTexture;
    protected       double                 transitionRemaining  = 0;
    protected       double                 transitionDuration   = 0;
    protected       double                 transitionPercentage = 0;
    protected final HashMap<PairS, Double> transitionTimes      = new HashMap<>();
    
    protected String[] objectIDs;
    protected String[] elementIDs;
    protected int      borderWidth = 1;
    
    // ----- Property Stuffs -----
    protected boolean alive   = true;
    protected boolean visible = true;
    protected boolean focused = false;
    protected boolean hovered = false;
    protected boolean enabled = false;
    
    public UIElement(Rectc rect, IUIContainerLike container, UIElement parent, String objectID, String elementID)
    {
        this.rect.set(rect);
        
        this.container = container != null ? container.getContainer() : null;
        
        if (this.container != null) { this.container.addElement(this); }
        else { GUI.INSTANCE.elements.add(this); }
        
        UIElement idParent = parent;
        if (parent == null && this.container != null) idParent = this.container;
        if (objectID != null && (objectID.contains(".") || objectID.contains(" "))) throw new RuntimeException("Object ID cannot contain fullstops or spaces: " + objectID);
        if (idParent != null)
        {
            this.objectIDs = new String[idParent.objectIDs.length + 1];
            System.arraycopy(idParent.objectIDs, 0, this.objectIDs, 0, idParent.objectIDs.length);
            this.objectIDs[idParent.objectIDs.length] = objectID;
            
            this.elementIDs = new String[idParent.elementIDs.length + 1];
            System.arraycopy(idParent.elementIDs, 0, this.elementIDs, 0, idParent.elementIDs.length);
            this.elementIDs[idParent.elementIDs.length] = elementID;
        }
        else
        {
            this.objectIDs  = new String[] {objectID};
            this.elementIDs = new String[] {elementID};
        }
        
        setState("normal");
        rebuild();
        
        this.transitionTimes.put(new PairS("hovered", "normal"), 1.0);
        this.transitionTimes.put(new PairS("normal", "hovered"), 1.0);
    }
    
    public void kill()
    {
        this.alive = false;
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
        this.stateTexture      = new Texture(this.rect.width(), this.rect.height(), 4);
        this.prevStateTexture  = new Texture(this.rect.width(), this.rect.height(), 4);
        this.transitionTexture = new Texture(this.rect.width(), this.rect.height(), 4);
    }
    
    public UIElement getTopElement(double mouseX, double mouseY)
    {
        UIElement top = null;
        if (alive() && visible())
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
    
    public boolean mouseOver(double mouseX, double mouseY)
    {
        return this.rect.collide((int) mouseX - absX() + this.rect.x(), (int) mouseY - absY() + this.rect.y());
    }
    
    // ----------------------
    // ----- PROPERTIES -----
    // ----------------------
    
    public boolean alive()
    {
        return this.alive;
    }
    
    public boolean focused()
    {
        return this.focused;
    }
    
    public boolean visible()
    {
        return this.visible;
    }
    
    public boolean hovered()
    {
        return this.hovered;
    }
    
    public boolean canHover()
    {
        return this.alive;
    }
    
    public boolean enabled()
    {
        return this.enabled;
    }
    
    // --------------------
    // ----- THEMEING -----
    // --------------------
    
    protected void setState(String state)
    {
        if (!state.equals(this.state))
        {
            this.prevState = this.state;
            this.state     = state;
            
            this.redrawStates = true;
            
            PairS statePair = new PairS(this.prevState, this.state);
            if (this.prevState != null && this.transitionTimes.containsKey(statePair))
            {
                this.transitionDuration   = this.transitionTimes.get(statePair);
                this.transitionRemaining  = this.transitionDuration - this.transitionRemaining;
                this.transitionPercentage = 0;
            }
            else
            {
                this.transitionRemaining = 0;
            }
        }
    }
    
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
     *
     * @param elapsedTime The amount of time in seconds since the last update.
     * @return If the GUI should be redrawn.
     */
    public boolean update(double elapsedTime, double mouseX, double mouseY)
    {
        if (alive())
        {
            boolean redraw = false;
    
            if (this.transitionDuration > 0)
            {
                this.transitionRemaining -= elapsedTime;
                if (this.transitionRemaining > 0)
                {
                    this.transitionPercentage = 1 - (this.transitionRemaining / this.transitionDuration);
                    redraw                    = true;
                }
                else
                {
                    this.transitionRemaining = 0;
                }
            }
    
    
            return redraw | this.redrawStates;
        }
        return false;
    }
    
    protected boolean updateWindow(double elapsedTime, double mouseX, double mouseY)
    {
        return false;
    }
    
    // -------------------
    // ----- Drawing -----
    // -------------------
    
    public Texture texture()
    {
        return this.transitionRemaining > 0 ? this.transitionTexture : this.stateTexture;
    }
    
    public void draw(double elapsedTime, int mouseX, int mouseY)
    {
        if (this.redrawStates)
        {
            if (this.prevState != null)
            {
                target(this.prevStateTexture);
                drawState(this.prevState);
            }
            
            target(this.stateTexture);
            drawState(this.state);
            
            this.redrawStates = false;
        }
        
        if (this.transitionRemaining > 0)
        {
            target(this.transitionTexture);
            interpolateTexture(this.prevStateTexture, this.stateTexture, this.transitionPercentage, 0, 0);
        }
    }
    
    public void drawState(String state)
    {
        String stateBorder     = state + "_border";
        String stateBackground = state + "_bg";
        
        fill(GUI.theme().getColor(this.objectIDs, this.elementIDs, stateBorder));
        fillRect(0, 0, this.rect.width(), this.rect.height());
        
        fill(GUI.theme().getColor(this.objectIDs, this.elementIDs, stateBackground));
        fillRect(this.borderWidth, this.borderWidth, this.rect.width() - (this.borderWidth << 1), this.rect.height() - (this.borderWidth << 1));
    }
    
    // ------------------
    // ----- EVENTS -----
    // ------------------
    
    private IFocus   focus;
    private IUnfocus unfocus;
    
    private IMouseEntered  mEntered;
    private IMouseExited   mExited;
    private IMouseHovered  mHovered;
    private IMouseScrolled mScrolled;
    
    private IMouseButtonDown     mBDown;
    private IMouseButtonUp       mBUp;
    private IMouseButtonHeld     mBHeld;
    private IMouseButtonRepeated mBRepeated;
    private IMouseButtonClicked  mBClicked;
    private IMouseButtonDragged  mBDragged;
    
    private IKeyboardKeyDown     kKDown;
    private IKeyboardKeyUp       kKUp;
    private IKeyboardKeyHeld     kKHeld;
    private IKeyboardKeyRepeated kKRepeated;
    private IKeyboardKeyPressed  kKPressed;
    private IKeyboardKeyTyped    kKTyped;
    
    /**
     * Sets the function that is called in onFocus.
     *
     * @param focus The IFocus like object.
     */
    public void onFocus(IFocus focus)
    {
        this.focus = focus;
    }
    
    /**
     * Called whenever the element is focused.
     */
    protected void onFocus()
    {
        this.focused = true;
        if (this.focus != null) this.focus.fire();
    }
    
    /**
     * Sets the function that is called in onUnfocus.
     *
     * @param unfocus The IUnfocus like object.
     */
    public void onUnfocus(IUnfocus unfocus)
    {
        this.unfocus = unfocus;
    }
    
    /**
     * Called whenever the element is unfocused.
     */
    protected void onUnfocus()
    {
        this.focused = false;
        if (this.unfocus != null) this.unfocus.fire();
    }
    
    /**
     * Sets the function that is called in onMouseEnter.
     *
     * @param mouseEntered The IMouseEntered like object.
     */
    public void onMouseEnter(IMouseEntered mouseEntered)
    {
        this.mEntered = mouseEntered;
    }
    
    /**
     * Called when the mouse entered the element and it not covered by another element under the mouse.
     */
    protected void onMouseEnter()
    {
        if (canHover()) this.hovered = true;
        if (this.mEntered != null) this.mEntered.fire();
    }
    
    /**
     * Sets the function that is called in onMouseExit.
     *
     * @param mouseExited The IMouseExited like object.
     */
    public void onMouseExit(IMouseExited mouseExited)
    {
        this.mExited = mouseExited;
    }
    
    /**
     * Called whenever the mouse has left the element or covered by another element.
     */
    protected void onMouseExit()
    {
        this.hovered = false;
        if (this.mExited != null) this.mExited.fire();
    }
    
    /**
     * Sets the function that is called in onMouseHover.
     *
     * @param mouseHovered The IMouseHovered like object.
     */
    public void onMouseHover(IMouseHovered mouseHovered)
    {
        this.mHovered = mouseHovered;
    }
    
    /**
     * Called when the mouse is over an element and is hover-able.
     *
     * @param hoverTime The time in seconds that the mouse has been over the element.
     * @param elementX  The x position relative to the top left corner of the UIElement.
     * @param elementY  The y position relative to the top left corner of the UIElement.
     */
    protected void onMouseHover(double hoverTime, double elementX, double elementY)
    {
        if (this.mHovered != null) this.mHovered.fire(hoverTime, elementX, elementY);
    }
    
    /**
     * Sets the function that is called in onMouseScrolled.
     *
     * @param mouseScrolled The IMouseScrolled like object.
     */
    public void onMouseScrolled(IMouseScrolled mouseScrolled)
    {
        this.mScrolled = mouseScrolled;
    }
    
    /**
     * Called whenever the scroll wheel is scrolled while the mouse is over the UIElement and not covered by another UIElement.
     *
     * @param scrollX The x direction that the scroll wheel was moved.
     * @param scrollY The y direction that the scroll wheel was moved.
     * @return If the event should be consumed.
     */
    protected boolean onMouseScrolled(double scrollX, double scrollY)
    {
        return this.mScrolled != null && this.mScrolled.fire(scrollX, scrollY);
    }
    
    /**
     * Sets the function that is called in onMouseButtonDown.
     *
     * @param mouseButtonDown The IMouseButtonDown like object.
     */
    public void onMouseButtonDown(IMouseButtonDown mouseButtonDown)
    {
        this.mBDown = mouseButtonDown;
    }
    
    /**
     * Called when a mouse button is pressed.
     *
     * @param button   The key that was pressed.
     * @param elementX The x position relative to the top left corner of the UIElement.
     * @param elementY The y position relative to the top left corner of the UIElement.
     * @return If the event should be consumed.
     */
    protected boolean onMouseButtonDown(Mouse.Button button, double elementX, double elementY)
    {
        return this.mBDown != null && this.mBDown.fire(button, elementX, elementY);
    }
    
    /**
     * Sets the function that is called in onMouseButtonUp.
     *
     * @param mouseButtonUp The IMouseButtonUp like object.
     */
    public void onMouseButtonUp(IMouseButtonUp mouseButtonUp)
    {
        this.mBUp = mouseButtonUp;
    }
    
    /**
     * Called when a mouse button is released.
     *
     * @param button   The key that was released.
     * @param elementX The x position relative to the top left corner of the UIElement.
     * @param elementY The y position relative to the top left corner of the UIElement.
     * @return If the event should be consumed.
     */
    protected boolean onMouseButtonUp(Mouse.Button button, double elementX, double elementY)
    {
        return this.mBUp != null && this.mBUp.fire(button, elementX, elementY);
    }
    
    /**
     * Sets the function that is called in onMouseButtonHeld.
     *
     * @param mouseButtonHeld The IMouseButtonHeld like object.
     */
    public void onMouseButtonHeld(IMouseButtonHeld mouseButtonHeld)
    {
        this.mBHeld = mouseButtonHeld;
    }
    
    /**
     * Called when a mouse button is held.
     *
     * @param button   The key that was held.
     * @param elementX The x position relative to the top left corner of the UIElement.
     * @param elementY The y position relative to the top left corner of the UIElement.
     * @return If the event should be consumed.
     */
    protected boolean onMouseButtonHeld(Mouse.Button button, double elementX, double elementY)
    {
        return this.mBHeld != null && this.mBHeld.fire(button, elementX, elementY);
    }
    
    /**
     * Sets the function that is called in onMouseButtonRepeated.
     *
     * @param mouseButtonRepeated The IMouseButtonRepeated like object.
     */
    public void onMouseButtonRepeated(IMouseButtonRepeated mouseButtonRepeated)
    {
        this.mBRepeated = mouseButtonRepeated;
    }
    
    /**
     * Called when a mouse button is repeated.
     *
     * @param button   The key that was repeated.
     * @param elementX The x position relative to the top left corner of the UIElement.
     * @param elementY The y position relative to the top left corner of the UIElement.
     * @return If the event should be consumed.
     */
    protected boolean onMouseButtonRepeated(Mouse.Button button, double elementX, double elementY)
    {
        return this.mBRepeated != null && this.mBRepeated.fire(button, elementX, elementY);
    }
    
    /**
     * Sets the function that is called in onMouseButtonClicked.
     *
     * @param mouseButtonClicked The IMouseButtonHeld like object.
     */
    public void onMouseButtonClicked(IMouseButtonClicked mouseButtonClicked)
    {
        this.mBClicked = mouseButtonClicked;
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
    protected boolean onMouseButtonClicked(Mouse.Button button, double elementX, double elementY, boolean doubleClicked)
    {
        return this.mBClicked != null && this.mBClicked.fire(button, elementX, elementY, doubleClicked);
    }
    
    /**
     * Sets the function that is called in onMouseButtonDragged.
     *
     * @param mouseButtonDragged The IMouseButtonHeld like object.
     */
    public void onMouseButtonDragged(IMouseButtonDragged mouseButtonDragged)
    {
        this.mBDragged = mouseButtonDragged;
    }
    
    /**
     * Called whenever the mouse is dragged over an element.
     *
     * @param button   The button is down.
     * @param elementX The x position relative to the top left corner of the UIElement.
     * @param elementY The y position relative to the top left corner of the UIElement.
     * @param dragX    The absolute x position of the start of the drag.
     * @param dragY    The absolute y position of the start of the drag.
     * @param relX     The relative x position since the last frame.
     * @param relY     The relative y position since the last frame.
     * @return If the event should be consumed.
     */
    protected boolean onMouseButtonDragged(Mouse.Button button, double elementX, double elementY, double dragX, double dragY, double relX, double relY)
    {
        return this.mBDragged != null && this.mBDragged.fire(button, elementX, elementY, dragX, dragY, relX, relY);
    }
    
    /**
     * Sets the function that is called in onKeyboardKeyDown.
     *
     * @param keyboardKeyDown The IKeyboardKeyDown like object.
     */
    public void onKeyboardKeyDown(IKeyboardKeyDown keyboardKeyDown)
    {
        this.kKDown = keyboardKeyDown;
    }
    
    /**
     * Called when a UIElement is focused and a key is pressed down.
     *
     * @param key The key that was pressed down.
     * @return If the event should be consumed.
     */
    protected boolean onKeyboardKeyDown(Keyboard.Key key)
    {
        return this.kKDown != null && this.kKDown.fire(key);
    }
    
    /**
     * Sets the function that is called in onKeyboardKeyUp.
     *
     * @param keyboardKeyUp The IKeyboardKeyUp like object.
     */
    public void onKeyboardKeyUp(IKeyboardKeyUp keyboardKeyUp)
    {
        this.kKUp = keyboardKeyUp;
    }
    
    /**
     * Called when a UIElement is focused and a key is released.
     *
     * @param key The key that was released.
     * @return If the event should be consumed.
     */
    protected boolean onKeyboardKeyUp(Keyboard.Key key)
    {
        return this.kKUp != null && this.kKUp.fire(key);
    }
    
    /**
     * Sets the function that is called in onKeyboardKeyHeld.
     *
     * @param keyboardKeyHeld The IMouseButtonHeld like object.
     */
    public void onKeyboardKeyHeld(IKeyboardKeyHeld keyboardKeyHeld)
    {
        this.kKHeld = keyboardKeyHeld;
    }
    
    /**
     * Called when a UIElement is focused and a key is held.
     *
     * @param key The key that was held.
     * @return If the event should be consumed.
     */
    protected boolean onKeyboardKeyHeld(Keyboard.Key key)
    {
        return this.kKHeld != null && this.kKHeld.fire(key);
    }
    
    /**
     * Sets the function that is called in onKeyboardKeyRepeated.
     *
     * @param keyboardKeyRepeated The IMouseButtonHeld like object.
     */
    public void onKeyboardKeyRepeated(IKeyboardKeyRepeated keyboardKeyRepeated)
    {
        this.kKRepeated = keyboardKeyRepeated;
    }
    
    /**
     * Called when a UIElement is focused and a key is repeated.
     *
     * @param key The key that was repeated.
     * @return If the event should be consumed.
     */
    protected boolean onKeyboardKeyRepeated(Keyboard.Key key)
    {
        return this.kKRepeated != null && this.kKRepeated.fire(key);
    }
    
    /**
     * Sets the function that is called in onKeyboardKeyPressed.
     *
     * @param keyboardKeyPressed The IMouseButtonHeld like object.
     */
    public void onKeyboardKeyPressed(IKeyboardKeyPressed keyboardKeyPressed)
    {
        this.kKPressed = keyboardKeyPressed;
    }
    
    /**
     * Called when a UIElement is focused and a key is pressed, then released in an amount of time.
     *
     * @param key           The key that was pressed, then released.
     * @param doublePressed If the key was double pressed.
     * @return If the event should be consumed.
     */
    protected boolean onKeyboardKeyPressed(Keyboard.Key key, boolean doublePressed)
    {
        return this.kKPressed != null && this.kKPressed.fire(key, doublePressed);
    }
    
    /**
     * Sets the function that is called in onKeyboardKeyTyped.
     *
     * @param keyboardKeyTyped The IMouseButtonHeld like object.
     */
    public void onKeyboardKeyTyped(IKeyboardKeyTyped keyboardKeyTyped)
    {
        this.kKTyped = keyboardKeyTyped;
    }
    
    /**
     * Fired whenever a UIElement is focused and a key is pressed that has a character associated with it.
     *
     * @param character The key's character.
     * @return If the event should be consumed.
     */
    protected boolean onKeyboardKeyTyped(char character)
    {
        return this.kKTyped != null && this.kKTyped.fire(character);
    }
}
