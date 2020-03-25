package engine;

import engine.color.Color;
import engine.render.RectMode;
import engine.render.Texture;
import engine.util.Logger;

import static engine.util.Util.map;

public class EngineTest extends Engine
{
    Logger  logger = new Logger();
    int     state  = 6;
    Texture texture;
    
    @Override
    protected void setup()
    {
        // size(200, 200, 2, 2);
        // size(100, 100, 8, 8);
        size(400, 400, 2, 2, "opengl");
    
        texture = new Texture(30, 30);
        Color c = new Color();
        for (int j = 0; j < texture.height(); j++)
        {
            for (int i = 0; i < texture.width(); i++)
            {
                texture.setPixel(i, j, c.set((double) i / (double) texture.width(), (double) j / (double) texture.height(), 255, 255));
            }
        }
        
        enableBlend(true);
    }
    
    @Override
    protected void draw(double elapsedTime)
    {
        switch (state)
        {
            case 1:
                clear();
            
                weight(map(mouse().x(), 0, screenHeight(), 1, 15));
                logger.info(mouse().pos().toString());
            
                stroke(Color.RED);
                line(screenWidth() / 2.0, screenHeight() / 2.0, mouse().pos());
                point(mouse().pos());
                break;
            case 2:
                clear();
                stroke(255);
                bezier(0, 0, mouse().pos(), screenWidth() - 1, screenHeight() - 1);
                break;
            case 3:
                clear();
                stroke(255, 0, 0, 100);
                weight(3);
                fill(255);
                // polygon(mouse().pos(), 99, 75, 75, 75, 50, 25, 25, 75, 0, 75);
                // polygon(mouse().pos(), 99, 25, 99, 99, 0, 99, 0, 25);
                polygon(0, 25, 10, 0, 30, 50, 30, 0, 40, 25, 50, 0, 50, 50, 70, 0, 80, 25, 99, 99, mouse().pos());
                break;
            case 4:
                clear();
                translate(screenWidth() / 2., screenHeight() / 2.);
                rotate(time() / 1000000000.);
                noStroke();
                circle(0, 0, 50);
                break;
            case 5:
                int[] pixels = loadPixels();
                for (int i = 0; i < pixels.length; i++)
                {
                    pixels[i] = nextInt(255);
                }
                updatePixels();
                break;
            case 6:
                clear();
                weight(10);
                stroke(255);
                fill(255, 0, 0);
                rectMode(RectMode.CENTER);
                translate(screenWidth() / 2., screenHeight() / 2.);
                circle(0, 0, 200 * Math.sqrt(2));
                rotate(time() / 2000000000.);
                // scale(0.5, 2);
                texture(texture, 0, 0, 200, 200);
                break;
            case 7:
                break;
            case 8:
                break;
            case 9:
                break;
            case 10:
                break;
            case 11:
                break;
            case 12:
                break;
        }
        if (keyboard().SPACE.down()) screenShot("screenshot" + frameCount());
        if (keyboard().S.down()) screenShot();
        if (keyboard().F.down()) fullscreen(!fullscreen());
        if (keyboard().V.down()) vsync(!vsync());
    
        if (keyboard().F1.down()) state = 1;
        if (keyboard().F2.down()) state = 2;
        if (keyboard().F3.down()) state = 3;
        if (keyboard().F4.down()) state = 4;
        if (keyboard().F5.down()) state = 5;
        if (keyboard().F6.down()) state = 6;
        if (keyboard().F7.down()) state = 7;
        if (keyboard().F8.down()) state = 8;
        if (keyboard().F9.down()) state = 9;
        if (keyboard().F10.down()) state = 10;
        if (keyboard().F11.down()) state = 11;
        if (keyboard().F12.down()) state = 12;
    }
    
    @Override
    protected void destroy()
    {
    
    }
    
    public static void main(String[] args)
    {
        // enableProfiler();
        start(new EngineTest(), Logger.Level.DEBUG);
    }
}
