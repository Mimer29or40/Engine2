package engine.gui;

import engine.Engine;
import engine.Extension;
import engine.Keyboard;
import engine.Mouse;
import engine.color.Color;
import engine.event.*;
import engine.font.FontFamily;
import engine.gui.elment.UIContainer;
import engine.gui.elment.UIWindow;
import engine.gui.util.Rect;
import org.joml.Vector2d;
import org.joml.Vector2dc;
import org.joml.Vector2i;
import org.joml.Vector2ic;

public class EEXT_GUI extends Extension
{
    public static final EEXT_GUI INSTANCE = new EEXT_GUI();
    
    private static final Vector2d screenToGUI = new Vector2d(0, 0);
    private static final Vector2d guiToScreen = new Vector2d(0, 0);
    
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
    
    public EEXT_GUI()
    {
        super();
        this.enabled = false;
    }
    
    /**
     * Sets the extension enabled or not.
     */
    @Override
    public void enabled(boolean enabled)
    {
        this.enabled = enabled;
        if (this.enabled)
        {
            EventBus.register(this);
        }
        else
        {
            EventBus.unregister(this);
        }
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
        FontFamily.register("fonts", "OpenSans");
    }
    
    /**
     * This is called once per frame before the {@link Engine#draw} method is called.
     *
     * @param elapsedTime The time in seconds since the last frame.
     */
    @Override
    public void beforeDraw(double elapsedTime)
    {
        Engine.profiler().startSection("Theme Update");
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
                    }
                }
            }
        }
        Engine.profiler().endSection();
        
        Vector2dc mouse = screenToGUI(Engine.mouse().x(), Engine.mouse().y());
        
        double mouseX = mouse.x();
        double mouseY = mouse.y();
        
        Engine.profiler().startSection("Stack Solving");
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
        Engine.profiler().endSection();
    }
    
    @EventBus.Subscribe
    public void handleMouseButton(EventMouseButton event)
    {
        Engine.profiler().startSection("Mouse");
        {
            Mouse.Button button = event.button();
    
            Vector2dc pos = event.pos();
            
            if (event instanceof EventMouseButtonDown)
            {
                UIElement element = this.topElement;
                while (element != null)
                {
                    if (element.onMouseButtonDown(button, pos.x() - element.absX(), pos.y() - element.absY())) break;
                    element = element.parent();
                }
                setFocused(element);
                return;
            }
            if (event instanceof EventMouseButtonUp)
            {
                // this.drag = null;
    
                UIElement element = this.topElement;
                while (element != null)
                {
                    if (element.onMouseButtonUp(button, pos.x() - element.absX(), pos.y() - element.absY())) break;
                    element = element.parent();
                }
                return;
            }
            if (event instanceof EventMouseButtonHeld)
            {
                UIElement element = this.topElement;
                while (element != null)
                {
                    if (element.onMouseButtonHeld(button, pos.x() - element.absX(), pos.y() - element.absY())) break;
                    element = element.parent();
                }
                return;
            }
            if (event instanceof EventMouseButtonRepeated)
            {
                UIElement element = this.topElement;
                while (element != null)
                {
                    if (element.onMouseButtonRepeated(button, pos.x() - element.absX(), pos.y() - element.absY())) break;
                    element = element.parent();
                }
                return;
            }
            if (event instanceof EventMouseButtonPressed)
            {
                EventMouseButtonPressed e = (EventMouseButtonPressed) event;
    
                boolean doubleClicked = e.doublePressed();
    
                UIElement element = this.topElement;
                while (element != null)
                {
                    if (element.onMouseButtonClicked(button, pos.x() - element.absX(), pos.y() - element.absY(), doubleClicked)) break;
                    element = element.parent();
                }
                return;
            }
            if (event instanceof EventMouseButtonDragged)
            {
                EventMouseButtonDragged e = (EventMouseButtonDragged) event;
    
                Vector2dc drag = e.dragStart();
                Vector2dc rel  = e.rel();
    
                UIElement element = this.topElement;
                while (element != null)
                {
                    if (element.onMouseButtonDragged(button, pos.x() - element.absX(), pos.y() - element.absY(), drag.x(), drag.y(), rel.x(), rel.y()))
                    {
                        // this.drag = element;
                        break;
                    }
                    element = element.parent();
                }
                return;
            }
        }
        Engine.profiler().endSection();
    }
    
    @EventBus.Subscribe
    public void handleMouseButton(EventMouseScrolled event)
    {
        Vector2dc dir = event.scroll();
    
        // TODO - This may cause double events so look out for them. FIXED MAYBE?
        UIElement element = this.topElement != this.focusedVScrollbar ? this.focusedVScrollbar : this.topElement;
        while (element != null)
        {
            if (element.onMouseScrolled(dir.x(), dir.y())) break;
            element = element.parent();
        }
    }
    
    @EventBus.Subscribe
    public void handleKeyboardKey(EventKeyboardKey event)
    {
        Engine.profiler().startSection("Keyboard");
        {
            if (this.focusedElement != null)
            {
                Keyboard.Key key = event.key();
                
                if (event instanceof EventKeyboardKeyDown)
                {
                    UIElement element = this.focusedElement;
                    while (element != null)
                    {
                        if (element.onKeyboardKeyDown(key)) break;
                        element = element.parent();
                    }
                    return;
                }
                if (event instanceof EventKeyboardKeyUp)
                {
                    UIElement element = this.focusedElement;
                    while (element != null)
                    {
                        if (element.onKeyboardKeyUp(key)) break;
                        element = element.parent();
                    }
                    return;
                }
                if (event instanceof EventKeyboardKeyHeld)
                {
                    UIElement element = this.focusedElement;
                    while (element != null)
                    {
                        if (element.onKeyboardKeyHeld(key)) break;
                        element = element.parent();
                    }
                    return;
                }
                if (event instanceof EventKeyboardKeyRepeated)
                {
                    UIElement element = this.focusedElement;
                    while (element != null)
                    {
                        if (element.onKeyboardKeyRepeated(key)) break;
                        element = element.parent();
                    }
                    return;
                }
                if (event instanceof EventKeyboardKeyPressed)
                {
                    boolean doublePressed = ((EventKeyboardKeyPressed) event).doublePressed();
    
                    UIElement element = this.focusedElement;
                    while (element != null)
                    {
                        if (element.onKeyboardKeyPressed(key, doublePressed)) break;
                        element = element.parent();
                    }
                    return;
                }
            }
        }
        Engine.profiler().endSection();
    }
    
    @EventBus.Subscribe
    public void handleKeyTyped(EventKeyboardTyped event)
    {
        String charTyped = event.typed();
    
        UIElement element = this.focusedElement;
        while (element != null)
        {
            if (element.onKeyboardKeyTyped(charTyped)) break;
            element = element.parent();
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
        Vector2dc mouse = screenToGUI(Engine.mouse().x(), Engine.mouse().y());
        
        double mouseX = mouse.x();
        double mouseY = mouse.y();
        
        Engine.profiler().startSection("UIElements Update");
        {
            this.redrawScreen |= this.rootContainer.update(elapsedTime, mouseX, mouseY);
        }
        Engine.profiler().endSection();
        
        Engine.profiler().startSection("UIElements Draw");
        {
            if (this.redrawScreen)
            {
                Engine.layer(Engine.layerCount() - 1);
                
                Engine.clear(Color.BLANK);
                
                this.rootContainer.draw(elapsedTime, mouseX, mouseY);
                
                this.redrawScreen = false;
            }
            Engine.profiler().endSection();
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
        EEXT_GUI.INSTANCE.enabled = true;
        
        EEXT_GUI.INSTANCE.size.set(width, height);
        Engine.createLayer(Engine.layerCount() - 1, width, height);
        
        EEXT_GUI.INSTANCE.theme = new Theme();
        if (themePath != null) EEXT_GUI.INSTANCE.theme.loadTheme(themePath);
        
        EEXT_GUI.INSTANCE.liveThemeUpdates = liveThemeUpdates;
        
        EEXT_GUI.INSTANCE.rootContainer = new UIContainer(new Rect(0, 0, width, height), null, null, "#root_container");
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
        createGUI(Engine.screenWidth(), Engine.screenHeight(), theme, liveThemeUpdates);
    }
    
    public static void createGUI(int width, int height)
    {
        createGUI(width, height, null, false);
    }
    
    public static void createGUI(String theme)
    {
        createGUI(Engine.screenWidth(), Engine.screenHeight(), theme, false);
    }
    
    public static void createGUI(boolean liveThemeUpdates)
    {
        createGUI(Engine.screenWidth(), Engine.screenHeight(), null, liveThemeUpdates);
    }
    
    public static void createGUI()
    {
        createGUI(Engine.screenWidth() * Engine.pixelWidth(), Engine.screenHeight() * Engine.pixelHeight(), null, false);
    }
    
    public static Theme theme()
    {
        return EEXT_GUI.INSTANCE.theme;
    }
    
    public static UIContainer rootContainer()
    {
        return EEXT_GUI.INSTANCE.rootContainer;
    }
    
    public static UIElement focusedVScrollbar()
    {
        return EEXT_GUI.INSTANCE.focusedVScrollbar;
    }
    
    public static UIElement focusedHScrollbar()
    {
        return EEXT_GUI.INSTANCE.focusedHScrollbar;
    }
    
    public static void setFocused(UIElement element)
    {
        if (element == EEXT_GUI.INSTANCE.focusedElement) return;
        
        if (EEXT_GUI.INSTANCE.focusedElement != null) EEXT_GUI.INSTANCE.focusedElement.onUnfocus();
        
        EEXT_GUI.INSTANCE.focusedElement = element;
        
        if (EEXT_GUI.INSTANCE.focusedElement != null)
        {
            EEXT_GUI.INSTANCE.focusedElement.onFocus();
            
            if (EEXT_GUI.INSTANCE.focusedElement.containsElementID("vertical_scroll_bar")) EEXT_GUI.INSTANCE.focusedVScrollbar = EEXT_GUI.INSTANCE.focusedElement;
            if (EEXT_GUI.INSTANCE.focusedElement.containsElementID("horizontal_scroll_bar")) EEXT_GUI.INSTANCE.focusedHScrollbar = EEXT_GUI.INSTANCE.focusedElement;
        }
    }
    
    public static void clearFocusedVScrollbar(UIElement element)
    {
        if (element != null && EEXT_GUI.INSTANCE.focusedVScrollbar == element) EEXT_GUI.INSTANCE.focusedVScrollbar = null;
    }
    
    public static void clearFocusedHScrollbar(UIElement element)
    {
        if (element != null && EEXT_GUI.INSTANCE.focusedHScrollbar == element) EEXT_GUI.INSTANCE.focusedHScrollbar = null;
    }
    
    public static Vector2dc screenToGUI(double screenX, double screenY)
    {
        EEXT_GUI.screenToGUI.x = screenX * EEXT_GUI.INSTANCE.size.x() / Engine.screenWidth();
        EEXT_GUI.screenToGUI.y = screenY * EEXT_GUI.INSTANCE.size.y() / Engine.screenHeight();
        return EEXT_GUI.screenToGUI;
    }
    
    public static Vector2dc guiToScreen(double guiX, double guiY)
    {
        EEXT_GUI.guiToScreen.x = guiX * Engine.screenWidth() / EEXT_GUI.INSTANCE.size.x();
        EEXT_GUI.guiToScreen.y = guiY * Engine.screenHeight() / EEXT_GUI.INSTANCE.size.y();
        return EEXT_GUI.guiToScreen;
    }
    
    public static Vector2ic guiSize()
    {
        return EEXT_GUI.INSTANCE.size;
    }
    
    public static int guiWidth()
    {
        return EEXT_GUI.INSTANCE.size.x;
    }
    
    public static int guiHeight()
    {
        return EEXT_GUI.INSTANCE.size.y;
    }
}
