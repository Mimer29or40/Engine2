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

public class ScanLine2
{
    static final int WIDTH  = 800;
    static final int HEIGHT = 600;
    
    static boolean running = true;
    
    static class Edge
    {
        int yMax, yMin, x, sign, dx, dy, sum;
    }
    
    /**
     * Creates edge buckets from the given edges
     *
     * @param n Number of vertices
     * @param x array of x points
     * @param y array of y points
     * @return List of edge buckets
     */
    static ArrayList<Edge> createEdges(int n, int[] x, int[] y)
    {
        ArrayList<Edge> edgeTable = new ArrayList<>();
        
        int x1 = x[n - 1];
        int y1 = y[n - 1];
        int x2, y2;
        for (int i = 0; i < n; i++)
        {
            x2 = x[i];
            y2 = y[i];
            
            if (y1 != y2)
            {
                Edge edge = new Edge();
                edge.yMax = Math.max(y1, y2);
                edge.yMin = Math.min(y1, y2);
                edge.x    = y2 >= y1 ? x1 : x2;
                edge.sign = y2 >= y1 ? 1 : -1;
                edge.dy   = Math.abs(y2 - y1);
                edge.dx   = Math.abs(x2 - x1);
                edge.sum  = 0;
                edgeTable.add(edge);
            }
            
            x1 = x[i];
            y1 = y[i];
        }
        return edgeTable;
    }
    
    /**
     * Given the edge table of the polygon, fill the polygons
     *
     * @param n Number of vertices
     * @param x array of x points
     * @param y array of y points
     */
    static void processEdgeTable(int n, int[] x, int[] y)
    {
        ArrayList<Edge> edgeTable  = new ArrayList<>();
        ArrayList<Edge> activeList = new ArrayList<>();
        
        int x1 = x[n - 1];
        int y1 = y[n - 1];
        int x2, y2;
        for (int i = 0; i < n; i++)
        {
            x2 = x[i];
            y2 = y[i];
            
            if (y1 != y2)
            {
                Edge edge = new Edge();
                edge.yMax = Math.max(y1, y2);
                edge.yMin = Math.min(y1, y2);
                edge.x    = y2 >= y1 ? x1 : x2;
                edge.sign = y2 >= y1 ? 1 : -1;
                edge.dy   = Math.abs(y2 - y1);
                edge.dx   = Math.abs(x2 - x1);
                edge.sum  = 0;
                edgeTable.add(edge);
            }
            
            x1 = x[i];
            y1 = y[i];
        }
        edgeTable.sort(Comparator.comparingInt(o -> o.yMin));
        
        int scanLine = edgeTable.get(0).yMin;
        glColor3f(1.0f, 0.0f, 0.0f);
        while (edgeTable.size() > 0)
        {
            for (int i = 0; i < activeList.size(); i++)
            {
                Edge edge = activeList.get(i);
                if (edge.yMax == scanLine)
                {
                    activeList.remove(edge);
                    edgeTable.remove(edge);
                    i--;
                }
            }
            for (Edge edge : edgeTable)
            {
                if (edge.yMin == scanLine)
                {
                    activeList.add(edge);
                }
            }
            activeList.sort(Comparator.comparingInt(o -> o.x));
            
            for (int i = 0; i < activeList.size(); i += 2)
            {
                Edge edge1 = activeList.get(i);
                Edge edge2 = activeList.get(i + 1);
                glBegin(GL_LINES);
                glVertex2i(edge1.x, scanLine);
                glVertex2i(edge2.x, scanLine);
                glEnd();
            }
            scanLine++;
            for (Edge edge : activeList)
            {
                if (edge.dx != 0)
                {
                    edge.sum += edge.dx;
                    while (edge.sum >= edge.dy)
                    {
                        edge.x += edge.sign;
                        edge.sum -= edge.dy;
                    }
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
        
        int   n = lines.size();
        int[] x = new int[n];
        int[] y = new int[n];
        for (int i = 0; i < n; i++)
        {
            String[] numbers = lines.get(i).split(",");
            x[i] = Integer.parseInt(numbers[0]);
            y[i] = Integer.parseInt(numbers[1]);
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
            
            processEdgeTable(n, x, y);
            
            glfwSwapBuffers(glfwWindow);
        }
    }
}
