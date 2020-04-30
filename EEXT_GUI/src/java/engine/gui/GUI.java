package engine.gui;

import engine.Engine;
import engine.Extension;
import engine.color.Color;
import engine.event.*;
import engine.gui.elment.UIWindow;
import engine.gui.theme.Theme;
import engine.render.RectMode;
import org.joml.Vector2i;

import java.util.ArrayList;

import static engine.Engine.*;

public class GUI extends Extension
{
    public static GUI INSTANCE = new GUI();
    
    private final Vector2i size = new Vector2i();
    
    private Theme   theme;
    private boolean liveThemeUpdates = true;
    private double  themeUpdateTime  = 0.0;
    
    private boolean redrawScreen = true;
    
    final ArrayList<UIElement> elements = new ArrayList<>();
    
    private UIElement topElement     = null;
    private UIElement focusedElement = null;
    
    private double hoverTime = 0.0;
    
    public GUI()
    {
        super();
        this.enabled = false;
    }
    
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
        profiler().startSection("Theme Update");
        {
            if (this.liveThemeUpdates)
            {
                if ((this.themeUpdateTime += elapsedTime) >= 1.0)
                {
                    this.themeUpdateTime = 0.0;
                    if (this.theme.shouldReload())
                    {
                        this.redrawScreen = true;
                        for (UIElement element : this.elements)
                        {
                            element.rebuildTheme();
                        }
                    }
                }
            }
        }
        profiler().endSection();
        
        double mouseX = mouse().x() * this.size.x() / screenWidth();
        double mouseY = mouse().y() * this.size.y() / screenHeight();
        
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
                    UIElement topElement = element.getTopElement(mouseX, mouseY);
                    if (topElement != null) this.topElement = topElement;
                }
            }
            
            if (prevTopElement != this.topElement)
            {
                this.hoverTime = 0;
                if (prevTopElement != null) prevTopElement.onMouseExit();
                if (this.topElement != null) this.topElement.onMouseEnter();
            }
            else if (this.topElement != null && this.topElement.canHover())
            {
                this.topElement.onMouseHover(this.hoverTime, mouseX - this.topElement.absX(), mouseY - this.topElement.absY());
                this.hoverTime += elapsedTime;
            }
        }
        profiler().endSection();
        
        profiler().startSection("Events");
        {
            profiler().startSection("Mouse");
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
            
            profiler().startSection("Keyboard");
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
        double mouseX = mouse().x() * this.size.x() / screenWidth();
        double mouseY = mouse().y() * this.size.y() / screenHeight();
        
        profiler().startSection("UIElements Update");
        {
            for (UIElement element : this.elements)
            {
                this.redrawScreen |= element.update(elapsedTime, mouseX, mouseY);
            }
        }
        profiler().endSection();
        
        if (this.redrawScreen)
        {
            layer(layerCount() - 1);
            
            clear(Color.BLANK);
            
            for (UIElement element : this.elements)
            {
                push();
                element.draw(elapsedTime, mouseX, mouseY);
                pop();
                
                if (element.visible())
                {
                    rectMode(RectMode.CORNER);
                    texture(element.texture, element.rect.x(), element.rect.y(), element.rect.width(), element.rect.height());
                    element.texture.bindTexture().download().saveImage(element.getClass().toString());
                    // element.texture.bindTexture().saveImage(element.getClass().toString());
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
    
    public static void createGUI(int width, int height, String themePath, boolean liveThemeUpdates)
    {
        GUI.INSTANCE.enabled = true;
        
        GUI.INSTANCE.size.set(width, height);
        createLayer(layerCount() - 1, width, height);
        
        GUI.INSTANCE.theme = new Theme();
        if (themePath != null) GUI.INSTANCE.theme.loadTheme(themePath);
        
        GUI.INSTANCE.liveThemeUpdates = liveThemeUpdates;
    }
    
    public static void createGUI(int width, int height, String themePath)
    {
        createGUI(width, height, themePath, false);
    }
    
    public static void createGUI(int width, int height, boolean liveThemeUpdates)
    {
        createGUI(width, height, null, liveThemeUpdates);
    }
    
    public static void createGUI(String theme, boolean liveThemeUpdates)
    {
        createGUI(screenWidth(), screenHeight(), theme, liveThemeUpdates);
    }
    
    public static void createGUI(int width, int height)
    {
        createGUI(width, height, null, false);
    }
    
    public static void createGUI(String theme)
    {
        createGUI(screenWidth(), screenHeight(), theme, false);
    }
    
    public static void createGUI(boolean liveThemeUpdates)
    {
        createGUI(screenWidth(), screenHeight(), null, liveThemeUpdates);
    }
    
    public static void createGUI()
    {
        createGUI(screenWidth(), screenHeight(), null, false);
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
    
    public static Theme theme()
    {
        return GUI.INSTANCE.theme;
    }
}
