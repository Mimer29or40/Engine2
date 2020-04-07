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
            
            fill(255, alpha);
            noStroke();
            
            double sx = map(pos.x / pos.z, 0, 1, 0, screenWidth() / 2.);
            double sy = map(pos.y / pos.z, 0, 1, 0, screenHeight() / 2.);
            double r  = map(pos.z, 0, screenWidth(), 8, 0);
            circle(sx, sy, r);
            
            stroke(255, alpha);
            weight(r);
            
            double px = map(pos.x / prevZ, 0, 1, 0, screenWidth() / 2.);
            double py = map(pos.y / prevZ, 0, 1, 0, screenHeight() / 2.);
            line(px, py, sx, sy);
            
            prevZ = pos.z;
        }
    }
    
    double speed = 0;
    final ArrayList<Star> stars = new ArrayList<>();
    
    @Override
    protected void setup()
    {
        size(400, 400, 1, 1, OPENGL);
        
        frameRate(60);
        rendererBlend(true);
        
        for (int i = 0; i < 100; i++) stars.add(new Star());
    }
    
    @Override
    protected void draw(double elapsedTime)
    {
        speed = map(mouse().x(), 0, screenWidth(), 0, 50);
        
        clear(0);
        
        translate(screenWidth() / 2., screenHeight() / 2.);
        
        for (Star s : stars)
        {
            s.update();
            s.show();
        }
    }
    
    @Override
    protected void destroy()
    {
    
    }
    
    public static void main(String[] args)
    {
        start(new CT001_StarField());
    }
}
