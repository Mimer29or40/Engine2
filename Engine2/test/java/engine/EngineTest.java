package engine;

import engine.util.Logger;

public class EngineTest extends Engine
{
    Logger logger = new Logger();
    
    @Override
    protected void setup()
    {
        // size(200, 200, 2, 2);
        size(100, 100, 8, 8);
    
        enableBlend(true);
    }
    
    @Override
    protected void draw(double elapsedTime)
    {
        // for (Event event : Events.get()) logger.info(event.toString());
    
        clear();
    
        // stroke(255, 100);
        // weight(map(mouse().x(), 0, screenHeight(), 1, 15));
        // logger.info(mouse().pos().toString());
    
        // weight(10);
        // stroke(Color.RED);
        // line(screenWidth() / 2.0, screenHeight() / 2.0, mouse().pos());
        // point(mouse().pos());
    
        // bezier(0,0, mouse().pos(), screenWidth() - 1, screenHeight() - 1);
    
        // stroke(255, 0, 0, 100);
        // weight(3);
        // fill(255);
        // polygon(mouse().pos(), 99, 75, 75, 75, 50, 25, 25, 75, 0, 75);
        // polygon(mouse().pos(), 99, 25, 99, 99, 0, 99, 0, 25);
        // polygon(0, 25, 10, 0, 30, 50, 30, 0, 40, 25, 50, 0, 50, 50, 70, 0, 80, 25, 99, 99, mouse().pos());
    
        translate(screenWidth() / 2, screenHeight() / 2);
        rotate(elapsedTime / 1000);
        noStroke();
        circle(0, 0, 50);
    
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
