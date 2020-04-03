package engine;

import engine.color.Blend;
import engine.color.Color;
import engine.color.Colorc;
import engine.event.Events;
import engine.input.Keyboard;
import engine.input.Mouse;
import engine.render.*;
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
import java.util.logging.Level;

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
    
    private static Mouse    mouse;
    private static Keyboard keyboard;
    private static Window   window;
    
    private static Blend       blend;
    private static Texture     target;
    private static Shader      shader;
    private static VertexArray vertexArray;
    public static  Renderer    renderer;
    
    private static String printFrame;
    private static String screenshot;
    
    // ----------------------
    // -- Engine Functions --
    // ----------------------
    
    /**
     * Starts the engine with the engine instance and optional log level. This method should only be called once in a static main method.
     * <p>
     * Below is an example class.
     * <pre>{@code
     * public class EngineClass extends Engine
     * {
     *     @Override
     *     protected void setup()
     *     {
     *
     *     }
     *
     *     @Override
     *     protected void draw(double elapsedTime)
     *     {
     *
     *     }
     *
     *     @Override
     *     protected void destroy()
     *     {
     *
     *     }
     *
     *     public static void main(String[] args)
     *     {
     *         start(new EngineClass(), Logger.Level.DEBUG);
     *     }
     * }}
     * </pre>
     *
     * @param logic The engine instance to use.
     * @param level The level logging is set to.
     */
    protected static void start(Engine logic, Level level)
    {
        Logger.setLevel(level);
        
        Engine.LOGGER.info("Engine Started");
        
        if (Engine.logic != null) throw new RuntimeException("start can only be called once");
        
        Engine.logic     = logic;
        Engine.running   = true;
        Engine.startTime = System.nanoTime();
        
        Engine.LOGGER.fine("Looking for Extensions");
        
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
        
        try
        {
            Engine.LOGGER.fine("Extension Pre Setup");
            Engine.extensions.values().forEach(Extension::beforeSetup);
            
            Engine.LOGGER.fine("User Initialization");
            Engine.logic.setup();
            
            if (Engine.window != null)
            {
                Engine.LOGGER.fine("Extension Post Setup");
                Engine.extensions.values().forEach(Extension::afterSetup);
                
                Engine.window.unmakeCurrent();
                
                new Thread(() -> {
                    try
                    {
                        Engine.window.makeCurrent();
                        
                        GL.createCapabilities();
                        
                        long t, dt;
                        long lastFrame  = nanoseconds();
                        long lastSecond = 0;
                        
                        long frameTime;
                        long totalTime = 0;
                        
                        long minTime = Long.MAX_VALUE;
                        long maxTime = Long.MIN_VALUE;
                        
                        int totalFrames = 0;
                        
                        while (Engine.running)
                        {
                            t = nanoseconds();
                            
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
                                        Engine.renderer.start();
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
                                                    Engine.renderer.push();
                                                    Engine.extensions.get(name).beforeDraw(Engine.PROFILER, dt / 1_000_000_000D);
                                                    Engine.renderer.pop();
                                                }
                                                Engine.PROFILER.endSection();
                                            }
                                        }
                                    }
                                    Engine.PROFILER.endSection();
                                    
                                    Engine.PROFILER.startSection("User Update");
                                    {
                                        Engine.renderer.push();
                                        Engine.logic.draw(dt / 1_000_000_000D);
                                        Engine.renderer.pop();
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
                                                    Engine.renderer.push();
                                                    Engine.extensions.get(name).afterDraw(Engine.PROFILER, dt / 1_000_000_000D);
                                                    Engine.renderer.pop();
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
                                        glBindFramebuffer(GL_FRAMEBUFFER, 0);
                                        if (window.updateViewport())
                                        {
                                            Engine.pixelSize.x = Math.max(Engine.window.viewW() / Engine.screenSize.x, 1);
                                            Engine.pixelSize.y = Math.max(Engine.window.viewH() / Engine.screenSize.y, 1);
                                        }
                                        
                                        Engine.target.bind();
                                        Engine.shader.bind();
                                        Engine.vertexArray.bind();
                                        Engine.vertexArray.draw(GL_TRIANGLES);
                                        
                                        Engine.window.swap();
                                    }
                                    Engine.PROFILER.endSection();
    
                                    frameTime = nanoseconds() - t;
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
                                    String fileName = Engine.screenshot + (!Engine.screenshot.endsWith(".png") ? ".png" : "");
    
                                    int w = Engine.window.frameBufferWidth();
                                    int h = Engine.window.frameBufferHeight();
                                    int c = 3;
    
                                    int stride = w * c;
    
                                    ByteBuffer buf = BufferUtils.createByteBuffer(w * h * c);
                                    glReadBuffer(GL_FRONT);
                                    glReadPixels(0, 0, w, h, GL_RGB, GL_UNSIGNED_BYTE, buf);
    
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
    
                                    if (!stbi_write_png(fileName, w, h, c, buf, stride)) Engine.LOGGER.severe("Could not take screen shot");
                                    
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
            Engine.LOGGER.fine("Extension Pre Destruction");
            Engine.extensions.values().forEach(Extension::beforeDestroy);
            
            Engine.LOGGER.fine("User Destruction");
            Engine.logic.destroy();
            
            Engine.LOGGER.fine("Extension Post Destruction");
            Engine.extensions.values().forEach(Extension::afterDestroy);
            
            if (Engine.window != null) Engine.window.destroy();
        }
        
        Engine.LOGGER.info("Engine Finished");
    }
    
    /**
     * Starts the engine with the engine instance at log level INFO. This method should only be called once in a static main method.
     *
     * @param logic The engine instance to use.
     */
    protected static void start(Engine logic)
    {
        start(logic, Level.INFO);
    }
    
    /**
     * Stops the engine after the current frame. This should be called instead of System.exit() to allow destruction methods to be called.
     */
    public static void stop()
    {
        Engine.running = false;
    }
    
    /**
     * Sets the size of the window and screen pixels and uses the renderer specified. If this function is not called then the engine will not enter the render loop.
     * <p>
     * <b>THIS MUST ONLY BE CALLED ONCE</b>
     *
     * @param screenW  The width of the screen in drawable pixels.
     * @param screenH  The height of the screen in drawable pixels.
     * @param pixelW   The width of the drawable pixels in actual pixels.
     * @param pixelH   The height of the drawable pixels in actual pixels.
     * @param renderer The optional renderer to use.
     */
    protected static void size(int screenW, int screenH, int pixelW, int pixelH, String renderer)
    {
        Engine.screenSize.set(screenW, screenH);
        Engine.LOGGER.finest("Screen Size %s", Engine.screenSize);
        
        Engine.pixelSize.set(pixelW, pixelH);
        Engine.LOGGER.finest("Pixel Dimensions %s", Engine.pixelSize);
        
        if (Engine.screenSize.lengthSquared() == 0) throw new RuntimeException("Screen dimension must be > 0");
        if (Engine.pixelSize.lengthSquared() == 0) throw new RuntimeException("Pixel dimension must be > 0");
    
        Engine.mouse    = new Mouse();
        Engine.keyboard = new Keyboard();
        Engine.window   = new Window(Engine.mouse, Engine.keyboard);
    
        Engine.window.makeCurrent();
    
        GL.createCapabilities();
    
        Engine.blend = new Blend();
    
        Engine.target = new Texture(screenW, screenH);
    
        Engine.shader = new Shader().loadVertexFile("shader/pixel.vert").loadFragmentFile("shader/pixel.frag").validate();
    
        glEnable(GL_TEXTURE_2D);
    
        Engine.vertexArray = new VertexArray().bind();
        Engine.vertexArray.add(new float[] {-1.0F, 1.0F, -1.0F, -1.0F, 1.0F, -1.0F, -1.0F, 1.0F, 1.0F, -1.0F, 1.0F, 1.0F}, 2);
    
        Engine.renderer = Renderer.getRenderer(Engine.target, renderer);
    }
    
    /**
     * Sets the size of the window and screen pixels and uses the software renderer. If this function is not called then the engine will not enter the render loop.
     * <p>
     * <b>THIS MUST ONLY BE CALLED ONCE</b>
     *
     * @param screenW The width of the screen in drawable pixels.
     * @param screenH The height of the screen in drawable pixels.
     * @param pixelW  The width of the drawable pixels in actual pixels.
     * @param pixelH  The height of the drawable pixels in actual pixels.
     */
    protected static void size(int screenW, int screenH, int pixelW, int pixelH)
    {
        size(screenW, screenH, pixelW, pixelH, "software");
    }
    
    
    /**
     * Sets the size of the window and uses the renderer specified. If this function is not called then the engine will not enter the render loop.
     * <p>
     * <b>THIS MUST ONLY BE CALLED ONCE</b>
     *
     * @param screenW  The width of the screen in drawable pixels.
     * @param screenH  The height of the screen in drawable pixels.
     * @param renderer The optional renderer to use.
     */
    protected static void size(int screenW, int screenH, String renderer)
    {
        size(screenW, screenH, 4, 4, renderer);
    }
    
    /**
     * Sets the size of the window and uses the software renderer If this function is not called then the engine will not enter the render loop.
     * <p>
     * <b>THIS MUST ONLY BE CALLED ONCE</b>
     *
     * @param screenW The width of the screen in drawable pixels.
     * @param screenH The height of the screen in drawable pixels.
     */
    protected static void size(int screenW, int screenH)
    {
        size(screenW, screenH, 4, 4, "software");
    }
    
    // ----------------
    // -- Properties --
    // ----------------
    
    /**
     * @return The time in nanoseconds since the engine started
     */
    public static long nanoseconds()
    {
        return Engine.startTime > 0 ? System.nanoTime() - Engine.startTime : -1L;
    }
    
    /**
     * @return The time in microseconds since the engine started
     */
    public static double microseconds()
    {
        return nanoseconds() / 1_000D;
    }
    
    /**
     * @return The time in milliseconds since the engine started
     */
    public static double milliseconds()
    {
        return nanoseconds() / 1_000_000D;
    }
    
    /**
     * @return The time in seconds since the engine started
     */
    public static double seconds()
    {
        return nanoseconds() / 1_000_000_000D;
    }
    
    /**
     * @return The current frame rate or zero if not limit is set.
     */
    public static int frameRate()
    {
        return Engine.frameRate > 0 ? (int) (1_000_000_000L / Engine.frameRate) : 0;
    }
    
    /**
     * Sets the frame rate to try to run at. Use zero for no limit.
     *
     * @param frameRate The new frame rate.
     */
    public static void frameRate(int frameRate)
    {
        Engine.frameRate = frameRate > 0 ? 1_000_000_000L / (long) frameRate : 0L;
    }
    
    /**
     * @return The current frame that engine is on.
     */
    public static long frameCount()
    {
        return Engine.frameCount;
    }
    
    /**
     * @return The read-only screen size vector in screen pixels. This will be the values passed in to the {@link #size} function.
     */
    public static Vector2ic screenSize()
    {
        return Engine.screenSize;
    }
    
    /**
     * @return The screen width in screen pixels. This will be the value passed in to the {@link #size} function.
     */
    public static int screenWidth()
    {
        return Engine.screenSize.x;
    }
    
    /**
     * @return The screen height in screen pixels. This will be the value passed in to the {@link #size} function.
     */
    public static int screenHeight()
    {
        return Engine.screenSize.y;
    }
    
    /**
     * @return The read-only pixel size vector in actual pixels. This will be the values passed in to the {@link #size} function.
     */
    public static Vector2ic pixelSize()
    {
        return Engine.pixelSize;
    }
    
    /**
     * @return The pixel width in actual pixels. This will be the value passed in to the {@link #size} function.
     */
    public static int pixelWidth()
    {
        return Engine.pixelSize.x;
    }
    
    /**
     * @return The pixel height in actual pixels. This will be the value passed in to the {@link #size} function.
     */
    public static int pixelHeight()
    {
        return Engine.pixelSize.y;
    }
    
    /**
     * @return The mouse instance. This will be null unless {@link #size} is called.
     */
    public static Mouse mouse()
    {
        return Engine.mouse;
    }
    
    /**
     * @return The keyboard instance. This will be null unless {@link #size} is called.
     */
    public static Keyboard keyboard()
    {
        return Engine.keyboard;
    }
    
    /**
     * @return The window instance. This will be null unless {@link #size} is called.
     */
    public static Window window()
    {
        return Engine.window;
    }
    
    // ---------------
    // -- Functions --
    // ---------------
    
    /**
     * Enables the profiler to start keeping track of frame data.
     */
    public static void enableProfiler()
    {
        Engine.PROFILER.enabled = true;
    }
    
    /**
     * Disabled the profiler.
     */
    public static void disableProfiler()
    {
        Engine.PROFILER.enabled = false;
    }
    
    /**
     * Prints the data for the current frame after the frame has competed.
     *
     * @param parent The frame group to print. If no parent is given, then it will print everything.
     */
    public static void printFrameData(String parent)
    {
        if (Engine.PROFILER.enabled) Engine.printFrame = parent;
    }
    
    /**
     * Takes a screen shot after the frame is completed and saves it to the path given.
     *
     * @param path The path to the output image file.
     */
    public static void screenShot(String path)
    {
        Engine.screenshot = path == null || path.equals("") ? "screenshot - " + getCurrentDateTimeString() : path;
    }
    
    /**
     * Takes a screen shot and saves it to the current directory.
     */
    public static void screenShot()
    {
        screenShot(null);
    }
    
    // ---------------------
    // -- Random Instance --
    // ---------------------
    
    /**
     * Gets the engine's random instance. This is used to give the entire engine a common state if desired.
     *
     * @return The common random instance.
     */
    public static Random random()
    {
        return Engine.random;
    }
    
    /**
     * See {@link Random#}
     */
    public static void setSeed(long seed)
    {
        Engine.random.setSeed(seed);
    }
    
    /**
     * See {@link Random#nextBoolean()}
     */
    public static boolean nextBoolean()
    {
        return Engine.random.nextBoolean();
    }
    
    /**
     * See {@link Random#nextInt()}
     */
    public static int nextInt()
    {
        return Engine.random.nextInt();
    }
    
    /**
     * See {@link Random#nextInt(int)}
     */
    public static int nextInt(int bound)
    {
        return Engine.random.nextInt(bound);
    }
    
    /**
     * See {@link Random#nextInt(int, int)}
     */
    public static int nextInt(int origin, int bound)
    {
        return Engine.random.nextInt(origin, bound);
    }
    
    /**
     * See {@link Random#nextLong()}
     */
    public static long nextLong()
    {
        return Engine.random.nextLong();
    }
    
    /**
     * See {@link Random#nextLong(long)}
     */
    public static long nextLong(long bound)
    {
        return Engine.random.nextLong(bound);
    }
    
    /**
     * See {@link Random#nextLong(long, long)}
     */
    public static long nextLong(long origin, long bound)
    {
        return Engine.random.nextLong(origin, bound);
    }
    
    /**
     * See {@link Random#nextFloat()}
     */
    public static float nextFloat()
    {
        return Engine.random.nextFloat();
    }
    
    /**
     * See {@link Random#nextFloat(float)}
     */
    public static float nextFloat(float bound)
    {
        return Engine.random.nextFloat(bound);
    }
    
    /**
     * See {@link Random#nextFloat(float, float)}
     */
    public static float nextFloat(float origin, float bound)
    {
        return Engine.random.nextFloat(origin, bound);
    }
    
    /**
     * See {@link Random#nextFloatDir()}
     */
    public static float nextFloatDir()
    {
        return Engine.random.nextFloatDir();
    }
    
    /**
     * See {@link Random#nextDouble()}
     */
    public static double nextDouble()
    {
        return Engine.random.nextDouble();
    }
    
    /**
     * See {@link Random#nextDouble(double)}
     */
    public static double nextDouble(double bound)
    {
        return Engine.random.nextDouble(bound);
    }
    
    /**
     * See {@link Random#nextDouble(double, double)}
     */
    public static double nextDouble(double origin, double bound)
    {
        return Engine.random.nextDouble(origin, bound);
    }
    
    /**
     * See {@link Random#nextDoubleDir()}
     */
    public static double nextDoubleDir()
    {
        return Engine.random.nextDouble();
    }
    
    /**
     * See {@link Random#nextGaussian()}
     */
    public static double nextGaussian()
    {
        return Engine.random.nextGaussian();
    }
    
    /**
     * See {@link Random#nextFrom(int...)}
     */
    public static int nextFrom(int... array)
    {
        return Engine.random.nextFrom(array);
    }
    
    /**
     * See {@link Random#nextFrom(long...)}
     */
    public static long nextFrom(long... array)
    {
        return Engine.random.nextFrom(array);
    }
    
    /**
     * See {@link Random#nextFrom(float...)}
     */
    public static float nextFrom(float... array)
    {
        return Engine.random.nextFrom(array);
    }
    
    /**
     * See {@link Random#nextFrom(double...)}
     */
    public static double nextFrom(double... array)
    {
        return Engine.random.nextFrom(array);
    }
    
    /**
     * See {@link Random#}
     * public static <T> T nextFrom(T...)
     */
    @SafeVarargs
    public static <T> T nextFrom(T... array)
    {
        return Engine.random.nextFrom(array);
    }
    
    /**
     * See {@link Random#> T nextFrom(Collection<T> collection)}
     */
    public static <T> T nextFrom(Collection<T> collection)
    {
        return Engine.random.nextFrom(collection);
    }
    
    /**
     * See {@link Random#nextVector2i()}
     */
    public static Vector2i nextVector2i()
    {
        return Engine.random.nextVector2i();
    }
    
    /**
     * See {@link Random#nextVector2i(int)}
     */
    public static Vector2i nextVector2i(int bound)
    {
        return Engine.random.nextVector2i(bound);
    }
    
    /**
     * See {@link Random#nextVector2i(int, int)}
     */
    public static Vector2i nextVector2i(int origin, int bound)
    {
        return Engine.random.nextVector2i(origin, bound);
    }
    
    /**
     * See {@link Random#nextVector3i()}
     */
    public static Vector3i nextVector3i()
    {
        return Engine.random.nextVector3i();
    }
    
    /**
     * See {@link Random#nextVector3i(int)}
     */
    public static Vector3i nextVector3i(int bound)
    {
        return Engine.random.nextVector3i(bound);
    }
    
    /**
     * See {@link Random#nextVector3i(int, int)}
     */
    public static Vector3i nextVector3i(int origin, int bound)
    {
        return Engine.random.nextVector3i(origin, bound);
    }
    
    /**
     * See {@link Random#nextVector4i()}
     */
    public static Vector4i nextVector4i()
    {
        return Engine.random.nextVector4i();
    }
    
    /**
     * See {@link Random#nextVector4i(int)}
     */
    public static Vector4i nextVector4i(int bound)
    {
        return Engine.random.nextVector4i(bound);
    }
    
    /**
     * See {@link Random#nextVector4i(int, int)}
     */
    public static Vector4i nextVector4i(int origin, int bound)
    {
        return Engine.random.nextVector4i(origin, bound);
    }
    
    /**
     * See {@link Random#nextVector2f()}
     */
    public static Vector2f nextVector2f()
    {
        return Engine.random.nextVector2f();
    }
    
    /**
     * See {@link Random#nextVector2f(float)}
     */
    public static Vector2f nextVector2f(float bound)
    {
        return Engine.random.nextVector2f(bound);
    }
    
    /**
     * See {@link Random#nextVector2f(float, float)}
     */
    public static Vector2f nextVector2f(float origin, float bound)
    {
        return Engine.random.nextVector2f(origin, bound);
    }
    
    /**
     * See {@link Random#nextVector3f()}
     */
    public static Vector3f nextVector3f()
    {
        return Engine.random.nextVector3f();
    }
    
    /**
     * See {@link Random#nextVector3f(float)}
     */
    public static Vector3f nextVector3f(float bound)
    {
        return Engine.random.nextVector3f(bound);
    }
    
    /**
     * See {@link Random#nextVector3f(float, float)}
     */
    public static Vector3f nextVector3f(float origin, float bound)
    {
        return Engine.random.nextVector3f(origin, bound);
    }
    
    /**
     * See {@link Random#nextVector4f()}
     */
    public static Vector4f nextVector4f()
    {
        return Engine.random.nextVector4f();
    }
    
    /**
     * See {@link Random#nextVector4f(float)}
     */
    public static Vector4f nextVector4f(float bound)
    {
        return Engine.random.nextVector4f(bound);
    }
    
    /**
     * See {@link Random#nextVector4f(float, float)}
     */
    public static Vector4f nextVector4f(float origin, float bound)
    {
        return Engine.random.nextVector4f(origin, bound);
    }
    
    /**
     * See {@link Random#nextVector2d()}
     */
    public static Vector2d nextVector2d()
    {
        return Engine.random.nextVector2d();
    }
    
    /**
     * See {@link Random#nextVector2d(float)}
     */
    public static Vector2d nextVector2d(float bound)
    {
        return Engine.random.nextVector2d(bound);
    }
    
    /**
     * See {@link Random#nextVector2d(float, float)}
     */
    public static Vector2d nextVector2d(float origin, float bound)
    {
        return Engine.random.nextVector2d(origin, bound);
    }
    
    /**
     * See {@link Random#nextVector3d()}
     */
    public static Vector3d nextVector3d()
    {
        return Engine.random.nextVector3d();
    }
    
    /**
     * See {@link Random#nextVector3d(float)}
     */
    public static Vector3d nextVector3d(float bound)
    {
        return Engine.random.nextVector3d(bound);
    }
    
    /**
     * See {@link Random#nextVector3d(float, float)}
     */
    public static Vector3d nextVector3d(float origin, float bound)
    {
        return Engine.random.nextVector3d(origin, bound);
    }
    
    /**
     * See {@link Random#nextVector4d()}
     */
    public static Vector4d nextVector4d()
    {
        return Engine.random.nextVector4d();
    }
    
    /**
     * See {@link Random#nextVector4d(float)}
     */
    public static Vector4d nextVector4d(float bound)
    {
        return Engine.random.nextVector4d(bound);
    }
    
    /**
     * See {@link Random#nextVector4d(float, float)}
     */
    public static Vector4d nextVector4d(float origin, float bound)
    {
        return Engine.random.nextVector4d(origin, bound);
    }
    
    /**
     * See {@link Random#nextUnit2f()}
     */
    public static Vector2f nextUnit2f()
    {
        return Engine.random.nextUnit2f();
    }
    
    /**
     * See {@link Random#nextUnit3f()}
     */
    public static Vector3f nextUnit3f()
    {
        return Engine.random.nextUnit3f();
    }
    
    /**
     * See {@link Random#nextUnit4f()}
     */
    public static Vector4f nextUnit4f()
    {
        return Engine.random.nextUnit4f();
    }
    
    /**
     * See {@link Random#nextUnit2d()}
     */
    public static Vector2d nextUnit2d()
    {
        return Engine.random.nextUnit2d();
    }
    
    /**
     * See {@link Random#nextUnit3d()}
     */
    public static Vector3d nextUnit3d()
    {
        return Engine.random.nextUnit3d();
    }
    
    /**
     * See {@link Random#nextUnit4d()}
     */
    public static Vector4d nextUnit4d()
    {
        return Engine.random.nextUnit4d();
    }
    
    /**
     * See {@link Random#nextColor(int, int, boolean, Color)}
     */
    public static Color nextColor(int lower, int upper, boolean alpha, Color out)
    {
        return Engine.random.nextColor(lower, upper, alpha, out);
    }
    
    /**
     * See {@link Random#nextColor(int, int, Color)}
     */
    public static Color nextColor(int lower, int upper, Color out)
    {
        return Engine.random.nextColor(lower, upper, out);
    }
    
    /**
     * See {@link Random#nextColor(int, int, boolean)}
     */
    public static Color nextColor(int lower, int upper, boolean alpha)
    {
        return Engine.random.nextColor(lower, upper, alpha);
    }
    
    /**
     * See {@link Random#nextColor(int, int)}
     */
    public static Color nextColor(int lower, int upper)
    {
        return Engine.random.nextColor(lower, upper);
    }
    
    /**
     * See {@link Random#nextColor(int, Color)}
     */
    public static Color nextColor(int upper, Color out)
    {
        return Engine.random.nextColor(upper, out);
    }
    
    /**
     * See {@link Random#nextColor(int, boolean)}
     */
    public static Color nextColor(int upper, boolean alpha)
    {
        return Engine.random.nextColor(upper, alpha);
    }
    
    /**
     * See {@link Random#nextColor(int)}
     */
    public static Color nextColor(int upper)
    {
        return Engine.random.nextColor(upper);
    }
    
    /**
     * See {@link Random#nextColor(Color)}
     */
    public static Color nextColor(Color out)
    {
        return Engine.random.nextColor(out);
    }
    
    /**
     * See {@link Random#nextColor(boolean)}
     */
    public static Color nextColor(boolean alpha)
    {
        return Engine.random.nextColor(alpha);
    }
    
    /**
     * See {@link Random#nextColor()}
     */
    public static Color nextColor()
    {
        return Engine.random.nextColor();
    }
    
    // --------------------
    // -- Blend Instance --
    // --------------------
    
    /**
     * @return The engine's blend instance.
     */
    public static Blend blend()
    {
        return Engine.blend;
    }
    
    /**
     * See {@link Blend#sourceFactor()}
     */
    public static Blend.Func sourceFactor()
    {
        return Engine.blend.sourceFactor();
    }
    
    /**
     * See {@link Blend#destFactor()}
     */
    public static Blend.Func destFactor()
    {
        return Engine.blend.destFactor();
    }
    
    /**
     * See {@link Blend#blendFunc(Blend.Func, Blend.Func)}
     */
    public static Blend blendFunc(Blend.Func sourceFactor, Blend.Func destFactor)
    {
        return Engine.blend.blendFunc(sourceFactor, destFactor);
    }
    
    /**
     * See {@link Blend#blendEquation()}
     */
    public static Blend.Equation blendEquation()
    {
        return Engine.blend.blendEquation();
    }
    
    /**
     * See {@link Blend#blendEquation(Blend.Equation)}
     */
    public static Blend blendEquation(Blend.Equation blendEquation)
    {
        return Engine.blend.blendEquation(blendEquation);
    }
    
    /**
     * See {@link Blend#blend(int, int, int, int, int, int, int, int, Color)}
     */
    public static Color blend(int rs, int gs, int bs, int as, int rd, int gd, int bd, int ad, Color result)
    {
        return Engine.blend.blend(rs, gs, bs, as, rd, gd, bd, ad, result);
    }
    
    /**
     * See {@link Blend#blend(Colorc, int, int, int, int, Color)}
     */
    public static Color blend(Colorc source, int rd, int gd, int bd, int ad, Color result)
    {
        return Engine.blend.blend(source, rd, gd, bd, ad, result);
    }
    
    /**
     * See {@link Blend#blend(int, int, int, int, Colorc, Color)}
     */
    public static Color blend(int rs, int gs, int bs, int as, Colorc dest, Color result)
    {
        return Engine.blend.blend(rs, gs, bs, as, dest, result);
    }
    
    /**
     * See {@link Blend#blend(Colorc, Colorc, Color)}
     */
    public static Color blend(Colorc source, Colorc dest, Color result)
    {
        return Engine.blend.blend(source, dest, result);
    }
    
    // -----------------------
    // -- Renderer Instance --
    // -----------------------
    
    /**
     * @return The engine's render instance. This should only be used for {@link Overloads} methods.
     */
    public static Renderer renderer()
    {
        return Engine.renderer;
    }
    
    /**
     * See {@link Renderer#enableBlend()}
     */
    public static boolean enableBlend()
    {
        return Engine.renderer.enableBlend();
    }
    
    /**
     * See {@link Renderer#enableBlend(boolean)}
     */
    public static void enableBlend(boolean enableBlend)
    {
        Engine.renderer.enableBlend(enableBlend);
    }
    
    /**
     * See {@link Renderer#fill()}
     */
    public static Colorc fill()
    {
        return Engine.renderer.fill();
    }
    
    /**
     * See {@link Renderer#fill(Number, Number, Number, Number)}
     */
    public static void fill(Number r, Number g, Number b, Number a)
    {
        Engine.renderer.fill(r, g, b, a);
    }
    
    /**
     * See {@link Renderer#fill(Number, Number, Number)}
     */
    public static void fill(Number r, Number g, Number b)
    {
        Engine.renderer.fill(r, g, b);
    }
    
    /**
     * See {@link Renderer#fill(Number, Number)}
     */
    public static void fill(Number grey, Number a)
    {
        Engine.renderer.fill(grey, a);
    }
    
    /**
     * See {@link Renderer#fill(Number)}
     */
    public static void fill(Number grey)
    {
        Engine.renderer.fill(grey);
    }
    
    /**
     * See {@link Renderer#fill(Colorc)}
     */
    public static void fill(Colorc fill)
    {
        Engine.renderer.fill(fill);
    }
    
    /**
     * See {@link Renderer#noFill()}
     */
    public static void noFill()
    {
        Engine.renderer.noFill();
    }
    
    /**
     * See {@link Renderer#stroke()}
     */
    public static Colorc stroke()
    {
        return Engine.renderer.stroke();
    }
    
    /**
     * See {@link Renderer#stroke(Number, Number, Number, Number)}
     */
    public static void stroke(Number r, Number g, Number b, Number a)
    {
        Engine.renderer.stroke(r, g, b, a);
    }
    
    /**
     * See {@link Renderer#stroke(Number, Number, Number)}
     */
    public static void stroke(Number r, Number g, Number b)
    {
        Engine.renderer.stroke(r, g, b);
    }
    
    /**
     * See {@link Renderer#stroke(Number, Number)}
     */
    public static void stroke(Number grey, Number a)
    {
        Engine.renderer.stroke(grey, a);
    }
    
    /**
     * See {@link Renderer#stroke(Number)}
     */
    public static void stroke(Number grey)
    {
        Engine.renderer.stroke(grey);
    }
    
    /**
     * See {@link Renderer#stroke(Colorc)}
     */
    public static void stroke(Colorc stroke)
    {
        Engine.renderer.stroke(stroke);
    }
    
    /**
     * See {@link Renderer#noStroke()}
     */
    public static void noStroke()
    {
        Engine.renderer.noStroke();
    }
    
    /**
     * See {@link Renderer#weight()}
     */
    public static double weight()
    {
        return Engine.renderer.weight();
    }
    
    /**
     * See {@link Renderer#weight(double)}
     */
    public static void weight(double weight)
    {
        Engine.renderer.weight(weight);
    }
    
    /**
     * See {@link Renderer#rectMode()}
     */
    public static RectMode rectMode()
    {
        return Engine.renderer.rectMode();
    }
    
    /**
     * See {@link Renderer#rectMode(RectMode)}
     */
    public static void rectMode(RectMode rectMode)
    {
        Engine.renderer.rectMode(rectMode);
    }
    
    /**
     * See {@link Renderer#ellipseMode()}
     */
    public static EllipseMode ellipseMode()
    {
        return Engine.renderer.ellipseMode();
    }
    
    /**
     * See {@link Renderer#ellipseMode(EllipseMode)}
     */
    public static void ellipseMode(EllipseMode ellipseMode)
    {
        Engine.renderer.ellipseMode(ellipseMode);
    }
    
    /**
     * See {@link Renderer#arcMode()}
     */
    public static ArcMode arcMode()
    {
        return Engine.renderer.arcMode();
    }
    
    /**
     * See {@link Renderer#arcMode(ArcMode)}
     */
    public static void arcMode(ArcMode arcMode)
    {
        Engine.renderer.arcMode(arcMode);
    }
    
    /**
     * See {@link Renderer#textFont()}
     */
    public static Font textFont()
    {
        return Engine.renderer.textFont();
    }
    
    /**
     * See {@link Renderer#textFont(Font)}
     */
    public static void textFont(Font font)
    {
        Engine.renderer.textFont(font);
    }
    
    /**
     * See {@link Renderer#textFont(String)}
     */
    public static void textFont(String font)
    {
        Engine.renderer.textFont(font);
    }
    
    /**
     * See {@link Renderer#textFont(String)}
     */
    public static void textFont(String font, int size)
    {
        Engine.renderer.textFont(font, size);
    }
    
    /**
     * See {@link Renderer#textSize()}
     */
    public static int textSize()
    {
        return Engine.renderer.textSize();
    }
    
    /**
     * See {@link Renderer#textSize(int)}
     */
    public static void textSize(int textSize)
    {
        Engine.renderer.textSize(textSize);
    }
    
    /**
     * See {@link Renderer#textAscent()}
     */
    public static double textAscent()
    {
        return Engine.renderer.textAscent();
    }
    
    /**
     * See {@link Renderer#textDescent()}
     */
    public static double textDescent()
    {
        return Engine.renderer.textDescent();
    }
    
    /**
     * See {@link Renderer#textAlign()}
     */
    public static TextAlign textAlign()
    {
        return Engine.renderer.textAlign();
    }
    
    /**
     * See {@link Renderer#textAlign(TextAlign)}
     */
    public static void textAlign(TextAlign textAlign)
    {
        Engine.renderer.textAlign(textAlign);
    }
    
    /**
     * See {@link Renderer#identity()}
     */
    public static void identity()
    {
        Engine.renderer.identity();
    }
    
    /**
     * See {@link Renderer#translate(double, double)}
     */
    public static void translate(double x, double y)
    {
        Engine.renderer.translate(x, y);
    }
    
    /**
     * See {@link Renderer#rotate(double)}
     */
    public static void rotate(double angle)
    {
        Engine.renderer.rotate(angle);
    }
    
    /**
     * See {@link Renderer#scale(double, double)}
     */
    public static void scale(double x, double y)
    {
        Engine.renderer.scale(x, y);
    }
    
    /**
     * See {@link Renderer#start()}
     */
    public static void start()
    {
        Engine.renderer.start();
    }
    
    /**
     * See {@link Renderer#finish()}
     */
    public static void finish()
    {
        Engine.renderer.finish();
    }
    
    /**
     * See {@link Renderer#push()}
     */
    public static void push()
    {
        Engine.renderer.push();
    }
    
    /**
     * See {@link Renderer#pop()}
     */
    public static void pop()
    {
        Engine.renderer.pop();
    }
    
    /**
     * See {@link Renderer#clear()}
     */
    public static void clear(Number r, Number g, Number b, Number a)
    {
        Engine.renderer.clear(r, g, b, a);
    }
    
    /**
     * See {@link Renderer#clear()}
     */
    public static void clear(Number r, Number g, Number b)
    {
        Engine.renderer.clear(r, g, b);
    }
    
    /**
     * See {@link Renderer#clear()}
     */
    public static void clear(Number grey, Number a)
    {
        Engine.renderer.clear(grey, a);
    }
    
    /**
     * See {@link Renderer#clear()}
     */
    public static void clear(Number grey)
    {
        Engine.renderer.clear(grey);
    }
    
    /**
     * See {@link Renderer#clear()}
     */
    public static void clear()
    {
        Engine.renderer.clear();
    }
    
    /**
     * See {@link Renderer#clear()}
     */
    public static void clear(Colorc color)
    {
        Engine.renderer.clear(color);
    }
    
    /**
     * See {@link Renderer#drawPoint(double, double)}
     */
    public static void drawPoint(double x, double y)
    {
        Engine.renderer.drawPoint(x, y);
    }
    
    /**
     * See {@link Renderer#point(double, double)}
     */
    public static void point(double x, double y)
    {
        Engine.renderer.point(x, y);
    }
    
    /**
     * See {@link Renderer#drawLine(double, double, double, double)}
     */
    public static void drawLine(double x1, double y1, double x2, double y2)
    {
        Engine.renderer.drawLine(x1, y1, x2, y2);
    }
    
    /**
     * See {@link Renderer#line(double, double, double, double)}
     */
    public static void line(double x1, double y1, double x2, double y2)
    {
        Engine.renderer.line(x1, y1, x2, y2);
    }
    
    /**
     * See {@link Renderer#drawBezier(double, double, double, double, double, double)}
     */
    public static void drawBezier(double x1, double y1, double x2, double y2, double x3, double y3)
    {
        Engine.renderer.drawBezier(x1, y1, x2, y2, x3, y3);
    }
    
    /**
     * See {@link Renderer#bezier(double, double, double, double, double, double)}
     */
    public static void bezier(double x1, double y1, double x2, double y2, double x3, double y3)
    {
        Engine.renderer.bezier(x1, y1, x2, y2, x3, y3);
    }
    
    /**
     * See {@link Renderer#drawTriangle(double, double, double, double, double, double)}
     */
    public static void drawTriangle(double x1, double y1, double x2, double y2, double x3, double y3)
    {
        Engine.renderer.drawTriangle(x1, y1, x2, y2, x3, y3);
    }
    
    /**
     * See {@link Renderer#fillTriangle(double, double, double, double, double, double)}
     */
    public static void fillTriangle(double x1, double y1, double x2, double y2, double x3, double y3)
    {
        Engine.renderer.fillTriangle(x1, y1, x2, y2, x3, y3);
    }
    
    /**
     * See {@link Renderer#triangle(double, double, double, double, double, double)}
     */
    public static void triangle(double x1, double y1, double x2, double y2, double x3, double y3)
    {
        Engine.renderer.triangle(x1, y1, x2, y2, x3, y3);
    }
    
    /**
     * See {@link Renderer#drawSquare(double, double, double)}
     */
    public static void drawSquare(double x, double y, double w)
    {
        Engine.renderer.drawSquare(x, y, w);
    }
    
    /**
     * See {@link Renderer#fillSquare(double, double, double)}
     */
    public static void fillSquare(double x, double y, double w)
    {
        Engine.renderer.fillSquare(x, y, w);
    }
    
    /**
     * See {@link Renderer#square(double, double, double)}
     */
    public static void square(double a, double b, double c)
    {
        Engine.renderer.square(a, b, c);
    }
    
    /**
     * See {@link Renderer#drawRect(double, double, double, double)}
     */
    public static void drawRect(double x, double y, double w, double h)
    {
        Engine.renderer.drawRect(x, y, w, h);
    }
    
    /**
     * See {@link Renderer#fillRect(double, double, double, double)}
     */
    public static void fillRect(double x, double y, double w, double h)
    {
        Engine.renderer.drawRect(x, y, w, h);
    }
    
    /**
     * See {@link Renderer#rect(double, double, double, double)}
     */
    public static void rect(double a, double b, double c, double d)
    {
        Engine.renderer.rect(a, b, c, d);
    }
    
    /**
     * See {@link Renderer#drawQuad(double, double, double, double, double, double, double, double)}
     */
    public static void drawQuad(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4)
    {
        Engine.renderer.drawQuad(x1, y1, x2, y2, x3, y3, x4, y4);
    }
    
    /**
     * See {@link Renderer#fillQuad(double, double, double, double, double, double, double, double)}
     */
    public static void fillQuad(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4)
    {
        Engine.renderer.fillQuad(x1, y1, x2, y2, x3, y3, x4, y4);
    }
    
    /**
     * See {@link Renderer#quad(double, double, double, double, double, double, double, double)}
     */
    public static void quad(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4)
    {
        Engine.renderer.quad(x1, y1, x2, y2, x3, y3, x4, y4);
    }
    
    /**
     * See {@link Renderer#drawPolygon(double...)}
     */
    public static void drawPolygon(double... points)
    {
        Engine.renderer.drawPolygon(points);
    }
    
    /**
     * See {@link Renderer#fillPolygon(double...)}
     */
    public static void fillPolygon(double... points)
    {
        Engine.renderer.fillPolygon(points);
    }
    
    /**
     * See {@link Renderer#polygon(double...)}
     */
    public static void polygon(double... points)
    {
        Engine.renderer.polygon(points);
    }
    
    /**
     * See {@link Renderer#drawCircle(double, double, double)}
     */
    public static void drawCircle(double x, double y, double r)
    {
        Engine.renderer.drawCircle(x, y, r);
    }
    
    /**
     * See {@link Renderer#fillCircle(double, double, double)}
     */
    public static void fillCircle(double x, double y, double r)
    {
        Engine.renderer.fillCircle(x, y, r);
    }
    
    /**
     * See {@link Renderer#circle(double, double, double)}
     */
    public static void circle(double a, double b, double c)
    {
        Engine.renderer.circle(a, b, c);
    }
    
    /**
     * See {@link Renderer#drawEllipse(double, double, double, double)}
     */
    public static void drawEllipse(double x, double y, double rx, double ry)
    {
        Engine.renderer.drawEllipse(x, y, rx, ry);
    }
    
    /**
     * See {@link Renderer#fillEllipse(double, double, double, double)}
     */
    public static void fillEllipse(double x, double y, double rx, double ry)
    {
        Engine.renderer.fillEllipse(x, y, rx, ry);
    }
    
    /**
     * See {@link Renderer#ellipse(double, double, double, double)}
     */
    public static void ellipse(double a, double b, double c, double d)
    {
        Engine.renderer.ellipse(a, b, c, d);
    }
    
    /**
     * See {@link Renderer#drawArc(double, double, double, double, double, double)}
     */
    public static void drawArc(double x, double y, double rx, double ry, double start, double stop)
    {
        Engine.renderer.drawArc(x, y, rx, ry, start, stop);
    }
    
    /**
     * See {@link Renderer#fillArc(double, double, double, double, double, double)}
     */
    public static void fillArc(double x, double y, double rx, double ry, double start, double stop)
    {
        Engine.renderer.fillArc(x, y, rx, ry, start, stop);
    }
    
    /**
     * See {@link Renderer#arc(double, double, double, double, double, double)}
     */
    public static void arc(double a, double b, double c, double d, double start, double stop)
    {
        Engine.renderer.arc(a, b, c, d, start, stop);
    }
    
    /**
     * See {@link Renderer#drawTexture(Texture, double, double, double, double, double, double, double, double)}
     */
    public static void drawTexture(Texture texture, double x1, double y1, double x2, double y2, double u1, double v1, double v2, double u2)
    {
        Engine.renderer.drawTexture(texture, x1, y1, x2, y2, u1, v1, u2, v2);
    }
    
    /**
     * See {@link Renderer#texture(Texture, double, double, double, double, double, double, double, double)}
     */
    public static void texture(Texture texture, double a, double b, double c, double d, double u1, double v1, double v2, double u2)
    {
        Engine.renderer.texture(texture, a, b, c, d, u1, v1, v2, u2);
    }
    
    /**
     * See {@link Renderer#texture(Texture, double, double, double, double, double, double)}
     */
    public static void texture(Texture t, double x, double y, double u1, double v1, double v2, double u2)
    {
        Engine.renderer.texture(t, x, y, u1, v1, v2, u2);
    }
    
    /**
     * See {@link Renderer#texture(Texture, double, double, double, double)}
     */
    public static void texture(Texture t, double a, double b, double c, double d)
    {
        Engine.renderer.texture(t, a, b, c, d);
    }
    
    /**
     * See {@link Renderer#texture(Texture, double, double)}
     */
    public static void texture(Texture t, double x, double y)
    {
        Engine.renderer.texture(t, x, y);
    }
    
    /**
     * See {@link Renderer#drawText(String, double, double)}
     */
    public static void drawText(String text, double x, double y)
    {
        Engine.renderer.drawText(text, x, y);
    }
    
    /**
     * See {@link Renderer#text(String, double, double, double, double)}
     */
    public static void text(String text, double a, double b, double c, double d)
    {
        Engine.renderer.text(text, a, b, c, d);
    }
    
    /**
     * See {@link Renderer#text(String, double, double)}
     */
    public static void text(String text, double x, double y)
    {
        Engine.renderer.text(text, x, y);
    }
    
    /**
     * See {@link Renderer#loadPixels()}
     */
    public static int[] loadPixels()
    {
        return Engine.renderer.loadPixels();
    }
    
    /**
     * See {@link Renderer#updatePixels()}
     */
    public static void updatePixels()
    {
        Engine.renderer.updatePixels();
    }
    
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
    
    /**
     * This method is called once the engine's environment is full setup. This is only called once.
     * <p>
     * This is where you call {@link #size} to enable rendering. At which point you can create textures and render to them.
     */
    protected void setup() { }
    
    /**
     * This method is called once per frame.
     *
     * @param elapsedTime The time in seconds since the last frame.
     */
    protected void draw(double elapsedTime) { }
    
    /**
     * This method is called after the render loop has exited for any reason, exception or otherwise. This is only called once.
     */
    protected void destroy() { }
}
