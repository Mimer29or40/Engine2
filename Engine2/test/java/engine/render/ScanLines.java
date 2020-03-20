package engine.render;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Stream;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class ScanLines
{
    static final int WIDTH   = 800;
    static final int HEIGHT  = 600;
    static final int MAX_VER = 10000;
    
    static class EdgeBucket
    {
        int    ymax;
        double xofymin;
        double slopeinverse;
    }
    
    static class EdgeTableTuple
    {
        int          countEdgeBucket;
        EdgeBucket[] buckets = new EdgeBucket[MAX_VER];
        
        public EdgeTableTuple()
        {
            for (int i = 0; i < MAX_VER; i++) this.buckets[i] = new EdgeBucket();
        }
    }
    
    static EdgeTableTuple[] EdgeTable       = new EdgeTableTuple[HEIGHT];
    static EdgeTableTuple   ActiveEdgeTuple = new EdgeTableTuple();
    
    static
    {
        for (int i = 0; i < HEIGHT; i++)
        {
            EdgeTable[i] = new EdgeTableTuple();
        }
    }
    
    static boolean running = true;
    
    // Scanline Function
    static void initEdgeTable()
    {
        for (int i = 0; i < HEIGHT; i++)
        {
            EdgeTable[i].countEdgeBucket = 0;
        }
        
        ActiveEdgeTuple.countEdgeBucket = 0;
    }
    
    static void printTuple(EdgeTableTuple tup)
    {
        if (tup.countEdgeBucket > 0) System.out.println("Count %s-----\n" + tup.countEdgeBucket);
        
        for (int j = 0; j < tup.countEdgeBucket; j++)
        {
            System.out.println(String.format(" %s+%.2f+%.2f", tup.buckets[j].ymax, tup.buckets[j].xofymin, tup.buckets[j].slopeinverse));
        }
    }
    
    static void printTable()
    {
        for (int i = 0; i < HEIGHT; i++)
        {
            if (EdgeTable[i].countEdgeBucket > 0) System.out.println("Scanline %s" + i);
            printTuple(EdgeTable[i]);
        }
    }
    
    static void insertionSort(EdgeTableTuple ett)
    {
        int        i, j;
        EdgeBucket temp = new EdgeBucket();
        
        for (i = 1; i < ett.countEdgeBucket; i++)
        {
            temp.ymax         = ett.buckets[i].ymax;
            temp.xofymin      = ett.buckets[i].xofymin;
            temp.slopeinverse = ett.buckets[i].slopeinverse;
            j                 = i - 1;
            
            while (j >= 0 && temp.xofymin < ett.buckets[j].xofymin)
            {
                ett.buckets[j + 1].ymax         = ett.buckets[j].ymax;
                ett.buckets[j + 1].xofymin      = ett.buckets[j].xofymin;
                ett.buckets[j + 1].slopeinverse = ett.buckets[j].slopeinverse;
                j                               = j - 1;
            }
            ett.buckets[j + 1].ymax         = temp.ymax;
            ett.buckets[j + 1].xofymin      = temp.xofymin;
            ett.buckets[j + 1].slopeinverse = temp.slopeinverse;
        }
    }
    
    static void storeEdgeInTuple(EdgeTableTuple receiver, int ym, double xm, double slopInv)
    {
        // both used for edgetable and active edge table..
        // The edge tuple sorted in increasing ymax and x of the lower end.
        receiver.buckets[receiver.countEdgeBucket].ymax         = ym;
        receiver.buckets[receiver.countEdgeBucket].xofymin      = xm;
        receiver.buckets[receiver.countEdgeBucket].slopeinverse = slopInv;
        
        // sort the buckets
        insertionSort(receiver);
        
        receiver.countEdgeBucket++;
    }
    
    static void storeEdgeInTable(int x1, int y1, int x2, int y2)
    {
        double m, minv;
        int    ymaxTS, xwithyminTS, scanline; //ts stands for to store
        
        if (x2 == x1)
        {
            minv = 0.000000;
        }
        else
        {
            m = ((float) (y2 - y1)) / ((float) (x2 - x1));
            
            // horizontal lines are not stored in edge table
            if (y2 == y1)
            { return; }
            
            minv = 1.0 / m;
            System.out.println(String.format("Slope string for %s %s & %s %s: %s", x1, y1, x2, y2, minv));
        }
        
        if (y1 > y2)
        {
            scanline    = y2;
            ymaxTS      = y1;
            xwithyminTS = x2;
        }
        else
        {
            scanline    = y1;
            ymaxTS      = y2;
            xwithyminTS = x1;
        }
        // the assignment part is done..now storage..
        storeEdgeInTuple(EdgeTable[scanline], ymaxTS, xwithyminTS, minv);
    }
    
    static void removeEdgeByYmax(EdgeTableTuple Tup, int yy)
    {
        for (int i = 0, j; i < Tup.countEdgeBucket; i++)
        {
            if (Tup.buckets[i].ymax == yy)
            {
                System.out.println("Removed at " + yy);
                
                for (j = i; j < Tup.countEdgeBucket - 1; j++)
                {
                    Tup.buckets[j].ymax         = Tup.buckets[j + 1].ymax;
                    Tup.buckets[j].xofymin      = Tup.buckets[j + 1].xofymin;
                    Tup.buckets[j].slopeinverse = Tup.buckets[j + 1].slopeinverse;
                }
                Tup.countEdgeBucket--;
                i--;
            }
        }
    }
    
    static void updatexbyslopeinv(EdgeTableTuple Tup)
    {
        for (int i = 0; i < Tup.countEdgeBucket; i++)
        {
            (Tup.buckets[i]).xofymin = (Tup.buckets[i]).xofymin + (Tup.buckets[i]).slopeinverse;
        }
    }
    
    static void ScanlineFill()
    {
        /* Follow the following rules:
        1. Horizontal edges: Do not include in edge table
        2. Horizontal edges: Drawn either on the bottom or on the top.
        3. Vertices: If local max or min, then count twice, else count
            once.
        4. Either vertices at local minima or at local maxima are drawn.*/
        int i, j, x1, ymax1, x2, ymax2, FillFlag = 0, coordCount;
        
        // we will start from scanline 0;
        // Repeat until last scanline:
        for (i = 0; i < HEIGHT; i++)//4. Increment y by 1 (next scan line)
        {
            // 1. Move from ET bucket y to the
            // AET those edges whose ymin = y (entering edges)
            for (j = 0; j < EdgeTable[i].countEdgeBucket; j++)
            {
                storeEdgeInTuple(ActiveEdgeTuple, EdgeTable[i].buckets[j].ymax, EdgeTable[i].buckets[j].xofymin, EdgeTable[i].buckets[j].slopeinverse);
            }
            printTuple(ActiveEdgeTuple);
            
            // 2. Remove from AET those edges for
            // which y=ymax (not involved in next scan line)
            removeEdgeByYmax(ActiveEdgeTuple, i);
            
            //sort AET (remember: ET is presorted)
            insertionSort(ActiveEdgeTuple);
            
            printTuple(ActiveEdgeTuple);
            
            //3. Fill lines on scan line y by using pairs of x-coords from AET
            j          = 0;
            FillFlag   = 0;
            coordCount = 0;
            x1         = 0;
            x2         = 0;
            ymax1      = 0;
            ymax2      = 0;
            while (j < ActiveEdgeTuple.countEdgeBucket)
            {
                if (coordCount % 2 == 0)
                {
                    x1    = (int) (ActiveEdgeTuple.buckets[j].xofymin);
                    ymax1 = ActiveEdgeTuple.buckets[j].ymax;
                    if (x1 == x2)
                    {
                        /* three cases can arrive-
                            1. lines are towards top of the intersection
                            2. lines are towards bottom
                            3. one line is towards top and other is towards bottom
                        */
                        if (((x1 == ymax1) && (x2 != ymax2)) || ((x1 != ymax1) && (x2 == ymax2)))
                        {
                            x2    = x1;
                            ymax2 = ymax1;
                        }
                        
                        else
                        {
                            coordCount++;
                        }
                    }
                    
                    else
                    {
                        coordCount++;
                    }
                }
                else
                {
                    x2    = (int) ActiveEdgeTuple.buckets[j].xofymin;
                    ymax2 = ActiveEdgeTuple.buckets[j].ymax;
                    
                    FillFlag = 0;
                    
                    // checking for intersection...
                    if (x1 == x2)
                    {
                        /*three cases can arive-
                            1. lines are towards top of the intersection
                            2. lines are towards bottom
                            3. one line is towards top and other is towards bottom
                        */
                        if (((x1 == ymax1) && (x2 != ymax2)) || ((x1 != ymax1) && (x2 == ymax2)))
                        {
                            x1    = x2;
                            ymax1 = ymax2;
                        }
                        else
                        {
                            coordCount++;
                            FillFlag = 1;
                        }
                    }
                    else
                    {
                        coordCount++;
                        FillFlag = 1;
                    }
                    if (FillFlag > 0)
                    {
                        //drawing actual lines...
                        glColor3f(0.0f, 0.7f, 0.0f);
                        
                        glBegin(GL_LINES);
                        glVertex2i(x1, i);
                        glVertex2i(x2, i);
                        glEnd();
                        glFlush();
                        
                        // printf("\nLine drawn from %s,%s to %s,%s",x1,i,x2,i);
                    }
                }
                j++;
            }
            // 5. For each nonvertical edge remaining in AET, update x for new y
            updatexbyslopeinv(ActiveEdgeTuple);
        }
        System.out.println("Scanline filling complete");
    }
    
    public static void main(String[] args)
    {
        ArrayList<String> lines = new ArrayList<>();
        Path              path  = null;
        URL               file  = ScanLines.class.getClassLoader().getResource("PolyDino.txt");
        if (file != null)
        {
            try
            {
                path = Paths.get(file.toURI());
            }
            catch (URISyntaxException ignored)
            {
            
            }
        }
        if (path == null) path = Paths.get("PolyDino.txt");
        try (Stream<String> stream = Files.lines(path))
        {
            stream.forEach(lines::add);
        }
        catch (IOException e)
        {
            System.out.println("Could not open file");
            return;
        }
        System.out.println(lines);
        
        GLFWErrorCallback.createPrint(System.err).set();
        if (!glfwInit()) throw new IllegalStateException("Unable to initialize GLFW");
        
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_TRUE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 1);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 1);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_ANY_PROFILE);
        
        long glfwWindow = glfwCreateWindow(WIDTH, HEIGHT, "", NULL, NULL);
        
        if (glfwWindow == NULL) throw new RuntimeException("Failed to create the GLFW window");
        
        GLFWVidMode videoMode = Objects.requireNonNull(glfwGetVideoMode(glfwGetPrimaryMonitor()));
        glfwSetWindowPos(glfwWindow, (videoMode.width() - WIDTH) >> 1, (videoMode.height() - HEIGHT) >> 1);
        glfwSetWindowCloseCallback(glfwWindow, window -> running = false);
        
        glfwMakeContextCurrent(glfwWindow);
        GL.createCapabilities();
        
        glClearColor(1.0F, 1.0F, 1.0F, 0.0F);
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0, WIDTH, 0, HEIGHT, -1, 1);
        
        // glClear(GL_COLOR_BUFFER_BIT);
        
        while (running)
        {
            glfwPollEvents();
            
            initEdgeTable();
            
            // drawPolyDino();
            glColor3f(1.0f, 0.0f, 0.0f);
            int count = 0, x1 = 0, y1 = 0, x2 = 0, y2 = 0;
            for (String line : lines)
            {
                count++;
                if (count > 2)
                {
                    x1    = x2;
                    y1    = y2;
                    count = 2;
                }
                String[] numbers = line.split(",");
                if (count == 1)
                {
                    x1 = Integer.parseInt(numbers[0]);
                    y1 = Integer.parseInt(numbers[1]);
                }
                else
                {
                    x2 = Integer.parseInt(numbers[0]);
                    y2 = Integer.parseInt(numbers[1]);
                    System.out.println(String.format("%s,%s", x2, y2));
                    glBegin(GL_LINES);
                    glVertex2i(x1, y1);
                    glVertex2i(x2, y2);
                    glEnd();
                    storeEdgeInTable(x1, y1, x2, y2);//storage of edges in edge table.
                    
                    glFlush();
                }
            }
            
            System.out.println("Table");
            printTable();
            
            ScanlineFill();//actual calling of scanline filling..
            
            glfwSwapBuffers(glfwWindow);
        }
    }
}
