package engine;

import engine.render.Texture;

import static engine.shader.EEXT_Shader.addShader;

public class BookOfShaders extends Engine
{
    Texture texture;
    
    @Override
    public void setup()
    {
        size(800, 800, 1, 1);
        addShader("shader.frag");
        
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