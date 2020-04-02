package engine;

import engine.color.Color;
import engine.render.ArcMode;
import engine.render.RectMode;
import engine.render.Texture;
import engine.util.Logger;

import java.util.logging.Level;

import static engine.util.Util.map;

public class EngineTest extends Engine
{
    Logger  logger = new Logger();
    int     state  = 4;
    Texture texture;
    
    @Override
    protected void setup()
    {
        // size(200, 200, 2, 2);
        // size(100, 100, 8, 8);
        size(400, 400, 2, 2, "opengl");
        // size(400, 400, 2, 2, "opengl");
    
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
    
                weight(map(mouse().x(), 0, screenHeight(), 1, 50));
    
                stroke(Color.BLUE);
                point(mouse().x(), mouse().y());
                stroke(Color.RED);
                line(screenWidth() / 2.0, screenHeight() / 2.0, mouse().x(), mouse().y());
                break;
            case 2:
                clear();
                stroke(255);
                bezier(0, 0, mouse().x(), mouse().y(), screenWidth() - 1, screenHeight() - 1);
                break;
            case 3:
                clear();
                stroke(255, 0, 0, 100);
                weight(3);
                fill(255);
                // polygon(mouse().pos(), 99, 75, 75, 75, 50, 25, 25, 75, 0, 75);
                // polygon(mouse().pos(), 99, 25, 99, 99, 0, 99, 0, 25);
                polygon(0, 25, 10, 0, 30, 50, 30, 0, 40, 25, 50, 0, 50, 50, 70, 0, 80, 25, 99, 99, mouse().x(), mouse().y());
                break;
            case 4:
                clear();
                rectMode(RectMode.CENTER);
                translate(screenWidth() / 2., screenHeight() / 2.);
                rotate(seconds() / 2);
                // noStroke();
                weight(map(mouse().x(), 0, screenHeight(), 1, 50));
                stroke(Color.WHITE);
                // noStroke();
                fill(Color.BLUE);
                square(0, 0, 300);
                fill(Color.RED);
                circle(0, 0, 300);
                break;
            case 5:
                int[] pixels = loadPixels();
                for (int i = 0, n = pixels.length; i < n; i++)
                {
                    // pixels[i] = nextInt(255);
                    pixels[i] = i;
                }
                updatePixels();
                break;
            case 6:
                // clear();
                weight(10);
                stroke(255);
                fill(255, 0, 0);
                rectMode(RectMode.CENTER);
                translate(screenWidth() / 2., screenHeight() / 2.);
                circle(0, 0, 200 * Math.sqrt(2));
                rotate(seconds());
                // scale(0.5, 2);
                texture(texture, 0, 0, 200, 200);
                break;
            case 7:
                clear();
                stroke(255, 0, 0, 100);
                weight(3);
                fill(255);
                translate(screenWidth() / 2., screenHeight() / 2.);
                // polygon(mouse().pos(), 99, 75, 75, 75, 50, 25, 25, 75, 0, 75);
                // polygon(mouse().pos(), 99, 25, 99, 99, 0, 99, 0, 25);
                ellipse(0, 0, 10, 100);
                line(-10, 0, 10, 0);
                break;
            case 8:
                if (keyboard().K1.held()) arcMode(ArcMode.DEFAULT);
                if (keyboard().K2.held()) arcMode(ArcMode.OPEN);
                if (keyboard().K3.held()) arcMode(ArcMode.CHORD);
                if (keyboard().K4.held()) arcMode(ArcMode.PIE);
                clear();
                translate(screenWidth() / 2., screenHeight() / 2.);
                arc(0, 0, 100, 100, 0, map(mouse().x(), 0, screenWidth() - 1, 0, 2.0 * Math.PI));
                break;
            case 9:
                clear();
                push();
                fill(nextColor());
                rectMode(RectMode.CENTER);
                translate(screenWidth() / 2., screenHeight() / 2.);
                rotate(seconds());
                // noStroke();
                square(0, 0, 50);
                pop();
                // fill(Color.WHITE);
                triangle(0, 0, mouse().x(), mouse().y(), 50, 50);
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
        if (keyboard().F.down()) window().fullscreen(!window().fullscreen());
        if (keyboard().V.down()) window().vsync(!window().vsync());
    
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
        start(new EngineTest(), Level.FINE);
    }
}
