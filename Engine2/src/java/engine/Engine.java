package engine;

import engine.color.Blend;
import engine.color.Color;
import engine.color.Colorc;
import engine.event.Events;
import engine.input.Keyboard;
import engine.input.Mouse;
import engine.render.Renderer;
import engine.render.Shader;
import engine.render.Texture;
import engine.render.VertexArray;
import engine.util.Logger;
import engine.util.Profiler;
import engine.util.Random;
import org.joml.*;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL;
import org.reflections.Reflections;

import java.lang.Math;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.HashMap;

import static engine.util.Util.getCurrentDateTimeString;
import static engine.util.Util.println;
import static org.lwjgl.opengl.GL43.*;
import static org.lwjgl.stb.STBImageWrite.stbi_write_png;

public class Engine
{
    private static final String   TITLE    = "Engine - %s - FPS(%s) SPF(Avg: %s us, Min: %s us, Max: %s us)";
    private static final Logger   LOGGER   = new Logger();
    private static final Profiler PROFILER = new Profiler("Engine");
    
    private static Engine  logic;
    private static boolean running;
    private static long    startTime;
    
    private static final HashMap<String, Extension> extensions = new HashMap<>();
    
    private static long frameRate;
    private static long frameCount;
    
    private static final Vector2i screenSize = new Vector2i();
    private static final Vector2i pixelSize  = new Vector2i();
    
    private static Random random;
    private static Blend  blend;
    
    private static Mouse    mouse;
    private static Keyboard keyboard;
    private static Window   window;
    
    private static Texture target, previous;
    private static Shader      shader;
    private static VertexArray vertexArray;
    public static  Renderer    renderer;
    
    private static String printFrame;
    private static String screenshot;
    
    // ----------------------
    // -- Engine Functions --
    // ----------------------
    
    protected static void start(Engine logic, Logger.Level level)
    {
        Logger.setLevel(level);
        
        Engine.LOGGER.info("Engine Started");
        
        if (Engine.logic != null) throw new RuntimeException("start can only be called once");
        
        Engine.logic     = logic;
        Engine.running   = true;
        Engine.startTime = System.nanoTime();
        
        Engine.LOGGER.debug("Looking for Extensions");
        
        Reflections reflections = new Reflections("engine");
        for (Class<? extends Extension> ext : reflections.getSubTypesOf(Extension.class))
        {
            try
            {
                String name = ext.getSimpleName();
                Engine.extensions.put(name, ext.getConstructor().newInstance());
                Engine.LOGGER.info("Loaded: %s", name);
            }
            catch (ReflectiveOperationException ignored)
            {
            
            }
        }
        
        Engine.random = new Random();
        Engine.blend  = new Blend();
        
        try
        {
            Engine.LOGGER.debug("Extension Pre Setup");
            Engine.extensions.values().forEach(Extension::beforeSetup);
            
            Engine.LOGGER.debug("User Initialization");
            Engine.logic.setup();
            
            if (Engine.window != null)
            {
                Engine.LOGGER.debug("Extension Post Setup");
                Engine.extensions.values().forEach(Extension::afterSetup);
                
                Engine.window.unmakeCurrent();
                
                new Thread(() -> {
                    try
                    {
                        Engine.window.makeCurrent();
                        
                        GL.createCapabilities();
                        
                        long t, dt;
                        long lastFrame  = System.nanoTime();
                        long lastSecond = 0;
                        
                        long frameTime;
                        long totalTime = 0;
                        
                        long minTime = Long.MAX_VALUE;
                        long maxTime = Long.MIN_VALUE;
                        
                        int totalFrames = 0;
                        
                        while (Engine.running)
                        {
                            t = System.nanoTime();
                            
                            dt = t - lastFrame;
                            if (dt >= Engine.frameRate)
                            {
                                lastFrame = t;
                                
                                Engine.PROFILER.startFrame();
                                {
                                    Engine.PROFILER.startSection("Events");
                                    {
                                        Events.clear(); // TODO - Have a way to have events persist and be consumable.
                                        
                                        Engine.PROFILER.startSection("Mouse Events");
                                        {
                                            Engine.mouse.handleEvents(t, dt);
                                        }
                                        Engine.PROFILER.endSection();
                                        
                                        Engine.PROFILER.startSection("Key Events");
                                        {
                                            Engine.keyboard.handleEvents(t, dt);
                                        }
                                        Engine.PROFILER.endSection();
                                        
                                        Engine.PROFILER.startSection("Window Events");
                                        {
                                            Engine.window.handleEvents(t, dt);
                                        }
                                        Engine.PROFILER.endSection();
                                    }
                                    Engine.PROFILER.endSection();
                                    
                                    Engine.PROFILER.startSection("Renderer Begin");
                                    {
                                        Engine.renderer.begin();
                                    }
                                    Engine.PROFILER.endSection();
                                    
                                    Engine.PROFILER.startSection("PEX Pre");
                                    {
                                        for (String name : Engine.extensions.keySet())
                                        {
                                            if (Engine.extensions.get(name).isEnabled())
                                            {
                                                Engine.PROFILER.startSection(name);
                                                {
                                                    Engine.extensions.get(name).beforeDraw(Engine.PROFILER, dt / 1_000_000_000D);
                                                }
                                                Engine.PROFILER.endSection();
                                            }
                                        }
                                    }
                                    Engine.PROFILER.endSection();
                                    
                                    Engine.PROFILER.startSection("User Update");
                                    {
                                        Engine.logic.draw(dt / 1_000_000_000D);
                                    }
                                    Engine.PROFILER.endSection();
                                    
                                    Engine.PROFILER.startSection("PEX Post");
                                    {
                                        for (String name : Engine.extensions.keySet())
                                        {
                                            if (Engine.extensions.get(name).isEnabled())
                                            {
                                                Engine.PROFILER.startSection(name);
                                                {
                                                    Engine.extensions.get(name).afterDraw(Engine.PROFILER, dt / 1_000_000_000D);
                                                }
                                                Engine.PROFILER.endSection();
                                            }
                                        }
                                    }
                                    Engine.PROFILER.endSection();
                                    
                                    Engine.PROFILER.startSection("Renderer Finish");
                                    {
                                        Engine.renderer.finish();
                                    }
                                    Engine.PROFILER.endSection();
                                    
                                    Engine.PROFILER.startSection("Render");
                                    {
                                        if (window.updateViewport())
                                        {
                                            Engine.pixelSize.x = Math.max(Engine.window.viewW() / Engine.screenSize.x, 1);
                                            Engine.pixelSize.y = Math.max(Engine.window.viewH() / Engine.screenSize.y, 1);
                                        }
                                        
                                        Engine.target.bind();
                                        Engine.shader.bind();
                                        Engine.vertexArray.bind();
                                        
                                        glDrawArrays(GL_TRIANGLES, 0, 6);
                                        
                                        Engine.window.swap();
                                    }
                                    Engine.PROFILER.endSection();
                                    
                                    frameTime = System.nanoTime() - t;
                                    minTime   = Math.min(minTime, frameTime);
                                    maxTime   = Math.max(maxTime, frameTime);
                                    totalTime += frameTime;
                                    totalFrames++;
                                    Engine.frameCount++;
                                }
                                Engine.PROFILER.endFrame();
                                
                                if (Engine.PROFILER.enabled && Engine.printFrame != null)
                                {
                                    String parent = Engine.printFrame.equals("") ? null : Engine.printFrame;
                                    println(Engine.PROFILER.getFormattedData(parent));
                                    Engine.printFrame = null;
                                }
                                
                                if (Engine.screenshot != null)
                                {
                                    screenShotNow(Engine.screenshot);
                                    
                                    Engine.screenshot = null;
                                }
                            }
                            
                            dt = t - lastSecond;
                            if (dt >= 1_000_000_000L && totalFrames > 0)
                            {
                                lastSecond = t;
                                
                                totalTime /= totalFrames;
                                
                                Engine.window.title(String.format(Engine.TITLE, Engine.logic.name, totalFrames, totalTime / 1000D, minTime / 1000D, maxTime / 1000D));
                                
                                totalTime = 0;
                                
                                minTime = Long.MAX_VALUE;
                                maxTime = Long.MIN_VALUE;
                                
                                totalFrames = 0;
                            }
                        }
                    }
                    finally
                    {
                        Engine.window.unmakeCurrent();
                        Engine.running = false;
                    }
                }, "render").start();
                
                if (Engine.window != null) while (Engine.running) Engine.window.pollEvents();
            }
        }
        finally
        {
            Engine.LOGGER.trace("Extension Pre Destruction");
            Engine.extensions.values().forEach(Extension::beforeDestroy);
            
            Engine.LOGGER.debug("User Initialization");
            Engine.logic.destroy();
            
            Engine.LOGGER.trace("Extension Post Destruction");
            Engine.extensions.values().forEach(Extension::afterDestroy);
            
            if (Engine.window != null) Engine.window.destroy();
        }
        
        Engine.LOGGER.info("Engine Finished");
    }
    
    protected static void start(Engine logic) { start(logic, Logger.Level.INFO); }
    
    public static void stop()                 { Engine.running = false; }
    
    protected static void size(int screenW, int screenH, int pixelW, int pixelH)
    {
        Engine.screenSize.set(screenW, screenH);
        Engine.LOGGER.trace("Screen Size %s", Engine.screenSize);
        
        Engine.pixelSize.set(pixelW, pixelH);
        Engine.LOGGER.trace("Color Dimensions %s", Engine.pixelSize);
        
        if (Engine.screenSize.lengthSquared() == 0) throw new RuntimeException("Screen dimension must be > 0");
        if (Engine.pixelSize.lengthSquared() == 0) throw new RuntimeException("Pixel dimension must be > 0");
        
        Engine.mouse    = new Mouse();
        Engine.keyboard = new Keyboard();
        Engine.window   = new Window(Engine.mouse, Engine.keyboard);
        
        Engine.window.makeCurrent();
        
        GL.createCapabilities();
        
        Engine.target   = new Texture(screenW, screenH);
        Engine.previous = new Texture(screenW, screenH);
        
        Engine.shader = new Shader();
        Engine.shader.loadVertexFile("shader/pixel.vert");
        Engine.shader.loadFragmentFile("shader/pixel.frag");
        Engine.shader.validate();
        
        glEnable(GL_TEXTURE_2D);
        
        Engine.vertexArray = new VertexArray();
        Engine.vertexArray.add(2, new float[] {-1.0F, 1.0F, -1.0F, -1.0F, 1.0F, -1.0F, -1.0F, 1.0F, 1.0F, -1.0F, 1.0F, 1.0F});
        
        Engine.renderer = Renderer.getRenderer(Engine.target);
    }
    
    protected static void size(int screenW, int screenH) { size(screenW, screenH, 4, 4); }
    
    // ----------------
    // -- Properties --
    // ----------------
    
    public static long getTime()                { return Engine.startTime > 0 ? System.nanoTime() - Engine.startTime : -1L; }
    
    public static int frameRate()               { return (int) (1_000_000_000L / Engine.frameRate); }
    
    public static void frameRate(int frameRate) { Engine.frameRate = frameRate > 0 ? 1_000_000_000L / (long) frameRate : 0L; }
    
    public static long frameCount()             { return Engine.frameCount; }
    
    public static Vector2ic screenSize()        { return Engine.screenSize; }
    
    public static int screenWidth()             { return Engine.screenSize.x; }
    
    public static int screenHeight()            { return Engine.screenSize.y; }
    
    public static Vector2ic pixelSize()         { return Engine.pixelSize; }
    
    public static int pixelWidth()              { return Engine.pixelSize.x; }
    
    public static int pixelHeight()             { return Engine.pixelSize.y; }
    
    public static Mouse mouse()                 { return Engine.mouse; }
    
    public static Keyboard keyboard()           { return Engine.keyboard; }
    
    // ---------------
    // -- Functions --
    // ---------------
    
    public static void enableProfiler()              { Engine.PROFILER.enabled = true; }
    
    public static void disableProfiler()             { Engine.PROFILER.enabled = false; }
    
    public static void printFrameData(String parent) { if (Engine.PROFILER.enabled) Engine.printFrame = parent; }
    
    public static void screenShot()                  { screenShot(null); }
    
    public static void screenShot(String path)       { Engine.screenshot = path == null || path.equals("") ? "screenshot - " + getCurrentDateTimeString() : path; }
    
    public static void screenShotNow(String path)
    {
        if (!path.endsWith(".png")) path += ".png";
        
        int w = Engine.window.windowWidth();
        int h = Engine.window.windowHeight();
        int c = 4;
        
        int stride = w * c;
        
        ByteBuffer buf = BufferUtils.createByteBuffer(w * h * c);
        glReadPixels(0, 0, w, h, GL_RGBA, GL_UNSIGNED_BYTE, buf);
        
        byte[] tmp1 = new byte[stride], tmp2 = new byte[stride];
        for (int i = 0, n = h >> 1, col1, col2; i < n; i++)
        {
            col1 = i * stride;
            col2 = (h - i - 1) * stride;
            buf.get(col1, tmp1);
            buf.get(col2, tmp2);
            buf.put(col1, tmp2);
            buf.put(col2, tmp1);
        }
        
        if (!stbi_write_png(path, w, h, c, buf, stride)) Engine.LOGGER.error("Could not take screen shot");
    }
    
    // ---------------------
    // -- Random Instance --
    // ---------------------
    
    public static Random random()                                                 { return Engine.random; }
    
    public static void setSeed(long seed)                                         { Engine.random.setSeed(seed); }
    
    public static boolean nextBoolean()                                           { return Engine.random.nextBoolean(); }
    
    public static int nextInt()                                                   { return Engine.random.nextInt(); }
    
    public static int nextInt(int limit)                                          { return Engine.random.nextInt(limit); }
    
    public static int nextInt(int origin, int limit)                              { return Engine.random.nextInt(origin, limit); }
    
    public static long nextLong()                                                 { return Engine.random.nextLong(); }
    
    public static long nextLong(long limit)                                       { return Engine.random.nextLong(limit); }
    
    public static long nextLong(long origin, long limit)                          { return Engine.random.nextLong(origin, limit); }
    
    public static float nextFloat()                                               { return Engine.random.nextFloat(); }
    
    public static float nextFloat(float limit)                                    { return Engine.random.nextFloat(limit); }
    
    public static float nextFloat(float origin, float limit)                      { return Engine.random.nextFloat(origin, limit); }
    
    public static float nextFloatDir()                                            { return Engine.random.nextFloatDir(); }
    
    public static double nextDouble()                                             { return Engine.random.nextDouble(); }
    
    public static double nextDouble(double limit)                                 { return Engine.random.nextDouble(limit); }
    
    public static double nextDouble(double origin, double limit)                  { return Engine.random.nextDouble(origin, limit); }
    
    public static double nextDoubleDir()                                          { return Engine.random.nextDouble(); }
    
    public static double nextGaussian()                                           { return Engine.random.nextGaussian(); }
    
    public static int nextIndex(int[] array)                                      { return Engine.random.nextIndex(array); }
    
    public static long nextIndex(long[] array)                                    { return Engine.random.nextIndex(array); }
    
    public static float nextIndex(float[] array)                                  { return Engine.random.nextIndex(array); }
    
    public static double nextIndex(double[] array)                                { return Engine.random.nextIndex(array); }
    
    public static <T> T nextIndex(T[] array)                                      { return Engine.random.nextIndex(array); }
    
    public static <T> T nextIndex(Collection<T> collection)                       { return Engine.random.nextIndex(collection); }
    
    public static int choose(int... options)                                      { return Engine.random.choose(options); }
    
    public static long choose(long... options)                                    { return Engine.random.choose(options); }
    
    public static float choose(float... options)                                  { return Engine.random.choose(options); }
    
    public static double choose(double... options)                                { return Engine.random.choose(options); }
    
    public static Vector2i nextVector2i()                                         { return Engine.random.nextVector2i(); }
    
    public static Vector2i nextVector2i(int bound)                                { return Engine.random.nextVector2i(bound); }
    
    public static Vector2i nextVector2i(int origin, int bound)                    { return Engine.random.nextVector2i(origin, bound); }
    
    public static Vector3i nextVector3i()                                         { return Engine.random.nextVector3i(); }
    
    public static Vector3i nextVector3i(int bound)                                { return Engine.random.nextVector3i(bound); }
    
    public static Vector3i nextVector3i(int origin, int bound)                    { return Engine.random.nextVector3i(origin, bound); }
    
    public static Vector4i nextVector4i()                                         { return Engine.random.nextVector4i(); }
    
    public static Vector4i nextVector4i(int bound)                                { return Engine.random.nextVector4i(bound); }
    
    public static Vector4i nextVector4i(int origin, int bound)                    { return Engine.random.nextVector4i(origin, bound); }
    
    public static Vector2f nextVector2f()                                         { return Engine.random.nextVector2f(); }
    
    public static Vector2f nextVector2f(float bound)                              { return Engine.random.nextVector2f(bound); }
    
    public static Vector2f nextVector2f(float origin, float bound)                { return Engine.random.nextVector2f(origin, bound); }
    
    public static Vector3f nextVector3f()                                         { return Engine.random.nextVector3f(); }
    
    public static Vector3f nextVector3f(float bound)                              { return Engine.random.nextVector3f(bound); }
    
    public static Vector3f nextVector3f(float origin, float bound)                { return Engine.random.nextVector3f(origin, bound); }
    
    public static Vector4f nextVector4f()                                         { return Engine.random.nextVector4f(); }
    
    public static Vector4f nextVector4f(float bound)                              { return Engine.random.nextVector4f(bound); }
    
    public static Vector4f nextVector4f(float origin, float bound)                { return Engine.random.nextVector4f(origin, bound); }
    
    public static Vector2d nextVector2d()                                         { return Engine.random.nextVector2d(); }
    
    public static Vector2d nextVector2d(float bound)                              { return Engine.random.nextVector2d(bound); }
    
    public static Vector2d nextVector2d(float origin, float bound)                { return Engine.random.nextVector2d(origin, bound); }
    
    public static Vector3d nextVector3d()                                         { return Engine.random.nextVector3d(); }
    
    public static Vector3d nextVector3d(float bound)                              { return Engine.random.nextVector3d(bound); }
    
    public static Vector3d nextVector3d(float origin, float bound)                { return Engine.random.nextVector3d(origin, bound); }
    
    public static Vector4d nextVector4d()                                         { return Engine.random.nextVector4d(); }
    
    public static Vector4d nextVector4d(float bound)                              { return Engine.random.nextVector4d(bound); }
    
    public static Vector4d nextVector4d(float origin, float bound)                { return Engine.random.nextVector4d(origin, bound); }
    
    public static Vector2f nextUnit2f()                                           { return Engine.random.nextUnit2f(); }
    
    public static Vector3f nextUnit3f()                                           { return Engine.random.nextUnit3f(); }
    
    public static Vector4f nextUnit4f()                                           { return Engine.random.nextUnit4f(); }
    
    public static Vector2d nextUnit2d()                                           { return Engine.random.nextUnit2d(); }
    
    public static Vector3d nextUnit3d()                                           { return Engine.random.nextUnit3d(); }
    
    public static Vector4d nextUnit4d()                                           { return Engine.random.nextUnit4d(); }
    
    public static Color nextColor(int lower, int upper, boolean alpha, Color out) { return Engine.random.nextColor(lower, upper, alpha, out); }
    
    public static Color nextColor(int lower, int upper, Color out)                { return Engine.random.nextColor(lower, upper, out); }
    
    public static Color nextColor(int lower, int upper, boolean alpha)            { return Engine.random.nextColor(lower, upper, alpha); }
    
    public static Color nextColor(int lower, int upper)                           { return Engine.random.nextColor(lower, upper); }
    
    public static Color nextColor(int upper, Color out)                           { return Engine.random.nextColor(upper, out); }
    
    public static Color nextColor(int upper, boolean alpha)                       { return Engine.random.nextColor(upper, alpha); }
    
    public static Color nextColor(int upper)                                      { return Engine.random.nextColor(upper); }
    
    public static Color nextColor(Color out)                                      { return Engine.random.nextColor(out); }
    
    public static Color nextColor(boolean alpha)                                  { return Engine.random.nextColor(alpha); }
    
    public static Color nextColor()                                               { return Engine.random.nextColor(); }
    
    // --------------------
    // -- Blend Instance --
    // --------------------
    
    public static Blend blend()                                                                             { return Engine.blend; }
    
    public static Blend.Func sourceFactor()                                                                 { return Engine.blend.sourceFactor(); }
    
    public static Blend.Func destFactor()                                                                   { return Engine.blend.destFactor(); }
    
    public static Blend blendFunc(Blend.Func sourceFactor, Blend.Func destFactor)                           { return Engine.blend.blendFunc(sourceFactor, destFactor); }
    
    public static Blend.Equation blendEquation()                                                            { return Engine.blend.blendEquation(); }
    
    public static Blend blendEquation(Blend.Equation blendEquation)                                         { return Engine.blend.blendEquation(blendEquation); }
    
    public static Color blend(int rs, int gs, int bs, int as, int rd, int gd, int bd, int ad, Color result) { return Engine.blend.blend(rs, gs, bs, as, rd, gd, bd, ad, result); }
    
    public static Color blend(Colorc source, int rd, int gd, int bd, int ad, Color result)                  { return Engine.blend.blend(source, rd, gd, bd, ad, result); }
    
    public static Color blend(int rs, int gs, int bs, int as, Colorc dest, Color result)                    { return Engine.blend.blend(rs, gs, bs, as, dest, result); }
    
    public static Color blend(Colorc source, Colorc dest, Color result)                                     { return Engine.blend.blend(source, dest, result); }
    
    // ---------------------
    // -- Window Instance --
    // ---------------------
    
    public static Vector2ic monitorSize()             { return Engine.window.monitorSize(); }
    
    public static int monitorWidth()                  { return Engine.window.monitorWidth(); }
    
    public static int monitorHeight()                 { return Engine.window.monitorHeight(); }
    
    public static Vector2ic windowPos()               { return Engine.window.windowPos(); }
    
    public static int windowX()                       { return Engine.window.windowX(); }
    
    public static int windowY()                       { return Engine.window.windowY(); }
    
    public static Vector2ic windowSize()              { return Engine.window.windowSize(); }
    
    public static int windowWidth()                   { return Engine.window.windowWidth(); }
    
    public static int windowHeight()                  { return Engine.window.windowHeight(); }
    
    public static boolean focused()                   { return Engine.window.focused(); }
    
    public static boolean fullscreen()                { return Engine.window.fullscreen(); }
    
    public static void fullscreen(boolean fullscreen) { Engine.window.fullscreen(fullscreen); }
    
    public static boolean vsync()           { return Engine.window.vsync(); }
    
    public static void vsync(boolean vsync) { Engine.window.vsync(vsync); }
    
    public static Vector2ic viewPos()       { return Engine.window.viewPos(); }
    
    public static int viewX()               { return Engine.window.viewX(); }
    
    public static int viewY()               { return Engine.window.viewY(); }
    
    public static Vector2ic viewSize()      { return Engine.window.viewSize(); }
    
    public static int viewW()               { return Engine.window.viewW(); }
    
    public static int viewH()               { return Engine.window.viewH(); }
    
    // -----------------------
    // -- Renderer Instance --
    // -----------------------
    
    public static Renderer renderer() { return Engine.renderer; }
    
    // --------------------
    // -- Instance Stuff --
    // --------------------
    
    private final String name;
    
    protected Engine()
    {
        String clazz = getClass().getSimpleName();
        
        StringBuilder name = new StringBuilder();
        for (int i = 0; i < clazz.length(); i++)
        {
            char ch = clazz.charAt(i);
            if (i > 0 && Character.isUpperCase(ch)) name.append(' ');
            if (ch == '_')
            {
                name.append(" - ");
            }
            else
            {
                name.append(ch);
            }
        }
        this.name = name.toString();
    }
    
    protected void setup()                  { }
    
    protected void draw(double elapsedTime) { }
    
    protected void destroy()                { }
}
