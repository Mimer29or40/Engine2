package engine.gui;

import engine.Engine;
import engine.Extension;
import engine.color.Color;
import engine.event.*;
import engine.render.Renderer;
import engine.render.Texture;
import org.joml.Vector2i;

import java.util.ArrayList;

import static engine.Engine.*;

public class EEXT_GUI extends Extension
{
    public static EEXT_GUI INSTANCE = new EEXT_GUI();
    
    private final Vector2i resolution = new Vector2i();
    
    private Texture  texture;
    private Renderer renderer;
    
    final ArrayList<UIElement> elements = new ArrayList<>();
    
    private UIElement topElement     = null;
    private UIElement focusedElement = null;
    
    private double hoverTime = 0.0;
    
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
        if (this.resolution.x <= 0 || this.resolution.y <= 0) this.resolution.set(screenSize());
        
        this.renderer = Renderer.getRenderer(this.texture = new Texture(this.resolution.x, this.resolution.y, 4), rendererType());
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
        // UIWindow focusedWindow = null;
        for (UIElement element : this.elements)
        {
            if (element instanceof UIWindow)
            {
                UIWindow window = (UIWindow) element;
                if (window.blocking())
                {
                    blockingWindow  = true;
                    this.topElement = window.getTopElement(mouseX, mouseY);
                    // if (this.topElement != null) focusedWindow = window;
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
        this.renderer.target(this.texture);
        
        this.renderer.start();
        
        this.renderer.clear(Color.BLANK);
        
        this.renderer.finish();
        
        texture(this.texture, 0, 0, screenWidth(), screenHeight());
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
    
    public static void size(int width, int height)
    {
        EEXT_GUI.INSTANCE.resolution.set(width, height);
    }
    
    public static void setFocused(UIElement element)
    {
        if (element == EEXT_GUI.INSTANCE.focusedElement) return;
        
        if (EEXT_GUI.INSTANCE.focusedElement != null) EEXT_GUI.INSTANCE.focusedElement.onUnfocus();
        
        EEXT_GUI.INSTANCE.focusedElement = element;
        
        if (EEXT_GUI.INSTANCE.focusedElement != null)
        {
            EEXT_GUI.INSTANCE.focusedElement.onFocus();
        }
    }
}
