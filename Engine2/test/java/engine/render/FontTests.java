package engine.render;

import engine.Engine;
import engine.color.Color;

import java.util.logging.Level;

import static engine.util.Util.map;

public class FontTests extends Engine
{
    Font font;
    
    @Override
    public void setup()
    {
        // size(800, 800, 1, 1);
        size(800, 800, 1, 1);
        
        // font = new Font("demo/FiraSans.ttf", 24, false);
        // font = new Font("fonts/BetterPixels.ttf", 24, false);
        font = Font.getFont(24);
    }
    
    @Override
    public void draw(double elapsedTime)
    {
        clear();
        
        textSize((int) map(mouse().x(), 0, screenWidth() - 1, 4, 256));
        // textSize(64);
        
        translate(screenWidth() / 2., screenHeight() / 2.);
        rotate(seconds());
        
        if (keyboard().F1.held()) textAlign(TextAlign.TOP_LEFT);
        if (keyboard().F2.held()) textAlign(TextAlign.TOP);
        if (keyboard().F3.held()) textAlign(TextAlign.TOP_RIGHT);
        if (keyboard().F4.held()) textAlign(TextAlign.LEFT);
        if (keyboard().F5.held()) textAlign(TextAlign.CENTER);
        if (keyboard().F6.held()) textAlign(TextAlign.RIGHT);
        if (keyboard().F7.held()) textAlign(TextAlign.BOTTOM_LEFT);
        if (keyboard().F8.held()) textAlign(TextAlign.BOTTOM);
        if (keyboard().F9.held()) textAlign(TextAlign.BOTTOM_RIGHT);
        
        if (keyboard().K1.held()) rectMode(RectMode.CORNER);
        if (keyboard().K2.held()) rectMode(RectMode.CORNERS);
        if (keyboard().K3.held()) rectMode(RectMode.CENTER);
        if (keyboard().K4.held()) rectMode(RectMode.RADIUS);
        
        fill(Color.DARK_GREEN);
        
        String text;
        text = "This is a String.";
        text = "This is a\nMultiline String.";
        if (keyboard().SPACE.held())
        {
            rectMode(RectMode.CENTER);
            rect(0, 0, 400, 400);
            text(text, 0, 0, 400, 400);
        }
        else
        {
            text(text, 0, 0);
        }
    }
    
    @Override
    public void destroy()
    {
    
    }
    
    public static void main(String[] args)
    {
        start(new FontTests(), Level.FINE);
    }
}
