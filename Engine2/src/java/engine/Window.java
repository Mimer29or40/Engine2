package engine;

import org.joml.*;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.system.MemoryStack;
import rutils.Logger;

import java.lang.Math;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.glFinish;

public class Window
{
    private static final Logger LOGGER = new Logger();
    
    protected final long handle;
    
    protected Monitor monitor;
    
    protected boolean windowed;
    
    protected int refreshRate;
    
    protected final Vector2i minSize = new Vector2i();
    protected final Vector2i maxSize = new Vector2i();
    
    protected final Matrix4d viewMatrix = new Matrix4d();
    
    // -------------------- Callback Objects -------------------- //
    protected boolean close;
    protected boolean _close;
    
    protected boolean vsync;
    protected boolean _vsync;
    
    protected boolean focused;
    protected boolean _focused;
    
    protected boolean iconified;
    protected boolean _iconified;
    
    protected boolean maximized;
    protected boolean _maximized;
    
    protected final Vector2i pos  = new Vector2i();
    protected final Vector2i _pos = new Vector2i();
    
    protected final Vector2i size  = new Vector2i();
    protected final Vector2i _size = new Vector2i();
    
    protected final Vector2d scale  = new Vector2d();
    protected final Vector2d _scale = new Vector2d();
    
    protected final Vector2i fbSize  = new Vector2i();
    protected final Vector2i _fbSize = new Vector2i();
    
    protected boolean _refresh;
    
    protected String[] _dropped;
    
    // -------------------- Internal Objects -------------------- //
    
    private final Vector2i deltaI = new Vector2i();
    private final Vector2d deltaD = new Vector2d();
    
    protected Window()
    {
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        
        int width  = Engine.screenSize.x * Engine.pixelSize.x;
        int height = Engine.screenSize.y * Engine.pixelSize.y;
        
        this.handle = glfwCreateWindow(width, height, "", 0L, 0L);
        if (this.handle == 0L) throw new RuntimeException("Failed to create the GLFW window");
        
        this.monitor = Engine.primaryMonitor;
        
        this.windowed = true;
        
        this._vsync = false;
        this.vsync  = true;
        
        this.focused = this._focused = glfwGetWindowAttrib(this.handle, GLFW_FOCUSED) == GLFW_TRUE;
        
        this.iconified = this._iconified = glfwGetWindowAttrib(this.handle, GLFW_ICONIFIED) == GLFW_TRUE;
        
        this.maximized = this._maximized = glfwGetWindowAttrib(this.handle, GLFW_MAXIMIZED) == GLFW_TRUE;
        
        try (MemoryStack stack = MemoryStack.stackPush())
        {
            IntBuffer x = stack.mallocInt(1);
            IntBuffer y = stack.mallocInt(1);
            
            FloatBuffer xf = stack.mallocFloat(1);
            FloatBuffer yf = stack.mallocFloat(1);
            
            glfwGetWindowPos(this.handle, x, y);
            this.pos.set(this._pos.set(x.get(0), y.get(0)));
            
            glfwGetWindowSize(this.handle, x, y);
            this.size.set(this._size.set(x.get(0), y.get(0)));
            
            glfwGetWindowContentScale(this.handle, xf, yf);
            this.scale.set(this._scale.set(xf.get(0), yf.get(0)));
            
            glfwGetFramebufferSize(this.handle, x, y);
            this.fbSize.set(this._fbSize.set(x.get(0), y.get(0)));
        }
        
        this.minSize.set(GLFW_DONT_CARE);
        this.maxSize.set(GLFW_DONT_CARE);
        
        glfwSetWindowSizeLimits(handle, this.minSize.x, this.minSize.y, this.maxSize.x, this.maxSize.y);
        
        glfwSetInputMode(handle, GLFW_LOCK_KEY_MODS, Modifier.lockMods() ? GLFW_TRUE : GLFW_FALSE);
        
        this.refreshRate = GLFW_DONT_CARE;
        
        show();
    }
    
    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Window window = (Window) o;
        return this.handle == window.handle;
    }
    
    @Override
    public int hashCode()
    {
        return Objects.hash(this.handle);
    }
    
    @Override
    public String toString()
    {
        return "Window{" + "name='" + this.handle + '\'' + '}';
    }
    
    // -------------------- Properties -------------------- //
    
    /**
     * Sets the window title, encoded as UTF-8, of the window.
     *
     * @param title The new title.
     */
    public void title(CharSequence title)
    {
        Engine.runTask(() -> glfwSetWindowTitle(this.handle, title));
    }
    
    /**
     * Sets the icon for the window.
     * <p>
     * This function sets the icon of the window. If passed an array of
     * candidate images, those of or closest to the sizes desired by the system
     * are selected. If no images are specified, the window reverts to its
     * default icon.
     * <p>
     * The pixels are 32-bit, little-endian, non-premultiplied RGBA, i.e. eight
     * bits per channel with the red channel first. They are arranged
     * canonically as packed sequential rows, starting from the top-left
     * corner.
     * <p>
     * The desired image sizes varies depending on platform and system
     * settings. The selected images will be rescaled as needed. Good sizes
     * include 16x16, 32x32 and 48x48.
     *
     * @param icon The new icon.
     */
    public void icon(GLFWImage.Buffer icon)
    {
        Engine.runTask(() -> glfwSetWindowIcon(this.handle, icon));
    }
    
    /**
     * @return Retrieves the current aspect ration of the window.
     */
    public double aspectRatio()
    {
        return (double) this.fbSize.x / (double) this.fbSize.y;
    }
    
    /**
     * Sets the required aspect ratio of the content area of the window. If the
     * window is full screen, the aspect ratio only takes effect once it is
     * made windowed. If the window is not resizable, this function does
     * nothing.
     * <p>
     * The aspect ratio is as a numerator and a denominator and both values
     * must be greater than zero. For example, the common 16:9 aspect ratio is
     * as 16 and 9, respectively.
     * <p>
     * If the numerator and denominator is set to
     * {@link org.lwjgl.glfw.GLFW#GLFW_DONT_CARE DONT_CARE} then the aspect
     * ratio limit is disabled.
     * <p>
     * The aspect ratio is applied immediately to a windowed mode window and
     * may cause it to be resized.
     *
     * @param numer the numerator of the desired aspect ratio, or {@link org.lwjgl.glfw.GLFW#GLFW_DONT_CARE DONT_CARE}
     * @param denom the denominator of the desired aspect ratio, or {@link org.lwjgl.glfw.GLFW#GLFW_DONT_CARE DONT_CARE}
     */
    public void aspectRatio(int numer, int denom)
    {
        Engine.runTask(() -> glfwSetWindowAspectRatio(this.handle, numer, denom));
    }
    
    /**
     * Restores the window if it was previously iconified (minimized) or
     * maximized. If the window is already restored, this function does
     * nothing.
     *
     * <p>If the window is a full screen window, the resolution
     * chosen for the window is restored on the selected monitor.</p>
     */
    public void restore()
    {
        Engine.runTask(() -> glfwRestoreWindow(this.handle));
    }
    
    /**
     * @return Retrieves if the window is resizable <i>by the user</i>.
     */
    @SuppressWarnings("ConstantConditions")
    public boolean resizable()
    {
        return Engine.waitReturnTask(() -> glfwGetWindowAttrib(this.handle, GLFW_RESIZABLE) == GLFW_TRUE);
    }
    
    /**
     * Indicates whether the window is resizable <i>by the user</i>.
     *
     * @param resizable if the window is resizable <i>by the user</i>.
     */
    public void resizable(boolean resizable)
    {
        Engine.runTask(() -> glfwSetWindowAttrib(this.handle, GLFW_RESIZABLE, resizable ? GLFW_TRUE : GLFW_FALSE));
    }
    
    /**
     * @return Retrieves if the window is visible. Window visibility can be controlled with {@link #show} and {@link #hide}.
     */
    @SuppressWarnings("ConstantConditions")
    public boolean visible()
    {
        return Engine.waitReturnTask(() -> glfwGetWindowAttrib(this.handle, GLFW_VISIBLE) == GLFW_TRUE);
    }
    
    /**
     * Makes the window visible if it was previously hidden. If the window is
     * already visible or is in full screen mode, this function does nothing.
     */
    public void show()
    {
        Engine.runTask(() -> glfwShowWindow(this.handle));
    }
    
    /**
     * Hides the window, if it was previously visible. If the window is already
     * hidden or is in full screen mode, this function does nothing.
     */
    public void hide()
    {
        Engine.runTask(() -> glfwHideWindow(this.handle));
    }
    
    /**
     * @return Retrieves if the window has decorations such as a border, a close widget, etc.
     */
    @SuppressWarnings("ConstantConditions")
    public boolean decorated()
    {
        return Engine.waitReturnTask(() -> glfwGetWindowAttrib(this.handle, GLFW_DECORATED) == GLFW_TRUE);
    }
    
    /**
     * Indicates whether the window has decorations such as a border, a close
     * widget, etc.
     *
     * @param decorated if the window has decorations.
     */
    public void decorated(boolean decorated)
    {
        Engine.runTask(() -> glfwSetWindowAttrib(this.handle, GLFW_DECORATED, decorated ? GLFW_TRUE : GLFW_FALSE));
    }
    
    /**
     * @return Retrieves if the window is floating, also called topmost or always-on-top.
     */
    @SuppressWarnings("ConstantConditions")
    public boolean floating()
    {
        return Engine.waitReturnTask(() -> glfwGetWindowAttrib(this.handle, GLFW_FLOATING) == GLFW_TRUE);
    }
    
    /**
     * Indicates whether the window is floating, also called topmost or
     * always-on-top.
     *
     * @param floating if the window is floating.
     */
    public void floating(boolean floating)
    {
        Engine.runTask(() -> glfwSetWindowAttrib(this.handle, GLFW_FLOATING, floating ? GLFW_TRUE : GLFW_FALSE));
    }
    
    /**
     * @return Retrieves if the cursor is currently directly over the content area of the window, with no other windows between.
     */
    @SuppressWarnings("ConstantConditions")
    public boolean hovered()
    {
        return Engine.waitReturnTask(() -> glfwGetWindowAttrib(this.handle, GLFW_HOVERED) == GLFW_TRUE);
    }
    
    /**
     * @return Retrieves if input focuses on calling show window.
     */
    @SuppressWarnings("ConstantConditions")
    public boolean focusOnShow()
    {
        return Engine.waitReturnTask(() -> glfwGetWindowAttrib(this.handle, GLFW_FOCUS_ON_SHOW) == GLFW_TRUE);
    }
    
    /**
     * Indicates if input focuses on calling show window.
     *
     * @param focusOnShow if input focuses on calling show window.
     */
    public void focusOnShow(boolean focusOnShow)
    {
        Engine.runTask(() -> glfwSetWindowAttrib(this.handle, GLFW_FOCUS_ON_SHOW, focusOnShow ? GLFW_TRUE : GLFW_FALSE));
    }
    
    /**
     * Raw access to {@link org.lwjgl.glfw.GLFW#glfwGetWindowAttrib}
     *
     * @param attribute The attribute to quarry
     * @return The value of the attribute.
     */
    @SuppressWarnings("ConstantConditions")
    public int getAttribute(int attribute)
    {
        return Engine.waitReturnTask(() -> glfwGetWindowAttrib(this.handle, attribute));
    }
    
    /**
     * Raw access to {@link org.lwjgl.glfw.GLFW#glfwSetWindowAttrib}
     *
     * @param attribute The attribute
     * @param value     The new value of the attribute.
     */
    public void setAttribute(int attribute, int value)
    {
        Engine.runTask(() -> glfwSetWindowAttrib(this.handle, attribute, value));
    }
    
    /**
     * Retrieves the minimum size, in screen coordinates, of the content area
     * of the window. If you wish to retrieve the size of the framebuffer of
     * the window in pixels, see {@link #framebufferSize framebufferSize}.
     *
     * @return The minimum size, in screen coordinates, of the content area.
     */
    public Vector2ic minSize()
    {
        return this.minSize;
    }
    
    /**
     * Retrieves the minimum width, in screen coordinates, of the content area
     * of the window. If you wish to retrieve the size of the framebuffer of
     * the window in pixels, see {@link #framebufferSize framebufferSize}.
     *
     * @return The minimum size, in screen coordinates, of the content area.
     */
    public int minWidth()
    {
        return this.minSize.x;
    }
    
    /**
     * Retrieves the minimum height, in screen coordinates, of the content area
     * of the window. If you wish to retrieve the size of the framebuffer of
     * the window in pixels, see {@link #framebufferSize framebufferSize}.
     *
     * @return The minimum size, in screen coordinates, of the content area.
     */
    public int minHeight()
    {
        return this.minSize.y;
    }
    
    /**
     * Retrieves the maximum size, in screen coordinates, of the content area
     * of the window. If you wish to retrieve the size of the framebuffer of
     * the window in pixels, see {@link #framebufferSize framebufferSize}.
     *
     * @return The maximum size, in screen coordinates, of the content area.
     */
    public Vector2ic maxSize()
    {
        return this.maxSize;
    }
    
    /**
     * Retrieves the maximum width, in screen coordinates, of the content area
     * of the window. If you wish to retrieve the size of the framebuffer of
     * the window in pixels, see {@link #framebufferSize framebufferSize}.
     *
     * @return The maximum size, in screen coordinates, of the content area.
     */
    public int maxWidth()
    {
        return this.maxSize.x;
    }
    
    /**
     * Retrieves the maximum height, in screen coordinates, of the content area
     * of the window. If you wish to retrieve the size of the framebuffer of
     * the window in pixels, see {@link #framebufferSize framebufferSize}.
     *
     * @return The maximum size, in screen coordinates, of the content area.
     */
    public int maxHeight()
    {
        return this.maxSize.y;
    }
    
    /**
     * Sets the size limits of the content area of the window. If the window is
     * full screen, the size limits only take effect if once it is made
     * windowed. If the window is not resizable, this function does nothing.
     * <p>
     * The size limits are applied immediately to a windowed mode window and
     * may cause it to be resized.
     * <p>
     * The maximum dimensions must be greater than or equal to the minimum
     * dimensions and all must be greater than or equal to zero.
     *
     * @param minWidth  the minimum width, in screen coordinates, of the content area, or {@link org.lwjgl.glfw.GLFW#GLFW_DONT_CARE DONT_CARE}
     * @param minHeight the minimum height, in screen coordinates, of the content area, or {@link org.lwjgl.glfw.GLFW#GLFW_DONT_CARE DONT_CARE}
     * @param maxWidth  the maximum width, in screen coordinates, of the content area, or {@link org.lwjgl.glfw.GLFW#GLFW_DONT_CARE DONT_CARE}
     * @param maxHeight the maximum height, in screen coordinates, of the content area, or {@link org.lwjgl.glfw.GLFW#GLFW_DONT_CARE DONT_CARE}
     */
    public void sizeLimits(int minWidth, int minHeight, int maxWidth, int maxHeight)
    {
        this.minSize.set(minWidth, minHeight);
        this.maxSize.set(maxWidth, maxHeight);
        
        Engine.runTask(() -> glfwSetWindowSizeLimits(this.handle, minWidth, minHeight, maxWidth, maxHeight));
    }
    
    /**
     * Sets the size limits of the content area of the window. If the window is
     * full screen, the size limits only take effect if once it is made
     * windowed. If the window is not resizable, this function does nothing.
     * <p>
     * The size limits are applied immediately to a windowed mode window and
     * may cause it to be resized.
     * <p>
     * The maximum dimensions must be greater than or equal to the minimum
     * dimensions and all must be greater than or equal to zero.
     *
     * @param min the minimum size, in screen coordinates, of the content area, or {@link org.lwjgl.glfw.GLFW#GLFW_DONT_CARE DONT_CARE}
     * @param max the maximum size, in screen coordinates, of the content area, or {@link org.lwjgl.glfw.GLFW#GLFW_DONT_CARE DONT_CARE}
     */
    public void sizeLimits(Vector2ic min, Vector2ic max)
    {
        sizeLimits(min.x(), min.y(), max.x(), max.y());
    }
    
    /**
     * Retrieves the size, in screen coordinates, of each edge of the frame of
     * the window. This size includes the title bar, if the window has one. The
     * size of the frame may vary depending on the
     * <a target="_blank" href="http://www.glfw.org/docs/latest/window.html#window-hints_wnd">window-related hints</a>
     * used to create it.
     * <p>
     * Because this function retrieves the size of each window frame edge and
     * not the offset along a particular coordinate axis, the retrieved values
     * will always be zero or positive.
     *
     * @return An {@link Integer} array with the edge sizes: {@code {left, top, right, bottom}}
     */
    public int[] getFrameSize()
    {
        return Engine.waitReturnTask(() -> {
            try (MemoryStack stack = MemoryStack.stackPush())
            {
                IntBuffer left   = stack.callocInt(1);
                IntBuffer top    = stack.callocInt(1);
                IntBuffer right  = stack.callocInt(1);
                IntBuffer bottom = stack.callocInt(1);
                
                glfwGetWindowFrameSize(this.handle, left, top, right, bottom);
                
                return new int[] {left.get(), top.get(), right.get(), bottom.get()};
            }
        });
    }
    
    /**
     * @return A read-only framebuffer view transformation matrix for this window.
     */
    public Matrix4dc viewMatrix()
    {
        return this.viewMatrix;
    }
    
    /**
     * @return Retrieves if the window is in windowed mode.
     */
    public boolean windowed()
    {
        return this.windowed;
    }
    
    /**
     * Sets the window to into windowed mode or fullscreen mode.
     * <p>
     * If windowed is set to {@code true}, then the window will be set to
     * fullscreen in the current monitor that it is in.
     * <p>
     * If windowed is set to {@code false}, then the window will be set to
     * windowed in the current monitor and placed in the center of the it and
     * the window will be resized to the previously set size.
     * <p>
     * If you only wish to update the resolution of a full screen window or the
     * size of a windowed mode window, see {@link #size(Vector2ic)}.
     * <p>
     * When a window transitions from full screen to windowed mode, this
     * function restores any previous window settings such as whether it is
     * decorated, floating, resizable, has size or aspect ratio limits, etc.
     *
     * @param windowed The new windowed mode state.
     */
    public void windowed(boolean windowed)
    {
        Engine.runTask(() -> {
            long monitor = (this.windowed = windowed) ? 0L : this.monitor.handle;
            
            int x = ((this.monitor.primaryVideoMode.width - this.size.x) >> 1) + this.monitor.x();
            int y = ((this.monitor.primaryVideoMode.height - this.size.y) >> 1) + this.monitor.y();
            
            glfwSetWindowMonitor(this.handle, monitor, x, y, this.size.x, this.size.y, this.refreshRate);
        });
    }
    
    /**
     * @return Retrieves the refresh rate of the window, or {@link org.lwjgl.glfw.GLFW#GLFW_DONT_CARE DONT_CARE}
     */
    public int refreshRate()
    {
        return this.refreshRate;
    }
    
    /**
     * Sets the refresh rate of the window.
     *
     * @param refreshRate The new refresh rate.
     */
    public void refreshRate(int refreshRate)
    {
        Engine.runTask(() -> {
            long monitor = this.windowed ? 0L : this.monitor.handle;
            glfwSetWindowMonitor(this.handle, monitor, this.pos.x, this.pos.y, this.size.x, this.size.y, this.refreshRate = refreshRate);
        });
    }
    
    // -------------------- Callback Related Things -------------------- //
    
    /**
     * @return Retrieves the vsync status for the current OpenGL or OpenGL ES context
     */
    public boolean vsync()
    {
        return this.vsync;
    }
    
    /**
     * Sets the vsync status for the current OpenGL or OpenGL ES context, i.e.
     * the number of screen updates to wait from the time
     * {@link org.lwjgl.glfw.GLFW#glfwSwapBuffers SwapBuffers} was called
     * before swapping the buffers and returning.
     *
     * @param vsync the new vsync status
     */
    public void vsync(boolean vsync)
    {
        this._vsync = vsync;
    }
    
    /**
     * Retrieves if the window has input focus.
     *
     * @return if the window has input focus
     */
    public boolean focused()
    {
        return this.focused;
    }
    
    /**
     * Brings the window to front and sets input focus. The window should
     * already be visible and not iconified.
     * <p>
     * By default, both windowed and full screen mode windows are focused when
     * initially created.
     * <p>
     * Also by default, windowed mode windows are focused when shown with
     * {@link #show}.
     * <p>
     * <b>Do not use this function</b> to steal focus from other applications
     * unless you are certain that is what the user wants. Focus stealing can
     * be extremely disruptive.
     * <p>
     * For a less disruptive way of getting the user's attention, see
     * {@link #requestFocus}.
     */
    public void focus()
    {
        Engine.runTask(() -> glfwFocusWindow(this.handle));
    }
    
    /**
     * Requests user attention to the window.
     * <p>
     * This function requests user attention to the window. On platforms where
     * this is not supported, attention is requested to the application as a
     * whole.
     * <p>
     * Once the user has given attention, usually by focusing the window or
     * application, the system will end the request automatically.
     */
    public void requestFocus()
    {
        Engine.runTask(() -> glfwRequestWindowAttention(this.handle));
    }
    
    /**
     * @return Retrieves whether the window is iconified, whether by the user or with {@link #iconify}.
     */
    public boolean iconified()
    {
        return this.iconified;
    }
    
    /**
     * Iconifies (minimizes) the window if it was previously restored. If the
     * window is already iconified, this function does nothing.
     * <p>
     * If the window is a full screen window, the original monitor resolution
     * is restored until the window is restored.
     */
    public void iconify()
    {
        Engine.runTask(() -> glfwIconifyWindow(this.handle));
    }
    
    /**
     * @return Retrieves whether the window is maximized, whether by the user or {@link #maximize}.
     */
    public boolean maximized()
    {
        return this.maximized;
    }
    
    /**
     * Maximizes the window if it was previously not maximized. If the window
     * is already maximized, this function does nothing.
     * <p>
     * If the window is a full screen window, this function does nothing.
     */
    public void maximize()
    {
        Engine.runTask(() -> glfwMaximizeWindow(this.handle));
    }
    
    /**
     * Retrieves the position, in screen coordinates, of the upper-left corner
     * of the content area of the window.
     *
     * @return The position of the upper-left corner of the content area
     */
    public Vector2ic pos()
    {
        return this.pos;
    }
    
    /**
     * Retrieves the x-coordinate of the position, in screen coordinates, of
     * the upper-left corner of the content area of the window.
     *
     * @return The x-coordinate of the upper-left corner of the content area
     */
    public int x()
    {
        return this.pos.x;
    }
    
    /**
     * Retrieves the y-coordinate of the position, in screen coordinates, of
     * the upper-left corner of the content area of the window.
     *
     * @return The y-coordinate of the upper-left corner of the content area
     */
    public int y()
    {
        return this.pos.y;
    }
    
    /**
     * Sets the position, in screen coordinates, of the upper-left corner of
     * the content area of the windowed mode window. If the window is a full
     * screen window, this function does nothing.
     *
     * <p><b>Do not use this function</b> to move an already visible window
     * unless you have very good reasons for doing so, as it will confuse and
     * annoy the user.</p>
     *
     * <p>The window manager may put limits on what positions are allowed. GLFW
     * cannot and should not override these limits.</p>
     *
     * @param x The x-coordinate of the upper-left corner of the content area.
     * @param y The y-coordinate of the upper-left corner of the content area.
     */
    public void pos(int x, int y)
    {
        Engine.waitRunTask(() -> glfwSetWindowPos(this.handle, x, y));
    }
    
    /**
     * Sets the position, in screen coordinates, of the upper-left corner of
     * the content area of the windowed mode window. If the window is a full
     * screen window, this function does nothing.
     *
     * <p><b>Do not use this function</b> to move an already visible window
     * unless you have very good reasons for doing so, as it will confuse and
     * annoy the user.</p>
     *
     * <p>The window manager may put limits on what positions are allowed. GLFW
     * cannot and should not override these limits.</p>
     *
     * @param pos The position of the upper-left corner of the content area.
     */
    public void pos(Vector2ic pos)
    {
        pos(pos.x(), pos.y());
    }
    
    /**
     * Sets the position, in screen coordinates, of the upper-left corner of
     * the content area of the windowed mode window. If the window is a full
     * screen window, this function does nothing.
     *
     * <p><b>Do not use this function</b> to move an already visible window
     * unless you have very good reasons for doing so, as it will confuse and
     * annoy the user.</p>
     *
     * <p>The window manager may put limits on what positions are allowed. GLFW
     * cannot and should not override these limits.</p>
     *
     * @param pos The position of the upper-left corner of the content area.
     */
    public void pos(Vector2fc pos)
    {
        pos((int) pos.x(), (int) pos.y());
    }
    
    /**
     * Sets the position, in screen coordinates, of the upper-left corner of
     * the content area of the windowed mode window. If the window is a full
     * screen window, this function does nothing.
     *
     * <p><b>Do not use this function</b> to move an already visible window
     * unless you have very good reasons for doing so, as it will confuse and
     * annoy the user.</p>
     *
     * <p>The window manager may put limits on what positions are allowed. GLFW
     * cannot and should not override these limits.</p>
     *
     * @param pos The position of the upper-left corner of the content area.
     */
    public void pos(Vector2dc pos)
    {
        pos((int) pos.x(), (int) pos.y());
    }
    
    /**
     * Retrieves the size, in screen coordinates, of the content area of the
     * window. If you wish to retrieve the size of the framebuffer of the
     * window in pixels, see {@link #framebufferSize framebufferSize}.
     *
     * @return The size, in screen coordinates, of the content area.
     */
    public Vector2ic size()
    {
        return this.size;
    }
    
    /**
     * Retrieves the width, in screen coordinates, of the content area of the
     * window. If you wish to retrieve the size of the framebuffer of the
     * window in pixels, see {@link #framebufferSize framebufferSize}.
     *
     * @return The width, in screen coordinates, of the content area.
     */
    public int width()
    {
        return this.size.x;
    }
    
    /**
     * Retrieves the height, in screen coordinates, of the content area of the
     * window. If you wish to retrieve the size of the framebuffer of the
     * window in pixels, see {@link #framebufferSize framebufferSize}.
     *
     * @return The height, in screen coordinates, of the content area.
     */
    public int height()
    {
        return this.size.y;
    }
    
    /**
     * Sets the size, in pixels, of the content area of the window.
     * <p>
     * For full screen windows, this function updates the resolution of its
     * desired video mode and switches to the video mode closest to it, without
     * affecting the window's context. As the context is unaffected, the bit
     * depths of the framebuffer remain unchanged.
     * <p>
     * The window manager may put limits on what sizes are allowed. GLFW cannot
     * and should not override these limits.
     *
     * @param width  The desired width, in screen coordinates, of the window content area
     * @param height The desired height, in screen coordinates, of the window content area
     */
    public void size(int width, int height)
    {
        Engine.waitRunTask(() -> glfwSetWindowSize(this.handle, width, height));
    }
    
    /**
     * Sets the size, in pixels, of the content area of the window.
     * <p>
     * For full screen windows, this function updates the resolution of its
     * desired video mode and switches to the video mode closest to it, without
     * affecting the window's context. As the context is unaffected, the bit
     * depths of the framebuffer remain unchanged.
     * <p>
     * The window manager may put limits on what sizes are allowed. GLFW
     * cannot and should not override these limits.
     *
     * @param size The desired size, in screen coordinates, of the window content area
     */
    public void size(Vector2ic size)
    {
        size(size.x(), size.y());
    }
    
    /**
     * Sets the size, in pixels, of the content area of the window.
     * <p>
     * For full screen windows, this function updates the resolution of its
     * desired video mode and switches to the video mode closest to it, without
     * affecting the window's context. As the context is unaffected, the bit
     * depths of the framebuffer remain unchanged.
     * <p>
     * The window manager may put limits on what sizes are allowed. GLFW
     * cannot and should not override these limits.
     *
     * @param size The desired size, in screen coordinates, of the window content area
     */
    public void size(Vector2fc size)
    {
        size((int) size.x(), (int) size.y());
    }
    
    /**
     * Sets the size, in pixels, of the content area of the window.
     * <p>
     * For full screen windows, this function updates the resolution of its
     * desired video mode and switches to the video mode closest to it, without
     * affecting the window's context. As the context is unaffected, the bit
     * depths of the framebuffer remain unchanged.
     * <p>
     * The window manager may put limits on what sizes are allowed. GLFW
     * cannot and should not override these limits.
     *
     * @param size The desired size, in screen coordinates, of the window content area
     */
    public void size(Vector2dc size)
    {
        size((int) size.x(), (int) size.y());
    }
    
    /**
     * Retrieves the content scale for the window.
     * <p>
     * This function retrieves the content scale for the window. The content
     * scale is the ratio between the current DPI and the platform's default
     * DPI. This is especially important for text and any UI elements. If the
     * pixel dimensions of your UI scaled by this look appropriate on your
     * machine then it should appear at a reasonable size on other machines
     * regardless of their DPI and scaling settings. This relies on the system
     * DPI and scaling settings being somewhat correct.
     * <p>
     * On systems where each monitor can have its own content scale, the window
     * content scale will depend on which monitor the system considers the
     * window to be on.
     *
     * @return the content scale for the window.
     */
    public Vector2dc contentScale()
    {
        return this.scale;
    }
    
    /**
     * Retrieves the horizontal content scale for the window.
     * <p>
     * This function retrieves the content scale for the window. The content
     * scale is the ratio between the current DPI and the platform's default
     * DPI. This is especially important for text and any UI elements. If the
     * pixel dimensions of your UI scaled by this look appropriate on your
     * machine then it should appear at a reasonable size on other machines
     * regardless of their DPI and scaling settings. This relies on the system
     * DPI and scaling settings being somewhat correct.
     * <p>
     * On systems where each monitor can have its own content scale, the window
     * content scale will depend on which monitor the system considers the
     * window to be on.
     *
     * @return the horizontal content scale for the window.
     */
    public double contentScaleX()
    {
        return this.scale.x;
    }
    
    /**
     * Retrieves the vertical content scale for the window.
     * <p>
     * This function retrieves the content scale for the window. The content
     * scale is the ratio between the current DPI and the platform's default
     * DPI. This is especially important for text and any UI elements. If the
     * pixel dimensions of your UI scaled by this look appropriate on your
     * machine then it should appear at a reasonable size on other machines
     * regardless of their DPI and scaling settings. This relies on the system
     * DPI and scaling settings being somewhat correct.
     * <p>
     * On systems where each monitor can have its own content scale, the window
     * content scale will depend on which monitor the system considers the
     * window to be on.
     *
     * @return the vertical content scale for the window.
     */
    public double contentScaleY()
    {
        return this.scale.y;
    }
    
    /**
     * Retrieves the size, in pixels, of the framebuffer of the specified
     * window. If you wish to retrieve the size of the window in screen
     * coordinates, see {@link #size}.
     *
     * @return The size, in pixels, of the framebuffer
     */
    public Vector2ic framebufferSize()
    {
        return this.fbSize;
    }
    
    /**
     * Retrieves the width, in pixels, of the framebuffer of the specified
     * window. If you wish to retrieve the size of the window in screen
     * coordinates, see {@link #size}.
     *
     * @return The width, in pixels, of the framebuffer
     */
    public int framebufferWidth()
    {
        return this.fbSize.x;
    }
    
    /**
     * Retrieves the height, in pixels, of the framebuffer of the specified
     * window. If you wish to retrieve the size of the window in screen
     * coordinates, see {@link #size}.
     *
     * @return The height, in pixels, of the framebuffer
     */
    public int framebufferHeight()
    {
        return this.fbSize.y;
    }
    
    // -------------------- GLFW Methods -------------------- //
    
    public void close()
    {
        this._close = true;
    }
    
    public void makeCurrent()
    {
        glfwMakeContextCurrent(this.handle);
        org.lwjgl.opengl.GL.createCapabilities();
    }
    
    public void unmakeCurrent()
    {
        org.lwjgl.opengl.GL.setCapabilities(null);
        glfwMakeContextCurrent(0L);
    }
    
    public void swap()
    {
        glFinish();
        glfwSwapBuffers(this.handle);
    }
    
    public void destroy()
    {
        unmakeCurrent();
        Engine.runTask(() -> {
            glfwFreeCallbacks(this.handle);
            glfwDestroyWindow(this.handle);
        });
    }
    
    /**
     * This method is called by the window it is attached to. This is where
     * events should be posted to when something has changed.
     *
     * @param time   The system time in nanoseconds.
     * @param deltaT The time in nanoseconds since the last time this method was called.
     */
    protected void postEvents(long time, long deltaT)
    {
        boolean updateMonitor = false;
        
        if (this.close != this._close)
        {
            this.close = this._close;
            // GLFW.EVENT_BUS.post(EventWindowClosed.create(this)); // TODO
            Engine.stop();
        }
        
        if (this.vsync != this._vsync)
        {
            this.vsync = this._vsync;
            glfwSwapInterval(this.vsync ? 1 : 0);
            // GLFW.EVENT_BUS.post(EventWindowVsyncChanged.create(this, this.vsync)); // TODO
        }
        
        if (this.focused != this._focused)
        {
            this.focused = this._focused;
            // GLFW.EVENT_BUS.post(EventWindowFocused.create(this, this.focused)); // TODO
        }
        
        if (this.iconified != this._iconified)
        {
            this.iconified = this._iconified;
            // GLFW.EVENT_BUS.post(EventWindowIconified.create(this, this.iconified)); // TODO
        }
        
        if (this.maximized != this._maximized)
        {
            this.maximized = this._maximized;
            // GLFW.EVENT_BUS.post(EventWindowMaximized.create(this, this.maximized)); // TODO
        }
        
        if (this.pos.x != this._pos.x || this.pos.y != this._pos.y)
        {
            this._pos.sub(this.pos, this.deltaI);
            this.pos.set(this._pos);
            // GLFW.EVENT_BUS.post(EventWindowMoved.create(this, this.pos, this.deltaI)); // TODO
            
            updateMonitor = true;
        }
        
        if (this.size.x != this._size.x || this.size.y != this._size.y)
        {
            this._size.sub(this.size, this.deltaI);
            this.size.set(this._size);
            // GLFW.EVENT_BUS.post(EventWindowResized.create(this, this.size, this.deltaI)); // TODO
            
            updateMonitor = true;
        }
        
        if (Double.compare(this.scale.x, this._scale.x) != 0 || Double.compare(this.scale.y, this._scale.y) != 0)
        {
            this._scale.sub(this.scale, this.deltaD);
            this.scale.set(this._scale);
            // GLFW.EVENT_BUS.post(EventWindowContentScaleChanged.create(this, this.scale, this.deltaD)); // TODO
        }
        
        if (this.fbSize.x != this._fbSize.x || this.fbSize.y != this._fbSize.y)
        {
            this._fbSize.sub(this.fbSize, this.deltaI);
            this.fbSize.set(this._fbSize);
            // GLFW.EVENT_BUS.post(EventWindowFramebufferResized.create(this, this.fbSize, this.deltaI)); // TODO
            
            this.viewMatrix.setOrtho(0, this.fbSize.x, this.fbSize.y, 0, -1F, 1F);
        }
        
        if (this._refresh)
        {
            this._refresh = false;
            // GLFW.EVENT_BUS.post(EventWindowRefreshed.create(this)); // TODO
        }
        
        if (this._dropped != null)
        {
            Path[] paths = new Path[this._dropped.length];
            for (int i = 0; i < this._dropped.length; i++) paths[i] = Paths.get(this._dropped[i]);
            this._dropped = null;
            // GLFW.EVENT_BUS.post(EventWindowDropped.create(this, paths)); // TODO
        }
        
        if (updateMonitor)
        {
            Monitor prevMonitor = this.monitor;
            
            int overlap, maxOverlap = 0;
            for (Monitor monitor : Engine.monitors.values())
            {
                Monitor.VideoMode current = monitor.videoMode();
                
                int mx = monitor.x(), my = monitor.y();
                int mw = mx + current.width, mh = my + current.height;
                
                int wx = x(), wy = y();
                int ww = wx + width(), wh = wy + height();
                
                overlap = (Math.min(ww, mw) - Math.max(wx, mx)) * (Math.min(wh, mh) - Math.max(wy, my));
                
                if (overlap > maxOverlap)
                {
                    maxOverlap   = overlap;
                    this.monitor = monitor;
                }
            }
            // if (this.monitor != prevMonitor) GLFW.EVENT_BUS.post(EventWindowMonitorChanged.create(this, prevMonitor, this.monitor)); // TODO
        }
    }
}
