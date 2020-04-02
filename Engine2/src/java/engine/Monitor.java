package engine;

import engine.util.ITuple;
import engine.util.TupleI;
import org.joml.Vector2f;
import org.joml.Vector2fc;
import org.joml.Vector2i;
import org.joml.Vector2ic;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Objects;

import static org.lwjgl.glfw.GLFW.*;

@SuppressWarnings("unused")
public class Monitor
{
    private final long   handle;
    private final int    index;
    private final String name;
    
    private final TupleI bitTuple;
    private final int    refreshRate;
    
    private final Vector2i pos  = new Vector2i();
    private final Vector2i size = new Vector2i();
    
    private final Vector2i workSpacePos  = new Vector2i();
    private final Vector2i workSpaceSize = new Vector2i();
    
    private final Vector2i actualSize = new Vector2i();
    
    private final Vector2f scale = new Vector2f();
    
    public Monitor(long handle, int index)
    {
        this.handle = handle;
        this.index  = index;
        this.name   = glfwGetMonitorName(this.handle);
        
        GLFWVidMode videoMode = Objects.requireNonNull(glfwGetVideoMode(this.handle), "Monitor not found!");
        
        this.bitTuple    = new TupleI(videoMode.redBits(), videoMode.greenBits(), videoMode.blueBits());
        this.refreshRate = videoMode.refreshRate();
        
        try (MemoryStack stack = MemoryStack.stackPush())
        {
            IntBuffer   x  = stack.mallocInt(1);
            IntBuffer   y  = stack.mallocInt(1);
            IntBuffer   w  = stack.mallocInt(1);
            IntBuffer   h  = stack.mallocInt(1);
            FloatBuffer sx = stack.mallocFloat(1);
            FloatBuffer sy = stack.mallocFloat(1);
            
            glfwGetMonitorPos(this.handle, x, y);
            this.pos.set(x.get(0), y.get(0));
            this.size.set(videoMode.width(), videoMode.height());
            
            glfwGetMonitorWorkarea(this.handle, x, y, w, h);
            this.workSpacePos.set(x.get(0), y.get(0));
            this.workSpaceSize.set(w.get(0), h.get(0));
            
            glfwGetMonitorPhysicalSize(this.handle, w, h);
            this.actualSize.set(w.get(0), h.get(0));
            
            glfwGetMonitorContentScale(this.handle, sx, sy);
            this.scale.set(sx.get(0), sy.get(0));
        }
    }
    
    /**
     * @return The name of the monitor.
     */
    public String name()
    {
        return this.name;
    }
    
    /**
     * @return If this monitor is the primary monitor.
     */
    public boolean isPrimary()
    {
        return this.handle == glfwGetPrimaryMonitor();
    }
    
    /**
     * @return The number of red, green, and blue bits.
     */
    public ITuple<Integer, Integer, Integer> bitTuple()
    {
        return this.bitTuple;
    }
    
    /**
     * @return The number of red bits.
     */
    public int redBits()
    {
        return this.bitTuple.a;
    }
    
    /**
     * @return The number of green bits.
     */
    public int greenBits()
    {
        return this.bitTuple.b;
    }
    
    /**
     * @return The number of blue bits.
     */
    public int blueBits()
    {
        return this.bitTuple.c;
    }
    
    /**
     * @return The number of times a second the monitor will refresh.
     */
    public int getRefreshRate()
    {
        return this.refreshRate;
    }
    
    /**
     * @return The virtual position of the monitor relative to the main monitor.
     */
    public Vector2ic pos()
    {
        return this.pos;
    }
    
    /**
     * @return The virtual x position of the monitor relative to the main monitor.
     */
    public int x()
    {
        return this.pos.x;
    }
    
    /**
     * @return The virtual y position of the monitor relative to the main monitor.
     */
    public int y()
    {
        return this.pos.y;
    }
    
    /**
     * @return The size in screen coordinates of the monitor.
     */
    public Vector2ic size()
    {
        return this.size;
    }
    
    /**
     * @return The width in screen coordinates of the monitor.
     */
    public int width()
    {
        return this.size.x;
    }
    
    /**
     * @return The height in screen coordinates of the monitor.
     */
    public int height()
    {
        return this.size.y;
    }
    
    /**
     * @return The position of the monitor that is not occluded by the operation system task bar. If none is present, then this will be the same as {@link #pos()}.
     */
    public Vector2ic workSpacePos()
    {
        return this.workSpacePos;
    }
    
    /**
     * @return The x position of the monitor that is not occluded by the operation system task bar. If none is present, then this will be the same as {@link #x()}.
     */
    public int workSpaceX()
    {
        return this.workSpacePos.x;
    }
    
    /**
     * @return The y position of the monitor that is not occluded by the operation system task bar. If none is present, then this will be the same as {@link #y()}.
     */
    public int workSpaceY()
    {
        return this.workSpacePos.y;
    }
    
    /**
     * @return The size of the monitor that is not occluded by the operation system task bar. If none is present, then this will be the same as {@link #size()}.
     */
    public Vector2ic workSpaceSize()
    {
        return this.workSpaceSize;
    }
    
    /**
     * @return The width of the monitor that is not occluded by the operation system task bar. If none is present, then this will be the same as {@link #width()}.
     */
    public int workSpaceWidth()
    {
        return this.workSpaceSize.x;
    }
    
    /**
     * @return The height of the monitor that is not occluded by the operation system task bar. If none is present, then this will be the same as {@link #height()}.
     */
    public int workSpaceHeight()
    {
        return this.workSpaceSize.y;
    }
    
    /**
     * @return The size of the monitor in millimeters.
     */
    public Vector2ic actualSize()
    {
        return this.actualSize;
    }
    
    /**
     * @return The width of the monitor in millimeters.
     */
    public int actualWidth()
    {
        return this.actualSize.x;
    }
    
    /**
     * @return The height of the monitor in millimeters.
     */
    public int actualHeight()
    {
        return this.actualSize.y;
    }
    
    /**
     * @return The scale of the monitor.
     */
    public Vector2fc getScale()
    {
        return this.scale;
    }
    
    /**
     * @return The horizontal scale of the monitor.
     */
    public float scaleX()
    {
        return this.scale.x;
    }
    
    /**
     * @return The vertical scale of the monitor.
     */
    public float scaleY()
    {
        return this.scale.y;
    }
    
    /**
     * Calculates the percentage if the window that is in the monitor.
     *
     * @param window The window.
     * @return The percentage.
     */
    public double isWindowIn(Window window)
    {
        int xOverlap = Math.max(0, Math.min(window.x() + window.width(), this.pos.x + this.size.x) - Math.max(window.x(), this.pos.x));
        int yOverlap = Math.max(0, Math.min(window.y() + window.height(), this.pos.y + this.size.y) - Math.max(window.y(), this.pos.y));
        
        return (double) (xOverlap * yOverlap) / (window.width() * window.height());
    }
}
