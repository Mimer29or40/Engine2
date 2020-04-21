package engine.ct;

import engine.Engine;
import org.joml.Vector3d;

import java.util.ArrayList;

import static engine.util.Util.map;

@SuppressWarnings("unused")
public class CT001_StarField extends Engine
{
    final class Star
    {
        final Vector3d pos = new Vector3d();
        double prevZ;
        
        Star()
        {
            pos.x = nextDouble(-screenWidth() / 2., screenWidth() / 2.);
            pos.y = nextDouble(-screenHeight() / 2., screenHeight() / 2.);
            prevZ = pos.z = nextDouble(screenWidth());
        }
        
        void update()
        {
            pos.z -= speed;
            if (pos.z < 1)
            {
                pos.x = nextDouble(-screenWidth() / 2., screenWidth() / 2.);
                pos.y = nextDouble(-screenHeight() / 2., screenHeight() / 2.);
                prevZ = pos.z = nextDouble(screenWidth());
            }
        }
        
        void show()
        {
            double alpha = map(pos.z, 0, screenWidth(), 1, 0.1);
    
            profiler().startSection("Fill");
            fill(255, alpha);
            noStroke();
            profiler().endSection();
    
            profiler().startSection("Map");
            double sx = map(pos.x / pos.z, 0, 1, 0, screenWidth() / 2.);
            double sy = map(pos.y / pos.z, 0, 1, 0, screenHeight() / 2.);
            double px = map(pos.x / prevZ, 0, 1, 0, screenWidth() / 2.);
            double py = map(pos.y / prevZ, 0, 1, 0, screenHeight() / 2.);
            double r  = map(pos.z, 0, screenWidth(), 8, 0);
            profiler().endSection();
            
            profiler().startSection("Circles");
            circle(sx, sy, r);
            circle(px, py, r);
            profiler().endSection();
    
            profiler().startSection("Stroke");
            stroke(255, alpha);
            weight(r);
            profiler().endSection();
    
            profiler().startSection("Line");
            line(px, py, sx, sy);
            profiler().endSection();
            
            prevZ = pos.z;
        }
    }
    
    double speed = 0;
    final ArrayList<Star> stars = new ArrayList<>();
    
    @Override
    public void setup()
    {
        size(800, 800, 1, 1, OPENGL);
        // size(800, 800, 1, 1, SOFTWARE);
        
        frameRate(60);
        // rendererBlend(true);
        
        for (int i = 0; i < 100; i++) stars.add(new Star());
    }
    
    @Override
    public void draw(double elapsedTime)
    {
        speed = map(mouse().x(), 0, screenWidth(), 0, 50);
    
    
        profiler().startSection("Clear");
        clear(0);
        profiler().endSection();
        
        translate(screenWidth() / 2., screenHeight() / 2.);
    
        profiler().startSection("Stars");
        for (Star s : stars)
        {
            s.update();
            s.show();
        }
        profiler().endSection();
    }
    
    @Override
    public void destroy()
    {
    
    }
    
    public static void main(String[] args)
    {
        start(new CT001_StarField());
    }
}
