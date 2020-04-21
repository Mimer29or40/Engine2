package engine.gui.util;

import engine.Engine;
import engine.color.Color;

public class RectTest extends Engine
{
    int state = 0;
    
    Rect r1 = new Rect();
    Rect r2 = new Rect();
    Rect r3 = new Rect();
    
    /**
     * This method is called once the engine's environment is fully setup. This is only called once.
     * <p>
     * This is where you call {@link #size} to enable rendering. At which point you can create textures and render to them.
     */
    @Override
    public void setup()
    {
        size(100, 100, OPENGL);
    
        r1.size(20, 30);
        r2.set(30, 30, 40, 40);
        
        rendererBlend(true);
    }
    
    /**
     * This method is called once per frame.
     *
     * @param elapsedTime The time in seconds since the last frame.
     */
    @Override
    public void draw(double elapsedTime)
    {
        if (keyboard().K1.down()) state = 0;
        if (keyboard().K2.down()) state = 1;
        if (keyboard().K3.down()) state = 2;
        if (keyboard().K4.down()) state = 3;
        
        if (mouse().scrollY() != 0) r1.inflate((int) mouse().scrollY(), (int) mouse().scrollY());
        
        clear();
        
        r1.pos((int) mouse().x(), (int) mouse().y());
        
        noStroke();
    
        fill(Color.BLUE);
        rect(r2.left(), r2.top(), r2.width(), r2.height());
    
        fill(Color.RED);
        rect(r1.left(), r1.top(), r1.width(), r1.height());
        
        switch (state)
        {
            case 0:
                r1.clamp(r2, r3);
                break;
            case 1:
                r1.clip(r2, r3);
                break;
            case 2:
                r1.union(r2, r3);
                break;
            case 3:
                r1.fit(r2, r3);
                break;
        }
    
        fill(Color.GREEN);
        rect(r3.left(), r3.top(), r3.width(), r3.height());
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
        start(new RectTest());
    }
}