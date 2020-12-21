package engine.shader;

import engine.Engine;
import engine.color.Color;

import static engine.shader.EEXT_Shader.addShader;

public class ShaderTest extends Engine
{
    /**
     * This method is called once the engine's environment is fully setup. This is only called once.
     * <p>
     * This is where you call {@link #size} to enable rendering. At which point you can create textures and render to them.
     */
    @Override
    public void setup()
    {
        size(400, 400, 2, 2);
        addShader("shader.frag");
    }
    
    /**
     * This method is called once per frame.
     *
     * @param elapsedTime The time in seconds since the last frame.
     */
    @Override
    public void draw(double elapsedTime)
    {
        clear(Color.RED);
    
        weight(20);
        point(mouse().x(), mouse().y());
    }
    
    /**
     * This method is called after the render loop has exited for any reason, exception or otherwise. This is only called once.
     */
    @Override
    public void destroy()
    {
        
    }
    
    public static void main(String[] args)
    {
        start(new ShaderTest());
    }
}
