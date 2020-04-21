package engine.gui;

import engine.Engine;
import engine.Extension;
import engine.event.*;
import org.joml.Vector2i;

import java.util.ArrayList;

import static engine.Engine.mouse;

public class EEXT_GUI extends Extension
{
    public static EEXT_GUI INSTANCE = new EEXT_GUI();
    
    final ArrayList<UIElement> elements = new ArrayList<>();
    // private final ArrayList<UIWindow> windows = new ArrayList<>();
    
    private UIElement topElement     = null;
    private UIElement focusedElement = null;
    
    private double hoverTime = 0.0;
    
    private final Vector2i vector2 = new Vector2i();
    
    /**
     * This is called once before the {@link Engine#setup} method is called.
     */
    @Override
    public void beforeSetup()
    {
    
    }
    
    /**
     * This is called once after the {@link Engine#setup} method is called only if {@link Engine#size} is called.
     */
    @Override
    public void afterSetup()
    {
    
    }
    
    /**
     * This is called once per frame before the {@link Engine#draw} method is called.
     *
     * @param elapsedTime The time in seconds since the last frame.
     */
    @Override
    public void beforeDraw(double elapsedTime)
    {
        int mouseX = (int) mouse().x(), mouseY = (int) mouse().y();
        
        UIElement prevTopElement = this.topElement;
        this.topElement = null;
        
        boolean blockingWindow = false;
        for (UIElement element : this.elements)
        {
            if (element instanceof UIWindow)
            {
                UIWindow window = (UIWindow) element;
                if (window.blocking())
                {
                    blockingWindow  = true;
                    this.topElement = window.getTopElement(mouseX, mouseY);
                }
            }
        }
        
        if (!blockingWindow)
        {
            for (UIElement element : this.elements)
            {
                if ((this.topElement = element.getTopElement(mouseX, mouseY)) != null) break;
            }
        }
        
        if (prevTopElement != this.topElement)
        {
            this.hoverTime = 0;
            if (prevTopElement != null) prevTopElement.onMouseExit();
            if (this.topElement != null) this.topElement.onMouseEnter();
        }
        else if (this.topElement != null)
        {
            this.topElement.onMouseHover(this.hoverTime);
            this.hoverTime += elapsedTime;
        }
        
        for (Event e : Events.get(Events.MOUSE_EVENTS))
        {
            if (e instanceof EventMouseButtonDown)
            {
                EventMouseButtonDown event = (EventMouseButtonDown) e;
                
                UIElement element = this.topElement;
                while (element != null)
                {
                    if (element.onMouseButtonDown(event.button(), event.x() - element.absX(), event.y() - element.absY())) break;
                    element = element.container();
                }
                setFocused(element);
            }
            else if (e instanceof EventMouseButtonUp)
            {
                // PEX_GUI.drag = null;
                EventMouseButtonUp event = (EventMouseButtonUp) e;
                
                UIElement element = this.topElement;
                while (element != null)
                {
                    if (element.onMouseButtonUp(event.button(), event.x() - element.absX(), event.y() - element.absY())) break;
                    element = element.container();
                }
            }
            else if (e instanceof EventMouseButtonClicked)
            {
                EventMouseButtonClicked event = (EventMouseButtonClicked) e;
                
                UIElement element = this.topElement;
                while (element != null)
                {
                    if (element.onMouseButtonClicked(event.button(), event.x() - element.absX(), event.y() - element.absY(), event.doubleClicked())) break;
                    element = element.container();
                }
            }
            else if (e instanceof EventMouseButtonHeld)
            {
                EventMouseButtonHeld event = (EventMouseButtonHeld) e;
                
                UIElement element = this.topElement;
                while (element != null)
                {
                    if (element.onMouseButtonHeld(event.button(), event.x() - element.absX(), event.y() - element.absY())) break;
                    element = element.container();
                }
            }
            else if (e instanceof EventMouseButtonRepeat)
            {
                EventMouseButtonRepeat event = (EventMouseButtonRepeat) e;
                
                UIElement element = this.topElement;
                while (element != null)
                {
                    if (element.onMouseButtonRepeated(event.button(), event.x() - element.absX(), event.y() - element.absY())) break;
                    element = element.container();
                }
            }
            else if (e instanceof EventMouseButtonDragged)
            {
                EventMouseButtonDragged event = (EventMouseButtonDragged) e;
                
                UIElement element = this.topElement;
                while (element != null)
                {
                    if (element.onMouseButtonDragged(event.button(), event.x() - element.absX(), event.y() - element.absY(), event.dragX(), event.dragY(), event.relX(), event.relY()))
                    {
                        // PEX_GUI.drag = element;
                        break;
                    }
                    element = element.container();
                }
            }
            else if (e instanceof EventMouseScrolled)
            {
                EventMouseScrolled event = (EventMouseScrolled) e;
                
                UIElement element = this.topElement;
                while (element != null)
                {
                    if (element.onMouseScrolled(event.x(), event.y())) break;
                    element = element.container();
                }
            }
        }
    }
    
    /**
     * This is called once per frame after the {@link Engine#draw} method is called.
     *
     * @param elapsedTime The time in seconds since the last frame.
     */
    @Override
    public void afterDraw(double elapsedTime)
    {
    
    }
    
    /**
     * This is called once before the {@link Engine#destroy} method is called.
     */
    @Override
    public void beforeDestroy()
    {
    
    }
    
    /**
     * This is called once after the {@link Engine#destroy} method is called.
     */
    @Override
    public void afterDestroy()
    {
    
    }
    
    public void setFocused(UIElement element)
    {
        if (element == this.focusedElement) return;
        
        if (this.focusedElement != null) this.focusedElement.onUnfocus();
        
        this.focusedElement = element;
        
        if (this.focusedElement != null)
        {
            this.focusedElement.onFocus();
        }
    }
}
