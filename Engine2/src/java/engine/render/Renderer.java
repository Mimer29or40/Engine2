package engine.render;

import engine.color.Blend;
import engine.color.Color;
import engine.color.Colorc;
import engine.util.Logger;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

import static engine.util.Util.getDecimal;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL32.GL_LINES_ADJACENCY;
import static org.lwjgl.opengl.GL43.GL_SHADER_STORAGE_BUFFER;

/**
 * Abstract Renderer to draw things to a texture.
 */
public class Renderer
{
    private static final Logger LOGGER = new Logger();
    
    private static final Blend.BTuple DEFAULT_BLEND        = new Blend.BTuple(new Blend());
    private static final Color        DEFAULT_FILL         = new Color(255);
    private static final Color        DEFAULT_STROKE       = new Color(0);
    private static final Color        DEFAULT_TINT         = new Color(255);
    private static final double       DEFAULT_WEIGHT       = 5;
    private static final RectMode     DEFAULT_RECT_MODE    = RectMode.CORNER;
    private static final EllipseMode  DEFAULT_ELLIPSE_MODE = EllipseMode.CENTER;
    private static final ArcMode      DEFAULT_ARC_MODE     = ArcMode.DEFAULT;
    private static final Font         DEFAULT_FONT         = Font.DEFAULT_FONT;
    private static final TextAlign    DEFAULT_TEXT_ALIGN   = TextAlign.TOP_LEFT;
    
    protected static final Color CLEAR = new Color();
    
    protected boolean debug = false;
    
    protected final Texture defaultTarget;
    protected       Texture target;
    protected final Stack<Texture> targets = new Stack<>();
    
    protected final Blend               blend  = Renderer.DEFAULT_BLEND.setBlend(new Blend());
    protected final Stack<Blend.BTuple> blends = new Stack<>();
    
    protected final Color          fill  = new Color(Renderer.DEFAULT_FILL);
    protected final Stack<Integer> fills = new Stack<>();
    
    protected final Color          stroke  = new Color(Renderer.DEFAULT_STROKE);
    protected final Stack<Integer> strokes = new Stack<>();
    
    protected final Color          tint  = new Color(Renderer.DEFAULT_TINT);
    protected final Stack<Integer> tints = new Stack<>();
    
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
    
    protected final Matrix4f        view  = new Matrix4f();
    protected final Stack<Matrix4f> views = new Stack<>();
    
    protected int[] pixels;
    
    protected boolean drawing = false;
    
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
    
    public Renderer(Texture target)
    {
        this.defaultTarget = this.target = target;
        
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
    
    // ----------------
    // -- Properties --
    // ----------------
    
    /**
     * @return If debug is enabled for the renderer.
     */
    public boolean debug()
    {
        return this.debug;
    }
    
    /**
     * Sets if the renderer is in debug mode.
     *
     * @param enableDebug If debug is enabled.
     */
    public void debug(boolean enableDebug)
    {
        Renderer.LOGGER.finest("Setting Debug State:", enableDebug);
        
        this.debug = enableDebug;
    }
    
    /**
     * Toggles if the renderer is in debug mode.
     */
    public void toggleDebug()
    {
        debug(!this.debug);
    }
    
    /**
     * @return Gets the current render target.
     */
    public Texture target()
    {
        return this.target;
    }
    
    /**
     * Sets the render target of the renderer.
     *
     * @param target The new target.
     */
    public void target(Texture target)
    {
        this.target = target;
        
        identity();
        
        Renderer.LOGGER.finest("Setting Render Target:", target);
    }
    
    /**
     * @return If blend is enabled for the renderer.
     */
    public Blend blend()
    {
        return this.blend;
    }
    
    /**
     * @return The current fill color.
     */
    public Colorc fill()
    {
        return this.fill;
    }
    
    /**
     * Sets the fill color.
     *
     * @param r The red value of the color [0-255] [0.0-1.0]
     * @param g The green value of the color [0-255] [0.0-1.0]
     * @param b The blue value of the color [0-255] [0.0-1.0]
     * @param a The alpha value of the color [0-255] [0.0-1.0]
     */
    public void fill(Number r, Number g, Number b, Number a)
    {
        this.fill.set(r, g, b, a);
        
        Renderer.LOGGER.finest("Setting Fill Color:", this.fill);
    }
    
    /**
     * Sets the fill color.
     *
     * @param r The red value of the color [0-255] [0.0-1.0]
     * @param g The green value of the color [0-255] [0.0-1.0]
     * @param b The blue value of the color [0-255] [0.0-1.0]
     */
    public void fill(Number r, Number g, Number b)
    {
        this.fill.set(r, g, b);
        
        Renderer.LOGGER.finest("Setting Fill Color:", this.fill);
    }
    
    /**
     * Sets the fill color.
     *
     * @param grey The red, green and blue value of the color [0-255] [0.0-1.0]
     * @param a    The alpha value of the color [0-255] [0.0-1.0]
     */
    public void fill(Number grey, Number a)
    {
        this.fill.set(grey, a);
        
        Renderer.LOGGER.finest("Setting Fill Color:", this.fill);
    }
    
    /**
     * Sets the fill color.
     *
     * @param grey The red, green and blue value of the color [0-255] [0.0-1.0]
     */
    public void fill(Number grey)
    {
        this.fill.set(grey);
        
        Renderer.LOGGER.finest("Setting Fill Color:", this.fill);
    }
    
    /**
     * Sets the fill color.
     *
     * @param fill The color to set fill to.
     */
    public void fill(Colorc fill)
    {
        this.fill.set(fill);
        
        Renderer.LOGGER.finest("Setting Fill Color:", this.fill);
    }
    
    /**
     * Disabled the fill of shapes
     */
    public void noFill()
    {
        this.fill.a(0);
        
        Renderer.LOGGER.finest("Setting No Fill");
    }
    
    /**
     * @return The current stroke color.
     */
    public Colorc stroke()
    {
        return this.stroke;
    }
    
    /**
     * Sets the stroke color.
     *
     * @param r The red value of the color [0-255] [0.0-1.0]
     * @param g The green value of the color [0-255] [0.0-1.0]
     * @param b The blue value of the color [0-255] [0.0-1.0]
     * @param a The alpha value of the color [0-255] [0.0-1.0]
     */
    public void stroke(Number r, Number g, Number b, Number a)
    {
        this.stroke.set(r, g, b, a);
        
        Renderer.LOGGER.finest("Setting Stroke Color:", this.stroke);
    }
    
    /**
     * Sets the stroke color.
     *
     * @param r The red value of the color [0-255] [0.0-1.0]
     * @param g The green value of the color [0-255] [0.0-1.0]
     * @param b The blue value of the color [0-255] [0.0-1.0]
     */
    public void stroke(Number r, Number g, Number b)
    {
        this.stroke.set(r, g, b);
        
        Renderer.LOGGER.finest("Setting Stroke Color:", this.stroke);
    }
    
    /**
     * Sets the stroke color.
     *
     * @param grey The red, green and blue value of the color [0-255] [0.0-1.0]
     * @param a    The alpha value of the color [0-255] [0.0-1.0]
     */
    public void stroke(Number grey, Number a)
    {
        this.stroke.set(grey, a);
        
        Renderer.LOGGER.finest("Setting Stroke Color:", this.stroke);
    }
    
    /**
     * Sets the stroke color.
     *
     * @param grey The red, green and blue value of the color [0-255] [0.0-1.0]
     */
    public void stroke(Number grey)
    {
        this.stroke.set(grey);
        
        Renderer.LOGGER.finest("Setting Stroke Color:", this.stroke);
    }
    
    /**
     * Sets the stroke color.
     *
     * @param stroke The color to set fill to.
     */
    public void stroke(Colorc stroke)
    {
        this.stroke.set(stroke);
        
        Renderer.LOGGER.finest("Setting Stroke Color:", this.stroke);
    }
    
    /**
     * Disabled the stroke of shapes
     */
    public void noStroke()
    {
        this.stroke.a(0);
        
        Renderer.LOGGER.finest("Setting No Stroke");
    }
    
    /**
     * @return The current tint color.
     */
    public Colorc tint()
    {
        return this.tint;
    }
    
    /**
     * Sets the tint color.
     *
     * @param r The red value of the color [0-255] [0.0-1.0]
     * @param g The green value of the color [0-255] [0.0-1.0]
     * @param b The blue value of the color [0-255] [0.0-1.0]
     * @param a The alpha value of the color [0-255] [0.0-1.0]
     */
    public void tint(Number r, Number g, Number b, Number a)
    {
        this.tint.set(r, g, b, a);
        
        Renderer.LOGGER.finest("Setting Tint Color:", this.tint);
    }
    
    /**
     * Sets the tint color.
     *
     * @param r The red value of the color [0-255] [0.0-1.0]
     * @param g The green value of the color [0-255] [0.0-1.0]
     * @param b The blue value of the color [0-255] [0.0-1.0]
     */
    public void tint(Number r, Number g, Number b)
    {
        this.tint.set(r, g, b);
        
        Renderer.LOGGER.finest("Setting Tint Color:", this.tint);
    }
    
    /**
     * Sets the tint color.
     *
     * @param grey The red, green and blue value of the color [0-255] [0.0-1.0]
     * @param a    The alpha value of the color [0-255] [0.0-1.0]
     */
    public void tint(Number grey, Number a)
    {
        this.tint.set(grey, a);
        
        Renderer.LOGGER.finest("Setting Tint Color:", this.tint);
    }
    
    /**
     * Sets the tint color.
     *
     * @param grey The red, green and blue value of the color [0-255] [0.0-1.0]
     */
    public void tint(Number grey)
    {
        this.tint.set(grey);
        
        Renderer.LOGGER.finest("Setting Tint Color:", this.tint);
    }
    
    /**
     * Sets the tint color.
     *
     * @param tint The color to set fill to.
     */
    public void tint(Colorc tint)
    {
        this.tint.set(tint);
        
        Renderer.LOGGER.finest("Setting Tint Color:", this.tint);
    }
    
    /**
     * Disabled the stroke of shapes
     */
    public void noTint()
    {
        this.tint.set(255, 255);
        
        Renderer.LOGGER.finest("Setting No Tint");
    }
    
    /**
     * @return The stroke weight in pixels.
     */
    public double weight()
    {
        return this.weight;
    }
    
    /**
     * Sets the stroke weight.
     *
     * @param weight The new stroke weight in pixels. [1..Double.MAX_VALUE]
     */
    public void weight(double weight)
    {
        if (weight < 1) noStroke();
        this.weight = Math.max(1, weight);
        
        Renderer.LOGGER.finest("Setting Stroke Weight:", this.weight);
    }
    
    /**
     * @return The current {@link RectMode}
     */
    public RectMode rectMode()
    {
        return this.rectMode;
    }
    
    /**
     * Sets the {@link RectMode} option.
     * <p>
     * See {@link RectMode} for how the points get transformed.
     *
     * @param rectMode The new {@link RectMode} option.
     */
    public void rectMode(RectMode rectMode)
    {
        Renderer.LOGGER.finest("Setting RectMode:", rectMode);
        
        this.rectMode = rectMode;
    }
    
    /**
     * @return The current {@link EllipseMode}
     */
    public EllipseMode ellipseMode()
    {
        return this.ellipseMode;
    }
    
    /**
     * Sets the {@link EllipseMode} option.
     * <p>
     * See {@link EllipseMode} for how the points get transformed.
     *
     * @param ellipseMode The new {@link EllipseMode} option.
     */
    public void ellipseMode(EllipseMode ellipseMode)
    {
        Renderer.LOGGER.finest("Setting EllipseMode:", ellipseMode);
        
        this.ellipseMode = ellipseMode;
    }
    
    /**
     * @return The current {@link ArcMode}
     */
    public ArcMode arcMode()
    {
        return this.arcMode;
    }
    
    /**
     * Sets the {@link ArcMode} option.
     * <p>
     * See {@link ArcMode} for how the points get transformed.
     *
     * @param arcMode The new {@link ArcMode} option.
     */
    public void arcMode(ArcMode arcMode)
    {
        Renderer.LOGGER.finest("Setting ArcMode:", arcMode);
        
        this.arcMode = arcMode;
    }
    
    /**
     * @return The current Font
     */
    public Font textFont()
    {
        return this.font;
    }
    
    /**
     * Sets the current Font.
     *
     * @param font The new font.
     */
    public void textFont(Font font)
    {
        this.font = font;
    }
    
    /**
     * Creates and sets the current Font.
     *
     * @param font The path to the ttf file.
     */
    public void textFont(String font)
    {
        this.font = Font.getFont(font);
        
        Renderer.LOGGER.finest("Setting Font:", this.font);
    }
    
    /**
     * Creates and sets the current Font.
     *
     * @param font The path to the ttf file.
     * @param size The size of the font in pixels. [4::Integer.MAX_VALUE]
     */
    public void textFont(String font, int size)
    {
        this.font = Font.getFont(font, size);
        
        Renderer.LOGGER.finest("Setting Font:", this.font);
    }
    
    /**
     * @return The size of the current font in pixels.
     */
    public int textSize()
    {
        return this.font.size();
    }
    
    /**
     * Sets the size of the current Font.
     *
     * @param textSize The new size in pixels [4::Integer.MAX_VALUE]
     */
    public void textSize(int textSize)
    {
        Renderer.LOGGER.finest("Setting Font Size:", textSize);
        
        this.font = Font.getFont(textSize);
    }
    
    /**
     * @return The size in pixels of the current Font's ascent.
     */
    public double textAscent()
    {
        return this.font.ascent();
    }
    
    /**
     * @return The size in pixels of the current Font's descent.
     */
    public double textDescent()
    {
        return this.font.descent();
    }
    
    /**
     * @return The current {@link TextAlign} value.
     */
    public TextAlign textAlign()
    {
        return this.textAlign;
    }
    
    /**
     * Sets the {@link TextAlign} option.
     * <p>
     * See {@link #text} for details on what each option does.
     *
     * @param textAlign The new {@link TextAlign} option.
     */
    public void textAlign(TextAlign textAlign)
    {
        Renderer.LOGGER.finest("Setting Text Align:", textAlign);
        
        this.textAlign = textAlign;
    }
    
    // ----------------------------
    // -- Transformation Methods --
    // ----------------------------
    
    /**
     * Resets the view space transformations.
     */
    public void identity()
    {
        Renderer.LOGGER.finest("Resetting View");
        
        this.view.setOrtho(0F, this.target.width(), 0F, this.target.height(), -1F, 1F);
    }
    
    /**
     * Translates the view space.
     *
     * @param x The amount to translate horizontally.
     * @param y The amount to translate vertically.
     */
    public void translate(double x, double y)
    {
        Renderer.LOGGER.finest("Translating View:", x, y);
        
        this.view.translate((float) x, (float) y, 0);
    }
    
    /**
     * Rotates the view space.
     *
     * @param angle The angle in radian to rotate by.
     */
    public void rotate(double angle)
    {
        Renderer.LOGGER.finest("Rotating View:", angle);
        
        this.view.rotate((float) angle, 0, 0, 1);
    }
    
    /**
     * Scales the view space.
     *
     * @param x The amount to scale horizontally.
     * @param y The amount to scale vertically.
     */
    public void scale(double x, double y)
    {
        Renderer.LOGGER.finest("Scaling View:", x, y);
        
        this.view.scale((float) x, (float) y, 1);
    }
    
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
        Renderer.LOGGER.finer("Begin Rendering");
        
        if (this.drawing) throw new RuntimeException("Renderer was never finished");
        
        this.drawing = true;
        
        this.target = this.defaultTarget;
        this.targets.clear();
        
        Renderer.DEFAULT_BLEND.setBlend(this.blend);
        this.blends.clear();
        
        this.fill.set(Renderer.DEFAULT_FILL);
        this.fills.clear();
        
        this.stroke.set(Renderer.DEFAULT_STROKE);
        this.strokes.clear();
        
        this.tint.set(Renderer.DEFAULT_TINT);
        this.tints.clear();
        
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
        
        identity();
        this.views.clear();
        
        this.target.bindFramebuffer();
        
        if (this.debug) glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
    }
    
    /**
     * Finishes the render.
     * <p>
     * This must be called after {@link #start}
     */
    public void finish()
    {
        Renderer.LOGGER.finer("Rendering Finished");
        
        if (!this.drawing) throw new RuntimeException("Renderer was never started");
        
        this.drawing = false;
        
        this.target.unbindFramebuffer();
        
        glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
    }
    
    /**
     * Creates a known state of the renderers properties that can be returned to by calling {@link #pop}.
     */
    public void push()
    {
        Renderer.LOGGER.finer("Pushing Renderer State");
        
        this.targets.push(this.target);
        this.blends.push(new Blend.BTuple(this.blend));
        this.fills.push(this.fill.toInt());
        this.strokes.push(this.stroke.toInt());
        this.tints.push(this.tint.toInt());
        this.weights.push(this.weight);
        this.rectModes.push(this.rectMode);
        this.ellipseModes.push(this.ellipseMode);
        this.arcModes.push(this.arcMode);
        this.fonts.push(this.font);
        this.textAligns.push(this.textAlign);
        this.views.push(new Matrix4f(this.view));
    }
    
    /**
     * Returns the renderers properties to the state when {@link #push} was called.
     */
    public void pop()
    {
        Renderer.LOGGER.finer("Popping Renderer State");
        
        this.target = this.targets.pop();
        this.blends.pop().setBlend(this.blend);
        this.fill.fromInt(this.fills.pop());
        this.stroke.fromInt(this.strokes.pop());
        this.tint.fromInt(this.tints.pop());
        this.weight      = this.weights.pop();
        this.rectMode    = this.rectModes.pop();
        this.ellipseMode = this.ellipseModes.pop();
        this.arcMode     = this.arcModes.pop();
        this.font        = this.fonts.pop();
        this.textAlign   = this.textAligns.pop();
        this.view.set(this.views.pop());
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
    public void clear(Number r, Number g, Number b, Number a)
    {
        clear(Renderer.CLEAR.set(r, g, b, a));
    }
    
    /**
     * Clears the render target to the color provided.
     *
     * @param r The red value to clear the target to. [0-255] [0.0-1.0]
     * @param g The green value to clear the target to. [0-255] [0.0-1.0]
     * @param b The blue value to clear the target to. [0-255] [0.0-1.0]
     */
    public void clear(Number r, Number g, Number b)
    {
        clear(Renderer.CLEAR.set(r, g, b));
    }
    
    /**
     * Clears the render target to the color provided.
     *
     * @param grey The red, green and blue value to clear the target to. [0-255] [0.0-1.0]
     * @param a    The alpha value to clear the target to. [0-255] [0.0-1.0]
     */
    public void clear(Number grey, Number a)
    {
        clear(Renderer.CLEAR.set(grey, a));
    }
    
    /**
     * Clears the render target to the color provided.
     *
     * @param grey The red, green and blue value to clear the target to. [0-255] [0.0-1.0]
     */
    public void clear(Number grey)
    {
        clear(Renderer.CLEAR.set(grey));
    }
    
    /**
     * Clears the render target to the r: 51, g: 51, b: 51, a: 255
     */
    public void clear()
    {
        clear(Color.BACKGROUND_GREY);
    }
    
    /**
     * Clears the render target to the color provided.
     *
     * @param color The color to set the target to.
     */
    public void clear(Colorc color)
    {
        Renderer.LOGGER.finest("Clearing Render Target to", color);
        
        this.target.bindFramebuffer();
        
        glClearColor(color.rf(), color.gf(), color.bf(), color.af());
        glClear(GL_COLOR_BUFFER_BIT);
    }
    
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
    public void drawPoint(double x, double y)
    {
        Renderer.LOGGER.finer("Drawing Point:", x, y);
        
        this.target.bindFramebuffer();
        
        this.pointShader.bind();
        this.pointShader.setMat4("pv", this.view);
        this.pointShader.setColor("color", this.stroke);
        this.pointShader.setColor("tint", this.tint);
        this.pointShader.setVec2("viewport", this.target.width(), this.target.height());
        this.pointShader.setUniform("thickness", (float) this.weight);
        
        this.pointVAO.bind().getBuffer(0).bind().set(new float[] {(float) x, (float) y}, GL_DYNAMIC_DRAW).unbind();
        this.pointVAO.draw(GL_POINTS).unbind();
        
        this.target.markGPUDirty();
    }
    
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
    public void drawLine(double x1, double y1, double x2, double y2)
    {
        Renderer.LOGGER.finer("Drawing Line:", x1, y1, x2, y2);
        
        this.target.bindFramebuffer();
        
        this.lineShader.bind();
        this.lineShader.setMat4("pv", this.view);
        this.lineShader.setColor("color", this.stroke);
        this.lineShader.setColor("tint", this.tint);
        this.lineShader.setVec2("viewport", this.target.width(), this.target.height());
        this.lineShader.setUniform("thickness", (float) this.weight);
        
        this.lineVAO.bind().getBuffer(0).bind().set(new float[] {(float) x1, (float) y1, (float) x2, (float) y2}, GL_DYNAMIC_DRAW).unbind();
        this.lineVAO.draw(GL_LINES).unbind();
        
        this.target.markGPUDirty();
    }
    
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
    public void drawBezier(double x1, double y1, double x2, double y2, double x3, double y3)
    {
        Renderer.LOGGER.finer("Drawing Bezier:", x1, y1, x2, y2, x3, y3);
        
        // TODO
        drawLine(x1, y1, x2, y2);
        drawLine(x2, y2, x3, y3);
    }
    
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
    public void drawTriangle(double x1, double y1, double x2, double y2, double x3, double y3)
    {
        Renderer.LOGGER.finer("Drawing Triangle:", x1, y1, x2, y2, x3, y3);
        
        this.target.bindFramebuffer();
        
        this.linesShader.bind();
        this.linesShader.setMat4("pv", this.view);
        this.linesShader.setColor("color", this.stroke);
        this.linesShader.setColor("tint", this.tint);
        this.linesShader.setVec2("viewport", this.target.width(), this.target.height());
        this.linesShader.setUniform("thickness", (float) this.weight);
        
        this.triangleLinesVAO.bind().getBuffer(0).bind().set(new float[] {
                (float) x3, (float) y3, (float) x1, (float) y1, (float) x2, (float) y2, (float) x3, (float) y3,
                (float) x1, (float) y1, (float) x2, (float) y2, (float) x3, (float) y3, (float) x1, (float) y1,
                (float) x2, (float) y2, (float) x3, (float) y3, (float) x1, (float) y1, (float) x2, (float) y2
        }, GL_DYNAMIC_DRAW).unbind();
        this.triangleLinesVAO.draw(GL_LINES_ADJACENCY).unbind();
        
        this.target.markGPUDirty();
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
    public void fillTriangle(double x1, double y1, double x2, double y2, double x3, double y3)
    {
        Renderer.LOGGER.finer("Filling Triangle:", x1, y1, x2, y2, x3, y3);
        
        this.target.bindFramebuffer();
        
        this.triangleShader.bind();
        this.triangleShader.setMat4("pv", this.view);
        this.triangleShader.setColor("color", this.fill);
        this.triangleShader.setColor("tint", this.tint);
        
        this.triangleVAO.bind().getBuffer(0).bind().set(new float[] {(float) x1, (float) y1, (float) x2, (float) y2, (float) x3, (float) y3}, GL_DYNAMIC_DRAW).unbind();
        this.triangleVAO.draw(GL_TRIANGLES).unbind();
        
        this.target.markGPUDirty();
    }
    
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
    public void fillSquare(double x, double y, double w)
    {
        fillQuad(x, y, x + w, y, x + w, y + w, x, y + w);
    }
    
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
    public void fillRect(double x, double y, double w, double h)
    {
        fillQuad(x, y, x + w, y, x + w, y + h, x, y + h);
    }
    
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
                if (this.fill.a() > 0) fillRect(a, b, c - a + 1, d - b + 1);
                if (this.stroke.a() > 0) drawRect(a, b, c - a + 1, d - b + 1);
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
    public void drawQuad(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4)
    {
        Renderer.LOGGER.finer("Drawing Quad:", x1, y1, x2, y2, x3, y3, x4, y4);
        
        this.target.bindFramebuffer();
        
        this.linesShader.bind();
        this.linesShader.setMat4("pv", this.view);
        this.linesShader.setColor("color", this.stroke);
        this.linesShader.setColor("tint", this.tint);
        this.linesShader.setVec2("viewport", this.target.width(), this.target.height());
        this.linesShader.setUniform("thickness", (float) this.weight);
        
        this.quadLinesVAO.bind().getBuffer(0).bind().set(new float[] {
                (float) x4, (float) y4, (float) x1, (float) y1, (float) x2, (float) y2, (float) x3, (float) y3,
                (float) x1, (float) y1, (float) x2, (float) y2, (float) x3, (float) y3, (float) x4, (float) y4,
                (float) x2, (float) y2, (float) x3, (float) y3, (float) x4, (float) y4, (float) x1, (float) y1,
                (float) x3, (float) y3, (float) x4, (float) y4, (float) x1, (float) y1, (float) x2, (float) y2
        }, GL_DYNAMIC_DRAW).unbind();
        this.quadLinesVAO.draw(GL_LINES_ADJACENCY).unbind();
        
        this.target.markGPUDirty();
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
    public void fillQuad(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4)
    {
        Renderer.LOGGER.finer("Filling Quad:", x1, y1, x2, y2, x3, y3, x4, y4);
        
        this.target.bindFramebuffer();
        
        this.quadShader.bind();
        this.quadShader.setMat4("pv", this.view);
        this.quadShader.setColor("color", this.fill);
        this.quadShader.setColor("tint", this.tint);
        
        this.quadVAO.bind().getBuffer(0).bind().set(new float[] {(float) x1, (float) y1, (float) x2, (float) y2, (float) x3, (float) y3, (float) x4, (float) y4}, GL_DYNAMIC_DRAW).unbind();
        this.quadVAO.draw(GL_QUADS).unbind();
        
        this.target.markGPUDirty();
    }
    
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
    public void drawPolygon(double... points)
    {
        Renderer.LOGGER.finer("Drawing Polygon:", Arrays.toString(points));
        
        this.target.bindFramebuffer();
        
        this.linesShader.bind();
        this.linesShader.setMat4("pv", this.view);
        this.linesShader.setColor("color", this.stroke);
        this.linesShader.setColor("tint", this.tint);
        this.linesShader.setVec2("viewport", this.target.width(), this.target.height());
        this.linesShader.setUniform("thickness", (float) this.weight);
        
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
        
        this.target.markGPUDirty();
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
    public void fillPolygon(double... points)
    {
        Renderer.LOGGER.finer("Filling Polygon:", Arrays.toString(points));
        
        this.target.bindFramebuffer();
        
        this.polygonShader.bind();
        this.polygonShader.setMat4("pv", this.view);
        this.polygonShader.setColor("color", this.fill);
        this.polygonShader.setColor("tint", this.tint);
        
        float[] array = new float[points.length];
        for (int i = 0, n = points.length; i < n; i++) array[i] = (float) points[i];
        this.polygonSSBO.bind().set(array, GL_STATIC_DRAW).unbind();
        
        this.polygonVAO.bind().draw(GL_POINTS).unbind();
        
        this.target.markGPUDirty();
    }
    
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
    public void fillCircle(double x, double y, double r)
    {
        fillEllipse(x, y, r, r);
    }
    
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
    public void drawEllipse(double x, double y, double rx, double ry)
    {
        Renderer.LOGGER.finer("Drawing Ellipse:", x, y, rx, ry);
        
        this.target.bindFramebuffer();
        
        this.ellipseOutlineShader.bind();
        this.ellipseOutlineShader.setMat4("pv", this.view);
        this.ellipseOutlineShader.setColor("color", this.stroke);
        this.ellipseOutlineShader.setColor("tint", this.tint);
        this.ellipseOutlineShader.setVec2("radius", (float) rx, (float) ry);
        this.ellipseOutlineShader.setVec2("viewport", this.target.width(), this.target.height());
        this.ellipseOutlineShader.setUniform("thickness", (float) this.weight);
        
        this.ellipseOutlineVAO.bind().getBuffer(0).bind().set(new float[] {(float) x, (float) y}, GL_DYNAMIC_DRAW).unbind();
        this.ellipseOutlineVAO.draw(GL_POINTS).unbind();
        
        this.target.markGPUDirty();
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
    public void fillEllipse(double x, double y, double rx, double ry)
    {
        Renderer.LOGGER.finer("Filling Ellipse:", x, y, rx, ry);
        
        this.target.bindFramebuffer();
        
        this.ellipseShader.bind();
        this.ellipseShader.setMat4("pv", this.view);
        this.ellipseShader.setColor("color", this.fill);
        this.ellipseShader.setColor("tint", this.tint);
        this.ellipseShader.setVec2("radius", (float) rx, (float) ry);
        
        this.ellipseVAO.bind().getBuffer(0).bind().set(new float[] {(float) x, (float) y}, GL_DYNAMIC_DRAW).unbind();
        this.ellipseVAO.draw(GL_POINTS).unbind();
        
        this.target.markGPUDirty();
    }
    
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
    public void drawArc(double x, double y, double rx, double ry, double start, double stop)
    {
        Renderer.LOGGER.finer("Drawing Arc:", x, y, rx, ry, start, stop);
    
        this.target.bindFramebuffer();
        
        this.arcOutlineShader.bind();
        this.arcOutlineShader.setMat4("pv", this.view);
        this.arcOutlineShader.setColor("color", this.stroke);
        this.arcOutlineShader.setColor("tint", this.tint);
        this.arcOutlineShader.setVec2("radius", (float) rx, (float) ry);
        this.arcOutlineShader.setVec2("viewport", this.target.width(), this.target.height());
        this.arcOutlineShader.setUniform("thickness", (float) this.weight);
        this.arcOutlineShader.setVec2("bounds", (float) start, (float) stop);
        this.arcOutlineShader.setUniform("mode", this.arcMode.ordinal());
        
        this.arcOutlineVAO.bind().getBuffer(0).bind().set(new float[] {(float) x, (float) y}, GL_DYNAMIC_DRAW).unbind();
        this.arcOutlineVAO.draw(GL_POINTS).unbind();
        
        this.target.markGPUDirty();
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
    public void fillArc(double x, double y, double rx, double ry, double start, double stop)
    {
        Renderer.LOGGER.finer("Filling Arc:", x, y, rx, ry, start, stop);
    
        this.target.bindFramebuffer();
        
        this.arcShader.bind();
        this.arcShader.setMat4("pv", this.view);
        this.arcShader.setColor("color", this.fill);
        this.arcShader.setColor("tint", this.tint);
        this.arcShader.setVec2("radius", (float) rx, (float) ry);
        this.arcShader.setVec2("bounds", (float) start, (float) stop);
        this.arcShader.setUniform("mode", this.arcMode.ordinal());
        
        this.arcVAO.bind().getBuffer(0).bind().set(new float[] {(float) x, (float) y}, GL_DYNAMIC_DRAW).unbind();
        this.arcVAO.draw(GL_POINTS).unbind();
        
        this.target.markGPUDirty();
    }
    
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
        double TWO_PI = 2 * Math.PI;
        
        if (start < 0) start = getDecimal(start / TWO_PI) * TWO_PI;
        if (start >= TWO_PI) start = getDecimal(start / TWO_PI) * TWO_PI;
        if (stop < 0) stop = getDecimal(stop / TWO_PI) * TWO_PI;
        if (stop >= TWO_PI) stop = getDecimal(stop / TWO_PI) * TWO_PI;
        
        if (start > stop)
        {
            double temp = start;
            start = stop;
            stop  = temp;
        }
        
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
     * Draws a textured rectangle whose top left corner is at {@code (x1, y1)} and is {@code x2-x1} pixels wide and {@code y2-y1} tall.
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
    public void drawTexture(Texture texture, double x1, double y1, double x2, double y2, double u1, double v1, double u2, double v2)
    {
        Renderer.LOGGER.finer("Drawing Texture:", x1, y1, x2, y2, u1, v1, u2, v2);
    
        this.target.bindFramebuffer();
        
        glActiveTexture(GL_TEXTURE0);
        texture.bindTexture();
        
        this.textureShader.bind();
        this.textureShader.setMat4("pv", this.view);
        this.textureShader.setColor("tint", this.tint);
        this.textureShader.setUniform("interpolate", -1f);
        this.textureShader.setUniform("tex1", 0);
        this.textureShader.setUniform("tex2", 0);
        
        this.textureVAO.bind().getBuffer(0).bind().set(new float[] {
                (float) x1, (float) y1, (float) u1, (float) v1,
                (float) x1, (float) y2, (float) u1, (float) v2,
                (float) x2, (float) y2, (float) u2, (float) v2,
                (float) x2, (float) y1, (float) u2, (float) v1
        }, GL_DYNAMIC_DRAW).unbind();
        this.textureVAO.draw(GL_QUADS).unbind();
        
        this.target.markGPUDirty();
    }
    
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
     * @param u1      The top left corner x texture coordinate of the rectangle.
     * @param v1      The top left corner y texture coordinate of the rectangle.
     * @param u2      The bottom right corner u texture coordinate of the rectangle.
     * @param v2      The bottom right corner v texture coordinate of the rectangle.
     */
    public void texture(Texture texture, double a, double b, double c, double d, double u1, double v1, double v2, double u2)
    {
        switch (this.rectMode)
        {
            case CORNER:
            default:
                drawTexture(texture, a, b, a + c, b + d, u1, v1, u2, v2);
                break;
            case CORNERS:
                drawTexture(texture, a, b, c, d, u1, v1, u2, v2);
                break;
            case CENTER:
                drawTexture(texture, a - c * 0.5, b - d * 0.5, a + c * 0.5, b + d * 0.5, u1, v1, u2, v2);
                break;
            case RADIUS:
                drawTexture(texture, a - c, b - d, a + c, b + d, u1, v1, u2, v2);
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
     * @param u1      The top left corner x texture coordinate of the rectangle.
     * @param v1      The top left corner y texture coordinate of the rectangle.
     * @param u2      The bottom right corner u texture coordinate of the rectangle.
     * @param v2      The bottom right corner v texture coordinate of the rectangle.
     */
    public void texture(Texture texture, double x, double y, double u1, double v1, double v2, double u2)
    {
        drawTexture(texture, x, y, x + texture.width(), y + texture.height(), u1, v1, u2, v2);
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
        drawTexture(texture, x, y, x + texture.width(), y + texture.height(), 0, 0, 1, 1);
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
    public void drawInterpolatedTexture(Texture texture1, Texture texture2, double amount, double x1, double y1, double x2, double y2, double u1, double v1, double u2, double v2)
    {
        Renderer.LOGGER.finer("Drawing Interpolated Texture:", amount, x1, y1, x2, y2, u1, v1, u2, v2);
    
        this.target.bindFramebuffer();
        
        glActiveTexture(GL_TEXTURE1);
        texture2.bindTexture();
        
        glActiveTexture(GL_TEXTURE0);
        texture1.bindTexture();
        
        this.textureShader.bind();
        this.textureShader.setMat4("pv", this.view);
        this.textureShader.setColor("tint", this.tint);
        this.textureShader.setUniform("interpolate", (float) amount);
        this.textureShader.setUniform("tex1", 0);
        this.textureShader.setUniform("tex2", 1);
        
        this.textureVAO.bind().getBuffer(0).bind().set(new float[] {
                (float) x1, (float) y1, (float) u1, (float) v1,
                (float) x1, (float) y2, (float) u1, (float) v2,
                (float) x2, (float) y2, (float) u2, (float) v2,
                (float) x2, (float) y1, (float) u2, (float) v1
        }, GL_DYNAMIC_DRAW).unbind();
        this.textureVAO.draw(GL_QUADS).unbind();
        
        this.target.markGPUDirty();
    }
    
    /**
     * Draws an interpolated textured rectangle based on {@link #rectMode()}, with uv coordinates.
     * <p>
     * See {@link RectMode} for how the points get transformed.
     * <p>
     * You can specify the coordinate of the texture to pull from.
     * <p>
     * The coordinates passed in will be transformed by the view matrix
     *
     * @param texture1 The first texture to draw.
     * @param texture2 The second texture to draw.
     * @param amount   The amount to interpolate.
     * @param a        The a value.
     * @param b        The b value.
     * @param c        The c value.
     * @param d        The d value.
     * @param u1       The top left corner x texture coordinate of the rectangle.
     * @param v1       The top left corner y texture coordinate of the rectangle.
     * @param u2       The bottom right corner u texture coordinate of the rectangle.
     * @param v2       The bottom right corner v texture coordinate of the rectangle.
     */
    public void interpolateTexture(Texture texture1, Texture texture2, double amount, double a, double b, double c, double d, double u1, double v1, double v2, double u2)
    {
        switch (this.rectMode)
        {
            case CORNER:
            default:
                drawInterpolatedTexture(texture1, texture2, amount, a, b, c - a, d - b, u1, v1, u2, v2);
                break;
            case CORNERS:
                drawInterpolatedTexture(texture1, texture2, amount, a, b, c, d, u1, v1, u2, v2);
                break;
            case CENTER:
                drawInterpolatedTexture(texture1, texture2, amount, a - c * 0.5, b - d * 0.5, a + c * 0.5, b + d * 0.5, u1, v1, u2, v2);
                break;
            case RADIUS:
                drawInterpolatedTexture(texture1, texture2, amount, a - c, b - d, a + c, b + d, u1, v1, u2, v2);
                break;
        }
    }
    
    /**
     * Draws an interpolated textured rectangle whose top left coordinate is at {@code (x, y)} the size of {@code texture}, with uv coordinates.
     * <p>
     * You can specify the coordinate of the texture to pull from.
     * <p>
     * The coordinates passed in will be transformed by the view matrix
     *
     * @param texture1 The first texture to draw.
     * @param texture2 The second texture to draw.
     * @param amount   The amount to interpolate.
     * @param x        The top left x coordinate of the textured rectangle.
     * @param y        The top left y coordinate of the textured rectangle.
     * @param u1       The top left corner x texture coordinate of the rectangle.
     * @param v1       The top left corner y texture coordinate of the rectangle.
     * @param u2       The bottom right corner u texture coordinate of the rectangle.
     * @param v2       The bottom right corner v texture coordinate of the rectangle.
     */
    public void interpolateTexture(Texture texture1, Texture texture2, double amount, double x, double y, double u1, double v1, double v2, double u2)
    {
        drawInterpolatedTexture(texture1, texture2, amount, x, y, x + texture1.width(), y + texture1.height(), u1, v1, u2, v2);
    }
    
    /**
     * Draws an interpolated textured rectangle based on {@link #rectMode()}.
     * <p>
     * See {@link RectMode} for how the points get transformed.
     * <p>
     * The coordinates passed in will be transformed by the view matrix
     *
     * @param texture1 The first texture to draw.
     * @param texture2 The second texture to draw.
     * @param amount   The amount to interpolate.
     * @param a        The a value.
     * @param b        The b value.
     * @param c        The c value.
     * @param d        The d value.
     */
    public void interpolateTexture(Texture texture1, Texture texture2, double amount, double a, double b, double c, double d)
    {
        interpolateTexture(texture1, texture2, amount, a, b, c, d, 0, 0, 1, 1);
    }
    
    /**
     * Draws an interpolated textured rectangle whose top left coordinate is at {@code (x, y)} the size of {@code texture}.
     * <p>
     * The coordinates passed in will be transformed by the view matrix
     *
     * @param texture1 The first texture to draw.
     * @param texture2 The second texture to draw.
     * @param x        The top left x coordinate of the textured rectangle.
     * @param y        The top left y coordinate of the textured rectangle.
     */
    public void interpolateTexture(Texture texture1, Texture texture2, double amount, double x, double y)
    {
        drawInterpolatedTexture(texture1, texture2, amount, x, y, x + texture1.width(), y + texture1.height(), 0, 0, 1, 1);
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
    public void drawText(String text, double x, double y)
    {
        Renderer.LOGGER.finer("Drawing Text:", text, x, y);
    
        this.target.bindFramebuffer();
        
        glActiveTexture(GL_TEXTURE0);
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
        
        this.target.markGPUDirty();
    }
    
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
        if (this.fill.a() > 0)
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
                                if ((lines.size() + 1) * this.font.size() > h) break;
                                lines.add(builder.toString());
                                builder.setLength(0);
                                builder.append(subLines[j]);
                                continue;
                            }
                            builder.append(" ").append(subLines[j]);
                        }
                        if (this.font.getStringWidth(builder.toString()) > w) break;
                        if ((lines.size() + 1) * this.font.size() > h) break;
                        lines.add(builder.toString());
                    }
                    else
                    {
                        if ((lines.size() + 1) * this.font.size() > h) break;
                        lines.add(line);
                    }
                }
            }
            else
            {
                lines = Arrays.asList(text.split("\n"));
            }
            
            double actualHeight = this.font.getStringHeight(text);
            
            int    hPos    = this.textAlign.getHorizontal();
            int    vPos    = this.textAlign.getVertical();
            double yOffset = vPos == -1 ? 0 : vPos == 0 ? 0.5 * (h - actualHeight) : h - actualHeight;
            for (String line : lines)
            {
                double lineWidth = Math.ceil(this.font.getStringWidth(line));
                double xOffset   = hPos == -1 ? 0 : hPos == 0 ? 0.5 * (w - lineWidth) : w - lineWidth;
                
                drawText(line, x + xOffset, y + yOffset);
                
                yOffset += this.font.getStringHeight(line);
            }
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
    public int[] loadPixels()
    {
        Renderer.LOGGER.finer("Loading Pixels");
        
        int length = this.target.width() * this.target.height() * this.target.channels();
        if (this.pixels == null || this.pixels.length != length) this.pixels = new int[length];
        this.target.bindTexture().download();
        for (int i = 0, n = this.pixels.length; i < n; i++) this.pixels[i] = this.target.data().get(i) & 0xFF;
        return this.pixels;
    }
    
    /**
     * Updates the target with the values of the pixel array.
     * <p>
     * This will do nothing if {@link #loadPixels()} is not called and the pixel array is not modified.
     */
    public void updatePixels()
    {
        Renderer.LOGGER.finer("Updating Pixels");
        
        for (int i = 0, n = this.pixels.length; i < n; i++) this.target.data().put(i, (byte) (this.pixels[i] & 0xFF));
        this.target.bindTexture().upload();
    }
}
