package engine.ct;

import engine.Engine;
import org.joml.Vector3d;

import java.util.ArrayList;

import static engine.util.Util.map;

@SuppressWarnings("unused")
public class CT004_PurpleRain extends Engine
{
    static class Drop
    {
        final Vector3d pos = new Vector3d(nextDouble(screenWidth()), nextDouble(-500, -50), nextDouble(20));
        final double   len = map(pos.z, 0, 20, 10, 20);
        double vel = map(pos.z, 0, 20, 1, 20);
        
        void fall()
        {
            this.pos.y += this.vel;
            this.vel += map(this.pos.z, 0, 20, 0, 0.2);
            
            if (this.pos.y > screenHeight())
            {
                this.pos.x = nextDouble(screenWidth());
                this.pos.y = nextDouble(-500, -50);
                this.vel   = map(this.pos.z, 0, 20, 4, 10);
            }
        }
        
        void show()
        {
            weight(map(this.pos.z, 0, 20, 1, 2));
            stroke(138, 43, 226, map(this.pos.z, 0, 20, 0.1, 0.9));
            line(this.pos.x, this.pos.y, this.pos.x, this.pos.y + this.len);
        }
    }
    
    final ArrayList<Drop> drops = new ArrayList<>();
    
    @Override
    protected void setup()
    {
        size(400, 300, 2, 2, OPENGL);
        
        frameRate(60);
        rendererBlend(true);
        
        for (int i = 0; i < 500; i++) drops.add(new Drop());
    }
    
    @Override
    protected void draw(double elapsedTime)
    {
        clear();
        
        for (Drop d : drops)
        {
            d.fall();
            d.show();
        }
    }
    
    @Override
    protected void destroy()
    {
        
    }
    
    public static void main(String[] args)
    {
        start(new CT004_PurpleRain());
    }
}