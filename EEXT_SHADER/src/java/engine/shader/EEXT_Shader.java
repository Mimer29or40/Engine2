package engine.shader;

import engine.Engine;
import engine.Extension;
import engine.input.Keyboard;
import engine.input.Mouse;
import engine.render.Shader;
import engine.render.TextAlign;
import engine.render.Texture;
import engine.render.VertexArray;

import static engine.Engine.*;
import static org.lwjgl.opengl.GL40.GL_DYNAMIC_DRAW;
import static org.lwjgl.opengl.GL40.GL_QUADS;

public class EEXT_Shader extends Extension
{
    public static final EEXT_Shader INSTANCE = new EEXT_Shader();
    
    private Texture     texture;
    private VertexArray vao;
    
    private String      shaderFile    = "";
    private FileWatcher fileWatcher;
    private Shader      shader;
    private String      shaderError;
    
    public EEXT_Shader()
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
        if (this.fileWatcher.fileChanged())
        {
            try
            {
                this.shader = new Shader().loadVertexFile("shaders/pixel.vert").loadFragmentFile(this.shaderFile).validate().unbind();
                this.shaderError = null;
            }
            catch (RuntimeException exception)
            {
                this.shader = null;
                this.shaderError = exception.getMessage();
            }
        }
        
        if (keyboard().ESCAPE.down()) mouse().toggleCaptured();
        
        if (this.shader != null)
        {
            this.shader.bind();
            this.shader.setVec2("resolution", screenWidth(), screenHeight());
    
            this.shader.setUniform("frameCount", frameCount());
            this.shader.setUniform("seconds", seconds());
            this.shader.setUniform("elapsedTime", elapsedTime);
    
            this.shader.setUniform("mouseCaptured", mouse().captured());
            this.shader.setUniform("mouseEntered", mouse().entered());
            this.shader.setVec2("mousePos", mouse().x(), mouse().y());
            this.shader.setVec2("mouseRel", mouse().relX(), mouse().relY());
            this.shader.setVec2("mouseScroll", mouse().scrollX(), mouse().scrollY());
            for (Mouse.Button button : mouse().inputs())
            {
                this.shader.setVec4(button.toString().replace(".", ""), button.down(), button.up(), button.held(), button.repeat());
            }
            for (Keyboard.Key key : keyboard().inputs())
            {
                this.shader.setVec4(key.toString().replace(".", ""), key.down(), key.up(), key.held(), key.repeat());
            }
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
        if (this.shader != null)
        {
            this.texture.bindFramebuffer();
    
            this.shader.bind();
            this.vao.bind().draw(GL_QUADS).unbind();
    
            this.texture.markGPUDirty();
    
            texture(this.texture, 0, 0);
        }
        else if (this.shaderError != null)
        {
            clear();
            textSize(24);
            textAlign(TextAlign.CENTER);
            text(this.shaderError, 0, 0, screenWidth(), screenHeight());
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
        this.fileWatcher.stopThread();
    }
    
    public static void setShaderFile(String shaderFile)
    {
        EEXT_Shader.INSTANCE.enabled = true;
        
        EEXT_Shader.INSTANCE.texture = new Texture(screenWidth(), screenHeight());
        EEXT_Shader.INSTANCE.vao     = new VertexArray().bind().add(new float[] {-1f, -1f, 1f, -1f, 1f, 1f, -1f, 1f}, GL_DYNAMIC_DRAW, 2);
        
        EEXT_Shader.INSTANCE.shaderFile = shaderFile;
        EEXT_Shader.INSTANCE.fileWatcher = new FileWatcher(shaderFile);
    }
    
    public static Shader shader()
    {
        return EEXT_Shader.INSTANCE.shader;
    }
}
