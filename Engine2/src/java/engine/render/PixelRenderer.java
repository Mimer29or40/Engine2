package engine.render;

import engine.Engine;
import engine.color.Color;
import engine.color.Colorc;
import engine.util.Logger;
import engine.util.PairI;

import java.util.HashMap;
import java.util.HashSet;

import static engine.util.Util.*;

/**
 * Non-transformable renderer meant for small canvases.
 */
@SuppressWarnings("unused")
public class PixelRenderer extends Renderer
{
    private static final Logger LOGGER = new Logger();
    
    private static final byte LINE_OVERLAP_NONE  = 0b00;
    private static final byte LINE_OVERLAP_MAJOR = 0b01;
    private static final byte LINE_OVERLAP_MINOR = 0b10;
    
    private static final Color          COLOR  = new Color();
    private static final HashSet<PairI> POINTS = new HashSet<>();
    
    protected PixelRenderer(Texture target)
    {
        super(target);
    }
    
    /**
     * Resets the view space transformations.
     */
    public void identity()
    {
        PixelRenderer.LOGGER.warning("View transformations not supported in PixelRenderer");
    }
    
    /**
     * Translates the view space.
     *
     * @param x The amount to translate horizontally.
     * @param y The amount to translate vertically.
     */
    public void translate(double x, double y)
    {
        PixelRenderer.LOGGER.warning("View transformations not supported in PixelRenderer");
    }
    
    /**
     * Rotates the view space.
     *
     * @param angle The angle in radian to rotate by.
     */
    public void rotate(double angle)
    {
        PixelRenderer.LOGGER.warning("View transformations not supported in PixelRenderer");
    }
    
    /**
     * Scales the view space.
     *
     * @param x The amount to scale horizontally.
     * @param y The amount to scale vertically.
     */
    public void scale(double x, double y)
    {
        PixelRenderer.LOGGER.warning("View transformations not supported in PixelRenderer");
    }
    
    /**
     * Finishes the render.
     * <p>
     * This must be called after {@link #start}
     */
    @Override
    public void finish()
    {
        super.finish();
        
        this.target.bind().upload(); // Upload should only happen if it needs to.
    }
    
    /**
     * Clears the render target to the color provided.
     *
     * @param color The color to set the target to.
     */
    @Override
    public void clear(Colorc color)
    {
        super.clear(color);
        
        this.target.clear(color);
    }
    
    /**
     * Draws a point that is {@link #weight()} in size and {@link #stroke()} in color.
     * <p>
     * The coordinates passed in will be transformed by the view matrix
     *
     * @param x The x coordinate to draw the point at.
     * @param y The y coordinate to draw the point at.
     */
    @Override
    public void drawPoint(double x, double y)
    {
        int xi = (int) x, yi = (int) y;
        
        if (this.weight == 1)
        {
            pointImpl(xi, yi, this.stroke);
        }
        else
        {
            int min = ((int) this.weight - 1) >> 1, max = min + (isEven(this.weight) ? 1 : 0);
            xi += getDecimal(x) >= 0.5 || isOdd(this.weight) ? 0 : -1;
            yi += getDecimal(y) >= 0.5 || isOdd(this.weight) ? 0 : -1;
            for (int j = -min; j <= max; j++)
            {
                for (int i = -min; i <= max; i++)
                {
                    pointImpl(xi + i, yi + j, this.stroke);
                }
            }
        }
    }
    
    /**
     * Draws a line from {@code (x1, y1)} to {@code (x2, y2)} that is {@link #weight()} pixels thick and {@link #stroke()} in color.
     * <p>
     * The coordinates passed in will be transformed by the view matrix
     *
     * @param x1 The start x coordinate to draw the line at.
     * @param y1 The start y coordinate to draw the line at.
     * @param x2 The end x coordinate to draw the line at.
     * @param y2 The end y coordinate to draw the line at.
     */
    @Override
    public void drawLine(double x1, double y1, double x2, double y2)
    {
        int x1i = (int) round(x1), y1i = (int) round(y1), x2i = (int) round(x2), y2i = (int) round(y2);
        
        lineImpl(x1i, y1i, x2i, y2i, (int) this.weight, LINE_OVERLAP_NONE);
        pointsImpl(this.stroke);
    }
    
    /**
     * Draws a bezier from {@code (x1, y1)} through {@code (x2, y2) to {@code (x3, y3)} that is {@link #weight()} pixels thick and {@link #stroke()} in color.
     * <p>
     * The coordinates passed in will be transformed by the view matrix
     *
     * @param x1 The start x coordinate to draw the line at.
     * @param y1 The start y coordinate to draw the line at.
     * @param x2 The midpoint x coordinate to determine the curve.
     * @param y2 The midpoint y coordinate to determine the curve.
     * @param x3 The end x coordinate to draw the line at.
     * @param y3 The end y coordinate to draw the line at.
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @param x3
     * @param y3
     */
    @Override
    public void drawBezier(double x1, double y1, double x2, double y2, double x3, double y3)
    {
        // TODO - From SoftwareRenderer
    }
    
    /**
     * Draws a triangle from {@code (x1, y1)} through {@code (x2, y2) to {@code (x3, y3)} that is {@link #weight()} pixels thick and {@link #stroke()} in color.
     * <p>
     * The coordinates passed in will be transformed by the view matrix
     *
     * @param x1 The first x coordinate of the triangle.
     * @param y1 The first y coordinate of the triangle.
     * @param x2 The second x coordinate of the triangle.
     * @param y2 The second y coordinate of the triangle.
     * @param x3 The third x coordinate of the triangle.
     * @param y3 The third y coordinate of the triangle.
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @param x3
     * @param y3
     */
    @Override
    public void drawTriangle(double x1, double y1, double x2, double y2, double x3, double y3)
    {
        drawPolygon(x1, y1, x2, y2, x3, y3);
    }
    
    /**
     * Fills a triangle from {@code (x1, y1)} through {@code (x2, y2) to {@code (x3, y3)} that is {@link #fill()} in color.
     * <p>
     * The coordinates passed in will be transformed by the view matrix
     *
     * @param x1 The first x coordinate of the triangle.
     * @param y1 The first y coordinate of the triangle.
     * @param x2 The second x coordinate of the triangle.
     * @param y2 The second y coordinate of the triangle.
     * @param x3 The third x coordinate of the triangle.
     * @param y3 The third y coordinate of the triangle.
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @param x3
     * @param y3
     */
    @Override
    public void fillTriangle(double x1, double y1, double x2, double y2, double x3, double y3)
    {
        fillPolygon(x1, y1, x2, y2, x3, y3);
    }
    
    /**
     * Draws a square whose top left corner is at {@code (x, y)} that is {@link #weight()} pixels thick and {@link #stroke()} in color.
     * <p>
     * The coordinates passed in will be transformed by the view matrix
     *
     * @param x The top left x coordinate of the square.
     * @param y The top left y coordinate of the square.
     * @param w The side length of the square.
     */
    @Override
    public void drawSquare(double x, double y, double w)
    {
        drawRect(x, y, w, w);
    }
    
    /**
     * Fills a square whose top left corner is at {@code (x, y)} that is {@link #fill()} in color.
     * <p>
     * The coordinates passed in will be transformed by the view matrix
     *
     * @param x The top left x coordinate of the square.
     * @param y The top left y coordinate of the square.
     * @param w The side length of the square.
     */
    @Override
    public void fillSquare(double x, double y, double w)
    {
        fillRect(x, y, w, w);
    }
    
    /**
     * Draws a rectangle whose top left corner is at {@code (x, y)} that is {@link #weight()} pixels thick and {@link #stroke()} in color.
     * <p>
     * The coordinates passed in will be transformed by the view matrix
     *
     * @param x The top left x coordinate of the rectangle.
     * @param y The top left y coordinate of the rectangle.
     * @param w The width of the rectangle.
     * @param h The height of the rectangle.
     */
    @Override
    public void drawRect(double x, double y, double w, double h)
    {
        int x1 = (int) round(x), y1 = (int) round(y), x2 = (int) round(x + w - 1), y2 = (int) round(y + h - 1);
        
        lineImpl(x1, y1, x2, y1, (int) this.weight, LINE_OVERLAP_NONE);
        lineImpl(x2, y1, x2, y2, (int) this.weight, LINE_OVERLAP_NONE);
        lineImpl(x2, y2, x1, y2, (int) this.weight, LINE_OVERLAP_NONE);
        lineImpl(x1, y2, x1, y1, (int) this.weight, LINE_OVERLAP_NONE);
    }
    
    /**
     * Fills a rectangle whose top left corner is at {@code (x, y)} that is {@link #fill()} in color.
     * <p>
     * The coordinates passed in will be transformed by the view matrix
     *
     * @param x The top left x coordinate of the rectangle.
     * @param y The top left y coordinate of the rectangle.
     * @param w The width of the rectangle.
     * @param h The height of the rectangle.
     */
    @Override
    public void fillRect(double x, double y, double w, double h)
    {
        int x1 = Math.max(0, Math.min((int) round(x), this.target.width() - 1));
        int y1 = Math.max(0, Math.min((int) round(y), this.target.height() - 1));
        int x2 = Math.max(0, Math.min((int) round(x + w - 1), this.target.width() - 1));
        int y2 = Math.max(0, Math.min((int) round(y + h - 1), this.target.height() - 1));
        
        for (int i = x1; i <= x2; i++)
        {
            lineImpl(i, y1, i, y2, (int) this.weight, LINE_OVERLAP_NONE);
        }
        pointsImpl(this.fill);
    }
    
    /**
     * Draws a quad from the points provided that is {@link #weight()} pixels thick and {@link #stroke()} in color.
     * <p>
     * The winding order does not matter.
     * <p>
     * The coordinates passed in will be transformed by the view matrix
     *
     * @param x1 The first x coordinate.
     * @param y1 The first y coordinate.
     * @param x2 The second x coordinate.
     * @param y2 The second y coordinate.
     * @param x3 The third x coordinate.
     * @param y3 The third y coordinate.
     * @param x4 The fourth x coordinate.
     * @param y4 The fourth y coordinate.
     */
    @Override
    public void drawQuad(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4)
    {
        drawPolygon(x1, y1, x2, y2, x3, y3, x4, y4);
    }
    
    /**
     * Fills a quad from the points provided that is {@link #fill()} in color.
     * <p>
     * The winding order does not matter.
     * <p>
     * The coordinates passed in will be transformed by the view matrix
     *
     * @param x1 The first x coordinate.
     * @param y1 The first y coordinate.
     * @param x2 The second x coordinate.
     * @param y2 The second y coordinate.
     * @param x3 The third x coordinate.
     * @param y3 The third y coordinate.
     * @param x4 The fourth x coordinate.
     * @param y4 The fourth y coordinate.
     */
    @Override
    public void fillQuad(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4)
    {
        fillPolygon(x1, y1, x2, y2, x3, y3, x4, y4);
    }
    
    /**
     * Draws a polygon from the points provided that is {@link #weight()} pixels thick and {@link #stroke()} in color.
     * <p>
     * The winding order does not matter.
     * <p>
     * This method does not validate the points.
     * <p>
     * The coordinates passed in will be transformed by the view matrix
     *
     * @param points The points.
     */
    @Override
    public void drawPolygon(double... points)
    {
        drawPolygonImpl(points, true);
    }
    
    /**
     * Fills a polygon from the points provided that is {@link #fill()} in color.
     * <p>
     * The winding order does not matter.
     * <p>
     * This method does not validate the points.
     * <p>
     * The coordinates passed in will be transformed by the view matrix
     *
     * @param points The points.
     */
    @Override
    public void fillPolygon(double... points)
    {
        int n = points.length;
        int x1, y1, x2, y2;
        
        x1 = (int) round(points[n - 2]);
        y1 = (int) round(points[n - 1]);
        for (int i = 0; i < n; i += 2)
        {
            x2 = (int) round(points[i]);
            y2 = (int) round(points[i + 1]);
            lineImpl(x1, y1, x2, y2, 1, LINE_OVERLAP_NONE);
            x1 = x2;
            y1 = y2;
        }
        fillBetweenLines();
        pointsImpl(this.fill);
    }
    
    /**
     * Draws a circle whose center is at {@code (x, y)} that is {@link #weight()} pixels thick and {@link #stroke()} in color.
     * <p>
     * The coordinates passed in will be transformed by the view matrix
     *
     * @param x The center x coordinate of the circle.
     * @param y The center y coordinate of the circle.
     * @param r The radius of the circle.
     */
    @Override
    public void drawCircle(double x, double y, double r)
    {
        drawPolygon(getCirclePoints(x, y, r, r));
    }
    
    /**
     * Fills a circle whose center is at {@code (x, y)} that is {@link #fill()} in color.
     * <p>
     * The coordinates passed in will be transformed by the view matrix
     *
     * @param x The center x coordinate of the circle.
     * @param y The center y coordinate of the circle.
     * @param r The radius of the circle.
     */
    @Override
    public void fillCircle(double x, double y, double r)
    {
        fillPolygon(getCirclePoints(x, y, r, r));
    }
    
    /**
     * Draws an ellipse whose center is at {@code (x, y)} that is {@link #weight()} pixels thick and {@link #stroke()} in color.
     * <p>
     * The coordinates passed in will be transformed by the view matrix
     *
     * @param x  The center x coordinate of the ellipse.
     * @param y  The center y coordinate of the ellipse.
     * @param rx The radius of the ellipse along the x axis.
     * @param ry The radius of the ellipse along the y axis.
     */
    @Override
    public void drawEllipse(double x, double y, double rx, double ry)
    {
        drawPolygon(getCirclePoints(x, y, rx, ry));
    }
    
    /**
     * Fills an ellipse whose center is at {@code (x, y)} that is {@link #fill()} in color.
     * <p>
     * The coordinates passed in will be transformed by the view matrix
     *
     * @param x  The center x coordinate of the ellipse.
     * @param y  The center y coordinate of the ellipse.
     * @param rx The radius of the ellipse along the x axis.
     * @param ry The radius of the ellipse along the y axis.
     */
    @Override
    public void fillEllipse(double x, double y, double rx, double ry)
    {
        fillPolygon(getCirclePoints(x, y, rx, ry));
    }
    
    /**
     * Draws an arc whose center is at {@code (x, y)} that is {@link #weight()} pixels thick and {@link #stroke()} in color.
     * <p>
     * The coordinates passed in will be transformed by the view matrix
     *
     * @param x     The center x coordinate of the ellipse.
     * @param y     The center y coordinate of the ellipse.
     * @param rx    The radius of the ellipse along the x axis.
     * @param ry    The radius of the ellipse along the y axis.
     * @param start The starting angle in radians.
     * @param stop  The ending angle in radians.
     */
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
    
    /**
     * Fills an arc whose center is at {@code (x, y)} that is {@link #fill()} in color.
     * <p>
     * The coordinates passed in will be transformed by the view matrix
     *
     * @param x     The center x coordinate of the ellipse.
     * @param y     The center y coordinate of the ellipse.
     * @param rx    The radius of the ellipse along the x axis.
     * @param ry    The radius of the ellipse along the y axis.
     * @param start The start angle in radians
     * @param stop  The end angle in radians
     */
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
    
    /**
     * Draws a textured rectangle whose top left corner is at {@code (x, y)} and is {@code w} pixels wide and {@code y} tall.
     * <p>
     * You can specify the coordinate of the texture to pull from.
     * <p>
     * The coordinates passed in will be transformed by the view matrix
     *
     * @param texture The texture to draw.
     * @param x1      The top left corner x coordinate of the rectangle.
     * @param y1      The top left corner y coordinate of the rectangle.
     * @param x2      The width of the rectangle.
     * @param y2      The height of the rectangle.
     * @param u1      The top left corner x texture coordinate of the rectangle.
     * @param v1      The top left corner y texture coordinate of the rectangle.
     * @param u2      The width of the texture rectangle.
     * @param v2      The height of the texture rectangle.
     */
    @Override
    public void drawTexture(Texture texture, double x1, double y1, double x2, double y2, double u1, double v1, double u2, double v2)
    {
        if (x2 <= 0 || y2 <= 0 || u2 <= 0 || v2 <= 0) return;
        
        texture.bind().download();
        
        int topLeftX     = (int) round(x1);
        int topLeftY     = (int) round(y1);
        int topRightX    = (int) round(x2);
        int topRightY    = (int) round(y1);
        int bottomLeftX  = (int) round(x1);
        int bottomLeftY  = (int) round(y2);
        int bottomRightX = (int) round(x2);
        int bottomRightY = (int) round(y2);
        
        int u1i = (int) round(u1 * texture.width());
        int v1i = (int) round(v1 * texture.height());
        int u2i = (int) round(u2 * texture.width());
        int v2i = (int) round(v2 * texture.height());
        
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
        
        for (PairI point : PixelRenderer.POINTS)
        {
            int dx    = point.a() - topLeftX;
            int dy    = point.b() - topLeftY;
            int textX = u1i + ((dx * xAxisX + dy * xAxisY) * (u2i - u1i) / xAxisLen);
            int textY = v1i + ((dx * yAxisX + dy * yAxisY) * (v2i - v1i) / yAxisLen);
            pointImpl(point.a(), point.b(), texture.getPixel(textX, textY));
        }
        PixelRenderer.POINTS.clear();
    }
    
    /**
     * Draws a string of text to the screen. The coordinate specified will be the top left of the text.
     * <p>
     * You can change the font with {@link #textFont()}.
     * <p>
     * You can change the size of the text with {@link #textSize()}
     * <p>
     * The coordinates passed in will be transformed by the view matrix
     *
     * @param text The text to render
     * @param x    The x coordinate of the text.
     * @param y    The y coordinate of the text.
     */
    @Override
    public void drawText(String text, double x, double y)
    {
        double[] vertices = this.font.renderText(text);
        for (int i = 0, lineSize = text.length(); i < lineSize; i++)
        {
            int index = i * 8;
            
            double x1 = vertices[index] + x;
            double y1 = vertices[index + 1] + y;
            double x2 = vertices[index + 2] + x;
            double y2 = vertices[index + 3] + y;
            double u1 = vertices[index + 4];
            double v1 = vertices[index + 5];
            double u2 = vertices[index + 6];
            double v2 = vertices[index + 7];
            
            int topLeftX     = (int) round(x1);
            int topLeftY     = (int) round(y1);
            int topRightX    = (int) round(x2);
            int topRightY    = (int) round(y1);
            int bottomLeftX  = (int) round(x1);
            int bottomLeftY  = (int) round(y2);
            int bottomRightX = (int) round(x2);
            int bottomRightY = (int) round(y2);
            
            int u1i = (int) round(u1 * this.font.getTexture().width());
            int v1i = (int) round(v1 * this.font.getTexture().height());
            int u2i = (int) round(u2 * this.font.getTexture().width());
            int v2i = (int) round(v2 * this.font.getTexture().height());
            
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
            
            for (PairI point : PixelRenderer.POINTS)
            {
                int dx    = point.a() - topLeftX;
                int dy    = point.b() - topLeftY;
                int textX = u1i + ((dx * xAxisX + dy * xAxisY) * (u2i - u1i) / xAxisLen);
                int textY = v1i + ((dx * yAxisX + dy * yAxisY) * (v2i - v1i) / yAxisLen);
                int red   = this.font.getTexture().getPixel(textX, textY).r();
                if (red > 0) pointImpl(point.a(), point.b(), PixelRenderer.COLOR.set(this.fill).scale((double) red / 255, true));
            }
            PixelRenderer.POINTS.clear();
        }
    }
    
    /**
     * Loads the color values of the pixels into an 1-D array.
     * <p>
     * The length of the array will be {@code (this.target.width() * this.target.height() * this.target.channels())} long.
     * <p>
     * The order of values depends on the number of channels the target has, by default the value order is [r0,g0,b0,a0,r1,g1,b1,a1,...]
     * <p>
     * If you modify thee values of the array, you must call {@link #updatePixels()} to show the changes.
     *
     * @return The color array.
     */
    @Override
    public int[] loadPixels()
    {
        for (int i = 0, n = this.pixels.length; i < n; i++)
        {
            this.pixels[i] = this.target.data().get(i) & 0xFF;
        }
        return this.pixels;
    }
    
    /**
     * Updates the target with the values of the pixel array.
     * <p>
     * This will do nothing if {@link #loadPixels()} is not called and the pixel array is not modified.
     */
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
        if (this.blend)
        {
            this.target.setPixel(x, y, Engine.blend().blend(color, this.target.getPixel(x, y), PixelRenderer.COLOR));
        }
        else
        {
            this.target.setPixel(x, y, color);
        }
    }
    
    private void pointsImpl(Colorc color)
    {
        for (PairI point : PixelRenderer.POINTS) pointImpl(point.a(), point.b(), color);
        PixelRenderer.POINTS.clear();
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
                    PixelRenderer.POINTS.add(new PairI(x1, y1));
                    if (y1 == y2) break;
                    y1 += sy;
                }
            }
            else if (dy == 0)
            {
                while (true)
                {
                    PixelRenderer.POINTS.add(new PairI(x1, y1));
                    if (x1 == x2) break;
                    x1 += sx;
                }
            }
            else
            {
                int err = dx - dy, e2, c;
                while (true)
                {
                    PixelRenderer.POINTS.add(new PairI(x1, y1));
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
                            
                            if ((aOverlap & LINE_OVERLAP_MAJOR) > 0) PixelRenderer.POINTS.add(new PairI(x1, y1 - sy));
                            if ((aOverlap & LINE_OVERLAP_MINOR) > 0) PixelRenderer.POINTS.add(new PairI(x1 - sx, y1));
                        }
                        else
                        {
                            if ((aOverlap & LINE_OVERLAP_MAJOR) > 0) PixelRenderer.POINTS.add(new PairI(x1 - sx, y1));
                            if ((aOverlap & LINE_OVERLAP_MINOR) > 0) PixelRenderer.POINTS.add(new PairI(x1, y1 - sy));
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
        
        x1 = (int) (connected ? points[n - 2] : points[0]);
        y1 = (int) (connected ? points[n - 1] : points[1]);
        for (int i = start; i < n; i += 2)
        {
            x2 = (int) points[i];
            y2 = (int) points[i + 1];
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
        for (PairI point : PixelRenderer.POINTS)
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
                if (yPair.a <= y && y <= yPair.b && 0 <= x && x < this.target.width() && 0 <= y && y < this.target.height()) PixelRenderer.POINTS.add(new PairI(x, y));
            }
        }
    }
}
