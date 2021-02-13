package engine;

import engine.color.Blend;
import engine.color.Color;
import engine.color.Colorc;
import engine.event.*;
import engine.font.Font;
import engine.render.*;
import engine.render.gl.GLConst;
import engine.render.gl.GLShader;
import engine.render.gl.GLVertexArray;
import engine.util.Random;
import engine.util.SimplexNoise;
import engine.util.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.*;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFWGamepadState;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.APIUtil;
import org.lwjgl.system.Callback;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import rutils.Logger;
import rutils.group.Pair;
import rutils.group.Triple;
import rutils.profiler.Profiler;
import rutils.profiler.Section;
import rutils.profiler.SectionData;

import java.lang.Math;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.SynchronousQueue;
import java.util.function.Supplier;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL43.*;
import static org.lwjgl.stb.STBEasyFont.*;
import static org.lwjgl.stb.STBImageWrite.stbi_write_png;
import static rutils.StringUtil.getCurrentDateTimeString;

@SuppressWarnings({"EmptyMethod", "unused"})
public class Engine
{
    private static final Logger LOGGER = new Logger();
    
    protected static Engine  logic;
    protected static boolean running;
    protected static long    startTime;
    
    protected static final HashMap<String, Extension> extensions = new LinkedHashMap<>();
    
    protected static long frameRate;
    protected static long frameCount;
    
    protected static final Vector2i screenSize = new Vector2i();
    protected static final Vector2i pixelSize  = new Vector2i();
    protected static final Vector2i viewPos    = new Vector2i();
    protected static final Vector2i viewSize   = new Vector2i();
    
    protected static Random random;
    
    protected static Noise valueNoise;
    protected static Noise perlinNoise;
    protected static Noise simplexNoise;
    protected static Noise openSimplexNoise;
    protected static Noise worleyNoise;
    protected static Noise noise;
    
    protected static final Map<Long, Monitor> monitors       = new LinkedHashMap<>();
    protected static       Monitor            primaryMonitor = null;
    
    protected static Window window;
    
    protected static Mouse    mouse;
    protected static Keyboard keyboard;
    
    protected static final Map<Integer, Joystick> joysticks = new LinkedHashMap<>();
    
    // -------------------- Joystick Callback Emulation -------------------- //
    private static final float[][] JOYSTICK_AXIS_STATES   = new float[GLFW_JOYSTICK_LAST][];
    private static final byte[][]  JOYSTICK_BUTTON_STATES = new byte[GLFW_JOYSTICK_LAST][];
    private static final byte[][]  JOYSTICK_HAT_STATES    = new byte[GLFW_JOYSTICK_LAST][];
    // -------------------- Joystick Callback Emulation -------------------- //
    
    protected static Renderer renderer;
    
    protected static int           layerCount;
    protected static Texture[]     layers;
    protected static boolean[]     activeLayers;
    protected static GLShader      screenShader;
    protected static GLVertexArray screenVAO;
    
    protected static GLShader      debugShader;
    protected static GLVertexArray debugTextVAO;
    protected static GLVertexArray debugBoxVAO;
    protected static Matrix4d      debugView;
    
    protected static final Profiler profiler = Profiler.get("engine");
    
    protected static final Color debugLineText       = new Color();
    protected static final Color debugLineBackground = new Color();
    
    protected static final List<Triple<Integer, Integer, String>> debugLines = new ArrayList<>();
    
    protected static long    titleFrequency;
    protected static boolean debug;
    protected static String  notification;
    protected static long    notificationTime;
    protected static long    notificationDuration;
    
    protected static long              profilerFrequency;
    protected static int               profilerMode;
    protected static String            profilerParent;
    protected static List<SectionData> profilerData;
    
    protected static boolean paused;
    
    protected static String screenshot;
    
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
     *         start(new EngineClass(), Level.FINE);
     *     }
     * }}
     * </pre>
     *
     * @param logic The engine instance to use.
     */
    @SafeVarargs
    protected static void start(@NotNull Engine logic, @NotNull Class<? extends Extension>... extensions)
    {
        try
        {
            Engine.LOGGER.info("Engine Started");
        
            if (Engine.logic != null) throw new RuntimeException("start can only be called once");
        
            Engine.logic     = logic;
            Engine.running   = true;
            Engine.startTime = System.nanoTime();
        
            Engine.LOGGER.finest("Loading Config Values");
            Engine.layerCount = Config.LAYER_COUNT.get();
            Engine.debugLineText.fromHex(Config.DEBUG_TEXT_COLOR.get());
            Engine.debugLineBackground.fromHex(Config.DEBUG_BACKGROUND_COLOR.get());
            Engine.notificationDuration = (long) (1_000_000_000L * Config.NOTIFICATION_DURATION.get());
            Engine.profilerFrequency    = 1_000_000_000L / Config.PROFILER_FREQUENCY.get();
            Engine.titleFrequency       = 1_000_000_000L / Config.TITLE_FREQUENCY.get();
        
            Engine.LOGGER.finer("[GLFW] Init");
            if (!glfwInit()) throw new IllegalStateException("Unable to initialize GLFW");
        
            EventBus.start();
            EventBus.register(Engine.logic);
        
            Engine.LOGGER.finest("[GLFW] Setting Callbacks");
            glfwSetErrorCallback(Engine::errorCallback);
            glfwSetMonitorCallback(Engine::monitorCallback);
            glfwSetJoystickCallback(Engine::joystickCallback);
        
            Engine.LOGGER.finest("[GLFW] Building Monitor Objects");
            PointerBuffer monitors = Objects.requireNonNull(glfwGetMonitors(), "No monitors found.");
            for (int i = 0, n = monitors.remaining(); i < n; i++)
            {
                long handle = monitors.get();
                Engine.monitors.put(handle, new Monitor(handle, i));
            }
            Engine.primaryMonitor = Engine.monitors.get(glfwGetPrimaryMonitor());
        
            Engine.LOGGER.info("Loading Extensions");
            for (Class<? extends Extension> ext : extensions)
            {
                try
                {
                    String    name = ext.getSimpleName();
                    Extension extInstance;
                    try
                    {
                        extInstance = (Extension) ext.getField("INSTANCE").get(ext);
                        Engine.LOGGER.fine("Using %s.INSTANCE", name);
                    }
                    catch (NoSuchFieldException ignored)
                    {
                        Engine.LOGGER.fine("Extension instance created for %s", name);
                        extInstance = ext.getConstructor().newInstance();
                    }
                    Engine.extensions.put(name, extInstance);
                    Engine.LOGGER.info("Loaded: %s", name);
                }
                catch (ReflectiveOperationException ignored) { }
            }
        
            Engine.random = new Random();
        
            Engine.valueNoise       = new ValueNoise();
            Engine.perlinNoise      = new PerlinNoise();
            Engine.simplexNoise     = new SimplexNoise();
            Engine.openSimplexNoise = new OpenSimplexNoise();
            Engine.worleyNoise      = new WorleyNoise();
        
            Engine.noise = Engine.perlinNoise;
        
            Engine.LOGGER.fine("Extension Pre Setup");
            for (String name : Engine.extensions.keySet())
            {
                Engine.LOGGER.finer("Extension:", name);
                Engine.extensions.get(name).beforeSetup();
            }
        
            Engine.LOGGER.fine("User Initialization");
            Engine.logic.setup();
            
            if (Engine.window != null)
            {
                Engine.LOGGER.fine("Extension Post Setup");
                for (String name : Engine.extensions.keySet())
                {
                    Engine.LOGGER.finer("Extension:", name);
                    Engine.extensions.get(name).afterSetup();
                }
                
                Engine.window.unmakeCurrent();
                
                final CountDownLatch latch = new CountDownLatch(1);
                
                new Thread(() -> {
                    try
                    {
                        Engine.window.makeCurrent();
                        GL.createCapabilities();
                        
                        long t, dt;
                        long lastFrame   = nanoseconds();
                        long lastProfile = nanoseconds();
                        long lastTitle   = 0;
                        
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
                                
                                Engine.profiler.startFrame();
                                {
                                    try (Section events = Engine.profiler.startSection("Events"))
                                    {
                                        try (Section mouse = Engine.profiler.startSection("Mouse"))
                                        {
                                            Engine.mouse.postEvents(t, dt);
                                        }
                                        
                                        try (Section keyboard = Engine.profiler.startSection("Keyboard"))
                                        {
                                            Engine.keyboard.postEvents(t, dt);
                                        }
                                        
                                        try (Section joysticks = Engine.profiler.startSection("Joysticks"))
                                        {
                                            for (Joystick joystick : Engine.joysticks.values()) joystick.postEvents(t, dt);
                                        }
                                        
                                        try (Section window = Engine.profiler.startSection("Window"))
                                        {
                                            Engine.window.postEvents(t, dt);
                                        }
                                        
                                        try (Section internal = Engine.profiler.startSection("Internal"))
                                        {
                                            if (Engine.keyboard.down(Keyboard.Key.F1) && Modifier.testExclusive(Modifier.CONTROL, Modifier.ALT, Modifier.SHIFT))
                                            {
                                                Engine.profilerData = null;
                                                Engine.profiler.enabled(false);
                                                Engine.profilerMode = 0;
                                                notification("Profile Mode: Off");
                                            }
                                            if (Engine.keyboard.down(Keyboard.Key.F2) && Modifier.testExclusive(Modifier.CONTROL, Modifier.ALT, Modifier.SHIFT))
                                            {
                                                Engine.profiler.enabled(true);
                                                Engine.profilerMode = 1;
                                                notification("Profile Mode: Average");
                                            }
                                            if (Engine.keyboard.down(Keyboard.Key.F3) && Modifier.testExclusive(Modifier.CONTROL, Modifier.ALT, Modifier.SHIFT))
                                            {
                                                Engine.profiler.enabled(true);
                                                Engine.profilerMode = 2;
                                                notification("Profile Mode: Min");
                                            }
                                            if (Engine.keyboard.down(Keyboard.Key.F4) && Modifier.testExclusive(Modifier.CONTROL, Modifier.ALT, Modifier.SHIFT))
                                            {
                                                Engine.profiler.enabled(true);
                                                Engine.profilerMode = 3;
                                                notification("Profile Mode: Max");
                                            }
                                            if (Engine.keyboard.down(Keyboard.Key.F5) && Modifier.testExclusive(Modifier.CONTROL, Modifier.ALT, Modifier.SHIFT))
                                            {
                                                Engine.profiler.enabled(true);
                                                Engine.profilerMode = 4;
                                                notification("Profile Mode: Real-Time");
                                            }
                                            if (Engine.keyboard.down(Keyboard.Key.F10) && Modifier.testExclusive(Modifier.CONTROL, Modifier.ALT, Modifier.SHIFT))
                                            {
                                                Engine.renderer.debug(!Engine.renderer.debug());
                                                notification(Engine.renderer.debug() ? "Renderer Debug Mode: On" : "Renderer Debug Mode: Off");
                                            }
                                            if (Engine.keyboard.down(Keyboard.Key.F11) && Modifier.testExclusive(Modifier.CONTROL, Modifier.ALT, Modifier.SHIFT))
                                            {
                                                Engine.debug = !Engine.debug;
                                                notification(Engine.debug ? "Debug Mode: On" : "Debug Mode: Off");
                                            }
                                            if (Engine.keyboard.down(Keyboard.Key.F12) && Modifier.testExclusive(Modifier.CONTROL, Modifier.ALT, Modifier.SHIFT))
                                            {
                                                Engine.paused = !Engine.paused;
                                                notification(Engine.paused ? "Engine Paused" : "Engine Unpaused");
                                            }
                                            if (Engine.profilerMode > 0 && Engine.profilerData != null)
                                            {
                                                for (int i = 1; i < 9; i++)
                                                {
                                                    if (Engine.keyboard.down(Keyboard.Key.get("K" + i)) && Engine.profilerData.size() > i)
                                                    {
                                                        String name = Engine.profilerData.get(i).name;
                                                        if (name.contains(".")) name = name.substring(name.lastIndexOf(".") + 1);
                                                        Engine.profilerParent = (Engine.profilerParent != null ? Engine.profilerParent + "." : "") + name;
                                                    }
                                                }
                                                if (Engine.keyboard.down(Keyboard.Key.K0) && Engine.profilerParent != null)
                                                {
                                                    if (Engine.profilerParent.contains("."))
                                                    {
                                                        Engine.profilerParent = Engine.profilerParent.substring(0, Engine.profilerParent.lastIndexOf("."));
                                                    }
                                                    else
                                                    {
                                                        Engine.profilerParent = null;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    
                                    try (Section draw = Engine.profiler.startSection("Draw"))
                                    {
                                        if (!Engine.paused)
                                        {
                                            try (Section start = Engine.profiler.startSection("Start"))
                                            {
                                                Engine.renderer.start();
                                            }
                                            
                                            try (Section extensionPreDraw = Engine.profiler.startSection("Extension Pre Draw"))
                                            {
                                                Engine.LOGGER.finer("Extension Pre Draw");
                                                for (String name : Engine.extensions.keySet())
                                                {
                                                    Extension extension = Engine.extensions.get(name);
                                                    if (extension.enabled())
                                                    {
                                                        try (Section ext = Engine.profiler.startSection(name))
                                                        {
                                                            Engine.renderer.push();
                                                            extension.beforeDraw(dt / 1_000_000_000D);
                                                            Engine.renderer.pop();
                                                        }
                                                    }
                                                }
                                            }
                                            
                                            try (Section user = Engine.profiler.startSection("User"))
                                            {
                                                Engine.LOGGER.finer("User Draw");
                                                Engine.renderer.push();
                                                Engine.logic.draw(dt / 1_000_000_000D);
                                                Engine.renderer.pop();
                                            }
                                            
                                            try (Section extensionPostDraw = Engine.profiler.startSection("Extension Post Draw"))
                                            {
                                                Engine.LOGGER.finer("Extension Post Draw");
                                                for (String name : Engine.extensions.keySet())
                                                {
                                                    Extension extension = Engine.extensions.get(name);
                                                    if (extension.enabled())
                                                    {
                                                        try (Section ext = Engine.profiler.startSection(name))
                                                        {
                                                            Engine.renderer.push();
                                                            extension.afterDraw(dt / 1_000_000_000D);
                                                            Engine.renderer.pop();
                                                        }
                                                    }
                                                }
                                            }
                                            
                                            try (Section finish = Engine.profiler.startSection("Finish"))
                                            {
                                                Engine.renderer.finish();
                                            }
                                        }
                                        
                                        try (Section viewport = Engine.profiler.startSection("Viewport"))
                                        {
                                            // TODO - Flickering when Maximizes
                                            double aspect = (double) (Engine.screenSize.x * Engine.pixelSize.x) / (double) (Engine.screenSize.y * Engine.pixelSize.y);
                                            
                                            int frameWidth  = Engine.window.framebufferWidth();
                                            int frameHeight = Engine.window.framebufferHeight();
                                            
                                            Engine.viewSize.set(frameWidth, (int) (frameWidth / aspect));
                                            if (Engine.viewSize.y > frameHeight) Engine.viewSize.set((int) (frameHeight * aspect), frameHeight);
                                            Engine.viewPos.set((frameWidth - Engine.viewSize.x) >> 1, (frameHeight - Engine.viewSize.y) >> 1);
                                            
                                            Engine.pixelSize.x = Math.max(Engine.viewSize.x / Engine.screenSize.x, 1);
                                            Engine.pixelSize.y = Math.max(Engine.viewSize.y / Engine.screenSize.y, 1);
                                            
                                            glViewport(Engine.viewPos.x, Engine.viewPos.y, Engine.viewSize.x, Engine.viewSize.y);
                                        }
                                        
                                        try (Section viewport = Engine.profiler.startSection("Layers"))
                                        {
                                            Engine.screenShader.bind();
                                            Engine.screenVAO.bind();
                                            for (int i = 0; i < Engine.layerCount; i++)
                                            {
                                                if (Engine.activeLayers[i])
                                                {
                                                    Engine.layers[i].bindTexture().unbindFramebuffer();
                                                    Engine.screenVAO.draw(GLConst.QUADS);
                                                }
                                            }
                                            Engine.screenShader.unbind();
                                            Engine.screenVAO.unbind();
                                        }
                                    }
                                    
                                    try (Section debug = Engine.profiler.startSection("Debug"))
                                    {
                                        dt = t - Engine.notificationTime;
                                        if (dt < Engine.notificationDuration && Engine.notification != null)
                                        {
                                            int x = (Engine.viewSize.x - stb_easy_font_width(Engine.notification)) >> 1;
                                            int y = (Engine.viewSize.y - stb_easy_font_height(Engine.notification)) >> 1;
                                            
                                            drawDebugText(x, y, Engine.notification);
                                        }
                                        if (Engine.debug)
                                        {
                                            drawDebugText(0, 0, "Frame: " + Engine.frameCount);
                                        }
                                        if (Engine.profilerData != null && Engine.profilerData.size() > 0)
                                        {
                                            int nameLength = 0;
                                            for (SectionData data : Engine.profilerData) nameLength = Math.max(nameLength, data.name.length());
                                            
                                            int y = Engine.viewSize.y - stb_easy_font_height(" " + "\n".repeat(Engine.profilerData.size() - 1) + " ");
                                            for (int i = 0, n = Engine.profilerData.size(); i < n; i++)
                                            {
                                                SectionData data = Engine.profilerData.get(i);
                                                String      line = String.format("%s %-20s %s", i, data.name, data.valueString());
                                                drawDebugText(0, y, i + "");
                                                drawDebugText(20, y, data.name);
                                                drawDebugText(20 + nameLength * 8, y, data.valueString());
                                                
                                                y += stb_easy_font_height(line);
                                            }
                                        }
                                        
                                        if (Engine.debugLines.size() > 0)
                                        {
                                            try (Section text = Engine.profiler.startSection("Text"))
                                            {
                                                Engine.debugShader.bind();
    
                                                // Engine.debugShader.setUniform("pv", Engine.debugView.setOrtho(0, this.fbSize.x, this.fbSize.y, 0, -1F, 1F));
                                                // Engine.window().viewMatrix()
    
                                                if (!Engine.debugLines.isEmpty())
                                                {
                                                    try (MemoryStack stack = MemoryStack.stackPush())
                                                    {
                                                        ByteBuffer  charBuffer = stack.malloc(Engine.debugTextVAO.bufferSize());
                                                        FloatBuffer boxBuffer  = stack.malloc(Engine.debugBoxVAO.bufferSize()).asFloatBuffer();
                                                        for (Triple<Integer, Integer, String> line : Engine.debugLines)
                                                        {
                                                            int quads = stb_easy_font_print(line.a + 2, line.b + 2, line.c, null, charBuffer.clear());
                                                            
                                                            float x1 = line.a;
                                                            float y1 = line.b;
                                                            float x2 = line.a + stb_easy_font_width(line.c) + 2;
                                                            float y2 = line.b + stb_easy_font_height(line.c);
                                                            
                                                            boxBuffer.put(0, x1);
                                                            boxBuffer.put(1, y1);
                                                            boxBuffer.put(2, x2);
                                                            boxBuffer.put(3, y1);
                                                            boxBuffer.put(4, x2);
                                                            boxBuffer.put(5, y2);
                                                            boxBuffer.put(6, x1);
                                                            boxBuffer.put(7, y2);
                                                            
                                                            Engine.debugShader.setUniform("color", Engine.debugLineBackground);
                                                            Engine.debugBoxVAO.bind().set(boxBuffer).draw(GLConst.QUADS).unbind();
                                                            
                                                            Engine.debugShader.setUniform("color", Engine.debugLineText);
                                                            Engine.debugTextVAO.bind().set(charBuffer).draw(GLConst.QUADS, quads * 4).unbind();
                                                        }
                                                    }
                                                    Engine.debugLines.clear();
                                                }
                                            }
                                        }
                                    }
                                    
                                    try (Section swap = Engine.profiler.startSection("Swap"))
                                    {
                                        Engine.window.swap();
                                    }
                                    
                                    frameTime = nanoseconds() - t;
                                    if (!Engine.paused)
                                    {
                                        minTime = Math.min(minTime, frameTime);
                                        maxTime = Math.max(maxTime, frameTime);
                                        totalTime += frameTime;
                                        totalFrames++;
                                        Engine.frameCount++;
                                    }
                                }
                                Engine.profiler.endFrame();
                            }
                            
                            dt = t - lastProfile;
                            if ((Engine.profilerMode == 4 || dt >= Engine.profilerFrequency) && !Engine.paused)
                            {
                                lastProfile = t;
                                
                                switch (Engine.profilerMode)
                                {
                                    case 0 -> Engine.profilerData = null;
                                    case 1 -> Engine.profilerData = Engine.profiler.getAverageData(Engine.profilerParent);
                                    case 2, 4 -> Engine.profilerData = Engine.profiler.getMinData(Engine.profilerParent);
                                    case 3 -> Engine.profilerData = Engine.profiler.getMaxData(Engine.profilerParent);
                                }
                                Engine.profiler.clear();
                            }
                            
                            if (Engine.screenshot != null)
                            {
                                String fileName = Engine.screenshot + (!Engine.screenshot.endsWith(".png") ? ".png" : "");
                                
                                int w = Engine.viewSize.x;
                                int h = Engine.viewSize.y;
                                int c = 3;
                                
                                int stride = w * c;
                                
                                ByteBuffer buf = MemoryUtil.memAlloc(w * h * c);
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
                                MemoryUtil.memFree(buf);
                                
                                Engine.screenshot = null;
                            }
                            
                            dt = t - lastProfile;
                            if ((Engine.profilerMode == 4 || dt >= Engine.profilerFrequency) && !Engine.paused)
                            {
                                lastProfile = t;
                                
                                switch (Engine.profilerMode)
                                {
                                    case 0 -> Engine.profilerData = null;
                                    case 1 -> Engine.profilerData = Engine.profiler.getAverageData(Engine.profilerParent);
                                    case 2, 4 -> Engine.profilerData = Engine.profiler.getMinData(Engine.profilerParent);
                                    case 3 -> Engine.profilerData = Engine.profiler.getMaxData(Engine.profilerParent);
                                }
                                Engine.profiler.clear();
                            }
                            
                            dt = t - lastTitle;
                            if (dt >= Engine.titleFrequency && totalFrames > 0 && !Engine.paused)
                            {
                                lastTitle = t;
    
                                totalTime /= totalFrames;
    
                                String format = "Engine - %s - FPS(%s) SPF(Avg: %s us, Min: %s us, Max: %s us)";
    
                                Engine.window.title(String.format(format, Engine.logic.name, totalFrames, totalTime / 1000D, minTime / 1000D, maxTime / 1000D));
    
                                totalTime = 0;
    
                                minTime = Long.MAX_VALUE;
                                maxTime = Long.MIN_VALUE;
    
                                totalFrames = 0;
                            }
                        }
                    }
                    catch (Exception e)
                    {
                        Engine.LOGGER.severe(e);
                    }
                    finally
                    {
                        Engine.window.unmakeCurrent();
                        GL.destroy();
                        GL.setCapabilities(null);
                        
                        Engine.running = false;
                        
                        latch.countDown();
                    }
                }, "render").start();
                
                if (Engine.window != null)
                {
                    while (Engine.running)
                    {
                        glfwPollEvents();
                        
                        // -------------------- Joystick Callback Emulation -------------------- //
                        {
                            for (int jid = GLFW_JOYSTICK_1; jid < GLFW_JOYSTICK_LAST; jid++)
                            {
                                int n;
                                
                                if (glfwJoystickPresent(jid))
                                {
                                    FloatBuffer axes    = null;
                                    ByteBuffer  buttons = null;
                                    ByteBuffer  hats    = glfwGetJoystickHats(jid);
                                    
                                    if (!glfwJoystickIsGamepad(jid))
                                    {
                                        axes    = glfwGetJoystickAxes(jid);
                                        buttons = glfwGetJoystickButtons(jid);
                                    }
                                    else
                                    {
                                        try (MemoryStack stack = MemoryStack.stackPush())
                                        {
                                            GLFWGamepadState state = GLFWGamepadState.mallocStack(stack);
                                            
                                            if (glfwGetGamepadState(jid, state))
                                            {
                                                axes    = state.axes();
                                                buttons = state.buttons();
                                            }
                                        }
                                    }
                                    
                                    if (axes != null)
                                    {
                                        n = axes.remaining();
                                        if (Engine.JOYSTICK_AXIS_STATES[jid].length != n) Engine.JOYSTICK_AXIS_STATES[jid] = new float[n];
                                        for (int axis = 0; axis < n; axis++)
                                        {
                                            float newValue = axes.get(axis);
                                            if (Float.compare(Engine.JOYSTICK_AXIS_STATES[jid][axis], newValue) != 0)
                                            {
                                                Engine.JOYSTICK_AXIS_STATES[jid][axis] = newValue;
                                                if (!glfwJoystickPresent(jid)) break;
                                                joystickAxisCallback(jid, axis, Engine.JOYSTICK_AXIS_STATES[jid][axis]);
                                            }
                                        }
                                    }
                                    if (buttons != null)
                                    {
                                        n = buttons.remaining();
                                        if (Engine.JOYSTICK_BUTTON_STATES[jid].length != n) Engine.JOYSTICK_BUTTON_STATES[jid] = new byte[n];
                                        for (int button = 0; button < n; button++)
                                        {
                                            byte newValue = buttons.get(button);
                                            if (Float.compare(Engine.JOYSTICK_BUTTON_STATES[jid][button], newValue) != 0)
                                            {
                                                Engine.JOYSTICK_BUTTON_STATES[jid][button] = newValue;
                                                if (!glfwJoystickPresent(jid)) break;
                                                joystickButtonCallback(jid, button, Engine.JOYSTICK_BUTTON_STATES[jid][button]);
                                            }
                                        }
                                    }
                                    if (hats != null)
                                    {
                                        n = hats.remaining();
                                        if (Engine.JOYSTICK_HAT_STATES[jid].length != n) Engine.JOYSTICK_HAT_STATES[jid] = new byte[n];
                                        for (int hat = 0; hat < n; hat++)
                                        {
                                            byte newValue = hats.get(hat);
                                            if (Float.compare(Engine.JOYSTICK_HAT_STATES[jid][hat], newValue) != 0)
                                            {
                                                Engine.JOYSTICK_HAT_STATES[jid][hat] = newValue;
                                                if (!glfwJoystickPresent(jid)) break;
                                                joystickHatCallback(jid, hat, Engine.JOYSTICK_HAT_STATES[jid][hat]);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        // -------------------- Joystick Callback Emulation -------------------- //
                        
                        runTasks();
                        
                        Thread.yield();
                    }
                    latch.await();
                }
            }
        }
        catch (Exception e)
        {
            Engine.LOGGER.severe(e);
        }
        finally
        {
            Engine.LOGGER.fine("Extension Pre Destruction");
            for (String name : Engine.extensions.keySet())
            {
                Engine.LOGGER.finer("Extension:", name);
                Engine.extensions.get(name).beforeDestroy();
            }
            
            Engine.LOGGER.fine("User Destruction");
            Engine.logic.destroy();
            
            Engine.LOGGER.fine("Extension Post Destruction");
            for (String name : Engine.extensions.keySet())
            {
                Engine.LOGGER.finer("Extension:", name);
                Engine.extensions.get(name).afterDestroy();
            }
            
            EventBus.shutdown();
    
            Engine.LOGGER.fine("[GLFW] Object Cleanup");
            Engine.monitors.clear();
            Engine.primaryMonitor = null;
    
            Engine.mouse    = null;
            Engine.keyboard = null;
    
            Engine.joysticks.clear();
    
            if (Engine.window != null) Engine.window.destroy();
    
            org.lwjgl.opengl.GL.destroy();
    
            Engine.LOGGER.finest("[GLFW] Callback Cleanup");
            Callback callback;
            if ((callback = glfwSetErrorCallback(null)) != null) callback.free();
            if ((callback = glfwSetMonitorCallback(null)) != null) callback.free();
            if ((callback = glfwSetJoystickCallback(null)) != null) callback.free();
    
            runTasks();
    
            Engine.LOGGER.finest("[GLFW] Termination");
            glfwTerminate();
        }
        
        Engine.LOGGER.info("Engine Finished");
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
     * @param screenW The width of the screen in drawable pixels.
     * @param screenH The height of the screen in drawable pixels.
     * @param pixelW  The width of the drawable pixels in actual pixels.
     * @param pixelH  The height of the drawable pixels in actual pixels.
     */
    public static void size(int screenW, int screenH, int pixelW, int pixelH)
    {
        Engine.screenSize.set(screenW, screenH);
        if (Engine.screenSize.x <= 0 || Engine.screenSize.y <= 0) throw new RuntimeException("Screen dimension must be > 0");
        Engine.LOGGER.fine("Screen Size:", Engine.screenSize);
    
        Engine.pixelSize.set(pixelW, pixelH);
        if (Engine.pixelSize.x <= 0 || Engine.pixelSize.y <= 0) throw new RuntimeException("Pixel dimension must be > 0");
        Engine.LOGGER.fine("Pixel Size:", Engine.pixelSize);
    
        Engine.LOGGER.finer("Window Creation");
        Engine.window = new Window();
    
        Engine.LOGGER.finer("Input Device Creation");
        Engine.mouse    = new Mouse();
        Engine.keyboard = new Keyboard();
    
        for (int jid = GLFW_JOYSTICK_1; jid < GLFW_JOYSTICK_LAST; jid++)
        {
            // -------------------- Joystick Callback Emulation -------------------- //
            {
                if (glfwJoystickIsGamepad(jid))
                {
                    try (MemoryStack stack = MemoryStack.stackPush())
                    {
                        GLFWGamepadState state = GLFWGamepadState.mallocStack(stack);
                        
                        if (glfwGetGamepadState(jid, state))
                        {
                            FloatBuffer axes = state.axes();
                            Engine.JOYSTICK_AXIS_STATES[jid] = new float[axes.remaining()];
                            
                            ByteBuffer buttons = state.buttons();
                            Engine.JOYSTICK_BUTTON_STATES[jid] = new byte[buttons.remaining()];
                        }
                        else
                        {
                            Engine.JOYSTICK_AXIS_STATES[jid]   = new float[0];
                            Engine.JOYSTICK_BUTTON_STATES[jid] = new byte[0];
                        }
                    }
                }
                else
                {
                    FloatBuffer axes = glfwGetJoystickAxes(jid);
                    Engine.JOYSTICK_AXIS_STATES[jid] = axes != null ? new float[axes.remaining()] : new float[0];
                    
                    ByteBuffer buttons = glfwGetJoystickButtons(jid);
                    Engine.JOYSTICK_BUTTON_STATES[jid] = buttons != null ? new byte[buttons.remaining()] : new byte[0];
                }
                
                ByteBuffer hats = glfwGetJoystickHats(jid);
                Engine.JOYSTICK_HAT_STATES[jid] = hats != null ? new byte[hats.remaining()] : new byte[0];
            }
            // -------------------- Joystick Callback Emulation -------------------- //
            
            if (glfwJoystickPresent(jid))
            {
                boolean isGamepad = glfwJoystickIsGamepad(jid);
                Engine.joysticks.put(jid, isGamepad ? new Gamepad(jid) : new Joystick(jid, false));
            }
        }
    
        Engine.LOGGER.finer("[GLFW] Attaching Window Callbacks");
        glfwSetWindowCloseCallback(Engine.window.handle, Engine::windowCloseCallback);
        glfwSetWindowFocusCallback(Engine.window.handle, Engine::windowFocusCallback);
        glfwSetWindowIconifyCallback(Engine.window.handle, Engine::windowIconifyCallback);
        glfwSetWindowMaximizeCallback(Engine.window.handle, Engine::windowMaximizeCallback);
        glfwSetWindowPosCallback(Engine.window.handle, Engine::windowPosCallback);
        glfwSetWindowSizeCallback(Engine.window.handle, Engine::windowSizeCallback);
        glfwSetWindowContentScaleCallback(Engine.window.handle, Engine::windowContentScaleCallback);
        glfwSetFramebufferSizeCallback(Engine.window.handle, Engine::framebufferSizeCallback);
        glfwSetWindowRefreshCallback(Engine.window.handle, Engine::windowRefreshCallback);
        glfwSetDropCallback(Engine.window.handle, Engine::dropCallback);
        
        glfwSetCursorEnterCallback(Engine.window.handle, Engine::mouseEnteredCallback);
        glfwSetCursorPosCallback(Engine.window.handle, Engine::mousePosCallback);
        glfwSetScrollCallback(Engine.window.handle, Engine::scrollCallback);
        glfwSetMouseButtonCallback(Engine.window.handle, Engine::mouseButtonCallback);
        
        glfwSetKeyCallback(Engine.window.handle, Engine::keyCallback);
        glfwSetCharCallback(Engine.window.handle, Engine::charCallback);
    
        Engine.LOGGER.finer("Initializing OpenGL Context");
        Engine.window.makeCurrent();
        
        glEnable(GL_TEXTURE_2D);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glBlendEquation(GL_FUNC_ADD);
        
        Engine.layers       = new Texture[Engine.layerCount];
        Engine.activeLayers = new boolean[Engine.layerCount];
        
        Engine.renderer        = new Renderer(Engine.layers[0] = new Texture(screenW, screenH));
        Engine.activeLayers[0] = true;
        
        Engine.screenShader = new GLShader().loadFile("shaders/pixel.vert").loadFile("shaders/pixel.frag").validate().unbind();
        Engine.screenVAO    = new GLVertexArray().bind().add(new float[] {-1.0F, 1.0F, -1.0F, -1.0F, 1.0F, -1.0F, 1.0F, 1.0F}, GLConst.DYNAMIC_DRAW, 2);
    
        Engine.debugShader  = new GLShader().loadFile("shaders/debug.vert").loadFile("shaders/debug.frag").validate().unbind();
        Engine.debugTextVAO = new GLVertexArray().bind().add((Float.BYTES * 3 + Byte.BYTES * 4) * 1024, GLConst.DYNAMIC_DRAW, GLConst.FLOAT, 3, GLConst.UNSIGNED_BYTE, 4).unbind();
        Engine.debugBoxVAO  = new GLVertexArray().bind().add((Float.BYTES * 2) * 8, GLConst.DYNAMIC_DRAW, GLConst.FLOAT, 2).unbind();
        Engine.debugView    = new Matrix4d();
    }
    
    /**
     * Sets the size of the window and uses the software renderer If this function is not called then the engine will not enter the render loop.
     * <p>
     * <b>THIS MUST ONLY BE CALLED ONCE</b>
     *
     * @param screenW The width of the screen in drawable pixels.
     * @param screenH The height of the screen in drawable pixels.
     */
    public static void size(int screenW, int screenH)
    {
        size(screenW, screenH, 4, 4);
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
    
    // ----------------------------
    // -- Render Loop Properties --
    // ----------------------------
    
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
    
    // -------------------------
    // -- Render Loop Objects --
    // -------------------------
    
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
    
    // -------------------
    // -- Debug Objects --
    // -------------------
    
    /**
     * @return The Engine's Profiler instance.
     */
    public static Profiler profiler()
    {
        return Engine.profiler;
    }
    
    // ---------------
    // -- Functions --
    // ---------------
    
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
    // -- Debug Functions --
    // ---------------------
    
    /**
     * Draws Debug text to the screen. The coordinates passed in will not be affected by any transformations.
     *
     * @param x    The x coordinate of the top left point if the text.
     * @param y    The y coordinate of the top left point if the text.
     * @param text The text to render.
     */
    public static void drawDebugText(int x, int y, String text)
    {
        Engine.debugLines.add(new Triple<>(x, y, text));
    }
    
    public static void notification(String notification)
    {
        Engine.notification     = notification;
        Engine.notificationTime = nanoseconds();
    }
    
    // ----------------
    // -- Task Stuff --
    // ----------------
    
    protected static final Deque<Pair<Runnable, Boolean>>          runTasks          = new ArrayDeque<>();
    protected static final Deque<Runnable>                         waitRunTasks      = new ArrayDeque<>();
    protected static final BlockingQueue<Pair<Integer, Exception>> waitRunResults    = new SynchronousQueue<>();
    protected static final Deque<Supplier<Object>>                 waitReturnTasks   = new ArrayDeque<>();
    protected static final BlockingQueue<Pair<Object, Exception>>  waitReturnResults = new SynchronousQueue<>();
    
    /**
     * Runs a task on the TaskDelegator's thread. Non-blocking.
     *
     * @param task          The task to complete.
     * @param passException If the TaskDelegator should pass the exception to the calling thread. If false the exception if effectively ignored.
     */
    public static void runTask(@NotNull Runnable task, boolean passException)
    {
        Engine.LOGGER.finest("Passing task to TaskDelegator, passException=%s", passException);
        
        if (Thread.currentThread().getName().equals("main"))
        {
            task.run();
            return;
        }
        
        Engine.runTasks.offer(new Pair<>(task, passException));
    }
    
    /**
     * Runs a task on the TaskDelegator's thread. Non-blocking.
     *
     * @param task The task to complete.
     */
    public static void runTask(@NotNull Runnable task)
    {
        runTask(task, false);
    }
    
    /**
     * Runs a task on the TaskDelegator's thread. Blocks until the task is completed.
     *
     * @param task The task to complete.
     */
    public static void waitRunTask(@NotNull Runnable task)
    {
        Engine.LOGGER.finest("Passing task to TaskDelegator and waiting for completion");
        
        if (Thread.currentThread().getName().equals("main"))
        {
            task.run();
            return;
        }
        
        Engine.waitRunTasks.offer(task);
        
        try
        {
            Pair<Integer, Exception> result = Engine.waitRunResults.take();
            if (result.a != 0) throw new RuntimeException(result.b);
        }
        catch (InterruptedException e)
        {
            Engine.LOGGER.warning("Run task was interrupted.");
        }
    }
    
    /**
     * Runs a supplier on the TaskDelegator's thread. Blocks until the task is completed.
     *
     * @param task The supplier to run.
     * @param <T>  The type of the supplier.
     * @return The supplied object or null if the thread was interrupted
     */
    @SuppressWarnings("unchecked")
    public static <T> @Nullable T waitReturnTask(@NotNull Supplier<T> task)
    {
        Engine.LOGGER.finest("Passing task to TaskDelegator and waiting for return");
        
        if (Thread.currentThread().getName().equals("main")) return task.get();
        
        Engine.waitReturnTasks.offer((Supplier<Object>) task);
        
        try
        {
            Pair<Object, Exception> result = Engine.waitReturnResults.take();
            if (result.b != null) throw new RuntimeException(result.b);
            return (T) result.a;
        }
        catch (InterruptedException e)
        {
            Engine.LOGGER.warning("Return task was interrupted.");
        }
        return null;
    }
    
    private static void runTasks()
    {
        while (!Engine.runTasks.isEmpty())
        {
            Pair<Runnable, Boolean> task = Engine.runTasks.poll();
            try
            {
                task.a.run();
            }
            catch (Exception e)
            {
                if (task.b)
                {
                    throw new RuntimeException(e);
                }
                else
                {
                    Engine.LOGGER.severe("An exception occurred while trying to run task.");
                    Engine.LOGGER.severe(e);
                }
            }
        }
        
        while (!Engine.waitRunTasks.isEmpty())
        {
            Runnable task = Engine.waitRunTasks.poll();
            
            int       result = 0;
            Exception except = null;
            try
            {
                task.run();
            }
            catch (Exception e)
            {
                result = 1;
                except = e;
            }
            Engine.waitRunResults.offer(new Pair<>(result, except));
        }
        
        while (!Engine.waitReturnTasks.isEmpty())
        {
            Supplier<Object> task = waitReturnTasks.poll();
            
            Object    result = null;
            Exception except = null;
            try
            {
                result = task.get();
            }
            catch (Exception e)
            {
                except = e;
            }
            Engine.waitReturnResults.offer(new Pair<>(result, except));
        }
    }
    
    // --------------------
    // -- Callback Stuff --
    // --------------------
    
    private static final Map<Integer, String> ERROR_CODES = APIUtil.apiClassTokens((field, value) -> 0x10000 < value && value < 0x20000, null, org.lwjgl.glfw.GLFW.class);
    
    private static void errorCallback(int error, long description)
    {
        StringBuilder message = new StringBuilder();
        message.append("[LWJGL] ").append(Engine.ERROR_CODES.get(error)).append(" error\n");
        message.append("\tDescription : ").append(MemoryUtil.memUTF8(description)).append('\n');
        message.append("\tStacktrace  :\n");
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        for (int i = 4; i < stack.length; i++) message.append("\t\t").append(stack[i].toString()).append('\n');
        Engine.LOGGER.severe(message.toString());
    }
    
    private static void monitorCallback(long handle, int event)
    {
        switch (event)
        {
            case GLFW_CONNECTED -> {
                Monitor monitor = new Monitor(handle, Engine.monitors.size());
                Engine.monitors.put(handle, monitor);
                EventBus.post(EventMonitorConnected.create(monitor));
            }
            case GLFW_DISCONNECTED -> {
                Monitor monitor = Engine.monitors.remove(handle);
                EventBus.post(EventMonitorDisconnected.create(monitor));
            }
        }
        Engine.primaryMonitor = Engine.monitors.get(glfwGetPrimaryMonitor());
    }
    
    private static void joystickCallback(int jid, int event)
    {
        switch (event)
        {
            case GLFW_CONNECTED -> {
                Joystick joystick = glfwJoystickIsGamepad(jid) ? new Gamepad(jid) : new Joystick(jid, false);
                Engine.joysticks.put(jid, joystick);
                EventBus.post(EventJoystickConnected.create(joystick));
            }
            case GLFW_DISCONNECTED -> {
                Joystick joystick = Engine.joysticks.remove(jid);
                EventBus.post(EventJoystickDisconnected.create(joystick));
            }
        }
    }
    
    private static void joystickAxisCallback(int jid, int axis, float value)
    {
        Joystick joystick = Engine.joysticks.get(jid);
        
        joystick.axisStateChanges.offer(new Pair<>(axis, value));
    }
    
    private static void joystickButtonCallback(int jid, int button, int action)
    {
        Joystick joystick = Engine.joysticks.get(jid);
        
        joystick.buttonStateChanges.offer(new Pair<>(button, action));
    }
    
    private static void joystickHatCallback(int jid, int hat, int action)
    {
        Joystick joystick = Engine.joysticks.get(jid);
        
        joystick.hatStateChanges.offer(new Pair<>(hat, action));
    }
    
    private static void windowCloseCallback(long handle)
    {
        Engine.window._close = true;
    }
    
    private static void windowFocusCallback(long handle, boolean focused)
    {
        Engine.window._focused = focused;
    }
    
    private static void windowIconifyCallback(long handle, boolean iconified)
    {
        Engine.window._iconified = iconified;
    }
    
    private static void windowMaximizeCallback(long handle, boolean maximized)
    {
        Engine.window._maximized = maximized;
    }
    
    private static void windowRefreshCallback(long handle)
    {
        Engine.window._refresh = true;
    }
    
    private static void windowPosCallback(long handle, int x, int y)
    {
        Engine.window._pos.set(x, y);
    }
    
    private static void windowSizeCallback(long handle, int width, int height)
    {
        Engine.window._size.set(width, height);
    }
    
    private static void windowContentScaleCallback(long handle, float xScale, float yScale)
    {
        Engine.window._scale.set(xScale, yScale);
    }
    
    private static void framebufferSizeCallback(long handle, int width, int height)
    {
        Engine.window._fbSize.set(width, height);
    }
    
    private static void dropCallback(long handle, int count, long names)
    {
        Engine.window._dropped = new String[count];
        PointerBuffer charPointers = MemoryUtil.memPointerBuffer(names, count);
        for (int i = 0; i < count; i++) Engine.window._dropped[i] = MemoryUtil.memUTF8(charPointers.get(i));
    }
    
    private static void mouseEnteredCallback(long handle, boolean entered)
    {
        Engine.mouse._entered = entered;
    }
    
    private static void mousePosCallback(long handle, double x, double y)
    {
        x = (x - Engine.viewPos.x) * (double) Engine.screenSize.x / (double) Engine.viewSize.x;
        y = (y - Engine.viewPos.y) * (double) Engine.screenSize.y / (double) Engine.viewSize.y;
        
        Engine.mouse._pos.set(x, y);
    }
    
    private static void scrollCallback(long handle, double dx, double dy)
    {
        Engine.mouse._scroll.add(dx, dy);
    }
    
    private static void mouseButtonCallback(long handle, int button, int action, int mods)
    {
        Engine.mouse.buttonStateChanges.offer(new Pair<>(Mouse.Button.get(button), action));
        
        Modifier.updateMods(mods);
    }
    
    private static void keyCallback(long handle, int key, int scancode, int action, int mods)
    {
        Engine.keyboard.keyStateChanges.offer(new Pair<>(Keyboard.Key.get(key, scancode), action));
        
        Modifier.updateMods(mods);
    }
    
    private static void charCallback(long handle, int codePoint)
    {
        Engine.keyboard._charChanges.offer(Character.toString(codePoint));
    }
    
    // ---------------------
    // -- Random Instance --
    // ---------------------
    
    /**
     * Gets the engine's random instance.
     *
     * @return The common random instance.
     */
    public static @NotNull Random random()
    {
        return Engine.random;
    }
    
    /**
     * See {@link Random#setSeed(long)}
     */
    public static void randomSeed(long seed)
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
     * See {@link Random#nextInts(int[])}
     */
    public static int[] nextInts(int[] ints)
    {
        return Engine.random.nextInts(ints);
    }
    
    /**
     * See {@link Random#nextInts(int[], int)}
     */
    public static int[] nextInts(int[] ints, int bound)
    {
        return Engine.random.nextInts(ints, bound);
    }
    
    /**
     * See {@link Random#nextInts(int[], int, int)}
     */
    public static int[] nextInts(int[] ints, int origin, int bound)
    {
        return Engine.random.nextInts(ints, origin, bound);
    }
    
    /**
     * See {@link Random#nextLongs(long[])}
     */
    public static long[] nextLongs(long[] longs)
    {
        return Engine.random.nextLongs(longs);
    }
    
    /**
     * See {@link Random#nextLongs(long[], long)}
     */
    public static long[] nextLongs(long[] longs, long bound)
    {
        return Engine.random.nextLongs(longs, bound);
    }
    
    /**
     * See {@link Random#nextLongs(long[], long, long)}
     */
    public static long[] nextLongs(long[] longs, long origin, long bound)
    {
        return Engine.random.nextLongs(longs, origin, bound);
    }
    
    /**
     * See {@link Random#nextFloats(float[])}
     */
    public static float[] nextFloats(float[] floats)
    {
        return Engine.random.nextFloats(floats);
    }
    
    /**
     * See {@link Random#nextFloats(float[], float)}
     */
    public static float[] nextFloats(float[] floats, float bound)
    {
        return Engine.random.nextFloats(floats, bound);
    }
    
    /**
     * See {@link Random#nextFloats(float[], float, float)}
     */
    public static float[] nextFloats(float[] floats, float origin, float bound)
    {
        return Engine.random.nextFloats(floats, origin, bound);
    }
    
    /**
     * See {@link Random#nextDoubles(double[])}
     */
    public static double[] nextDoubles(double[] doubles)
    {
        return Engine.random.nextDoubles(doubles);
    }
    
    /**
     * See {@link Random#nextDoubles(double[], double)}
     */
    public static double[] nextDoubles(double[] doubles, double bound)
    {
        return Engine.random.nextDoubles(doubles, bound);
    }
    
    /**
     * See {@link Random#nextDoubles(double[], double, double)}
     */
    public static double[] nextDoubles(double[] doubles, double origin, double bound)
    {
        return Engine.random.nextDoubles(doubles, origin, bound);
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
     * See {@link Random#nextGaussian(double, double)}
     */
    public static double nextGaussian(double mean, double stdDev)
    {
        return Engine.random.nextGaussian(mean, stdDev);
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
     * See {@link Random#nextFrom(T...)}
     */
    @SafeVarargs
    public static <T> @Nullable T nextFrom(@Nullable T... array)
    {
        return Engine.random.nextFrom(array);
    }
    
    /**
     * See {@link Random#> T nextFrom(Collection<T> collection)}
     */
    public static <T> @Nullable T nextFrom(@Nullable Collection<T> collection)
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
     * See {@link Random#nextVector2d(double)}
     */
    public static Vector2d nextVector2d(double bound)
    {
        return Engine.random.nextVector2d(bound);
    }
    
    /**
     * See {@link Random#nextVector2d(double, double)}
     */
    public static Vector2d nextVector2d(double origin, double bound)
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
     * See {@link Random#nextVector3d(double)}
     */
    public static Vector3d nextVector3d(double bound)
    {
        return Engine.random.nextVector3d(bound);
    }
    
    /**
     * See {@link Random#nextVector3d(double, double)}
     */
    public static Vector3d nextVector3d(double origin, double bound)
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
     * See {@link Random#nextVector4d(double)}
     */
    public static Vector4d nextVector4d(double bound)
    {
        return Engine.random.nextVector4d(bound);
    }
    
    /**
     * See {@link Random#nextVector4d(double, double)}
     */
    public static Vector4d nextVector4d(double origin, double bound)
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
    
    // ----------------------
    // -- Noise Methods --
    // ----------------------
    
    /**
     * Gets the engine's noise instance.
     *
     * @return The common noise instance.
     */
    public static @NotNull Noise noise()
    {
        return Engine.noise;
    }
    
    public static void noise(String noise)
    {
        switch (noise)
        {
            case Noise.VALUE:
                Engine.noise = Engine.valueNoise;
            case Noise.PERLIN:
                Engine.noise = Engine.perlinNoise;
            case Noise.SIMPLEX:
                Engine.noise = Engine.simplexNoise;
            case Noise.OPEN_SIMPLEX:
                Engine.noise = Engine.openSimplexNoise;
            case Noise.WORLEY:
                Engine.noise = Engine.worleyNoise;
            default:
                Engine.noise = Engine.perlinNoise;
        }
    }
    
    /**
     * See {@link Noise#setSeed(long)}
     */
    public static void noiseSeed(long seed)
    {
        Engine.noise.setSeed(seed);
    }
    
    /**
     * See {@link Noise#octaves()}
     */
    public static int octaves()
    {
        return Engine.noise.octaves();
    }
    
    /**
     * See {@link Noise#octaves(int)}
     */
    public static void octaves(int octaves)
    {
        Engine.noise.octaves(octaves);
    }
    
    /**
     * See {@link Noise#persistence()}
     */
    public static double persistence()
    {
        return Engine.noise.persistence();
    }
    
    /**
     * See {@link Noise#persistence(double)}
     */
    public static void persistence(double persistence)
    {
        Engine.noise.persistence(persistence);
    }
    
    /**
     * See {@link Noise#setProperty(String, Object)}
     */
    public static void setProperty(String property, Object object)
    {
        Engine.noise.setProperty(property, object);
    }
    
    /**
     * See {@link Noise#noise(double...)}
     */
    public static double noise(double... coord)
    {
        return Engine.noise.noise(coord);
    }
    
    // ----------------------
    // -- Layer Methods --
    // ----------------------
    
    /**
     * @return The layer count.
     */
    public static int layerCount()
    {
        return Engine.layerCount;
    }
    
    /**
     * @return The current render layer.
     */
    public static int layer()
    {
        for (int i = 0; i < Engine.layerCount; i++)
        {
            if (Engine.renderer.target() == Engine.layers[i]) return i;
        }
        return -1;
    }
    
    /**
     * Sets the current layer. The layer must have been created first.
     *
     * @param layer The new layer.
     */
    public static void layer(int layer)
    {
        Engine.renderer.target(Engine.layers[layer]);
    }
    
    /**
     * Creates a new layer at the layer specified.
     *
     * @param layer  The layer number.
     * @param width  The layer width.
     * @param height The layer height.
     */
    public static void createLayer(int layer, int width, int height)
    {
        if (layer < 0 || Engine.layerCount <= layer) throw new RuntimeException("Invalid Layer: " + layer);
        
        if (layer == 0) throw new RuntimeException("Cannot overwrite default layer");
        
        boolean create = Engine.layers[layer] == null;
        
        Engine.layers[layer]       = new Texture(width, height);
        Engine.activeLayers[layer] = true;
        
        Engine.LOGGER.fine("Layer " + (create ? "Created" : "Overwritten") + ": " + layer);
    }
    
    /**
     * Creates a new layer at the next available layer.
     *
     * @param width  The layer width.
     * @param height The layer height.
     */
    public static void createLayer(int width, int height)
    {
        for (int i = 0; i < Engine.layerCount; i++)
        {
            if (Engine.layers[i] == null)
            {
                createLayer(i, width, height);
                return;
            }
        }
    }
    
    /**
     * Enables the layer to be drawn to the screen.
     *
     * @param layer The layer.
     */
    public static void enableLayer(int layer)
    {
        if (Engine.layers[layer] == null) throw new RuntimeException("Layer not created: " + layer);
        
        Engine.LOGGER.finest("Enabling Layer: " + layer);
        
        Engine.activeLayers[layer] = true;
    }
    
    /**
     * Disables the layer from being drawn to the screen.
     *
     * @param layer The layer.
     */
    public static void disableLayer(int layer)
    {
        if (Engine.layers[layer] == null) throw new RuntimeException("Layer not created: " + layer);
        
        Engine.LOGGER.finest("Disabling Layer: " + layer);
        
        Engine.activeLayers[layer] = false;
    }
    
    // -----------------------
    // -- Renderer Instance --
    // -----------------------
    
    /**
     * @return The engine's render instance. This should only be used for {@link Overloads} methods.
     */
    public static @NotNull Renderer renderer()
    {
        return Engine.renderer;
    }
    
    /**
     * See {@link Renderer#target()}
     */
    public static @NotNull Texture target()
    {
        return Engine.renderer.target();
    }
    
    /**
     * See {@link Renderer#target(Texture)}
     */
    public static void target(@NotNull Texture target)
    {
        Engine.profiler.startSection("target");
        Engine.renderer.target(target);
        Engine.profiler.endSection();
    }
    
    /**
     * See {@link Renderer#blend()}
     */
    public static @NotNull Blend blend()
    {
        return Engine.renderer.blend();
    }
    
    /**
     * See {@link Renderer#blend()}
     */
    public static boolean rendererDebug()
    {
        return Engine.renderer.debug();
    }
    
    /**
     * See {@link Renderer#debug(boolean)}
     */
    public static void rendererDebug(boolean debug)
    {
        Engine.renderer.debug(debug);
    }
    
    /**
     * See {@link Renderer#toggleDebug()}
     */
    public static void rendererToggleDebug()
    {
        Engine.renderer.toggleDebug();
    }
    
    /**
     * See {@link Renderer#fill()}
     */
    public static @NotNull Colorc fill()
    {
        return Engine.renderer.fill();
    }
    
    /**
     * See {@link Renderer#fill(Number, Number, Number, Number)}
     */
    public static void fill(@NotNull Number r, @NotNull Number g, @NotNull Number b, @NotNull Number a)
    {
        Engine.profiler.startSection("fill");
        Engine.renderer.fill(r, g, b, a);
        Engine.profiler.endSection();
    }
    
    /**
     * See {@link Renderer#fill(Number, Number, Number)}
     */
    public static void fill(@NotNull Number r, @NotNull Number g, @NotNull Number b)
    {
        Engine.profiler.startSection("fill");
        Engine.renderer.fill(r, g, b);
        Engine.profiler.endSection();
    }
    
    /**
     * See {@link Renderer#fill(Number, Number)}
     */
    public static void fill(@NotNull Number grey, @NotNull Number a)
    {
        Engine.profiler.startSection("fill");
        Engine.renderer.fill(grey, a);
        Engine.profiler.endSection();
    }
    
    /**
     * See {@link Renderer#fill(Number)}
     */
    public static void fill(@NotNull Number grey)
    {
        Engine.profiler.startSection("fill");
        Engine.renderer.fill(grey);
        Engine.profiler.endSection();
    }
    
    /**
     * See {@link Renderer#fill(Colorc)}
     */
    public static void fill(@NotNull Colorc fill)
    {
        Engine.profiler.startSection("fill");
        Engine.renderer.fill(fill);
        Engine.profiler.endSection();
    }
    
    /**
     * See {@link Renderer#noFill()}
     */
    public static void noFill()
    {
        Engine.profiler.startSection("noFill");
        Engine.renderer.noFill();
        Engine.profiler.endSection();
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
    public static void stroke(@NotNull Number r, @NotNull Number g, @NotNull Number b, @NotNull Number a)
    {
        Engine.profiler.startSection("stroke");
        Engine.renderer.stroke(r, g, b, a);
        Engine.profiler.endSection();
    }
    
    /**
     * See {@link Renderer#stroke(Number, Number, Number)}
     */
    public static void stroke(@NotNull Number r, @NotNull Number g, @NotNull Number b)
    {
        Engine.profiler.startSection("stroke");
        Engine.renderer.stroke(r, g, b);
        Engine.profiler.endSection();
    }
    
    /**
     * See {@link Renderer#stroke(Number, Number)}
     */
    public static void stroke(@NotNull Number grey, @NotNull Number a)
    {
        Engine.profiler.startSection("stroke");
        Engine.renderer.stroke(grey, a);
        Engine.profiler.endSection();
    }
    
    /**
     * See {@link Renderer#stroke(Number)}
     */
    public static void stroke(@NotNull Number grey)
    {
        Engine.profiler.startSection("stroke");
        Engine.renderer.stroke(grey);
        Engine.profiler.endSection();
    }
    
    /**
     * See {@link Renderer#stroke(Colorc)}
     */
    public static void stroke(@NotNull Colorc stroke)
    {
        Engine.profiler.startSection("stroke");
        Engine.renderer.stroke(stroke);
        Engine.profiler.endSection();
    }
    
    /**
     * See {@link Renderer#noStroke()}
     */
    public static void noStroke()
    {
        Engine.profiler.startSection("noStroke");
        Engine.renderer.noStroke();
        Engine.profiler.endSection();
    }
    
    /**
     * See {@link Renderer#tint()}
     */
    public static Colorc tint()
    {
        Engine.profiler.startSection("tint");
        return Engine.renderer.tint();
    }
    
    /**
     * See {@link Renderer#tint(Number, Number, Number, Number)}
     */
    public static void tint(@NotNull Number r, @NotNull Number g, @NotNull Number b, @NotNull Number a)
    {
        Engine.profiler.startSection("tint");
        Engine.renderer.tint(r, g, b, a);
        Engine.profiler.endSection();
    }
    
    /**
     * See {@link Renderer#tint(Number, Number, Number)}
     */
    public static void tint(@NotNull Number r, @NotNull Number g, @NotNull Number b)
    {
        Engine.profiler.startSection("tint");
        Engine.renderer.tint(r, g, b);
        Engine.profiler.endSection();
    }
    
    /**
     * See {@link Renderer#tint(Number, Number)}
     */
    public static void tint(@NotNull Number grey, @NotNull Number a)
    {
        Engine.profiler.startSection("tint");
        Engine.renderer.tint(grey, a);
        Engine.profiler.endSection();
    }
    
    /**
     * See {@link Renderer#tint(Number)}
     */
    public static void tint(@NotNull Number grey)
    {
        Engine.profiler.startSection("tint");
        Engine.renderer.tint(grey);
        Engine.profiler.endSection();
    }
    
    /**
     * See {@link Renderer#tint(Colorc)}
     */
    public static void tint(@NotNull Colorc tint)
    {
        Engine.profiler.startSection("tint");
        Engine.renderer.tint(tint);
        Engine.profiler.endSection();
    }
    
    /**
     * See {@link Renderer#noTint()}
     */
    public static void noTint()
    {
        Engine.profiler.startSection("noTint");
        Engine.renderer.noTint();
        Engine.profiler.endSection();
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
        Engine.profiler.startSection("weight");
        Engine.renderer.weight(weight);
        Engine.profiler.endSection();
    }
    
    /**
     * See {@link Renderer#rectMode()}
     */
    public static @NotNull RectMode rectMode()
    {
        return Engine.renderer.rectMode();
    }
    
    /**
     * See {@link Renderer#rectMode(RectMode)}
     */
    public static void rectMode(@NotNull RectMode rectMode)
    {
        Engine.profiler.startSection("rectMode");
        Engine.renderer.rectMode(rectMode);
        Engine.profiler.endSection();
    }
    
    /**
     * See {@link Renderer#ellipseMode()}
     */
    public static @NotNull EllipseMode ellipseMode()
    {
        return Engine.renderer.ellipseMode();
    }
    
    /**
     * See {@link Renderer#ellipseMode(EllipseMode)}
     */
    public static void ellipseMode(@NotNull EllipseMode ellipseMode)
    {
        Engine.profiler.startSection("ellipseMode");
        Engine.renderer.ellipseMode(ellipseMode);
        Engine.profiler.endSection();
    }
    
    /**
     * See {@link Renderer#arcMode()}
     */
    public static @NotNull ArcMode arcMode()
    {
        return Engine.renderer.arcMode();
    }
    
    /**
     * See {@link Renderer#arcMode(ArcMode)}
     */
    public static void arcMode(@NotNull ArcMode arcMode)
    {
        Engine.profiler.startSection("arcMode");
        Engine.renderer.arcMode(arcMode);
        Engine.profiler.endSection();
    }
    
    /**
     * See {@link Renderer#textFont()}
     */
    public static @NotNull Font textFont()
    {
        return Engine.renderer.textFont();
    }
    
    /**
     * See {@link Renderer#textFont(Font)}
     */
    public static void textFont(@NotNull Font font)
    {
        Engine.profiler.startSection("textFont");
        Engine.renderer.textFont(font);
        Engine.profiler.endSection();
    }
    
    /**
     * See {@link Renderer#textFont(String)}
     */
    public static void textFont(@NotNull String font)
    {
        Engine.profiler.startSection("textFont");
        Engine.renderer.textFont(font);
        Engine.profiler.endSection();
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
        Engine.profiler.startSection("textSize");
        Engine.renderer.textSize(textSize);
        Engine.profiler.endSection();
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
    public static @NotNull TextAlign textAlign()
    {
        return Engine.renderer.textAlign();
    }
    
    /**
     * See {@link Renderer#textAlign(TextAlign)}
     */
    public static void textAlign(@NotNull TextAlign textAlign)
    {
        Engine.profiler.startSection("textAlign");
        Engine.renderer.textAlign(textAlign);
        Engine.profiler.endSection();
    }
    
    /**
     * See {@link Renderer#identity()}
     */
    public static void identity()
    {
        Engine.profiler.startSection("identity");
        Engine.renderer.identity();
        Engine.profiler.endSection();
    }
    
    /**
     * See {@link Renderer#translate(double, double)}
     */
    public static void translate(double x, double y)
    {
        Engine.profiler.startSection("translate");
        Engine.renderer.translate(x, y);
        Engine.profiler.endSection();
    }
    
    /**
     * See {@link Renderer#rotate(double)}
     */
    public static void rotate(double angle)
    {
        Engine.profiler.startSection("rotate");
        Engine.renderer.rotate(angle);
        Engine.profiler.endSection();
    }
    
    /**
     * See {@link Renderer#scale(double, double)}
     */
    public static void scale(double x, double y)
    {
        Engine.profiler.startSection("scale");
        Engine.renderer.scale(x, y);
        Engine.profiler.endSection();
    }
    
    /**
     * See {@link Renderer#start()}
     */
    public static void start()
    {
        Engine.profiler.startSection("start");
        Engine.renderer.start();
        Engine.profiler.endSection();
    }
    
    /**
     * See {@link Renderer#finish()}
     */
    public static void finish()
    {
        Engine.profiler.startSection("finish");
        Engine.renderer.finish();
        Engine.profiler.endSection();
    }
    
    /**
     * See {@link Renderer#push()}
     */
    public static void push()
    {
        Engine.profiler.startSection("push");
        Engine.renderer.push();
        Engine.profiler.endSection();
    }
    
    /**
     * See {@link Renderer#pop()}
     */
    public static void pop()
    {
        Engine.profiler.startSection("pop");
        Engine.renderer.pop();
        Engine.profiler.endSection();
    }
    
    /**
     * See {@link Renderer#clear()}
     */
    public static void clear(@NotNull Number r, @NotNull Number g, @NotNull Number b, @NotNull Number a)
    {
        Engine.profiler.startSection("clear");
        Engine.renderer.clear(r, g, b, a);
        Engine.profiler.endSection();
    }
    
    /**
     * See {@link Renderer#clear()}
     */
    public static void clear(@NotNull Number r, @NotNull Number g, @NotNull Number b)
    {
        Engine.profiler.startSection("clear");
        Engine.renderer.clear(r, g, b);
        Engine.profiler.endSection();
    }
    
    /**
     * See {@link Renderer#clear()}
     */
    public static void clear(@NotNull Number grey, @NotNull Number a)
    {
        Engine.profiler.startSection("clear");
        Engine.renderer.clear(grey, a);
        Engine.profiler.endSection();
    }
    
    /**
     * See {@link Renderer#clear()}
     */
    public static void clear(@NotNull Number grey)
    {
        Engine.profiler.startSection("clear");
        Engine.renderer.clear(grey);
        Engine.profiler.endSection();
    }
    
    /**
     * See {@link Renderer#clear()}
     */
    public static void clear()
    {
        Engine.profiler.startSection("clear");
        Engine.renderer.clear();
        Engine.profiler.endSection();
    }
    
    /**
     * See {@link Renderer#clear()}
     */
    public static void clear(@NotNull Colorc color)
    {
        Engine.profiler.startSection("clear");
        Engine.renderer.clear(color);
        Engine.profiler.endSection();
    }
    
    /**
     * See {@link Renderer#drawPoint(double, double)}
     */
    public static void drawPoint(double x, double y)
    {
        Engine.profiler.startSection("drawPoint");
        Engine.renderer.drawPoint(x, y);
        Engine.profiler.endSection();
    }
    
    /**
     * See {@link Renderer#point(double, double)}
     */
    public static void point(double x, double y)
    {
        Engine.profiler.startSection("point");
        Engine.renderer.point(x, y);
        Engine.profiler.endSection();
    }
    
    /**
     * See {@link Renderer#drawLine(double, double, double, double)}
     */
    public static void drawLine(double x1, double y1, double x2, double y2)
    {
        Engine.profiler.startSection("drawLine");
        Engine.renderer.drawLine(x1, y1, x2, y2);
        Engine.profiler.endSection();
    }
    
    /**
     * See {@link Renderer#line(double, double, double, double)}
     */
    public static void line(double x1, double y1, double x2, double y2)
    {
        Engine.profiler.startSection("line");
        Engine.renderer.line(x1, y1, x2, y2);
        Engine.profiler.endSection();
    }
    
    /**
     * See {@link Renderer#drawBezier(double...)}
     */
    public static void drawBezier(double... points)
    {
        Engine.profiler.startSection("drawBezier");
        Engine.renderer.drawBezier(points);
        Engine.profiler.endSection();
    }
    
    /**
     * See {@link Renderer#bezier(double...)}
     */
    public static void bezier(double... points)
    {
        Engine.profiler.startSection("bezier");
        Engine.renderer.bezier(points);
        Engine.profiler.endSection();
    }
    
    /**
     * See {@link Renderer#drawTriangle(double, double, double, double, double, double)}
     */
    public static void drawTriangle(double x1, double y1, double x2, double y2, double x3, double y3)
    {
        Engine.profiler.startSection("drawTriangle");
        Engine.renderer.drawTriangle(x1, y1, x2, y2, x3, y3);
        Engine.profiler.endSection();
    }
    
    /**
     * See {@link Renderer#fillTriangle(double, double, double, double, double, double)}
     */
    public static void fillTriangle(double x1, double y1, double x2, double y2, double x3, double y3)
    {
        Engine.profiler.startSection("fillTriangle");
        Engine.renderer.fillTriangle(x1, y1, x2, y2, x3, y3);
        Engine.profiler.endSection();
    }
    
    /**
     * See {@link Renderer#triangle(double, double, double, double, double, double)}
     */
    public static void triangle(double x1, double y1, double x2, double y2, double x3, double y3)
    {
        Engine.profiler.startSection("triangle");
        Engine.renderer.triangle(x1, y1, x2, y2, x3, y3);
        Engine.profiler.endSection();
    }
    
    /**
     * See {@link Renderer#drawSquare(double, double, double)}
     */
    public static void drawSquare(double x, double y, double w)
    {
        Engine.profiler.startSection("drawSquare");
        Engine.renderer.drawSquare(x, y, w);
        Engine.profiler.endSection();
    }
    
    /**
     * See {@link Renderer#fillSquare(double, double, double)}
     */
    public static void fillSquare(double x, double y, double w)
    {
        Engine.profiler.startSection("startSection");
        Engine.renderer.fillSquare(x, y, w);
        Engine.profiler.endSection();
    }
    
    /**
     * See {@link Renderer#square(double, double, double)}
     */
    public static void square(double a, double b, double c)
    {
        Engine.profiler.startSection("square");
        Engine.renderer.square(a, b, c);
        Engine.profiler.endSection();
    }
    
    /**
     * See {@link Renderer#drawRect(double, double, double, double)}
     */
    public static void drawRect(double x, double y, double w, double h)
    {
        Engine.profiler.startSection("drawRect");
        Engine.renderer.drawRect(x, y, w, h);
        Engine.profiler.endSection();
    }
    
    /**
     * See {@link Renderer#fillRect(double, double, double, double)}
     */
    public static void fillRect(double x, double y, double w, double h)
    {
        Engine.profiler.startSection("fillRect");
        Engine.renderer.fillRect(x, y, w, h);
        Engine.profiler.endSection();
    }
    
    /**
     * See {@link Renderer#rect(double, double, double, double)}
     */
    public static void rect(double a, double b, double c, double d)
    {
        Engine.profiler.startSection("rect");
        Engine.renderer.rect(a, b, c, d);
        Engine.profiler.endSection();
    }
    
    /**
     * See {@link Renderer#drawQuad(double, double, double, double, double, double, double, double)}
     */
    public static void drawQuad(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4)
    {
        Engine.profiler.startSection("drawQuad");
        Engine.renderer.drawQuad(x1, y1, x2, y2, x3, y3, x4, y4);
        Engine.profiler.endSection();
    }
    
    /**
     * See {@link Renderer#fillQuad(double, double, double, double, double, double, double, double)}
     */
    public static void fillQuad(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4)
    {
        Engine.profiler.startSection("fillQuad");
        Engine.renderer.fillQuad(x1, y1, x2, y2, x3, y3, x4, y4);
        Engine.profiler.endSection();
    }
    
    /**
     * See {@link Renderer#quad(double, double, double, double, double, double, double, double)}
     */
    public static void quad(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4)
    {
        Engine.profiler.startSection("quad");
        Engine.renderer.quad(x1, y1, x2, y2, x3, y3, x4, y4);
        Engine.profiler.endSection();
    }
    
    /**
     * See {@link Renderer#drawPolygon(double...)}
     */
    public static void drawPolygon(double... points)
    {
        Engine.profiler.startSection("drawPolygon");
        Engine.renderer.drawPolygon(points);
        Engine.profiler.endSection();
    }
    
    /**
     * See {@link Renderer#fillPolygon(double...)}
     */
    public static void fillPolygon(double... points)
    {
        Engine.profiler.startSection("fillPolygon");
        Engine.renderer.fillPolygon(points);
        Engine.profiler.endSection();
    }
    
    /**
     * See {@link Renderer#polygon(double...)}
     */
    public static void polygon(double... points)
    {
        Engine.profiler.startSection("polygon");
        Engine.renderer.polygon(points);
        Engine.profiler.endSection();
    }
    
    /**
     * See {@link Renderer#drawCircle(double, double, double)}
     */
    public static void drawCircle(double x, double y, double r)
    {
        Engine.profiler.startSection("drawCircle");
        Engine.renderer.drawCircle(x, y, r);
        Engine.profiler.endSection();
    }
    
    /**
     * See {@link Renderer#fillCircle(double, double, double)}
     */
    public static void fillCircle(double x, double y, double r)
    {
        Engine.profiler.startSection("fillCircle");
        Engine.renderer.fillCircle(x, y, r);
        Engine.profiler.endSection();
    }
    
    /**
     * See {@link Renderer#circle(double, double, double)}
     */
    public static void circle(double a, double b, double c)
    {
        Engine.profiler.startSection("circle");
        Engine.renderer.circle(a, b, c);
        Engine.profiler.endSection();
    }
    
    /**
     * See {@link Renderer#drawEllipse(double, double, double, double)}
     */
    public static void drawEllipse(double x, double y, double rx, double ry)
    {
        Engine.profiler.startSection("drawEllipse");
        Engine.renderer.drawEllipse(x, y, rx, ry);
        Engine.profiler.endSection();
    }
    
    /**
     * See {@link Renderer#fillEllipse(double, double, double, double)}
     */
    public static void fillEllipse(double x, double y, double rx, double ry)
    {
        Engine.profiler.startSection("fillEllipse");
        Engine.renderer.fillEllipse(x, y, rx, ry);
        Engine.profiler.endSection();
    }
    
    /**
     * See {@link Renderer#ellipse(double, double, double, double)}
     */
    public static void ellipse(double a, double b, double c, double d)
    {
        Engine.profiler.startSection("ellipse");
        Engine.renderer.ellipse(a, b, c, d);
        Engine.profiler.endSection();
    }
    
    /**
     * See {@link Renderer#drawArc(double, double, double, double, double, double)}
     */
    public static void drawArc(double x, double y, double rx, double ry, double start, double stop)
    {
        Engine.profiler.startSection("drawArc");
        Engine.renderer.drawArc(x, y, rx, ry, start, stop);
        Engine.profiler.endSection();
    }
    
    /**
     * See {@link Renderer#fillArc(double, double, double, double, double, double)}
     */
    public static void fillArc(double x, double y, double rx, double ry, double start, double stop)
    {
        Engine.profiler.startSection("fillArc");
        Engine.renderer.fillArc(x, y, rx, ry, start, stop);
        Engine.profiler.endSection();
    }
    
    /**
     * See {@link Renderer#arc(double, double, double, double, double, double)}
     */
    public static void arc(double a, double b, double c, double d, double start, double stop)
    {
        Engine.profiler.startSection("arc");
        Engine.renderer.arc(a, b, c, d, start, stop);
        Engine.profiler.endSection();
    }
    
    /**
     * See {@link Renderer#drawTexture(Texture, double, double, double, double, double, double, double, double)}
     */
    public static void drawTexture(@NotNull Texture texture, double x1, double y1, double x2, double y2, double u1, double v1, double v2, double u2)
    {
        Engine.profiler.startSection("drawTexture");
        Engine.renderer.drawTexture(texture, x1, y1, x2, y2, u1, v1, u2, v2);
        Engine.profiler.endSection();
    }
    
    /**
     * See {@link Renderer#texture(Texture, double, double, double, double, double, double, double, double)}
     */
    public static void texture(@NotNull Texture texture, double a, double b, double c, double d, double u1, double v1, double v2, double u2)
    {
        Engine.profiler.startSection("texture");
        Engine.renderer.texture(texture, a, b, c, d, u1, v1, v2, u2);
        Engine.profiler.endSection();
    }
    
    /**
     * See {@link Renderer#texture(Texture, double, double, double, double, double, double)}
     */
    public static void texture(@NotNull Texture t, double x, double y, double u1, double v1, double v2, double u2)
    {
        Engine.profiler.startSection("texture");
        Engine.renderer.texture(t, x, y, u1, v1, v2, u2);
        Engine.profiler.endSection();
    }
    
    /**
     * See {@link Renderer#texture(Texture, double, double, double, double)}
     */
    public static void texture(@NotNull Texture t, double a, double b, double c, double d)
    {
        Engine.profiler.startSection("texture");
        Engine.renderer.texture(t, a, b, c, d);
        Engine.profiler.endSection();
    }
    
    /**
     * See {@link Renderer#texture(Texture, double, double)}
     */
    public static void texture(@NotNull Texture t, double x, double y)
    {
        Engine.profiler.startSection("texture");
        Engine.renderer.texture(t, x, y);
        Engine.profiler.endSection();
    }
    
    /**
     * See {@link Renderer#drawInterpolatedTexture(Texture, Texture, double, double, double, double, double, double, double, double, double)}
     */
    public static void drawInterpolatedTexture(@NotNull Texture texture1, @NotNull Texture texture2, double amount, double x1, double y1, double x2, double y2, double u1, double v1, double v2, double u2)
    {
        Engine.profiler.startSection("drawInterpolatedTexture");
        Engine.renderer.drawInterpolatedTexture(texture1, texture2, amount, x1, y1, x2, y2, u1, v1, u2, v2);
        Engine.profiler.endSection();
    }
    
    /**
     * See {@link Renderer#interpolateTexture(Texture, Texture, double, double, double, double, double, double, double, double, double)}
     */
    public static void interpolateTexture(@NotNull Texture texture1, @NotNull Texture texture2, double amount, double a, double b, double c, double d, double u1, double v1, double v2, double u2)
    {
        Engine.profiler.startSection("interpolateTexture");
        Engine.renderer.interpolateTexture(texture1, texture2, amount, a, b, c, d, u1, v1, v2, u2);
        Engine.profiler.endSection();
    }
    
    /**
     * See {@link Renderer#interpolateTexture(Texture, Texture, double, double, double, double, double, double, double)}
     */
    public static void interpolateTexture(@NotNull Texture texture1, @NotNull Texture texture2, double amount, double x, double y, double u1, double v1, double v2, double u2)
    {
        Engine.profiler.startSection("interpolateTexture");
        Engine.renderer.interpolateTexture(texture1, texture2, amount, x, y, u1, v1, v2, u2);
        Engine.profiler.endSection();
    }
    
    /**
     * See {@link Renderer#interpolateTexture(Texture, Texture, double, double, double, double, double)}
     */
    public static void interpolateTexture(@NotNull Texture texture1, @NotNull Texture texture2, double amount, double a, double b, double c, double d)
    {
        Engine.profiler.startSection("interpolateTexture");
        Engine.renderer.interpolateTexture(texture1, texture2, amount, a, b, c, d);
        Engine.profiler.endSection();
    }
    
    /**
     * See {@link Renderer#interpolateTexture(Texture, Texture, double, double, double)}
     */
    public static void interpolateTexture(@NotNull Texture texture1, @NotNull Texture texture2, double amount, double x, double y)
    {
        Engine.profiler.startSection("interpolateTexture");
        Engine.renderer.interpolateTexture(texture1, texture2, amount, x, y);
        Engine.profiler.endSection();
    }
    
    /**
     * See {@link Renderer#drawText(String, double, double)}
     */
    public static void drawText(@NotNull String text, double x, double y)
    {
        Engine.profiler.startSection("drawText");
        Engine.renderer.drawText(text, x, y);
        Engine.profiler.endSection();
    }
    
    /**
     * See {@link Renderer#text(String, double, double, double, double)}
     */
    public static void text(@NotNull String text, double a, double b, double c, double d)
    {
        Engine.profiler.startSection("text");
        Engine.renderer.text(text, a, b, c, d);
        Engine.profiler.endSection();
    }
    
    /**
     * See {@link Renderer#text(String, double, double)}
     */
    public static void text(@NotNull String text, double x, double y)
    {
        Engine.profiler.startSection("text");
        Engine.renderer.text(text, x, y);
        Engine.profiler.endSection();
    }
    
    /**
     * See {@link Renderer#loadPixels()}
     */
    public static int[] loadPixels()
    {
        Engine.profiler.startSection("loadPixels");
        int[] pixels = Engine.renderer.loadPixels();
        Engine.profiler.endSection();
        return pixels;
    }
    
    /**
     * See {@link Renderer#updatePixels()}
     */
    public static void updatePixels()
    {
        Engine.profiler.startSection("updatePixels");
        Engine.renderer.updatePixels();
        Engine.profiler.endSection();
    }
    
    // --------------------
    // -- Instance Stuff --
    // --------------------
    
    private final String name;
    
    protected Engine()
    {
        String className = getClass().getSimpleName();
        
        StringBuilder name = new StringBuilder();
        for (int i = 0; i < className.length(); i++)
        {
            char ch = className.charAt(i);
            if (i > 0 && Character.isUpperCase(ch)) name.append(' ');
            name.append(ch == '_' ? " - " : ch);
        }
        this.name = name.toString();
    }
    
    /**
     * This method is called once the engine's environment is fully setup. This is only called once.
     * <p>
     * This is where you call {@link #size} to enable rendering. At which point you can create textures and render to them.
     */
    public void setup() { }
    
    /**
     * This method is called once per frame.
     *
     * @param elapsedTime The time in seconds since the last frame.
     */
    public void draw(double elapsedTime) { }
    
    /**
     * This method is called after the render loop has exited for any reason, exception or otherwise. This is only called once.
     */
    public void destroy() { }
}
