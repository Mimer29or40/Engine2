package engine.shader;

import engine.Engine;
import engine.Extension;
import engine.input.Keyboard;
import engine.input.Mouse;
import engine.render.*;

import java.util.HashMap;

import static engine.Engine.*;

public class EEXT_Shader extends Extension
{
    public static final EEXT_Shader INSTANCE = new EEXT_Shader();
    
    private Texture     texture;
    private VertexArray vao;
    
    private final FileWatcher fileWatcher = new FileWatcher();
    
    private String defaultShader = null;
    
    private final HashMap<String, Shader> shaders = new HashMap<>();
    
    private String errorMessage;
    
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
        this.texture = new Texture(screenWidth(), screenHeight());
        this.vao     = new VertexArray().bind().add(new float[] {-1f, -1f, 1f, -1f, 1f, 1f, -1f, 1f}, GL.DYNAMIC_DRAW, 2).unbind();
    }
    
    /**
     * This is called once per frame before the {@link Engine#draw} method is called.
     *
     * @param elapsedTime The time in seconds since the last frame.
     */
    @Override
    public void beforeDraw(double elapsedTime)
    {
        for (String changedFile : this.fileWatcher.changedFiles())
        {
            try
            {
                this.shaders.replace(changedFile, new Shader().bind().loadVertexFile("shaders/pixel.vert").loadFragmentFile(changedFile).validate().unbind());
                this.errorMessage = null;
            }
            catch (RuntimeException exception)
            {
                this.shaders.replace(changedFile, null);
                this.errorMessage = exception.getMessage();
            }
        }
        
        if (keyboard().ESCAPE.down()) mouse().toggleCaptured();
    
        for (Shader shader : this.shaders.values())
        {
            if (shader != null)
            {
                shader.bind();
                shader.setVec2("resolution", screenWidth(), screenHeight());
            
                shader.setUniform("frameCount", frameCount());
                shader.setUniform("seconds", seconds());
                shader.setUniform("elapsedTime", elapsedTime);
            
                shader.setUniform("mouseCaptured", mouse().captured());
                shader.setUniform("mouseEntered", mouse().entered());
                shader.setVec2("mousePos", mouse().x(), mouse().y());
                shader.setVec2("mouseRel", mouse().relX(), mouse().relY());
                shader.setVec2("mouseScroll", mouse().scrollX(), mouse().scrollY());
                for (Mouse.Button button : mouse().inputs())
                {
                    shader.setVec4(button.toString().replace(".", ""), button.down(), button.up(), button.held(), button.repeat());
                }
                for (Keyboard.Key key : keyboard().inputs())
                {
                    shader.setVec4(key.toString().replace(".", ""), key.down(), key.up(), key.held(), key.repeat());
                }
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
        if (shader() != null)
        {
            drawShader(this.texture, shader());
    
            texture(this.texture, 0, 0);
        }
        else if (this.errorMessage != null)
        {
            clear();
            textSize(24);
            textAlign(TextAlign.CENTER);
            text(this.errorMessage, 0, 0, screenWidth(), screenHeight());
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
    
    public static String addShader(String shaderFile)
    {
        EEXT_Shader.INSTANCE.enabled = true;
        
        if (EEXT_Shader.INSTANCE.defaultShader == null) EEXT_Shader.INSTANCE.defaultShader = shaderFile;
        
        EEXT_Shader.INSTANCE.shaders.put(shaderFile, null);
        EEXT_Shader.INSTANCE.fileWatcher.addFile(shaderFile);
        
        return shaderFile;
    }
    
    public static Shader shader(String shader)
    {
        return EEXT_Shader.INSTANCE.shaders.get(shader);
    }
    
    public static Shader shader()
    {
        return EEXT_Shader.INSTANCE.shaders.get(EEXT_Shader.INSTANCE.defaultShader);
    }
    
    public static void drawShader(Texture target, Shader shader)
    {
        target.bindFramebuffer();
        shader.bind();
        EEXT_Shader.INSTANCE.vao.bind().draw(GL.QUADS).unbind();
        target.markGPUDirty();
    }
}
