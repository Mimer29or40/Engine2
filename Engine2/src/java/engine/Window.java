package engine;

import engine.event.*;
import engine.input.Keyboard;
import engine.input.Mouse;
import engine.util.Logger;
import org.joml.Vector2i;
import org.joml.Vector2ic;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;

import java.util.Objects;

import static engine.Engine.*;
import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window
{
    private static final Logger LOGGER = new Logger();
    
    private final long glfwWindow;
    
    private final Vector2i monitorSize = new Vector2i();
    
    private final Vector2i windowPos    = new Vector2i();
    private final Vector2i newWindowPos = new Vector2i();
    private final Vector2i fullPos      = new Vector2i();
    
    private final Vector2i windowSize    = new Vector2i();
    private final Vector2i newWindowSize = new Vector2i();
    private final Vector2i fullSize      = new Vector2i();
    
    private boolean focused, newFocused;
    
    private boolean fullscreen, newFullscreen;
    private boolean vsync, newVsync;
    
    private final Vector2i viewPos  = new Vector2i();
    private final Vector2i viewSize = new Vector2i();
    
    private boolean update = true;
    
    public Window(Mouse mouse, Keyboard keyboard)
    {
        Window.LOGGER.trace("Window Creation Started");
        
        screenSize().mul(pixelSize(), this.windowSize);
        
        Window.LOGGER.trace("Window Size: %s", this.windowSize);
        
        Window.LOGGER.trace("GLFW: Init");
        GLFWErrorCallback.createPrint(System.err).set();
        if (!glfwInit()) throw new IllegalStateException("Unable to initialize GLFW");
        
        Window.LOGGER.trace("GLFW: Hints");
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);
        
        Window.LOGGER.trace("GLFW: Checking Window Size");
        GLFWVidMode videoMode = Objects.requireNonNull(glfwGetVideoMode(glfwGetPrimaryMonitor()));
        this.monitorSize.set(videoMode.width(), videoMode.height());
        
        if (this.fullscreen) this.windowSize.set(this.monitorSize);
        
        if (this.windowSize.x > this.monitorSize.x) throw new RuntimeException(String.format("Window width (%s) is greater than Monitor width", this.windowSize.x));
        if (this.windowSize.y > this.monitorSize.y) throw new RuntimeException(String.format("Window height (%s) is greater than Monitor height", this.windowSize.y));
        
        Window.LOGGER.trace("GLFW: Creating Window");
        
        this.glfwWindow = glfwCreateWindow(this.windowSize.x, this.windowSize.y, "", NULL, NULL);
        
        glfwSetWindowSizeLimits(this.glfwWindow, screenWidth(), screenHeight(), GLFW_DONT_CARE, GLFW_DONT_CARE);
        
        if (this.glfwWindow == NULL) throw new RuntimeException("Failed to create the GLFW window");
    
        this.windowPos.x = (this.monitorSize.x - this.windowSize.x) >> 1;
        this.windowPos.y = (this.monitorSize.y - this.windowSize.y) >> 1;
        glfwSetWindowPos(this.glfwWindow, this.windowPos.x, this.windowPos.y);
        
        Window.LOGGER.trace("GLFW: Event Handling");
        
        glfwSetWindowCloseCallback(this.glfwWindow, window -> {
            if (window != this.glfwWindow) return;
            Engine.stop();
        });
        
        glfwSetWindowPosCallback(this.glfwWindow, (window, x, y) -> {
            if (window != this.glfwWindow) return;
            this.newWindowPos.set(x, y);
        });
        
        glfwSetWindowSizeCallback(this.glfwWindow, (window, w, h) -> {
            if (window != this.glfwWindow) return;
            this.newWindowSize.set(w, h);
        });
        
        glfwSetWindowFocusCallback(this.glfwWindow, (window, focused) -> {
            if (window != this.glfwWindow) return;
            this.newFocused = focused;
        });
        
        glfwSetCursorEnterCallback(this.glfwWindow, (window, entered) -> {
            if (window != this.glfwWindow) return;
            mouse.enteredCallback(entered);
        });
        
        glfwSetCursorPosCallback(this.glfwWindow, (window, x, y) -> {
            if (window != this.glfwWindow) return;
            x = (x - this.viewPos.x) * (double) screenWidth() / (double) this.viewSize.x;
            y = (y - this.viewPos.y) * (double) screenHeight() / (double) this.viewSize.y;
            mouse.positionCallback(x, y);
        });
        
        glfwSetScrollCallback(this.glfwWindow, (window, x, y) -> {
            if (window != this.glfwWindow) return;
            mouse.scrollCallback(x, y);
        });
        
        glfwSetMouseButtonCallback(this.glfwWindow, (window, button, action, mods) -> {
            if (window != this.glfwWindow) return;
            mouse.stateCallback(button, action);
        });
        
        glfwSetKeyCallback(this.glfwWindow, (window, key, scancode, action, mods) -> {
            if (window != this.glfwWindow) return;
            keyboard.stateCallback(key, action);
        });
        
        glfwSetCharCallback(this.glfwWindow, (window, codePoint) -> {
            if (window != this.glfwWindow) return;
            keyboard.charCallback(codePoint);
        });
        
        show();
    
        Window.LOGGER.debug("Window Created");
    }
    
    public Vector2ic monitorSize()             { return this.monitorSize; }
    
    public int monitorWidth()                  { return this.monitorSize.x; }
    
    public int monitorHeight()                 { return this.monitorSize.y; }
    
    public Vector2ic windowPos()               { return this.windowPos; }
    
    public int windowX()                       { return this.windowPos.x; }
    
    public int windowY()                       { return this.windowPos.y; }
    
    public Vector2ic windowSize()              { return this.windowSize; }
    
    public int windowWidth()                   { return this.windowSize.x; }
    
    public int windowHeight()                  { return this.windowSize.y; }
    
    public boolean focused()                   { return this.focused; }
    
    public boolean fullscreen()                { return this.fullscreen; }
    
    public void fullscreen(boolean fullscreen) { this.newFullscreen = fullscreen; }
    
    public boolean vsync()                     { return this.vsync; }
    
    public void vsync(boolean vsync)           { this.newVsync = vsync; }
    
    public Vector2ic viewPos()                 { return this.viewPos; }
    
    public int viewX()                         { return this.viewPos.x; }
    
    public int viewY()                         { return this.viewPos.y; }
    
    public Vector2ic viewSize()                { return this.viewSize; }
    
    public int viewW()                         { return this.viewSize.x; }
    
    public int viewH()                         { return this.viewSize.y; }
    
    public void title(String title)            { glfwSetWindowTitle(this.glfwWindow, title); }
    
    public void handleEvents(long time, long delta)
    {
        if (this.focused != this.newFocused)
        {
            this.focused = this.newFocused;
            Events.post(EventWindowFocused.class, this.focused);
        }
        
        if (this.fullscreen != this.newFullscreen)
        {
            this.fullscreen = this.newFullscreen;
            Events.post(EventWindowFullscreen.class, this.fullscreen);
            
            if (this.fullscreen)
            {
                this.fullPos.set(this.windowPos);
                this.fullSize.set(this.windowSize);
                this.newWindowPos.set(0, 0);
                this.newWindowSize.set(this.monitorSize);
            }
            else
            {
                this.newWindowPos.set(this.fullPos);
                this.newWindowSize.set(this.fullSize);
            }
        }
        
        if (this.vsync != this.newVsync)
        {
            this.vsync = this.newVsync;
            Events.post(EventWindowVSync.class, this.vsync);
            
            glfwSwapInterval(this.vsync ? 1 : 0);
        }
        
        if (this.windowPos.x != this.newWindowPos.x || this.windowPos.y != this.newWindowPos.y)
        {
            this.windowPos.set(this.newWindowPos);
            Events.post(EventWindowMoved.class, this.windowPos);
            
            glfwSetWindowPos(this.glfwWindow, this.windowPos.x, this.windowPos.y);
        }
        
        if (this.windowSize.x != this.newWindowSize.x || this.windowSize.y != this.newWindowSize.y)
        {
            this.windowSize.set(this.newWindowSize);
            Events.post(EventWindowResized.class, this.windowSize);
            this.update = true;
            
            glfwSetWindowSize(this.glfwWindow, this.windowSize.x, this.windowSize.y);
        }
    }
    
    public boolean updateViewport()
    {
        if (this.update)
        {
            double aspect = (double) (screenWidth() * pixelWidth()) / (double) (screenHeight() * pixelHeight());
    
            this.viewSize.set(this.windowSize.x, (int) (this.windowSize.x / aspect));
            if (this.viewSize.y > this.windowSize.y) this.viewSize.set((int) (this.windowSize.y * aspect), this.windowSize.y);
    
            this.viewPos.set((this.windowSize.x - this.viewSize.x) >> 1, (this.windowSize.y - this.viewSize.y) >> 1);
    
            glViewport(this.viewPos.x, this.viewPos.y, this.viewSize.x, this.viewSize.y);
        }
        return this.update;
    }
    
    public void show()          { glfwShowWindow(this.glfwWindow); }
    
    public void hide()          { glfwShowWindow(NULL); }
    
    public void makeCurrent()   { glfwMakeContextCurrent(this.glfwWindow); }
    
    public void unmakeCurrent() { glfwMakeContextCurrent(NULL); }
    
    public void pollEvents()    { glfwPollEvents(); }
    
    public void swap()
    {
        glfwSwapBuffers(this.glfwWindow);
        glClear(GL_COLOR_BUFFER_BIT);
        this.update = false;
    }
    
    public void destroy()
    {
        if (this.glfwWindow > 0)
        {
            glfwFreeCallbacks(this.glfwWindow);
            glfwDestroyWindow(this.glfwWindow);
            
            glfwTerminate();
            glfwSetErrorCallback(null);
        }
    }
}
