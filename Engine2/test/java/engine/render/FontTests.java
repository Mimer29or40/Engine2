package engine.render;

import engine.Engine;
import engine.Keyboard;
import engine.color.Color;
import engine.font.Font;
import rutils.Logger;

import java.util.logging.Level;

import static rutils.NumUtil.map;

public class FontTests extends Engine
{
    Font font;
    
    @Override
    public void setup()
    {
        // size(800, 800, 1, 1);
        size(800, 800, 1, 1);
    
        // font = new Font("demo/FiraSans.ttf", 24, false);
        // font = new Font("fonts/BetterPixels-Regular.ttf", 24, false);
        font = Font.DEFAULT;
    }
    
    @Override
    public void draw(double elapsedTime)
    {
        clear();
    
        textSize((int) map(mouse().x(), 0, screenWidth() - 1, 4, 256));
        // textSize(64);
    
        translate(screenWidth() / 2., screenHeight() / 2.);
        rotate(seconds());
    
        if (keyboard().held(Keyboard.Key.F1)) textAlign(TextAlign.TOP_LEFT);
        if (keyboard().held(Keyboard.Key.F2)) textAlign(TextAlign.TOP);
        if (keyboard().held(Keyboard.Key.F3)) textAlign(TextAlign.TOP_RIGHT);
        if (keyboard().held(Keyboard.Key.F4)) textAlign(TextAlign.LEFT);
        if (keyboard().held(Keyboard.Key.F5)) textAlign(TextAlign.CENTER);
        if (keyboard().held(Keyboard.Key.F6)) textAlign(TextAlign.RIGHT);
        if (keyboard().held(Keyboard.Key.F7)) textAlign(TextAlign.BOTTOM_LEFT);
        if (keyboard().held(Keyboard.Key.F8)) textAlign(TextAlign.BOTTOM);
        if (keyboard().held(Keyboard.Key.F9)) textAlign(TextAlign.BOTTOM_RIGHT);
    
        if (keyboard().held(Keyboard.Key.K1)) rectMode(RectMode.CORNER);
        if (keyboard().held(Keyboard.Key.K2)) rectMode(RectMode.CORNERS);
        if (keyboard().held(Keyboard.Key.K3)) rectMode(RectMode.CENTER);
        if (keyboard().held(Keyboard.Key.K4)) rectMode(RectMode.RADIUS);
    
        fill(Color.DARK_GREEN);
    
        String text;
        text = "This is a String.";
        text = "This is a\nMultiline String.";
        if (keyboard().held(Keyboard.Key.SPACE))
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
        Logger.setLevel(Level.FINE);
    
        start(new FontTests());
    }
}
