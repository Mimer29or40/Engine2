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
        super.finish();
        
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
        int   xi = (int) round(p.a()), yi = (int) round(p.b());
        
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
        int   x1i = (int) round(p1.a()), y1i = (int) round(p1.b());
        
        PairD p2  = transform(x2, y2);
        int   x2i = (int) round(p2.a()), y2i = (int) round(p2.b());
        
        lineImpl(x1i, y1i, x2i, y2i, (int) this.weight, LINE_OVERLAP_NONE);
        pointsImpl(this.stroke);
    }
    
    @Override
    public void drawBezier(double x1, double y1, double x2, double y2, double x3, double y3)
    {
        PairD p1  = transform(x1, y1);
        int   x1i = (int) round(p1.a()), y1i = (int) round(p1.b());
        
        PairD p2  = transform(x2, y2);
        int   x2i = (int) round(p2.a()), y2i = (int) round(p2.b());
        
        PairD p3  = transform(x3, y3);
        int   x3i = (int) round(p3.a()), y3i = (int) round(p3.b());
        
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
                SoftwareRenderer.POINTS.add(new PairI(x1i, y1i)); /* plot curve */
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
    public void drawTriangle(double x1, double y1, double x2, double y2, double x3, double y3)
    {
        drawPolygon(new double[] {x1, y1, x2, y2, x3, y3});
    }
    
    @Override
    public void fillTriangle(double x1, double y1, double x2, double y2, double x3, double y3)
    {
        fillPolygon(new double[] {x1, y1, x2, y2, x3, y3});
    }
    
    @Override
    public void drawSquare(double x, double y, double w)
    {
        drawPolygon(new double[] {x, y, x + w, y, x + w, y + w, x, y + w});
    }
    
    @Override
    public void fillSquare(double x, double y, double w)
    {
        fillPolygon(new double[] {x, y, x + w, y, x + w, y + w, x, y + w});
    }
    
    @Override
    public void drawRect(double x, double y, double w, double h)
    {
        drawPolygon(new double[] {x, y, x + w, y, x + w, y + h, x, y + h});
    }
    
    @Override
    public void fillRect(double x, double y, double w, double h)
    {
        fillPolygon(new double[] {x, y, x + w, y, x + w, y + h, x, y + h});
    }
    
    @Override
    public void drawQuad(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4)
    {
        drawPolygon(new double[] {x1, y1, x2, y2, x3, y3, x4, y4});
    }
    
    @Override
    public void fillQuad(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4)
    {
        fillPolygon(new double[] {x1, y1, x2, y2, x3, y3, x4, y4});
    }
    
    @Override
    public void drawPolygon(double[] points)
    {
        drawPolygonImpl(points, true);
    }
    
    @Override
    public void fillPolygon(double[] points)
    {
        int n = points.length;
        int x1, y1, x2, y2;
        
        PairD pt = transform(points[n - 2], points[n - 1]);
        x1 = (int) round(pt.a());
        y1 = (int) round(pt.b());
        for (int i = 0; i < n; i += 2)
        {
            pt = transform(points[i], points[i + 1]);
            x2 = (int) round(pt.a());
            y2 = (int) round(pt.b());
            lineImpl(x1, y1, x2, y2, 1, LINE_OVERLAP_NONE);
            x1 = x2;
            y1 = y2;
        }
        fillBetweenLines();
        pointsImpl(this.fill);
    }
    
    @Override
    public void drawCircle(double x, double y, double r)
    {
        drawPolygon(getCirclePoints(x, y, r, r));
    }
    
    @Override
    public void fillCircle(double x, double y, double r)
    {
        fillPolygon(getCirclePoints(x, y, r, r));
    }
    
    @Override
    public void drawEllipse(double x, double y, double rx, double ry)
    {
        drawPolygon(getCirclePoints(x, y, rx, ry));
    }
    
    @Override
    public void fillEllipse(double x, double y, double rx, double ry)
    {
        fillPolygon(getCirclePoints(x, y, rx, ry));
    }
    
    @Override
    public void drawArc(double x, double y, double rx, double ry, double start, double stop)
    {
        int modifier = (int) (180. * Math.abs(stop - start) / Math.PI);
        if (modifier == 0) return;
        int      RESOLUTION = Math.max(2, (int) Math.max(rx, ry) / 2) * 360 / modifier;
        double[] points     = new double[RESOLUTION * 2 + (this.arcMode == ArcMode.PIE ? 2 : 0)];
        for (int i = 0; i < RESOLUTION; i++)
        {
            double angle = start + (stop - start) * (double) i / (double) (RESOLUTION - 1);
            points[(2 * i)]     = x + Math.cos(angle) * rx;
            points[(2 * i) + 1] = y + Math.sin(angle) * ry;
        }
        if (this.arcMode == ArcMode.PIE)
        {
            points[points.length - 2] = x;
            points[points.length - 1] = y;
        }
        drawPolygonImpl(points, this.arcMode == ArcMode.CHORD || this.arcMode == ArcMode.PIE);
    }
    
    @Override
    public void fillArc(double x, double y, double rx, double ry, double start, double stop)
    {
        int modifier = (int) (180. * Math.abs(stop - start) / Math.PI);
        if (modifier == 0) return;
        int      RESOLUTION = Math.max(2, (int) Math.max(rx, ry) / 2) * 360 / modifier;
        double[] points     = new double[RESOLUTION * 2 + (this.arcMode == ArcMode.DEFAULT || this.arcMode == ArcMode.PIE ? 2 : 0)];
        for (int i = 0; i < RESOLUTION; i++)
        {
            double angle = start + (stop - start) * (double) i / (double) (RESOLUTION - 1);
            points[(2 * i)]     = x + Math.cos(angle) * rx;
            points[(2 * i) + 1] = y + Math.sin(angle) * ry;
        }
        if (this.arcMode == ArcMode.DEFAULT || this.arcMode == ArcMode.PIE)
        {
            points[points.length - 2] = x;
            points[points.length - 1] = y;
        }
        fillPolygon(points);
    }
    
    @Override
    public void drawTexture(Texture texture, double x, double y, double w, double h, double u, double v, double uw, double vh)
    {
        if (w <= 0 || h <= 0 || uw <= 0 || vh <= 0) return;
        
        PairD topLeft     = transform(x, y);
        PairD topRight    = transform(x + w, y);
        PairD bottomLeft  = transform(x, y + h);
        PairD bottomRight = transform(x + w, y + h);
        
        int topLeftX     = (int) round(topLeft.a());
        int topLeftY     = (int) round(topLeft.b());
        int topRightX    = (int) round(topRight.a());
        int topRightY    = (int) round(topRight.b());
        int bottomLeftX  = (int) round(bottomLeft.a());
        int bottomLeftY  = (int) round(bottomLeft.b());
        int bottomRightX = (int) round(bottomRight.a());
        int bottomRightY = (int) round(bottomRight.b());
        
        int ui  = (int) round(u * texture.width());
        int vi  = (int) round(v * texture.height());
        int uwi = (int) round(uw * texture.width());
        int vhi = (int) round(vh * texture.height());
        
        lineImpl(topLeftX, topLeftY, topRightX, topRightY, 1, LINE_OVERLAP_NONE);
        lineImpl(topRightX, topRightY, bottomRightX, bottomRightY, 1, LINE_OVERLAP_NONE);
        lineImpl(bottomRightX, bottomRightY, bottomLeftX, bottomLeftY, 1, LINE_OVERLAP_NONE);
        lineImpl(bottomLeftX, bottomLeftY, topLeftX, topLeftY, 1, LINE_OVERLAP_NONE);
        fillBetweenLines();
        
        int xAxisX   = topRightX - topLeftX;
        int xAxisY   = topRightY - topLeftY;
        int yAxisX   = bottomLeftX - topLeftX;
        int yAxisY   = bottomLeftY - topLeftY;
        int xAxisLen = xAxisX * xAxisX + xAxisY * xAxisY;
        int yAxisLen = yAxisX * yAxisX + yAxisY * yAxisY;
        if (xAxisLen == 0) xAxisLen = 1;
        if (yAxisLen == 0) yAxisLen = 1;
        
        for (PairI point : SoftwareRenderer.POINTS)
        {
            int dx    = point.a() - topLeftX;
            int dy    = point.b() - topLeftY;
            int textX = ui + ((dx * xAxisX + dy * xAxisY) * uwi / xAxisLen);
            int textY = vi + ((dx * yAxisX + dy * yAxisY) * vhi / yAxisLen);
            pointImpl(point.a(), point.b(), texture.getPixel(textX, textY));
        }
        SoftwareRenderer.POINTS.clear();
    }
    
    @Override
    public void drawText(String text, double x, double y)
    {
        double[] vertices = this.font.renderText(text);
        for (int i = 0, lineSize = text.length(); i < lineSize; i++)
        {
            int index = i * 8;
            
            double xPos = vertices[index] + x;
            double yPos = vertices[index + 1] + y;
            double w    = vertices[index + 2];
            double h    = vertices[index + 3];
            double u    = vertices[index + 4];
            double v    = vertices[index + 5];
            double uw   = vertices[index + 6];
            double vh   = vertices[index + 7];
            
            PairD topLeft     = transform(xPos, yPos);
            PairD topRight    = transform(xPos + w, yPos);
            PairD bottomLeft  = transform(xPos, yPos + h);
            PairD bottomRight = transform(xPos + w, yPos + h);
            
            int topLeftX     = (int) round(topLeft.a());
            int topLeftY     = (int) round(topLeft.b());
            int topRightX    = (int) round(topRight.a());
            int topRightY    = (int) round(topRight.b());
            int bottomLeftX  = (int) round(bottomLeft.a());
            int bottomLeftY  = (int) round(bottomLeft.b());
            int bottomRightX = (int) round(bottomRight.a());
            int bottomRightY = (int) round(bottomRight.b());
            
            int ui  = (int) round(u * this.font.getBitmap().width());
            int vi  = (int) round(v * this.font.getBitmap().height());
            int uwi = (int) round(uw * this.font.getBitmap().width());
            int vhi = (int) round(vh * this.font.getBitmap().height());
            
            lineImpl(topLeftX, topLeftY, topRightX, topRightY, 1, LINE_OVERLAP_NONE);
            lineImpl(topRightX, topRightY, bottomRightX, bottomRightY, 1, LINE_OVERLAP_NONE);
            lineImpl(bottomRightX, bottomRightY, bottomLeftX, bottomLeftY, 1, LINE_OVERLAP_NONE);
            lineImpl(bottomLeftX, bottomLeftY, topLeftX, topLeftY, 1, LINE_OVERLAP_NONE);
            fillBetweenLines();
            
            int xAxisX   = topRightX - topLeftX;
            int xAxisY   = topRightY - topLeftY;
            int yAxisX   = bottomLeftX - topLeftX;
            int yAxisY   = bottomLeftY - topLeftY;
            int xAxisLen = xAxisX * xAxisX + xAxisY * xAxisY;
            int yAxisLen = yAxisX * yAxisX + yAxisY * yAxisY;
            if (xAxisLen == 0) xAxisLen = 1;
            if (yAxisLen == 0) yAxisLen = 1;
            
            for (PairI point : SoftwareRenderer.POINTS)
            {
                int dx    = point.a() - topLeftX;
                int dy    = point.b() - topLeftY;
                int textX = ui + ((dx * xAxisX + dy * xAxisY) * uwi / xAxisLen);
                int textY = vi + ((dx * yAxisX + dy * yAxisY) * vhi / yAxisLen);
                int red   = this.font.getBitmap().getPixel(textX, textY).r();
                if (red > 0) pointImpl(point.a(), point.b(), SoftwareRenderer.COLOR.set(this.fill).scale((double) red / 255, true));
            }
            SoftwareRenderer.POINTS.clear();
        }
    }
    
    @Override
    public int[] loadPixels()
    {
        for (int i = 0, n = this.pixels.length; i < n; i++)
        {
            this.pixels[i] = this.target.data().get(i) & 0xFF;
        }
        return this.pixels;
    }
    
    @Override
    public void updatePixels()
    {
        for (int i = 0, n = this.pixels.length; i < n; i++)
        {
            this.target.data().put(i, (byte) (this.pixels[i] & 0xFF));
        }
    }
    
    private void pointImpl(int x, int y, Colorc color)
    {
        if (this.enableBlend)
        {
            this.target.setPixel(x, y, Engine.blend().blend(color, this.target.getPixel(x, y), SoftwareRenderer.COLOR));
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
        // if (y1 > y2)
        // {
        //     int temp = y2;
        //     y2   = y1;
        //     y1   = temp;
        //     temp = x2;
        //     x2   = x1;
        //     x1   = temp;
        // }
        
        if (thickness == 1)
        {
            int dx = Math.abs(x2 - x1), sx = x1 < x2 ? 1 : -1;
            int dy = Math.abs(y2 - y1), sy = y1 < y2 ? 1 : -1;
            
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
            int dx = Math.abs(y2 - y1), sx = x1 < x2 ? 1 : -1;
            int dy = Math.abs(x2 - x1), sy = y1 < y2 ? 1 : -1;
            
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
            for (i = 0; i < halfWidth; i++)
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
                if (c == 2) overlap = LINE_OVERLAP_MAJOR | LINE_OVERLAP_MINOR;
                lineImpl(x1, y1, x2, y2, 1, overlap);
            }
        }
    }
    
    private void drawPolygonImpl(double[] points, boolean connected)
    {
        int n = points.length;
        int x1, y1, x2, y2;
        
        int start = connected ? 0 : 2;
        
        PairD pt = connected ? transform(points[n - 2], points[n - 1]) : transform(points[0], points[1]);
        x1 = (int) round(pt.a());
        y1 = (int) round(pt.b());
        for (int i = start; i < n; i += 2)
        {
            pt = transform(points[i], points[i + 1]);
            x2 = (int) round(pt.a());
            y2 = (int) round(pt.b());
            lineImpl(x1, y1, x2, y2, (int) this.weight, LINE_OVERLAP_NONE);
            x1 = x2;
            y1 = y2;
        }
        pointsImpl(this.stroke);
    }
    
    private double[] getCirclePoints(double x, double y, double rx, double ry)
    {
        int      RESOLUTION = Math.max(16, (int) Math.max(rx, ry) / 2);
        double   TWO_PI     = 2.0 * Math.PI;
        double[] points     = new double[RESOLUTION * 2];
        for (int i = 0; i < RESOLUTION; i++)
        {
            double angle = TWO_PI * (double) i / (double) RESOLUTION;
            points[(2 * i)]     = x + Math.cos(angle) * rx;
            points[(2 * i) + 1] = y + Math.sin(angle) * ry;
        }
        return points;
    }
    
    private void fillBetweenLines()
    {
        HashMap<Integer, PairI> xMap = new HashMap<>(), yMap = new HashMap<>();
        
        PairI xPair, yPair;
        for (PairI point : SoftwareRenderer.POINTS)
        {
            if (!xMap.containsKey(point.a)) xMap.put(point.a, new PairI(point.b, point.b));
            if (!yMap.containsKey(point.b)) yMap.put(point.b, new PairI(point.a, point.a));
            xPair = xMap.get(point.a);
            yPair = yMap.get(point.b);
            
            xPair.a = Math.min(xPair.a, point.b + 1);
            xPair.b = Math.max(xPair.b, point.b - 1);
            yPair.a = Math.min(yPair.a, point.a + 1);
            yPair.b = Math.max(yPair.b, point.a - 1);
        }
        for (int y : yMap.keySet())
        {
            xPair = yMap.get(y);
            for (int x = xPair.a; x <= xPair.b; x++)
            {
                yPair = xMap.get(x);
                if (yPair.a <= y && y <= yPair.b && 0 <= x && x < this.target.width() && 0 <= y && y < this.target.height()) SoftwareRenderer.POINTS.add(new PairI(x, y));
            }
        }
    }
    
    private PairD transform(double x, double y)
    {
        this.viewMatrix.transform(SoftwareRenderer.VECTOR.set(x, y, 0, 1));
        return new PairD(SoftwareRenderer.VECTOR.x, SoftwareRenderer.VECTOR.y);
    }
}