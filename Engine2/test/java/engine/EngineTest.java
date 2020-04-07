package engine;

import engine.color.Color;
import engine.render.ArcMode;
import engine.render.RectMode;
import engine.render.Texture;
import engine.util.Logger;

import java.util.logging.Level;

import static engine.util.Util.map;
import static engine.util.Util.println;

public class EngineTest extends Engine
{
    Logger  logger = new Logger();
    int     state  = 8;
    Texture texture;
    
    @Override
    protected void setup()
    {
        // size(200, 200, 2, 2);
        // size(100, 100, 8, 8);
        // size(400, 400, 2, 2, SOFTWARE);
        size(400, 400, 2, 2, OPENGL);
    
        texture = new Texture(30, 30);
        Color c = new Color();
        for (int j = 0; j < texture.height(); j++)
        {
            for (int i = 0; i < texture.width(); i++)
            {
                texture.setPixel(i, j, c.set((double) i / (double) texture.width(), (double) j / (double) texture.height(), 255, 255));
            }
        }
        texture.upload();
        
        rendererBlend(true);
    }
    
    @Override
    protected void draw(double elapsedTime)
    {
        clear();
        // push();
        switch (state)
        {
            case 1:
                weight(map(mouse().x(), 0, screenWidth(), 1, 50));
                stroke(Color.BLUE);
                point(mouse().x(), mouse().y());
                stroke(Color.RED);
                line(screenWidth() / 2.0, screenHeight() / 2.0, mouse().x(), mouse().y());
                break;
            case 2:
                stroke(255);
                bezier(0, 0, mouse().x(), mouse().y(), screenWidth() - 1, screenHeight() - 1);
                break;
            case 3:
                stroke(255, 0, 0, 100);
                int thick = 80;
                weight(5);
                // scale(10, 10);
                // translate(screenWidth() / 4., screenHeight() / 4.);
                // translate(5, 5);
                fill(255);
                // polygon(mouse().pos(), 99, 75, 75, 75, 50, 25, 25, 75, 0, 75);
                // polygon(mouse().pos(), 99, 25, 99, 99, 0, 99, 0, 25);
                polygon(0, 25, 10, 0, 30, 50, 30, 0, 40, 25, 50, 0, 50, 50, 70, 0, 80, 25, 99, 99, mouse().x(), mouse().y());
                // polygon(0, 25, 10, 0, 30, 50, 30, 0, 40, 25, 50, 0, 50, 50, 70, 0, 80, 25, 99, 99, 0, 99);
                // polygon(thick, thick, screenWidth() - thick, thick, mouse().x(), mouse().y(), thick, screenHeight() - thick);
                // polygon(thick, thick, screenWidth() - thick, thick, screenWidth() - thick, screenHeight() - thick, thick, screenHeight() - thick);
                break;
            case 4:
                rectMode(RectMode.CENTER);
                translate(screenWidth() / 2., screenHeight() / 2.);
                rotate(seconds() / 2);
                // noStroke();
                stroke(255, 225);
                weight(map(mouse().x(), 0, screenWidth(), 0, 40));
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
                if (keyboard().K1.held(modifiers().NONE)) arcMode(ArcMode.DEFAULT);
                if (keyboard().K2.held(modifiers().NONE)) arcMode(ArcMode.OPEN);
                if (keyboard().K3.held(modifiers().NONE)) arcMode(ArcMode.CHORD);
                if (keyboard().K4.held(modifiers().NONE)) arcMode(ArcMode.PIE);
                weight(10);
                translate(screenWidth() / 2., screenHeight() / 2.);
                translate(50 * Math.cos(seconds()), 50 * Math.sin(seconds()));
                scale(1, 2 * Math.cos(seconds()));
                rotate(Math.sin(seconds()));
                // rotate(seconds());
                // arc(0, 0, 300, 100, 0, map(Math.sin(seconds()), -1, 1, 0, 2.0 * Math.PI));
                arc(0, 0, 300, 100, 0, seconds() + 1 * Math.sin(4 * seconds()));
                break;
            case 9:
                push();
                stroke(255, 100);
                triangle(0, 0, mouse().x(), mouse().y(), 0, screenHeight() / 2.);
                pop();
                push();
                fill(nextColor(true));
                rectMode(RectMode.CENTER);
                translate(screenWidth() / 2., screenHeight() / 2.);
                rotate(seconds());
                // noStroke();
                square(0, 0, 50);
                pop();
                // fill(Color.WHITE);
                break;
            case 10:
                break;
            case 11:
                break;
            case 12:
                break;
        }
        // pop();
        // fill(Color.GREEN);
        // textSize(30);
        // text("Frame: " + frameCount(), 0, 0);
        if (keyboard().K1.down(modifiers().CONTROL)) println("Modifier Tests");
        if (keyboard().S.down()) screenShot();
        if (keyboard().D.down()) rendererDebug(!rendererDebug());
        if (keyboard().F.down()) window().toggleFullscreen();
        if (keyboard().V.down()) window().toggleVsync();
    
        if (keyboard().F1.down(modifiers().NONE)) state = 1;
        if (keyboard().F2.down(modifiers().NONE)) state = 2;
        if (keyboard().F3.down(modifiers().NONE)) state = 3;
        if (keyboard().F4.down(modifiers().NONE)) state = 4;
        if (keyboard().F5.down(modifiers().NONE)) state = 5;
        if (keyboard().F6.down(modifiers().NONE)) state = 6;
        if (keyboard().F7.down(modifiers().NONE)) state = 7;
        if (keyboard().F8.down(modifiers().NONE)) state = 8;
        if (keyboard().F9.down(modifiers().NONE)) state = 9;
        if (keyboard().F10.down(modifiers().NONE)) state = 10;
        if (keyboard().F11.down(modifiers().NONE)) state = 11;
        if (keyboard().F12.down(modifiers().NONE)) state = 12;
    
        if (keyboard().ESCAPE.down()) stop();
    }
    
    @Override
    protected void destroy()
    {
    
    }
    
    public static void main(String[] args)
    {
        enableProfiler();
        start(new EngineTest(), Level.INFO);
    }
}
