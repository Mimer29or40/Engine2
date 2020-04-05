package engine.ct;

import engine.Engine;
import engine.event.Event;
import engine.event.EventMouseButtonDown;
import engine.event.Events;
import engine.render.Overloads;
import engine.render.RectMode;
import org.joml.Vector2d;

import java.util.ArrayList;

import static engine.util.Util.sum;

@SuppressWarnings("unused")
public class CT002_MengerSponge extends Engine
{
    final int[] xRange = new int[] {-1, 0, 1};
    final int[] yRange = new int[] {-1, 0, 1};
    
    class Box
    {
        final Vector2d pos;
        final double   r;
        
        Box(Vector2d pos, double r)
        {
            this.pos = pos;
            this.r   = r;
        }
        
        ArrayList<Box> generate()
        {
            ArrayList<Box> boxes = new ArrayList<>();
            for (int x : xRange)
            {
                for (int y : yRange)
                {
                    int sum = sum(Math.abs(x), Math.abs(y));
                    if (sum > 0)
                    {
                        double r = this.r / 3;
                        boxes.add(new Box(this.pos.add(x * r, y * r, new Vector2d()), r));
                    }
                }
            }
            return boxes;
        }
        
        void show()
        {
            push();
            Overloads.translate(renderer(), this.pos);
            noStroke();
            fill(255);
            Overloads.square(renderer(), this.pos, this.r);
            pop();
        }
    }
    
    ArrayList<Box> sponge = new ArrayList<>();
    
    @Override
    protected void setup()
    {
        size(400, 400, 1, 1, OPENGL);
        
        sponge.add(new Box(new Vector2d(0, 0), 300));
    }
    
    @Override
    protected void draw(double elapsedTime)
    {
        for (Event e : Events.get(EventMouseButtonDown.class))
        {
            ArrayList<Box> next = new ArrayList<>();
            for (Box b : sponge) next.addAll(b.generate());
            sponge = next;
        }
        
        clear();
        stroke(255);
        noFill();
        translate(screenWidth() >> 1, screenHeight() >> 1);
        scale(0.5, 0.5);
        rectMode(RectMode.RADIUS);
        
        for (Box box : sponge)
        {
            box.show();
        }
    }
    
    @Override
    protected void destroy()
    {
    
    }
    
    public static void main(String[] args)
    {
        start(new CT002_MengerSponge());
    }
}