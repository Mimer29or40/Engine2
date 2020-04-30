package engine.gui.util;

import engine.Engine;
import engine.color.Color;
import engine.render.RectMode;

import static engine.util.Util.println;

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
        size(100, 100);
    
        r1.size(20, 30);
        r2.set(30, 30, 40, 40);
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
        
        if (mouse().scrollY() != 0)
        {
            r1.inflate((int) mouse().scrollY(), (int) mouse().scrollY());
            println(r1.topLeft(), r1.bottomRight());
            println(r1.size());
        }
        
        clear();
        
        r1.pos((int) mouse().x(), (int) mouse().y());
        
        noStroke();
        rectMode(RectMode.CORNERS);
    
        fill(Color.BLUE);
        rect(r2.left(), r2.top(), r2.right(), r2.bottom());
    
        fill(Color.RED);
        rect(r1.left(), r1.top(), r1.right(), r1.bottom());
        
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
        rect(r3.left(), r3.top(), r3.right(), r3.bottom());
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