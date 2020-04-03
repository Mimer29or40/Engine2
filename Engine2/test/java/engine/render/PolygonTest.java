package engine.render;

import engine.Engine;
import org.joml.Vector2f;

import static engine.util.Util.println;

public class PolygonTest extends Engine
{
    float EPSILON = 0.0000000001f;
    
    Vector2f[] vertices;
    Vector2f   temp1 = new Vector2f(), temp2 = new Vector2f();
    
    int[] indices = new int[256];
    
    float wedge(Vector2f a, Vector2f b)
    {
        return a.x * b.y - a.y * b.x;
    }
    
    boolean valid_triangle(int n, int prev_i, int curr_i, int next_i)
    {
        Vector2f prev = vertices[prev_i];
        Vector2f curr = vertices[curr_i];
        Vector2f next = vertices[next_i];
        
        if (wedge(next.sub(curr, temp1), prev.sub(curr, temp2)) < EPSILON) return false;
        for (int p = 0; p < n; p++)
        {
            if (p == prev_i || p == curr_i || p == next_i) continue;
            if (wedge(curr.sub(prev, temp1), vertices[p].sub(prev, temp2)) >= EPSILON &&
                wedge(next.sub(curr, temp1), vertices[p].sub(curr, temp2)) >= EPSILON &&
                wedge(prev.sub(next, temp1), vertices[p].sub(next, temp2)) >= EPSILON) { return false; }
        }
        return true;
    }
    
    @Override
    protected void setup()
    {
        vertices = new Vector2f[]
                {
                        new Vector2f(0, 25),
                        new Vector2f(10, 0),
                        new Vector2f(30, 50),
                        new Vector2f(30, 0),
                        new Vector2f(40, 25),
                        new Vector2f(50, 0),
                        new Vector2f(50, 50),
                        new Vector2f(70, 0),
                        new Vector2f(80, 25),
                        new Vector2f(100, 100),
                        new Vector2f(0, 100),
                        };
        
        
        int n = vertices.length;
        
        float a = 0f;
        for (int p = n - 1, q = 0; q < n; p = q++)
        {
            a += wedge(vertices[p], vertices[q]);
        }
        if (a > 0.0)
        {
            for (int i = 0; i < n; i++) indices[i] = i;
        }
        else
        {
            for (int i = 0; i < n; i++) indices[i] = n - 1 - i;
        }
        
        int i = 0, count = 2 * n;
        while (n >= 3 && count > 0)
        {
            count--;
            int prev_i = indices[(i + n - 1) % n];
            int curr_i = indices[(i + n + 0) % n];
            int next_i = indices[(i + n + 1) % n];
            if (valid_triangle(n, prev_i, curr_i, next_i))
            {
                // gl_Position = pv * vec4(vertices[prev_i], 0.0, 1.0);
                // EmitVertex();
                //
                // gl_Position = pv * vec4(vertices[curr_i], 0.0, 1.0);
                // EmitVertex();
                //
                // gl_Position = pv * vec4(vertices[next_i], 0.0, 1.0);
                // EmitVertex();
                //
                // EndPrimitive();
                println(prev_i, curr_i, next_i);
                
                for (int s = i % n, t = i % n + 1; t < n; s = t++)
                {
                    indices[s] = indices[t];
                }
                n--;
                count = 2 * n;
            }
            else { i++; }
        }
    }
    
    @Override
    protected void destroy()
    {
    
    }
    
    public static void main(String[] args)
    {
        start(new PolygonTest());
    }
}
