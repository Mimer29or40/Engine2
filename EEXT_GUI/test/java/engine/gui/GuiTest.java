package engine.gui;

import engine.Engine;
import engine.gui.util.Rect;
import engine.input.Mouse;

import java.util.logging.Level;

import static engine.util.Util.println;

public class GuiTest extends Engine
{
    UIElement element;
    
    /**
     * This method is called once the engine's environment is fully setup. This is only called once.
     * <p>
     * This is where you call {@link #size} to enable rendering. At which point you can create textures and render to them.
     */
    @Override
    public void setup()
    {
        size(100, 100, 4, 4, OPENGL);
        
        element = new UIWindow(new Rect(0, 0, 50, 50), "Title", true)
        {
            @Override
            public boolean blocking()
            {
                return true;
            }
    
            @Override
            public boolean onMouseButtonDown(Mouse.Button button, double x, double y)
            {
                println("Mouse Down");
                return super.onMouseButtonDown(button, x, y);
            }
        };
    }
    
    /**
     * This method is called once per frame.
     *
     * @param elapsedTime The time in seconds since the last frame.
     */
    @Override
    public void draw(double elapsedTime)
    {
        
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
        start(new GuiTest(), Level.FINE);
    }
}