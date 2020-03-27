package engine.render;

import engine.color.Colorc;

public class OpenGLRenderer extends Renderer
{
    
    protected OpenGLRenderer(Texture target)
    {
        super(target);
    }
    
    @Override
    public void finish()
    {
        this.target.download();
    }
    
    @Override
    public void clear(Colorc color)
    {
    
    }
    
    @Override
    public void drawPoint(double x, double y)
    {
    
    }
    
    @Override
    public void drawLine(double x1, double y1, double x2, double y2)
    {
    
    }
    
    @Override
    public void drawBezier(double x1, double y1, double x2, double y2, double x3, double y3)
    {
    
    }
    
    @Override
    public void drawTriangle(double x1, double y1, double x2, double y2, double x3, double y3)
    {
    
    }
    
    @Override
    public void fillTriangle(double x1, double y1, double x2, double y2, double x3, double y3)
    {
    
    }
    
    @Override
    public void drawSquare(double x, double y, double w)
    {
    
    }
    
    @Override
    public void fillSquare(double x, double y, double w)
    {
    
    }
    
    @Override
    public void drawRect(double x, double y, double w, double h)
    {
    
    }
    
    @Override
    public void fillRect(double x, double y, double w, double h)
    {
    
    }
    
    @Override
    public void drawQuad(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4)
    {
    
    }
    
    @Override
    public void fillQuad(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4)
    {
    
    }
    
    @Override
    public void drawPolygon(double[] points)
    {
    
    }
    
    @Override
    public void fillPolygon(double[] points)
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
    public void drawEllipse(double x, double y, double rx, double ry)
    {
    
    }
    
    @Override
    public void fillEllipse(double x, double y, double rx, double ry)
    {
    
    }
    
    @Override
    public void drawTexture(Texture texture, double x, double y, double w, double h, double u, double v, double uw, double vh)
    {
    
    }
    
    @Override
    public void drawText(String text, double x, double y)
    {
    
    }
    
    @Override
    public int[] loadPixels()
    {
        return new int[0];
    }
    
    @Override
    public void updatePixels()
    {
    
    }
}
