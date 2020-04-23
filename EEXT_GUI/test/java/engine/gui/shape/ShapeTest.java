package engine.gui.shape;

import engine.Engine;
import engine.color.Color;

import static engine.gui.GUI.createGUI;

public class ShapeTest extends Engine
{
    /**
     * This method is called once the engine"s environment is fully setup. This is only called once.
     * <p>
     * This is where you call {@link #size} to enable rendering. At which point you can create textures and render to them.
     */
    @Override
    public void setup()
    {
        size(400, 400, 2, 2, OPENGL);
        createGUI();
    }
    
    /**
     * This method is called once per frame.
     *
     * @param elapsedTime The time in seconds since the last frame.
     */
    @Override
    public void draw(double elapsedTime)
    {
        clear();
        
        Color shadowColor     = new Color(255, 10);
        Color borderColor     = new Color(Color.BLACK);
        Color backgroundColor = new Color(Color.WHITE);
        
        int x = 20;
        int y = 20;
        
        int width  = 100;
        int height = 30;
        
        int shadowSize = 2;
        int borderSize = 2;
        
        noStroke();
        
        fill(shadowColor);
        rect(x, y, width, height);
        
        fill(borderColor);
        rect(x + shadowSize, y + shadowSize, width - (shadowSize << 1), height - (shadowSize << 1));
        
        fill(backgroundColor);
        rect(x + shadowSize + borderSize, y + shadowSize + borderSize, width - ((shadowSize + borderSize) << 1), height - ((shadowSize + borderSize) << 1));
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
        start(new ShapeTest());
    }
}