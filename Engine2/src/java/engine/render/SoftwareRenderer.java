package engine.render;

import engine.Engine;
import engine.color.Color;
import engine.color.Colorc;
import engine.util.PairD;
import engine.util.PairI;
import org.joml.Vector4d;

import java.util.HashMap;
import java.util.HashSet;

import static engine.util.Util.*;

public class SoftwareRenderer extends Renderer
{
    private static final byte LINE_OVERLAP_NONE  = 0b00;
    private static final byte LINE_OVERLAP_MAJOR = 0b01;
    private static final byte LINE_OVERLAP_MINOR = 0b10;
    
    private static final Vector4d       VECTOR = new Vector4d();
    private static final Color          COLOR  = new Color();
    private static final HashSet<PairI> POINTS = new HashSet<>();
    
    protected SoftwareRenderer(Texture target)
    {
        super(target);
    }
    
    @Override
    public void finish()
    {
        this.target.upload();
    }
    
    @Override
    public void clear(Colorc clear)
    {
        this.target.clear(clear);
    }
    
    @Override
    public void drawPoint(double x, double y)
    {
        PairD p  = transform(x, y);
        int   xi = (int) p.a(), yi = (int) p.b();
        
        if (this.weight == 1)
        {
            pointImpl(xi, yi, this.stroke);
        }
        else
        {
            int min = ((int) this.weight - 1) >> 1, max = min + (isEven(this.weight) ? 1 : 0);
            xi += getDecimal(p.a()) >= 0.5 || isOdd(this.weight) ? 0 : -1;
            yi += getDecimal(p.b()) >= 0.5 || isOdd(this.weight) ? 0 : -1;
            for (int j = -min; j <= max; j++)
            {
                for (int i = -min; i <= max; i++)
                {
                    pointImpl(xi + i, yi + j, this.stroke);
                }
            }
        }
    }
    
    @Override
    public void drawLine(double x1, double y1, double x2, double y2)
    {
        PairD p1  = transform(x1, y1);
        int   x1i = (int) p1.a(), y1i = (int) p1.b();
        
        PairD p2  = transform(x2, y2);
        int   x2i = (int) p2.a(), y2i = (int) p2.b();
        
        lineImpl(x1i, y1i, x2i, y2i, (int) this.weight, LINE_OVERLAP_NONE);
        pointsImpl(this.stroke);
    }
    
    @Override
    public void drawBezier(double x1, double y1, double x2, double y2, double x3, double y3)
    {
        PairD p1  = transform(x1, y1);
        int   x1i = (int) p1.a(), y1i = (int) p1.b();
        
        PairD p2  = transform(x2, y2);
        int   x2i = (int) p2.a(), y2i = (int) p2.b();
        
        PairD p3  = transform(x3, y3);
        int   x3i = (int) p3.a(), y3i = (int) p3.b();
        
        int width = (int) this.weight;
        
        // TODO - Have a width
        // TODO - http://members.chello.at/~easyfilter/bresenham.html
        // TODO - https://stackoverflow.com/questions/24326531/how-to-generate-a-thick-bezier-curve
        // TODO - https://pomax.github.io/bezierinfo/#offsetting
        // TODO - http://brunoimbrizi.com/unbox/2015/03/offset-curve/
        int  sx = x3i - x2i, sy = y3i - y2i;
        long xx = x1i - x2i, yy = y1i - y2i, xy;         /* relative values for checks */
        
        double dx, dy, err, cur = xx * sy - yy * sx;                    /* curvature */
        
        assert (xx * sx <= 0 && yy * sy <= 0);  /* sign of gradient must not change */
        
        if (sx * (long) sx + sy * (long) sy > xx * xx + yy * yy)
        { /* begin with longer part */
            x3i = x1i;
            x1i = sx + x2i;
            y3i = y1i;
            y1i = sy + y2i;
            cur = -cur;  /* swap P0 P2 */
        }
        if (cur != 0)
        {                                    /* no straight line */
            xx += sx;
            xx *= sx = x1i < x3i ? 1 : -1;           /* x step direction */
            yy += sy;
            yy *= sy = y1i < y3i ? 1 : -1;           /* y step direction */
            xy       = 2 * xx * yy;
            xx *= xx;
            yy *= yy;          /* differences 2nd degree */
            if (cur * sx * sy < 0)
            {                           /* negated curvature? */
                xx  = -xx;
                yy  = -yy;
                xy  = -xy;
                cur = -cur;
            }
            dx  = 4.0 * sy * cur * (x2i - x1i) + xx - xy;             /* differences 1st degree */
            dy  = 4.0 * sx * cur * (y1i - y2i) + yy - xy;
            xx += xx;
            yy += yy;
            err = dx + dy + xy;                /* error 1st step */
            do
            {
                POINTS.add(new PairI(x1i, y1i)); /* plot curve */
                if (x1i == x3i && y1i == y3i) return;  /* last pixel -> curve finished */
                boolean yStep = 2 * err < dx;      /* save value for test of y step */
                if (2 * err > dy)
                {
                    x1i += sx;
                    dx -= xy;
                    err += dy += yy;
                } /* x step */
                if (yStep)
                {
                    y1i += sy;
                    dy -= xy;
                    err += dx += xx;
                } /* y step */
            } while (dy < dx);           /* gradient negates -> algorithm fails */
        }
        pointsImpl(this.stroke);
    }
    
    @Override
    public void drawEllipse(double x, double y, double w, double h)
    {
    
    }
    
    @Override
    public void fillEllipse(double x, double y, double w, double h)
    {
    
    }
    
    @Override
    public void drawCircle(double x, double y, double r)
    {
    
    }
    
    @Override
    public void fillCircle(double x, double y, double r)
    {
    
    }
    
    @Override
    public void drawPolygon(double[] coordinates)
    {
        int n = coordinates.length;
        
        if ((n & 1) == 1) throw new RuntimeException("Invalid coordinates. Must be an even number");
        if (n < 6) throw new RuntimeException("Invalid coordinates. Must have at least 3 coordinates");
        
        int x1, y1, x2, y2;
        
        PairD coord = transform(coordinates[n - 2], coordinates[n - 1]);
        x1 = (int) coord.a();
        y1 = (int) coord.b();
        for (int i = 0; i < n; i += 2)
        {
            coord = transform(coordinates[i], coordinates[i + 1]);
            x2    = (int) coord.a();
            y2    = (int) coord.b();
            
            lineImpl(x1, y1, x2, y2, 1, LINE_OVERLAP_NONE);
            
            x1 = x2;
            y1 = y2;
        }
        pointsImpl(this.stroke);
    }
    
    @Override
    public void fillPolygon(double[] coordinates)
    {
        int n = coordinates.length;
        
        if ((n & 1) == 1) throw new RuntimeException("Invalid coordinates. Must be an even number");
        if (n < 6) throw new RuntimeException("Invalid coordinates. Must have at least 3 coordinates");
        
        int minX, maxX, minY, maxY;
        int x1, y1, x2, y2;
        
        PairD cord = transform(coordinates[n - 2], coordinates[n - 1]);
        minX = maxX = x1 = (int) cord.a();
        minY = maxY = y1 = (int) cord.b();
        for (int i = 0; i < n; i += 2)
        {
            cord = transform(coordinates[i], coordinates[i + 1]);
            x2   = (int) cord.a();
            y2   = (int) cord.b();
            minX = Math.min(minX, x2);
            maxX = Math.max(maxX, x2);
            minY = Math.min(minY, y2);
            maxY = Math.max(maxY, y2);
            lineImpl(x1, y1, x2, y2, 1, LINE_OVERLAP_NONE);
            x1 = x2;
            y1 = y2;
        }
        
        HashMap<Integer, PairI> xMap = new HashMap<>(), yMap = new HashMap<>();
        PairI                   xPair, yPair;
        for (PairI point : SoftwareRenderer.POINTS)
        {
            int x = point.a, y = point.b;
            if (!xMap.containsKey(x)) xMap.put(x, new PairI(y, y));
            if (!yMap.containsKey(y)) yMap.put(y, new PairI(x, x));
            xPair = xMap.get(x);
            yPair = yMap.get(y);
            
            xPair.a = Math.min(xPair.a, y + 1);
            xPair.b = Math.max(xPair.b, y - 1);
            yPair.a = Math.min(yPair.a, x + 1);
            yPair.b = Math.max(yPair.b, x - 1);
        }
        for (int y : yMap.keySet())
        {
            xPair = yMap.get(y);
            for (int x = xPair.a; x <= xPair.b; x++)
            {
                yPair = xMap.get(x);
                if (yPair.a <= y && y <= yPair.b) SoftwareRenderer.POINTS.add(new PairI(x, y));
            }
        }
        pointsImpl(this.fill);
        
        // ArrayList<Edge> edgeTable  = new ArrayList<>();
        // ArrayList<Edge> activeList = new ArrayList<>();
        //
        // int minX, maxX, minY, maxY;
        // int x1, y1, x2, y2;
        //
        // PairD cord = transform(coordinates[n - 2], coordinates[n - 1]);
        // minX = maxX = x1 = (int) cord.a();
        // minY = maxY = y1 = (int) cord.b();
        // for (int i = 0; i < n; i += 2)
        // {
        //     cord = transform(coordinates[i], coordinates[i + 1]);
        //     x2   = (int) cord.a();
        //     y2   = (int) cord.b();
        //     minX = Math.min(minX, x2);
        //     maxX = Math.max(maxX, x2);
        //     minY = Math.min(minY, y2);
        //     maxY = Math.max(maxY, y2);
        //     if (y1 != y2) edgeTable.add(new Edge(x1, y1, x2, y2));
        //     x1 = x2;
        //     y1 = y2;
        // }
        // int scanLine = minY;
        // if (edgeTable.size() == 0) lineImpl(minX, scanLine, maxX, scanLine, 1, LINE_OVERLAP_NONE);
        // edgeTable.sort(Comparator.comparingInt(o -> o.yMin));
        //
        // while (edgeTable.size() > 0)
        // {
        //     for (Edge edge : edgeTable) if (edge.yMin == scanLine) activeList.add(edge);
        //     for (Edge edge : activeList)
        //     {
        //         while (edge.y < scanLine)
        //         {
        //             int e2 = edge.sum << 1;
        //             if (e2 <= edge.dx)
        //             {
        //                 edge.y++;
        //                 edge.sum += edge.dx;
        //             }
        //             if (e2 >= -edge.dy)
        //             {
        //                 edge.x += edge.sign;
        //                 edge.sum -= edge.dy;
        //             }
        //             if (edge.x == edge.xMax && edge.y == edge.yMax) break;
        //         }
        //     }
        //     activeList.sort((o1, o2) -> {
        //         if (o1.x < o2.x) return -1;
        //         if (o1.x > o2.x) return 1;
        //         if (o1.sign < o2.sign) return -1;
        //         if (o1.sign > o2.sign) return 1;
        //         if (o1.slope < o2.slope) return -1;
        //         if (o1.slope > o2.slope) return 1;
        //         return Integer.compare(o1.xMax, o2.xMax);
        //     });
        //
        //     minX = Integer.MAX_VALUE;
        //     minY = Integer.MAX_VALUE;
        //     maxY = Integer.MIN_VALUE;
        //     for (Edge edge : activeList)
        //     {
        //         int x = minXBresenham(edge);
        //         if (minX == Integer.MIN_VALUE || (edge.yMin < minY && minX == x))
        //         {
        //             minX = x;
        //             minY = edge.yMin;
        //             maxY = edge.yMax;
        //         }
        //         else if (maxY != edge.yMin)
        //         {
        //             x = maxXBresenham(edge);
        //             lineImpl(minX, scanLine, x, scanLine, 1, LINE_OVERLAP_NONE);
        //             minX = Integer.MIN_VALUE;
        //         }
        //     }
        //
        //     scanLine++;
        //     for (int i = 0; i < activeList.size(); i++)
        //     {
        //         Edge edge = activeList.get(i);
        //         if (edge.yMax < scanLine)
        //         {
        //             activeList.remove(edge);
        //             edgeTable.remove(edge);
        //             i--;
        //         }
        //     }
        // }
        // pointsImpl(this.fill);
    }
    
    private void pointImpl(int x, int y, Colorc color)
    {
        if (this.blend)
        {
            this.target.setPixel(x, y, Engine.blend().blend(color, this.target.getPixel(x, y), COLOR));
        }
        else
        {
            this.target.setPixel(x, y, color);
        }
    }
    
    private void pointsImpl(Colorc color)
    {
        for (PairI point : SoftwareRenderer.POINTS) pointImpl(point.a(), point.b(), color);
        SoftwareRenderer.POINTS.clear();
    }
    
    private void lineImpl(int x1, int y1, int x2, int y2, int thickness, int aOverlap)
    {
        if (y1 > y2)
        {
            int temp = y2;
            y2   = y1;
            y1   = temp;
            temp = x2;
            x2   = x1;
            x1   = temp;
        }
        
        int dx = Math.abs(x2 - x1), sx = x1 < x2 ? 1 : -1;
        int dy = Math.abs(y2 - y1), sy = y1 < y2 ? 1 : -1;
        
        if (thickness == 1)
        {
            if (dx == 0)
            {
                while (true)
                {
                    SoftwareRenderer.POINTS.add(new PairI(x1, y1));
                    if (y1 == y2) break;
                    y1 += sy;
                }
            }
            else if (dy == 0)
            {
                while (true)
                {
                    SoftwareRenderer.POINTS.add(new PairI(x1, y1));
                    if (x1 == x2) break;
                    x1 += sx;
                }
            }
            else
            {
                int err = dx - dy, e2, c;
                while (true)
                {
                    SoftwareRenderer.POINTS.add(new PairI(x1, y1));
                    if (x1 == x2 && y1 == y2) break;
                    e2 = err << 1;
                    c  = 0;
                    if (e2 >= -dy)
                    {
                        err -= dy;
                        x1 += sx;
                        c++;
                    }
                    if (e2 <= dx)
                    {
                        err += dx;
                        y1 += sy;
                        c++;
                    }
                    if (c == 2)
                    {
                        if (dx > dy)
                        {
                            
                            if ((aOverlap & LINE_OVERLAP_MAJOR) > 0) SoftwareRenderer.POINTS.add(new PairI(x1, y1 - sy));
                            if ((aOverlap & LINE_OVERLAP_MINOR) > 0) SoftwareRenderer.POINTS.add(new PairI(x1 - sx, y1));
                        }
                        else
                        {
                            if ((aOverlap & LINE_OVERLAP_MAJOR) > 0) SoftwareRenderer.POINTS.add(new PairI(x1 - sx, y1));
                            if ((aOverlap & LINE_OVERLAP_MINOR) > 0) SoftwareRenderer.POINTS.add(new PairI(x1, y1 - sy));
                        }
                    }
                }
            }
        }
        else
        {
            int halfWidth = thickness >> 1;
            
            if (sx < 0 == sy < 0)
            {
                if (dx >= dy)
                {
                    halfWidth = (thickness - 1) - halfWidth;
                    sy        = -sy;
                }
                else
                {
                    sx = -sx;
                }
            }
            else
            {
                if (dx >= dy)
                {
                    sx = -sx;
                }
                else
                {
                    halfWidth = (thickness - 1) - halfWidth;
                    sy        = -sy;
                }
            }
            
            int i, err, e2, overlap, c;
            err = dx - dy;
            for (i = halfWidth; i > 0; i--)
            {
                e2 = err << 1;
                if (e2 >= -dy)
                {
                    err -= dy;
                    x1 -= sx;
                    x2 -= sx;
                }
                if (e2 <= dx)
                {
                    err += dx;
                    y1 -= sy;
                    y2 -= sy;
                }
            }
            lineImpl(x1, y1, x2, y2, 1, LINE_OVERLAP_NONE);
            err = dx - dy;
            for (i = thickness; i > 1; i--)
            {
                e2      = err << 1;
                overlap = LINE_OVERLAP_NONE;
                c       = 0;
                if (e2 >= -dy)
                {
                    err -= dy;
                    x1 += sx;
                    x2 += sx;
                    c++;
                }
                if (e2 <= dx)
                {
                    err += dx;
                    y1 += sy;
                    y2 += sy;
                    c++;
                }
                if (c == 2) overlap = LINE_OVERLAP_MAJOR;
                lineImpl(x1, y1, x2, y2, 1, overlap);
            }
        }
    }
    
    private PairD transform(double x, double y)
    {
        this.viewMatrix.transform(SoftwareRenderer.VECTOR.set(x, y, 0, 1));
        return new PairD(SoftwareRenderer.VECTOR.x, SoftwareRenderer.VECTOR.y);
    }
    
    private static class Edge
    {
        int xMin, yMin, xMax, yMax, x, y, sign, dx, dy, sum, slope;
        
        public Edge(int x1, int y1, int x2, int y2)
        {
            boolean maxPoint = y2 >= y1;
            this.xMin  = this.x = !maxPoint ? x2 : x1;
            this.yMin  = this.y = !maxPoint ? y2 : y1;
            this.xMax  = maxPoint ? x2 : x1;
            this.yMax  = maxPoint ? y2 : y1;
            this.sign  = Integer.signum(this.xMax - this.xMin);
            this.dy    = Math.abs(y2 - y1);
            this.dx    = Math.abs(x2 - x1);
            this.sum   = this.dx - this.dy;
            this.slope = this.dx != 0 ? this.dy * 1000 / this.dx : Integer.MAX_VALUE;
        }
    }
    
    private int minXBresenham(Edge edge)
    {
        int tempX   = edge.x;
        int tempSum = edge.sum;
        while (true)
        {
            int e2 = tempSum << 1;
            if (e2 <= edge.dx) return tempX;
            if (e2 >= -edge.dy)
            {
                if (tempX + edge.sign > tempX) return tempX;
                tempX += edge.sign;
                tempSum -= edge.dy;
            }
            if (tempX == edge.xMax && edge.y == edge.yMax) return tempX;
        }
    }
    
    private int maxXBresenham(Edge edge)
    {
        int tempX   = edge.x;
        int tempSum = edge.sum;
        while (true)
        {
            int e2 = tempSum << 1;
            if (e2 <= edge.dx) return tempX;
            if (e2 >= -edge.dy)
            {
                if (tempX + edge.sign < tempX) return tempX;
                tempX += edge.sign;
                tempSum -= edge.dy;
            }
            if (tempX == edge.xMax && edge.y == edge.yMax) return tempX;
        }
    }
}
