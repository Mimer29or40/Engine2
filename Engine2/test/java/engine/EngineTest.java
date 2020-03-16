package engine;

import engine.color.Color;
import engine.util.Logger;

import static engine.util.Util.map;

public class EngineTest extends Engine
{
    Logger logger = new Logger();
    
    @Override
    protected void setup()
    {
        // logger.info("TEST1");
        // logger.info("TEST2", 1);
        size(100, 100);
        vsync(false);
    }
    
    @Override
    protected void draw(double elapsedTime)
    {
        // for (Event event : Events.get()) logger.info(event.toString());
        
        Engine.renderer.clear(Color.RED);
        Engine.renderer.stroke(255);
        Engine.renderer.weight(map(mouse().x(), 0, screenHeight(), 1, 10));
        // logger.info(mouse().pos().toString());
        
        Engine.renderer.point(mouse().x(), mouse().y());
        
        if (keyboard().SPACE.down()) screenShot("screenshot" + frameCount());
        if (keyboard().S.down()) screenShot();
        if (keyboard().F.down()) fullscreen(!fullscreen());
        if (keyboard().V.down()) vsync(!vsync());
    }
    
    @Override
    protected void destroy()
    {
    
    }
    
    public static void main(String[] args)
    {
        // enableProfiler();
        start(new EngineTest(), Logger.Level.INFO);
    }
}
