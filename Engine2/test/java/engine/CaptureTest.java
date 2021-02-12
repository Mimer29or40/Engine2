package engine;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;

import java.nio.FloatBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL46.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class CaptureTest extends Engine
{
    // We need to strongly reference callback instances.
    private GLFWErrorCallback     errorCallback;
    private GLFWKeyCallback       keyCallback;
    private GLFWCursorPosCallback cursorPosCallback;
    
    // The window handle
    private long window;
    
    // Mouse positions
    private int mouseX, mouseY;
    private int mouseDX, mouseDY;
    
    public void run()
    {
        // System.out.println("Hello LWJGL " + LWJGL..getVersion() + "!");
        
        try
        {
            init();
            loop();
            
            // Release window and window callbacks
            glfwDestroyWindow(window);
            keyCallback.free();
        }
        finally
        {
            // Terminate GLFW and release the GLFWerrorfun
            glfwTerminate();
            glfwSetErrorCallback(null);
        }
    }
    
    private void init()
    {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();
        
        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if (!glfwInit()) throw new IllegalStateException("Unable to initialize GLFW");
        
        // Configure our window
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GL_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GL_TRUE); // the window will be resizable
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);
        
        
        int WIDTH  = 800;
        int HEIGHT = 600;
        
        // Create the window
        window = glfwCreateWindow(WIDTH, HEIGHT, "Hello World!", NULL, NULL);
        if (window == NULL)
        { throw new RuntimeException("Failed to create the GLFW window"); }
        
        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(window, keyCallback = new GLFWKeyCallback()
        {
            @Override
            public void invoke(long window, int key, int scancode, int action, int mods)
            {
                if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
                {
                    glfwSetWindowShouldClose(window, true); // We will detect this in our rendering loop
                }
            }
        });
        
        // Initialize all mouse values as 0
        mouseX = mouseY = mouseDX = mouseDY = 0;
        glfwSetCursorPosCallback(window, cursorPosCallback = new GLFWCursorPosCallback()
        {
            
            @Override
            public void invoke(long window, double xpos, double ypos)
            {
                // Add delta of x and y mouse coordinates
                mouseDX += (int) xpos - mouseX;
                mouseDY += (int) xpos - mouseY;
                // Set new positions of x and y
                mouseX = (int) xpos;
                mouseY = (int) ypos;
            }
        });
        
        // Get the resolution of the primary monitor
        GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        // Center our window
        glfwSetWindowPos(window, (vidmode.width() - WIDTH) / 2, (vidmode.height() - HEIGHT) / 2);
        
        // Make the OpenGL context current
        glfwMakeContextCurrent(window);
        // Enable v-sync
        glfwSwapInterval(1);
        
        // Make the window visible
        glfwShowWindow(window);
    }
    
    public int getDX()
    {
        // Return mouse delta x and set delta x to 0
        return mouseDX | (mouseDX = 0);
    }
    
    public int getDY()
    {
        // Return mouse delta y and set delta y to 0
        return mouseDY | (mouseDY = 0);
    }
    
    private void loop()
    {
        // This line is critical for LWJGL's interoperation with GLFW's
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the ContextCapabilities instance and makes the OpenGL
        // bindings available for use.
        glfwMakeContextCurrent(window);
        GL.createCapabilities();
        
        
        // Create a FloatBuffer to hold our vertex data
        FloatBuffer vertices = BufferUtils.createFloatBuffer(9);
        // Add vertices of the triangle
        vertices.put(new float[]
                             {
                                     0.0f, 0.5f, 0.0f,
                                     0.5f, -0.5f, 0.0f,
                                     -0.5f, -0.5f, 0.0f
                             });
        // Rewind the vertices
        vertices.rewind();
        
        
        int vbo = glGenBuffers();
        int vao = glGenVertexArrays();
        
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
        glBindVertexArray(vao);
        
        glEnableVertexAttribArray(0);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
        
        final String vertex_shader =
                "#version 410\n" +
                "in vec3 vp;\n" +
                "void main () {\n" +
                "  gl_Position = vec4 (vp, 1.0);\n" +
                "}";
        
        final String frag_shader =
                "#version 400\n" +
                "out vec4 frag_colour;" +
                "void main () {" +
                "  frag_colour = vec4 (0.5, 0.0, 0.5, 1.0);" +
                "}";
        
        int shader_programme = glCreateProgram();
        
        
        int vertexShaderID = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexShaderID, vertex_shader);
        glCompileShader(vertexShaderID);
        
        if (glGetShaderi(vertexShaderID, GL_COMPILE_STATUS) == 0)
        {
            System.err.println(glGetShaderInfoLog(vertexShaderID, 1024));
            System.exit(1);
        }
        
        glAttachShader(shader_programme, vertexShaderID);
        
        int fragmentShaderID = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentShaderID, frag_shader);
        glCompileShader(fragmentShaderID);
        
        if (glGetShaderi(fragmentShaderID, GL_COMPILE_STATUS) == 0)
        {
            System.err.println(glGetShaderInfoLog(fragmentShaderID, 1024));
            System.exit(1);
        }
        
        glAttachShader(shader_programme, fragmentShaderID);
        
        glLinkProgram(shader_programme);
        
        if (glGetProgrami(shader_programme, GL_LINK_STATUS) == 0)
        {
            System.err.println(glGetProgramInfoLog(shader_programme, 1024));
            System.exit(1);
        }
        
        glValidateProgram(shader_programme);
        
        if (glGetProgrami(shader_programme, GL_VALIDATE_STATUS) == 0)
        {
            System.err.println(glGetProgramInfoLog(shader_programme, 1024));
            System.exit(1);
        }
        
        while (!glfwWindowShouldClose(window))
        {
            
            // Print the mouse delta x and delta Y
            System.out.println("Delta X = " + getDX() + " Delta Y = " + getDY());
            
            // wipe the drawing surface clear
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            glUseProgram(shader_programme);
            glBindVertexArray(vao);
            // draw points 0-3 from the currently bound VAO with current in-use shader
            glDrawArrays(GL_TRIANGLES, 0, 3);
            // update other events like input handling
            glfwPollEvents();
            // put the stuff we've been drawing onto the display
            glfwSwapBuffers(window);
            
        }
    }
    
    public static void main(String[] args)
    {
        new CaptureTest().run();
    }
    
}
