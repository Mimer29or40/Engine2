package engine.render;

import engine.color.Colorc;
import engine.util.Logger;
import org.joml.Matrix4f;

import static engine.Engine.screenHeight;
import static engine.Engine.screenWidth;
import static engine.util.Util.max;
import static org.lwjgl.opengl.GL43.*;

/**
 * Renderer using OpenGL on the GPU.
 */
public class OpenGLRenderer extends Renderer
{
    private static final Logger LOGGER = new Logger();
    
    protected final int fbo, rbo;
    
    protected final Matrix4f proj = new Matrix4f();
    protected final Matrix4f pv   = new Matrix4f();
    
    protected final VertexArray vertexArray;
    
    protected final Shader pointShader;
    protected final Shader lineShader;
    protected final Shader triangleShader;
    protected final Shader quadShader;
    protected final Shader ellipseShader;
    protected final Shader ellipseOutlineShader;
    
    protected OpenGLRenderer(Texture target)
    {
        super(target);
        
        this.fbo = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, this.fbo);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, this.target.bind().upload().id(), 0);
        
        this.rbo = glGenRenderbuffers();
        glBindRenderbuffer(GL_RENDERBUFFER, this.rbo);
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH24_STENCIL8, this.target.width(), this.target.height());
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT, GL_RENDERBUFFER, this.rbo); // now actually attach it
        
        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) OpenGLRenderer.LOGGER.severe("Could not create FrameBuffer");
        
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        
        this.proj.m00(2F / screenWidth());
        this.proj.m11(2F / screenHeight());
        this.proj.m22(2F / max(screenWidth() * screenWidth(), screenHeight() * screenHeight()));
        
        this.vertexArray = new VertexArray();
        
        this.pointShader    = new Shader().loadVertexFile("shader/shared.vert").loadGeometryFile("shader/point.geom").loadFragmentFile("shader/shared.frag").validate();
        this.lineShader     = new Shader().loadVertexFile("shader/shared.vert").loadGeometryFile("shader/line.geom").loadFragmentFile("shader/shared.frag").validate();
        this.triangleShader = new Shader().loadVertexFile("shader/shared.vert").loadFragmentFile("shader/shared.frag").validate();
        this.quadShader     = new Shader().loadVertexFile("shader/shared.vert").loadFragmentFile("shader/shared.frag").validate();
        this.ellipseShader  = new Shader().loadVertexFile("shader/shared.vert").loadGeometryFile("shader/ellipse.geom").loadFragmentFile("shader/shared.frag").validate();
        this.ellipseOutlineShader  = new Shader().loadVertexFile("shader/shared.vert").loadGeometryFile("shader/ellipse.geom").loadFragmentFile("shader/shared.frag").validate();
    }
    
    /**
     * Resets the view space transformations.
     */
    public void identity()
    {
        this.view.identity();
        this.proj.mul(this.view, this.pv);
    }
    
    /**
     * Translates the view space.
     *
     * @param x The amount to translate horizontally.
     * @param y The amount to translate vertically.
     */
    public void translate(double x, double y)
    {
        this.view.translate((float) x, (float) y, 0);
        this.proj.mul(this.view, this.pv);
    }
    
    /**
     * Rotates the view space.
     *
     * @param angle The angle in radian to rotate by.
     */
    public void rotate(double angle)
    {
        this.view.rotate((float) angle, 0, 0, 1);
        this.proj.mul(this.view, this.pv);
    }
    
    /**
     * Scales the view space.
     *
     * @param x The amount to scale horizontally.
     * @param y The amount to scale vertically.
     */
    public void scale(double x, double y)
    {
        this.view.scale((float) x, (float) y, 1);
        this.proj.mul(this.view, this.pv);
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
        
        glBindFramebuffer(GL_FRAMEBUFFER, this.fbo);
        glViewport(0, 0, this.target.width(), this.target.height());
        
        this.view.translate(-screenWidth() / 2f, -screenHeight() / 2f, 0);
        this.proj.mul(this.view, this.pv);
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
    
        this.target.bind().download();
    }
    
    /**
     * Returns the renderers properties to the state when {@link #push} was called.
     */
    @Override
    public void pop()
    {
        super.pop();
        this.proj.mul(this.view, this.pv);
    }
    
    /**
     * Clears the render target to the color provided.
     *
     * @param color The color to set the target to.
     */
    @Override
    public void clear(Colorc color)
    {
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
        this.pointShader.setMat4("pv", this.pv);
        this.pointShader.setColor("color", this.stroke);
        this.pointShader.setVec2("viewport", screenWidth(), screenHeight());
        this.pointShader.setFloat("thickness", (float) this.weight);
        
        this.vertexArray.bind();
        this.vertexArray.reset();
        this.vertexArray.add(new float[] {(float) x, (float) y}, 2);
        this.vertexArray.draw(GL_POINTS);
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
        this.lineShader.setMat4("pv", this.pv);
        this.lineShader.setColor("color", this.stroke);
        this.lineShader.setVec2("viewport", screenWidth(), screenHeight());
        this.lineShader.setFloat("thickness", (float) this.weight);
        
        this.vertexArray.bind();
        this.vertexArray.reset();
        this.vertexArray.add(new float[] {(float) x1, (float) y1, (float) x2, (float) y2}, 2);
        this.vertexArray.draw(GL_LINES);
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
        this.triangleShader.bind();
        this.triangleShader.setMat4("pv", this.pv);
        this.triangleShader.setColor("color", this.fill);
        this.triangleShader.setVec2("viewport", screenWidth(), screenHeight());
        this.triangleShader.setFloat("thickness", (float) this.weight);
        
        this.vertexArray.bind();
        this.vertexArray.reset();
        this.vertexArray.add(new float[] {(float) x1, (float) y1, (float) x2, (float) y2, (float) x3, (float) y3}, 2);
        this.vertexArray.draw(GL_TRIANGLES);
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
        this.quadShader.setMat4("pv", this.pv);
        this.quadShader.setColor("color", this.fill);
        this.quadShader.setVec2("viewport", screenWidth(), screenHeight());
        this.quadShader.setFloat("thickness", (float) this.weight);
        
        this.vertexArray.bind();
        this.vertexArray.reset();
        this.vertexArray.add(new float[] {(float) x1, (float) y1, (float) x2, (float) y2, (float) x3, (float) y3, (float) x4, (float) y4}, 2);
        this.vertexArray.draw(GL_QUADS);
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
        this.ellipseOutlineShader.setMat4("pv", this.pv);
        this.ellipseOutlineShader.setColor("color", this.stroke);
        this.ellipseOutlineShader.setVec2("radius", (float) rx, (float) ry);
        this.ellipseOutlineShader.setVec2("viewport", screenWidth(), screenHeight());
        this.ellipseOutlineShader.setFloat("thickness", (float) this.weight);
    
        this.vertexArray.bind();
        this.vertexArray.reset();
        this.vertexArray.add(new float[] {(float) x, (float) y}, 2);
        this.vertexArray.draw(GL_POINTS);
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
        this.ellipseShader.setMat4("pv", this.pv);
        this.ellipseShader.setColor("color", this.fill);
        this.ellipseShader.setVec2("radius", (float) rx, (float) ry);
    
        this.vertexArray.bind();
        this.vertexArray.reset();
        this.vertexArray.add(new float[] {(float) x, (float) y}, 2);
        this.vertexArray.draw(GL_POINTS);
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
    
    }
    
    /**
     * Fills an arc whose center is at {@code (x, y)} that is {@link #fill()} in color.
     * <p>
     * The coordinates passed in will be transformed by the view matrix
     *
     * @param x  The center x coordinate of the ellipse.
     * @param y  The center y coordinate of the ellipse.
     * @param rx The radius of the ellipse along the x axis.
     * @param ry The radius of the ellipse along the y axis.
     */
    @Override
    public void fillArc(double x, double y, double rx, double ry, double start, double stop)
    {
    
    }
    
    /**
     * Draws a textured rectangle whose top left corner is at {@code (x, y)} and is {@code w} pixels wide and {@code y} tall.
     * <p>
     * You can specify the coordinate of the texture to pull from.
     * <p>
     * The coordinates passed in will be transformed by the view matrix
     *
     * @param texture The texture to draw.
     * @param x       The top left corner x coordinate of the rectangle.
     * @param y       The top left corner y coordinate of the rectangle.
     * @param w       The width of the rectangle.
     * @param h       The height of the rectangle.
     * @param u       The top left corner x texture coordinate of the rectangle.
     * @param v       The top left corner y texture coordinate of the rectangle.
     * @param uw      The width of the texture rectangle.
     * @param vh      The height of the texture rectangle.
     */
    @Override
    public void drawTexture(Texture texture, double x, double y, double w, double h, double u, double v, double uw, double vh)
    {
    
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
        this.target.bind();
        this.target.download();
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
        this.target.bind();
        this.target.upload();
    }
    
    private void makeCurrent()
    {
        glBindFramebuffer(GL_FRAMEBUFFER, this.fbo);
        glViewport(0, 0, this.target.width(), this.target.height());
    }
}
