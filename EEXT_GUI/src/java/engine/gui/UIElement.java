package engine.gui;

import engine.Engine;
import engine.gui.interfaces.*;
import engine.gui.util.Rect;
import engine.gui.util.Rectc;
import engine.input.Keyboard;
import engine.input.Mouse;
import engine.render.Font;
import engine.render.RectMode;
import engine.render.TextAlign;
import engine.render.Texture;
import engine.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import static engine.Engine.*;
import static engine.util.Util.println;

/**
 * A base class for UI elements.
 */
public abstract class UIElement
{
    protected int layer          = 0;
    protected int requestedLayer = 0;
    
    public UIElement(Rectc rect, UIElement parent, UIElement themeParent, String objectID, String elementID, String[] states)
    {
        this.rect.set(rect);
        
        this.parent = parent != null ? parent : EEXT_GUI.rootContainer();
        
        if (parent() != null)
        {
            parent().elements.add(this);
            recalculateLayers();
        }
        
        themeParent = themeParent != null ? themeParent : parent();
        if (objectID != null && (objectID.contains(".") || objectID.contains(" "))) throw new RuntimeException("Object ID cannot contain fullstops or spaces: " + objectID);
        if (themeParent != null)
        {
            this.objectIDs = new String[themeParent.objectIDs.length + 1];
            System.arraycopy(themeParent.objectIDs, 0, this.objectIDs, 0, themeParent.objectIDs.length);
            this.objectIDs[themeParent.objectIDs.length] = objectID;
            
            this.elementIDs = new String[themeParent.elementIDs.length + 1];
            System.arraycopy(themeParent.elementIDs, 0, this.elementIDs, 0, themeParent.elementIDs.length);
            this.elementIDs[themeParent.elementIDs.length] = elementID;
        }
        else
        {
            this.objectIDs  = new String[] {objectID};
            this.elementIDs = new String[] {elementID};
        }
        this.states = states;
        
        setState("normal");
    }
    
    /**
     * Removes the element from its container and prevents further updates.
     */
    public void kill()
    {
        this.alive = false;
        if (parent() != null)
        {
            parent().elements.remove(this);
            recalculateLayers();
        }
        while (this.elements.size() > 0) this.elements.remove(0).kill();
    }
    
    /**
     * Rebuilds the necessary textures and sub elements of the element.
     */
    public void rebuild()
    {
        this.texture          = new Texture(rect().width(), rect().height(), 3);
        this.stateTexture     = new Texture(rect().width(), rect().height(), 3);
        this.prevStateTexture = new Texture(rect().width(), rect().height(), 3);
        
        redrawStates();
    }
    
    // -------------------
    // ----- LINEAGE -----
    // -------------------
    
    private final UIElement parent;
    
    private final ArrayList<UIElement> elements = new ArrayList<>();
    
    /**
     * @return Gets the containing element. If its null then the element is attached to the main EEXT_GUI elements.
     */
    public UIElement parent()
    {
        return this.parent;
    }
    
    /**
     * @return The list of elements.
     */
    public Iterable<UIElement> elements()
    {
        return this.elements;
    }
    
    /**
     * @return The number of elements.
     */
    public int elementCount()
    {
        return this.elements.size();
    }
    
    /**
     * Recalculates the child elements layers to determine overlapping elements.
     */
    protected void recalculateLayers()
    {
        int n = this.elements.size();
        for (int i = 0; i < n; i++)
        {
            UIElement e1 = this.elements.get(i);
            e1.layer = e1.requestedLayer;
            for (int j = 0; j < i; j++)
            {
                UIElement e2 = this.elements.get(j);
                if (e1.layer >= e2.layer && e2.rect().collide(e1.rect())) e2.layer = e1.layer + 1;
            }
        }
    }
    
    /**
     * Gets the top element if its alive, visible and the mouse it over it.
     *
     * @param mouseX The mouse x position
     * @param mouseY The mouse y position
     * @return The top element or null of the mouse is not over anything.
     */
    public UIElement getTopElement(double mouseX, double mouseY)
    {
        UIElement top = null;
        if (alive() && visible())
        {
            for (UIElement element : this.elements)
            {
                UIElement topChild = element.getTopElement(mouseX, mouseY);
                if (top == null || (topChild != null && topChild.layer >= top.layer)) top = topChild;
            }
            return top != null ? top : mouseOver(mouseX, mouseY) ? this : null;
        }
        return null;
    }
    
    /**
     * Check to see if the mouse is over the element.
     *
     * @param mouseX The mouse x position
     * @param mouseY The mouse y position
     * @return True if the mouse is over the element.
     */
    public boolean mouseOver(double mouseX, double mouseY)
    {
        return rect().collide((int) mouseX - absX() + rect().x(), (int) mouseY - absY() + rect().y());
    }
    
    // ----------------------
    // ----- PROPERTIES -----
    // ----------------------
    
    private final Rect rect = new Rect();
    
    private boolean alive   = true;
    private boolean focused = false;
    private boolean hovered = false;
    private boolean held    = false;
    private boolean visible = true;
    private boolean enabled = true;
    
    /**
     * @return The containing rect of the element. The x and y positions are relative to the container.
     */
    public Rectc rect()
    {
        return this.rect;
    }
    
    /**
     * @return Gets the x position relative to the top left corner of the screen.
     */
    public int absX()
    {
        return (parent() != null ? parent().absX() : 0) + this.rect.left();
    }
    
    /**
     * @return Gets the y position relative to the top left corner of the screen.
     */
    public int absY()
    {
        return (parent() != null ? parent().absY() : 0) + this.rect.top();
    }
    
    /**
     * Sets the position of the element in its parent coordinate space.
     *
     * @param x The new x position.
     * @param y The new y position.
     */
    public void position(int x, int y)
    {
        if (this.rect.x() != x || this.rect.y() != y)
        {
            this.rect.pos(x, y);
            
            if (parent() != null) parent().recalculateLayers();
            
            redraw();
        }
    }
    
    /**
     * Sets the new dimensions of the element.
     *
     * @param width  The new width.
     * @param height The new height.
     */
    public void dimensions(int width, int height)
    {
        if (this.rect.width() != width || this.rect.height() != height)
        {
            this.rect.size(width, height);
            
            if (parent() != null) parent().recalculateLayers();
            
            rebuild();
        }
    }
    
    /**
     * @return If the element is alive and updatable.
     */
    public boolean alive()
    {
        return this.alive;
    }
    
    /**
     * @return If the element has EEXT_GUI focus.
     */
    public boolean focused()
    {
        return this.focused;
    }
    
    /**
     * @return If the mouse is over the element and the element is hoverable.
     */
    public boolean hovered()
    {
        return this.hovered;
    }
    
    /**
     * @return If the element goes into hovered state when the mouse if over the element.
     */
    public boolean canHover()
    {
        return alive();
    }
    
    /**
     * @return If the element is being held by the mouse.
     */
    public boolean held()
    {
        return this.held;
    }
    
    /**
     * @return If the element can be seen.
     */
    public boolean visible()
    {
        return this.visible;
    }
    
    /**
     * Sets the visible state of the element
     *
     * @param visible The new state.
     */
    public void visible(boolean visible)
    {
        if (this.visible != visible)
        {
            boolean prev = this.visible;
            this.visible = visible;
            visibleChanged(prev, this.visible);
        }
    }
    
    /**
     * Toggles the visible state of the element.
     */
    public void toggleVisibility()
    {
        visible(!visible());
    }
    
    /**
     * Called whenever the visible state is changed.
     *
     * @param prevState The previous visible state
     * @param visible   The new visible state
     */
    protected void visibleChanged(boolean prevState, boolean visible)
    {
        redraw();
    }
    
    /**
     * @return True if the element is enabled
     */
    public boolean enabled()
    {
        return this.enabled;
    }
    
    /**
     * Sets the enabled state of the element
     *
     * @param enabled The new state.
     */
    public void enabled(boolean enabled)
    {
        if (this.enabled != enabled)
        {
            boolean prev = this.enabled;
            this.enabled = enabled;
            enabledChanged(prev, this.enabled);
        }
    }
    
    /**
     * Toggles the enabled state of the element.
     */
    public void toggleEnabled()
    {
        enabled(!enabled());
    }
    
    /**
     * Called whenever the enabled state is changed.
     *
     * @param prevState The previous enabled state
     * @param enabled   The new enabled state
     */
    protected void enabledChanged(boolean prevState, boolean enabled)
    {
        setState(!enabled ? "disabled" : canHover() && hovered() ? "hovered" : "normal");
    }
    
    /**
     * @return The text of the element. Can be null.
     */
    public String text()
    {
        return null;
    }
    
    // --------------------
    // ----- THEMEING -----
    // --------------------
    
    protected String[] objectIDs;
    protected String[] elementIDs;
    protected String[] states;
    
    private final HashMap<Pair.S, Double> stateTransitions = new HashMap<>();
    
    private int    borderWidth;
    private double tooltipDelay;
    
    private Font font;
    
    private String textHAlignment        = "center";
    private String textVAlignment        = "center";
    private int    textHAlignmentPadding = 1;
    private int    textVAlignmentPadding = 1;
    
    public boolean containsObjectID(String objectID)
    {
        for (String object : this.objectIDs) if (Objects.equals(object, objectID)) return true;
        return false;
    }
    
    public boolean containsElementID(String elementID)
    {
        for (String element : this.elementIDs) if (Objects.equals(element, elementID)) return true;
        return false;
    }
    
    /**
     * @return The border width defined by the theme.
     */
    public int borderWidth()
    {
        return this.borderWidth;
    }
    
    /**
     * @return The time in seconds before the tooltip will appear.
     */
    public double tooltipDelay()
    {
        return this.tooltipDelay;
    }
    
    /**
     * @return The font supplied by the theme. Can be null
     */
    public Font font()
    {
        return this.font;
    }
    
    /**
     * @return Gets the horizontal text alignment.
     */
    public String textHAlignment()
    {
        return this.textHAlignment;
    }
    
    /**
     * @return Gets the vertical text alignment.
     */
    public String textVAlignment()
    {
        return this.textVAlignment;
    }
    
    /**
     * @return Gets the horizontal text alignment padding.
     */
    public int textHAlignmentPadding()
    {
        return this.textHAlignmentPadding;
    }
    
    /**
     * @return Gets the vertical text alignment padding.
     */
    public int textVAlignmentPadding()
    {
        return this.textVAlignmentPadding;
    }
    
    /**
     * Sets the state of the element. If a transition is available, it will initiate it.
     *
     * @param state The new state.
     */
    protected void setState(String state)
    {
        boolean contains = false;
        for (String s : this.states) contains = contains || state.equals(s);
        if (contains && !state.equals(this.state))
        {
            println(state, this);
            this.prevState = this.state;
            this.state     = state;
            
            redrawStates();
    
            Pair.S statePair = new Pair.S(this.prevState, this.state);
            if (this.prevState != null && this.stateTransitions.containsKey(statePair))
            {
                this.transitionDuration   = this.stateTransitions.get(statePair);
                this.transitionRemaining  = this.transitionDuration - this.transitionRemaining;
                this.transitionPercentage = 0;
            }
            else
            {
                this.transitionRemaining = 0;
            }
        }
    }
    
    /**
     * Rebuilds theme information for this element and all child elements after the theme file changed.
     */
    public void rebuildThemeFromFileChange()
    {
        rebuildTheme();
        for (UIElement element : elements()) element.rebuildThemeFromFileChange();
    }
    
    /**
     * Rebuild all theme information. If anything changed, then redraw the states.
     */
    public void rebuildTheme()
    {
        if (rebuildTheme(false)) rebuild();
    }
    
    /**
     * Rebuilds all theme information. This method can be overridden by elements.
     *
     * @param anyChanged pass-through
     * @return anyChanged pass-through
     */
    public boolean rebuildTheme(boolean anyChanged)
    {
        String stateTransitionString = EEXT_GUI.theme().getMiscData(this.objectIDs, this.elementIDs, "state_transitions");
        if (stateTransitionString != null)
        {
            this.stateTransitions.clear();
            for (String transitionTime : stateTransitionString.split("-"))
            {
                String[] split  = transitionTime.split(":");
                String[] states = split[0].split("_");
                if (split.length == 2 && states.length == 2)
                {
                    this.stateTransitions.put(new Pair.S(states[0], states[1]), Double.parseDouble(split[1]));
                }
            }
        }
        
        if (checkThemeSizeChange(1)) anyChanged = true;
        
        String toolTipDelayString = EEXT_GUI.theme().getMiscData(this.objectIDs, this.elementIDs, "tool_tip_delay");
        if (toolTipDelayString != null)
        {
            double toolTipDelay = Double.parseDouble(toolTipDelayString);
            if (this.tooltipDelay != toolTipDelay)
            {
                this.tooltipDelay = toolTipDelay;
                anyChanged        = true;
            }
        }
        
        Font font = EEXT_GUI.theme().getFont(this.objectIDs, this.elementIDs);
        if (this.font == null || this.font.equals(font))
        {
            this.font  = font;
            anyChanged = true;
        }
        
        if (rebuildTextAlignment()) anyChanged = true;
        
        return anyChanged;
    }
    
    /**
     * Checks if the border width has changed in the theme.
     *
     * @param defaultBorderWidth The default border width;
     * @return If the border width has changed.
     */
    protected boolean checkThemeSizeChange(int defaultBorderWidth)
    {
        boolean anyChange = false;
        
        String borderWidthString = EEXT_GUI.theme().getMiscData(this.objectIDs, this.elementIDs, "border_width");
        
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
    
    protected boolean rebuildTextAlignment()
    {
        boolean anyChanged = false;
        
        String textHAlignment = EEXT_GUI.theme().getMiscData(this.objectIDs, this.elementIDs, "text_h_alignment");
        if (textHAlignment != null)
        {
            if (!this.textHAlignment.equals(textHAlignment))
            {
                this.textHAlignment = textHAlignment;
                anyChanged          = true;
            }
        }
        
        String textVAlignment = EEXT_GUI.theme().getMiscData(this.objectIDs, this.elementIDs, "text_v_alignment");
        if (textVAlignment != null)
        {
            if (!this.textVAlignment.equals(textVAlignment))
            {
                this.textVAlignment = textVAlignment;
                anyChanged          = true;
            }
        }
        
        String textHAlignmentPaddingString = EEXT_GUI.theme().getMiscData(this.objectIDs, this.elementIDs, "text_h_alignment_padding");
        if (textHAlignmentPaddingString != null)
        {
            int textHAlignmentPadding = Integer.parseInt(textHAlignmentPaddingString);
            if (this.textHAlignmentPadding != textHAlignmentPadding)
            {
                this.textHAlignmentPadding = textHAlignmentPadding;
                anyChanged                 = true;
            }
        }
        
        String textVAlignmentPaddingString = EEXT_GUI.theme().getMiscData(this.objectIDs, this.elementIDs, "text_v_alignment_padding");
        if (textVAlignmentPaddingString != null)
        {
            int textVAlignmentPadding = Integer.parseInt(textVAlignmentPaddingString);
            if (this.textVAlignmentPadding != textVAlignmentPadding)
            {
                this.textVAlignmentPadding = textVAlignmentPadding;
                anyChanged                 = true;
            }
        }
        
        return anyChanged;
    }
    
    // -------------------
    // ----- UPDATES -----
    // -------------------
    
    /**
     * Updates the element and sub elements. This is called and past along to its children as long as it is alive.
     *
     * @param elapsedTime The amount of time in seconds since the last update.
     * @param mouseX      The x position of the mouse.
     * @param mouseY      The y position of the mouse.
     * @return If the EEXT_GUI window should be redrawn.
     */
    protected boolean update(double elapsedTime, double mouseX, double mouseY)
    {
        if (alive())
        {
            if (this.transitionDuration > 0)
            {
                this.transitionRemaining -= elapsedTime;
                if (this.transitionRemaining > 0)
                {
                    this.transitionPercentage = 1 - (this.transitionRemaining / this.transitionDuration);
                    
                    this.redraw = true;
                }
                else
                {
                    this.transitionRemaining = 0;
                }
            }
            
            this.redraw |= updateElement(elapsedTime, mouseX, mouseY);
            
            for (UIElement child : this.elements)
            {
                this.redraw |= child.update(elapsedTime, mouseX, mouseY);
            }
            
            return this.redraw;
        }
        return false;
    }
    
    /**
     * Updates the element. This is called and past along to its children as long as it is alive.
     * <p>
     * Can be overridden.
     *
     * @param elapsedTime The amount of time in seconds since the last update.
     * @param mouseX      The x position of the mouse.
     * @param mouseY      The y position of the mouse.
     * @return If the EEXT_GUI window should be redrawn.
     */
    protected boolean updateElement(double elapsedTime, double mouseX, double mouseY)
    {
        return false;
    }
    
    // -------------------
    // ----- Drawing -----
    // -------------------
    
    protected Texture texture;
    protected String  state, prevState;
    protected Texture stateTexture, prevStateTexture;
    
    protected double transitionRemaining  = 0;
    protected double transitionDuration   = 0;
    protected double transitionPercentage = 0;
    
    private boolean redraw       = true;
    private boolean redrawStates = true;
    
    /**
     * Triggers this element to redraw. Also triggers all parent containers and finally the EEXT_GUI screen to redraw.
     */
    public void redraw()
    {
        this.redraw = true;
    }
    
    /**
     * Triggers this element to redraw its current state. Also triggers all parent containers and finally the EEXT_GUI screen to redraw.
     */
    public void redrawStates()
    {
        this.redrawStates = true;
        redraw();
    }
    
    /**
     * Draws the element and sub elements. This is called and past along to its children as long as it is alive and is set to be redrawn.
     * <p>
     * Can be overridden.
     *
     * @param elapsedTime The amount of time in seconds since the last update.
     * @param mouseX      The x position of the mouse.
     * @param mouseY      The y position of the mouse.
     */
    public void draw(double elapsedTime, double mouseX, double mouseY)
    {
        if (alive())
        {
            if (this.redrawStates)
            {
                if (this.prevState != null && this.prevStateTexture != null)
                {
                    target(this.prevStateTexture);
                    drawState(this.prevState);
                }
                
                if (this.state != null && this.stateTexture != null)
                {
                    target(this.stateTexture);
                    drawState(this.state);
                }
                
                this.redrawStates = false;
            }
            
            if (this.redraw || this.texture == null)
            {
                if (this.texture != null)
                {
                    target(this.texture);
                }
                else
                {
                    translate(rect().x(), rect().y());
                }
                
                if (this.transitionRemaining > 0)
                {
                    interpolateTexture(this.prevStateTexture, this.stateTexture, this.transitionPercentage, 0, 0);
                }
                else if (this.stateTexture != null)
                {
                    texture(this.stateTexture, 0, 0);
                }
                drawElement(elapsedTime, mouseX, mouseY);
                
                for (UIElement child : this.elements)
                {
                    push();
                    child.draw(elapsedTime, mouseX, mouseY);
                    pop();
                    
                    if (child.visible() && child.texture != null)
                    {
                        rectMode(RectMode.CORNER);
                        texture(child.texture, child.rect.x(), child.rect.y(), child.rect.width(), child.rect.height());
                    }
                }
                this.redraw = false;
            }
        }
    }
    
    /**
     * Draws the element and sub elements. This is called and past along to its children as long as it is alive.
     * <p>
     * Can be overridden.
     *
     * @param elapsedTime The amount of time in seconds since the last update.
     * @param mouseX      The x position of the mouse.
     * @param mouseY      The y position of the mouse.
     */
    protected void drawElement(double elapsedTime, double mouseX, double mouseY)
    {
    
    }
    
    /**
     * Draws the requested state with theme information.
     *
     * @param state The state.
     */
    public void drawState(String state)
    {
        String stateBorder     = state + "_border";
        String stateBackground = state + "_bg";
        
        if (borderWidth() > 0)
        {
            fill(EEXT_GUI.theme().getColor(this.objectIDs, this.elementIDs, stateBorder));
            fillRect(0, 0, rect().width(), rect().height());
        }
        
        fill(EEXT_GUI.theme().getColor(this.objectIDs, this.elementIDs, stateBackground));
        fillRect(borderWidth(), borderWidth(), rect().width() - (borderWidth() << 1), rect().height() - (borderWidth() << 1));
    }
    
    public void drawImageAndText(String state)
    {
        String imageState     = state + "_image";
        String textColorState = state + "_text";
        
        Texture image = EEXT_GUI.theme().getImage(this.objectIDs, this.elementIDs, imageState);
        if (image == null) image = EEXT_GUI.theme().getImage(this.objectIDs, this.elementIDs, "normal_image");
        if (image != null)
        {
            rectMode(RectMode.CENTER);
            texture(image, rect().centerX(), rect().centerY(), image.width(), image.height());
        }
        
        if (text() != null && font() != null && text().length() > 0)
        {
            Rect rect = new Rect(0, 0, (int) font().getStringWidth(text()), (int) font().getStringHeight(text()));
    
            switch (this.textHAlignment)
            {
                case "left" -> rect.x(this.textHAlignmentPadding + this.borderWidth);
                case "right" -> rect.x(rect().width() - this.textHAlignmentPadding - this.borderWidth);
                default -> rect.centerX(rect().width() >> 1);
            }
    
            switch (this.textVAlignment)
            {
                default -> rect.centerY(rect().height() >> 1);
                case "top" -> rect.y(this.textVAlignmentPadding + this.borderWidth);
                case "bottom" -> rect.y(rect().height() - this.textVAlignmentPadding - this.borderWidth);
            }
            
            textFont(font());
            rectMode(RectMode.CENTER);
            textAlign(TextAlign.CENTER);
            
            fill(EEXT_GUI.theme().getColor(this.objectIDs, this.elementIDs, "text_shadow"));
            Engine.text(text(), rect.centerX(), rect.centerY() + 1);
            Engine.text(text(), rect.centerX(), rect.centerY() - 1);
            Engine.text(text(), rect.centerX() + 1, rect.centerY());
            Engine.text(text(), rect.centerX() - 1, rect.centerY());
            
            fill(EEXT_GUI.theme().getColor(this.objectIDs, this.elementIDs, textColorState));
            Engine.text(text(), rect.centerX(), rect.centerY());
        }
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
        this.held    = false;
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
        this.held = false;
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
        this.held = true;
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
