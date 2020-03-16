package engine.render;

import engine.color.Color;
import engine.color.Colorc;
import org.joml.*;

import java.lang.Math;
import java.util.Stack;

public class Renderer
{
    private static final Color       DEFAULT_FILL         = new Color(255);
    private static final Color       DEFAULT_STROKE       = new Color(51);
    private static final double      DEFAULT_WEIGHT       = 5;
    private static final RectMode    DEFAULT_RECT_MODE    = RectMode.CORNER;
    private static final EllipseMode DEFAULT_ELLIPSE_MODE = EllipseMode.RADIUS;
    private static final ArcMode     DEFAULT_ARC_MODE     = ArcMode.OPEN;
    private static final double      DEFAULT_TEXT_SIZE    = 8;
    private static final TextAlign   DEFAULT_TEXT_ALIGN   = TextAlign.TOP_LEFT;
    
    protected static final Color    CLEAR  = new Color();
    protected static final Vector4d VECTOR = new Vector4d();
    
    public static Renderer getRenderer(Texture target)
    {
        // TODO - Set renderer then return a new instance here.
        return new Renderer(target);
    }
    
    protected Texture target, previous;
    protected Shader      shader;
    protected VertexArray vertexArray;
    
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
    
    protected final Matrix4d viewMatrix = new Matrix4d();
    
    private static int[] pixels;
    
    protected Renderer(Texture target)
    {
        this.target = target;
    }
    
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
    
    public void translate(double x, double y)                  { this.viewMatrix.translate(x, y, 0); }
    
    public void translate(Vector2ic vector)                    { translate(vector.x(), vector.y()); }
    
    public void translate(Vector2fc vector)                    { translate(vector.x(), vector.y()); }
    
    public void translate(Vector2dc vector)                    { translate(vector.x(), vector.y()); }
    
    public void rotate(double angle)                           { this.viewMatrix.rotate(angle, 0, 0, 1); }
    
    public void scale(double x, double y)                      { this.viewMatrix.scale(x, y, 1); }
    
    public void scale(Vector2ic vector)                        { scale(vector.x(), vector.y()); }
    
    public void scale(Vector2fc vector)                        { scale(vector.x(), vector.y()); }
    
    public void scale(Vector2dc vector)                        { scale(vector.x(), vector.y()); }
    
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
    }
    
    // TODO - Abstract
    public void finish()
    {
        this.target.upload();
    }
    
    // ------------------
    // -- Draw Methods --
    // ------------------
    
    public void clear(Number r, Number g, Number b, Number a) { clear(Renderer.CLEAR.set(r, g, b, a)); }
    
    public void clear(Number r, Number g, Number b)           { clear(Renderer.CLEAR.set(r, g, b)); }
    
    public void clear(Number grey, Number a)                  { clear(Renderer.CLEAR.set(grey, a)); }
    
    public void clear(Number grey)                            { clear(Renderer.CLEAR.set(grey)); }
    
    public void clear()                                       { clear(Color.BACKGROUND_GREY); }
    
    // TODO - Abstract
    public void clear(Colorc clear)
    {
        this.target.clear(clear);
    }
    
    public void point(double x, double y)
    {
        if (this.stroke.a() > 0)
        {
            Renderer.VECTOR.set(x, y, 0, 1);
            this.viewMatrix.transform(Renderer.VECTOR);
            int posX = (int) Renderer.VECTOR.x, posY = (int) Renderer.VECTOR.y;
            
            if (this.weight == 1)
            {
                this.target.setPixel(posX, posY, this.stroke);
            }
            else
            {
                int min = (int) Math.floor(this.weight / 2), max = (int) Math.ceil(this.weight / 2);
                for (int j = -min; j <= max; j++)
                {
                    for (int i = -min; i <= max; i++)
                    {
                        this.target.setPixel(posX + i, posY + j, this.stroke);
                    }
                }
            }
        }
    }
}
