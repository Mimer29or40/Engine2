package engine.render;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLUtil;
import org.lwjgl.system.Callback;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.Platform;

import java.nio.FloatBuffer;
import java.util.Objects;

import static lwjgl.glfw.GLFWUtil.glfwInvoke;
import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * OpenGL Demo Application
 */
public abstract class OpenGLDemo
{
    public long window;
    public int  width  = 800;
    public int  height = 600;
    
    public float contentScaleX, contentScaleY;
    
    private Callback debugProc;
    
    public OpenGLDemo(int width, int height)
    {
        this.width  = width;
        this.height = height;
    }
    
    public OpenGLDemo() {}
    
    public void run(String title)
    {
        try
        {
            init(title);
            
            loop();
        }
        finally
        {
            try
            {
                destroy();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
    
    protected void init(String title)
    {
        GLFWErrorCallback.createPrint().set();
        if (!glfwInit()) throw new IllegalStateException("Unable to initialize GLFW");
        
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_OPENGL_DEBUG_CONTEXT, GLFW_TRUE);
        
        long monitor = glfwGetPrimaryMonitor();
        
        int framebufferW;
        int framebufferH;
        try (MemoryStack s = stackPush())
        {
            FloatBuffer px = s.mallocFloat(1);
            FloatBuffer py = s.mallocFloat(1);
            
            glfwGetMonitorContentScale(monitor, px, py);
            
            this.contentScaleX = px.get(0);
            this.contentScaleY = py.get(0);
            
            if (Platform.get() == Platform.MACOSX)
            {
                framebufferW = this.width;
                framebufferH = this.height;
            }
            else
            {
                framebufferW = Math.round(this.width * this.contentScaleX);
                framebufferH = Math.round(this.height * this.contentScaleY);
            }
        }
        
        this.window = glfwCreateWindow(framebufferW, framebufferH, title, NULL, NULL);
        if (this.window == NULL) throw new RuntimeException("Failed to create the GLFW window");
        
        glfwSetFramebufferSizeCallback(window, this::framebufferSizeCallback);
        
        glfwSetWindowCloseCallback(this.window, this::windowCloseCallback);
        glfwSetWindowPosCallback(this.window, this::windowPosCallback);
        glfwSetWindowSizeCallback(this.window, this::windowSizeCallback);
        glfwSetWindowFocusCallback(this.window, this::windowFocusCallback);
        glfwSetCursorEnterCallback(this.window, this::cursorEnterCallback);
        glfwSetCursorPosCallback(this.window, this::cursorPosCallback);
        glfwSetScrollCallback(this.window, this::scrollCallback);
        glfwSetMouseButtonCallback(this.window, this::mouseButtonCallback);
        glfwSetKeyCallback(this.window, this::keyCallback);
        glfwSetCharCallback(this.window, this::charCallback);
        
        // Center window
        GLFWVidMode vidMode = Objects.requireNonNull(glfwGetVideoMode(monitor));
        
        glfwSetWindowPos(this.window, (vidMode.width() - framebufferW) / 2, (vidMode.height() - framebufferH) / 2);
        
        // Create context
        glfwMakeContextCurrent(this.window);
        GL.createCapabilities();
        this.debugProc = GLUtil.setupDebugMessageCallback();
        
        glfwSwapInterval(1);
        glfwShowWindow(this.window);
        
        glfwInvoke(this.window, this::windowSizeCallback, this::framebufferSizeCallback);
    }
    
    protected abstract void loop();
    
    protected void destroy()
    {
        if (this.debugProc != null) this.debugProc.free();
        
        glfwFreeCallbacks(this.window);
        glfwDestroyWindow(this.window);
        glfwTerminate();
        Objects.requireNonNull(glfwSetErrorCallback(null)).free();
    }
    
    protected void framebufferSizeCallback(long window, int width, int height)
    {
        glViewport(0, 0, width, height);
    }
    
    protected void windowCloseCallback(long window)
    {
    
    }
    
    protected void windowPosCallback(long window, int x, int y)
    {
    
    }
    
    protected void windowSizeCallback(long window, int width, int height)
    {
        if (Platform.get() != Platform.MACOSX)
        {
            width /= this.contentScaleX;
            height /= this.contentScaleY;
        }
        
        this.width  = width;
        this.height = height;
    }
    
    protected void windowFocusCallback(long window, boolean focused)
    {
    
    }
    
    protected void cursorEnterCallback(long window, boolean entered)
    {
    
    }
    
    protected void cursorPosCallback(long window, double x, double y)
    {
    
    }
    
    protected void scrollCallback(long window, double xOffset, double yOffset)
    {
    
    }
    
    protected void mouseButtonCallback(long window, int button, int action, int mod)
    {
    
    }
    
    protected void keyCallback(long window, int key, int scancode, int action, int mod)
    {
    
    }
    
    protected void charCallback(long window, int codepoint)
    {
    
    }
}
