package engine.render;

import engine.color.Color;
import engine.color.Colorc;
import engine.util.Logger;
import org.joml.Matrix4d;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

/**
 * Abstract Renderer to draw things to a texture.
 */
public abstract class Renderer
{
    private static final Logger LOGGER = new Logger();
    
    private static final Color       DEFAULT_FILL         = new Color(255);
    private static final Color       DEFAULT_STROKE       = new Color(0);
    private static final double      DEFAULT_WEIGHT       = 5;
    private static final RectMode    DEFAULT_RECT_MODE    = RectMode.CORNER;
    private static final EllipseMode DEFAULT_ELLIPSE_MODE = EllipseMode.CENTER;
    private static final ArcMode     DEFAULT_ARC_MODE     = ArcMode.DEFAULT;
    private static final Font        DEFAULT_FONT         = new Font();
    private static final TextAlign   DEFAULT_TEXT_ALIGN   = TextAlign.TOP_LEFT;
    
    protected static final Color CLEAR = new Color();
    
    /**
     * Gets a new renderer instance for the target texture based on the string passed in.
     * <p>
     * If the string does not match a renderer, then the default SoftwareRenderer is used.
     *
     * @param target   The target texture.
     * @param renderer The name of the renderer.
     * @return The new Renderer instance.
     */
    public static Renderer getRenderer(Texture target, String renderer)
    {
        if (renderer.equals("software"))
        {
            Renderer.LOGGER.debug("Using Software Renderer");
            return new SoftwareRenderer(target);
        }
        else if (renderer.equals("pixel"))
        {
            Renderer.LOGGER.debug("Using Pixel Renderer");
            return new PixelRenderer(target);
        }
        else if (renderer.equals("opengl"))
        {
            Renderer.LOGGER.debug("Using OpenGL Renderer");
            return new OpenGLRenderer(target);
        }
        // else
        // {
        //     // TODO - Check for registered renderers?
        // }
        Renderer.LOGGER.warn("Could not parse renderer. Using Software Renderer");
        return new SoftwareRenderer(target);
    }
    
    protected final Texture target;
    
    protected boolean enableBlend = false;
    
    protected final Color        fill  = new Color(Renderer.DEFAULT_FILL);
    protected final Stack<Color> fills = new Stack<>();
    
    protected final Color        stroke  = new Color(Renderer.DEFAULT_STROKE);
    protected final Stack<Color> strokes = new Stack<>();
    
    protected       double        weight  = Renderer.DEFAULT_WEIGHT;
    protected final Stack<Double> weights = new Stack<>();
    
    protected       RectMode        rectMode  = Renderer.DEFAULT_RECT_MODE;
    protected final Stack<RectMode> rectModes = new Stack<>();
    
    protected       EllipseMode        ellipseMode  = Renderer.DEFAULT_ELLIPSE_MODE;
    protected final Stack<EllipseMode> ellipseModes = new Stack<>();
    
    protected       ArcMode        arcMode  = Renderer.DEFAULT_ARC_MODE;
    protected final Stack<ArcMode> arcModes = new Stack<>();
    
    protected       Font        font  = Renderer.DEFAULT_FONT;
    protected final Stack<Font> fonts = new Stack<>();
    
    protected       TextAlign        textAlign  = Renderer.DEFAULT_TEXT_ALIGN;
    protected final Stack<TextAlign> textAligns = new Stack<>();
    
    protected final Matrix4d        viewMatrix   = new Matrix4d();
    protected final Stack<Matrix4d> viewMatrices = new Stack<>();
    
    protected int[] pixels;
    
    private boolean drawing = false;
    
    protected Renderer(Texture target)
    {
        this.target = target;
        
        this.pixels = new int[this.target.width() * this.target.height() * this.target.channels()];
    }
    
    // ----------------
    // -- Properties --
    // ----------------
    
    /**
     * @return If blend is enabled for the renderer.
     */
    public boolean enableBlend() { return this.enableBlend; }
    
    /**
     * Sets if the renderer should blend when pixels are drawn.
     *
     * @param enableBlend If blend is enabled.
     */
    public void enableBlend(boolean enableBlend) { this.enableBlend = enableBlend; }
    
    /**
     * @return The current fill color.
     */
    public Colorc fill() { return this.fill; }
    
    /**
     * Sets the fill color.
     *
     * @param r The red value of the color [0-255] [0.0-1.0]
     * @param g The green value of the color [0-255] [0.0-1.0]
     * @param b The blue value of the color [0-255] [0.0-1.0]
     * @param a The alpha value of the color [0-255] [0.0-1.0]
     */
    public void fill(Number r, Number g, Number b, Number a) { this.fill.set(r, g, b, a); }
    
    /**
     * Sets the fill color.
     *
     * @param r The red value of the color [0-255] [0.0-1.0]
     * @param g The green value of the color [0-255] [0.0-1.0]
     * @param b The blue value of the color [0-255] [0.0-1.0]
     */
    public void fill(Number r, Number g, Number b) { this.fill.set(r, g, b); }
    
    /**
     * Sets the fill color.
     *
     * @param grey The red, green and blue value of the color [0-255] [0.0-1.0]
     * @param a    The alpha value of the color [0-255] [0.0-1.0]
     */
    public void fill(Number grey, Number a) { this.fill.set(grey, a); }
    
    /**
     * Sets the fill color.
     *
     * @param grey The red, green and blue value of the color [0-255] [0.0-1.0]
     */
    public void fill(Number grey) { this.fill.set(grey); }
    
    /**
     * Sets the fill color.
     *
     * @param fill The color to set fill to.
     */
    public void fill(Colorc fill) { this.fill.set(fill); }
    
    /**
     * Disabled the fill of shapes
     */
    public void noFill() { this.fill.a(0); }
    
    /**
     * @return The current stroke color.
     */
    public Colorc stroke() { return this.stroke; }
    
    /**
     * Sets the stroke color.
     *
     * @param r The red value of the color [0-255] [0.0-1.0]
     * @param g The green value of the color [0-255] [0.0-1.0]
     * @param b The blue value of the color [0-255] [0.0-1.0]
     * @param a The alpha value of the color [0-255] [0.0-1.0]
     */
    public void stroke(Number r, Number g, Number b, Number a) { this.stroke.set(r, g, b, a); }
    
    /**
     * Sets the stroke color.
     *
     * @param r The red value of the color [0-255] [0.0-1.0]
     * @param g The green value of the color [0-255] [0.0-1.0]
     * @param b The blue value of the color [0-255] [0.0-1.0]
     */
    public void stroke(Number r, Number g, Number b) { this.stroke.set(r, g, b); }
    
    /**
     * Sets the stroke color.
     *
     * @param grey The red, green and blue value of the color [0-255] [0.0-1.0]
     * @param a    The alpha value of the color [0-255] [0.0-1.0]
     */
    public void stroke(Number grey, Number a) { this.stroke.set(grey, a); }
    
    /**
     * Sets the stroke color.
     *
     * @param grey The red, green and blue value of the color [0-255] [0.0-1.0]
     */
    public void stroke(Number grey) { this.stroke.set(grey); }
    
    /**
     * Sets the stroke color.
     *
     * @param stroke The color to set fill to.
     */
    public void stroke(Colorc stroke) { this.stroke.set(stroke); }
    
    /**
     * Disabled the stroke of shapes
     */
    public void noStroke() { this.stroke.a(0); }
    
    /**
     * @return The stroke weight in pixels.
     */
    public double weight() { return this.weight; }
    
    /**
     * Sets the stroke weight.
     *
     * @param weight The new stroke weight in pixels. [1..Double.MAX_VALUE]
     */
    public void weight(double weight) { this.weight = Math.max(1, weight); }
    
    /**
     * @return The current {@link RectMode}
     */
    public RectMode rectMode() { return this.rectMode; }
    
    /**
     * Sets the {@link RectMode} option.
     * <p>
     * See {@link RectMode} for how the points get transformed.
     *
     * @param rectMode The new {@link RectMode} option.
     */
    public void rectMode(RectMode rectMode) { this.rectMode = rectMode; }
    
    /**
     * @return The current {@link EllipseMode}
     */
    public EllipseMode ellipseMode() { return this.ellipseMode; }
    
    /**
     * Sets the {@link EllipseMode} option.
     * <p>
     * See {@link EllipseMode} for how the points get transformed.
     *
     * @param ellipseMode The new {@link EllipseMode} option.
     */
    public void ellipseMode(EllipseMode ellipseMode) { this.ellipseMode = ellipseMode; }
    
    /**
     * @return The current {@link ArcMode}
     */
    public ArcMode arcMode() { return this.arcMode; }
    
    /**
     * Sets the {@link ArcMode} option.
     * <p>
     * See {@link ArcMode} for how the points get transformed.
     *
     * @param arcMode The new {@link ArcMode} option.
     */
    public void arcMode(ArcMode arcMode) { this.arcMode = arcMode; }
    
    /**
     * @return The current Font
     */
    public Font textFont() { return this.font; }
    
    /**
     * Sets the current Font.
     *
     * @param font The new font.
     */
    public void textFont(Font font) { this.font = font; }
    
    /**
     * Creates and sets the current Font.
     *
     * @param font The path to the ttf file.
     */
    public void textFont(String font) { this.font = new Font(font); }
    
    /**
     * Creates and sets the current Font.
     *
     * @param font The path to the ttf file.
     * @param size The size of the font in pixels. [4::Integer.MAX_VALUE]
     */
    public void textFont(String font, int size) { this.font = new Font(font, size); }
    
    /**
     * @return The size of the current font in pixels.
     */
    public int textSize() { return this.font.getSize(); }
    
    /**
     * Sets the size of the current Font.
     *
     * @param textSize The new size in pixels [4::Integer.MAX_VALUE]
     */
    public void textSize(int textSize) { this.font.setSize(textSize); }
    
    /**
     * @return The size in pixels of the current Font's ascent.
     */
    public double textAscent() { return this.font.getAscent(); }
    
    /**
     * @return The size in pixels of the current Font's descent.
     */
    public double textDescent() { return this.font.getDescent(); }
    
    /**
     * @return The current {@link TextAlign} value.
     */
    public TextAlign textAlign() { return this.textAlign; }
    
    /**
     * Sets the {@link TextAlign} option.
     * <p>
     * See {@link #text} for details on what each option does.
     *
     * @param textAlign The new {@link TextAlign} option.
     */
    public void textAlign(TextAlign textAlign) { this.textAlign = textAlign; }
    
    // ----------------------------
    // -- Transformation Methods --
    // ----------------------------
    
    /**
     * Resets the view space transformations.
     */
    public void identity() { this.viewMatrix.identity(); }
    
    /**
     * Translates the view space.
     *
     * @param x The amount to translate horizontally.
     * @param y The amount to translate vertically.
     */
    public void translate(double x, double y) { this.viewMatrix.translate(x, y, 0); }
    
    /**
     * Rotates the view space.
     *
     * @param angle The angle in radian to rotate by.
     */
    public void rotate(double angle) { this.viewMatrix.rotate(angle, 0, 0, 1); }
    
    /**
     * Scales the view space.
     *
     * @param x The amount to scale horizontally.
     * @param y The amount to scale vertically.
     */
    public void scale(double x, double y) { this.viewMatrix.scale(x, y, 1); }
    
    // --------------------
    // -- Render Methods --
    // --------------------
    
    /**
     * Begins the rendering process.
     * <p>
     * This must be called before any draw functions are called.
     */
    public void start()
    {
        if (this.drawing) throw new RuntimeException("Renderer was never finished");
        
        this.drawing = true;
        
        this.fill.set(Renderer.DEFAULT_FILL);
        this.fills.clear();
        
        this.stroke.set(Renderer.DEFAULT_STROKE);
        this.strokes.clear();
        
        this.weight = Renderer.DEFAULT_WEIGHT;
        this.weights.clear();
        
        this.rectMode = Renderer.DEFAULT_RECT_MODE;
        this.rectModes.clear();
        
        this.ellipseMode = Renderer.DEFAULT_ELLIPSE_MODE;
        this.ellipseModes.clear();
        
        this.arcMode = Renderer.DEFAULT_ARC_MODE;
        this.arcModes.clear();
        
        this.font = Renderer.DEFAULT_FONT;
        this.fonts.clear();
        
        this.textAlign = Renderer.DEFAULT_TEXT_ALIGN;
        this.textAligns.clear();
        
        this.viewMatrix.identity();
        this.viewMatrices.clear();
    }
    
    /**
     * Finishes the render.
     * <p>
     * This must be called after {@link #start}
     */
    public void finish()
    {
        if (!this.drawing) throw new RuntimeException("Renderer was never started");
        
        this.drawing = false;
    }
    
    /**
     * Creates a known state of the renderers properties that can be returned to by calling {@link #pop}.
     */
    public void push()
    {
        this.fills.push(new Color(this.fill));
        this.strokes.push(new Color(this.stroke));
        this.weights.push(this.weight);
        this.rectModes.push(this.rectMode);
        this.ellipseModes.push(this.ellipseMode);
        this.arcModes.push(this.arcMode);
        this.fonts.push(new Font(this.font));
        this.textAligns.push(this.textAlign);
        this.viewMatrices.push(new Matrix4d(this.viewMatrix));
    }
    
    /**
     * Returns the renderers properties to the state when {@link #push} was called.
     */
    public void pop()
    {
        this.fill.set(this.fills.pop());
        this.stroke.set(this.strokes.pop());
        this.weight      = this.weights.pop();
        this.rectMode    = this.rectModes.pop();
        this.ellipseMode = this.ellipseModes.pop();
        this.arcMode     = this.arcModes.pop();
        this.font        = this.fonts.pop();
        this.textAlign   = this.textAligns.pop();
        this.viewMatrix.set(this.viewMatrices.pop());
    }
    
    // -------------------
    // -- Clear Methods --
    // -------------------
    
    /**
     * Clears the render target to the color provided.
     *
     * @param r The red value to clear the target to. [0-255] [0.0-1.0]
     * @param g The green value to clear the target to. [0-255] [0.0-1.0]
     * @param b The blue value to clear the target to. [0-255] [0.0-1.0]
     * @param a The alpha value to clear the target to. [0-255] [0.0-1.0]
     */
    public void clear(Number r, Number g, Number b, Number a) { clear(Renderer.CLEAR.set(r, g, b, a)); }
    
    /**
     * Clears the render target to the color provided.
     *
     * @param r The red value to clear the target to. [0-255] [0.0-1.0]
     * @param g The green value to clear the target to. [0-255] [0.0-1.0]
     * @param b The blue value to clear the target to. [0-255] [0.0-1.0]
     */
    public void clear(Number r, Number g, Number b) { clear(Renderer.CLEAR.set(r, g, b)); }
    
    /**
     * Clears the render target to the color provided.
     *
     * @param grey The red, green and blue value to clear the target to. [0-255] [0.0-1.0]
     * @param a    The alpha value to clear the target to. [0-255] [0.0-1.0]
     */
    public void clear(Number grey, Number a) { clear(Renderer.CLEAR.set(grey, a)); }
    
    /**
     * Clears the render target to the color provided.
     *
     * @param grey The red, green and blue value to clear the target to. [0-255] [0.0-1.0]
     */
    public void clear(Number grey) { clear(Renderer.CLEAR.set(grey)); }
    
    /**
     * Clears the render target to the r: 51, g: 51, b: 51, a: 255
     */
    public void clear() { clear(Color.BACKGROUND_GREY); }
    
    /**
     * Clears the render target to the color provided.
     *
     * @param color The color to set the target to.
     */
    public abstract void clear(Colorc color);
    
    // -------------------
    // -- Point Methods --
    // -------------------
    
    /**
     * Draws a point that is {@link #weight()} in size and {@link #stroke()} in color.
     * <p>
     * The coordinates passed in will be transformed by the view matrix
     *
     * @param x The x coordinate to draw the point at.
     * @param y The y coordinate to draw the point at.
     */
    public abstract void drawPoint(double x, double y);
    
    /**
     * Draws a point that is {@link #weight()} in size and {@link #stroke()} in color.
     * <p>
     * If the strokes alpha is equal to zero then the point will not be drawn.
     * <p>
     * The coordinates passed in will be transformed by the view matrix
     *
     * @param x The x coordinate to draw the point at.
     * @param y The y coordinate to draw the point at.
     */
    public void point(double x, double y)
    {
        if (this.stroke.a() > 0) drawPoint(x, y);
    }
    
    // ------------------
    // -- Line Methods --
    // ------------------
    
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
    public abstract void drawLine(double x1, double y1, double x2, double y2);
    
    /**
     * Draws a line from {@code (x1, y1)} to {@code (x2, y2)} that is {@link #weight()} pixels thick and {@link #stroke()} in color.
     * <p>
     * If the strokes alpha is equal to zero then the point will not be drawn.
     * <p>
     * The coordinates passed in will be transformed by the view matrix
     *
     * @param x1 The start x coordinate to draw the line at.
     * @param y1 The start y coordinate to draw the line at.
     * @param x2 The end x coordinate to draw the line at.
     * @param y2 The end y coordinate to draw the line at.
     */
    public void line(double x1, double y1, double x2, double y2)
    {
        if (this.stroke.a() > 0) drawLine(x1, y1, x2, y2);
    }
    
    // --------------------
    // -- Bezier Methods --
    // --------------------
    
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
    public abstract void drawBezier(double x1, double y1, double x2, double y2, double x3, double y3);
    
    /**
     * Draws a bezier from {@code (x1, y1)} through {@code (x2, y2) to {@code (x3, y3)} that is {@link #weight()} pixels thick and {@link #stroke()} in color.
     * <p>
     * If the strokes alpha is equal to zero then the point will not be drawn.
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
    public void bezier(double x1, double y1, double x2, double y2, double x3, double y3)
    {
        if (this.stroke.a() > 0) drawBezier(x1, y1, x2, y2, x3, y3);
    }
    
    // ----------------------
    // -- Triangle Methods --
    // ----------------------
    
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
    public abstract void drawTriangle(double x1, double y1, double x2, double y2, double x3, double y3);
    
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
    public abstract void fillTriangle(double x1, double y1, double x2, double y2, double x3, double y3);
    
    /**
     * Draws a triangle from {@code (x1, y1)} through {@code (x2, y2) to {@code (x3, y3)} that is {@link #fill()} in color
     * with a border {@link #weight()} thick and {@link #stroke()} in color.
     * <p>
     * If the strokes alpha is equal to zero then the border will not be drawn.
     * <p>
     * If the fills alpha is equal to zero then the inside will not be filled.
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
    public void triangle(double x1, double y1, double x2, double y2, double x3, double y3)
    {
        if (this.fill.a() > 0) fillTriangle(x1, y1, x2, y2, x3, y3);
        if (this.stroke.a() > 0) drawTriangle(x1, y1, x2, y2, x3, y3);
    }
    
    // --------------------
    // -- Square Methods --
    // --------------------
    
    /**
     * Draws a square whose top left corner is at {@code (x, y)} that is {@link #weight()} pixels thick and {@link #stroke()} in color.
     * <p>
     * The coordinates passed in will be transformed by the view matrix
     *
     * @param x The top left x coordinate of the square.
     * @param y The top left y coordinate of the square.
     * @param w The side length of the square.
     */
    public abstract void drawSquare(double x, double y, double w);
    
    /**
     * Fills a square whose top left corner is at {@code (x, y)} that is {@link #fill()} in color.
     * <p>
     * The coordinates passed in will be transformed by the view matrix
     *
     * @param x The top left x coordinate of the square.
     * @param y The top left y coordinate of the square.
     * @param w The side length of the square.
     */
    public abstract void fillSquare(double x, double y, double w);
    
    /**
     * Draws a square based on {@link #rectMode()} that is {@link #fill()} in color
     * with a border {@link #weight()} thick and {@link #stroke()} in color.
     * <p>
     * See {@link RectMode} for how the points get transformed.
     * <p>
     * If the strokes alpha is equal to zero then the border will not be drawn.
     * <p>
     * If the fills alpha is equal to zero then the inside will not be filled.
     * <p>
     * The coordinates passed in will be transformed by the view matrix
     *
     * @param a The a value.
     * @param b The b value.
     * @param c The c value.
     */
    public void square(double a, double b, double c)
    {
        switch (this.rectMode)
        {
            case CORNER:
            default:
                if (this.fill.a() > 0) fillSquare(a, b, c);
                if (this.stroke.a() > 0) drawSquare(a, b, c);
                break;
            case CORNERS:
                double w = c - a;
                if (this.fill.a() > 0) fillSquare(a, b, w);
                if (this.stroke.a() > 0) drawSquare(a, b, w);
                break;
            case CENTER:
                if (this.fill.a() > 0) fillSquare(a - c * 0.5, b - c * 0.5, c);
                if (this.stroke.a() > 0) drawSquare(a - c * 0.5, b - c * 0.5, c);
                break;
            case RADIUS:
                if (this.fill.a() > 0) fillSquare(a - c, b - c, c * 2.0);
                if (this.stroke.a() > 0) drawSquare(a - c, b - c, c * 2.0);
                break;
        }
    }
    
    // ------------------
    // -- Rect Methods --
    // ------------------
    
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
    public abstract void drawRect(double x, double y, double w, double h);
    
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
    public abstract void fillRect(double x, double y, double w, double h);
    
    /**
     * Draws a rectangle based on {@link #rectMode()} that is {@link #fill()} in color
     * with a border {@link #weight()} thick and {@link #stroke()} in color.
     * <p>
     * See {@link RectMode} for how the points get transformed.
     * <p>
     * If the strokes alpha is equal to zero then the border will not be drawn.
     * <p>
     * If the fills alpha is equal to zero then the inside will not be filled.
     * <p>
     * The coordinates passed in will be transformed by the view matrix
     *
     * @param a The a value.
     * @param b The b value.
     * @param c The c value.
     * @param d The d value.
     */
    public void rect(double a, double b, double c, double d)
    {
        switch (this.rectMode)
        {
            case CORNER:
            default:
                if (this.fill.a() > 0) fillRect(a, b, c, d);
                if (this.stroke.a() > 0) drawRect(a, b, c, d);
                break;
            case CORNERS:
                if (this.fill.a() > 0) fillRect(a, b, c - a, d - b);
                if (this.stroke.a() > 0) drawRect(a, b, c - a, d - b);
                break;
            case CENTER:
                if (this.fill.a() > 0) fillRect(a - c * 0.5, b - d * 0.5, c, d);
                if (this.stroke.a() > 0) drawRect(a - c * 0.5, b - d * 0.5, c, d);
                break;
            case RADIUS:
                if (this.fill.a() > 0) fillRect(a - c, b - d, c * 2.0, d * 2.0);
                if (this.stroke.a() > 0) drawRect(a - c, b - d, c * 2.0, d * 2.0);
                break;
        }
    }
    
    // ------------------
    // -- Quad Methods --
    // ------------------
    
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
    public abstract void drawQuad(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4);
    
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
    public abstract void fillQuad(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4);
    
    /**
     * Draws a quad from the points provided that is {@link #weight()} pixels thick and {@link #stroke()} in color and filled with color {@link #fill()}.
     * <p>
     * The winding order does not matter.
     * <p>
     * If the strokes alpha is equal to zero then the border will not be drawn.
     * <p>
     * If the fills alpha is equal to zero then the inside will not be filled.
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
    public void quad(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4)
    {
        if (this.fill.a() > 0) fillQuad(x1, y1, x2, y2, x3, y3, x4, y4);
        if (this.stroke.a() > 0) drawQuad(x1, y1, x2, y2, x3, y3, x4, y4);
    }
    
    // ---------------------
    // -- Polygon Methods --
    // ---------------------
    
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
    public abstract void drawPolygon(double... points);
    
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
    public abstract void fillPolygon(double... points);
    
    /**
     * Draws a polygon from the points provided that is {@link #weight()} pixels thick and {@link #stroke()} in color and filled with color {@link #fill()}.
     * <p>
     * The winding order does not matter.
     * <p>
     * If the strokes alpha is equal to zero then the border will not be drawn.
     * <p>
     * If the fills alpha is equal to zero then the inside will not be filled.
     * <p>
     * The coordinates passed in will be transformed by the view matrix
     *
     * @param points The points.
     */
    public void polygon(double... points)
    {
        int n = points.length;
        
        if ((n & 1) == 1) throw new RuntimeException("Invalid coordinates. Must be an even number");
        if (n < 6) throw new RuntimeException("Invalid coordinates. Must have at least 3 coordinate pairs");
        
        if (this.fill.a() > 0) fillPolygon(points);
        if (this.stroke.a() > 0) drawPolygon(points);
    }
    
    // --------------------
    // -- Circle Methods --
    // --------------------
    
    /**
     * Draws a circle whose center is at {@code (x, y)} that is {@link #weight()} pixels thick and {@link #stroke()} in color.
     * <p>
     * The coordinates passed in will be transformed by the view matrix
     *
     * @param x The center x coordinate of the circle.
     * @param y The center y coordinate of the circle.
     * @param r The radius of the circle.
     */
    public abstract void drawCircle(double x, double y, double r);
    
    /**
     * Fills a circle whose center is at {@code (x, y)} that is {@link #fill()} in color.
     * <p>
     * The coordinates passed in will be transformed by the view matrix
     *
     * @param x The center x coordinate of the circle.
     * @param y The center y coordinate of the circle.
     * @param r The radius of the circle.
     */
    public abstract void fillCircle(double x, double y, double r);
    
    /**
     * Draws a circle based on {@link #ellipseMode()} that is {@link #fill()} in color
     * with a border {@link #weight()} thick and {@link #stroke()} in color.
     * <p>
     * See {@link EllipseMode} for how the points get transformed.
     * <p>
     * If the strokes alpha is equal to zero then the border will not be drawn.
     * <p>
     * If the fills alpha is equal to zero then the inside will not be filled.
     * <p>
     * The coordinates passed in will be transformed by the view matrix
     *
     * @param a The a value.
     * @param b The b value.
     * @param c The c value.
     */
    public void circle(double a, double b, double c)
    {
        switch (this.ellipseMode)
        {
            case CENTER:
            default:
                if (this.fill.a() > 0) fillCircle(a, b, c * 0.5);
                if (this.stroke.a() > 0) drawCircle(a, b, c * 0.5);
                break;
            case RADIUS:
                if (this.fill.a() > 0) fillCircle(a, b, c);
                if (this.stroke.a() > 0) drawCircle(a, b, c);
                break;
            case CORNER:
                c *= 0.5;
                if (this.fill.a() > 0) fillCircle(a + c, b + c, c);
                if (this.stroke.a() > 0) drawCircle(a + c, b + c, c);
                break;
            case CORNERS:
                throw new RuntimeException("CORNERS mode not supported for circle");
        }
    }
    
    // ---------------------
    // -- Ellipse Methods --
    // ---------------------
    
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
    public abstract void drawEllipse(double x, double y, double rx, double ry);
    
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
    public abstract void fillEllipse(double x, double y, double rx, double ry);
    
    /**
     * Draws an ellipse based on {@link #ellipseMode()} that is {@link #fill()} in color
     * with a border {@link #weight()} thick and {@link #stroke()} in color.
     * <p>
     * See {@link EllipseMode} for how the points get transformed.
     * <p>
     * If the strokes alpha is equal to zero then the border will not be drawn.
     * <p>
     * If the fills alpha is equal to zero then the inside will not be filled.
     * <p>
     * The coordinates passed in will be transformed by the view matrix
     *
     * @param a The a value.
     * @param b The b value.
     * @param c The c value.
     * @param d The d value.
     */
    public void ellipse(double a, double b, double c, double d)
    {
        switch (this.ellipseMode)
        {
            case CENTER:
            default:
                if (this.fill.a() > 0) fillEllipse(a, b, c * 0.5, d * 0.5);
                if (this.stroke.a() > 0) drawEllipse(a, b, c * 0.5, d * 0.5);
                break;
            case RADIUS:
                if (this.fill.a() > 0) fillEllipse(a, b, c, d);
                if (this.stroke.a() > 0) drawEllipse(a, b, c, d);
                break;
            case CORNER:
                c *= 0.5;
                d *= 0.5;
                if (this.fill.a() > 0) fillEllipse(a + c, b + d, c, d);
                if (this.stroke.a() > 0) drawEllipse(a + c, b + d, c, d);
                break;
            case CORNERS:
                double rx = (c - a) * 0.5, ry = (d - b) * 0.5;
                if (this.fill.a() > 0) fillEllipse(a + rx, b + ry, rx, ry);
                if (this.stroke.a() > 0) drawEllipse(a + rx, b + ry, rx, ry);
                break;
        }
    }
    
    // -----------------
    // -- Aec Methods --
    // -----------------
    
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
    public abstract void drawArc(double x, double y, double rx, double ry, double start, double stop);
    
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
    public abstract void fillArc(double x, double y, double rx, double ry, double start, double stop);
    
    /**
     * Draws an arc based on {@link #ellipseMode()} that is {@link #fill()} in color
     * with a border {@link #weight()} thick and {@link #stroke()} in color.
     * <p>
     * See {@link EllipseMode} for how the points get transformed.
     * <p>
     * See {@link ArcMode} for how the arc is drawn.
     * <p>
     * If the strokes alpha is equal to zero then the border will not be drawn.
     * <p>
     * If the fills alpha is equal to zero then the inside will not be filled.
     * <p>
     * The coordinates passed in will be transformed by the view matrix
     *
     * @param a The a value.
     * @param b The b value.
     * @param c The c value.
     */
    public void arc(double a, double b, double c, double d, double start, double stop)
    {
        switch (this.ellipseMode)
        {
            case CENTER:
            default:
                if (this.fill.a() > 0) fillArc(a, b, c * 0.5, d * 0.5, start, stop);
                if (this.stroke.a() > 0) drawArc(a, b, c * 0.5, d * 0.5, start, stop);
                break;
            case RADIUS:
                if (this.fill.a() > 0) fillArc(a, b, c, d, start, stop);
                if (this.stroke.a() > 0) drawArc(a, b, c, d, start, stop);
                break;
            case CORNER:
                c *= 0.5;
                d *= 0.5;
                if (this.fill.a() > 0) fillArc(a + c, b + d, c, d, start, stop);
                if (this.stroke.a() > 0) drawArc(a + c, b + d, c, d, start, stop);
                break;
            case CORNERS:
                double rx = (c - a) * 0.5, ry = (d - b) * 0.5;
                if (this.fill.a() > 0) fillArc(a + rx, b + ry, rx, ry, start, stop);
                if (this.stroke.a() > 0) drawArc(a + rx, b + ry, rx, ry, start, stop);
                break;
        }
    }
    
    // ---------------------
    // -- Texture Methods --
    // ---------------------
    
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
    public abstract void drawTexture(Texture texture, double x, double y, double w, double h, double u, double v, double uw, double vh);
    
    /**
     * Draws a textured rectangle based on {@link #rectMode()}, with uv coordinates.
     * <p>
     * See {@link RectMode} for how the points get transformed.
     * <p>
     * You can specify the coordinate of the texture to pull from.
     * <p>
     * The coordinates passed in will be transformed by the view matrix
     *
     * @param texture The texture to draw.
     * @param a       The a value.
     * @param b       The b value.
     * @param c       The c value.
     * @param d       The d value.
     * @param u       The top left corner x texture coordinate of the rectangle.
     * @param v       The top left corner y texture coordinate of the rectangle.
     * @param uw      The width of the texture rectangle.
     * @param vh      The height of the texture rectangle.
     */
    public void texture(Texture texture, double a, double b, double c, double d, double u, double v, double uw, double vh)
    {
        switch (this.rectMode)
        {
            case CORNER:
            default:
                drawTexture(texture, a, b, c, d, u, v, uw, vh);
                break;
            case CORNERS:
                drawTexture(texture, a, b, c - a, d - b, u, v, uw, vh);
                break;
            case CENTER:
                drawTexture(texture, a - c * 0.5, b - d * 0.5, c, d, u, v, uw, vh);
                break;
            case RADIUS:
                drawTexture(texture, a - c, b - d, c * 2.0, d * 2.0, u, v, uw, vh);
                break;
        }
    }
    
    /**
     * Draws a textured rectangle whose top left coordinate is at {@code (x, y)} the size of {@code texture}, with uv coordinates.
     * <p>
     * You can specify the coordinate of the texture to pull from.
     * <p>
     * The coordinates passed in will be transformed by the view matrix
     *
     * @param texture The texture to draw.
     * @param x       The top left x coordinate of the textured rectangle.
     * @param y       The top left y coordinate of the textured rectangle.
     * @param u       The top left corner x texture coordinate of the rectangle.
     * @param v       The top left corner y texture coordinate of the rectangle.
     * @param uw      The width of the texture rectangle.
     * @param vh      The height of the texture rectangle.
     */
    public void texture(Texture texture, double x, double y, double u, double v, double uw, double vh)
    {
        drawTexture(texture, x, y, texture.width(), texture.height(), u, v, uw, vh);
    }
    
    /**
     * Draws a textured rectangle based on {@link #rectMode()}.
     * <p>
     * See {@link RectMode} for how the points get transformed.
     * <p>
     * The coordinates passed in will be transformed by the view matrix
     *
     * @param texture The texture to draw.
     * @param a       The a value.
     * @param b       The b value.
     * @param c       The c value.
     * @param d       The d value.
     */
    public void texture(Texture texture, double a, double b, double c, double d)
    {
        texture(texture, a, b, c, d, 0, 0, 1, 1);
    }
    
    /**
     * Draws a textured rectangle whose top left coordinate is at {@code (x, y)} the size of {@code texture}.
     * <p>
     * The coordinates passed in will be transformed by the view matrix
     *
     * @param texture The texture to draw.
     * @param x       The top left x coordinate of the textured rectangle.
     * @param y       The top left y coordinate of the textured rectangle.
     */
    public void texture(Texture texture, double x, double y)
    {
        drawTexture(texture, x, y, texture.width(), texture.height(), 0, 0, 1, 1);
    }
    
    // ------------------
    // -- Text Methods --
    // ------------------
    
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
    public abstract void drawText(String text, double x, double y);
    
    /**
     * Draws a string of text to the screen contained in a rectangle described by the parameters.
     * If the text will be outside of the rectangle, then it wont be drawn.
     * <p>
     * The rectangle size and position is determined by {@link #rectMode()}.
     * <p>
     * You can change the font with {@link #textFont()}.
     * <p>
     * You can change the size of the text with {@link #textSize()}
     * <p>
     * The coordinates passed in will be transformed by the view matrix
     *
     * @param text The text to render
     * @param a    The a value.
     * @param b    The b value.
     * @param c    The c value.
     * @param d    The d value.
     */
    public void text(String text, double a, double b, double c, double d)
    {
        List<String> lines;
        
        double x = a, y = b;
        double w = 0, h = 0;
        
        if (c > 0 && d > 0)
        {
            switch (this.rectMode)
            {
                case CORNER:
                default:
                    w = c;
                    h = d;
                    break;
                case CORNERS:
                    w = c - a;
                    h = d - b;
                    break;
                case CENTER:
                    x = a - c * 0.5;
                    y = b - d * 0.5;
                    w = c;
                    h = d;
                    break;
                case RADIUS:
                    x = a - c;
                    y = b - d;
                    w = c * 2.0;
                    h = d * 2.0;
                    break;
            }
            lines = new ArrayList<>();
            for (String line : text.split("\n"))
            {
                if (this.font.getStringWidth(line) > w)
                {
                    String[]      subLines = line.split(" ");
                    StringBuilder builder  = new StringBuilder(subLines[0]);
                    for (int j = 1, n = subLines.length; j < n; j++)
                    {
                        if (this.font.getStringWidth(builder.toString() + " " + subLines[j]) > w)
                        {
                            if (this.font.getStringWidth(builder.toString()) > w) break;
                            if ((lines.size() + 1) * this.font.getSize() > h) break;
                            lines.add(builder.toString());
                            builder.setLength(0);
                            builder.append(subLines[j]);
                            continue;
                        }
                        builder.append(" ").append(subLines[j]);
                    }
                    if (this.font.getStringWidth(builder.toString()) > w) break;
                    if ((lines.size() + 1) * this.font.getSize() > h) break;
                    lines.add(builder.toString());
                }
                else
                {
                    if ((lines.size() + 1) * this.font.getSize() > h) break;
                    lines.add(line);
                }
            }
        }
        else
        {
            lines = Arrays.asList(text.split("\n"));
        }
        
        double actualHeight = lines.size() * this.font.getSize();
        
        int    hPos    = this.textAlign.getHorizontal();
        int    vPos    = this.textAlign.getVertical();
        double yOffset = vPos == -1 ? 0 : vPos == 0 ? 0.5 * (h - actualHeight) : h - actualHeight;
        for (String line : lines)
        {
            double lineWidth = this.font.getStringWidth(line);
            double xOffset   = hPos == -1 ? 0 : hPos == 0 ? 0.5 * (w - lineWidth) : w - lineWidth;
            
            drawText(line, x + xOffset, y + yOffset);
            
            yOffset += this.font.getSize();
        }
    }
    
    /**
     * Draws a string of text to the screen.
     * <p>
     * You can change the font with {@link #textFont()}.
     * <p>
     * You can change the size of the text with {@link #textSize()}
     * <p>
     * You can change the alignment of the text will change with {@link #textAlign()}.
     * <p>
     * The coordinates passed in will be transformed by the view matrix
     *
     * @param text The text to render
     * @param x    The x coordinate of the text.
     * @param y    The y coordinate of the text.
     */
    public void text(String text, double x, double y)
    {
        text(text, x, y, 0, 0);
    }
    
    // -------------------
    // -- Pixel Methods --
    // -------------------
    
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
    public abstract int[] loadPixels();
    
    /**
     * Updates the target with the values of the pixel array.
     * <p>
     * This will do nothing if {@link #loadPixels()} is not called and the pixel array is not modified.
     */
    public abstract void updatePixels();
}
