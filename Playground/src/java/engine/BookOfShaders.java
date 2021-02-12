package engine;

import engine.render.Texture;
// import engine.shader.EEXT_Shader;

public class BookOfShaders extends Engine
{
    Texture texture;
    
    @Override
    public void setup()
    {
        size(800, 800, 1, 1);
        // EEXT_Shader.addShader("shader.frag");
        
        this.texture = new Texture(screenWidth(), screenHeight());
    }
    
    @SuppressWarnings("EmptyMethod")
    @Override
    public void draw(double elapsedTime)
    {
        // drawShader(this.texture, shader());
    }
    
    public static void main(String[] args)
    {
        start(new BookOfShaders());
    }
}
