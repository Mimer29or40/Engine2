package engine;

import engine.color.Color;

import java.util.ArrayList;

public class Fireworks extends Engine
{
    static class Particle
    {
        double posX = 0.0;
        double posY = 0.0;
        double velX = 0.0;
        double velY = 0.0;
        
        double fuse     = 0.0;
        double lifetime = 0.0;
        
        Color color;
        
        public void update(double elapsedTime)
        {
            double drag    = 0.999;
            double gravity = 9.81 * 7;
            
            double accX = -this.velX * drag;
            double accY = gravity - this.velY * drag;
            
            this.velX += accX * elapsedTime;
            this.velY += accY * elapsedTime;
            
            this.posX += this.velX * elapsedTime;
            this.posY += this.velY * elapsedTime;
            
            this.lifetime += elapsedTime;
        }
    }
    
    static class Firework extends Particle
    {
        final ArrayList<Particle> particles = new ArrayList<>();
        
        boolean exploded = false;
        
        public Firework()
        {
            this.posX = screenWidth() * 0.5 + nextDouble(-screenWidth() * 0.25, screenWidth() * 0.25);
            this.posY = screenHeight();
            
            this.velX = nextInt(-40, 40);
            this.velY = -nextInt(300, 500);
            
            this.color = nextColor(100, 255);
            
            this.fuse = nextDouble(3);
        }
        
        public void update(double elapsedTime)
        {
            super.update(elapsedTime);
            
            if (!this.exploded && this.lifetime >= this.fuse)
            {
                this.exploded = true;
                
                for (int i = 0; i < nextInt(50, 100); i++)
                {
                    Particle p = new Particle();
                    p.posX = this.posX;
                    p.posY = this.posY;
                    double angle = nextDouble(2 * Math.PI);
                    double power = nextDouble(160);
                    p.velX = this.velX + Math.cos(angle) * power;
                    p.velY = this.velY + Math.sin(angle) * power;
                    this.particles.add(p);
                }
            }
            
            if (this.exploded)
            {
                this.color.darker(0.999999999999999);
                for (Particle particle : this.particles)
                {
                    particle.update(elapsedTime);
                }
            }
            
            if (this.color.maxComponent() == 0) this.particles.clear();
        }
        
        public void draw()
        {
            push();
            noStroke();
            fill(this.color);
            if (this.lifetime < this.fuse)
            {
                circle(this.posX, this.posY, 5);
            }
            else
            {
                this.particles.forEach(particle -> circle(particle.posX, particle.posY, 3));
            }
            pop();
        }
    }
    
    final ArrayList<Firework> fireworks = new ArrayList<>();
    double timeTillNext = 0.0;
    
    @Override
    public void setup()
    {
        size(400, 400, 2, 2);
        frameRate(60);
    }
    
    @Override
    public void draw(double elapsedTime)
    {
        clear(Color.BLACK);
    
        if ((timeTillNext -= elapsedTime) <= 0)
        {
            fireworks.add(new Firework());
            timeTillNext = nextDouble();
        }
        
        if (mouse().held(Mouse.Button.LEFT)) fireworks.add(new Firework());
        
        for (Firework f : fireworks)
        {
            f.update(elapsedTime);
            f.draw();
        }
        
        fireworks.removeIf(firework -> firework.posY > screenHeight() || firework.color.maxComponent() == 0);
        
        // text("" + fireworks.size(), 3, 3);
    }
    
    public static void main(String[] args)
    {
        start(new Fireworks());
    }
}
