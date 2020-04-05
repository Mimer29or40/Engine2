package engine.render;

import org.joml.Matrix4f;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL43.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Framebuffer
{
    static final int SCR_WIDTH  = 1200;
    static final int SCR_HEIGHT = 800;
    
    static final Matrix4f model      = new Matrix4f();
    static final Matrix4f view       = new Matrix4f();
    static final Matrix4f projection = new Matrix4f();
    
    static double  lastX      = (float) SCR_WIDTH / 2F;
    static double  lastY      = (float) SCR_HEIGHT / 2F;
    static boolean firstMouse = true;
    
    static double deltaTime = 0;
    static double lastFrame = 0;
    
    public static void main(String[] args)
    {
        // glfw: initialize and configure
        // ------------------------------
        glfwInit();
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);
        
        // glfw window creation
        // --------------------
        long window = glfwCreateWindow(SCR_WIDTH, SCR_HEIGHT, "LearnOpenGL", NULL, NULL);
        if (window == NULL)
        {
            System.out.println("Failed to create GLFW window");
            glfwTerminate();
            return;
        }
        glfwMakeContextCurrent(window);
        glfwSwapInterval(1);
        
        glfwSetFramebufferSizeCallback(window, Framebuffer::framebuffer_size_callback);
        glfwSetCursorPosCallback(window, Framebuffer::mouse_callback);
        glfwSetScrollCallback(window, Framebuffer::scroll_callback);
        
        // tell GLFW to capture our mouse
        // glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
        
        // glad: load all OpenGL function pointers
        // ---------------------------------------
        GL.createCapabilities();
        
        // configure global opengl state
        // -----------------------------
        glEnable(GL_DEPTH_TEST);
        
        // build and compile shaders
        // -------------------------
        Shader shader       = new Shader().loadVertexFile("shaders/5.1.framebuffers.vert").loadFragmentFile("shaders/5.1.framebuffers.frag").validate();
        Shader screenShader = new Shader().loadVertexFile("shaders/5.1.framebuffers_screen.vert").loadFragmentFile("shaders/5.1.framebuffers_screen.frag").validate();
        
        // set up vertex data (and buffer(s)) and configure vertex attributes
        // ------------------------------------------------------------------
        VertexArray cubeArray = new VertexArray().bind();
        cubeArray.add(new float[] {
                -0.5f, -0.5f, -0.5f, 0.0f, 0.0f,
                0.5f, -0.5f, -0.5f, 1.0f, 0.0f,
                0.5f, 0.5f, -0.5f, 1.0f, 1.0f,
                0.5f, 0.5f, -0.5f, 1.0f, 1.0f,
                -0.5f, 0.5f, -0.5f, 0.0f, 1.0f,
                -0.5f, -0.5f, -0.5f, 0.0f, 0.0f,
                -0.5f, -0.5f, 0.5f, 0.0f, 0.0f,
                0.5f, -0.5f, 0.5f, 1.0f, 0.0f,
                0.5f, 0.5f, 0.5f, 1.0f, 1.0f,
                0.5f, 0.5f, 0.5f, 1.0f, 1.0f,
                -0.5f, 0.5f, 0.5f, 0.0f, 1.0f,
                -0.5f, -0.5f, 0.5f, 0.0f, 0.0f,
                -0.5f, 0.5f, 0.5f, 1.0f, 0.0f,
                -0.5f, 0.5f, -0.5f, 1.0f, 1.0f,
                -0.5f, -0.5f, -0.5f, 0.0f, 1.0f,
                -0.5f, -0.5f, -0.5f, 0.0f, 1.0f,
                -0.5f, -0.5f, 0.5f, 0.0f, 0.0f,
                -0.5f, 0.5f, 0.5f, 1.0f, 0.0f,
                0.5f, 0.5f, 0.5f, 1.0f, 0.0f,
                0.5f, 0.5f, -0.5f, 1.0f, 1.0f,
                0.5f, -0.5f, -0.5f, 0.0f, 1.0f,
                0.5f, -0.5f, -0.5f, 0.0f, 1.0f,
                0.5f, -0.5f, 0.5f, 0.0f, 0.0f,
                0.5f, 0.5f, 0.5f, 1.0f, 0.0f,
                -0.5f, -0.5f, -0.5f, 0.0f, 1.0f,
                0.5f, -0.5f, -0.5f, 1.0f, 1.0f,
                0.5f, -0.5f, 0.5f, 1.0f, 0.0f,
                0.5f, -0.5f, 0.5f, 1.0f, 0.0f,
                -0.5f, -0.5f, 0.5f, 0.0f, 0.0f,
                -0.5f, -0.5f, -0.5f, 0.0f, 1.0f,
                -0.5f, 0.5f, -0.5f, 0.0f, 1.0f,
                0.5f, 0.5f, -0.5f, 1.0f, 1.0f,
                0.5f, 0.5f, 0.5f, 1.0f, 0.0f,
                0.5f, 0.5f, 0.5f, 1.0f, 0.0f,
                -0.5f, 0.5f, 0.5f, 0.0f, 0.0f,
                -0.5f, 0.5f, -0.5f, 0.0f, 1.0f
        }, GL_STATIC_DRAW, 3, 2).unbind();
        
        // plane VAO
        VertexArray planeArray = new VertexArray().bind();
        planeArray.add(new float[] {
                5.0f, -0.5f, 5.0f, 2.0f, 0.0f,
                -5.0f, -0.5f, 5.0f, 0.0f, 0.0f,
                -5.0f, -0.5f, -5.0f, 0.0f, 2.0f,
                5.0f, -0.5f, 5.0f, 2.0f, 0.0f,
                -5.0f, -0.5f, -5.0f, 0.0f, 2.0f,
                5.0f, -0.5f, -5.0f, 2.0f, 2.0f
        }, GL_STATIC_DRAW, 3, 2);
        planeArray.unbind();
        
        // screen quad VAO
        VertexArray quadArray = new VertexArray().bind();
        quadArray.add(new float[] {
                -1.0f, 1.0f, 0.0f, 1.0f,
                -1.0f, -1.0f, 0.0f, 0.0f,
                1.0f, -1.0f, 1.0f, 0.0f,
                1.0f, 1.0f, 1.0f, 1.0f
        }, GL_STATIC_DRAW, 2, 2);
        quadArray.addEBO(new int[] {0, 1, 2, 0, 2, 3}, GL_STATIC_DRAW);
        quadArray.unbind();
        
        // load textures
        // -------------
        Texture cubeTexture  = Texture.loadImage("textures/marble.jpg").wrapMode(GL_REPEAT, GL_REPEAT).bind().upload();
        Texture floorTexture = Texture.loadImage("textures/metal.png").wrapMode(GL_REPEAT, GL_REPEAT).bind().upload();
        
        // shader configuration
        // --------------------
        shader.bind();
        shader.setInt("texture1", 0);
        
        screenShader.bind();
        screenShader.setInt("screenTexture", 0);
        
        // framebuffer configuration
        // -------------------------
        int framebufferWidth  = SCR_WIDTH / 1;
        int framebufferHeight = SCR_HEIGHT / 1;
        int framebuffer       = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, framebuffer);
        // create a color attachment texture
        Texture texture = new Texture(framebufferWidth, framebufferHeight, 3);
        texture.bind().upload();
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, texture.id(), 0);
        // create a renderbuffer object for depth and stencil attachment (we won't be sampling these)
        int rbo = glGenRenderbuffers();
        glBindRenderbuffer(GL_RENDERBUFFER, rbo);
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH24_STENCIL8, framebufferWidth, framebufferHeight); // use a single renderbuffer object for both a depth AND stencil buffer.
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT, GL_RENDERBUFFER, rbo); // now actually attach it
        // now that we actually created the framebuffer and added all attachments we want to check if it is actually complete now
        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) System.out.println("ERROR::FRAMEBUFFER:: Framebuffer is not complete!");
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        
        // draw as wireframe
        // glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
        
        // render loop
        // -----------
        while (!glfwWindowShouldClose(window))
        {
            // per-frame time logic
            // --------------------
            double currentFrame = glfwGetTime();
            deltaTime = currentFrame - lastFrame;
            lastFrame = currentFrame;
            
            // input
            // -----
            processInput(window);
            
            
            // render
            // ------
            // bind to framebuffer and draw scene as we normally would to color texture
            glBindFramebuffer(GL_FRAMEBUFFER, framebuffer);
            glViewport(0, 0, framebufferWidth, framebufferHeight);
            glEnable(GL_DEPTH_TEST); // enable depth testing (is disabled for rendering screen-space quad)
            
            // make sure we clear the framebuffer's content
            glClearColor(0.1f, 0.1f, 0.1f, 1.0f);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            
            shader.bind();
            model.identity();
            view.identity().lookAt(5 * (float) Math.cos(currentFrame), 1, 5 * (float) Math.sin(currentFrame), 0, 0, 0, 0, 1, 0);
            projection.identity().perspective((float) Math.toRadians(90), (float) SCR_WIDTH / (float) SCR_HEIGHT, 0.1f, 100.0f);
            shader.setMat4("view", view);
            shader.setMat4("projection", projection);
            // cubes
            cubeArray.bind();
            glActiveTexture(GL_TEXTURE0);
            cubeTexture.bind();
            model.translate(-1.0f, 0.0f, -1.0f);
            shader.setMat4("model", model);
            cubeArray.draw(GL_TRIANGLES);
            model.identity();
            model.translate(2.0f, 0.0f, 0.0f);
            shader.setMat4("model", model);
            cubeArray.draw(GL_TRIANGLES);
            cubeArray.unbind();
            
            // floor
            floorTexture.bind();
            shader.setMat4("model", model.identity());
            planeArray.bind().draw(GL_TRIANGLES).unbind();
            
            // now bind back to default framebuffer and draw a quad plane with the attached framebuffer color texture
            glBindFramebuffer(GL_FRAMEBUFFER, 0);
            glViewport(0, 0, SCR_WIDTH, SCR_HEIGHT);
            glDisable(GL_DEPTH_TEST); // disable depth test so screen-space quad isn't discarded due to depth test.
            // clear all relevant buffers
            glClearColor(1.0f, 1.0f, 1.0f, 1.0f); // set clear color to white (not really necessery actually, since we won't be able to see behind the quad anyways)
            glClear(GL_COLOR_BUFFER_BIT);
            
            screenShader.bind();
            texture.bind();    // use the color attachment texture as the texture of the quad plane
            quadArray.bind().draw(GL_TRIANGLES).unbind();
            
            
            // glfw: swap buffers and poll IO events (keys pressed/released, mouse moved etc.)
            // -------------------------------------------------------------------------------
            glfwSwapBuffers(window);
            glfwPollEvents();
        }
        
        // optional: de-allocate all resources once they've outlived their purpose:
        // ------------------------------------------------------------------------
        cubeArray.delete();
        planeArray.delete();
        quadArray.delete();
        
        glfwTerminate();
    }
    
    
    // process all input: query GLFW whether relevant keys are pressed/released this frame and react accordingly
    // ---------------------------------------------------------------------------------------------------------
    static void processInput(long window)
    {
        if (glfwGetKey(window, GLFW_KEY_ESCAPE) == GLFW_PRESS)
        { glfwSetWindowShouldClose(window, true); }
        
        // if (glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS)
        //     camera.ProcessKeyboard(FORWARD, deltaTime);
        // if (glfwGetKey(window, GLFW_KEY_S) == GLFW_PRESS)
        //     camera.ProcessKeyboard(BACKWARD, deltaTime);
        // if (glfwGetKey(window, GLFW_KEY_A) == GLFW_PRESS)
        //     camera.ProcessKeyboard(LEFT, deltaTime);
        // if (glfwGetKey(window, GLFW_KEY_D) == GLFW_PRESS)
        //     camera.ProcessKeyboard(RIGHT, deltaTime);
    }
    
    // glfw: whenever the window size changed (by OS or user resize) this callback function executes
    // ---------------------------------------------------------------------------------------------
    static void framebuffer_size_callback(long window, int width, int height)
    {
        // make sure the viewport matches the new window dimensions; note that width and
        // height will be significantly larger than specified on retina displays.
        glViewport(0, 0, width, height);
    }
    
    // glfw: whenever the mouse moves, this callback is called
    // -------------------------------------------------------
    static void mouse_callback(long window, double xpos, double ypos)
    {
        if (firstMouse)
        {
            lastX      = xpos;
            lastY      = ypos;
            firstMouse = false;
        }
        
        // double xoffset = xpos - lastX;
        // double yoffset = lastY - ypos; // reversed since y-coordinates go from bottom to top
        
        lastX = xpos;
        lastY = ypos;
        
        // camera.ProcessMouseMovement(xoffset, yoffset);
    }
    
    // glfw: whenever the mouse scroll wheel scrolls, this callback is called
    // ----------------------------------------------------------------------
    static void scroll_callback(long window, double xoffset, double yoffset)
    {
        // camera.ProcessMouseScroll(yoffset);
    }
}
