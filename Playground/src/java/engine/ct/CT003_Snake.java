package engine.ct;

import engine.Engine;
import org.joml.Vector2i;
import org.joml.Vector2ic;

import java.util.ArrayList;

import static engine.util.Util.println;

@SuppressWarnings("unused")
public class CT003_Snake extends Engine
{
    class Snake
    {
        final Vector2i pos = new Vector2i(0, 0);
        final Vector2i dir = new Vector2i(1, 0);
        int total;
        final ArrayList<Vector2i> tail = new ArrayList<>();
        
        boolean eat(Vector2ic pos)
        {
            if (this.pos.distance(pos) <= 1)
            {
                this.total += 1;
                return true;
            }
            return false;
        }
        
        void death()
        {
            for (Vector2i pos : this.tail)
            {
                if (this.pos.distance(pos) <= 1)
                {
                    println("Starting Over");
                    this.total = 0;
                    this.tail.clear();
                    break;
                }
            }
        }
        
        void update()
        {
            if (this.total > 0)
            {
                if (this.total == this.tail.size()) this.tail.remove(0);
                this.tail.add(new Vector2i(this.pos));
            }
            
            this.pos.add(this.dir.x * scale, this.dir.y * scale);
            
            if (this.pos.x < 0) this.pos.x = 0;
            if (this.pos.y < 0) this.pos.y = 0;
            if (this.pos.x > screenWidth() - scale) this.pos.x = screenWidth() - scale;
            if (this.pos.y > screenHeight() - scale) this.pos.y = screenHeight() - scale;
        }
        
        void show()
        {
            fill(255);
            for (Vector2i t : this.tail)
            {
                square(t.x, t.y, scale);
            }
            square(this.pos.x, this.pos.y, scale);
        }
    }
    
    void pickLocation()
    {
        food.set(nextInt(screenWidth() / scale) * scale, nextInt(screenHeight() / scale) * scale);
    }
    
    final int      scale = 20;
    final Snake    snake = new Snake();
    final Vector2i food  = new Vector2i();
    
    @Override
    protected void setup()
    {
        size(400, 400, 2, 2, OPENGL);
        
        frameRate(10);
        
        pickLocation();
    }
    
    @Override
    protected void draw(double elapsedTime)
    {
        if (keyboard().UP.down()) snake.dir.set(0, -1);
        if (keyboard().DOWN.down()) snake.dir.set(0, 1);
        if (keyboard().LEFT.down()) snake.dir.set(-1, 0);
        if (keyboard().RIGHT.down()) snake.dir.set(1, 0);
        
        clear();
        
        if (snake.eat(food)) pickLocation();
        
        weight(2);
        
        snake.death();
        snake.update();
        snake.show();
        
        fill(255, 0, 100);
        square(food.x, food.y, scale);
    }
    
    @Override
    protected void destroy()
    {
    
    }
    
    public static void main(String[] args)
    {
        start(new CT003_Snake());
    }
}
