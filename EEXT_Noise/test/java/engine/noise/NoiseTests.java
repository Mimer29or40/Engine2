package engine.noise;

import engine.Engine;
import engine.color.Color;

import static engine.util.Util.map;

public class NoiseTests extends Engine
{
    Noise noise;
    
    boolean mode;
    
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
        noise = new ValueNoise(1337);
        noise.octaves(4);
    }
    
    @Override
    public void draw(double elapsedTime)
    {
        if (keyboard().SPACE.down()) mode = !mode;
        
        if (mode)
        {
            double vScale = screenHeight() >> 1;
            double hScale = map(mouse().x(), 0, screenWidth(), 0.01, 0.5);
    
            clear();
            translate(0, vScale);
            stroke(Color.WHITE);
            weight(2);
    
            double value;
            for (int i = 0, n = screenWidth(); i < n; i++)
            {
                value = this.noise.calculate(i * hScale + seconds() * 0.5) * vScale;
                point(i, value);
            }
        }
        else
        {
            int[] pixels = loadPixels();
    
            double hScale = map(mouse().x(), screenWidth(), 0, 0.01, 0.4);
            double vScale = map(mouse().y(), screenHeight(), 0, 0.01, 0.4);
    
            int n = screenWidth();
            int m = screenHeight();
    
            double t  = seconds() * 0.5;
            // double xt = seconds() * 0.5;
            // double yt = seconds() * 1.0;
    
            double value;
            for (int j = 0; j < m; j++)
            {
                for (int i = 0; i < n; i++)
                {
                    int index = (j * n + i) << 2;
            
                    value         = (this.noise.calculate(i * hScale, j * vScale, t) + 1) * 0.5;
                    pixels[index] = (int) (value * 255);
                    pixels[index + 1] = (int) (value * 255);
                    pixels[index + 2] = (int) (value * 255);
                    pixels[index + 3] = 255;
                }
            }
    
            updatePixels();
        }
    }
    
    public static void main(String[] args)
    {
        start(new NoiseTests());
        
        // Random random = new Random(1337);
        // double[] coord = new double[] {1.25, 0.75, 0.5};
        //
        // int dimension = coord.length;
        // // for (int i = 0; i < dimension; i++)
        // // {
        // //     coord[i] *= freq;
        // // }
        //
        // double[][] extrema = new double[dimension][2];
        // for (int i = 0; i < dimension; i++)
        // {
        //     extrema[i][0] = Math.floor(coord[i]);
        //     extrema[i][1] = extrema[i][0] + 1;
        // }
        //
        // double p[] = new double[1 << dimension];
        // for (int i = 0, n = p.length; i < n; i++)
        // {
        //     double pCoord[] = new double[dimension];
        //     for (int b = 0; b < dimension; b++)
        //     {
        //         pCoord[b] = extrema[b][0] + ((i >> b) & 1);
        //     }
        //     setSeed(pCoord);
        //     p[i] = this.random.nextDouble(-1, 1);
        // }
    }
}