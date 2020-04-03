package engine.render;

import org.joml.Vector2dc;
import org.joml.Vector2fc;
import org.joml.Vector2ic;

import java.util.ArrayList;

/**
 * This class is used to pass in joml Vectors to renderer methods.
 */
@SuppressWarnings("unused")
public class Overloads
{
    public static void translate(Renderer r, Vector2ic vector)
    {
        r.translate(vector.x(), vector.y());
    }
    
    public static void translate(Renderer r, Vector2fc vector)
    {
        r.translate(vector.x(), vector.y());
    }
    
    public static void translate(Renderer r, Vector2dc vector)
    {
        r.translate(vector.x(), vector.y());
    }
    
    public static void scale(Renderer r, Vector2ic vector)
    {
        r.scale(vector.x(), vector.y());
    }
    
    public static void scale(Renderer r, Vector2fc vector)
    {
        r.scale(vector.x(), vector.y());
    }
    
    public static void scale(Renderer r, Vector2dc vector)
    {
        r.scale(vector.x(), vector.y());
    }
    
    public static void point(Renderer r, Vector2ic p)
    {
        r.point(p.x(), p.y());
    }
    
    public static void point(Renderer r, Vector2fc p)
    {
        r.point(p.x(), p.y());
    }
    
    public static void point(Renderer r, Vector2dc p)
    {
        r.point(p.x(), p.y());
    }
    
    public static void line(Renderer r, double x1, double y1, Vector2ic p2)
    {
        r.line(x1, y1, p2.x(), p2.y());
    }
    
    public static void line(Renderer r, double x1, double y1, Vector2fc p2)
    {
        r.line(x1, y1, p2.x(), p2.y());
    }
    
    public static void line(Renderer r, double x1, double y1, Vector2dc p2)
    {
        r.line(x1, y1, p2.x(), p2.y());
    }
    
    public static void line(Renderer r, Vector2ic p1, double x2, double y2)
    {
        r.line(p1.x(), p1.y(), x2, y2);
    }
    
    public static void line(Renderer r, Vector2fc p1, double x2, double y2)
    {
        r.line(p1.x(), p1.y(), x2, y2);
    }
    
    public static void line(Renderer r, Vector2dc p1, double x2, double y2)
    {
        r.line(p1.x(), p1.y(), x2, y2);
    }
    
    public static void line(Renderer r, Vector2ic p1, Vector2ic p2)
    {
        r.line(p1.x(), p1.y(), p2.x(), p2.y());
    }
    
    public static void line(Renderer r, Vector2ic p1, Vector2fc p2)
    {
        r.line(p1.x(), p1.y(), p2.x(), p2.y());
    }
    
    public static void line(Renderer r, Vector2ic p1, Vector2dc p2)
    {
        r.line(p1.x(), p1.y(), p2.x(), p2.y());
    }
    
    public static void line(Renderer r, Vector2fc p1, Vector2ic p2)
    {
        r.line(p1.x(), p1.y(), p2.x(), p2.y());
    }
    
    public static void line(Renderer r, Vector2fc p1, Vector2fc p2)
    {
        r.line(p1.x(), p1.y(), p2.x(), p2.y());
    }
    
    public static void line(Renderer r, Vector2fc p1, Vector2dc p2)
    {
        r.line(p1.x(), p1.y(), p2.x(), p2.y());
    }
    
    public static void line(Renderer r, Vector2dc p1, Vector2ic p2)
    {
        r.line(p1.x(), p1.y(), p2.x(), p2.y());
    }
    
    public static void line(Renderer r, Vector2dc p1, Vector2fc p2)
    {
        r.line(p1.x(), p1.y(), p2.x(), p2.y());
    }
    
    public static void line(Renderer r, Vector2dc p1, Vector2dc p2)
    {
        r.line(p1.x(), p1.y(), p2.x(), p2.y());
    }
    
    public static void bezier(Renderer r, double x1, double y1, double x2, double y2, Vector2ic p3)
    {
        r.bezier(x1, y1, x2, y2, p3.x(), p3.y());
    }
    
    public static void bezier(Renderer r, double x1, double y1, double x2, double y2, Vector2fc p3)
    {
        r.bezier(x1, y1, x2, y2, p3.x(), p3.y());
    }
    
    public static void bezier(Renderer r, double x1, double y1, double x2, double y2, Vector2dc p3)
    {
        r.bezier(x1, y1, x2, y2, p3.x(), p3.y());
    }
    
    public static void bezier(Renderer r, double x1, double y1, Vector2ic p2, double x3, double y3)
    {
        r.bezier(x1, y1, p2.x(), p2.y(), x3, y3);
    }
    
    public static void bezier(Renderer r, double x1, double y1, Vector2fc p2, double x3, double y3)
    {
        r.bezier(x1, y1, p2.x(), p2.y(), x3, y3);
    }
    
    public static void bezier(Renderer r, double x1, double y1, Vector2dc p2, double x3, double y3)
    {
        r.bezier(x1, y1, p2.x(), p2.y(), x3, y3);
    }
    
    public static void bezier(Renderer r, Vector2ic p1, double x2, double y2, double x3, double y3)
    {
        r.bezier(p1.x(), p1.y(), x2, y2, x3, y3);
    }
    
    public static void bezier(Renderer r, Vector2fc p1, double x2, double y2, double x3, double y3)
    {
        r.bezier(p1.x(), p1.y(), x2, y2, x3, y3);
    }
    
    public static void bezier(Renderer r, Vector2dc p1, double x2, double y2, double x3, double y3)
    {
        r.bezier(p1.x(), p1.y(), x2, y2, x3, y3);
    }
    
    public static void bezier(Renderer r, double x1, double y1, Vector2ic p2, Vector2ic p3)
    {
        r.bezier(x1, y1, p2.x(), p2.y(), p3.x(), p3.y());
    }
    
    public static void bezier(Renderer r, double x1, double y1, Vector2ic p2, Vector2fc p3)
    {
        r.bezier(x1, y1, p2.x(), p2.y(), p3.x(), p3.y());
    }
    
    public static void bezier(Renderer r, double x1, double y1, Vector2ic p2, Vector2dc p3)
    {
        r.bezier(x1, y1, p2.x(), p2.y(), p3.x(), p3.y());
    }
    
    public static void bezier(Renderer r, double x1, double y1, Vector2fc p2, Vector2ic p3)
    {
        r.bezier(x1, y1, p2.x(), p2.y(), p3.x(), p3.y());
    }
    
    public static void bezier(Renderer r, double x1, double y1, Vector2fc p2, Vector2fc p3)
    {
        r.bezier(x1, y1, p2.x(), p2.y(), p3.x(), p3.y());
    }
    
    public static void bezier(Renderer r, double x1, double y1, Vector2fc p2, Vector2dc p3)
    {
        r.bezier(x1, y1, p2.x(), p2.y(), p3.x(), p3.y());
    }
    
    public static void bezier(Renderer r, double x1, double y1, Vector2dc p2, Vector2ic p3)
    {
        r.bezier(x1, y1, p2.x(), p2.y(), p3.x(), p3.y());
    }
    
    public static void bezier(Renderer r, double x1, double y1, Vector2dc p2, Vector2fc p3)
    {
        r.bezier(x1, y1, p2.x(), p2.y(), p3.x(), p3.y());
    }
    
    public static void bezier(Renderer r, double x1, double y1, Vector2dc p2, Vector2dc p3)
    {
        r.bezier(x1, y1, p2.x(), p2.y(), p3.x(), p3.y());
    }
    
    public static void bezier(Renderer r, Vector2ic p1, double x2, double y2, Vector2ic p3)
    {
        r.bezier(p1.x(), p1.y(), x2, y2, p3.x(), p3.y());
    }
    
    public static void bezier(Renderer r, Vector2ic p1, double x2, double y2, Vector2fc p3)
    {
        r.bezier(p1.x(), p1.y(), x2, y2, p3.x(), p3.y());
    }
    
    public static void bezier(Renderer r, Vector2ic p1, double x2, double y2, Vector2dc p3)
    {
        r.bezier(p1.x(), p1.y(), x2, y2, p3.x(), p3.y());
    }
    
    public static void bezier(Renderer r, Vector2fc p1, double x2, double y2, Vector2ic p3)
    {
        r.bezier(p1.x(), p1.y(), x2, y2, p3.x(), p3.y());
    }
    
    public static void bezier(Renderer r, Vector2fc p1, double x2, double y2, Vector2fc p3)
    {
        r.bezier(p1.x(), p1.y(), x2, y2, p3.x(), p3.y());
    }
    
    public static void bezier(Renderer r, Vector2fc p1, double x2, double y2, Vector2dc p3)
    {
        r.bezier(p1.x(), p1.y(), x2, y2, p3.x(), p3.y());
    }
    
    public static void bezier(Renderer r, Vector2dc p1, double x2, double y2, Vector2ic p3)
    {
        r.bezier(p1.x(), p1.y(), x2, y2, p3.x(), p3.y());
    }
    
    public static void bezier(Renderer r, Vector2dc p1, double x2, double y2, Vector2fc p3)
    {
        r.bezier(p1.x(), p1.y(), x2, y2, p3.x(), p3.y());
    }
    
    public static void bezier(Renderer r, Vector2dc p1, double x2, double y2, Vector2dc p3)
    {
        r.bezier(p1.x(), p1.y(), x2, y2, p3.x(), p3.y());
    }
    
    public static void bezier(Renderer r, Vector2ic p1, Vector2ic p2, double x3, double y3)
    {
        r.bezier(p1.x(), p1.y(), p2.x(), p2.y(), x3, y3);
    }
    
    public static void bezier(Renderer r, Vector2ic p1, Vector2fc p2, double x3, double y3)
    {
        r.bezier(p1.x(), p1.y(), p2.x(), p2.y(), x3, y3);
    }
    
    public static void bezier(Renderer r, Vector2ic p1, Vector2dc p2, double x3, double y3)
    {
        r.bezier(p1.x(), p1.y(), p2.x(), p2.y(), x3, y3);
    }
    
    public static void bezier(Renderer r, Vector2fc p1, Vector2ic p2, double x3, double y3)
    {
        r.bezier(p1.x(), p1.y(), p2.x(), p2.y(), x3, y3);
    }
    
    public static void bezier(Renderer r, Vector2fc p1, Vector2fc p2, double x3, double y3)
    {
        r.bezier(p1.x(), p1.y(), p2.x(), p2.y(), x3, y3);
    }
    
    public static void bezier(Renderer r, Vector2fc p1, Vector2dc p2, double x3, double y3)
    {
        r.bezier(p1.x(), p1.y(), p2.x(), p2.y(), x3, y3);
    }
    
    public static void bezier(Renderer r, Vector2dc p1, Vector2ic p2, double x3, double y3)
    {
        r.bezier(p1.x(), p1.y(), p2.x(), p2.y(), x3, y3);
    }
    
    public static void bezier(Renderer r, Vector2dc p1, Vector2fc p2, double x3, double y3)
    {
        r.bezier(p1.x(), p1.y(), p2.x(), p2.y(), x3, y3);
    }
    
    public static void bezier(Renderer r, Vector2dc p1, Vector2dc p2, double x3, double y3)
    {
        r.bezier(p1.x(), p1.y(), p2.x(), p2.y(), x3, y3);
    }
    
    public static void bezier(Renderer r, Vector2ic p1, Vector2ic p2, Vector2ic p3)
    {
        r.bezier(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y());
    }
    
    public static void bezier(Renderer r, Vector2ic p1, Vector2ic p2, Vector2fc p3)
    {
        r.bezier(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y());
    }
    
    public static void bezier(Renderer r, Vector2ic p1, Vector2ic p2, Vector2dc p3)
    {
        r.bezier(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y());
    }
    
    public static void bezier(Renderer r, Vector2ic p1, Vector2fc p2, Vector2ic p3)
    {
        r.bezier(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y());
    }
    
    public static void bezier(Renderer r, Vector2ic p1, Vector2fc p2, Vector2fc p3)
    {
        r.bezier(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y());
    }
    
    public static void bezier(Renderer r, Vector2ic p1, Vector2fc p2, Vector2dc p3)
    {
        r.bezier(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y());
    }
    
    public static void bezier(Renderer r, Vector2ic p1, Vector2dc p2, Vector2ic p3)
    {
        r.bezier(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y());
    }
    
    public static void bezier(Renderer r, Vector2ic p1, Vector2dc p2, Vector2fc p3)
    {
        r.bezier(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y());
    }
    
    public static void bezier(Renderer r, Vector2ic p1, Vector2dc p2, Vector2dc p3)
    {
        r.bezier(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y());
    }
    
    public static void bezier(Renderer r, Vector2fc p1, Vector2ic p2, Vector2ic p3)
    {
        r.bezier(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y());
    }
    
    public static void bezier(Renderer r, Vector2fc p1, Vector2ic p2, Vector2fc p3)
    {
        r.bezier(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y());
    }
    
    public static void bezier(Renderer r, Vector2fc p1, Vector2ic p2, Vector2dc p3)
    {
        r.bezier(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y());
    }
    
    public static void bezier(Renderer r, Vector2fc p1, Vector2fc p2, Vector2ic p3)
    {
        r.bezier(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y());
    }
    
    public static void bezier(Renderer r, Vector2fc p1, Vector2fc p2, Vector2fc p3)
    {
        r.bezier(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y());
    }
    
    public static void bezier(Renderer r, Vector2fc p1, Vector2fc p2, Vector2dc p3)
    {
        r.bezier(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y());
    }
    
    public static void bezier(Renderer r, Vector2fc p1, Vector2dc p2, Vector2ic p3)
    {
        r.bezier(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y());
    }
    
    public static void bezier(Renderer r, Vector2fc p1, Vector2dc p2, Vector2fc p3)
    {
        r.bezier(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y());
    }
    
    public static void bezier(Renderer r, Vector2fc p1, Vector2dc p2, Vector2dc p3)
    {
        r.bezier(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y());
    }
    
    public static void bezier(Renderer r, Vector2dc p1, Vector2ic p2, Vector2ic p3)
    {
        r.bezier(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y());
    }
    
    public static void bezier(Renderer r, Vector2dc p1, Vector2ic p2, Vector2fc p3)
    {
        r.bezier(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y());
    }
    
    public static void bezier(Renderer r, Vector2dc p1, Vector2ic p2, Vector2dc p3)
    {
        r.bezier(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y());
    }
    
    public static void bezier(Renderer r, Vector2dc p1, Vector2fc p2, Vector2ic p3)
    {
        r.bezier(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y());
    }
    
    public static void bezier(Renderer r, Vector2dc p1, Vector2fc p2, Vector2fc p3)
    {
        r.bezier(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y());
    }
    
    public static void bezier(Renderer r, Vector2dc p1, Vector2fc p2, Vector2dc p3)
    {
        r.bezier(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y());
    }
    
    public static void bezier(Renderer r, Vector2dc p1, Vector2dc p2, Vector2ic p3)
    {
        r.bezier(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y());
    }
    
    public static void bezier(Renderer r, Vector2dc p1, Vector2dc p2, Vector2fc p3)
    {
        r.bezier(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y());
    }
    
    public static void bezier(Renderer r, Vector2dc p1, Vector2dc p2, Vector2dc p3)
    {
        r.bezier(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y());
    }
    
    public static void triangle(Renderer r, double x1, double y1, double x2, double y2, Vector2ic p3)
    {
        r.triangle(x1, y1, x2, y2, p3.x(), p3.y());
    }
    
    public static void triangle(Renderer r, double x1, double y1, double x2, double y2, Vector2fc p3)
    {
        r.triangle(x1, y1, x2, y2, p3.x(), p3.y());
    }
    
    public static void triangle(Renderer r, double x1, double y1, double x2, double y2, Vector2dc p3)
    {
        r.triangle(x1, y1, x2, y2, p3.x(), p3.y());
    }
    
    public static void triangle(Renderer r, double x1, double y1, Vector2ic p2, double x3, double y3)
    {
        r.triangle(x1, y1, p2.x(), p2.y(), x3, y3);
    }
    
    public static void triangle(Renderer r, double x1, double y1, Vector2fc p2, double x3, double y3)
    {
        r.triangle(x1, y1, p2.x(), p2.y(), x3, y3);
    }
    
    public static void triangle(Renderer r, double x1, double y1, Vector2dc p2, double x3, double y3)
    {
        r.triangle(x1, y1, p2.x(), p2.y(), x3, y3);
    }
    
    public static void triangle(Renderer r, Vector2ic p1, double x2, double y2, double x3, double y3)
    {
        r.triangle(p1.x(), p1.y(), x2, y2, x3, y3);
    }
    
    public static void triangle(Renderer r, Vector2fc p1, double x2, double y2, double x3, double y3)
    {
        r.triangle(p1.x(), p1.y(), x2, y2, x3, y3);
    }
    
    public static void triangle(Renderer r, Vector2dc p1, double x2, double y2, double x3, double y3)
    {
        r.triangle(p1.x(), p1.y(), x2, y2, x3, y3);
    }
    
    public static void triangle(Renderer r, double x1, double y1, Vector2ic p2, Vector2ic p3)
    {
        r.triangle(x1, y1, p2.x(), p2.y(), p3.x(), p3.y());
    }
    
    public static void triangle(Renderer r, double x1, double y1, Vector2ic p2, Vector2fc p3)
    {
        r.triangle(x1, y1, p2.x(), p2.y(), p3.x(), p3.y());
    }
    
    public static void triangle(Renderer r, double x1, double y1, Vector2ic p2, Vector2dc p3)
    {
        r.triangle(x1, y1, p2.x(), p2.y(), p3.x(), p3.y());
    }
    
    public static void triangle(Renderer r, double x1, double y1, Vector2fc p2, Vector2ic p3)
    {
        r.triangle(x1, y1, p2.x(), p2.y(), p3.x(), p3.y());
    }
    
    public static void triangle(Renderer r, double x1, double y1, Vector2fc p2, Vector2fc p3)
    {
        r.triangle(x1, y1, p2.x(), p2.y(), p3.x(), p3.y());
    }
    
    public static void triangle(Renderer r, double x1, double y1, Vector2fc p2, Vector2dc p3)
    {
        r.triangle(x1, y1, p2.x(), p2.y(), p3.x(), p3.y());
    }
    
    public static void triangle(Renderer r, double x1, double y1, Vector2dc p2, Vector2ic p3)
    {
        r.triangle(x1, y1, p2.x(), p2.y(), p3.x(), p3.y());
    }
    
    public static void triangle(Renderer r, double x1, double y1, Vector2dc p2, Vector2fc p3)
    {
        r.triangle(x1, y1, p2.x(), p2.y(), p3.x(), p3.y());
    }
    
    public static void triangle(Renderer r, double x1, double y1, Vector2dc p2, Vector2dc p3)
    {
        r.triangle(x1, y1, p2.x(), p2.y(), p3.x(), p3.y());
    }
    
    public static void triangle(Renderer r, Vector2ic p1, double x2, double y2, Vector2ic p3)
    {
        r.triangle(p1.x(), p1.y(), x2, y2, p3.x(), p3.y());
    }
    
    public static void triangle(Renderer r, Vector2ic p1, double x2, double y2, Vector2fc p3)
    {
        r.triangle(p1.x(), p1.y(), x2, y2, p3.x(), p3.y());
    }
    
    public static void triangle(Renderer r, Vector2ic p1, double x2, double y2, Vector2dc p3)
    {
        r.triangle(p1.x(), p1.y(), x2, y2, p3.x(), p3.y());
    }
    
    public static void triangle(Renderer r, Vector2fc p1, double x2, double y2, Vector2ic p3)
    {
        r.triangle(p1.x(), p1.y(), x2, y2, p3.x(), p3.y());
    }
    
    public static void triangle(Renderer r, Vector2fc p1, double x2, double y2, Vector2fc p3)
    {
        r.triangle(p1.x(), p1.y(), x2, y2, p3.x(), p3.y());
    }
    
    public static void triangle(Renderer r, Vector2fc p1, double x2, double y2, Vector2dc p3)
    {
        r.triangle(p1.x(), p1.y(), x2, y2, p3.x(), p3.y());
    }
    
    public static void triangle(Renderer r, Vector2dc p1, double x2, double y2, Vector2ic p3)
    {
        r.triangle(p1.x(), p1.y(), x2, y2, p3.x(), p3.y());
    }
    
    public static void triangle(Renderer r, Vector2dc p1, double x2, double y2, Vector2fc p3)
    {
        r.triangle(p1.x(), p1.y(), x2, y2, p3.x(), p3.y());
    }
    
    public static void triangle(Renderer r, Vector2dc p1, double x2, double y2, Vector2dc p3)
    {
        r.triangle(p1.x(), p1.y(), x2, y2, p3.x(), p3.y());
    }
    
    public static void triangle(Renderer r, Vector2ic p1, Vector2ic p2, double x3, double y3)
    {
        r.triangle(p1.x(), p1.y(), p2.x(), p2.y(), x3, y3);
    }
    
    public static void triangle(Renderer r, Vector2ic p1, Vector2fc p2, double x3, double y3)
    {
        r.triangle(p1.x(), p1.y(), p2.x(), p2.y(), x3, y3);
    }
    
    public static void triangle(Renderer r, Vector2ic p1, Vector2dc p2, double x3, double y3)
    {
        r.triangle(p1.x(), p1.y(), p2.x(), p2.y(), x3, y3);
    }
    
    public static void triangle(Renderer r, Vector2fc p1, Vector2ic p2, double x3, double y3)
    {
        r.triangle(p1.x(), p1.y(), p2.x(), p2.y(), x3, y3);
    }
    
    public static void triangle(Renderer r, Vector2fc p1, Vector2fc p2, double x3, double y3)
    {
        r.triangle(p1.x(), p1.y(), p2.x(), p2.y(), x3, y3);
    }
    
    public static void triangle(Renderer r, Vector2fc p1, Vector2dc p2, double x3, double y3)
    {
        r.triangle(p1.x(), p1.y(), p2.x(), p2.y(), x3, y3);
    }
    
    public static void triangle(Renderer r, Vector2dc p1, Vector2ic p2, double x3, double y3)
    {
        r.triangle(p1.x(), p1.y(), p2.x(), p2.y(), x3, y3);
    }
    
    public static void triangle(Renderer r, Vector2dc p1, Vector2fc p2, double x3, double y3)
    {
        r.triangle(p1.x(), p1.y(), p2.x(), p2.y(), x3, y3);
    }
    
    public static void triangle(Renderer r, Vector2dc p1, Vector2dc p2, double x3, double y3)
    {
        r.triangle(p1.x(), p1.y(), p2.x(), p2.y(), x3, y3);
    }
    
    public static void triangle(Renderer r, Vector2ic p1, Vector2ic p2, Vector2ic p3)
    {
        r.triangle(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y());
    }
    
    public static void triangle(Renderer r, Vector2ic p1, Vector2ic p2, Vector2fc p3)
    {
        r.triangle(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y());
    }
    
    public static void triangle(Renderer r, Vector2ic p1, Vector2ic p2, Vector2dc p3)
    {
        r.triangle(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y());
    }
    
    public static void triangle(Renderer r, Vector2ic p1, Vector2fc p2, Vector2ic p3)
    {
        r.triangle(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y());
    }
    
    public static void triangle(Renderer r, Vector2ic p1, Vector2fc p2, Vector2fc p3)
    {
        r.triangle(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y());
    }
    
    public static void triangle(Renderer r, Vector2ic p1, Vector2fc p2, Vector2dc p3)
    {
        r.triangle(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y());
    }
    
    public static void triangle(Renderer r, Vector2ic p1, Vector2dc p2, Vector2ic p3)
    {
        r.triangle(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y());
    }
    
    public static void triangle(Renderer r, Vector2ic p1, Vector2dc p2, Vector2fc p3)
    {
        r.triangle(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y());
    }
    
    public static void triangle(Renderer r, Vector2ic p1, Vector2dc p2, Vector2dc p3)
    {
        r.triangle(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y());
    }
    
    public static void triangle(Renderer r, Vector2fc p1, Vector2ic p2, Vector2ic p3)
    {
        r.triangle(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y());
    }
    
    public static void triangle(Renderer r, Vector2fc p1, Vector2ic p2, Vector2fc p3)
    {
        r.triangle(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y());
    }
    
    public static void triangle(Renderer r, Vector2fc p1, Vector2ic p2, Vector2dc p3)
    {
        r.triangle(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y());
    }
    
    public static void triangle(Renderer r, Vector2fc p1, Vector2fc p2, Vector2ic p3)
    {
        r.triangle(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y());
    }
    
    public static void triangle(Renderer r, Vector2fc p1, Vector2fc p2, Vector2fc p3)
    {
        r.triangle(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y());
    }
    
    public static void triangle(Renderer r, Vector2fc p1, Vector2fc p2, Vector2dc p3)
    {
        r.triangle(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y());
    }
    
    public static void triangle(Renderer r, Vector2fc p1, Vector2dc p2, Vector2ic p3)
    {
        r.triangle(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y());
    }
    
    public static void triangle(Renderer r, Vector2fc p1, Vector2dc p2, Vector2fc p3)
    {
        r.triangle(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y());
    }
    
    public static void triangle(Renderer r, Vector2fc p1, Vector2dc p2, Vector2dc p3)
    {
        r.triangle(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y());
    }
    
    public static void triangle(Renderer r, Vector2dc p1, Vector2ic p2, Vector2ic p3)
    {
        r.triangle(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y());
    }
    
    public static void triangle(Renderer r, Vector2dc p1, Vector2ic p2, Vector2fc p3)
    {
        r.triangle(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y());
    }
    
    public static void triangle(Renderer r, Vector2dc p1, Vector2ic p2, Vector2dc p3)
    {
        r.triangle(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y());
    }
    
    public static void triangle(Renderer r, Vector2dc p1, Vector2fc p2, Vector2ic p3)
    {
        r.triangle(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y());
    }
    
    public static void triangle(Renderer r, Vector2dc p1, Vector2fc p2, Vector2fc p3)
    {
        r.triangle(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y());
    }
    
    public static void triangle(Renderer r, Vector2dc p1, Vector2fc p2, Vector2dc p3)
    {
        r.triangle(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y());
    }
    
    public static void triangle(Renderer r, Vector2dc p1, Vector2dc p2, Vector2ic p3)
    {
        r.triangle(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y());
    }
    
    public static void triangle(Renderer r, Vector2dc p1, Vector2dc p2, Vector2fc p3)
    {
        r.triangle(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y());
    }
    
    public static void triangle(Renderer r, Vector2dc p1, Vector2dc p2, Vector2dc p3)
    {
        r.triangle(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y());
    }
    
    public static void square(Renderer r, Vector2ic ab, double c)
    {
        r.square(ab.x(), ab.y(), c);
    }
    
    public static void square(Renderer r, Vector2fc ab, double c)
    {
        r.square(ab.x(), ab.y(), c);
    }
    
    public static void square(Renderer r, Vector2dc ab, double c)
    {
        r.square(ab.x(), ab.y(), c);
    }
    
    public static void rect(Renderer r, double a, double b, Vector2ic cd)
    {
        r.rect(a, b, cd.x(), cd.y());
    }
    
    public static void rect(Renderer r, double a, double b, Vector2fc cd)
    {
        r.rect(a, b, cd.x(), cd.y());
    }
    
    public static void rect(Renderer r, double a, double b, Vector2dc cd)
    {
        r.rect(a, b, cd.x(), cd.y());
    }
    
    public static void rect(Renderer r, Vector2ic ab, double c, double d)
    {
        r.rect(ab.x(), ab.y(), c, d);
    }
    
    public static void rect(Renderer r, Vector2fc ab, double c, double d)
    {
        r.rect(ab.x(), ab.y(), c, d);
    }
    
    public static void rect(Renderer r, Vector2dc ab, double c, double d)
    {
        r.rect(ab.x(), ab.y(), c, d);
    }
    
    public static void rect(Renderer r, Vector2ic ab, Vector2ic cd)
    {
        r.rect(ab.x(), ab.y(), cd.x(), cd.y());
    }
    
    public static void rect(Renderer r, Vector2ic ab, Vector2fc cd)
    {
        r.rect(ab.x(), ab.y(), cd.x(), cd.y());
    }
    
    public static void rect(Renderer r, Vector2ic ab, Vector2dc cd)
    {
        r.rect(ab.x(), ab.y(), cd.x(), cd.y());
    }
    
    public static void rect(Renderer r, Vector2fc ab, Vector2ic cd)
    {
        r.rect(ab.x(), ab.y(), cd.x(), cd.y());
    }
    
    public static void rect(Renderer r, Vector2fc ab, Vector2fc cd)
    {
        r.rect(ab.x(), ab.y(), cd.x(), cd.y());
    }
    
    public static void rect(Renderer r, Vector2fc ab, Vector2dc cd)
    {
        r.rect(ab.x(), ab.y(), cd.x(), cd.y());
    }
    
    public static void rect(Renderer r, Vector2dc ab, Vector2ic cd)
    {
        r.rect(ab.x(), ab.y(), cd.x(), cd.y());
    }
    
    public static void rect(Renderer r, Vector2dc ab, Vector2fc cd)
    {
        r.rect(ab.x(), ab.y(), cd.x(), cd.y());
    }
    
    public static void rect(Renderer r, Vector2dc ab, Vector2dc cd)
    {
        r.rect(ab.x(), ab.y(), cd.x(), cd.y());
    }
    
    public static void quad(Renderer r, double x1, double y1, double x2, double y2, double x3, double y3, Vector2ic p4)
    {
        r.quad(x1, y1, x2, y2, x3, y3, p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, double x1, double y1, double x2, double y2, double x3, double y3, Vector2fc p4)
    {
        r.quad(x1, y1, x2, y2, x3, y3, p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, double x1, double y1, double x2, double y2, double x3, double y3, Vector2dc p4)
    {
        r.quad(x1, y1, x2, y2, x3, y3, p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, double x1, double y1, double x2, double y2, Vector2ic p3, double x4, double y4)
    {
        r.quad(x1, y1, x2, y2, p3.x(), p3.y(), x4, y4);
    }
    
    public static void quad(Renderer r, double x1, double y1, double x2, double y2, Vector2fc p3, double x4, double y4)
    {
        r.quad(x1, y1, x2, y2, p3.x(), p3.y(), x4, y4);
    }
    
    public static void quad(Renderer r, double x1, double y1, double x2, double y2, Vector2dc p3, double x4, double y4)
    {
        r.quad(x1, y1, x2, y2, p3.x(), p3.y(), x4, y4);
    }
    
    public static void quad(Renderer r, double x1, double y1, double x2, double y2, Vector2ic p3, Vector2ic p4)
    {
        r.quad(x1, y1, x2, y2, p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, double x1, double y1, double x2, double y2, Vector2ic p3, Vector2fc p4)
    {
        r.quad(x1, y1, x2, y2, p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, double x1, double y1, double x2, double y2, Vector2ic p3, Vector2dc p4)
    {
        r.quad(x1, y1, x2, y2, p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, double x1, double y1, double x2, double y2, Vector2fc p3, Vector2ic p4)
    {
        r.quad(x1, y1, x2, y2, p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, double x1, double y1, double x2, double y2, Vector2fc p3, Vector2fc p4)
    {
        r.quad(x1, y1, x2, y2, p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, double x1, double y1, double x2, double y2, Vector2fc p3, Vector2dc p4)
    {
        r.quad(x1, y1, x2, y2, p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, double x1, double y1, double x2, double y2, Vector2dc p3, Vector2ic p4)
    {
        r.quad(x1, y1, x2, y2, p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, double x1, double y1, double x2, double y2, Vector2dc p3, Vector2fc p4)
    {
        r.quad(x1, y1, x2, y2, p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, double x1, double y1, double x2, double y2, Vector2dc p3, Vector2dc p4)
    {
        r.quad(x1, y1, x2, y2, p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, double x1, double y1, Vector2ic p2, double x3, double y3, Vector2ic p4)
    {
        r.quad(x1, y1, p2.x(), p2.y(), x3, y3, p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, double x1, double y1, Vector2ic p2, double x3, double y3, Vector2fc p4)
    {
        r.quad(x1, y1, p2.x(), p2.y(), x3, y3, p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, double x1, double y1, Vector2ic p2, double x3, double y3, Vector2dc p4)
    {
        r.quad(x1, y1, p2.x(), p2.y(), x3, y3, p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, double x1, double y1, Vector2fc p2, double x3, double y3, Vector2ic p4)
    {
        r.quad(x1, y1, p2.x(), p2.y(), x3, y3, p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, double x1, double y1, Vector2fc p2, double x3, double y3, Vector2fc p4)
    {
        r.quad(x1, y1, p2.x(), p2.y(), x3, y3, p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, double x1, double y1, Vector2fc p2, double x3, double y3, Vector2dc p4)
    {
        r.quad(x1, y1, p2.x(), p2.y(), x3, y3, p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, double x1, double y1, Vector2dc p2, double x3, double y3, Vector2ic p4)
    {
        r.quad(x1, y1, p2.x(), p2.y(), x3, y3, p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, double x1, double y1, Vector2dc p2, double x3, double y3, Vector2fc p4)
    {
        r.quad(x1, y1, p2.x(), p2.y(), x3, y3, p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, double x1, double y1, Vector2dc p2, double x3, double y3, Vector2dc p4)
    {
        r.quad(x1, y1, p2.x(), p2.y(), x3, y3, p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, double x1, double y1, Vector2ic p2, Vector2ic p3, double x4, double y4)
    {
        r.quad(x1, y1, p2.x(), p2.y(), p3.x(), p3.y(), x4, y4);
    }
    
    public static void quad(Renderer r, double x1, double y1, Vector2ic p2, Vector2fc p3, double x4, double y4)
    {
        r.quad(x1, y1, p2.x(), p2.y(), p3.x(), p3.y(), x4, y4);
    }
    
    public static void quad(Renderer r, double x1, double y1, Vector2ic p2, Vector2dc p3, double x4, double y4)
    {
        r.quad(x1, y1, p2.x(), p2.y(), p3.x(), p3.y(), x4, y4);
    }
    
    public static void quad(Renderer r, double x1, double y1, Vector2fc p2, Vector2ic p3, double x4, double y4)
    {
        r.quad(x1, y1, p2.x(), p2.y(), p3.x(), p3.y(), x4, y4);
    }
    
    public static void quad(Renderer r, double x1, double y1, Vector2fc p2, Vector2fc p3, double x4, double y4)
    {
        r.quad(x1, y1, p2.x(), p2.y(), p3.x(), p3.y(), x4, y4);
    }
    
    public static void quad(Renderer r, double x1, double y1, Vector2fc p2, Vector2dc p3, double x4, double y4)
    {
        r.quad(x1, y1, p2.x(), p2.y(), p3.x(), p3.y(), x4, y4);
    }
    
    public static void quad(Renderer r, double x1, double y1, Vector2dc p2, Vector2ic p3, double x4, double y4)
    {
        r.quad(x1, y1, p2.x(), p2.y(), p3.x(), p3.y(), x4, y4);
    }
    
    public static void quad(Renderer r, double x1, double y1, Vector2dc p2, Vector2fc p3, double x4, double y4)
    {
        r.quad(x1, y1, p2.x(), p2.y(), p3.x(), p3.y(), x4, y4);
    }
    
    public static void quad(Renderer r, double x1, double y1, Vector2dc p2, Vector2dc p3, double x4, double y4)
    {
        r.quad(x1, y1, p2.x(), p2.y(), p3.x(), p3.y(), x4, y4);
    }
    
    public static void quad(Renderer r, Vector2ic p1, double x2, double y2, double x3, double y3, Vector2ic p4)
    {
        r.quad(p1.x(), p1.y(), x2, y2, x3, y3, p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2ic p1, double x2, double y2, double x3, double y3, Vector2fc p4)
    {
        r.quad(p1.x(), p1.y(), x2, y2, x3, y3, p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2ic p1, double x2, double y2, double x3, double y3, Vector2dc p4)
    {
        r.quad(p1.x(), p1.y(), x2, y2, x3, y3, p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2fc p1, double x2, double y2, double x3, double y3, Vector2ic p4)
    {
        r.quad(p1.x(), p1.y(), x2, y2, x3, y3, p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2fc p1, double x2, double y2, double x3, double y3, Vector2fc p4)
    {
        r.quad(p1.x(), p1.y(), x2, y2, x3, y3, p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2fc p1, double x2, double y2, double x3, double y3, Vector2dc p4)
    {
        r.quad(p1.x(), p1.y(), x2, y2, x3, y3, p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2dc p1, double x2, double y2, double x3, double y3, Vector2ic p4)
    {
        r.quad(p1.x(), p1.y(), x2, y2, x3, y3, p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2dc p1, double x2, double y2, double x3, double y3, Vector2fc p4)
    {
        r.quad(p1.x(), p1.y(), x2, y2, x3, y3, p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2dc p1, double x2, double y2, double x3, double y3, Vector2dc p4)
    {
        r.quad(p1.x(), p1.y(), x2, y2, x3, y3, p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2ic p1, double x2, double y2, Vector2ic p3, double x4, double y4)
    {
        r.quad(p1.x(), p1.y(), x2, y2, p3.x(), p3.y(), x4, y4);
    }
    
    public static void quad(Renderer r, Vector2ic p1, double x2, double y2, Vector2fc p3, double x4, double y4)
    {
        r.quad(p1.x(), p1.y(), x2, y2, p3.x(), p3.y(), x4, y4);
    }
    
    public static void quad(Renderer r, Vector2ic p1, double x2, double y2, Vector2dc p3, double x4, double y4)
    {
        r.quad(p1.x(), p1.y(), x2, y2, p3.x(), p3.y(), x4, y4);
    }
    
    public static void quad(Renderer r, Vector2fc p1, double x2, double y2, Vector2ic p3, double x4, double y4)
    {
        r.quad(p1.x(), p1.y(), x2, y2, p3.x(), p3.y(), x4, y4);
    }
    
    public static void quad(Renderer r, Vector2fc p1, double x2, double y2, Vector2fc p3, double x4, double y4)
    {
        r.quad(p1.x(), p1.y(), x2, y2, p3.x(), p3.y(), x4, y4);
    }
    
    public static void quad(Renderer r, Vector2fc p1, double x2, double y2, Vector2dc p3, double x4, double y4)
    {
        r.quad(p1.x(), p1.y(), x2, y2, p3.x(), p3.y(), x4, y4);
    }
    
    public static void quad(Renderer r, Vector2dc p1, double x2, double y2, Vector2ic p3, double x4, double y4)
    {
        r.quad(p1.x(), p1.y(), x2, y2, p3.x(), p3.y(), x4, y4);
    }
    
    public static void quad(Renderer r, Vector2dc p1, double x2, double y2, Vector2fc p3, double x4, double y4)
    {
        r.quad(p1.x(), p1.y(), x2, y2, p3.x(), p3.y(), x4, y4);
    }
    
    public static void quad(Renderer r, Vector2dc p1, double x2, double y2, Vector2dc p3, double x4, double y4)
    {
        r.quad(p1.x(), p1.y(), x2, y2, p3.x(), p3.y(), x4, y4);
    }
    
    public static void quad(Renderer r, Vector2ic p1, Vector2ic p2, double x3, double y3, double x4, double y4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), x3, y3, x4, y4);
    }
    
    public static void quad(Renderer r, Vector2ic p1, Vector2fc p2, double x3, double y3, double x4, double y4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), x3, y3, x4, y4);
    }
    
    public static void quad(Renderer r, Vector2ic p1, Vector2dc p2, double x3, double y3, double x4, double y4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), x3, y3, x4, y4);
    }
    
    public static void quad(Renderer r, Vector2fc p1, Vector2ic p2, double x3, double y3, double x4, double y4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), x3, y3, x4, y4);
    }
    
    public static void quad(Renderer r, Vector2fc p1, Vector2fc p2, double x3, double y3, double x4, double y4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), x3, y3, x4, y4);
    }
    
    public static void quad(Renderer r, Vector2fc p1, Vector2dc p2, double x3, double y3, double x4, double y4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), x3, y3, x4, y4);
    }
    
    public static void quad(Renderer r, Vector2dc p1, Vector2ic p2, double x3, double y3, double x4, double y4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), x3, y3, x4, y4);
    }
    
    public static void quad(Renderer r, Vector2dc p1, Vector2fc p2, double x3, double y3, double x4, double y4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), x3, y3, x4, y4);
    }
    
    public static void quad(Renderer r, Vector2dc p1, Vector2dc p2, double x3, double y3, double x4, double y4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), x3, y3, x4, y4);
    }
    
    public static void quad(Renderer r, double x1, double y1, Vector2ic p2, Vector2ic p3, Vector2ic p4)
    {
        r.quad(x1, y1, p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, double x1, double y1, Vector2ic p2, Vector2ic p3, Vector2fc p4)
    {
        r.quad(x1, y1, p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, double x1, double y1, Vector2ic p2, Vector2ic p3, Vector2dc p4)
    {
        r.quad(x1, y1, p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, double x1, double y1, Vector2ic p2, Vector2fc p3, Vector2ic p4)
    {
        r.quad(x1, y1, p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, double x1, double y1, Vector2ic p2, Vector2fc p3, Vector2fc p4)
    {
        r.quad(x1, y1, p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, double x1, double y1, Vector2ic p2, Vector2fc p3, Vector2dc p4)
    {
        r.quad(x1, y1, p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, double x1, double y1, Vector2ic p2, Vector2dc p3, Vector2ic p4)
    {
        r.quad(x1, y1, p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, double x1, double y1, Vector2ic p2, Vector2dc p3, Vector2fc p4)
    {
        r.quad(x1, y1, p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, double x1, double y1, Vector2ic p2, Vector2dc p3, Vector2dc p4)
    {
        r.quad(x1, y1, p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, double x1, double y1, Vector2fc p2, Vector2ic p3, Vector2ic p4)
    {
        r.quad(x1, y1, p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, double x1, double y1, Vector2fc p2, Vector2ic p3, Vector2fc p4)
    {
        r.quad(x1, y1, p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, double x1, double y1, Vector2fc p2, Vector2ic p3, Vector2dc p4)
    {
        r.quad(x1, y1, p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, double x1, double y1, Vector2fc p2, Vector2fc p3, Vector2ic p4)
    {
        r.quad(x1, y1, p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, double x1, double y1, Vector2fc p2, Vector2fc p3, Vector2fc p4)
    {
        r.quad(x1, y1, p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, double x1, double y1, Vector2fc p2, Vector2fc p3, Vector2dc p4)
    {
        r.quad(x1, y1, p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, double x1, double y1, Vector2fc p2, Vector2dc p3, Vector2ic p4)
    {
        r.quad(x1, y1, p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, double x1, double y1, Vector2fc p2, Vector2dc p3, Vector2fc p4)
    {
        r.quad(x1, y1, p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, double x1, double y1, Vector2fc p2, Vector2dc p3, Vector2dc p4)
    {
        r.quad(x1, y1, p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, double x1, double y1, Vector2dc p2, Vector2ic p3, Vector2ic p4)
    {
        r.quad(x1, y1, p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, double x1, double y1, Vector2dc p2, Vector2ic p3, Vector2fc p4)
    {
        r.quad(x1, y1, p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, double x1, double y1, Vector2dc p2, Vector2ic p3, Vector2dc p4)
    {
        r.quad(x1, y1, p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, double x1, double y1, Vector2dc p2, Vector2fc p3, Vector2ic p4)
    {
        r.quad(x1, y1, p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, double x1, double y1, Vector2dc p2, Vector2fc p3, Vector2fc p4)
    {
        r.quad(x1, y1, p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, double x1, double y1, Vector2dc p2, Vector2fc p3, Vector2dc p4)
    {
        r.quad(x1, y1, p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, double x1, double y1, Vector2dc p2, Vector2dc p3, Vector2ic p4)
    {
        r.quad(x1, y1, p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, double x1, double y1, Vector2dc p2, Vector2dc p3, Vector2fc p4)
    {
        r.quad(x1, y1, p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2ic p1, double x2, double y2, Vector2ic p3, Vector2ic p4)
    {
        r.quad(p1.x(), p1.y(), x2, y2, p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2ic p1, double x2, double y2, Vector2ic p3, Vector2fc p4)
    {
        r.quad(p1.x(), p1.y(), x2, y2, p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2ic p1, double x2, double y2, Vector2ic p3, Vector2dc p4)
    {
        r.quad(p1.x(), p1.y(), x2, y2, p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2ic p1, double x2, double y2, Vector2fc p3, Vector2ic p4)
    {
        r.quad(p1.x(), p1.y(), x2, y2, p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2ic p1, double x2, double y2, Vector2fc p3, Vector2fc p4)
    {
        r.quad(p1.x(), p1.y(), x2, y2, p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2ic p1, double x2, double y2, Vector2fc p3, Vector2dc p4)
    {
        r.quad(p1.x(), p1.y(), x2, y2, p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2ic p1, double x2, double y2, Vector2dc p3, Vector2ic p4)
    {
        r.quad(p1.x(), p1.y(), x2, y2, p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2ic p1, double x2, double y2, Vector2dc p3, Vector2fc p4)
    {
        r.quad(p1.x(), p1.y(), x2, y2, p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2ic p1, double x2, double y2, Vector2dc p3, Vector2dc p4)
    {
        r.quad(p1.x(), p1.y(), x2, y2, p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2fc p1, double x2, double y2, Vector2ic p3, Vector2ic p4)
    {
        r.quad(p1.x(), p1.y(), x2, y2, p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2fc p1, double x2, double y2, Vector2ic p3, Vector2fc p4)
    {
        r.quad(p1.x(), p1.y(), x2, y2, p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2fc p1, double x2, double y2, Vector2ic p3, Vector2dc p4)
    {
        r.quad(p1.x(), p1.y(), x2, y2, p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2fc p1, double x2, double y2, Vector2fc p3, Vector2ic p4)
    {
        r.quad(p1.x(), p1.y(), x2, y2, p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2fc p1, double x2, double y2, Vector2fc p3, Vector2fc p4)
    {
        r.quad(p1.x(), p1.y(), x2, y2, p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2fc p1, double x2, double y2, Vector2fc p3, Vector2dc p4)
    {
        r.quad(p1.x(), p1.y(), x2, y2, p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2fc p1, double x2, double y2, Vector2dc p3, Vector2ic p4)
    {
        r.quad(p1.x(), p1.y(), x2, y2, p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2fc p1, double x2, double y2, Vector2dc p3, Vector2fc p4)
    {
        r.quad(p1.x(), p1.y(), x2, y2, p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2fc p1, double x2, double y2, Vector2dc p3, Vector2dc p4)
    {
        r.quad(p1.x(), p1.y(), x2, y2, p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2dc p1, double x2, double y2, Vector2ic p3, Vector2ic p4)
    {
        r.quad(p1.x(), p1.y(), x2, y2, p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2dc p1, double x2, double y2, Vector2ic p3, Vector2fc p4)
    {
        r.quad(p1.x(), p1.y(), x2, y2, p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2dc p1, double x2, double y2, Vector2ic p3, Vector2dc p4)
    {
        r.quad(p1.x(), p1.y(), x2, y2, p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2dc p1, double x2, double y2, Vector2fc p3, Vector2ic p4)
    {
        r.quad(p1.x(), p1.y(), x2, y2, p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2dc p1, double x2, double y2, Vector2fc p3, Vector2fc p4)
    {
        r.quad(p1.x(), p1.y(), x2, y2, p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2dc p1, double x2, double y2, Vector2fc p3, Vector2dc p4)
    {
        r.quad(p1.x(), p1.y(), x2, y2, p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2dc p1, double x2, double y2, Vector2dc p3, Vector2ic p4)
    {
        r.quad(p1.x(), p1.y(), x2, y2, p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2dc p1, double x2, double y2, Vector2dc p3, Vector2fc p4)
    {
        r.quad(p1.x(), p1.y(), x2, y2, p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2ic p1, Vector2ic p2, double x3, double y3, Vector2ic p4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), x3, y3, p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2ic p1, Vector2ic p2, double x3, double y3, Vector2fc p4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), x3, y3, p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2ic p1, Vector2ic p2, double x3, double y3, Vector2dc p4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), x3, y3, p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2ic p1, Vector2fc p2, double x3, double y3, Vector2ic p4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), x3, y3, p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2ic p1, Vector2fc p2, double x3, double y3, Vector2fc p4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), x3, y3, p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2ic p1, Vector2fc p2, double x3, double y3, Vector2dc p4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), x3, y3, p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2ic p1, Vector2dc p2, double x3, double y3, Vector2ic p4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), x3, y3, p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2ic p1, Vector2dc p2, double x3, double y3, Vector2fc p4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), x3, y3, p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2ic p1, Vector2dc p2, double x3, double y3, Vector2dc p4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), x3, y3, p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2fc p1, Vector2ic p2, double x3, double y3, Vector2ic p4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), x3, y3, p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2fc p1, Vector2ic p2, double x3, double y3, Vector2fc p4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), x3, y3, p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2fc p1, Vector2ic p2, double x3, double y3, Vector2dc p4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), x3, y3, p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2fc p1, Vector2fc p2, double x3, double y3, Vector2ic p4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), x3, y3, p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2fc p1, Vector2fc p2, double x3, double y3, Vector2fc p4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), x3, y3, p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2fc p1, Vector2fc p2, double x3, double y3, Vector2dc p4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), x3, y3, p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2fc p1, Vector2dc p2, double x3, double y3, Vector2ic p4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), x3, y3, p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2fc p1, Vector2dc p2, double x3, double y3, Vector2fc p4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), x3, y3, p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2fc p1, Vector2dc p2, double x3, double y3, Vector2dc p4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), x3, y3, p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2dc p1, Vector2ic p2, double x3, double y3, Vector2ic p4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), x3, y3, p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2dc p1, Vector2ic p2, double x3, double y3, Vector2fc p4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), x3, y3, p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2dc p1, Vector2ic p2, double x3, double y3, Vector2dc p4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), x3, y3, p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2dc p1, Vector2fc p2, double x3, double y3, Vector2ic p4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), x3, y3, p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2dc p1, Vector2fc p2, double x3, double y3, Vector2fc p4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), x3, y3, p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2dc p1, Vector2fc p2, double x3, double y3, Vector2dc p4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), x3, y3, p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2dc p1, Vector2dc p2, double x3, double y3, Vector2ic p4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), x3, y3, p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2dc p1, Vector2dc p2, double x3, double y3, Vector2fc p4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), x3, y3, p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2ic p1, Vector2ic p2, Vector2ic p3, double x4, double y4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), x4, y4);
    }
    
    public static void quad(Renderer r, Vector2ic p1, Vector2ic p2, Vector2fc p3, double x4, double y4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), x4, y4);
    }
    
    public static void quad(Renderer r, Vector2ic p1, Vector2ic p2, Vector2dc p3, double x4, double y4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), x4, y4);
    }
    
    public static void quad(Renderer r, Vector2ic p1, Vector2fc p2, Vector2ic p3, double x4, double y4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), x4, y4);
    }
    
    public static void quad(Renderer r, Vector2ic p1, Vector2fc p2, Vector2fc p3, double x4, double y4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), x4, y4);
    }
    
    public static void quad(Renderer r, Vector2ic p1, Vector2fc p2, Vector2dc p3, double x4, double y4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), x4, y4);
    }
    
    public static void quad(Renderer r, Vector2ic p1, Vector2dc p2, Vector2ic p3, double x4, double y4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), x4, y4);
    }
    
    public static void quad(Renderer r, Vector2ic p1, Vector2dc p2, Vector2fc p3, double x4, double y4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), x4, y4);
    }
    
    public static void quad(Renderer r, Vector2ic p1, Vector2dc p2, Vector2dc p3, double x4, double y4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), x4, y4);
    }
    
    public static void quad(Renderer r, Vector2fc p1, Vector2ic p2, Vector2ic p3, double x4, double y4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), x4, y4);
    }
    
    public static void quad(Renderer r, Vector2fc p1, Vector2ic p2, Vector2fc p3, double x4, double y4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), x4, y4);
    }
    
    public static void quad(Renderer r, Vector2fc p1, Vector2ic p2, Vector2dc p3, double x4, double y4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), x4, y4);
    }
    
    public static void quad(Renderer r, Vector2fc p1, Vector2fc p2, Vector2ic p3, double x4, double y4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), x4, y4);
    }
    
    public static void quad(Renderer r, Vector2fc p1, Vector2fc p2, Vector2fc p3, double x4, double y4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), x4, y4);
    }
    
    public static void quad(Renderer r, Vector2fc p1, Vector2fc p2, Vector2dc p3, double x4, double y4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), x4, y4);
    }
    
    public static void quad(Renderer r, Vector2fc p1, Vector2dc p2, Vector2ic p3, double x4, double y4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), x4, y4);
    }
    
    public static void quad(Renderer r, Vector2fc p1, Vector2dc p2, Vector2fc p3, double x4, double y4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), x4, y4);
    }
    
    public static void quad(Renderer r, Vector2fc p1, Vector2dc p2, Vector2dc p3, double x4, double y4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), x4, y4);
    }
    
    public static void quad(Renderer r, Vector2dc p1, Vector2ic p2, Vector2ic p3, double x4, double y4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), x4, y4);
    }
    
    public static void quad(Renderer r, Vector2dc p1, Vector2ic p2, Vector2fc p3, double x4, double y4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), x4, y4);
    }
    
    public static void quad(Renderer r, Vector2dc p1, Vector2ic p2, Vector2dc p3, double x4, double y4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), x4, y4);
    }
    
    public static void quad(Renderer r, Vector2dc p1, Vector2fc p2, Vector2ic p3, double x4, double y4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), x4, y4);
    }
    
    public static void quad(Renderer r, Vector2dc p1, Vector2fc p2, Vector2fc p3, double x4, double y4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), x4, y4);
    }
    
    public static void quad(Renderer r, Vector2dc p1, Vector2fc p2, Vector2dc p3, double x4, double y4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), x4, y4);
    }
    
    public static void quad(Renderer r, Vector2dc p1, Vector2dc p2, Vector2ic p3, double x4, double y4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), x4, y4);
    }
    
    public static void quad(Renderer r, Vector2dc p1, Vector2dc p2, Vector2fc p3, double x4, double y4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), x4, y4);
    }
    
    public static void quad(Renderer r, Vector2ic p1, Vector2ic p2, Vector2ic p3, Vector2ic p4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2ic p1, Vector2ic p2, Vector2ic p3, Vector2fc p4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2ic p1, Vector2ic p2, Vector2ic p3, Vector2dc p4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2ic p1, Vector2ic p2, Vector2fc p3, Vector2ic p4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2ic p1, Vector2ic p2, Vector2fc p3, Vector2fc p4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2ic p1, Vector2ic p2, Vector2fc p3, Vector2dc p4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2ic p1, Vector2ic p2, Vector2dc p3, Vector2ic p4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2ic p1, Vector2ic p2, Vector2dc p3, Vector2fc p4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2ic p1, Vector2ic p2, Vector2dc p3, Vector2dc p4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2ic p1, Vector2fc p2, Vector2ic p3, Vector2ic p4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2ic p1, Vector2fc p2, Vector2ic p3, Vector2fc p4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2ic p1, Vector2fc p2, Vector2ic p3, Vector2dc p4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2ic p1, Vector2fc p2, Vector2fc p3, Vector2ic p4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2ic p1, Vector2fc p2, Vector2fc p3, Vector2fc p4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2ic p1, Vector2fc p2, Vector2fc p3, Vector2dc p4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2ic p1, Vector2fc p2, Vector2dc p3, Vector2ic p4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2ic p1, Vector2fc p2, Vector2dc p3, Vector2fc p4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2ic p1, Vector2fc p2, Vector2dc p3, Vector2dc p4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2ic p1, Vector2dc p2, Vector2ic p3, Vector2ic p4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2ic p1, Vector2dc p2, Vector2ic p3, Vector2fc p4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2ic p1, Vector2dc p2, Vector2ic p3, Vector2dc p4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2ic p1, Vector2dc p2, Vector2fc p3, Vector2ic p4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2ic p1, Vector2dc p2, Vector2fc p3, Vector2fc p4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2ic p1, Vector2dc p2, Vector2fc p3, Vector2dc p4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2ic p1, Vector2dc p2, Vector2dc p3, Vector2ic p4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2ic p1, Vector2dc p2, Vector2dc p3, Vector2fc p4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2ic p1, Vector2dc p2, Vector2dc p3, Vector2dc p4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2fc p1, Vector2ic p2, Vector2ic p3, Vector2ic p4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2fc p1, Vector2ic p2, Vector2ic p3, Vector2fc p4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2fc p1, Vector2ic p2, Vector2ic p3, Vector2dc p4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2fc p1, Vector2ic p2, Vector2fc p3, Vector2ic p4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2fc p1, Vector2ic p2, Vector2fc p3, Vector2fc p4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2fc p1, Vector2ic p2, Vector2fc p3, Vector2dc p4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2fc p1, Vector2ic p2, Vector2dc p3, Vector2ic p4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2fc p1, Vector2ic p2, Vector2dc p3, Vector2fc p4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2fc p1, Vector2ic p2, Vector2dc p3, Vector2dc p4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2fc p1, Vector2fc p2, Vector2ic p3, Vector2ic p4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2fc p1, Vector2fc p2, Vector2ic p3, Vector2fc p4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2fc p1, Vector2fc p2, Vector2ic p3, Vector2dc p4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2fc p1, Vector2fc p2, Vector2fc p3, Vector2ic p4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2fc p1, Vector2fc p2, Vector2fc p3, Vector2fc p4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2fc p1, Vector2fc p2, Vector2fc p3, Vector2dc p4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2fc p1, Vector2fc p2, Vector2dc p3, Vector2ic p4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2fc p1, Vector2fc p2, Vector2dc p3, Vector2fc p4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2fc p1, Vector2fc p2, Vector2dc p3, Vector2dc p4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2fc p1, Vector2dc p2, Vector2ic p3, Vector2ic p4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2fc p1, Vector2dc p2, Vector2ic p3, Vector2fc p4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2fc p1, Vector2dc p2, Vector2ic p3, Vector2dc p4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2fc p1, Vector2dc p2, Vector2fc p3, Vector2ic p4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2fc p1, Vector2dc p2, Vector2fc p3, Vector2fc p4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2fc p1, Vector2dc p2, Vector2fc p3, Vector2dc p4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2fc p1, Vector2dc p2, Vector2dc p3, Vector2ic p4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2fc p1, Vector2dc p2, Vector2dc p3, Vector2fc p4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2fc p1, Vector2dc p2, Vector2dc p3, Vector2dc p4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2dc p1, Vector2ic p2, Vector2ic p3, Vector2ic p4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2dc p1, Vector2ic p2, Vector2ic p3, Vector2fc p4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2dc p1, Vector2ic p2, Vector2ic p3, Vector2dc p4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2dc p1, Vector2ic p2, Vector2fc p3, Vector2ic p4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2dc p1, Vector2ic p2, Vector2fc p3, Vector2fc p4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2dc p1, Vector2ic p2, Vector2fc p3, Vector2dc p4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2dc p1, Vector2ic p2, Vector2dc p3, Vector2ic p4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2dc p1, Vector2ic p2, Vector2dc p3, Vector2fc p4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2dc p1, Vector2ic p2, Vector2dc p3, Vector2dc p4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2dc p1, Vector2fc p2, Vector2ic p3, Vector2ic p4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2dc p1, Vector2fc p2, Vector2ic p3, Vector2fc p4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2dc p1, Vector2fc p2, Vector2ic p3, Vector2dc p4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2dc p1, Vector2fc p2, Vector2fc p3, Vector2ic p4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2dc p1, Vector2fc p2, Vector2fc p3, Vector2fc p4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2dc p1, Vector2fc p2, Vector2fc p3, Vector2dc p4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2dc p1, Vector2fc p2, Vector2dc p3, Vector2ic p4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2dc p1, Vector2fc p2, Vector2dc p3, Vector2fc p4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2dc p1, Vector2fc p2, Vector2dc p3, Vector2dc p4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2dc p1, Vector2dc p2, Vector2ic p3, Vector2ic p4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2dc p1, Vector2dc p2, Vector2ic p3, Vector2fc p4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2dc p1, Vector2dc p2, Vector2ic p3, Vector2dc p4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2dc p1, Vector2dc p2, Vector2fc p3, Vector2ic p4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2dc p1, Vector2dc p2, Vector2fc p3, Vector2fc p4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2dc p1, Vector2dc p2, Vector2fc p3, Vector2dc p4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2dc p1, Vector2dc p2, Vector2dc p3, Vector2ic p4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2dc p1, Vector2dc p2, Vector2dc p3, Vector2fc p4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void quad(Renderer r, Vector2dc p1, Vector2dc p2, Vector2dc p3, Vector2dc p4)
    {
        r.quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y());
    }
    
    public static void polygon(Renderer r, Object... points)
    {
        boolean           expectNumber = false;
        ArrayList<Double> data         = new ArrayList<>();
        for (Object value : points)
        {
            if (value instanceof Integer)
            {
                expectNumber = !expectNumber;
                data.add((double) ((Integer) value));
            }
            else if (value instanceof Float)
            {
                expectNumber = !expectNumber;
                data.add((double) ((Float) value));
            }
            else if (value instanceof Double)
            {
                expectNumber = !expectNumber;
                data.add((Double) value);
            }
            else if (value instanceof Vector2ic)
            {
                if (expectNumber) throw new RuntimeException("Invalid coordinates. Mismatch");
                Vector2ic point = (Vector2ic) value;
                data.add((double) point.x());
                data.add((double) point.y());
            }
            else if (value instanceof Vector2fc)
            {
                if (expectNumber) throw new RuntimeException("Invalid coordinates. Mismatch");
                Vector2fc point = (Vector2fc) value;
                data.add((double) point.x());
                data.add((double) point.y());
            }
            else if (value instanceof Vector2dc)
            {
                if (expectNumber) throw new RuntimeException("Invalid coordinates. Mismatch");
                Vector2dc point = (Vector2dc) value;
                data.add(point.x());
                data.add(point.y());
            }
        }
        if (expectNumber) throw new RuntimeException("Invalid coordinates. Mismatch");
        
        int      n         = data.size();
        double[] newPoints = new double[n];
        for (int i = 0; i < n; i++)
        {
            newPoints[i] = data.get(i);
        }
        
        if ((n & 1) == 1) throw new RuntimeException("Invalid coordinates. Must be an even number");
        if (n < 6) throw new RuntimeException("Invalid coordinates. Must have at least 3 coordinate pairs");
        
        r.polygon(newPoints);
    }
    
    public static void circle(Renderer r, Vector2ic ab, double c)
    {
        r.circle(ab.x(), ab.y(), c);
    }
    
    public static void circle(Renderer r, Vector2fc ab, double c)
    {
        r.circle(ab.x(), ab.y(), c);
    }
    
    public static void circle(Renderer r, Vector2dc ab, double c)
    {
        r.circle(ab.x(), ab.y(), c);
    }
    
    public static void ellipse(Renderer r, double a, double b, Vector2ic cd)
    {
        r.ellipse(a, b, cd.x(), cd.y());
    }
    
    public static void ellipse(Renderer r, double a, double b, Vector2fc cd)
    {
        r.ellipse(a, b, cd.x(), cd.y());
    }
    
    public static void ellipse(Renderer r, double a, double b, Vector2dc cd)
    {
        r.ellipse(a, b, cd.x(), cd.y());
    }
    
    public static void ellipse(Renderer r, Vector2ic ab, double c, double d)
    {
        r.ellipse(ab.x(), ab.y(), c, d);
    }
    
    public static void ellipse(Renderer r, Vector2fc ab, double c, double d)
    {
        r.ellipse(ab.x(), ab.y(), c, d);
    }
    
    public static void ellipse(Renderer r, Vector2dc ab, double c, double d)
    {
        r.ellipse(ab.x(), ab.y(), c, d);
    }
    
    public static void ellipse(Renderer r, Vector2ic ab, Vector2ic cd)
    {
        r.ellipse(ab.x(), ab.y(), cd.x(), cd.y());
    }
    
    public static void ellipse(Renderer r, Vector2ic ab, Vector2fc cd)
    {
        r.ellipse(ab.x(), ab.y(), cd.x(), cd.y());
    }
    
    public static void ellipse(Renderer r, Vector2ic ab, Vector2dc cd)
    {
        r.ellipse(ab.x(), ab.y(), cd.x(), cd.y());
    }
    
    public static void ellipse(Renderer r, Vector2fc ab, Vector2ic cd)
    {
        r.ellipse(ab.x(), ab.y(), cd.x(), cd.y());
    }
    
    public static void ellipse(Renderer r, Vector2fc ab, Vector2fc cd)
    {
        r.ellipse(ab.x(), ab.y(), cd.x(), cd.y());
    }
    
    public static void ellipse(Renderer r, Vector2fc ab, Vector2dc cd)
    {
        r.ellipse(ab.x(), ab.y(), cd.x(), cd.y());
    }
    
    public static void ellipse(Renderer r, Vector2dc ab, Vector2ic cd)
    {
        r.ellipse(ab.x(), ab.y(), cd.x(), cd.y());
    }
    
    public static void ellipse(Renderer r, Vector2dc ab, Vector2fc cd)
    {
        r.ellipse(ab.x(), ab.y(), cd.x(), cd.y());
    }
    
    public static void ellipse(Renderer r, Vector2dc ab, Vector2dc cd)
    {
        r.ellipse(ab.x(), ab.y(), cd.x(), cd.y());
    }
    
    public static void arc(Renderer r, double a, double b, double c, double d, Vector2ic arcLength)
    {
        r.arc(a, b, c, d, arcLength.x(), arcLength.y());
    }
    
    public static void arc(Renderer r, double a, double b, double c, double d, Vector2fc arcLength)
    {
        r.arc(a, b, c, d, arcLength.x(), arcLength.y());
    }
    
    public static void arc(Renderer r, double a, double b, double c, double d, Vector2dc arcLength)
    {
        r.arc(a, b, c, d, arcLength.x(), arcLength.y());
    }
    
    public static void arc(Renderer r, double a, double b, Vector2ic cd, double start, double stop)
    {
        r.arc(a, b, cd.x(), cd.y(), start, stop);
    }
    
    public static void arc(Renderer r, double a, double b, Vector2fc cd, double start, double stop)
    {
        r.arc(a, b, cd.x(), cd.y(), start, stop);
    }
    
    public static void arc(Renderer r, double a, double b, Vector2dc cd, double start, double stop)
    {
        r.arc(a, b, cd.x(), cd.y(), start, stop);
    }
    
    public static void arc(Renderer r, Vector2ic ab, double c, double d, double start, double stop)
    {
        r.arc(ab.x(), ab.y(), c, d, start, stop);
    }
    
    public static void arc(Renderer r, Vector2fc ab, double c, double d, double start, double stop)
    {
        r.arc(ab.x(), ab.y(), c, d, start, stop);
    }
    
    public static void arc(Renderer r, Vector2dc ab, double c, double d, double start, double stop)
    {
        r.arc(ab.x(), ab.y(), c, d, start, stop);
    }
    
    public static void arc(Renderer r, double a, double b, Vector2ic cd, Vector2ic arcLength)
    {
        r.arc(a, b, cd.x(), cd.y(), arcLength.x(), arcLength.y());
    }
    
    public static void arc(Renderer r, double a, double b, Vector2ic cd, Vector2fc arcLength)
    {
        r.arc(a, b, cd.x(), cd.y(), arcLength.x(), arcLength.y());
    }
    
    public static void arc(Renderer r, double a, double b, Vector2ic cd, Vector2dc arcLength)
    {
        r.arc(a, b, cd.x(), cd.y(), arcLength.x(), arcLength.y());
    }
    
    public static void arc(Renderer r, double a, double b, Vector2fc cd, Vector2ic arcLength)
    {
        r.arc(a, b, cd.x(), cd.y(), arcLength.x(), arcLength.y());
    }
    
    public static void arc(Renderer r, double a, double b, Vector2fc cd, Vector2fc arcLength)
    {
        r.arc(a, b, cd.x(), cd.y(), arcLength.x(), arcLength.y());
    }
    
    public static void arc(Renderer r, double a, double b, Vector2fc cd, Vector2dc arcLength)
    {
        r.arc(a, b, cd.x(), cd.y(), arcLength.x(), arcLength.y());
    }
    
    public static void arc(Renderer r, double a, double b, Vector2dc cd, Vector2ic arcLength)
    {
        r.arc(a, b, cd.x(), cd.y(), arcLength.x(), arcLength.y());
    }
    
    public static void arc(Renderer r, double a, double b, Vector2dc cd, Vector2fc arcLength)
    {
        r.arc(a, b, cd.x(), cd.y(), arcLength.x(), arcLength.y());
    }
    
    public static void arc(Renderer r, double a, double b, Vector2dc cd, Vector2dc arcLength)
    {
        r.arc(a, b, cd.x(), cd.y(), arcLength.x(), arcLength.y());
    }
    
    public static void arc(Renderer r, Vector2ic ab, double c, double d, Vector2ic arcLength)
    {
        r.arc(ab.x(), ab.y(), c, d, arcLength.x(), arcLength.y());
    }
    
    public static void arc(Renderer r, Vector2ic ab, double c, double d, Vector2fc arcLength)
    {
        r.arc(ab.x(), ab.y(), c, d, arcLength.x(), arcLength.y());
    }
    
    public static void arc(Renderer r, Vector2ic ab, double c, double d, Vector2dc arcLength)
    {
        r.arc(ab.x(), ab.y(), c, d, arcLength.x(), arcLength.y());
    }
    
    public static void arc(Renderer r, Vector2fc ab, double c, double d, Vector2ic arcLength)
    {
        r.arc(ab.x(), ab.y(), c, d, arcLength.x(), arcLength.y());
    }
    
    public static void arc(Renderer r, Vector2fc ab, double c, double d, Vector2fc arcLength)
    {
        r.arc(ab.x(), ab.y(), c, d, arcLength.x(), arcLength.y());
    }
    
    public static void arc(Renderer r, Vector2fc ab, double c, double d, Vector2dc arcLength)
    {
        r.arc(ab.x(), ab.y(), c, d, arcLength.x(), arcLength.y());
    }
    
    public static void arc(Renderer r, Vector2dc ab, double c, double d, Vector2ic arcLength)
    {
        r.arc(ab.x(), ab.y(), c, d, arcLength.x(), arcLength.y());
    }
    
    public static void arc(Renderer r, Vector2dc ab, double c, double d, Vector2fc arcLength)
    {
        r.arc(ab.x(), ab.y(), c, d, arcLength.x(), arcLength.y());
    }
    
    public static void arc(Renderer r, Vector2dc ab, double c, double d, Vector2dc arcLength)
    {
        r.arc(ab.x(), ab.y(), c, d, arcLength.x(), arcLength.y());
    }
    
    public static void arc(Renderer r, Vector2ic ab, Vector2ic cd, double start, double stop)
    {
        r.arc(ab.x(), ab.y(), cd.x(), cd.y(), start, stop);
    }
    
    public static void arc(Renderer r, Vector2ic ab, Vector2fc cd, double start, double stop)
    {
        r.arc(ab.x(), ab.y(), cd.x(), cd.y(), start, stop);
    }
    
    public static void arc(Renderer r, Vector2ic ab, Vector2dc cd, double start, double stop)
    {
        r.arc(ab.x(), ab.y(), cd.x(), cd.y(), start, stop);
    }
    
    public static void arc(Renderer r, Vector2fc ab, Vector2ic cd, double start, double stop)
    {
        r.arc(ab.x(), ab.y(), cd.x(), cd.y(), start, stop);
    }
    
    public static void arc(Renderer r, Vector2fc ab, Vector2fc cd, double start, double stop)
    {
        r.arc(ab.x(), ab.y(), cd.x(), cd.y(), start, stop);
    }
    
    public static void arc(Renderer r, Vector2fc ab, Vector2dc cd, double start, double stop)
    {
        r.arc(ab.x(), ab.y(), cd.x(), cd.y(), start, stop);
    }
    
    public static void arc(Renderer r, Vector2dc ab, Vector2ic cd, double start, double stop)
    {
        r.arc(ab.x(), ab.y(), cd.x(), cd.y(), start, stop);
    }
    
    public static void arc(Renderer r, Vector2dc ab, Vector2fc cd, double start, double stop)
    {
        r.arc(ab.x(), ab.y(), cd.x(), cd.y(), start, stop);
    }
    
    public static void arc(Renderer r, Vector2dc ab, Vector2dc cd, double start, double stop)
    {
        r.arc(ab.x(), ab.y(), cd.x(), cd.y(), start, stop);
    }
    
    public static void arc(Renderer r, Vector2ic ab, Vector2ic cd, Vector2ic arcLength)
    {
        r.arc(ab.x(), ab.y(), cd.x(), cd.y(), arcLength.x(), arcLength.y());
    }
    
    public static void arc(Renderer r, Vector2ic ab, Vector2ic cd, Vector2fc arcLength)
    {
        r.arc(ab.x(), ab.y(), cd.x(), cd.y(), arcLength.x(), arcLength.y());
    }
    
    public static void arc(Renderer r, Vector2ic ab, Vector2ic cd, Vector2dc arcLength)
    {
        r.arc(ab.x(), ab.y(), cd.x(), cd.y(), arcLength.x(), arcLength.y());
    }
    
    public static void arc(Renderer r, Vector2ic ab, Vector2fc cd, Vector2ic arcLength)
    {
        r.arc(ab.x(), ab.y(), cd.x(), cd.y(), arcLength.x(), arcLength.y());
    }
    
    public static void arc(Renderer r, Vector2ic ab, Vector2fc cd, Vector2fc arcLength)
    {
        r.arc(ab.x(), ab.y(), cd.x(), cd.y(), arcLength.x(), arcLength.y());
    }
    
    public static void arc(Renderer r, Vector2ic ab, Vector2fc cd, Vector2dc arcLength)
    {
        r.arc(ab.x(), ab.y(), cd.x(), cd.y(), arcLength.x(), arcLength.y());
    }
    
    public static void arc(Renderer r, Vector2ic ab, Vector2dc cd, Vector2ic arcLength)
    {
        r.arc(ab.x(), ab.y(), cd.x(), cd.y(), arcLength.x(), arcLength.y());
    }
    
    public static void arc(Renderer r, Vector2ic ab, Vector2dc cd, Vector2fc arcLength)
    {
        r.arc(ab.x(), ab.y(), cd.x(), cd.y(), arcLength.x(), arcLength.y());
    }
    
    public static void arc(Renderer r, Vector2ic ab, Vector2dc cd, Vector2dc arcLength)
    {
        r.arc(ab.x(), ab.y(), cd.x(), cd.y(), arcLength.x(), arcLength.y());
    }
    
    public static void arc(Renderer r, Vector2fc ab, Vector2ic cd, Vector2ic arcLength)
    {
        r.arc(ab.x(), ab.y(), cd.x(), cd.y(), arcLength.x(), arcLength.y());
    }
    
    public static void arc(Renderer r, Vector2fc ab, Vector2ic cd, Vector2fc arcLength)
    {
        r.arc(ab.x(), ab.y(), cd.x(), cd.y(), arcLength.x(), arcLength.y());
    }
    
    public static void arc(Renderer r, Vector2fc ab, Vector2ic cd, Vector2dc arcLength)
    {
        r.arc(ab.x(), ab.y(), cd.x(), cd.y(), arcLength.x(), arcLength.y());
    }
    
    public static void arc(Renderer r, Vector2fc ab, Vector2fc cd, Vector2ic arcLength)
    {
        r.arc(ab.x(), ab.y(), cd.x(), cd.y(), arcLength.x(), arcLength.y());
    }
    
    public static void arc(Renderer r, Vector2fc ab, Vector2fc cd, Vector2fc arcLength)
    {
        r.arc(ab.x(), ab.y(), cd.x(), cd.y(), arcLength.x(), arcLength.y());
    }
    
    public static void arc(Renderer r, Vector2fc ab, Vector2fc cd, Vector2dc arcLength)
    {
        r.arc(ab.x(), ab.y(), cd.x(), cd.y(), arcLength.x(), arcLength.y());
    }
    
    public static void arc(Renderer r, Vector2fc ab, Vector2dc cd, Vector2ic arcLength)
    {
        r.arc(ab.x(), ab.y(), cd.x(), cd.y(), arcLength.x(), arcLength.y());
    }
    
    public static void arc(Renderer r, Vector2fc ab, Vector2dc cd, Vector2fc arcLength)
    {
        r.arc(ab.x(), ab.y(), cd.x(), cd.y(), arcLength.x(), arcLength.y());
    }
    
    public static void arc(Renderer r, Vector2fc ab, Vector2dc cd, Vector2dc arcLength)
    {
        r.arc(ab.x(), ab.y(), cd.x(), cd.y(), arcLength.x(), arcLength.y());
    }
    
    public static void arc(Renderer r, Vector2dc ab, Vector2ic cd, Vector2ic arcLength)
    {
        r.arc(ab.x(), ab.y(), cd.x(), cd.y(), arcLength.x(), arcLength.y());
    }
    
    public static void arc(Renderer r, Vector2dc ab, Vector2ic cd, Vector2fc arcLength)
    {
        r.arc(ab.x(), ab.y(), cd.x(), cd.y(), arcLength.x(), arcLength.y());
    }
    
    public static void arc(Renderer r, Vector2dc ab, Vector2ic cd, Vector2dc arcLength)
    {
        r.arc(ab.x(), ab.y(), cd.x(), cd.y(), arcLength.x(), arcLength.y());
    }
    
    public static void arc(Renderer r, Vector2dc ab, Vector2fc cd, Vector2ic arcLength)
    {
        r.arc(ab.x(), ab.y(), cd.x(), cd.y(), arcLength.x(), arcLength.y());
    }
    
    public static void arc(Renderer r, Vector2dc ab, Vector2fc cd, Vector2fc arcLength)
    {
        r.arc(ab.x(), ab.y(), cd.x(), cd.y(), arcLength.x(), arcLength.y());
    }
    
    public static void arc(Renderer r, Vector2dc ab, Vector2fc cd, Vector2dc arcLength)
    {
        r.arc(ab.x(), ab.y(), cd.x(), cd.y(), arcLength.x(), arcLength.y());
    }
    
    public static void arc(Renderer r, Vector2dc ab, Vector2dc cd, Vector2ic arcLength)
    {
        r.arc(ab.x(), ab.y(), cd.x(), cd.y(), arcLength.x(), arcLength.y());
    }
    
    public static void arc(Renderer r, Vector2dc ab, Vector2dc cd, Vector2fc arcLength)
    {
        r.arc(ab.x(), ab.y(), cd.x(), cd.y(), arcLength.x(), arcLength.y());
    }
    
    public static void arc(Renderer r, Vector2dc ab, Vector2dc cd, Vector2dc arcLength)
    {
        r.arc(ab.x(), ab.y(), cd.x(), cd.y(), arcLength.x(), arcLength.y());
    }
    
    public static void texture(Renderer r, Texture t, double a, double b, double c, double d, double u1, double v1, Vector2ic uv2)
    {
        r.texture(t, a, b, c, d, u1, v1, uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, double a, double b, double c, double d, double u1, double v1, Vector2fc uv2)
    {
        r.texture(t, a, b, c, d, u1, v1, uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, double a, double b, double c, double d, double u1, double v1, Vector2dc uv2)
    {
        r.texture(t, a, b, c, d, u1, v1, uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, double a, double b, double c, double d, Vector2ic uv1, double u2, double v2)
    {
        r.texture(t, a, b, c, d, uv1.x(), uv1.y(), v2, u2);
    }
    
    public static void texture(Renderer r, Texture t, double a, double b, double c, double d, Vector2fc uv1, double u2, double v2)
    {
        r.texture(t, a, b, c, d, uv1.x(), uv1.y(), v2, u2);
    }
    
    public static void texture(Renderer r, Texture t, double a, double b, double c, double d, Vector2dc uv1, double u2, double v2)
    {
        r.texture(t, a, b, c, d, uv1.x(), uv1.y(), v2, u2);
    }
    
    public static void texture(Renderer r, Texture t, double a, double b, double c, double d, Vector2ic uv1, Vector2ic uv2)
    {
        r.texture(t, a, b, c, d, uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, double a, double b, double c, double d, Vector2ic uv1, Vector2fc uv2)
    {
        r.texture(t, a, b, c, d, uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, double a, double b, double c, double d, Vector2ic uv1, Vector2dc uv2)
    {
        r.texture(t, a, b, c, d, uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, double a, double b, double c, double d, Vector2fc uv1, Vector2ic uv2)
    {
        r.texture(t, a, b, c, d, uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, double a, double b, double c, double d, Vector2fc uv1, Vector2fc uv2)
    {
        r.texture(t, a, b, c, d, uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, double a, double b, double c, double d, Vector2fc uv1, Vector2dc uv2)
    {
        r.texture(t, a, b, c, d, uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, double a, double b, double c, double d, Vector2dc uv1, Vector2ic uv2)
    {
        r.texture(t, a, b, c, d, uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, double a, double b, double c, double d, Vector2dc uv1, Vector2fc uv2)
    {
        r.texture(t, a, b, c, d, uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, double a, double b, double c, double d, Vector2dc uv1, Vector2dc uv2)
    {
        r.texture(t, a, b, c, d, uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, double a, double b, Vector2ic cd, double u1, double v1, Vector2ic uv2)
    {
        r.texture(t, a, b, cd.x(), cd.y(), u1, v1, uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, double a, double b, Vector2ic cd, double u1, double v1, Vector2fc uv2)
    {
        r.texture(t, a, b, cd.x(), cd.y(), u1, v1, uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, double a, double b, Vector2ic cd, double u1, double v1, Vector2dc uv2)
    {
        r.texture(t, a, b, cd.x(), cd.y(), u1, v1, uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, double a, double b, Vector2fc cd, double u1, double v1, Vector2ic uv2)
    {
        r.texture(t, a, b, cd.x(), cd.y(), u1, v1, uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, double a, double b, Vector2fc cd, double u1, double v1, Vector2fc uv2)
    {
        r.texture(t, a, b, cd.x(), cd.y(), u1, v1, uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, double a, double b, Vector2fc cd, double u1, double v1, Vector2dc uv2)
    {
        r.texture(t, a, b, cd.x(), cd.y(), u1, v1, uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, double a, double b, Vector2dc cd, double u1, double v1, Vector2ic uv2)
    {
        r.texture(t, a, b, cd.x(), cd.y(), u1, v1, uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, double a, double b, Vector2dc cd, double u1, double v1, Vector2fc uv2)
    {
        r.texture(t, a, b, cd.x(), cd.y(), u1, v1, uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, double a, double b, Vector2dc cd, double u1, double v1, Vector2dc uv2)
    {
        r.texture(t, a, b, cd.x(), cd.y(), u1, v1, uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, double a, double b, Vector2ic cd, Vector2ic uv1, double u2, double v2)
    {
        r.texture(t, a, b, cd.x(), cd.y(), uv1.x(), uv1.y(), v2, u2);
    }
    
    public static void texture(Renderer r, Texture t, double a, double b, Vector2ic cd, Vector2fc uv1, double u2, double v2)
    {
        r.texture(t, a, b, cd.x(), cd.y(), uv1.x(), uv1.y(), v2, u2);
    }
    
    public static void texture(Renderer r, Texture t, double a, double b, Vector2ic cd, Vector2dc uv1, double u2, double v2)
    {
        r.texture(t, a, b, cd.x(), cd.y(), uv1.x(), uv1.y(), v2, u2);
    }
    
    public static void texture(Renderer r, Texture t, double a, double b, Vector2fc cd, Vector2ic uv1, double u2, double v2)
    {
        r.texture(t, a, b, cd.x(), cd.y(), uv1.x(), uv1.y(), v2, u2);
    }
    
    public static void texture(Renderer r, Texture t, double a, double b, Vector2fc cd, Vector2fc uv1, double u2, double v2)
    {
        r.texture(t, a, b, cd.x(), cd.y(), uv1.x(), uv1.y(), v2, u2);
    }
    
    public static void texture(Renderer r, Texture t, double a, double b, Vector2fc cd, Vector2dc uv1, double u2, double v2)
    {
        r.texture(t, a, b, cd.x(), cd.y(), uv1.x(), uv1.y(), v2, u2);
    }
    
    public static void texture(Renderer r, Texture t, double a, double b, Vector2dc cd, Vector2ic uv1, double u2, double v2)
    {
        r.texture(t, a, b, cd.x(), cd.y(), uv1.x(), uv1.y(), v2, u2);
    }
    
    public static void texture(Renderer r, Texture t, double a, double b, Vector2dc cd, Vector2fc uv1, double u2, double v2)
    {
        r.texture(t, a, b, cd.x(), cd.y(), uv1.x(), uv1.y(), v2, u2);
    }
    
    public static void texture(Renderer r, Texture t, double a, double b, Vector2dc cd, Vector2dc uv1, double u2, double v2)
    {
        r.texture(t, a, b, cd.x(), cd.y(), uv1.x(), uv1.y(), v2, u2);
    }
    
    public static void texture(Renderer r, Texture t, Vector2ic ab, double c, double d, double u1, double v1, Vector2ic uv2)
    {
        r.texture(t, ab.x(), ab.y(), c, d, u1, v1, uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2ic ab, double c, double d, double u1, double v1, Vector2fc uv2)
    {
        r.texture(t, ab.x(), ab.y(), c, d, u1, v1, uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2ic ab, double c, double d, double u1, double v1, Vector2dc uv2)
    {
        r.texture(t, ab.x(), ab.y(), c, d, u1, v1, uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2fc ab, double c, double d, double u1, double v1, Vector2ic uv2)
    {
        r.texture(t, ab.x(), ab.y(), c, d, u1, v1, uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2fc ab, double c, double d, double u1, double v1, Vector2fc uv2)
    {
        r.texture(t, ab.x(), ab.y(), c, d, u1, v1, uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2fc ab, double c, double d, double u1, double v1, Vector2dc uv2)
    {
        r.texture(t, ab.x(), ab.y(), c, d, u1, v1, uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2dc ab, double c, double d, double u1, double v1, Vector2ic uv2)
    {
        r.texture(t, ab.x(), ab.y(), c, d, u1, v1, uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2dc ab, double c, double d, double u1, double v1, Vector2fc uv2)
    {
        r.texture(t, ab.x(), ab.y(), c, d, u1, v1, uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2dc ab, double c, double d, double u1, double v1, Vector2dc uv2)
    {
        r.texture(t, ab.x(), ab.y(), c, d, u1, v1, uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2ic ab, double c, double d, Vector2ic uv1, double u2, double v2)
    {
        r.texture(t, ab.x(), ab.y(), c, d, uv1.x(), uv1.y(), v2, u2);
    }
    
    public static void texture(Renderer r, Texture t, Vector2ic ab, double c, double d, Vector2fc uv1, double u2, double v2)
    {
        r.texture(t, ab.x(), ab.y(), c, d, uv1.x(), uv1.y(), v2, u2);
    }
    
    public static void texture(Renderer r, Texture t, Vector2ic ab, double c, double d, Vector2dc uv1, double u2, double v2)
    {
        r.texture(t, ab.x(), ab.y(), c, d, uv1.x(), uv1.y(), v2, u2);
    }
    
    public static void texture(Renderer r, Texture t, Vector2fc ab, double c, double d, Vector2ic uv1, double u2, double v2)
    {
        r.texture(t, ab.x(), ab.y(), c, d, uv1.x(), uv1.y(), v2, u2);
    }
    
    public static void texture(Renderer r, Texture t, Vector2fc ab, double c, double d, Vector2fc uv1, double u2, double v2)
    {
        r.texture(t, ab.x(), ab.y(), c, d, uv1.x(), uv1.y(), v2, u2);
    }
    
    public static void texture(Renderer r, Texture t, Vector2fc ab, double c, double d, Vector2dc uv1, double u2, double v2)
    {
        r.texture(t, ab.x(), ab.y(), c, d, uv1.x(), uv1.y(), v2, u2);
    }
    
    public static void texture(Renderer r, Texture t, Vector2dc ab, double c, double d, Vector2ic uv1, double u2, double v2)
    {
        r.texture(t, ab.x(), ab.y(), c, d, uv1.x(), uv1.y(), v2, u2);
    }
    
    public static void texture(Renderer r, Texture t, Vector2dc ab, double c, double d, Vector2fc uv1, double u2, double v2)
    {
        r.texture(t, ab.x(), ab.y(), c, d, uv1.x(), uv1.y(), v2, u2);
    }
    
    public static void texture(Renderer r, Texture t, Vector2dc ab, double c, double d, Vector2dc uv1, double u2, double v2)
    {
        r.texture(t, ab.x(), ab.y(), c, d, uv1.x(), uv1.y(), v2, u2);
    }
    
    public static void texture(Renderer r, Texture t, Vector2ic ab, Vector2ic cd, double u1, double v1, double u2, double v2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), u1, v1, v2, u2);
    }
    
    public static void texture(Renderer r, Texture t, Vector2ic ab, Vector2fc cd, double u1, double v1, double u2, double v2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), u1, v1, v2, u2);
    }
    
    public static void texture(Renderer r, Texture t, Vector2ic ab, Vector2dc cd, double u1, double v1, double u2, double v2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), u1, v1, v2, u2);
    }
    
    public static void texture(Renderer r, Texture t, Vector2fc ab, Vector2ic cd, double u1, double v1, double u2, double v2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), u1, v1, v2, u2);
    }
    
    public static void texture(Renderer r, Texture t, Vector2fc ab, Vector2fc cd, double u1, double v1, double u2, double v2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), u1, v1, v2, u2);
    }
    
    public static void texture(Renderer r, Texture t, Vector2fc ab, Vector2dc cd, double u1, double v1, double u2, double v2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), u1, v1, v2, u2);
    }
    
    public static void texture(Renderer r, Texture t, Vector2dc ab, Vector2ic cd, double u1, double v1, double u2, double v2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), u1, v1, v2, u2);
    }
    
    public static void texture(Renderer r, Texture t, Vector2dc ab, Vector2fc cd, double u1, double v1, double u2, double v2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), u1, v1, v2, u2);
    }
    
    public static void texture(Renderer r, Texture t, Vector2dc ab, Vector2dc cd, double u1, double v1, double u2, double v2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), u1, v1, v2, u2);
    }
    
    public static void texture(Renderer r, Texture t, double a, double b, Vector2ic cd, Vector2ic uv1, Vector2ic uv2)
    {
        r.texture(t, a, b, cd.x(), cd.y(), uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, double a, double b, Vector2ic cd, Vector2ic uv1, Vector2fc uv2)
    {
        r.texture(t, a, b, cd.x(), cd.y(), uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, double a, double b, Vector2ic cd, Vector2ic uv1, Vector2dc uv2)
    {
        r.texture(t, a, b, cd.x(), cd.y(), uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, double a, double b, Vector2ic cd, Vector2fc uv1, Vector2ic uv2)
    {
        r.texture(t, a, b, cd.x(), cd.y(), uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, double a, double b, Vector2ic cd, Vector2fc uv1, Vector2fc uv2)
    {
        r.texture(t, a, b, cd.x(), cd.y(), uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, double a, double b, Vector2ic cd, Vector2fc uv1, Vector2dc uv2)
    {
        r.texture(t, a, b, cd.x(), cd.y(), uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, double a, double b, Vector2ic cd, Vector2dc uv1, Vector2ic uv2)
    {
        r.texture(t, a, b, cd.x(), cd.y(), uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, double a, double b, Vector2ic cd, Vector2dc uv1, Vector2fc uv2)
    {
        r.texture(t, a, b, cd.x(), cd.y(), uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, double a, double b, Vector2ic cd, Vector2dc uv1, Vector2dc uv2)
    {
        r.texture(t, a, b, cd.x(), cd.y(), uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, double a, double b, Vector2fc cd, Vector2ic uv1, Vector2ic uv2)
    {
        r.texture(t, a, b, cd.x(), cd.y(), uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, double a, double b, Vector2fc cd, Vector2ic uv1, Vector2fc uv2)
    {
        r.texture(t, a, b, cd.x(), cd.y(), uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, double a, double b, Vector2fc cd, Vector2ic uv1, Vector2dc uv2)
    {
        r.texture(t, a, b, cd.x(), cd.y(), uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, double a, double b, Vector2fc cd, Vector2fc uv1, Vector2ic uv2)
    {
        r.texture(t, a, b, cd.x(), cd.y(), uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, double a, double b, Vector2fc cd, Vector2fc uv1, Vector2fc uv2)
    {
        r.texture(t, a, b, cd.x(), cd.y(), uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, double a, double b, Vector2fc cd, Vector2fc uv1, Vector2dc uv2)
    {
        r.texture(t, a, b, cd.x(), cd.y(), uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, double a, double b, Vector2fc cd, Vector2dc uv1, Vector2ic uv2)
    {
        r.texture(t, a, b, cd.x(), cd.y(), uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, double a, double b, Vector2fc cd, Vector2dc uv1, Vector2fc uv2)
    {
        r.texture(t, a, b, cd.x(), cd.y(), uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, double a, double b, Vector2fc cd, Vector2dc uv1, Vector2dc uv2)
    {
        r.texture(t, a, b, cd.x(), cd.y(), uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, double a, double b, Vector2dc cd, Vector2ic uv1, Vector2ic uv2)
    {
        r.texture(t, a, b, cd.x(), cd.y(), uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, double a, double b, Vector2dc cd, Vector2ic uv1, Vector2fc uv2)
    {
        r.texture(t, a, b, cd.x(), cd.y(), uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, double a, double b, Vector2dc cd, Vector2ic uv1, Vector2dc uv2)
    {
        r.texture(t, a, b, cd.x(), cd.y(), uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, double a, double b, Vector2dc cd, Vector2fc uv1, Vector2ic uv2)
    {
        r.texture(t, a, b, cd.x(), cd.y(), uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, double a, double b, Vector2dc cd, Vector2fc uv1, Vector2fc uv2)
    {
        r.texture(t, a, b, cd.x(), cd.y(), uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, double a, double b, Vector2dc cd, Vector2fc uv1, Vector2dc uv2)
    {
        r.texture(t, a, b, cd.x(), cd.y(), uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, double a, double b, Vector2dc cd, Vector2dc uv1, Vector2ic uv2)
    {
        r.texture(t, a, b, cd.x(), cd.y(), uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, double a, double b, Vector2dc cd, Vector2dc uv1, Vector2fc uv2)
    {
        r.texture(t, a, b, cd.x(), cd.y(), uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2ic ab, double c, double d, Vector2ic uv1, Vector2ic uv2)
    {
        r.texture(t, ab.x(), ab.y(), c, d, uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2ic ab, double c, double d, Vector2ic uv1, Vector2fc uv2)
    {
        r.texture(t, ab.x(), ab.y(), c, d, uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2ic ab, double c, double d, Vector2ic uv1, Vector2dc uv2)
    {
        r.texture(t, ab.x(), ab.y(), c, d, uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2ic ab, double c, double d, Vector2fc uv1, Vector2ic uv2)
    {
        r.texture(t, ab.x(), ab.y(), c, d, uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2ic ab, double c, double d, Vector2fc uv1, Vector2fc uv2)
    {
        r.texture(t, ab.x(), ab.y(), c, d, uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2ic ab, double c, double d, Vector2fc uv1, Vector2dc uv2)
    {
        r.texture(t, ab.x(), ab.y(), c, d, uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2ic ab, double c, double d, Vector2dc uv1, Vector2ic uv2)
    {
        r.texture(t, ab.x(), ab.y(), c, d, uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2ic ab, double c, double d, Vector2dc uv1, Vector2fc uv2)
    {
        r.texture(t, ab.x(), ab.y(), c, d, uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2ic ab, double c, double d, Vector2dc uv1, Vector2dc uv2)
    {
        r.texture(t, ab.x(), ab.y(), c, d, uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2fc ab, double c, double d, Vector2ic uv1, Vector2ic uv2)
    {
        r.texture(t, ab.x(), ab.y(), c, d, uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2fc ab, double c, double d, Vector2ic uv1, Vector2fc uv2)
    {
        r.texture(t, ab.x(), ab.y(), c, d, uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2fc ab, double c, double d, Vector2ic uv1, Vector2dc uv2)
    {
        r.texture(t, ab.x(), ab.y(), c, d, uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2fc ab, double c, double d, Vector2fc uv1, Vector2ic uv2)
    {
        r.texture(t, ab.x(), ab.y(), c, d, uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2fc ab, double c, double d, Vector2fc uv1, Vector2fc uv2)
    {
        r.texture(t, ab.x(), ab.y(), c, d, uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2fc ab, double c, double d, Vector2fc uv1, Vector2dc uv2)
    {
        r.texture(t, ab.x(), ab.y(), c, d, uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2fc ab, double c, double d, Vector2dc uv1, Vector2ic uv2)
    {
        r.texture(t, ab.x(), ab.y(), c, d, uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2fc ab, double c, double d, Vector2dc uv1, Vector2fc uv2)
    {
        r.texture(t, ab.x(), ab.y(), c, d, uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2fc ab, double c, double d, Vector2dc uv1, Vector2dc uv2)
    {
        r.texture(t, ab.x(), ab.y(), c, d, uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2dc ab, double c, double d, Vector2ic uv1, Vector2ic uv2)
    {
        r.texture(t, ab.x(), ab.y(), c, d, uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2dc ab, double c, double d, Vector2ic uv1, Vector2fc uv2)
    {
        r.texture(t, ab.x(), ab.y(), c, d, uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2dc ab, double c, double d, Vector2ic uv1, Vector2dc uv2)
    {
        r.texture(t, ab.x(), ab.y(), c, d, uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2dc ab, double c, double d, Vector2fc uv1, Vector2ic uv2)
    {
        r.texture(t, ab.x(), ab.y(), c, d, uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2dc ab, double c, double d, Vector2fc uv1, Vector2fc uv2)
    {
        r.texture(t, ab.x(), ab.y(), c, d, uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2dc ab, double c, double d, Vector2fc uv1, Vector2dc uv2)
    {
        r.texture(t, ab.x(), ab.y(), c, d, uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2dc ab, double c, double d, Vector2dc uv1, Vector2ic uv2)
    {
        r.texture(t, ab.x(), ab.y(), c, d, uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2dc ab, double c, double d, Vector2dc uv1, Vector2fc uv2)
    {
        r.texture(t, ab.x(), ab.y(), c, d, uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2ic ab, Vector2ic cd, double u1, double v1, Vector2ic uv2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), u1, v1, uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2ic ab, Vector2ic cd, double u1, double v1, Vector2fc uv2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), u1, v1, uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2ic ab, Vector2ic cd, double u1, double v1, Vector2dc uv2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), u1, v1, uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2ic ab, Vector2fc cd, double u1, double v1, Vector2ic uv2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), u1, v1, uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2ic ab, Vector2fc cd, double u1, double v1, Vector2fc uv2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), u1, v1, uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2ic ab, Vector2fc cd, double u1, double v1, Vector2dc uv2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), u1, v1, uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2ic ab, Vector2dc cd, double u1, double v1, Vector2ic uv2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), u1, v1, uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2ic ab, Vector2dc cd, double u1, double v1, Vector2fc uv2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), u1, v1, uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2ic ab, Vector2dc cd, double u1, double v1, Vector2dc uv2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), u1, v1, uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2fc ab, Vector2ic cd, double u1, double v1, Vector2ic uv2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), u1, v1, uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2fc ab, Vector2ic cd, double u1, double v1, Vector2fc uv2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), u1, v1, uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2fc ab, Vector2ic cd, double u1, double v1, Vector2dc uv2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), u1, v1, uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2fc ab, Vector2fc cd, double u1, double v1, Vector2ic uv2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), u1, v1, uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2fc ab, Vector2fc cd, double u1, double v1, Vector2fc uv2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), u1, v1, uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2fc ab, Vector2fc cd, double u1, double v1, Vector2dc uv2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), u1, v1, uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2fc ab, Vector2dc cd, double u1, double v1, Vector2ic uv2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), u1, v1, uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2fc ab, Vector2dc cd, double u1, double v1, Vector2fc uv2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), u1, v1, uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2fc ab, Vector2dc cd, double u1, double v1, Vector2dc uv2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), u1, v1, uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2dc ab, Vector2ic cd, double u1, double v1, Vector2ic uv2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), u1, v1, uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2dc ab, Vector2ic cd, double u1, double v1, Vector2fc uv2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), u1, v1, uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2dc ab, Vector2ic cd, double u1, double v1, Vector2dc uv2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), u1, v1, uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2dc ab, Vector2fc cd, double u1, double v1, Vector2ic uv2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), u1, v1, uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2dc ab, Vector2fc cd, double u1, double v1, Vector2fc uv2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), u1, v1, uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2dc ab, Vector2fc cd, double u1, double v1, Vector2dc uv2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), u1, v1, uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2dc ab, Vector2dc cd, double u1, double v1, Vector2ic uv2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), u1, v1, uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2dc ab, Vector2dc cd, double u1, double v1, Vector2fc uv2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), u1, v1, uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2ic ab, Vector2ic cd, Vector2ic uv1, double u2, double v2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), uv1.x(), uv1.y(), v2, u2);
    }
    
    public static void texture(Renderer r, Texture t, Vector2ic ab, Vector2ic cd, Vector2fc uv1, double u2, double v2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), uv1.x(), uv1.y(), v2, u2);
    }
    
    public static void texture(Renderer r, Texture t, Vector2ic ab, Vector2ic cd, Vector2dc uv1, double u2, double v2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), uv1.x(), uv1.y(), v2, u2);
    }
    
    public static void texture(Renderer r, Texture t, Vector2ic ab, Vector2fc cd, Vector2ic uv1, double u2, double v2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), uv1.x(), uv1.y(), v2, u2);
    }
    
    public static void texture(Renderer r, Texture t, Vector2ic ab, Vector2fc cd, Vector2fc uv1, double u2, double v2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), uv1.x(), uv1.y(), v2, u2);
    }
    
    public static void texture(Renderer r, Texture t, Vector2ic ab, Vector2fc cd, Vector2dc uv1, double u2, double v2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), uv1.x(), uv1.y(), v2, u2);
    }
    
    public static void texture(Renderer r, Texture t, Vector2ic ab, Vector2dc cd, Vector2ic uv1, double u2, double v2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), uv1.x(), uv1.y(), v2, u2);
    }
    
    public static void texture(Renderer r, Texture t, Vector2ic ab, Vector2dc cd, Vector2fc uv1, double u2, double v2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), uv1.x(), uv1.y(), v2, u2);
    }
    
    public static void texture(Renderer r, Texture t, Vector2ic ab, Vector2dc cd, Vector2dc uv1, double u2, double v2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), uv1.x(), uv1.y(), v2, u2);
    }
    
    public static void texture(Renderer r, Texture t, Vector2fc ab, Vector2ic cd, Vector2ic uv1, double u2, double v2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), uv1.x(), uv1.y(), v2, u2);
    }
    
    public static void texture(Renderer r, Texture t, Vector2fc ab, Vector2ic cd, Vector2fc uv1, double u2, double v2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), uv1.x(), uv1.y(), v2, u2);
    }
    
    public static void texture(Renderer r, Texture t, Vector2fc ab, Vector2ic cd, Vector2dc uv1, double u2, double v2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), uv1.x(), uv1.y(), v2, u2);
    }
    
    public static void texture(Renderer r, Texture t, Vector2fc ab, Vector2fc cd, Vector2ic uv1, double u2, double v2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), uv1.x(), uv1.y(), v2, u2);
    }
    
    public static void texture(Renderer r, Texture t, Vector2fc ab, Vector2fc cd, Vector2fc uv1, double u2, double v2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), uv1.x(), uv1.y(), v2, u2);
    }
    
    public static void texture(Renderer r, Texture t, Vector2fc ab, Vector2fc cd, Vector2dc uv1, double u2, double v2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), uv1.x(), uv1.y(), v2, u2);
    }
    
    public static void texture(Renderer r, Texture t, Vector2fc ab, Vector2dc cd, Vector2ic uv1, double u2, double v2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), uv1.x(), uv1.y(), v2, u2);
    }
    
    public static void texture(Renderer r, Texture t, Vector2fc ab, Vector2dc cd, Vector2fc uv1, double u2, double v2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), uv1.x(), uv1.y(), v2, u2);
    }
    
    public static void texture(Renderer r, Texture t, Vector2fc ab, Vector2dc cd, Vector2dc uv1, double u2, double v2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), uv1.x(), uv1.y(), v2, u2);
    }
    
    public static void texture(Renderer r, Texture t, Vector2dc ab, Vector2ic cd, Vector2ic uv1, double u2, double v2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), uv1.x(), uv1.y(), v2, u2);
    }
    
    public static void texture(Renderer r, Texture t, Vector2dc ab, Vector2ic cd, Vector2fc uv1, double u2, double v2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), uv1.x(), uv1.y(), v2, u2);
    }
    
    public static void texture(Renderer r, Texture t, Vector2dc ab, Vector2ic cd, Vector2dc uv1, double u2, double v2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), uv1.x(), uv1.y(), v2, u2);
    }
    
    public static void texture(Renderer r, Texture t, Vector2dc ab, Vector2fc cd, Vector2ic uv1, double u2, double v2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), uv1.x(), uv1.y(), v2, u2);
    }
    
    public static void texture(Renderer r, Texture t, Vector2dc ab, Vector2fc cd, Vector2fc uv1, double u2, double v2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), uv1.x(), uv1.y(), v2, u2);
    }
    
    public static void texture(Renderer r, Texture t, Vector2dc ab, Vector2fc cd, Vector2dc uv1, double u2, double v2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), uv1.x(), uv1.y(), v2, u2);
    }
    
    public static void texture(Renderer r, Texture t, Vector2dc ab, Vector2dc cd, Vector2ic uv1, double u2, double v2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), uv1.x(), uv1.y(), v2, u2);
    }
    
    public static void texture(Renderer r, Texture t, Vector2dc ab, Vector2dc cd, Vector2fc uv1, double u2, double v2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), uv1.x(), uv1.y(), v2, u2);
    }
    
    public static void texture(Renderer r, Texture t, Vector2ic ab, Vector2ic cd, Vector2ic uv1, Vector2ic uv2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2ic ab, Vector2ic cd, Vector2ic uv1, Vector2fc uv2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2ic ab, Vector2ic cd, Vector2ic uv1, Vector2dc uv2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2ic ab, Vector2ic cd, Vector2fc uv1, Vector2ic uv2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2ic ab, Vector2ic cd, Vector2fc uv1, Vector2fc uv2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2ic ab, Vector2ic cd, Vector2fc uv1, Vector2dc uv2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2ic ab, Vector2ic cd, Vector2dc uv1, Vector2ic uv2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2ic ab, Vector2ic cd, Vector2dc uv1, Vector2fc uv2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2ic ab, Vector2ic cd, Vector2dc uv1, Vector2dc uv2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2ic ab, Vector2fc cd, Vector2ic uv1, Vector2ic uv2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2ic ab, Vector2fc cd, Vector2ic uv1, Vector2fc uv2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2ic ab, Vector2fc cd, Vector2ic uv1, Vector2dc uv2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2ic ab, Vector2fc cd, Vector2fc uv1, Vector2ic uv2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2ic ab, Vector2fc cd, Vector2fc uv1, Vector2fc uv2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2ic ab, Vector2fc cd, Vector2fc uv1, Vector2dc uv2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2ic ab, Vector2fc cd, Vector2dc uv1, Vector2ic uv2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2ic ab, Vector2fc cd, Vector2dc uv1, Vector2fc uv2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2ic ab, Vector2fc cd, Vector2dc uv1, Vector2dc uv2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2ic ab, Vector2dc cd, Vector2ic uv1, Vector2ic uv2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2ic ab, Vector2dc cd, Vector2ic uv1, Vector2fc uv2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2ic ab, Vector2dc cd, Vector2ic uv1, Vector2dc uv2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2ic ab, Vector2dc cd, Vector2fc uv1, Vector2ic uv2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2ic ab, Vector2dc cd, Vector2fc uv1, Vector2fc uv2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2ic ab, Vector2dc cd, Vector2fc uv1, Vector2dc uv2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2ic ab, Vector2dc cd, Vector2dc uv1, Vector2ic uv2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2ic ab, Vector2dc cd, Vector2dc uv1, Vector2fc uv2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2ic ab, Vector2dc cd, Vector2dc uv1, Vector2dc uv2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2fc ab, Vector2ic cd, Vector2ic uv1, Vector2ic uv2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2fc ab, Vector2ic cd, Vector2ic uv1, Vector2fc uv2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2fc ab, Vector2ic cd, Vector2ic uv1, Vector2dc uv2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2fc ab, Vector2ic cd, Vector2fc uv1, Vector2ic uv2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2fc ab, Vector2ic cd, Vector2fc uv1, Vector2fc uv2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2fc ab, Vector2ic cd, Vector2fc uv1, Vector2dc uv2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2fc ab, Vector2ic cd, Vector2dc uv1, Vector2ic uv2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2fc ab, Vector2ic cd, Vector2dc uv1, Vector2fc uv2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2fc ab, Vector2ic cd, Vector2dc uv1, Vector2dc uv2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2fc ab, Vector2fc cd, Vector2ic uv1, Vector2ic uv2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2fc ab, Vector2fc cd, Vector2ic uv1, Vector2fc uv2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2fc ab, Vector2fc cd, Vector2ic uv1, Vector2dc uv2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2fc ab, Vector2fc cd, Vector2fc uv1, Vector2ic uv2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2fc ab, Vector2fc cd, Vector2fc uv1, Vector2fc uv2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2fc ab, Vector2fc cd, Vector2fc uv1, Vector2dc uv2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2fc ab, Vector2fc cd, Vector2dc uv1, Vector2ic uv2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2fc ab, Vector2fc cd, Vector2dc uv1, Vector2fc uv2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2fc ab, Vector2fc cd, Vector2dc uv1, Vector2dc uv2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2fc ab, Vector2dc cd, Vector2ic uv1, Vector2ic uv2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2fc ab, Vector2dc cd, Vector2ic uv1, Vector2fc uv2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2fc ab, Vector2dc cd, Vector2ic uv1, Vector2dc uv2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2fc ab, Vector2dc cd, Vector2fc uv1, Vector2ic uv2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2fc ab, Vector2dc cd, Vector2fc uv1, Vector2fc uv2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2fc ab, Vector2dc cd, Vector2fc uv1, Vector2dc uv2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2fc ab, Vector2dc cd, Vector2dc uv1, Vector2ic uv2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2fc ab, Vector2dc cd, Vector2dc uv1, Vector2fc uv2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2fc ab, Vector2dc cd, Vector2dc uv1, Vector2dc uv2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2dc ab, Vector2ic cd, Vector2ic uv1, Vector2ic uv2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2dc ab, Vector2ic cd, Vector2ic uv1, Vector2fc uv2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2dc ab, Vector2ic cd, Vector2ic uv1, Vector2dc uv2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2dc ab, Vector2ic cd, Vector2fc uv1, Vector2ic uv2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2dc ab, Vector2ic cd, Vector2fc uv1, Vector2fc uv2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2dc ab, Vector2ic cd, Vector2fc uv1, Vector2dc uv2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2dc ab, Vector2ic cd, Vector2dc uv1, Vector2ic uv2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2dc ab, Vector2ic cd, Vector2dc uv1, Vector2fc uv2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2dc ab, Vector2ic cd, Vector2dc uv1, Vector2dc uv2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2dc ab, Vector2fc cd, Vector2ic uv1, Vector2ic uv2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2dc ab, Vector2fc cd, Vector2ic uv1, Vector2fc uv2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2dc ab, Vector2fc cd, Vector2ic uv1, Vector2dc uv2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2dc ab, Vector2fc cd, Vector2fc uv1, Vector2ic uv2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2dc ab, Vector2fc cd, Vector2fc uv1, Vector2fc uv2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2dc ab, Vector2fc cd, Vector2fc uv1, Vector2dc uv2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2dc ab, Vector2fc cd, Vector2dc uv1, Vector2ic uv2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2dc ab, Vector2fc cd, Vector2dc uv1, Vector2fc uv2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2dc ab, Vector2fc cd, Vector2dc uv1, Vector2dc uv2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2dc ab, Vector2dc cd, Vector2ic uv1, Vector2ic uv2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2dc ab, Vector2dc cd, Vector2ic uv1, Vector2fc uv2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2dc ab, Vector2dc cd, Vector2ic uv1, Vector2dc uv2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2dc ab, Vector2dc cd, Vector2fc uv1, Vector2ic uv2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2dc ab, Vector2dc cd, Vector2fc uv1, Vector2fc uv2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2dc ab, Vector2dc cd, Vector2fc uv1, Vector2dc uv2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2dc ab, Vector2dc cd, Vector2dc uv1, Vector2ic uv2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2dc ab, Vector2dc cd, Vector2dc uv1, Vector2fc uv2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, Vector2dc ab, Vector2dc cd, Vector2dc uv1, Vector2dc uv2)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y(), uv1.x(), uv1.y(), uv2.y(), uv2.x());
    }
    
    public static void texture(Renderer r, Texture t, double x, double y, double u1, double v1, Vector2ic uv2)
    {
        r.texture(t, x, y, u1, v1, uv2.x(), uv2.y());
    }
    
    public static void texture(Renderer r, Texture t, double x, double y, double u1, double v1, Vector2fc uv2)
    {
        r.texture(t, x, y, u1, v1, uv2.x(), uv2.y());
    }
    
    public static void texture(Renderer r, Texture t, double x, double y, double u1, double v1, Vector2dc uv2)
    {
        r.texture(t, x, y, u1, v1, uv2.x(), uv2.y());
    }
    
    public static void texture(Renderer r, Texture t, double x, double y, Vector2ic uv1, double u2, double v2)
    {
        r.texture(t, x, y, uv1.x(), uv1.y(), u2, v2);
    }
    
    public static void texture(Renderer r, Texture t, double x, double y, Vector2fc uv1, double u2, double v2)
    {
        r.texture(t, x, y, uv1.x(), uv1.y(), u2, v2);
    }
    
    public static void texture(Renderer r, Texture t, double x, double y, Vector2dc uv1, double u2, double v2)
    {
        r.texture(t, x, y, uv1.x(), uv1.y(), u2, v2);
    }
    
    public static void texture(Renderer r, Texture t, Vector2ic pos, double u1, double v1, double u2, double v2)
    {
        r.texture(t, pos.x(), pos.y(), u1, v1, u2, v2);
    }
    
    public static void texture(Renderer r, Texture t, Vector2fc pos, double u1, double v1, double u2, double v2)
    {
        r.texture(t, pos.x(), pos.y(), u1, v1, u2, v2);
    }
    
    public static void texture(Renderer r, Texture t, Vector2dc pos, double u1, double v1, double u2, double v2)
    {
        r.texture(t, pos.x(), pos.y(), u1, v1, u2, v2);
    }
    
    public static void texture(Renderer r, Texture t, double x, double y, Vector2ic uv1, Vector2ic uv2)
    {
        r.texture(t, x, y, uv1.x(), uv1.y(), uv2.x(), uv2.y());
    }
    
    public static void texture(Renderer r, Texture t, double x, double y, Vector2ic uv1, Vector2fc uv2)
    {
        r.texture(t, x, y, uv1.x(), uv1.y(), uv2.x(), uv2.y());
    }
    
    public static void texture(Renderer r, Texture t, double x, double y, Vector2ic uv1, Vector2dc uv2)
    {
        r.texture(t, x, y, uv1.x(), uv1.y(), uv2.x(), uv2.y());
    }
    
    public static void texture(Renderer r, Texture t, double x, double y, Vector2fc uv1, Vector2ic uv2)
    {
        r.texture(t, x, y, uv1.x(), uv1.y(), uv2.x(), uv2.y());
    }
    
    public static void texture(Renderer r, Texture t, double x, double y, Vector2fc uv1, Vector2fc uv2)
    {
        r.texture(t, x, y, uv1.x(), uv1.y(), uv2.x(), uv2.y());
    }
    
    public static void texture(Renderer r, Texture t, double x, double y, Vector2fc uv1, Vector2dc uv2)
    {
        r.texture(t, x, y, uv1.x(), uv1.y(), uv2.x(), uv2.y());
    }
    
    public static void texture(Renderer r, Texture t, double x, double y, Vector2dc uv1, Vector2ic uv2)
    {
        r.texture(t, x, y, uv1.x(), uv1.y(), uv2.x(), uv2.y());
    }
    
    public static void texture(Renderer r, Texture t, double x, double y, Vector2dc uv1, Vector2fc uv2)
    {
        r.texture(t, x, y, uv1.x(), uv1.y(), uv2.x(), uv2.y());
    }
    
    public static void texture(Renderer r, Texture t, double x, double y, Vector2dc uv1, Vector2dc uv2)
    {
        r.texture(t, x, y, uv1.x(), uv1.y(), uv2.x(), uv2.y());
    }
    
    public static void texture(Renderer r, Texture t, Vector2ic pos, double u1, double v1, Vector2ic uv2)
    {
        r.texture(t, pos.x(), pos.y(), u1, v1, uv2.x(), uv2.y());
    }
    
    public static void texture(Renderer r, Texture t, Vector2ic pos, double u1, double v1, Vector2fc uv2)
    {
        r.texture(t, pos.x(), pos.y(), u1, v1, uv2.x(), uv2.y());
    }
    
    public static void texture(Renderer r, Texture t, Vector2ic pos, double u1, double v1, Vector2dc uv2)
    {
        r.texture(t, pos.x(), pos.y(), u1, v1, uv2.x(), uv2.y());
    }
    
    public static void texture(Renderer r, Texture t, Vector2fc pos, double u1, double v1, Vector2ic uv2)
    {
        r.texture(t, pos.x(), pos.y(), u1, v1, uv2.x(), uv2.y());
    }
    
    public static void texture(Renderer r, Texture t, Vector2fc pos, double u1, double v1, Vector2fc uv2)
    {
        r.texture(t, pos.x(), pos.y(), u1, v1, uv2.x(), uv2.y());
    }
    
    public static void texture(Renderer r, Texture t, Vector2fc pos, double u1, double v1, Vector2dc uv2)
    {
        r.texture(t, pos.x(), pos.y(), u1, v1, uv2.x(), uv2.y());
    }
    
    public static void texture(Renderer r, Texture t, Vector2dc pos, double u1, double v1, Vector2ic uv2)
    {
        r.texture(t, pos.x(), pos.y(), u1, v1, uv2.x(), uv2.y());
    }
    
    public static void texture(Renderer r, Texture t, Vector2dc pos, double u1, double v1, Vector2fc uv2)
    {
        r.texture(t, pos.x(), pos.y(), u1, v1, uv2.x(), uv2.y());
    }
    
    public static void texture(Renderer r, Texture t, Vector2dc pos, double u1, double v1, Vector2dc uv2)
    {
        r.texture(t, pos.x(), pos.y(), u1, v1, uv2.x(), uv2.y());
    }
    
    public static void texture(Renderer r, Texture t, Vector2ic pos, Vector2ic uv1, double u2, double v2)
    {
        r.texture(t, pos.x(), pos.y(), uv1.x(), uv1.y(), u2, v2);
    }
    
    public static void texture(Renderer r, Texture t, Vector2ic pos, Vector2fc uv1, double u2, double v2)
    {
        r.texture(t, pos.x(), pos.y(), uv1.x(), uv1.y(), u2, v2);
    }
    
    public static void texture(Renderer r, Texture t, Vector2ic pos, Vector2dc uv1, double u2, double v2)
    {
        r.texture(t, pos.x(), pos.y(), uv1.x(), uv1.y(), u2, v2);
    }
    
    public static void texture(Renderer r, Texture t, Vector2fc pos, Vector2ic uv1, double u2, double v2)
    {
        r.texture(t, pos.x(), pos.y(), uv1.x(), uv1.y(), u2, v2);
    }
    
    public static void texture(Renderer r, Texture t, Vector2fc pos, Vector2fc uv1, double u2, double v2)
    {
        r.texture(t, pos.x(), pos.y(), uv1.x(), uv1.y(), u2, v2);
    }
    
    public static void texture(Renderer r, Texture t, Vector2fc pos, Vector2dc uv1, double u2, double v2)
    {
        r.texture(t, pos.x(), pos.y(), uv1.x(), uv1.y(), u2, v2);
    }
    
    public static void texture(Renderer r, Texture t, Vector2dc pos, Vector2ic uv1, double u2, double v2)
    {
        r.texture(t, pos.x(), pos.y(), uv1.x(), uv1.y(), u2, v2);
    }
    
    public static void texture(Renderer r, Texture t, Vector2dc pos, Vector2fc uv1, double u2, double v2)
    {
        r.texture(t, pos.x(), pos.y(), uv1.x(), uv1.y(), u2, v2);
    }
    
    public static void texture(Renderer r, Texture t, Vector2dc pos, Vector2dc uv1, double u2, double v2)
    {
        r.texture(t, pos.x(), pos.y(), uv1.x(), uv1.y(), u2, v2);
    }
    
    public static void texture(Renderer r, Texture t, Vector2ic pos, Vector2ic uv1, Vector2ic uv2)
    {
        r.texture(t, pos.x(), pos.y(), uv1.x(), uv1.y(), uv2.x(), uv2.y());
    }
    
    public static void texture(Renderer r, Texture t, Vector2ic pos, Vector2ic uv1, Vector2fc uv2)
    {
        r.texture(t, pos.x(), pos.y(), uv1.x(), uv1.y(), uv2.x(), uv2.y());
    }
    
    public static void texture(Renderer r, Texture t, Vector2ic pos, Vector2ic uv1, Vector2dc uv2)
    {
        r.texture(t, pos.x(), pos.y(), uv1.x(), uv1.y(), uv2.x(), uv2.y());
    }
    
    public static void texture(Renderer r, Texture t, Vector2ic pos, Vector2fc uv1, Vector2ic uv2)
    {
        r.texture(t, pos.x(), pos.y(), uv1.x(), uv1.y(), uv2.x(), uv2.y());
    }
    
    public static void texture(Renderer r, Texture t, Vector2ic pos, Vector2fc uv1, Vector2fc uv2)
    {
        r.texture(t, pos.x(), pos.y(), uv1.x(), uv1.y(), uv2.x(), uv2.y());
    }
    
    public static void texture(Renderer r, Texture t, Vector2ic pos, Vector2fc uv1, Vector2dc uv2)
    {
        r.texture(t, pos.x(), pos.y(), uv1.x(), uv1.y(), uv2.x(), uv2.y());
    }
    
    public static void texture(Renderer r, Texture t, Vector2ic pos, Vector2dc uv1, Vector2ic uv2)
    {
        r.texture(t, pos.x(), pos.y(), uv1.x(), uv1.y(), uv2.x(), uv2.y());
    }
    
    public static void texture(Renderer r, Texture t, Vector2ic pos, Vector2dc uv1, Vector2fc uv2)
    {
        r.texture(t, pos.x(), pos.y(), uv1.x(), uv1.y(), uv2.x(), uv2.y());
    }
    
    public static void texture(Renderer r, Texture t, Vector2ic pos, Vector2dc uv1, Vector2dc uv2)
    {
        r.texture(t, pos.x(), pos.y(), uv1.x(), uv1.y(), uv2.x(), uv2.y());
    }
    
    public static void texture(Renderer r, Texture t, Vector2fc pos, Vector2ic uv1, Vector2ic uv2)
    {
        r.texture(t, pos.x(), pos.y(), uv1.x(), uv1.y(), uv2.x(), uv2.y());
    }
    
    public static void texture(Renderer r, Texture t, Vector2fc pos, Vector2ic uv1, Vector2fc uv2)
    {
        r.texture(t, pos.x(), pos.y(), uv1.x(), uv1.y(), uv2.x(), uv2.y());
    }
    
    public static void texture(Renderer r, Texture t, Vector2fc pos, Vector2ic uv1, Vector2dc uv2)
    {
        r.texture(t, pos.x(), pos.y(), uv1.x(), uv1.y(), uv2.x(), uv2.y());
    }
    
    public static void texture(Renderer r, Texture t, Vector2fc pos, Vector2fc uv1, Vector2ic uv2)
    {
        r.texture(t, pos.x(), pos.y(), uv1.x(), uv1.y(), uv2.x(), uv2.y());
    }
    
    public static void texture(Renderer r, Texture t, Vector2fc pos, Vector2fc uv1, Vector2fc uv2)
    {
        r.texture(t, pos.x(), pos.y(), uv1.x(), uv1.y(), uv2.x(), uv2.y());
    }
    
    public static void texture(Renderer r, Texture t, Vector2fc pos, Vector2fc uv1, Vector2dc uv2)
    {
        r.texture(t, pos.x(), pos.y(), uv1.x(), uv1.y(), uv2.x(), uv2.y());
    }
    
    public static void texture(Renderer r, Texture t, Vector2fc pos, Vector2dc uv1, Vector2ic uv2)
    {
        r.texture(t, pos.x(), pos.y(), uv1.x(), uv1.y(), uv2.x(), uv2.y());
    }
    
    public static void texture(Renderer r, Texture t, Vector2fc pos, Vector2dc uv1, Vector2fc uv2)
    {
        r.texture(t, pos.x(), pos.y(), uv1.x(), uv1.y(), uv2.x(), uv2.y());
    }
    
    public static void texture(Renderer r, Texture t, Vector2fc pos, Vector2dc uv1, Vector2dc uv2)
    {
        r.texture(t, pos.x(), pos.y(), uv1.x(), uv1.y(), uv2.x(), uv2.y());
    }
    
    public static void texture(Renderer r, Texture t, Vector2dc pos, Vector2ic uv1, Vector2ic uv2)
    {
        r.texture(t, pos.x(), pos.y(), uv1.x(), uv1.y(), uv2.x(), uv2.y());
    }
    
    public static void texture(Renderer r, Texture t, Vector2dc pos, Vector2ic uv1, Vector2fc uv2)
    {
        r.texture(t, pos.x(), pos.y(), uv1.x(), uv1.y(), uv2.x(), uv2.y());
    }
    
    public static void texture(Renderer r, Texture t, Vector2dc pos, Vector2ic uv1, Vector2dc uv2)
    {
        r.texture(t, pos.x(), pos.y(), uv1.x(), uv1.y(), uv2.x(), uv2.y());
    }
    
    public static void texture(Renderer r, Texture t, Vector2dc pos, Vector2fc uv1, Vector2ic uv2)
    {
        r.texture(t, pos.x(), pos.y(), uv1.x(), uv1.y(), uv2.x(), uv2.y());
    }
    
    public static void texture(Renderer r, Texture t, Vector2dc pos, Vector2fc uv1, Vector2fc uv2)
    {
        r.texture(t, pos.x(), pos.y(), uv1.x(), uv1.y(), uv2.x(), uv2.y());
    }
    
    public static void texture(Renderer r, Texture t, Vector2dc pos, Vector2fc uv1, Vector2dc uv2)
    {
        r.texture(t, pos.x(), pos.y(), uv1.x(), uv1.y(), uv2.x(), uv2.y());
    }
    
    public static void texture(Renderer r, Texture t, Vector2dc pos, Vector2dc uv1, Vector2ic uv2)
    {
        r.texture(t, pos.x(), pos.y(), uv1.x(), uv1.y(), uv2.x(), uv2.y());
    }
    
    public static void texture(Renderer r, Texture t, Vector2dc pos, Vector2dc uv1, Vector2fc uv2)
    {
        r.texture(t, pos.x(), pos.y(), uv1.x(), uv1.y(), uv2.x(), uv2.y());
    }
    
    public static void texture(Renderer r, Texture t, Vector2dc pos, Vector2dc uv1, Vector2dc uv2)
    {
        r.texture(t, pos.x(), pos.y(), uv1.x(), uv1.y(), uv2.x(), uv2.y());
    }
    
    public static void texture(Renderer r, Texture t, double a, double b, Vector2ic cd)
    {
        r.texture(t, a, b, cd.x(), cd.y());
    }
    
    public static void texture(Renderer r, Texture t, double a, double b, Vector2fc cd)
    {
        r.texture(t, a, b, cd.x(), cd.y());
    }
    
    public static void texture(Renderer r, Texture t, double a, double b, Vector2dc cd)
    {
        r.texture(t, a, b, cd.x(), cd.y());
    }
    
    public static void texture(Renderer r, Texture t, Vector2ic ab, double c, double d)
    {
        r.texture(t, ab.x(), ab.y(), c, d);
    }
    
    public static void texture(Renderer r, Texture t, Vector2fc ab, double c, double d)
    {
        r.texture(t, ab.x(), ab.y(), c, d);
    }
    
    public static void texture(Renderer r, Texture t, Vector2dc ab, double c, double d)
    {
        r.texture(t, ab.x(), ab.y(), c, d);
    }
    
    public static void texture(Renderer r, Texture t, Vector2ic ab, Vector2ic cd)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y());
    }
    
    public static void texture(Renderer r, Texture t, Vector2ic ab, Vector2fc cd)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y());
    }
    
    public static void texture(Renderer r, Texture t, Vector2ic ab, Vector2dc cd)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y());
    }
    
    public static void texture(Renderer r, Texture t, Vector2fc ab, Vector2ic cd)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y());
    }
    
    public static void texture(Renderer r, Texture t, Vector2fc ab, Vector2fc cd)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y());
    }
    
    public static void texture(Renderer r, Texture t, Vector2fc ab, Vector2dc cd)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y());
    }
    
    public static void texture(Renderer r, Texture t, Vector2dc ab, Vector2ic cd)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y());
    }
    
    public static void texture(Renderer r, Texture t, Vector2dc ab, Vector2fc cd)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y());
    }
    
    public static void texture(Renderer r, Texture t, Vector2dc ab, Vector2dc cd)
    {
        r.texture(t, ab.x(), ab.y(), cd.x(), cd.y());
    }
    
    public static void texture(Renderer r, Texture t, Vector2ic ab)
    {
        r.texture(t, ab.x(), ab.y());
    }
    
    public static void texture(Renderer r, Texture t, Vector2fc ab)
    {
        r.texture(t, ab.x(), ab.y());
    }
    
    public static void texture(Renderer r, Texture t, Vector2dc ab)
    {
        r.texture(t, ab.x(), ab.y());
    }
    
    public static void text(Renderer r, String text, double a, double b, Vector2ic cd)
    {
        r.text(text, a, b, cd.x(), cd.y());
    }
    
    public static void text(Renderer r, String text, double a, double b, Vector2fc cd)
    {
        r.text(text, a, b, cd.x(), cd.y());
    }
    
    public static void text(Renderer r, String text, double a, double b, Vector2dc cd)
    {
        r.text(text, a, b, cd.x(), cd.y());
    }
    
    public static void text(Renderer r, String text, Vector2ic ab, double c, double d)
    {
        r.text(text, ab.x(), ab.y(), c, d);
    }
    
    public static void text(Renderer r, String text, Vector2fc ab, double c, double d)
    {
        r.text(text, ab.x(), ab.y(), c, d);
    }
    
    public static void text(Renderer r, String text, Vector2dc ab, double c, double d)
    {
        r.text(text, ab.x(), ab.y(), c, d);
    }
    
    public static void text(Renderer r, String text, Vector2ic ab, Vector2ic cd)
    {
        r.text(text, ab.x(), ab.y(), cd.x(), cd.y());
    }
    
    public static void text(Renderer r, String text, Vector2ic ab, Vector2fc cd)
    {
        r.text(text, ab.x(), ab.y(), cd.x(), cd.y());
    }
    
    public static void text(Renderer r, String text, Vector2ic ab, Vector2dc cd)
    {
        r.text(text, ab.x(), ab.y(), cd.x(), cd.y());
    }
    
    public static void text(Renderer r, String text, Vector2fc ab, Vector2ic cd)
    {
        r.text(text, ab.x(), ab.y(), cd.x(), cd.y());
    }
    
    public static void text(Renderer r, String text, Vector2fc ab, Vector2fc cd)
    {
        r.text(text, ab.x(), ab.y(), cd.x(), cd.y());
    }
    
    public static void text(Renderer r, String text, Vector2fc ab, Vector2dc cd)
    {
        r.text(text, ab.x(), ab.y(), cd.x(), cd.y());
    }
    
    public static void text(Renderer r, String text, Vector2dc ab, Vector2ic cd)
    {
        r.text(text, ab.x(), ab.y(), cd.x(), cd.y());
    }
    
    public static void text(Renderer r, String text, Vector2dc ab, Vector2fc cd)
    {
        r.text(text, ab.x(), ab.y(), cd.x(), cd.y());
    }
    
    public static void text(Renderer r, String text, Vector2dc ab, Vector2dc cd)
    {
        r.text(text, ab.x(), ab.y(), cd.x(), cd.y());
    }
    
    public static void text(Renderer r, String text, Vector2ic pos)
    {
        r.text(text, pos.x(), pos.y());
    }
    
    public static void text(Renderer r, String text, Vector2fc pos)
    {
        r.text(text, pos.x(), pos.y());
    }
    
    public static void text(Renderer r, String text, Vector2dc pos)
    {
        r.text(text, pos.x(), pos.y());
    }
}
