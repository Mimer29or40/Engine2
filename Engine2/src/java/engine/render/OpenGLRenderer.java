package engine.render;

import engine.color.Colorc;
import engine.util.Logger;

import static org.lwjgl.opengl.GL43.*;

/**
 * Renderer using OpenGL on the GPU.
 */
@SuppressWarnings("unused")
public class OpenGLRenderer extends Renderer
{
    private static final Logger LOGGER = new Logger();
    
    protected final Shader      pointShader;
    protected final VertexArray pointVAO;
    
    protected final Shader      lineShader;
    protected final VertexArray lineVAO;
    
    protected final Shader linesShader;
    
    protected final VertexArray triangleLinesVAO;
    protected final Shader      triangleShader;
    protected final VertexArray triangleVAO;
    
    protected final VertexArray quadLinesVAO;
    protected final Shader      quadShader;
    protected final VertexArray quadVAO;
    
    protected final VertexArray polygonLinesVAO;
    protected final Shader      polygonShader;
    protected final VertexArray polygonVAO;
    protected final GLBuffer    polygonSSBO;
    
    protected final Shader      ellipseOutlineShader;
    protected final VertexArray ellipseOutlineVAO;
    
    protected final Shader      ellipseShader;
    protected final VertexArray ellipseVAO;
    
    protected final Shader      arcOutlineShader;
    protected final VertexArray arcOutlineVAO;
    
    protected final Shader      arcShader;
    protected final VertexArray arcVAO;
    
    protected final Shader      textureShader;
    protected final VertexArray textureVAO;
    
    protected final Shader      textShader;
    protected final VertexArray textVAO;
    
    protected OpenGLRenderer(Texture target)
    {
        super(target);
        
        this.pointShader = new Shader().loadVertexFile("shaders/shared.vert").loadGeometryFile("shaders/point.geom").loadFragmentFile("shaders/shared.frag").validate();
        this.pointVAO    = new VertexArray().bind().add(2, GL_DYNAMIC_DRAW, GL_FLOAT, 2).unbind();
        
        this.lineShader = new Shader().loadVertexFile("shaders/shared.vert").loadGeometryFile("shaders/line.geom").loadFragmentFile("shaders/shared.frag").validate();
        this.lineVAO    = new VertexArray().bind().add(4, GL_DYNAMIC_DRAW, GL_FLOAT, 2).unbind();
        
        this.linesShader = new Shader().loadVertexFile("shaders/shared.vert").loadGeometryFile("shaders/lines.geom").loadFragmentFile("shaders/shared.frag").validate();
        
        this.triangleLinesVAO = new VertexArray().bind().add(24, GL_DYNAMIC_DRAW, GL_FLOAT, 2).unbind();
        this.triangleShader   = new Shader().loadVertexFile("shaders/shared.vert").loadFragmentFile("shaders/shared.frag").validate();
        this.triangleVAO      = new VertexArray().bind().add(6, GL_DYNAMIC_DRAW, GL_FLOAT, 2).unbind();
        
        this.quadLinesVAO = new VertexArray().bind().add(32, GL_DYNAMIC_DRAW, GL_FLOAT, 2).unbind();
        this.quadShader   = new Shader().loadVertexFile("shaders/shared.vert").loadFragmentFile("shaders/shared.frag").validate();
        this.quadVAO      = new VertexArray().bind().add(8, GL_DYNAMIC_DRAW, GL_FLOAT, 2).unbind();
        
        this.polygonLinesVAO = new VertexArray().bind().add(new GLBuffer(GL_ARRAY_BUFFER), GL_FLOAT, 2).unbind();
        this.polygonShader   = new Shader().loadVertexFile("shaders/shared.vert").loadGeometryFile("shaders/poly.geom").loadFragmentFile("shaders/shared.frag").validate();
        this.polygonVAO      = new VertexArray().bind().add(2, GL_DYNAMIC_DRAW, GL_FLOAT, 2).unbind();
        this.polygonSSBO     = new GLBuffer(GL_SHADER_STORAGE_BUFFER).bind().base(1).unbind();
        
        this.ellipseOutlineShader = new Shader().loadVertexFile("shaders/shared.vert").loadGeometryFile("shaders/ellipseOutline.geom").loadFragmentFile("shaders/shared.frag").validate();
        this.ellipseOutlineVAO    = new VertexArray().bind().add(2, GL_DYNAMIC_DRAW, GL_FLOAT, 2).unbind();
        
        this.ellipseShader = new Shader().loadVertexFile("shaders/shared.vert").loadGeometryFile("shaders/ellipse.geom").loadFragmentFile("shaders/shared.frag").validate();
        this.ellipseVAO    = new VertexArray().bind().add(2, GL_DYNAMIC_DRAW, GL_FLOAT, 2).unbind();
        
        this.arcOutlineShader = new Shader().loadVertexFile("shaders/shared.vert").loadGeometryFile("shaders/arcOutline.geom").loadFragmentFile("shaders/shared.frag").validate();
        this.arcOutlineVAO    = new VertexArray().bind().add(2, GL_DYNAMIC_DRAW, GL_FLOAT, 2).unbind();
        
        this.arcShader = new Shader().loadVertexFile("shaders/shared.vert").loadGeometryFile("shaders/arc.geom").loadFragmentFile("shaders/shared.frag").validate();
        this.arcVAO    = new VertexArray().bind().add(2, GL_DYNAMIC_DRAW, GL_FLOAT, 2).unbind();
        
        this.textureShader = new Shader().loadVertexFile("shaders/texture.vert").loadFragmentFile("shaders/texture.frag").validate();
        this.textureVAO    = new VertexArray().bind().add(16, GL_DYNAMIC_DRAW, GL_FLOAT, 2, GL_FLOAT, 2).unbind();
        
        this.textShader = new Shader().loadVertexFile("shaders/texture.vert").loadFragmentFile("shaders/text.frag").validate();
        this.textVAO    = new VertexArray().bind().add(new GLBuffer(GL_ARRAY_BUFFER), GL_FLOAT, 2, GL_FLOAT, 2).unbind();
    }
    
    /**
     * Resets the view space transformations.
     */
    @Override
    public void identity()
    {
        this.view.setOrtho(0F, this.target.width(), 0F, this.target.height(), -1F, 1F);
    }
    
    /**
     * Begins the rendering process.
     * <p>
     * This must be called before any draw functions are called.
     */
    @Override
    public void start()
    {
        super.start();
        
        makeCurrent();
        
        if (this.debug) glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
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
        
        // this.target.bind().download(); // Download should only happen if the user want it to happen.
        
        this.target.unbindFramebuffer();
        
        glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
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
        
        makeCurrent();
        
        glClearColor(color.rf(), color.gf(), color.bf(), color.af());
        glClear(GL_COLOR_BUFFER_BIT);
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
        makeCurrent();
        
        this.pointShader.bind();
        this.pointShader.setMat4("pv", this.view);
        this.pointShader.setColor("color", this.stroke);
        this.pointShader.setColor("tint", this.tint);
        this.pointShader.setVec2("viewport", this.target.width(), this.target.height());
        this.pointShader.setFloat("thickness", (float) this.weight);
        
        this.pointVAO.bind().getBuffer(0).bind().set(new float[] {(float) x, (float) y}, GL_DYNAMIC_DRAW).unbind();
        this.pointVAO.draw(GL_POINTS).unbind();
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
        makeCurrent();
        
        this.lineShader.bind();
        this.lineShader.setMat4("pv", this.view);
        this.lineShader.setColor("color", this.stroke);
        this.lineShader.setColor("tint", this.tint);
        this.lineShader.setVec2("viewport", this.target.width(), this.target.height());
        this.lineShader.setFloat("thickness", (float) this.weight);
        
        this.lineVAO.bind().getBuffer(0).bind().set(new float[] {(float) x1, (float) y1, (float) x2, (float) y2}, GL_DYNAMIC_DRAW).unbind();
        this.lineVAO.draw(GL_LINES).unbind();
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
     */
    @Override
    public void drawBezier(double x1, double y1, double x2, double y2, double x3, double y3)
    {
    
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
     */
    @Override
    public void drawTriangle(double x1, double y1, double x2, double y2, double x3, double y3)
    {
        makeCurrent();
        
        this.linesShader.bind();
        this.linesShader.setMat4("pv", this.view);
        this.linesShader.setColor("color", this.stroke);
        this.linesShader.setColor("tint", this.tint);
        this.linesShader.setVec2("viewport", this.target.width(), this.target.height());
        this.linesShader.setFloat("thickness", (float) this.weight);
        
        this.triangleLinesVAO.bind().getBuffer(0).bind().set(new float[] {
                (float) x3, (float) y3, (float) x1, (float) y1, (float) x2, (float) y2, (float) x3, (float) y3,
                (float) x1, (float) y1, (float) x2, (float) y2, (float) x3, (float) y3, (float) x1, (float) y1,
                (float) x2, (float) y2, (float) x3, (float) y3, (float) x1, (float) y1, (float) x2, (float) y2
        }, GL_DYNAMIC_DRAW).unbind();
        this.triangleLinesVAO.draw(GL_LINES_ADJACENCY).unbind();
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
     */
    @Override
    public void fillTriangle(double x1, double y1, double x2, double y2, double x3, double y3)
    {
        makeCurrent();
        
        this.triangleShader.bind();
        this.triangleShader.setMat4("pv", this.view);
        this.triangleShader.setColor("color", this.fill);
        this.triangleShader.setColor("tint", this.tint);
        
        this.triangleVAO.bind().getBuffer(0).bind().set(new float[] {(float) x1, (float) y1, (float) x2, (float) y2, (float) x3, (float) y3}, GL_DYNAMIC_DRAW).unbind();
        this.triangleVAO.draw(GL_TRIANGLES).unbind();
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
        drawQuad(x, y, x + w, y, x + w, y + w, x, y + w);
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
        fillQuad(x, y, x + w, y, x + w, y + w, x, y + w);
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
        drawQuad(x, y, x + w, y, x + w, y + h, x, y + h);
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
        fillQuad(x, y, x + w, y, x + w, y + h, x, y + h);
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
        makeCurrent();
        
        this.linesShader.bind();
        this.linesShader.setMat4("pv", this.view);
        this.linesShader.setColor("color", this.stroke);
        this.linesShader.setColor("tint", this.tint);
        this.linesShader.setVec2("viewport", this.target.width(), this.target.height());
        this.linesShader.setFloat("thickness", (float) this.weight);
        
        this.quadLinesVAO.bind().getBuffer(0).bind().set(new float[] {
                (float) x4, (float) y4, (float) x1, (float) y1, (float) x2, (float) y2, (float) x3, (float) y3,
                (float) x1, (float) y1, (float) x2, (float) y2, (float) x3, (float) y3, (float) x4, (float) y4,
                (float) x2, (float) y2, (float) x3, (float) y3, (float) x4, (float) y4, (float) x1, (float) y1,
                (float) x3, (float) y3, (float) x4, (float) y4, (float) x1, (float) y1, (float) x2, (float) y2
        }, GL_DYNAMIC_DRAW).unbind();
        this.quadLinesVAO.draw(GL_LINES_ADJACENCY).unbind();
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
        makeCurrent();
        
        this.quadShader.bind();
        this.quadShader.setMat4("pv", this.view);
        this.quadShader.setColor("color", this.fill);
        this.quadShader.setColor("tint", this.tint);
        
        this.quadVAO.bind().getBuffer(0).bind().set(new float[] {(float) x1, (float) y1, (float) x2, (float) y2, (float) x3, (float) y3, (float) x4, (float) y4}, GL_DYNAMIC_DRAW).unbind();
        this.quadVAO.draw(GL_QUADS).unbind();
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
    public void drawPolygon(double[] points)
    {
        makeCurrent();
        
        this.linesShader.bind();
        this.linesShader.setMat4("pv", this.view);
        this.linesShader.setColor("color", this.stroke);
        this.linesShader.setColor("tint", this.tint);
        this.linesShader.setVec2("viewport", this.target.width(), this.target.height());
        this.linesShader.setFloat("thickness", (float) this.weight);
        
        float[] array = new float[points.length * 4];
        int     index = 0;
        for (int i = 0, n = points.length >> 1; i < n; i++)
        {
            int prev = (i - 1 + n) % n;
            int next = (i + 1 + n) % n;
            int four = (i + 2 + n) % n;
            
            array[index++] = (float) points[(2 * prev)];
            array[index++] = (float) points[(2 * prev) + 1];
            array[index++] = (float) points[(2 * i)];
            array[index++] = (float) points[(2 * i) + 1];
            array[index++] = (float) points[(2 * next)];
            array[index++] = (float) points[(2 * next) + 1];
            array[index++] = (float) points[(2 * four)];
            array[index++] = (float) points[(2 * four) + 1];
        }
        
        this.polygonLinesVAO.bind().getBuffer(0).bind().set(array, GL_DYNAMIC_DRAW).unbind();
        this.polygonLinesVAO.resize().draw(GL_LINES_ADJACENCY).unbind();
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
    public void fillPolygon(double[] points)
    {
        makeCurrent();
        
        this.polygonShader.bind();
        this.polygonShader.setMat4("pv", this.view);
        this.polygonShader.setColor("color", this.fill);
        this.polygonShader.setColor("tint", this.tint);
        
        float[] array = new float[points.length];
        for (int i = 0, n = points.length; i < n; i++) array[i] = (float) points[i];
        this.polygonSSBO.bind().set(array, GL_STATIC_DRAW).unbind();
        
        this.polygonVAO.bind().draw(GL_POINTS).unbind();
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
        drawEllipse(x, y, r, r);
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
        fillEllipse(x, y, r, r);
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
        makeCurrent();
        
        this.ellipseOutlineShader.bind();
        this.ellipseOutlineShader.setMat4("pv", this.view);
        this.ellipseOutlineShader.setColor("color", this.stroke);
        this.ellipseOutlineShader.setColor("tint", this.tint);
        this.ellipseOutlineShader.setVec2("radius", (float) rx, (float) ry);
        this.ellipseOutlineShader.setVec2("viewport", this.target.width(), this.target.height());
        this.ellipseOutlineShader.setFloat("thickness", (float) this.weight);
        
        this.ellipseOutlineVAO.bind().getBuffer(0).bind().set(new float[] {(float) x, (float) y}, GL_DYNAMIC_DRAW).unbind();
        this.ellipseOutlineVAO.draw(GL_POINTS).unbind();
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
        makeCurrent();
        
        this.ellipseShader.bind();
        this.ellipseShader.setMat4("pv", this.view);
        this.ellipseShader.setColor("color", this.fill);
        this.ellipseShader.setColor("tint", this.tint);
        this.ellipseShader.setVec2("radius", (float) rx, (float) ry);
        
        this.ellipseVAO.bind().getBuffer(0).bind().set(new float[] {(float) x, (float) y}, GL_DYNAMIC_DRAW).unbind();
        this.ellipseVAO.draw(GL_POINTS).unbind();
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
        makeCurrent();
        
        this.arcOutlineShader.bind();
        this.arcOutlineShader.setMat4("pv", this.view);
        this.arcOutlineShader.setColor("color", this.stroke);
        this.arcOutlineShader.setColor("tint", this.tint);
        this.arcOutlineShader.setVec2("radius", (float) rx, (float) ry);
        this.arcOutlineShader.setVec2("viewport", this.target.width(), this.target.height());
        this.arcOutlineShader.setFloat("thickness", (float) this.weight);
        this.arcOutlineShader.setVec2("bounds", (float) start, (float) stop);
        this.arcOutlineShader.setInt("mode", this.arcMode.ordinal());
        
        this.arcOutlineVAO.bind().getBuffer(0).bind().set(new float[] {(float) x, (float) y}, GL_DYNAMIC_DRAW).unbind();
        this.arcOutlineVAO.draw(GL_POINTS).unbind();
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
        makeCurrent();
        
        this.arcShader.bind();
        this.arcShader.setMat4("pv", this.view);
        this.arcShader.setColor("color", this.fill);
        this.arcShader.setColor("tint", this.tint);
        this.arcShader.setVec2("radius", (float) rx, (float) ry);
        this.arcShader.setVec2("bounds", (float) start, (float) stop);
        this.arcShader.setInt("mode", this.arcMode.ordinal());
        
        this.arcVAO.bind().getBuffer(0).bind().set(new float[] {(float) x, (float) y}, GL_DYNAMIC_DRAW).unbind();
        this.arcVAO.draw(GL_POINTS).unbind();
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
     * @param x2      The bottom right corner x coordinate of the rectangle.
     * @param y2      The bottom right corner y coordinate of the rectangle.
     * @param u1      The top left corner u texture coordinate of the rectangle.
     * @param v1      The top left corner v texture coordinate of the rectangle.
     * @param u2      The bottom right corner u texture coordinate of the rectangle.
     * @param v2      The bottom right corner v texture coordinate of the rectangle.
     */
    @Override
    public void drawTexture(Texture texture, double x1, double y1, double x2, double y2, double u1, double v1, double u2, double v2)
    {
        makeCurrent();
        
        texture.bindTexture();
        
        this.textureShader.bind();
        this.textureShader.setMat4("pv", this.view);
        this.textureShader.setColor("tint", this.tint);
        this.textureShader.setFloat("interpolate", -1);
        this.textureShader.setInt("texture1", 0);
        this.textureShader.setInt("texture2", 1);
        
        this.textureVAO.bind().getBuffer(0).bind().set(new float[] {
                (float) x1, (float) y1, (float) u1, (float) v1,
                (float) x1, (float) y2, (float) u1, (float) v2,
                (float) x2, (float) y2, (float) u2, (float) v2,
                (float) x2, (float) y1, (float) u2, (float) v1
        }, GL_DYNAMIC_DRAW).unbind();
        this.textureVAO.draw(GL_QUADS).unbind();
    }
    
    /**
     * Draws an interpolated textured rectangle whose top left corner is at {@code (x1, y1)} and is {@code x2-x1} pixels wide and {@code y2-y1} tall.
     * <p>
     * You can specify the coordinate of the texture to pull from.
     * <p>
     * The coordinates passed in will be transformed by the view matrix
     *
     * @param texture1 The first texture to draw.
     * @param texture2 The second texture to draw.
     * @param amount   The amount to interpolate.
     * @param x1       The top left corner x coordinate of the rectangle.
     * @param y1       The top left corner y coordinate of the rectangle.
     * @param x2       The bottom right corner x coordinate of the rectangle.
     * @param y2       The bottom right corner y coordinate of the rectangle.
     * @param u1       The top left corner u texture coordinate of the rectangle.
     * @param v1       The top left corner v texture coordinate of the rectangle.
     * @param u2       The bottom right corner u texture coordinate of the rectangle.
     * @param v2       The bottom right corner v texture coordinate of the rectangle.
     */
    @Override
    public void drawInterpolatedTexture(Texture texture1, Texture texture2, double amount, double x1, double y1, double x2, double y2, double u1, double v1, double u2, double v2)
    {
        makeCurrent();
    
        texture1.bindTexture();
        glActiveTexture(GL_TEXTURE1);
        texture2.bindTexture();
        glActiveTexture(GL_TEXTURE0);
    
        this.textureShader.bind();
        this.textureShader.setMat4("pv", this.view);
        this.textureShader.setColor("tint", this.tint);
        this.textureShader.setFloat("interpolate", (float) amount);
        this.textureShader.setInt("texture1", 0);
        this.textureShader.setInt("texture2", 1);
    
        this.textureVAO.bind().getBuffer(0).bind().set(new float[] {
                (float) x1, (float) y1, (float) u1, (float) v1,
                (float) x1, (float) y2, (float) u1, (float) v2,
                (float) x2, (float) y2, (float) u2, (float) v2,
                (float) x2, (float) y1, (float) u2, (float) v1
        }, GL_DYNAMIC_DRAW).unbind();
        this.textureVAO.draw(GL_QUADS).unbind();
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
        makeCurrent();
        
        this.font.texture().bindTexture();
        
        this.textShader.bind();
        this.textShader.setMat4("pv", this.view);
        this.textShader.setColor("color", this.fill);
        this.textShader.setColor("tint", this.tint);
        
        float[] data = new float[text.length() * 16];
        
        double[] vertices = this.font.renderText(text);
        for (int i = 0, n = text.length(), index = 0; i < n; i++)
        {
            int i8 = i * 8;
            
            double x1 = vertices[i8] + x;
            double y1 = vertices[i8 + 1] + y;
            double x2 = vertices[i8 + 2] + x;
            double y2 = vertices[i8 + 3] + y;
            double u1 = vertices[i8 + 4];
            double v1 = vertices[i8 + 5];
            double u2 = vertices[i8 + 6];
            double v2 = vertices[i8 + 7];
            
            data[index++] = (float) x1;
            data[index++] = (float) y1;
            data[index++] = (float) u1;
            data[index++] = (float) v1;
            data[index++] = (float) x1;
            data[index++] = (float) y2;
            data[index++] = (float) u1;
            data[index++] = (float) v2;
            data[index++] = (float) x2;
            data[index++] = (float) y2;
            data[index++] = (float) u2;
            data[index++] = (float) v2;
            data[index++] = (float) x2;
            data[index++] = (float) y1;
            data[index++] = (float) u2;
            data[index++] = (float) v1;
        }
        
        this.textVAO.bind().getBuffer(0).bind().set(data, GL_DYNAMIC_DRAW).unbind();
        this.textVAO.resize().draw(GL_QUADS).unbind();
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
        super.loadPixels();
        this.target.bindTexture().download();
        for (int i = 0, n = this.pixels.length; i < n; i++) this.pixels[i] = this.target.data().get(i) & 0xFF;
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
        for (int i = 0, n = this.pixels.length; i < n; i++) this.target.data().put(i, (byte) (this.pixels[i] & 0xFF));
        this.target.bindTexture().upload();
    }
    
    private void makeCurrent()
    {
        this.target.bindFramebuffer();
        glViewport(0, 0, this.target.width(), this.target.height());
    }
}
