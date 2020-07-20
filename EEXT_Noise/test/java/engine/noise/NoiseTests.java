package engine.noise;

import engine.Engine;
import engine.color.Color;

import static engine.util.Util.map;

public class NoiseTests extends Engine
{
    Noise noise;
    
    boolean mode = true;
    
    @Override
    public void setup()
    {
        size(200, 200, 4, 4);
    
        // noise = new Noise() {
        //     @Override
        //     public double calculate_impl(double... coord)
        //     {
        //         return 0.0;
        //     }
        // };
        // noise = new ValueNoise(1337);
        // noise.octaves(4);
        // noise = new PerlinNoise(1337);
        // noise = new SimplexNoise(1337);
        noise = new OpenSimplexNoise(1337);
        // noise = new WorleyNoise(1337)
        // {
        //     @Override
        //     protected double distanceFunction(List<Double> distances)
        //     {
        //         if (distances.size() == 0) return 0;
        //         if (distances.size() == 1) return (1.0 - distances.get(0)) * (1.0 - distances.get(0));
        //         return distances.get(1) - distances.get(0);
        //     }
        // };
    
        // frameRate(2);
    }
    
    @Override
    public void draw(double elapsedTime)
    {
        if (keyboard().SPACE.down()) mode = !mode;
        
        if (mode)
        {
            double vScale = screenHeight() >> 1;
            double hScale = map(mouse().x(), screenWidth(), 0, 0.01, 0.4);
            // hScale = 0.4;
    
            clear();
            weight(2);
    
            // for (int i = 0, n = (int) Math.floor(screenWidth() * hScale); i < n; i++)
            // {
            //     double x = i / hScale;
            //     stroke(Color.DARK_GREY);
            //     line(x, 0, x, screenHeight());
            //     stroke(Color.WHITE);
            //     text("" + i, x, 0);
            // }
    
            stroke(Color.WHITE);
            translate(0, vScale);
            scale(1, -1);
            double value;
            // double[] arr = new double[screenWidth()];
            for (int i = 0, n = screenWidth(); i < n; i++)
            {
                value = this.noise.calculate(i * hScale, seconds() * 0.5);
                // value = this.noise.calculate(i * hScale + seconds() * 0.5);
                // value = this.noise.calculate(i * hScale, 0.5);
                // arr[i] = value;
                point(i, value * vScale);
            }
            // println(Arrays.toString(arr));
        }
        else
        {
            int[] pixels = loadPixels();
            
            double hScale = map(mouse().x(), screenWidth(), 0, 0.01, 0.4);
            double vScale = map(mouse().y(), screenHeight(), 0, 0.01, 0.4);
            hScale = vScale = 0.05;
            
            int n = screenWidth();
            int m = screenHeight();
            
            double t = seconds() * 0.5;
            // double xt = seconds() * 0.5;
            // double yt = seconds() * 1.0;
            
            double value;
            for (int j = 0; j < m; j++)
            {
                for (int i = 0; i < n; i++)
                {
                    int index = (j * n + i) << 2;
    
                    value = this.noise.calculate(i * hScale, j * vScale, t) * 0.5 + 0.5;
    
                    pixels[index]     = (int) (value * 255);
                    pixels[index + 1] = (int) (value * 255);
                    pixels[index + 2] = (int) (value * 255);
                    pixels[index + 3] = 255;
                }
            }
    
            updatePixels();
    
            scale(1.0 / hScale, 1.0 / vScale);
            stroke(255, 0, 0, 10);
            weight(1);
            for (int i = 0; i < 10; i++)
            {
                line(i, 0, i, 10);
                line(0, i, 10, i);
            }
        }
    }
    
    // protected static int hash(int[] p, int... coord)
    // {
    //     int result = 0;
    //     for (int v : coord)
    //     {
    //         result = p[result + v];
    //     }
    //     return result;
    // }
    
    public static void main(String[] args)
    {
        start(new NoiseTests());
        
        // Random random = new Random(1337);
        //
        // int tableSize     = 512;
        // int tableSizeMask = tableSize - 1;
        //
        // int[] p = new int[tableSize << 1];
        //
        // for (int i = 0; i < tableSize; i++) p[i] = i;
        //
        // for (int i = 0; i < tableSize; i++)
        // {
        //     int index = random.nextInt() & tableSizeMask;
        //
        //     int swap = p[i];
        //     p[i]     = p[index];
        //     p[index] = swap;
        //
        //     p[i + tableSize] = p[i];
        // }
        //
        // double[] r = new double[tableSize];
        //
        // random.nextDoubles(r, -1.0, 1.0);
        //
        // double[] coord = new double[] {1.25, 0.75, 0.5};
        //
        // double x, xf, y, yf, z, zf;
        // int    xi, rx0, rx1, yi, ry0, ry1, zi, rz0, rz1;
        // double x0, x1, y0, y1, z0, z1;
        // double result;
        //
        // x = coord[0];
        // y = coord[1];
        // z = coord[2];
        //
        // xi = (int) Math.floor(x);
        // yi = (int) Math.floor(y);
        // zi = (int) Math.floor(z);
        //
        // xf = x - xi;
        // yf = y - yi;
        // zf = z - zi;
        //
        // rx0 = xi & tableSizeMask;
        // rx1 = (rx0 + 1) & tableSizeMask;
        // ry0 = yi & tableSizeMask;
        // ry1 = (ry0 + 1) & tableSizeMask;
        // rz0 = zi & tableSizeMask;
        // rz1 = (rz0 + 1) & tableSizeMask;
        //
        // x0 = r[p[p[p[rx0] + ry0] + rz0]];
        // x1 = r[p[p[p[rx1] + ry0] + rz0]];
        // println("HC Node [%s, %s, %s] %s", rx0, ry0, rz0, x0);
        // println("HC Node [%s, %s, %s] %s", rx1, ry0, rz0, x1);
        // y0 = smoothstep(x0, x1, xf);
        // x0 = r[p[p[p[rx0] + ry1] + rz0]];
        // x1 = r[p[p[p[rx1] + ry1] + rz0]];
        // println("HC Node [%s, %s, %s] %s", rx0, ry1, rz0, x0);
        // println("HC Node [%s, %s, %s] %s", rx1, ry1, rz0, x1);
        // y1 = smoothstep(x0, x1, xf);
        // z0 = smoothstep(y0, y1, yf);
        //
        // x0 = r[p[p[p[rx0] + ry0] + rz1]];
        // x1 = r[p[p[p[rx1] + ry0] + rz1]];
        // println("HC Node [%s, %s, %s] %s", rx0, ry0, rz1, x0);
        // println("HC Node [%s, %s, %s] %s", rx1, ry0, rz1, x1);
        // y0 = smoothstep(x0, x1, xf);
        // x0 = r[p[p[p[rx0] + ry1] + rz1]];
        // x1 = r[p[p[p[rx1] + ry1] + rz1]];
        // println("HC Node [%s, %s, %s] %s", rx0, ry1, rz1, x0);
        // println("HC Node [%s, %s, %s] %s", rx1, ry1, rz1, x1);
        // y1 = smoothstep(x0, x1, xf);
        // z1 = smoothstep(y0, y1, yf);
        //
        // result = smoothstep(z0, z1, zf);
        // println("HC:", result);
        //
        // int dimension = coord.length;
        //
        // int[]    vi = new int[dimension];
        // double[] vf = new double[dimension];
        // int[]    rv = new int[dimension << 1];
        //
        // int i, j, k, n;
        // for (i = 0; i < dimension; i++)
        // {
        //     j = i << 1;
        //
        //     vi[i] = (int) Math.floor(coord[i]);
        //     vf[i] = coord[i] - vi[i];
        //
        //     rv[j]     = vi[i] & tableSizeMask;
        //     rv[j + 1] = (rv[j] + 1) & tableSizeMask;
        // }
        //
        // double[] nodes     = new double[1 << dimension];
        // int[]    nodeCoord = new int[dimension];
        // for (i = 0, n = nodes.length; i < n; i++)
        // {
        //     for (j = 0; j < dimension; j++) nodeCoord[j] = rv[(j << 1) + ((i >> j) & 1)];
        //     nodes[i] = r[hash(p, nodeCoord)];
        //     println("ND Node", Arrays.toString(nodeCoord), nodes[i]);
        // }
        //
        // n = nodes.length;
        // for (i = 0; i < dimension; i++)
        // {
        //     n >>= 1;
        //     for (j = 0; j < n; j++)
        //     {
        //         k        = j << 1;
        //         nodes[j] = smoothstep(nodes[k], nodes[k + 1], vf[i]);
        //     }
        // }
        // println("ND:", nodes[0]);
    }
}