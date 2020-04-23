package engine.gui;

import engine.Engine;
import engine.Extension;
import engine.color.Color;
import engine.event.*;
import engine.render.RectMode;
import engine.render.Renderer;
import engine.render.Texture;
import org.joml.Vector2i;

import java.util.ArrayList;

import static engine.Engine.*;

public class GUI extends Extension
{
    public static GUI INSTANCE = new GUI();
    
    private final Vector2i size = new Vector2i();
    
    private boolean redrawScreen = true;
    
    private Renderer renderer;
    private Texture  emptyTexture;
    
    final ArrayList<UIElement> elements = new ArrayList<>();
    
    private UIElement topElement     = null;
    private UIElement focusedElement = null;
    
    private double hoverTime = 0.0;
    
    // public GUI()
    // {
    //     super();
    //     this.enabled = false;
    // }
    
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
        int mouseX = (int) (mouse().x() * this.size.x() / screenWidth());
        int mouseY = (int) (mouse().y() * this.size.y() / screenHeight());
        
        profiler().startSection("Stack Solving");
        {
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
                        if (this.topElement != null) this.focusedElement = window;
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
        }
        profiler().endSection();
        
        profiler().startSection("Mouse Events");
        {
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
                    // this.drag = null;
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
                            // this.drag = element;
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
        profiler().endSection();
        
        profiler().startSection("Keyboard Events");
        {
            if (this.focusedElement != null)
            {
                for (Event e : Events.get(Events.KEYBOARD_EVENTS))
                {
                    if (e instanceof EventKeyboardKeyDown)
                    {
                        EventKeyboardKeyDown event = (EventKeyboardKeyDown) e;
                        
                        UIElement element = this.focusedElement;
                        while (element != null)
                        {
                            if (element.onKeyboardKeyDown(event.key())) break;
                            element = element.container();
                        }
                    }
                    else if (e instanceof EventKeyboardKeyUp)
                    {
                        EventKeyboardKeyUp event = (EventKeyboardKeyUp) e;
                        
                        UIElement element = this.focusedElement;
                        while (element != null)
                        {
                            if (element.onKeyboardKeyUp(event.key())) break;
                            element = element.container();
                        }
                    }
                    else if (e instanceof EventKeyboardKeyHeld)
                    {
                        EventKeyboardKeyHeld event = (EventKeyboardKeyHeld) e;
                        
                        UIElement element = this.focusedElement;
                        while (element != null)
                        {
                            if (element.onKeyboardKeyHeld(event.key())) break;
                            element = element.container();
                        }
                    }
                    else if (e instanceof EventKeyboardKeyRepeat)
                    {
                        EventKeyboardKeyRepeat event = (EventKeyboardKeyRepeat) e;
                        
                        UIElement element = this.focusedElement;
                        while (element != null)
                        {
                            if (element.onKeyboardKeyRepeated(event.key())) break;
                            element = element.container();
                        }
                    }
                    else if (e instanceof EventKeyboardKeyPressed)
                    {
                        EventKeyboardKeyPressed event = (EventKeyboardKeyPressed) e;
                        
                        UIElement element = this.focusedElement;
                        while (element != null)
                        {
                            if (element.onKeyboardKeyPressed(event.key(), event.doublePressed())) break;
                            element = element.container();
                        }
                    }
                    else if (e instanceof EventKeyboardKeyTyped)
                    {
                        EventKeyboardKeyTyped event = (EventKeyboardKeyTyped) e;
                        
                        UIElement element = this.focusedElement;
                        while (element != null)
                        {
                            if (element.onKeyboardKeyTyped(event.charTyped())) break;
                            element = element.container();
                        }
                    }
                }
            }
        }
        profiler().endSection();
        
        profiler().startSection("Update UIElements");
        {
            for (UIElement element : this.elements)
            {
                if (element.update(elapsedTime, mouseX, mouseY)) this.redrawScreen = true;
            }
        }
        profiler().endSection();
    }
    
    /**
     * This is called once per frame after the {@link Engine#draw} method is called.
     *
     * @param elapsedTime The time in seconds since the last frame.
     */
    @Override
    public void afterDraw(double elapsedTime)
    {
        if (this.redrawScreen)
        {
            int mouseX = (int) (mouse().x() * this.size.x() / screenWidth());
            int mouseY = (int) (mouse().y() * this.size.y() / screenHeight());
            
            layer(99);
            
            clear(Color.BLANK);
            
            for (UIElement element : this.elements)
            {
                element.draw(elapsedTime, mouseX, mouseY);
                
                if (element.visible)
                {
                    rectMode(RectMode.CORNER);
                    texture(element.texture, element.rect.x(), element.rect.y(), element.rect.width(), element.rect.height());
                }
            }
            
            this.redrawScreen = false;
        }
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
    
    public Renderer renderer()
    {
        return this.renderer;
    }
    
    public Texture getEmptyTexture()
    {
        return this.emptyTexture;
    }
    
    public static void createGUI(int width, int height)
    {
        GUI.INSTANCE.size.set(width, height);
        createLayer(99, width, height);
        GUI.INSTANCE.renderer = Renderer.getRenderer(GUI.INSTANCE.emptyTexture = new Texture(width, height), rendererType());
    }
    
    public static void createGUI()
    {
        createGUI(screenWidth(), screenHeight());
    }
    
    public static void setFocused(UIElement element)
    {
        if (element == GUI.INSTANCE.focusedElement) return;
        
        if (GUI.INSTANCE.focusedElement != null) GUI.INSTANCE.focusedElement.onUnfocus();
        
        GUI.INSTANCE.focusedElement = element;
        
        if (GUI.INSTANCE.focusedElement != null)
        {
            GUI.INSTANCE.focusedElement.onFocus();
        }
    }
}