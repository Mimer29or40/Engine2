package engine.render;

import engine.color.Color;
import engine.color.Colorc;
import org.joml.Matrix4d;
import org.joml.Vector2dc;
import org.joml.Vector2fc;
import org.joml.Vector2ic;

import java.util.ArrayList;
import java.util.Stack;

public abstract class Renderer
{
    private static final Color       DEFAULT_FILL         = new Color(255);
    private static final Color       DEFAULT_STROKE       = new Color(51);
    private static final double      DEFAULT_WEIGHT       = 5;
    private static final RectMode    DEFAULT_RECT_MODE    = RectMode.CORNER;
    private static final EllipseMode DEFAULT_ELLIPSE_MODE = EllipseMode.CENTER;
    private static final ArcMode     DEFAULT_ARC_MODE     = ArcMode.OPEN;
    private static final double      DEFAULT_TEXT_SIZE    = 8;
    private static final TextAlign   DEFAULT_TEXT_ALIGN   = TextAlign.TOP_LEFT;
    
    protected static final Color CLEAR = new Color();
    
    public static Renderer getRenderer(Texture target)
    {
        // TODO - Set renderer then return a new instance here.
        return new SoftwareRenderer(target);
    }
    
    protected Texture target;
    
    protected boolean blend = false;
    
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
    
    protected       double        textSize  = Renderer.DEFAULT_TEXT_SIZE;
    protected final Stack<Double> textSizes = new Stack<>();
    
    protected       TextAlign        textAlign  = Renderer.DEFAULT_TEXT_ALIGN;
    protected final Stack<TextAlign> textAligns = new Stack<>();
    
    protected final Matrix4d        viewMatrix   = new Matrix4d();
    protected final Stack<Matrix4d> viewMatrices = new Stack<>();
    
    private static int[] pixels;
    
    protected Renderer(Texture target)
    {
        this.target = target;
    }
    
    public boolean blend()           { return this.blend; }
    
    public void blend(boolean blend) { this.blend = blend; }
    
    // ----------------
    // -- Properties --
    // ----------------
    
    public Colorc fill()                                       { return this.fill; }
    
    public void fill(Number r, Number g, Number b, Number a)   { this.fill.set(r, g, b, a); }
    
    public void fill(Number r, Number g, Number b)             { this.fill.set(r, g, b); }
    
    public void fill(Number grey, Number a)                    { this.fill.set(grey, a); }
    
    public void fill(Number grey)                              { this.fill.set(grey); }
    
    public void fill(Colorc fill)                              { this.fill.set(fill); }
    
    public void noFill()                                       { this.fill.a(0); }
    
    public Colorc stroke()                                     { return this.stroke; }
    
    public void stroke(Number r, Number g, Number b, Number a) { this.stroke.set(r, g, b, a); }
    
    public void stroke(Number r, Number g, Number b)           { this.stroke.set(r, g, b); }
    
    public void stroke(Number grey, Number a)                  { this.stroke.set(grey, a); }
    
    public void stroke(Number grey)                            { this.stroke.set(grey); }
    
    public void stroke(Colorc stroke)                          { this.stroke.set(stroke); }
    
    public void noStroke()                                     { this.stroke.a(0); }
    
    public double weight()                                     { return this.weight; }
    
    public void weight(double weight)                          { this.weight = Math.max(1, weight); }
    
    public RectMode rectMode()                                 { return this.rectMode; }
    
    public void rectMode(RectMode rectMode)                    { this.rectMode = rectMode; }
    
    public EllipseMode ellipseMode()                           { return this.ellipseMode; }
    
    public void ellipseMode(EllipseMode ellipseMode)           { this.ellipseMode = ellipseMode; }
    
    public ArcMode arcMode()                                   { return this.arcMode; }
    
    public void arcMode(ArcMode arcMode)                       { this.arcMode = arcMode; }
    
    public double textSize()                                   { return this.textSize; }
    
    public void textSize(double textSize)                      { this.textSize = Math.max(1, textSize); }
    
    public TextAlign textAlign()                               { return this.textAlign; }
    
    public void textAlign(TextAlign textAlign)                 { this.textAlign = textAlign; }
    
    // ----------------------------
    // -- Transformation Methods --
    // ----------------------------
    
    public void identity()                    { this.viewMatrix.identity(); }
    
    public void translate(double x, double y) { this.viewMatrix.translate(x, y, 0); }
    
    public void translate(Vector2ic vector)   { translate(vector.x(), vector.y()); }
    
    public void translate(Vector2fc vector)   { translate(vector.x(), vector.y()); }
    
    public void translate(Vector2dc vector)   { translate(vector.x(), vector.y()); }
    
    public void rotate(double angle)          { this.viewMatrix.rotate(angle, 0, 0, 1); }
    
    public void scale(double x, double y)     { this.viewMatrix.scale(x, y, 1); }
    
    public void scale(Vector2ic vector)       { scale(vector.x(), vector.y()); }
    
    public void scale(Vector2fc vector)       { scale(vector.x(), vector.y()); }
    
    public void scale(Vector2dc vector)       { scale(vector.x(), vector.y()); }
    
    // --------------------
    // -- Render Methods --
    // --------------------
    
    public void begin()
    {
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
        
        this.textSize = Renderer.DEFAULT_TEXT_SIZE;
        this.textSizes.clear();
        
        this.textAlign = Renderer.DEFAULT_TEXT_ALIGN;
        this.textAligns.clear();
        
        this.viewMatrix.identity();
        this.viewMatrices.clear();
    }
    
    public abstract void finish();
    
    public void push()
    {
        this.fills.push(new Color(this.fill));
        this.strokes.push(new Color(this.stroke));
        this.weights.push(this.weight);
        this.rectModes.push(this.rectMode);
        this.ellipseModes.push(this.ellipseMode);
        this.arcModes.push(this.arcMode);
        this.textSizes.push(this.textSize);
        this.textAligns.push(this.textAlign);
        this.viewMatrices.push(new Matrix4d(this.viewMatrix));
    }
    
    public void pop()
    {
        this.fill.set(this.fills.pop());
        this.stroke.set(this.strokes.pop());
        this.weight      = this.weights.pop();
        this.rectMode    = this.rectModes.pop();
        this.ellipseMode = this.ellipseModes.pop();
        this.arcMode     = this.arcModes.pop();
        this.textSize    = this.textSizes.pop();
        this.textAlign   = this.textAligns.pop();
        this.viewMatrix.set(this.viewMatrices.pop());
    }
    
    // -------------------
    // -- Clear Methods --
    // -------------------
    
    public void clear(Number r, Number g, Number b, Number a) { clear(Renderer.CLEAR.set(r, g, b, a)); }
    
    public void clear(Number r, Number g, Number b)           { clear(Renderer.CLEAR.set(r, g, b)); }
    
    public void clear(Number grey, Number a)                  { clear(Renderer.CLEAR.set(grey, a)); }
    
    public void clear(Number grey)                            { clear(Renderer.CLEAR.set(grey)); }
    
    public void clear()                                       { clear(Color.BACKGROUND_GREY); }
    
    public abstract void clear(Colorc clear);
    
    // TODO - Have methods that handle 3D coordinates
    
    // -------------------
    // -- Point Methods --
    // -------------------
    
    public abstract void drawPoint(double x, double y);
    
    public void point(double x, double y)
    {
        if (this.stroke.a() > 0) drawPoint(x, y);
    }
    
    public void point(Vector2ic p) { point(p.x(), p.y()); }
    
    public void point(Vector2fc p) { point(p.x(), p.y()); }
    
    public void point(Vector2dc p) { point(p.x(), p.y()); }
    
    // ------------------
    // -- Line Methods --
    // ------------------
    
    public abstract void drawLine(double x1, double y1, double x2, double y2);
    
    public void line(double x1, double y1, double x2, double y2)
    {
        if (this.stroke.a() > 0) drawLine(x1, y1, x2, y2);
    }
    
    public void line(Vector2ic p1, Vector2ic p2)         { line(p1.x(), p1.y(), p2.x(), p2.y()); }
    
    public void line(Vector2ic p1, Vector2fc p2)         { line(p1.x(), p1.y(), p2.x(), p2.y()); }
    
    public void line(Vector2ic p1, Vector2dc p2)         { line(p1.x(), p1.y(), p2.x(), p2.y()); }
    
    public void line(Vector2fc p1, Vector2ic p2)         { line(p1.x(), p1.y(), p2.x(), p2.y()); }
    
    public void line(Vector2fc p1, Vector2fc p2)         { line(p1.x(), p1.y(), p2.x(), p2.y()); }
    
    public void line(Vector2fc p1, Vector2dc p2)         { line(p1.x(), p1.y(), p2.x(), p2.y()); }
    
    public void line(Vector2dc p1, Vector2ic p2)         { line(p1.x(), p1.y(), p2.x(), p2.y()); }
    
    public void line(Vector2dc p1, Vector2fc p2)         { line(p1.x(), p1.y(), p2.x(), p2.y()); }
    
    public void line(Vector2dc p1, Vector2dc p2)         { line(p1.x(), p1.y(), p2.x(), p2.y()); }
    
    public void line(Vector2ic p1, double x2, double y2) { line(p1.x(), p1.y(), x2, y2); }
    
    public void line(Vector2fc p1, double x2, double y2) { line(p1.x(), p1.y(), x2, y2); }
    
    public void line(Vector2dc p1, double x2, double y2) { line(p1.x(), p1.y(), x2, y2); }
    
    public void line(double x1, double y1, Vector2ic p2) { line(x1, y1, p2.x(), p2.y()); }
    
    public void line(double x1, double y1, Vector2fc p2) { line(x1, y1, p2.x(), p2.y()); }
    
    public void line(double x1, double y1, Vector2dc p2) { line(x1, y1, p2.x(), p2.y()); }
    
    // --------------------
    // -- Bezier Methods --
    // --------------------
    
    public abstract void drawBezier(double x1, double y1, double x2, double y2, double x3, double y3);
    
    public void bezier(double x1, double y1, double x2, double y2, double x3, double y3)
    {
        if (this.stroke.a() > 0) drawBezier(x1, y1, x2, y2, x3, y3);
    }
    
    public void bezier(Vector2ic p1, Vector2ic p2, Vector2ic p3)                 { bezier(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y()); }
    
    public void bezier(Vector2ic p1, Vector2ic p2, Vector2fc p3)                 { bezier(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y()); }
    
    public void bezier(Vector2ic p1, Vector2ic p2, Vector2dc p3)                 { bezier(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y()); }
    
    public void bezier(Vector2ic p1, Vector2fc p2, Vector2ic p3)                 { bezier(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y()); }
    
    public void bezier(Vector2ic p1, Vector2fc p2, Vector2fc p3)                 { bezier(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y()); }
    
    public void bezier(Vector2ic p1, Vector2fc p2, Vector2dc p3)                 { bezier(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y()); }
    
    public void bezier(Vector2ic p1, Vector2dc p2, Vector2ic p3)                 { bezier(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y()); }
    
    public void bezier(Vector2ic p1, Vector2dc p2, Vector2fc p3)                 { bezier(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y()); }
    
    public void bezier(Vector2ic p1, Vector2dc p2, Vector2dc p3)                 { bezier(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y()); }
    
    public void bezier(Vector2fc p1, Vector2fc p2, Vector2fc p3)                 { bezier(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y()); }
    
    public void bezier(Vector2dc p1, Vector2dc p2, Vector2dc p3)                 { bezier(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y()); }
    
    public void bezier(double x1, double y1, Vector2ic p2, Vector2ic p3)         { bezier(x1, y1, p2.x(), p2.y(), p3.x(), p3.y()); }
    
    public void bezier(double x1, double y1, Vector2ic p2, Vector2fc p3)         { bezier(x1, y1, p2.x(), p2.y(), p3.x(), p3.y()); }
    
    public void bezier(double x1, double y1, Vector2ic p2, Vector2dc p3)         { bezier(x1, y1, p2.x(), p2.y(), p3.x(), p3.y()); }
    
    public void bezier(double x1, double y1, Vector2fc p2, Vector2ic p3)         { bezier(x1, y1, p2.x(), p2.y(), p3.x(), p3.y()); }
    
    public void bezier(double x1, double y1, Vector2fc p2, Vector2fc p3)         { bezier(x1, y1, p2.x(), p2.y(), p3.x(), p3.y()); }
    
    public void bezier(double x1, double y1, Vector2fc p2, Vector2dc p3)         { bezier(x1, y1, p2.x(), p2.y(), p3.x(), p3.y()); }
    
    public void bezier(double x1, double y1, Vector2dc p2, Vector2ic p3)         { bezier(x1, y1, p2.x(), p2.y(), p3.x(), p3.y()); }
    
    public void bezier(double x1, double y1, Vector2dc p2, Vector2fc p3)         { bezier(x1, y1, p2.x(), p2.y(), p3.x(), p3.y()); }
    
    public void bezier(double x1, double y1, Vector2dc p2, Vector2dc p3)         { bezier(x1, y1, p2.x(), p2.y(), p3.x(), p3.y()); }
    
    public void bezier(Vector2ic p1, double x2, double y2, Vector2ic p3)         { bezier(p1.x(), p1.y(), x2, y2, p3.x(), p3.y()); }
    
    public void bezier(Vector2ic p1, double x2, double y2, Vector2fc p3)         { bezier(p1.x(), p1.y(), x2, y2, p3.x(), p3.y()); }
    
    public void bezier(Vector2ic p1, double x2, double y2, Vector2dc p3)         { bezier(p1.x(), p1.y(), x2, y2, p3.x(), p3.y()); }
    
    public void bezier(Vector2fc p1, double x2, double y2, Vector2ic p3)         { bezier(p1.x(), p1.y(), x2, y2, p3.x(), p3.y()); }
    
    public void bezier(Vector2fc p1, double x2, double y2, Vector2fc p3)         { bezier(p1.x(), p1.y(), x2, y2, p3.x(), p3.y()); }
    
    public void bezier(Vector2fc p1, double x2, double y2, Vector2dc p3)         { bezier(p1.x(), p1.y(), x2, y2, p3.x(), p3.y()); }
    
    public void bezier(Vector2dc p1, double x2, double y2, Vector2ic p3)         { bezier(p1.x(), p1.y(), x2, y2, p3.x(), p3.y()); }
    
    public void bezier(Vector2dc p1, double x2, double y2, Vector2fc p3)         { bezier(p1.x(), p1.y(), x2, y2, p3.x(), p3.y()); }
    
    public void bezier(Vector2dc p1, double x2, double y2, Vector2dc p3)         { bezier(p1.x(), p1.y(), x2, y2, p3.x(), p3.y()); }
    
    public void bezier(Vector2ic p1, Vector2ic p2, double x3, double y3)         { bezier(p1.x(), p1.y(), p2.x(), p2.y(), x3, y3); }
    
    public void bezier(Vector2ic p1, Vector2fc p2, double x3, double y3)         { bezier(p1.x(), p1.y(), p2.x(), p2.y(), x3, y3); }
    
    public void bezier(Vector2ic p1, Vector2dc p2, double x3, double y3)         { bezier(p1.x(), p1.y(), p2.x(), p2.y(), x3, y3); }
    
    public void bezier(Vector2fc p1, Vector2ic p2, double x3, double y3)         { bezier(p1.x(), p1.y(), p2.x(), p2.y(), x3, y3); }
    
    public void bezier(Vector2fc p1, Vector2fc p2, double x3, double y3)         { bezier(p1.x(), p1.y(), p2.x(), p2.y(), x3, y3); }
    
    public void bezier(Vector2fc p1, Vector2dc p2, double x3, double y3)         { bezier(p1.x(), p1.y(), p2.x(), p2.y(), x3, y3); }
    
    public void bezier(Vector2dc p1, Vector2ic p2, double x3, double y3)         { bezier(p1.x(), p1.y(), p2.x(), p2.y(), x3, y3); }
    
    public void bezier(Vector2dc p1, Vector2fc p2, double x3, double y3)         { bezier(p1.x(), p1.y(), p2.x(), p2.y(), x3, y3); }
    
    public void bezier(Vector2dc p1, Vector2dc p2, double x3, double y3)         { bezier(p1.x(), p1.y(), p2.x(), p2.y(), x3, y3); }
    
    public void bezier(double x1, double y1, double x2, double y2, Vector2ic p3) { bezier(x1, y1, x2, y2, p3.x(), p3.y()); }
    
    public void bezier(double x1, double y1, double x2, double y2, Vector2fc p3) { bezier(x1, y1, x2, y2, p3.x(), p3.y()); }
    
    public void bezier(double x1, double y1, double x2, double y2, Vector2dc p3) { bezier(x1, y1, x2, y2, p3.x(), p3.y()); }
    
    public void bezier(Vector2ic p1, double x2, double y2, double x3, double y3) { bezier(p1.x(), p1.y(), x2, y2, x3, y3); }
    
    public void bezier(Vector2fc p1, double x2, double y2, double x3, double y3) { bezier(p1.x(), p1.y(), x2, y2, x3, y3); }
    
    public void bezier(Vector2dc p1, double x2, double y2, double x3, double y3) { bezier(p1.x(), p1.y(), x2, y2, x3, y3); }
    
    public void bezier(double x1, double y1, Vector2ic p2, double x3, double y3) { bezier(x1, y1, p2.x(), p2.y(), x3, y3); }
    
    public void bezier(double x1, double y1, Vector2fc p2, double x3, double y3) { bezier(x1, y1, p2.x(), p2.y(), x3, y3); }
    
    public void bezier(double x1, double y1, Vector2dc p2, double x3, double y3) { bezier(x1, y1, p2.x(), p2.y(), x3, y3); }
    
    // ----------------------
    // -- Triangle Methods --
    // ----------------------
    
    public abstract void drawTriangle(double x1, double y1, double x2, double y2, double x3, double y3);
    
    public abstract void fillTriangle(double x1, double y1, double x2, double y2, double x3, double y3);
    
    public void triangle(double x1, double y1, double x2, double y2, double x3, double y3)
    {
        if (this.fill.a() > 0) fillTriangle(x1, y1, x2, y2, x3, y3);
        if (this.stroke.a() > 0) drawTriangle(x1, y1, x2, y2, x3, y3);
    }
    
    public void triangle(Vector2ic p1, Vector2ic p2, Vector2ic p3)                 { triangle(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y()); }
    
    public void triangle(Vector2ic p1, Vector2ic p2, Vector2fc p3)                 { triangle(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y()); }
    
    public void triangle(Vector2ic p1, Vector2ic p2, Vector2dc p3)                 { triangle(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y()); }
    
    public void triangle(Vector2ic p1, Vector2fc p2, Vector2ic p3)                 { triangle(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y()); }
    
    public void triangle(Vector2ic p1, Vector2fc p2, Vector2fc p3)                 { triangle(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y()); }
    
    public void triangle(Vector2ic p1, Vector2fc p2, Vector2dc p3)                 { triangle(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y()); }
    
    public void triangle(Vector2ic p1, Vector2dc p2, Vector2ic p3)                 { triangle(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y()); }
    
    public void triangle(Vector2ic p1, Vector2dc p2, Vector2fc p3)                 { triangle(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y()); }
    
    public void triangle(Vector2ic p1, Vector2dc p2, Vector2dc p3)                 { triangle(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y()); }
    
    public void triangle(Vector2fc p1, Vector2fc p2, Vector2fc p3)                 { triangle(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y()); }
    
    public void triangle(Vector2dc p1, Vector2dc p2, Vector2dc p3)                 { triangle(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y()); }
    
    public void triangle(double x1, double y1, Vector2ic p2, Vector2ic p3)         { triangle(x1, y1, p2.x(), p2.y(), p3.x(), p3.y()); }
    
    public void triangle(double x1, double y1, Vector2ic p2, Vector2fc p3)         { triangle(x1, y1, p2.x(), p2.y(), p3.x(), p3.y()); }
    
    public void triangle(double x1, double y1, Vector2ic p2, Vector2dc p3)         { triangle(x1, y1, p2.x(), p2.y(), p3.x(), p3.y()); }
    
    public void triangle(double x1, double y1, Vector2fc p2, Vector2ic p3)         { triangle(x1, y1, p2.x(), p2.y(), p3.x(), p3.y()); }
    
    public void triangle(double x1, double y1, Vector2fc p2, Vector2fc p3)         { triangle(x1, y1, p2.x(), p2.y(), p3.x(), p3.y()); }
    
    public void triangle(double x1, double y1, Vector2fc p2, Vector2dc p3)         { triangle(x1, y1, p2.x(), p2.y(), p3.x(), p3.y()); }
    
    public void triangle(double x1, double y1, Vector2dc p2, Vector2ic p3)         { triangle(x1, y1, p2.x(), p2.y(), p3.x(), p3.y()); }
    
    public void triangle(double x1, double y1, Vector2dc p2, Vector2fc p3)         { triangle(x1, y1, p2.x(), p2.y(), p3.x(), p3.y()); }
    
    public void triangle(double x1, double y1, Vector2dc p2, Vector2dc p3)         { triangle(x1, y1, p2.x(), p2.y(), p3.x(), p3.y()); }
    
    public void triangle(Vector2ic p1, double x2, double y2, Vector2ic p3)         { triangle(p1.x(), p1.y(), x2, y2, p3.x(), p3.y()); }
    
    public void triangle(Vector2ic p1, double x2, double y2, Vector2fc p3)         { triangle(p1.x(), p1.y(), x2, y2, p3.x(), p3.y()); }
    
    public void triangle(Vector2ic p1, double x2, double y2, Vector2dc p3)         { triangle(p1.x(), p1.y(), x2, y2, p3.x(), p3.y()); }
    
    public void triangle(Vector2fc p1, double x2, double y2, Vector2ic p3)         { triangle(p1.x(), p1.y(), x2, y2, p3.x(), p3.y()); }
    
    public void triangle(Vector2fc p1, double x2, double y2, Vector2fc p3)         { triangle(p1.x(), p1.y(), x2, y2, p3.x(), p3.y()); }
    
    public void triangle(Vector2fc p1, double x2, double y2, Vector2dc p3)         { triangle(p1.x(), p1.y(), x2, y2, p3.x(), p3.y()); }
    
    public void triangle(Vector2dc p1, double x2, double y2, Vector2ic p3)         { triangle(p1.x(), p1.y(), x2, y2, p3.x(), p3.y()); }
    
    public void triangle(Vector2dc p1, double x2, double y2, Vector2fc p3)         { triangle(p1.x(), p1.y(), x2, y2, p3.x(), p3.y()); }
    
    public void triangle(Vector2dc p1, double x2, double y2, Vector2dc p3)         { triangle(p1.x(), p1.y(), x2, y2, p3.x(), p3.y()); }
    
    public void triangle(Vector2ic p1, Vector2ic p2, double x3, double y3)         { triangle(p1.x(), p1.y(), p2.x(), p2.y(), x3, y3); }
    
    public void triangle(Vector2ic p1, Vector2fc p2, double x3, double y3)         { triangle(p1.x(), p1.y(), p2.x(), p2.y(), x3, y3); }
    
    public void triangle(Vector2ic p1, Vector2dc p2, double x3, double y3)         { triangle(p1.x(), p1.y(), p2.x(), p2.y(), x3, y3); }
    
    public void triangle(Vector2fc p1, Vector2ic p2, double x3, double y3)         { triangle(p1.x(), p1.y(), p2.x(), p2.y(), x3, y3); }
    
    public void triangle(Vector2fc p1, Vector2fc p2, double x3, double y3)         { triangle(p1.x(), p1.y(), p2.x(), p2.y(), x3, y3); }
    
    public void triangle(Vector2fc p1, Vector2dc p2, double x3, double y3)         { triangle(p1.x(), p1.y(), p2.x(), p2.y(), x3, y3); }
    
    public void triangle(Vector2dc p1, Vector2ic p2, double x3, double y3)         { triangle(p1.x(), p1.y(), p2.x(), p2.y(), x3, y3); }
    
    public void triangle(Vector2dc p1, Vector2fc p2, double x3, double y3)         { triangle(p1.x(), p1.y(), p2.x(), p2.y(), x3, y3); }
    
    public void triangle(Vector2dc p1, Vector2dc p2, double x3, double y3)         { triangle(p1.x(), p1.y(), p2.x(), p2.y(), x3, y3); }
    
    public void triangle(double x1, double y1, double x2, double y2, Vector2ic p3) { triangle(x1, y1, x2, y2, p3.x(), p3.y()); }
    
    public void triangle(double x1, double y1, double x2, double y2, Vector2fc p3) { triangle(x1, y1, x2, y2, p3.x(), p3.y()); }
    
    public void triangle(double x1, double y1, double x2, double y2, Vector2dc p3) { triangle(x1, y1, x2, y2, p3.x(), p3.y()); }
    
    public void triangle(Vector2ic p1, double x2, double y2, double x3, double y3) { triangle(p1.x(), p1.y(), x2, y2, x3, y3); }
    
    public void triangle(Vector2fc p1, double x2, double y2, double x3, double y3) { triangle(p1.x(), p1.y(), x2, y2, x3, y3); }
    
    public void triangle(Vector2dc p1, double x2, double y2, double x3, double y3) { triangle(p1.x(), p1.y(), x2, y2, x3, y3); }
    
    public void triangle(double x1, double y1, Vector2ic p2, double x3, double y3) { triangle(x1, y1, p2.x(), p2.y(), x3, y3); }
    
    public void triangle(double x1, double y1, Vector2fc p2, double x3, double y3) { triangle(x1, y1, p2.x(), p2.y(), x3, y3); }
    
    public void triangle(double x1, double y1, Vector2dc p2, double x3, double y3) { triangle(x1, y1, p2.x(), p2.y(), x3, y3); }
    
    // --------------------
    // -- Square Methods --
    // --------------------
    
    public abstract void drawSquare(double x, double y, double w);
    
    public abstract void fillSquare(double x, double y, double w);
    
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
    
    public void square(Vector2ic ab, double c) { square(ab.x(), ab.y(), c); }
    
    public void square(Vector2fc ab, double c) { square(ab.x(), ab.y(), c); }
    
    public void square(Vector2dc ab, double c) { square(ab.x(), ab.y(), c); }
    
    // ------------------
    // -- Rect Methods --
    // ------------------
    
    public abstract void drawRect(double x, double y, double w, double h);
    
    public abstract void fillRect(double x, double y, double w, double h);
    
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
                double w = c - a, h = d - b;
                if (this.fill.a() > 0) fillRect(a, b, w, h);
                if (this.stroke.a() > 0) drawRect(a, b, w, h);
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
    
    public void rect(Vector2ic ab, Vector2ic cd)       { rect(ab.x(), ab.y(), cd.x(), cd.y()); }
    
    public void rect(Vector2ic ab, Vector2fc cd)       { rect(ab.x(), ab.y(), cd.x(), cd.y()); }
    
    public void rect(Vector2ic ab, Vector2dc cd)       { rect(ab.x(), ab.y(), cd.x(), cd.y()); }
    
    public void rect(Vector2fc ab, Vector2ic cd)       { rect(ab.x(), ab.y(), cd.x(), cd.y()); }
    
    public void rect(Vector2fc ab, Vector2fc cd)       { rect(ab.x(), ab.y(), cd.x(), cd.y()); }
    
    public void rect(Vector2fc ab, Vector2dc cd)       { rect(ab.x(), ab.y(), cd.x(), cd.y()); }
    
    public void rect(Vector2dc ab, Vector2ic cd)       { rect(ab.x(), ab.y(), cd.x(), cd.y()); }
    
    public void rect(Vector2dc ab, Vector2fc cd)       { rect(ab.x(), ab.y(), cd.x(), cd.y()); }
    
    public void rect(Vector2dc ab, Vector2dc cd)       { rect(ab.x(), ab.y(), cd.x(), cd.y()); }
    
    public void rect(Vector2ic ab, double c, double d) { rect(ab.x(), ab.y(), c, d); }
    
    public void rect(Vector2fc ab, double c, double d) { rect(ab.x(), ab.y(), c, d); }
    
    public void rect(Vector2dc ab, double c, double d) { rect(ab.x(), ab.y(), c, d); }
    
    public void rect(double a, double b, Vector2ic cd) { rect(a, b, cd.x(), cd.y()); }
    
    public void rect(double a, double b, Vector2fc cd) { rect(a, b, cd.x(), cd.y()); }
    
    public void rect(double a, double b, Vector2dc cd) { rect(a, b, cd.x(), cd.y()); }
    
    // ------------------
    // -- Quad Methods --
    // ------------------
    
    public abstract void drawQuad(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4);
    
    public abstract void fillQuad(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4);
    
    public void quad(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4)
    {
        if (this.fill.a() > 0) fillQuad(x1, y1, x2, y2, x3, y3, x4, y4);
        if (this.stroke.a() > 0) drawQuad(x1, y1, x2, y2, x3, y3, x4, y4);
    }
    
    public void quad(Vector2ic p1, Vector2ic p2, Vector2ic p3, Vector2ic p4)                 { quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y()); }
    
    public void quad(Vector2ic p1, Vector2ic p2, Vector2fc p3, Vector2fc p4)                 { quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y()); }
    
    public void quad(Vector2ic p1, Vector2ic p2, Vector2dc p3, Vector2dc p4)                 { quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y()); }
    
    public void quad(Vector2ic p1, Vector2fc p2, Vector2ic p3, Vector2ic p4)                 { quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y()); }
    
    public void quad(Vector2ic p1, Vector2fc p2, Vector2fc p3, Vector2fc p4)                 { quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y()); }
    
    public void quad(Vector2ic p1, Vector2fc p2, Vector2dc p3, Vector2dc p4)                 { quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y()); }
    
    public void quad(Vector2ic p1, Vector2dc p2, Vector2ic p3, Vector2ic p4)                 { quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y()); }
    
    public void quad(Vector2ic p1, Vector2dc p2, Vector2fc p3, Vector2fc p4)                 { quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y()); }
    
    public void quad(Vector2ic p1, Vector2dc p2, Vector2dc p3, Vector2dc p4)                 { quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y()); }
    
    public void quad(Vector2fc p1, Vector2fc p2, Vector2fc p3, Vector2fc p4)                 { quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y()); }
    
    public void quad(Vector2dc p1, Vector2dc p2, Vector2dc p3, Vector2dc p4)                 { quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y()); }
    
    public void quad(double x1, double y1, Vector2ic p2, Vector2ic p3, Vector2ic p4)         { quad(x1, y1, p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y()); }
    
    public void quad(double x1, double y1, Vector2ic p2, Vector2ic p3, Vector2fc p4)         { quad(x1, y1, p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y()); }
    
    public void quad(double x1, double y1, Vector2ic p2, Vector2ic p3, Vector2dc p4)         { quad(x1, y1, p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y()); }
    
    public void quad(double x1, double y1, Vector2ic p2, Vector2fc p3, Vector2ic p4)         { quad(x1, y1, p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y()); }
    
    public void quad(double x1, double y1, Vector2ic p2, Vector2fc p3, Vector2fc p4)         { quad(x1, y1, p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y()); }
    
    public void quad(double x1, double y1, Vector2ic p2, Vector2fc p3, Vector2dc p4)         { quad(x1, y1, p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y()); }
    
    public void quad(double x1, double y1, Vector2ic p2, Vector2dc p3, Vector2ic p4)         { quad(x1, y1, p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y()); }
    
    public void quad(double x1, double y1, Vector2ic p2, Vector2dc p3, Vector2fc p4)         { quad(x1, y1, p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y()); }
    
    public void quad(double x1, double y1, Vector2ic p2, Vector2dc p3, Vector2dc p4)         { quad(x1, y1, p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y()); }
    
    public void quad(double x1, double y1, Vector2fc p2, Vector2ic p3, Vector2ic p4)         { quad(x1, y1, p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y()); }
    
    public void quad(double x1, double y1, Vector2fc p2, Vector2ic p3, Vector2fc p4)         { quad(x1, y1, p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y()); }
    
    public void quad(double x1, double y1, Vector2fc p2, Vector2ic p3, Vector2dc p4)         { quad(x1, y1, p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y()); }
    
    public void quad(double x1, double y1, Vector2fc p2, Vector2fc p3, Vector2ic p4)         { quad(x1, y1, p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y()); }
    
    public void quad(double x1, double y1, Vector2fc p2, Vector2fc p3, Vector2fc p4)         { quad(x1, y1, p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y()); }
    
    public void quad(double x1, double y1, Vector2fc p2, Vector2fc p3, Vector2dc p4)         { quad(x1, y1, p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y()); }
    
    public void quad(double x1, double y1, Vector2fc p2, Vector2dc p3, Vector2ic p4)         { quad(x1, y1, p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y()); }
    
    public void quad(double x1, double y1, Vector2fc p2, Vector2dc p3, Vector2fc p4)         { quad(x1, y1, p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y()); }
    
    public void quad(double x1, double y1, Vector2fc p2, Vector2dc p3, Vector2dc p4)         { quad(x1, y1, p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y()); }
    
    public void quad(double x1, double y1, Vector2dc p2, Vector2ic p3, Vector2ic p4)         { quad(x1, y1, p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y()); }
    
    public void quad(double x1, double y1, Vector2dc p2, Vector2ic p3, Vector2fc p4)         { quad(x1, y1, p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y()); }
    
    public void quad(double x1, double y1, Vector2dc p2, Vector2ic p3, Vector2dc p4)         { quad(x1, y1, p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y()); }
    
    public void quad(double x1, double y1, Vector2dc p2, Vector2fc p3, Vector2ic p4)         { quad(x1, y1, p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y()); }
    
    public void quad(double x1, double y1, Vector2dc p2, Vector2fc p3, Vector2fc p4)         { quad(x1, y1, p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y()); }
    
    public void quad(double x1, double y1, Vector2dc p2, Vector2fc p3, Vector2dc p4)         { quad(x1, y1, p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y()); }
    
    public void quad(double x1, double y1, Vector2dc p2, Vector2dc p3, Vector2ic p4)         { quad(x1, y1, p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y()); }
    
    public void quad(double x1, double y1, Vector2dc p2, Vector2dc p3, Vector2fc p4)         { quad(x1, y1, p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y()); }
    
    public void quad(double x1, double y1, Vector2dc p2, Vector2dc p3, Vector2dc p4)         { quad(x1, y1, p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y()); }
    
    public void quad(Vector2ic p1, double x2, double y2, Vector2ic p3, Vector2ic p4)         { quad(p1.x(), p1.y(), x2, y2, p3.x(), p3.y(), p4.x(), p4.y()); }
    
    public void quad(Vector2ic p1, double x2, double y2, Vector2ic p3, Vector2fc p4)         { quad(p1.x(), p1.y(), x2, y2, p3.x(), p3.y(), p4.x(), p4.y()); }
    
    public void quad(Vector2ic p1, double x2, double y2, Vector2ic p3, Vector2dc p4)         { quad(p1.x(), p1.y(), x2, y2, p3.x(), p3.y(), p4.x(), p4.y()); }
    
    public void quad(Vector2ic p1, double x2, double y2, Vector2fc p3, Vector2ic p4)         { quad(p1.x(), p1.y(), x2, y2, p3.x(), p3.y(), p4.x(), p4.y()); }
    
    public void quad(Vector2ic p1, double x2, double y2, Vector2fc p3, Vector2fc p4)         { quad(p1.x(), p1.y(), x2, y2, p3.x(), p3.y(), p4.x(), p4.y()); }
    
    public void quad(Vector2ic p1, double x2, double y2, Vector2fc p3, Vector2dc p4)         { quad(p1.x(), p1.y(), x2, y2, p3.x(), p3.y(), p4.x(), p4.y()); }
    
    public void quad(Vector2ic p1, double x2, double y2, Vector2dc p3, Vector2ic p4)         { quad(p1.x(), p1.y(), x2, y2, p3.x(), p3.y(), p4.x(), p4.y()); }
    
    public void quad(Vector2ic p1, double x2, double y2, Vector2dc p3, Vector2fc p4)         { quad(p1.x(), p1.y(), x2, y2, p3.x(), p3.y(), p4.x(), p4.y()); }
    
    public void quad(Vector2ic p1, double x2, double y2, Vector2dc p3, Vector2dc p4)         { quad(p1.x(), p1.y(), x2, y2, p3.x(), p3.y(), p4.x(), p4.y()); }
    
    public void quad(Vector2fc p1, double x2, double y2, Vector2ic p3, Vector2ic p4)         { quad(p1.x(), p1.y(), x2, y2, p3.x(), p3.y(), p4.x(), p4.y()); }
    
    public void quad(Vector2fc p1, double x2, double y2, Vector2ic p3, Vector2fc p4)         { quad(p1.x(), p1.y(), x2, y2, p3.x(), p3.y(), p4.x(), p4.y()); }
    
    public void quad(Vector2fc p1, double x2, double y2, Vector2ic p3, Vector2dc p4)         { quad(p1.x(), p1.y(), x2, y2, p3.x(), p3.y(), p4.x(), p4.y()); }
    
    public void quad(Vector2fc p1, double x2, double y2, Vector2fc p3, Vector2ic p4)         { quad(p1.x(), p1.y(), x2, y2, p3.x(), p3.y(), p4.x(), p4.y()); }
    
    public void quad(Vector2fc p1, double x2, double y2, Vector2fc p3, Vector2fc p4)         { quad(p1.x(), p1.y(), x2, y2, p3.x(), p3.y(), p4.x(), p4.y()); }
    
    public void quad(Vector2fc p1, double x2, double y2, Vector2fc p3, Vector2dc p4)         { quad(p1.x(), p1.y(), x2, y2, p3.x(), p3.y(), p4.x(), p4.y()); }
    
    public void quad(Vector2fc p1, double x2, double y2, Vector2dc p3, Vector2ic p4)         { quad(p1.x(), p1.y(), x2, y2, p3.x(), p3.y(), p4.x(), p4.y()); }
    
    public void quad(Vector2fc p1, double x2, double y2, Vector2dc p3, Vector2fc p4)         { quad(p1.x(), p1.y(), x2, y2, p3.x(), p3.y(), p4.x(), p4.y()); }
    
    public void quad(Vector2fc p1, double x2, double y2, Vector2dc p3, Vector2dc p4)         { quad(p1.x(), p1.y(), x2, y2, p3.x(), p3.y(), p4.x(), p4.y()); }
    
    public void quad(Vector2dc p1, double x2, double y2, Vector2ic p3, Vector2ic p4)         { quad(p1.x(), p1.y(), x2, y2, p3.x(), p3.y(), p4.x(), p4.y()); }
    
    public void quad(Vector2dc p1, double x2, double y2, Vector2ic p3, Vector2fc p4)         { quad(p1.x(), p1.y(), x2, y2, p3.x(), p3.y(), p4.x(), p4.y()); }
    
    public void quad(Vector2dc p1, double x2, double y2, Vector2ic p3, Vector2dc p4)         { quad(p1.x(), p1.y(), x2, y2, p3.x(), p3.y(), p4.x(), p4.y()); }
    
    public void quad(Vector2dc p1, double x2, double y2, Vector2fc p3, Vector2ic p4)         { quad(p1.x(), p1.y(), x2, y2, p3.x(), p3.y(), p4.x(), p4.y()); }
    
    public void quad(Vector2dc p1, double x2, double y2, Vector2fc p3, Vector2fc p4)         { quad(p1.x(), p1.y(), x2, y2, p3.x(), p3.y(), p4.x(), p4.y()); }
    
    public void quad(Vector2dc p1, double x2, double y2, Vector2fc p3, Vector2dc p4)         { quad(p1.x(), p1.y(), x2, y2, p3.x(), p3.y(), p4.x(), p4.y()); }
    
    public void quad(Vector2dc p1, double x2, double y2, Vector2dc p3, Vector2ic p4)         { quad(p1.x(), p1.y(), x2, y2, p3.x(), p3.y(), p4.x(), p4.y()); }
    
    public void quad(Vector2dc p1, double x2, double y2, Vector2dc p3, Vector2fc p4)         { quad(p1.x(), p1.y(), x2, y2, p3.x(), p3.y(), p4.x(), p4.y()); }
    
    public void quad(Vector2dc p1, double x2, double y2, Vector2dc p3, Vector2dc p4)         { quad(p1.x(), p1.y(), x2, y2, p3.x(), p3.y(), p4.x(), p4.y()); }
    
    public void quad(Vector2ic p1, Vector2ic p2, double x3, double y3, Vector2ic p4)         { quad(p1.x(), p1.y(), p2.x(), p2.y(), x3, y3, p4.x(), p4.y()); }
    
    public void quad(Vector2ic p1, Vector2ic p2, double x3, double y3, Vector2fc p4)         { quad(p1.x(), p1.y(), p2.x(), p2.y(), x3, y3, p4.x(), p4.y()); }
    
    public void quad(Vector2ic p1, Vector2ic p2, double x3, double y3, Vector2dc p4)         { quad(p1.x(), p1.y(), p2.x(), p2.y(), x3, y3, p4.x(), p4.y()); }
    
    public void quad(Vector2ic p1, Vector2fc p2, double x3, double y3, Vector2ic p4)         { quad(p1.x(), p1.y(), p2.x(), p2.y(), x3, y3, p4.x(), p4.y()); }
    
    public void quad(Vector2ic p1, Vector2fc p2, double x3, double y3, Vector2fc p4)         { quad(p1.x(), p1.y(), p2.x(), p2.y(), x3, y3, p4.x(), p4.y()); }
    
    public void quad(Vector2ic p1, Vector2fc p2, double x3, double y3, Vector2dc p4)         { quad(p1.x(), p1.y(), p2.x(), p2.y(), x3, y3, p4.x(), p4.y()); }
    
    public void quad(Vector2ic p1, Vector2dc p2, double x3, double y3, Vector2ic p4)         { quad(p1.x(), p1.y(), p2.x(), p2.y(), x3, y3, p4.x(), p4.y()); }
    
    public void quad(Vector2ic p1, Vector2dc p2, double x3, double y3, Vector2fc p4)         { quad(p1.x(), p1.y(), p2.x(), p2.y(), x3, y3, p4.x(), p4.y()); }
    
    public void quad(Vector2ic p1, Vector2dc p2, double x3, double y3, Vector2dc p4)         { quad(p1.x(), p1.y(), p2.x(), p2.y(), x3, y3, p4.x(), p4.y()); }
    
    public void quad(Vector2fc p1, Vector2ic p2, double x3, double y3, Vector2ic p4)         { quad(p1.x(), p1.y(), p2.x(), p2.y(), x3, y3, p4.x(), p4.y()); }
    
    public void quad(Vector2fc p1, Vector2ic p2, double x3, double y3, Vector2fc p4)         { quad(p1.x(), p1.y(), p2.x(), p2.y(), x3, y3, p4.x(), p4.y()); }
    
    public void quad(Vector2fc p1, Vector2ic p2, double x3, double y3, Vector2dc p4)         { quad(p1.x(), p1.y(), p2.x(), p2.y(), x3, y3, p4.x(), p4.y()); }
    
    public void quad(Vector2fc p1, Vector2fc p2, double x3, double y3, Vector2ic p4)         { quad(p1.x(), p1.y(), p2.x(), p2.y(), x3, y3, p4.x(), p4.y()); }
    
    public void quad(Vector2fc p1, Vector2fc p2, double x3, double y3, Vector2fc p4)         { quad(p1.x(), p1.y(), p2.x(), p2.y(), x3, y3, p4.x(), p4.y()); }
    
    public void quad(Vector2fc p1, Vector2fc p2, double x3, double y3, Vector2dc p4)         { quad(p1.x(), p1.y(), p2.x(), p2.y(), x3, y3, p4.x(), p4.y()); }
    
    public void quad(Vector2fc p1, Vector2dc p2, double x3, double y3, Vector2ic p4)         { quad(p1.x(), p1.y(), p2.x(), p2.y(), x3, y3, p4.x(), p4.y()); }
    
    public void quad(Vector2fc p1, Vector2dc p2, double x3, double y3, Vector2fc p4)         { quad(p1.x(), p1.y(), p2.x(), p2.y(), x3, y3, p4.x(), p4.y()); }
    
    public void quad(Vector2fc p1, Vector2dc p2, double x3, double y3, Vector2dc p4)         { quad(p1.x(), p1.y(), p2.x(), p2.y(), x3, y3, p4.x(), p4.y()); }
    
    public void quad(Vector2dc p1, Vector2ic p2, double x3, double y3, Vector2ic p4)         { quad(p1.x(), p1.y(), p2.x(), p2.y(), x3, y3, p4.x(), p4.y()); }
    
    public void quad(Vector2dc p1, Vector2ic p2, double x3, double y3, Vector2fc p4)         { quad(p1.x(), p1.y(), p2.x(), p2.y(), x3, y3, p4.x(), p4.y()); }
    
    public void quad(Vector2dc p1, Vector2ic p2, double x3, double y3, Vector2dc p4)         { quad(p1.x(), p1.y(), p2.x(), p2.y(), x3, y3, p4.x(), p4.y()); }
    
    public void quad(Vector2dc p1, Vector2fc p2, double x3, double y3, Vector2ic p4)         { quad(p1.x(), p1.y(), p2.x(), p2.y(), x3, y3, p4.x(), p4.y()); }
    
    public void quad(Vector2dc p1, Vector2fc p2, double x3, double y3, Vector2fc p4)         { quad(p1.x(), p1.y(), p2.x(), p2.y(), x3, y3, p4.x(), p4.y()); }
    
    public void quad(Vector2dc p1, Vector2fc p2, double x3, double y3, Vector2dc p4)         { quad(p1.x(), p1.y(), p2.x(), p2.y(), x3, y3, p4.x(), p4.y()); }
    
    public void quad(Vector2dc p1, Vector2dc p2, double x3, double y3, Vector2ic p4)         { quad(p1.x(), p1.y(), p2.x(), p2.y(), x3, y3, p4.x(), p4.y()); }
    
    public void quad(Vector2dc p1, Vector2dc p2, double x3, double y3, Vector2fc p4)         { quad(p1.x(), p1.y(), p2.x(), p2.y(), x3, y3, p4.x(), p4.y()); }
    
    public void quad(Vector2dc p1, Vector2dc p2, double x3, double y3, Vector2dc p4)         { quad(p1.x(), p1.y(), p2.x(), p2.y(), x3, y3, p4.x(), p4.y()); }
    
    public void quad(Vector2ic p1, Vector2ic p2, Vector2ic p3, double x4, double y4)         { quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), x4, y4); }
    
    public void quad(Vector2ic p1, Vector2ic p2, Vector2fc p3, double x4, double y4)         { quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), x4, y4); }
    
    public void quad(Vector2ic p1, Vector2ic p2, Vector2dc p3, double x4, double y4)         { quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), x4, y4); }
    
    public void quad(Vector2ic p1, Vector2fc p2, Vector2ic p3, double x4, double y4)         { quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), x4, y4); }
    
    public void quad(Vector2ic p1, Vector2fc p2, Vector2fc p3, double x4, double y4)         { quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), x4, y4); }
    
    public void quad(Vector2ic p1, Vector2fc p2, Vector2dc p3, double x4, double y4)         { quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), x4, y4); }
    
    public void quad(Vector2ic p1, Vector2dc p2, Vector2ic p3, double x4, double y4)         { quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), x4, y4); }
    
    public void quad(Vector2ic p1, Vector2dc p2, Vector2fc p3, double x4, double y4)         { quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), x4, y4); }
    
    public void quad(Vector2ic p1, Vector2dc p2, Vector2dc p3, double x4, double y4)         { quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), x4, y4); }
    
    public void quad(Vector2fc p1, Vector2ic p2, Vector2ic p3, double x4, double y4)         { quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), x4, y4); }
    
    public void quad(Vector2fc p1, Vector2ic p2, Vector2fc p3, double x4, double y4)         { quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), x4, y4); }
    
    public void quad(Vector2fc p1, Vector2ic p2, Vector2dc p3, double x4, double y4)         { quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), x4, y4); }
    
    public void quad(Vector2fc p1, Vector2fc p2, Vector2ic p3, double x4, double y4)         { quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), x4, y4); }
    
    public void quad(Vector2fc p1, Vector2fc p2, Vector2fc p3, double x4, double y4)         { quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), x4, y4); }
    
    public void quad(Vector2fc p1, Vector2fc p2, Vector2dc p3, double x4, double y4)         { quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), x4, y4); }
    
    public void quad(Vector2fc p1, Vector2dc p2, Vector2ic p3, double x4, double y4)         { quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), x4, y4); }
    
    public void quad(Vector2fc p1, Vector2dc p2, Vector2fc p3, double x4, double y4)         { quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), x4, y4); }
    
    public void quad(Vector2fc p1, Vector2dc p2, Vector2dc p3, double x4, double y4)         { quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), x4, y4); }
    
    public void quad(Vector2dc p1, Vector2ic p2, Vector2ic p3, double x4, double y4)         { quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), x4, y4); }
    
    public void quad(Vector2dc p1, Vector2ic p2, Vector2fc p3, double x4, double y4)         { quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), x4, y4); }
    
    public void quad(Vector2dc p1, Vector2ic p2, Vector2dc p3, double x4, double y4)         { quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), x4, y4); }
    
    public void quad(Vector2dc p1, Vector2fc p2, Vector2ic p3, double x4, double y4)         { quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), x4, y4); }
    
    public void quad(Vector2dc p1, Vector2fc p2, Vector2fc p3, double x4, double y4)         { quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), x4, y4); }
    
    public void quad(Vector2dc p1, Vector2fc p2, Vector2dc p3, double x4, double y4)         { quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), x4, y4); }
    
    public void quad(Vector2dc p1, Vector2dc p2, Vector2ic p3, double x4, double y4)         { quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), x4, y4); }
    
    public void quad(Vector2dc p1, Vector2dc p2, Vector2fc p3, double x4, double y4)         { quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), x4, y4); }
    
    public void quad(Vector2dc p1, Vector2dc p2, Vector2dc p3, double x4, double y4)         { quad(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), x4, y4); }
    
    public void quad(double x1, double y1, double x2, double y2, Vector2ic p3, Vector2ic p4) { quad(x1, y1, x2, y2, p3.x(), p3.y(), p4.x(), p4.y()); }
    
    public void quad(double x1, double y1, double x2, double y2, Vector2ic p3, Vector2fc p4) { quad(x1, y1, x2, y2, p3.x(), p3.y(), p4.x(), p4.y()); }
    
    public void quad(double x1, double y1, double x2, double y2, Vector2ic p3, Vector2dc p4) { quad(x1, y1, x2, y2, p3.x(), p3.y(), p4.x(), p4.y()); }
    
    public void quad(double x1, double y1, double x2, double y2, Vector2fc p3, Vector2ic p4) { quad(x1, y1, x2, y2, p3.x(), p3.y(), p4.x(), p4.y()); }
    
    public void quad(double x1, double y1, double x2, double y2, Vector2fc p3, Vector2fc p4) { quad(x1, y1, x2, y2, p3.x(), p3.y(), p4.x(), p4.y()); }
    
    public void quad(double x1, double y1, double x2, double y2, Vector2fc p3, Vector2dc p4) { quad(x1, y1, x2, y2, p3.x(), p3.y(), p4.x(), p4.y()); }
    
    public void quad(double x1, double y1, double x2, double y2, Vector2dc p3, Vector2ic p4) { quad(x1, y1, x2, y2, p3.x(), p3.y(), p4.x(), p4.y()); }
    
    public void quad(double x1, double y1, double x2, double y2, Vector2dc p3, Vector2fc p4) { quad(x1, y1, x2, y2, p3.x(), p3.y(), p4.x(), p4.y()); }
    
    public void quad(double x1, double y1, double x2, double y2, Vector2dc p3, Vector2dc p4) { quad(x1, y1, x2, y2, p3.x(), p3.y(), p4.x(), p4.y()); }
    
    public void quad(double x1, double y1, Vector2ic p2, double x3, double y3, Vector2ic p4) { quad(x1, y1, p2.x(), p2.y(), x3, y3, p4.x(), p4.y()); }
    
    public void quad(double x1, double y1, Vector2ic p2, double x3, double y3, Vector2fc p4) { quad(x1, y1, p2.x(), p2.y(), x3, y3, p4.x(), p4.y()); }
    
    public void quad(double x1, double y1, Vector2ic p2, double x3, double y3, Vector2dc p4) { quad(x1, y1, p2.x(), p2.y(), x3, y3, p4.x(), p4.y()); }
    
    public void quad(double x1, double y1, Vector2fc p2, double x3, double y3, Vector2ic p4) { quad(x1, y1, p2.x(), p2.y(), x3, y3, p4.x(), p4.y()); }
    
    public void quad(double x1, double y1, Vector2fc p2, double x3, double y3, Vector2fc p4) { quad(x1, y1, p2.x(), p2.y(), x3, y3, p4.x(), p4.y()); }
    
    public void quad(double x1, double y1, Vector2fc p2, double x3, double y3, Vector2dc p4) { quad(x1, y1, p2.x(), p2.y(), x3, y3, p4.x(), p4.y()); }
    
    public void quad(double x1, double y1, Vector2dc p2, double x3, double y3, Vector2ic p4) { quad(x1, y1, p2.x(), p2.y(), x3, y3, p4.x(), p4.y()); }
    
    public void quad(double x1, double y1, Vector2dc p2, double x3, double y3, Vector2fc p4) { quad(x1, y1, p2.x(), p2.y(), x3, y3, p4.x(), p4.y()); }
    
    public void quad(double x1, double y1, Vector2dc p2, double x3, double y3, Vector2dc p4) { quad(x1, y1, p2.x(), p2.y(), x3, y3, p4.x(), p4.y()); }
    
    public void quad(double x1, double y1, Vector2ic p2, Vector2ic p3, double x4, double y4) { quad(x1, y1, p2.x(), p2.y(), p3.x(), p3.y(), x4, y4); }
    
    public void quad(double x1, double y1, Vector2ic p2, Vector2fc p3, double x4, double y4) { quad(x1, y1, p2.x(), p2.y(), p3.x(), p3.y(), x4, y4); }
    
    public void quad(double x1, double y1, Vector2ic p2, Vector2dc p3, double x4, double y4) { quad(x1, y1, p2.x(), p2.y(), p3.x(), p3.y(), x4, y4); }
    
    public void quad(double x1, double y1, Vector2fc p2, Vector2ic p3, double x4, double y4) { quad(x1, y1, p2.x(), p2.y(), p3.x(), p3.y(), x4, y4); }
    
    public void quad(double x1, double y1, Vector2fc p2, Vector2fc p3, double x4, double y4) { quad(x1, y1, p2.x(), p2.y(), p3.x(), p3.y(), x4, y4); }
    
    public void quad(double x1, double y1, Vector2fc p2, Vector2dc p3, double x4, double y4) { quad(x1, y1, p2.x(), p2.y(), p3.x(), p3.y(), x4, y4); }
    
    public void quad(double x1, double y1, Vector2dc p2, Vector2ic p3, double x4, double y4) { quad(x1, y1, p2.x(), p2.y(), p3.x(), p3.y(), x4, y4); }
    
    public void quad(double x1, double y1, Vector2dc p2, Vector2fc p3, double x4, double y4) { quad(x1, y1, p2.x(), p2.y(), p3.x(), p3.y(), x4, y4); }
    
    public void quad(double x1, double y1, Vector2dc p2, Vector2dc p3, double x4, double y4) { quad(x1, y1, p2.x(), p2.y(), p3.x(), p3.y(), x4, y4); }
    
    public void quad(Vector2ic p1, double x2, double y2, double x3, double y3, Vector2ic p4) { quad(p1.x(), p1.y(), x2, y2, x3, y3, p4.x(), p4.y()); }
    
    public void quad(Vector2ic p1, double x2, double y2, double x3, double y3, Vector2fc p4) { quad(p1.x(), p1.y(), x2, y2, x3, y3, p4.x(), p4.y()); }
    
    public void quad(Vector2ic p1, double x2, double y2, double x3, double y3, Vector2dc p4) { quad(p1.x(), p1.y(), x2, y2, x3, y3, p4.x(), p4.y()); }
    
    public void quad(Vector2fc p1, double x2, double y2, double x3, double y3, Vector2ic p4) { quad(p1.x(), p1.y(), x2, y2, x3, y3, p4.x(), p4.y()); }
    
    public void quad(Vector2fc p1, double x2, double y2, double x3, double y3, Vector2fc p4) { quad(p1.x(), p1.y(), x2, y2, x3, y3, p4.x(), p4.y()); }
    
    public void quad(Vector2fc p1, double x2, double y2, double x3, double y3, Vector2dc p4) { quad(p1.x(), p1.y(), x2, y2, x3, y3, p4.x(), p4.y()); }
    
    public void quad(Vector2dc p1, double x2, double y2, double x3, double y3, Vector2ic p4) { quad(p1.x(), p1.y(), x2, y2, x3, y3, p4.x(), p4.y()); }
    
    public void quad(Vector2dc p1, double x2, double y2, double x3, double y3, Vector2fc p4) { quad(p1.x(), p1.y(), x2, y2, x3, y3, p4.x(), p4.y()); }
    
    public void quad(Vector2dc p1, double x2, double y2, double x3, double y3, Vector2dc p4) { quad(p1.x(), p1.y(), x2, y2, x3, y3, p4.x(), p4.y()); }
    
    public void quad(Vector2ic p1, double x2, double y2, Vector2ic p3, double x4, double y4) { quad(p1.x(), p1.y(), x2, y2, p3.x(), p3.y(), x4, y4); }
    
    public void quad(Vector2ic p1, double x2, double y2, Vector2fc p3, double x4, double y4) { quad(p1.x(), p1.y(), x2, y2, p3.x(), p3.y(), x4, y4); }
    
    public void quad(Vector2ic p1, double x2, double y2, Vector2dc p3, double x4, double y4) { quad(p1.x(), p1.y(), x2, y2, p3.x(), p3.y(), x4, y4); }
    
    public void quad(Vector2fc p1, double x2, double y2, Vector2ic p3, double x4, double y4) { quad(p1.x(), p1.y(), x2, y2, p3.x(), p3.y(), x4, y4); }
    
    public void quad(Vector2fc p1, double x2, double y2, Vector2fc p3, double x4, double y4) { quad(p1.x(), p1.y(), x2, y2, p3.x(), p3.y(), x4, y4); }
    
    public void quad(Vector2fc p1, double x2, double y2, Vector2dc p3, double x4, double y4) { quad(p1.x(), p1.y(), x2, y2, p3.x(), p3.y(), x4, y4); }
    
    public void quad(Vector2dc p1, double x2, double y2, Vector2ic p3, double x4, double y4) { quad(p1.x(), p1.y(), x2, y2, p3.x(), p3.y(), x4, y4); }
    
    public void quad(Vector2dc p1, double x2, double y2, Vector2fc p3, double x4, double y4) { quad(p1.x(), p1.y(), x2, y2, p3.x(), p3.y(), x4, y4); }
    
    public void quad(Vector2dc p1, double x2, double y2, Vector2dc p3, double x4, double y4) { quad(p1.x(), p1.y(), x2, y2, p3.x(), p3.y(), x4, y4); }
    
    public void quad(Vector2ic p1, Vector2ic p2, double x3, double y3, double x4, double y4) { quad(p1.x(), p1.y(), p2.x(), p2.y(), x3, y3, x4, y4); }
    
    public void quad(Vector2ic p1, Vector2fc p2, double x3, double y3, double x4, double y4) { quad(p1.x(), p1.y(), p2.x(), p2.y(), x3, y3, x4, y4); }
    
    public void quad(Vector2ic p1, Vector2dc p2, double x3, double y3, double x4, double y4) { quad(p1.x(), p1.y(), p2.x(), p2.y(), x3, y3, x4, y4); }
    
    public void quad(Vector2fc p1, Vector2ic p2, double x3, double y3, double x4, double y4) { quad(p1.x(), p1.y(), p2.x(), p2.y(), x3, y3, x4, y4); }
    
    public void quad(Vector2fc p1, Vector2fc p2, double x3, double y3, double x4, double y4) { quad(p1.x(), p1.y(), p2.x(), p2.y(), x3, y3, x4, y4); }
    
    public void quad(Vector2fc p1, Vector2dc p2, double x3, double y3, double x4, double y4) { quad(p1.x(), p1.y(), p2.x(), p2.y(), x3, y3, x4, y4); }
    
    public void quad(Vector2dc p1, Vector2ic p2, double x3, double y3, double x4, double y4) { quad(p1.x(), p1.y(), p2.x(), p2.y(), x3, y3, x4, y4); }
    
    public void quad(Vector2dc p1, Vector2fc p2, double x3, double y3, double x4, double y4) { quad(p1.x(), p1.y(), p2.x(), p2.y(), x3, y3, x4, y4); }
    
    public void quad(Vector2dc p1, Vector2dc p2, double x3, double y3, double x4, double y4) { quad(p1.x(), p1.y(), p2.x(), p2.y(), x3, y3, x4, y4); }
    
    // ---------------------
    // -- Polygon Methods --
    // ---------------------
    
    public abstract void drawPolygon(double[] points);
    
    public abstract void fillPolygon(double[] points);
    
    public void polygon(Object... points)
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
        
        if (this.fill.a() > 0) fillPolygon(newPoints);
        if (this.stroke.a() > 0) drawPolygon(newPoints);
    }
    
    // --------------------
    // -- Circle Methods --
    // --------------------
    
    public abstract void drawCircle(double x, double y, double r);
    
    public abstract void fillCircle(double x, double y, double r);
    
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
    
    public void circle(Vector2ic ab, double c) { circle(ab.x(), ab.y(), c); }
    
    public void circle(Vector2fc ab, double c) { circle(ab.x(), ab.y(), c); }
    
    public void circle(Vector2dc ab, double c) { circle(ab.x(), ab.y(), c); }
    
    // ---------------------
    // -- Ellipse Methods --
    // ---------------------
    
    public abstract void drawEllipse(double x, double y, double rx, double ry);
    
    public abstract void fillEllipse(double x, double y, double rx, double ry);
    
    public void ellipse(double a, double b, double c, double d)
    {
        switch (this.ellipseMode)
        {
            case CENTER:
            default:
                if (this.fill.a() > 0) fillEllipse(a, b, c, d);
                if (this.stroke.a() > 0) drawEllipse(a, b, c, d);
                break;
            case RADIUS:
                if (this.fill.a() > 0) fillEllipse(a, b, c * 2, d * 2);
                if (this.stroke.a() > 0) drawEllipse(a, b, c * 2, d * 2);
                break;
            case CORNER:
                if (this.fill.a() > 0) fillEllipse(a + c * 0.5, b + d * 0.5, c, d);
                if (this.stroke.a() > 0) drawEllipse(a + c * 0.5, b + d * 0.5, c, d);
                break;
            case CORNERS:
                double w = c - a, h = d - b;
                if (this.fill.a() > 0) fillEllipse(a + w * 0.5, b + h * 0.5, w, h);
                if (this.stroke.a() > 0) drawEllipse(a + w * 0.5, b + h * 0.5, w, h);
                break;
        }
    }
    
    public void ellipse(Vector2ic ab, Vector2ic cd)       { ellipse(ab.x(), ab.y(), cd.x(), cd.y()); }
    
    public void ellipse(Vector2ic ab, Vector2fc cd)       { ellipse(ab.x(), ab.y(), cd.x(), cd.y()); }
    
    public void ellipse(Vector2ic ab, Vector2dc cd)       { ellipse(ab.x(), ab.y(), cd.x(), cd.y()); }
    
    public void ellipse(Vector2fc ab, Vector2ic cd)       { ellipse(ab.x(), ab.y(), cd.x(), cd.y()); }
    
    public void ellipse(Vector2fc ab, Vector2fc cd)       { ellipse(ab.x(), ab.y(), cd.x(), cd.y()); }
    
    public void ellipse(Vector2fc ab, Vector2dc cd)       { ellipse(ab.x(), ab.y(), cd.x(), cd.y()); }
    
    public void ellipse(Vector2dc ab, Vector2ic cd)       { ellipse(ab.x(), ab.y(), cd.x(), cd.y()); }
    
    public void ellipse(Vector2dc ab, Vector2fc cd)       { ellipse(ab.x(), ab.y(), cd.x(), cd.y()); }
    
    public void ellipse(Vector2dc ab, Vector2dc cd)       { ellipse(ab.x(), ab.y(), cd.x(), cd.y()); }
    
    public void ellipse(Vector2ic ab, double c, double d) { ellipse(ab.x(), ab.y(), c, d); }
    
    public void ellipse(Vector2fc ab, double c, double d) { ellipse(ab.x(), ab.y(), c, d); }
    
    public void ellipse(Vector2dc ab, double c, double d) { ellipse(ab.x(), ab.y(), c, d); }
    
    public void ellipse(double a, double b, Vector2ic cd) { ellipse(a, b, cd.x(), cd.y()); }
    
    public void ellipse(double a, double b, Vector2fc cd) { ellipse(a, b, cd.x(), cd.y()); }
    
    public void ellipse(double a, double b, Vector2dc cd) { ellipse(a, b, cd.x(), cd.y()); }
}
