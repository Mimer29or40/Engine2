package engine.gui;

import engine.Engine;
import engine.Extension;
import engine.color.Color;
import engine.event.*;
import engine.gui.elment.UIContainer;
import engine.gui.elment.UIWindow;
import engine.gui.util.Rect;
import engine.util.IPair;
import engine.util.PairD;
import org.joml.Vector2i;

import static engine.Engine.*;
import static engine.util.Util.println;

public class GUI extends Extension
{
    public static GUI INSTANCE = new GUI();
    
    private static final PairD screenToGUI = new PairD(0, 0);
    private static final PairD guiToScreen = new PairD(0, 0);
    
    private final Vector2i size = new Vector2i();
    
    private Theme   theme;
    private boolean liveThemeUpdates = true;
    private double  themeUpdateTime  = 0.0;
    
    private UIContainer rootContainer;
    
    private UIElement topElement        = null;
    private UIElement focusedElement    = null;
    private UIElement focusedVScrollbar = null;
    private UIElement focusedHScrollbar = null;
    
    private boolean redrawScreen = true;
    
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
                        this.rootContainer.rebuildThemeFromFileChange();
                        // for (UIElement element : this.rootContainer.elements())
                        // {
                        //     element.rebuildThemeFromFileChange();
                        // }
                    }
                }
            }
        }
        profiler().endSection();
        
        IPair<Double, Double> mouse = screenToGUI(mouse().x(), mouse().y());
        
        double mouseX = mouse.getA();
        double mouseY = mouse.getB();
        
        profiler().startSection("Stack Solving");
        {
            UIElement prevTopElement = this.topElement;
            this.topElement = null;
            
            boolean blockingWindow = false;
            for (UIElement element : this.rootContainer.elements())
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
                for (UIElement element : this.rootContainer.elements())
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
                            if (element.onMouseButtonDown(event.button(), event.x() - element.absX(), event.y() - element.absY()))
                            {
                                println(elapsedTime, element);
                                break;
                            }
                            element = element.parent();
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
                            element = element.parent();
                        }
                    }
                    else if (e instanceof EventMouseButtonClicked)
                    {
                        EventMouseButtonClicked event = (EventMouseButtonClicked) e;
                        
                        UIElement element = this.topElement;
                        while (element != null)
                        {
                            if (element.onMouseButtonClicked(event.button(), event.x() - element.absX(), event.y() - element.absY(), event.doubleClicked())) break;
                            element = element.parent();
                        }
                    }
                    else if (e instanceof EventMouseButtonHeld)
                    {
                        EventMouseButtonHeld event = (EventMouseButtonHeld) e;
                        
                        UIElement element = this.topElement;
                        while (element != null)
                        {
                            if (element.onMouseButtonHeld(event.button(), event.x() - element.absX(), event.y() - element.absY())) break;
                            element = element.parent();
                        }
                    }
                    else if (e instanceof EventMouseButtonRepeat)
                    {
                        EventMouseButtonRepeat event = (EventMouseButtonRepeat) e;
                        
                        UIElement element = this.topElement;
                        while (element != null)
                        {
                            if (element.onMouseButtonRepeated(event.button(), event.x() - element.absX(), event.y() - element.absY())) break;
                            element = element.parent();
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
                            element = element.parent();
                        }
                    }
                    else if (e instanceof EventMouseScrolled)
                    {
                        EventMouseScrolled event = (EventMouseScrolled) e;
                        
                        if (this.topElement != this.focusedVScrollbar) // TODO - This may cause double events so look out for them
                        {
                            UIElement element = this.focusedVScrollbar;
                            while (element != null)
                            {
                                if (element.onMouseScrolled(event.x(), event.y())) break;
                                element = element.parent();
                            }
                        }
                        
                        UIElement element = this.topElement;
                        while (element != null)
                        {
                            if (element.onMouseScrolled(event.x(), event.y())) break;
                            element = element.parent();
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
                                element = element.parent();
                            }
                        }
                        else if (e instanceof EventKeyboardKeyUp)
                        {
                            EventKeyboardKeyUp event = (EventKeyboardKeyUp) e;
                            
                            UIElement element = this.focusedElement;
                            while (element != null)
                            {
                                if (element.onKeyboardKeyUp(event.key())) break;
                                element = element.parent();
                            }
                        }
                        else if (e instanceof EventKeyboardKeyHeld)
                        {
                            EventKeyboardKeyHeld event = (EventKeyboardKeyHeld) e;
                            
                            UIElement element = this.focusedElement;
                            while (element != null)
                            {
                                if (element.onKeyboardKeyHeld(event.key())) break;
                                element = element.parent();
                            }
                        }
                        else if (e instanceof EventKeyboardKeyRepeat)
                        {
                            EventKeyboardKeyRepeat event = (EventKeyboardKeyRepeat) e;
                            
                            UIElement element = this.focusedElement;
                            while (element != null)
                            {
                                if (element.onKeyboardKeyRepeated(event.key())) break;
                                element = element.parent();
                            }
                        }
                        else if (e instanceof EventKeyboardKeyPressed)
                        {
                            EventKeyboardKeyPressed event = (EventKeyboardKeyPressed) e;
                            
                            UIElement element = this.focusedElement;
                            while (element != null)
                            {
                                if (element.onKeyboardKeyPressed(event.key(), event.doublePressed())) break;
                                element = element.parent();
                            }
                        }
                        else if (e instanceof EventKeyboardKeyTyped)
                        {
                            EventKeyboardKeyTyped event = (EventKeyboardKeyTyped) e;
                            
                            UIElement element = this.focusedElement;
                            while (element != null)
                            {
                                if (element.onKeyboardKeyTyped(event.charTyped())) break;
                                element = element.parent();
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
        IPair<Double, Double> mouse = screenToGUI(mouse().x(), mouse().y());
        
        double mouseX = mouse.getA();
        double mouseY = mouse.getB();
        
        profiler().startSection("UIElements Update");
        {
            this.redrawScreen |= this.rootContainer.update(elapsedTime, mouseX, mouseY);
        }
        profiler().endSection();
    
        profiler().startSection("UIElements Draw");
        {
            if (this.redrawScreen)
            {
                layer(layerCount() - 1);
        
                clear(Color.BLANK);
        
                this.rootContainer.draw(elapsedTime, mouseX, mouseY);
        
                this.redrawScreen = false;
            }
            profiler().endSection();
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
        
        GUI.INSTANCE.rootContainer = new UIContainer(new Rect(0, 0, width, height), null, null, "#root_container");
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
        createGUI(screenWidth() * pixelWidth(), screenHeight() * pixelHeight(), null, false);
    }
    
    public static Theme theme()
    {
        return GUI.INSTANCE.theme;
    }
    
    public static UIContainer rootContainer()
    {
        return GUI.INSTANCE.rootContainer;
    }
    
    public static UIElement focusedVScrollbar()
    {
        return GUI.INSTANCE.focusedVScrollbar;
    }
    
    public static UIElement focusedHScrollbar()
    {
        return GUI.INSTANCE.focusedHScrollbar;
    }
    
    public static void setFocused(UIElement element)
    {
        if (element == GUI.INSTANCE.focusedElement) return;
        
        if (GUI.INSTANCE.focusedElement != null) GUI.INSTANCE.focusedElement.onUnfocus();
        
        GUI.INSTANCE.focusedElement = element;
        
        if (GUI.INSTANCE.focusedElement != null)
        {
            GUI.INSTANCE.focusedElement.onFocus();
            
            if (GUI.INSTANCE.focusedElement.containsElementID("vertical_scroll_bar")) GUI.INSTANCE.focusedVScrollbar = GUI.INSTANCE.focusedElement;
            if (GUI.INSTANCE.focusedElement.containsElementID("horizontal_scroll_bar")) GUI.INSTANCE.focusedHScrollbar = GUI.INSTANCE.focusedElement;
        }
    }
    
    public static void clearFocusedVScrollbar(UIElement element)
    {
        if (element != null && GUI.INSTANCE.focusedVScrollbar == element) GUI.INSTANCE.focusedVScrollbar = null;
    }
    
    public static void clearFocusedHScrollbar(UIElement element)
    {
        if (element != null && GUI.INSTANCE.focusedHScrollbar == element) GUI.INSTANCE.focusedHScrollbar = null;
    }
    
    public static IPair<Double, Double> screenToGUI(double screenX, double screenY)
    {
        GUI.screenToGUI.a = screenX * GUI.INSTANCE.size.x() / screenWidth();
        GUI.screenToGUI.b = screenY * GUI.INSTANCE.size.y() / screenHeight();
        return GUI.screenToGUI;
    }
    
    public static IPair<Double, Double> guiToScreen(double guiX, double guiY)
    {
        GUI.guiToScreen.a = guiX * screenWidth() / GUI.INSTANCE.size.x();
        GUI.guiToScreen.b = guiY * screenHeight() / GUI.INSTANCE.size.y();
        return GUI.guiToScreen;
    }
}
