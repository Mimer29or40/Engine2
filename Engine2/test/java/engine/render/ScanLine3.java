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
        int xMin, yMin, xMax, yMax, x, y, sign, dx, dy, sum, slope;
        
        public Edge(int x1, int y1, int x2, int y2)
        {
            boolean maxPoint = y2 >= y1;
            this.xMin  = this.x = !maxPoint ? x2 : x1;
            this.yMin  = this.y = !maxPoint ? y2 : y1;
            this.xMax  = maxPoint ? x2 : x1;
            this.yMax  = maxPoint ? y2 : y1;
            this.sign  = Integer.signum(this.xMax - this.xMin);
            this.dy    = Math.abs(y2 - y1);
            this.dx    = Math.abs(x2 - x1);
            this.sum   = this.dx - this.dy;
            this.slope = this.dx != 0 ? this.dy * 1000 / this.dx : Integer.MAX_VALUE;
        }
    }
    
    private static int minXBresenham(Edge edge)
    {
        int tempX   = edge.x;
        int tempSum = edge.sum;
        while (true)
        {
            int e2 = tempSum << 1;
            if (e2 <= edge.dx) return tempX;
            if (e2 >= -edge.dy)
            {
                if (tempX + edge.sign > tempX) return tempX;
                tempX += edge.sign;
                tempSum -= edge.dy;
            }
            if (tempX == edge.xMax && edge.y == edge.yMax) return tempX;
        }
    }
    
    private static int maxXBresenham(Edge edge)
    {
        int tempX   = edge.x;
        int tempSum = edge.sum;
        while (true)
        {
            int e2 = tempSum << 1;
            if (e2 <= edge.dx) return tempX;
            if (e2 >= -edge.dy)
            {
                if (tempX + edge.sign < tempX) return tempX;
                tempX += edge.sign;
                tempSum -= edge.dy;
            }
            if (tempX == edge.xMax && edge.y == edge.yMax) return tempX;
        }
    }
    
    public static void drawPolygon(int[] coordinates)
    {
        int n = coordinates.length;
        
        if ((n & 1) == 1) throw new RuntimeException("Invalid coordinates. Must be an even number");
        if (n < 6) throw new RuntimeException("Invalid coordinates. Must have at least 3 coordinates");
        
        int x1, y1, x2, y2;
        
        x1 = coordinates[n - 2];
        y1 = coordinates[n - 1];
        glColor3f(1.0f, 0.0f, 0.0f);
        for (int i = 0; i < n; i += 2)
        {
            x2 = coordinates[i];
            y2 = coordinates[i + 1];
            
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
    
        int minX, maxX, minY, maxY;
        int x1, y1, x2, y2;
    
        ArrayList<Edge> edgeTable  = new ArrayList<>();
        ArrayList<Edge> activeList = new ArrayList<>();
    
        minX = maxX = x1 = coordinates[n - 2];
        minY = maxY = y1 = coordinates[n - 1];
        for (int i = 0; i < n; i += 2)
        {
            x2   = coordinates[i];
            y2   = coordinates[i + 1];
            minX = Math.min(minX, x2);
            maxX = Math.max(maxX, x2);
            minY = Math.min(minY, y2);
            maxY = Math.max(maxY, y2);
            if (y1 != y2) edgeTable.add(new Edge(x1, y1, x2, y2));
            x1 = x2;
            y1 = y2;
        }
        int scanLine = minY;
        // if (edgeTable.size() == 0) lineImpl(minX, scanLine, maxX, scanLine, 1, LINE_OVERLAP_NONE);
        edgeTable.sort(Comparator.comparingInt(o -> o.yMin));
    
        glColor3f(1.0f, 1.0f, 1.0f);
        while (edgeTable.size() > 0)
        {
            for (Edge edge : edgeTable) if (edge.yMin == scanLine) activeList.add(edge);
            for (Edge edge : activeList)
            {
                while (edge.y < scanLine)
                {
                    int e2 = edge.sum << 1;
                    if (e2 <= edge.dx)
                    {
                        edge.y++;
                        edge.sum += edge.dx;
                    }
                    if (e2 >= -edge.dy)
                    {
                        edge.x += edge.sign;
                        edge.sum -= edge.dy;
                    }
                    if (edge.x == edge.xMax && edge.y == edge.yMax) break;
                }
            }
            activeList.sort((o1, o2) -> {
                if (o1.x < o2.x) return -1;
                if (o1.x > o2.x) return 1;
                if (o1.sign < o2.sign) return -1;
                if (o1.sign > o2.sign) return 1;
                if (o1.slope < o2.slope) return -1;
                if (o1.slope > o2.slope) return 1;
                return Integer.compare(o1.xMax, o2.xMax);
            });
    
            minX = Integer.MAX_VALUE;
            minY = Integer.MAX_VALUE;
            maxY = Integer.MIN_VALUE;
            for (Edge edge : activeList)
            {
                int x = minXBresenham(edge);
                if (minX == Integer.MAX_VALUE || (edge.yMin < minY && minX == x))
                {
                    minX = x;
                    minY = edge.yMin;
                    maxY = edge.yMax;
                }
                else if (maxY != edge.yMin)
                {
                    x = maxXBresenham(edge);
                    glBegin(GL_LINES);
                    glVertex2i(minX, scanLine);
                    glVertex2i(x, scanLine);
                    glEnd();
                    minX = Integer.MAX_VALUE;
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
