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
    
        renderer().blend(true);
    }
    
    @Override
    protected void draw(double elapsedTime)
    {
        // for (Event event : Events.get()) logger.info(event.toString());
    
        renderer().clear();
    
        // renderer().stroke(255, 100);
        // renderer().weight(map(mouse().x(), 0, screenHeight(), 1, 15));
        // logger.info(mouse().pos().toString());
    
        // renderer().weight(10);
        // renderer().line(screenWidth() / 2.0, screenHeight() / 2.0, mouse().pos());
        // renderer().stroke(Color.RED);
        // renderer().point(mouse().pos());
    
        // renderer().bezier(0,0, mouse().pos(), screenWidth() - 1, screenHeight() - 1);
    
        double[] cords;
        // cords = new double[] {mouse().x(), mouse().y(), 99, 50, 75, 50, 50, 25, 25, 50, 0, 50};
        // cords = new double[] {mouse().x(), mouse().y(), 99, 25, 99, 99, 0, 99, 0, 25};
        cords = new double[] {0, 25, 10, 0, 30, 50, 30, 0, 40, 25, 50, 0, 50, 50, 70, 0, 80, 25, 99, 99, mouse().x(), mouse().y()};
        // cords = new double[] {0, 25, 10, 0, 30, 50, 30, 0, 40, 25, 50, 0, 50, 50, 70, 0, 80, 25, 99, 99, 0, 99};
    
        renderer().fill(255);
        renderer().fillPolygon(cords);
    
        renderer().stroke(255, 0, 0, 100);
        renderer().weight(1);
        renderer().drawPolygon(cords);
    
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
