package engine.render;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;
import java.util.stream.Stream;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class ScanLine3
{
    static final int WIDTH  = 800;
    static final int HEIGHT = 600;
    
    static boolean running = true;
    
    private static class Edge
    {
        int xMin, yMin, xMax, yMax, x, y, sign, dx, dy, sum;
        boolean rightEdge;
    }
    
    public static void drawPolygon(int[] coordinates)
    {
        int n = coordinates.length;
        
        if ((n & 1) == 1) throw new RuntimeException("Invalid coordinates. Must be an even number");
        if (n < 6) throw new RuntimeException("Invalid coordinates. Must have at least 3 coordinates");
        
        int x1, y1, x2, y2;
        
        x1 = coordinates[n - 2];
        y1 = coordinates[n - 1];
        for (int i = 0; i < n; i += 2)
        {
            x2 = coordinates[i];
            y2 = coordinates[i + 1];
            
            glColor3f(1.0f, 0.0f, 0.0f);
            glBegin(GL_LINES);
            glVertex2i(x1, y1);
            glVertex2i(x2, y2);
            glEnd();
            
            x1 = x2;
            y1 = y2;
        }
    }
    
    public static void fillPolygon(int[] coordinates)
    {
        int n = coordinates.length;
        
        if ((n & 1) == 1) throw new RuntimeException("Invalid coordinates. Must be an even number");
        if (n < 6) throw new RuntimeException("Invalid coordinates. Must have at least 3 coordinates");
        
        ArrayList<Edge> edgeTable  = new ArrayList<>();
        ArrayList<Edge> activeList = new ArrayList<>();
        
        int x1, y1, x2, y2;
        
        x1 = coordinates[n - 2];
        y1 = coordinates[n - 1];
        for (int i = 0; i < n; i += 2)
        {
            x2 = coordinates[i];
            y2 = coordinates[i + 1];
            
            if (y1 != y2)
            {
                Edge    edge     = new Edge();
                boolean maxPoint = y2 >= y1; // True if point2 is top most
                edge.xMin = edge.x = !maxPoint ? x2 : x1;
                edge.yMin = edge.y = !maxPoint ? y2 : y1;
                edge.xMax = maxPoint ? x2 : x1;
                edge.yMax = maxPoint ? y2 : y1;
                edge.sign = Integer.signum(edge.xMax - edge.xMin);
                edge.dy   = Math.abs(y2 - y1);
                edge.dx   = Math.abs(x2 - x1);
                edge.sum  = edge.dx - edge.dy;
                edgeTable.add(edge);
            }
            
            x1 = x2;
            y1 = y2;
        }
        edgeTable.sort(Comparator.comparingInt(o -> o.yMin));
        
        int scanLine = 0;
        if (edgeTable.size() > 0) scanLine = edgeTable.get(0).yMin;
        glColor3f(1.0f, 1.0f, 1.0f);
        while (edgeTable.size() > 0)
        {
            for (Edge edge : edgeTable)
            {
                if (edge.yMin == scanLine)
                {
                    activeList.add(edge);
                }
            }
            for (Edge edge : activeList)
            {
                // TODO - Need to determine which side the edge is on before this
                while (edge.y <= scanLine)
                {
                    int e2 = edge.sum << 1;
                    if (e2 <= edge.dx)
                    {
                        edge.sum += edge.dx;
                        edge.y++;
                        if (edge.y > scanLine)
                        {
                            edge.sum -= edge.dx;
                            edge.y--;
                            break;
                        }
                    }
                    if (e2 >= -edge.dy)
                    {
                        edge.sum -= edge.dy;
                        edge.x += edge.sign;
                    }
                    if (edge.y == scanLine && ((edge.sign < 0 && edge.rightEdge) || (edge.sign > 0 && !edge.rightEdge))) break;
                    if (edge.x == edge.xMax && edge.y == edge.yMax) break;
                }
            }
            activeList.sort(Comparator.comparingInt(o -> o.x));
            
            x1 = Integer.MIN_VALUE;
            y1 = Integer.MIN_VALUE;
            x2 = Integer.MIN_VALUE;
            for (Edge edge : activeList)
            {
                if (x1 == Integer.MIN_VALUE)
                {
                    x1             = edge.x;
                    y1             = edge.yMax;
                    edge.rightEdge = false;
                }
                else if (y1 == edge.yMin)
                {
                    continue;
                }
                else
                {
                    x2             = edge.x;
                    edge.rightEdge = true;
                }
                
                if (x2 != Integer.MIN_VALUE)
                {
                    glBegin(GL_LINES);
                    glVertex2i(x1, scanLine);
                    glVertex2i(x2, scanLine);
                    glEnd();
                    x1 = Integer.MIN_VALUE;
                    x2 = Integer.MIN_VALUE;
                }
            }
            
            scanLine++;
            for (int i = 0; i < activeList.size(); i++)
            {
                Edge edge = activeList.get(i);
                if (edge.yMax < scanLine)
                {
                    activeList.remove(edge);
                    edgeTable.remove(edge);
                    i--;
                }
            }
        }
    }
    
    public static void main(String[] args)
    {
        String file = "PolyDino.txt";
        Path   path = null;
        try
        {
            path = Paths.get(Objects.requireNonNull(ScanLines.class.getClassLoader().getResource(file)).toURI());
        }
        catch (URISyntaxException | NullPointerException ignored) {}
        if (path == null) path = Paths.get(file);
        
        ArrayList<String> lines = new ArrayList<>();
        try (Stream<String> stream = Files.lines(path))
        {
            stream.forEach(lines::add);
        }
        catch (IOException e)
        {
            System.out.println("Could not open file");
            return;
        }
        
        lines.clear();
        lines.add("100,0");
        lines.add("0,100");
        lines.add("100,200");
        lines.add("100,100");
        lines.add("200,100");
        
        System.out.println(lines);
        
        int   n     = lines.size();
        int[] x     = new int[n];
        int[] y     = new int[n];
        int[] cords = new int[n << 1];
        for (int i = 0; i < n; i++)
        {
            String[] numbers = lines.get(i).split(",");
            x[i]               = Integer.parseInt(numbers[0]);
            y[i]               = Integer.parseInt(numbers[1]);
            cords[(2 * i)]     = x[i];
            cords[(2 * i) + 1] = y[i];
        }
        
        long glfwWindow;
        {
            GLFWErrorCallback.createPrint(System.err).set();
            if (!glfwInit()) throw new IllegalStateException("Unable to initialize GLFW");
            
            glfwDefaultWindowHints();
            glfwWindowHint(GLFW_VISIBLE, GLFW_TRUE);
            glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
            glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 1);
            glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 1);
            glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_ANY_PROFILE);
            
            glfwWindow = glfwCreateWindow(WIDTH, HEIGHT, "", NULL, NULL);
            
            GLFWVidMode videoMode = Objects.requireNonNull(glfwGetVideoMode(glfwGetPrimaryMonitor()));
            glfwSetWindowPos(glfwWindow, (videoMode.width() - WIDTH) >> 1, (videoMode.height() - HEIGHT) >> 1);
            glfwSetWindowCloseCallback(glfwWindow, window -> running = false);
            
            glfwMakeContextCurrent(glfwWindow);
            GL.createCapabilities();
        }
        if (glfwWindow == NULL) throw new RuntimeException("Failed to create the GLFW window");
        
        glClearColor(0.1F, 0.1F, 0.1F, 1.0F);
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0, WIDTH, 0, HEIGHT, -1, 1);
        
        while (running)
        {
            glfwPollEvents();
            
            glClear(GL_COLOR_BUFFER_BIT);
            
            fillPolygon(cords);
            drawPolygon(cords);
            
            glfwSwapBuffers(glfwWindow);
        }
    }
}
