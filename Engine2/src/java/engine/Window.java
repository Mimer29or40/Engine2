package engine;

import engine.event.*;
import engine.input.Keyboard;
import engine.input.Mouse;
import engine.util.Logger;
import org.joml.Vector2i;
import org.joml.Vector2ic;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

import java.util.Objects;

import static engine.Engine.*;
import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

@SuppressWarnings("unused")
public class Window
{
    private static final Logger LOGGER = new Logger();
    
    private final long handle;
    
    private Monitor[] monitors;
    private Monitor   monitor;
    
    private final Vector2i pos     = new Vector2i();
    private final Vector2i newPos  = new Vector2i();
    private final Vector2i fullPos = new Vector2i();
    
    private final Vector2i size     = new Vector2i();
    private final Vector2i newSize  = new Vector2i();
    private final Vector2i fullSize = new Vector2i();
    
    private final Vector2i frame    = new Vector2i();
    private final Vector2i newFrame = new Vector2i();
    
    private boolean focused, newFocused;
    
    private boolean fullscreen, newFullscreen;
    private boolean vsync, newVsync;
    
    private final Vector2i viewPos  = new Vector2i();
    private final Vector2i viewSize = new Vector2i();
    
    private boolean update = true;
    
    public Window(Mouse mouse, Keyboard keyboard)
    {
        Window.LOGGER.finest("Window Creation Started");
        
        Window.LOGGER.finest("GLFW: Init");
        GLFWErrorCallback.createPrint(System.err).set();
        if (!glfwInit()) throw new IllegalStateException("Unable to initialize GLFW");
        
        Window.LOGGER.finest("GLFW: Hints");
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        
        PointerBuffer monitors = Objects.requireNonNull(glfwGetMonitors(), "No monitors found.");
        this.monitors = new Monitor[monitors.limit()];
        for (int i = 0, n = this.monitors.length; i < n; i++) this.monitors[i] = new Monitor(monitors.get(), 1);
        this.monitor = this.monitors[0];
        
        Window.LOGGER.finest("GLFW: Checking Window Size");
        
        screenSize().mul(pixelSize(), this.size);
        if (this.fullscreen) this.size.set(this.monitor.size());
        Window.LOGGER.finest("Window Size: %s", this.size);
        
        if (this.size.x > this.monitor.width()) throw new RuntimeException(String.format("Window width (%s) is greater than Monitor width", this.size.x));
        if (this.size.y > this.monitor.height()) throw new RuntimeException(String.format("Window height (%s) is greater than Monitor height", this.size.y));
        
        Window.LOGGER.finest("GLFW: Creating Window");
        
        this.handle = glfwCreateWindow(this.size.x, this.size.y, "", NULL, NULL);
        
        glfwSetWindowSizeLimits(this.handle, screenWidth(), screenHeight(), GLFW_DONT_CARE, GLFW_DONT_CARE);
        
        if (this.handle == NULL) throw new RuntimeException("Failed to create the GLFW window");
        
        this.pos.x = (this.monitor.width() - this.size.x) >> 1;
        this.pos.y = (this.monitor.height() - this.size.y) >> 1;
        glfwSetWindowPos(this.handle, this.pos.x, this.pos.y);
        
        Window.LOGGER.finest("GLFW: Event Handling");
        
        glfwSetMonitorCallback((monitor, event) -> {
            PointerBuffer buffer = Objects.requireNonNull(glfwGetMonitors(), "No monitors found.");
            this.monitors = new Monitor[buffer.limit()];
            double next, max = 0.0;
            for (int i = 0, n = this.monitors.length; i < n; i++)
            {
                this.monitors[i] = new Monitor(buffer.get(), i);
                if ((next = this.monitors[i].isWindowIn(this)) > max)
                {
                    max          = next;
                    this.monitor = this.monitors[i];
                }
            }
            this.newPos.x = (this.monitor.width() - this.size.x) >> 1;
            this.newPos.y = (this.monitor.height() - this.size.y) >> 1;
        });
        
        glfwSetWindowCloseCallback(this.handle, window -> {
            if (window != this.handle) return;
            Engine.stop();
        });
        
        glfwSetWindowPosCallback(this.handle, (window, x, y) -> {
            if (window != this.handle) return;
            this.newPos.set(x, y);
        });
        
        glfwSetWindowSizeCallback(this.handle, (window, w, h) -> {
            if (window != this.handle) return;
            this.newSize.set(w, h);
        });
        
        glfwSetWindowFocusCallback(this.handle, (window, focused) -> {
            if (window != this.handle) return;
            this.newFocused = focused;
        });
        
        glfwSetFramebufferSizeCallback(this.handle, (window, w, h) -> {
            if (window != this.handle) return;
            this.newFrame.set(w, h);
        });
        
        glfwSetCursorEnterCallback(this.handle, (window, entered) -> {
            if (window != this.handle) return;
            mouse.enteredCallback(entered);
        });
        
        glfwSetCursorPosCallback(this.handle, (window, x, y) -> {
            if (window != this.handle) return;
            x = (x - this.viewPos.x) * (double) screenWidth() / (double) this.viewSize.x;
            y = (y - this.viewPos.y) * (double) screenHeight() / (double) this.viewSize.y;
            mouse.positionCallback(x, y);
        });
        
        glfwSetScrollCallback(this.handle, (window, x, y) -> {
            if (window != this.handle) return;
            mouse.scrollCallback(x, y);
        });
        
        glfwSetMouseButtonCallback(this.handle, (window, button, action, mods) -> {
            if (window != this.handle) return;
            mouse.stateCallback(button, action);
        });
        
        glfwSetKeyCallback(this.handle, (window, key, scancode, action, mods) -> {
            if (window != this.handle) return;
            keyboard.stateCallback(key, action);
        });
        
        glfwSetCharCallback(this.handle, (window, codePoint) -> {
            if (window != this.handle) return;
            keyboard.charCallback(codePoint);
        });
        
        show();
        
        Window.LOGGER.fine("Window Created");
    }
    
    /**
     * @return The current monitor that the window is on and will go fullscreen on.
     */
    public Monitor monitor()
    {
        return this.monitor;
    }
    
    /**
     * @return The position of the window in monitor coordinates.
     */
    public Vector2ic pos()
    {
        return this.pos;
    }
    
    /**
     * Sets the position of the window.
     *
     * @param x The new x value.
     * @param y The new y value.
     */
    public void pos(int x, int y)
    {
        this.newPos.set(x, y);
    }
    
    /**
     * Sets the position of the window.
     *
     * @param pos The new pos value.
     */
    public void pos(Vector2ic pos)
    {
        this.newPos.set(pos);
    }
    
    /**
     * @return The x position of the window in monitor coordinates.
     */
    public int x()
    {
        return this.pos.x;
    }
    
    /**
     * Sets the x position of the window.
     *
     * @param x The new x position.
     */
    public void x(int x)
    {
        this.newPos.x = x;
    }
    
    /**
     * @return The y position of the window in monitor coordinates.
     */
    public int y()
    {
        return this.pos.y;
    }
    
    /**
     * Sets the y position of the window.
     *
     * @param y The new y position.
     */
    public void y(int y)
    {
        this.newPos.y = y;
    }
    
    /**
     * @return The size of the window in monitor space.
     */
    public Vector2ic size()
    {
        return this.size;
    }
    
    /**
     * Sets the size of the window.
     *
     * @param width  The new width value.
     * @param height The new height value.
     */
    public void size(int width, int height)
    {
        this.newSize.set(width, height);
    }
    
    /**
     * Sets the size of the window.
     *
     * @param size The new pos value.
     */
    public void size(Vector2ic size)
    {
        this.newSize.set(size);
    }
    
    /**
     * @return The width of the window in monitor space.
     */
    public int width()
    {
        return this.size.x;
    }
    
    /**
     * Sets the width of the window.
     *
     * @param width The new width.
     */
    public void width(int width)
    {
        this.newSize.x = width;
    }
    
    /**
     * @return The height of the window in monitor space.
     */
    public int height()
    {
        return this.size.y;
    }
    
    /**
     * Sets the height of the window.
     *
     * @param height The new height.
     */
    public void height(int height)
    {
        this.newSize.y = height;
    }
    
    /**
     * @return The size of the framebuffer in pixels.
     */
    public Vector2ic frameBufferSize()
    {
        return this.frame;
    }
    
    /**
     * @return The width of the framebuffer in pixels.
     */
    public int frameBufferWidth()
    {
        return this.frame.x;
    }
    
    /**
     * @return The height of the framebuffer in pixels.
     */
    public int frameBufferHeight()
    {
        return this.frame.y;
    }
    
    /**
     * @return If the window has input focus.
     */
    public boolean focused()
    {
        return this.focused;
    }
    
    /**
     * @return If the window is in fullscreen mode.
     */
    public boolean fullscreen()
    {
        return this.fullscreen;
    }
    
    /**
     * Sets whether or not the window is in fullscreen mode.
     *
     * @param fullscreen The new fullscreen state.
     */
    public void fullscreen(boolean fullscreen)
    {
        this.newFullscreen = fullscreen;
    }
    
    /**
     * @return If the window is locking the frame rate to the refresh rate of the current monitor.
     */
    public boolean vsync()
    {
        return this.vsync;
    }
    
    /**
     * Sets whether or not the window should lock the frame rate to the refresh rate of the current monitor.
     *
     * @param vsync The new vsync state.
     */
    public void vsync(boolean vsync)
    {
        this.newVsync = vsync;
    }
    
    /**
     * @return The position of the viewport in the window.
     */
    public Vector2ic viewPos()
    {
        return this.viewPos;
    }
    
    /**
     * @return The x position of the viewport in the window.
     */
    public int viewX()
    {
        return this.viewPos.x;
    }
    
    /**
     * @return The y position of the viewport in the window.
     */
    public int viewY()
    {
        return this.viewPos.y;
    }
    
    /**
     * @return The size of the viewport in the window.
     */
    public Vector2ic viewSize()
    {
        return this.viewSize;
    }
    
    /**
     * @return The width of the viewport in the window.
     */
    public int viewW()
    {
        return this.viewSize.x;
    }
    
    /**
     * @return The height of the viewport in the window.
     */
    public int viewH()
    {
        return this.viewSize.y;
    }
    
    /**
     * Sets the title of the window.
     *
     * @param title The new title.
     */
    public void title(String title)
    {
        glfwSetWindowTitle(this.handle, title);
    }
    
    /**
     * Called by the Engine once per frame to generate any Events for any state changes.
     *
     * @param time  The time since the engine began in nanoseconds.
     * @param delta The time since the last frame in nanoseconds.
     */
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
                this.fullPos.set(this.pos);
                this.fullSize.set(this.size);
                this.newPos.set(this.monitor.pos());
                this.newSize.set(this.monitor.size());
            }
            else
            {
                this.newPos.set(this.fullPos);
                this.newSize.set(this.fullSize);
            }
        }
        
        if (this.vsync != this.newVsync)
        {
            this.vsync = this.newVsync;
            Events.post(EventWindowVSync.class, this.vsync);
            
            glfwSwapInterval(this.vsync ? 1 : 0);
        }
        
        if (this.pos.x != this.newPos.x || this.pos.y != this.newPos.y)
        {
            this.pos.set(this.newPos);
            Events.post(EventWindowMoved.class, this.pos);
            
            glfwSetWindowPos(this.handle, this.pos.x, this.pos.y);
            
            double next, max = 0.0;
            for (Monitor monitor : this.monitors)
            {
                if ((next = monitor.isWindowIn(this)) > max)
                {
                    max          = next;
                    this.monitor = monitor;
                }
            }
        }
        
        if (this.size.x != this.newSize.x || this.size.y != this.newSize.y)
        {
            this.size.set(this.newSize);
            Events.post(EventWindowResized.class, this.size);
            this.update = true;
            
            glfwSetWindowSize(this.handle, this.size.x, this.size.y);
        }
        
        if (this.frame.x != this.newFrame.x || this.frame.y != this.newFrame.y)
        {
            this.frame.set(this.newFrame);
            Events.post(EventFrameBufferResized.class, this.frame);
            this.update = true;
        }
    }
    
    /**
     * Called by the Engine to update the viewport.
     *
     * @return If the window viewport has been updated.
     */
    public boolean updateViewport()
    {
        if (this.update)
        {
            double aspect = (double) (screenWidth() * pixelWidth()) / (double) (screenHeight() * pixelHeight());
            
            this.viewSize.set(this.frame.x, (int) (this.frame.x / aspect));
            if (this.viewSize.y > this.frame.y) this.viewSize.set((int) (this.frame.y * aspect), this.frame.y);
            
            this.viewPos.set((this.frame.x - this.viewSize.x) >> 1, (this.frame.y - this.viewSize.y) >> 1);
        }
        glViewport(this.viewPos.x, this.viewPos.y, this.viewSize.x, this.viewSize.y);
        
        return this.update;
    }
    
    /**
     * Shows the window. Starts events being process for the window.
     */
    public void show()
    {
        glfwShowWindow(this.handle);
    }
    
    /**
     * Shows the window. Events will not longer be generated for the window.
     */
    public void hide()
    {
        glfwShowWindow(NULL);
    }
    
    /**
     * Makes the current thread current to allow OpenGL rendering.
     */
    public void makeCurrent()
    {
        glfwMakeContextCurrent(this.handle);
    }
    
    /**
     * Makes the current thread not current to allow for context switching.
     */
    public void unmakeCurrent()
    {
        glfwMakeContextCurrent(NULL);
    }
    
    /**
     * Processes all pending events. This will call the callback methods.
     */
    public void pollEvents()
    {
        glfwPollEvents();
    }
    
    /**
     * Swaps buffers to show what was rendered on the screen.
     */
    public void swap()
    {
        glfwSwapBuffers(this.handle);
        glClearColor(0, 0, 0, 1);
        glClear(GL_COLOR_BUFFER_BIT);
        this.update = false;
    }
    
    /**
     * Destroys the window.
     */
    public void destroy()
    {
        if (this.handle > 0)
        {
            GL.destroy();
            
            glfwFreeCallbacks(this.handle);
            glfwDestroyWindow(this.handle);
            
            glfwTerminate();
            glfwSetErrorCallback(null);
        }
    }
}
