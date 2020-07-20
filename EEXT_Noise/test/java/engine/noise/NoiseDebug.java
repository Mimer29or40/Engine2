package engine.noise;

import engine.Engine;
import engine.color.Color;
import engine.render.Texture;

import static engine.util.Util.*;

public class NoiseDebug extends Engine
{
    Noise noise;
    
    final double xMin = -1;
    final double xMax = 2;
    
    final double yMin = -1;
    final double yMax = 2;
    
    Texture noiseTexture;
    
    @Override
    public void setup()
    {
        size(800, 800, 1, 1);
        
        noise = new ValueNoise(1337);
        noise = new PerlinNoise(1337);
        
        int w = screenWidth();
        int h = screenHeight();
        
        noiseTexture = new Texture(w, h);
        
        Color color = new Color(0, 255);
        for (int j = 0; j < h; j++)
        {
            double y = map(j, 0, h, yMin, yMax);
            for (int i = 0; i < w; i++)
            {
                double x = map(i, 0, w, xMin, xMax);
                
                noiseTexture.setPixel(i, j, color.set(noise.noise(x, y) * 0.5 + 0.5));
            }
        }
        noiseTexture.bindTexture().upload();
    }
    
    @Override
    public void draw(double elapsedTime)
    {
        texture(noiseTexture, 0, 0);
        
        double xScreen = mouse().x();
        double yScreen = mouse().y();
        
        double x = map(xScreen, 0, screenWidth() - 1, xMin, xMax);
        double y = map(yScreen, 0, screenHeight() - 1, yMin, yMax);
        
        int xi0 = (int) Math.floor(x);
        int yi0 = (int) Math.floor(y);
        int xi1 = xi0 + 1;
        int yi1 = yi0 + 1;
    
        double xi0Screen = map(xi0, xMin, xMax, 0, screenWidth());
        double yi0Screen = map(yi0, yMin, yMax, 0, screenHeight());
        double xi1Screen = map(xi1, xMin, xMax, 0, screenWidth());
        double yi1Screen = map(yi1, yMin, yMax, 0, screenHeight());
    
        double[] g0, g1, g2, g3;
    
        g0 = ((PerlinNoise) noise).grad2[noise.perm[noise.perm[xi0 & Noise.tableSizeMask] + yi0 & Noise.tableSizeMask]];
        g1 = ((PerlinNoise) noise).grad2[noise.perm[noise.perm[xi1 & Noise.tableSizeMask] + yi0 & Noise.tableSizeMask]];
        g2 = ((PerlinNoise) noise).grad2[noise.perm[noise.perm[xi0 & Noise.tableSizeMask] + yi1 & Noise.tableSizeMask]];
        g3 = ((PerlinNoise) noise).grad2[noise.perm[noise.perm[xi1 & Noise.tableSizeMask] + yi1 & Noise.tableSizeMask]];
    
        if (mouse().LEFT.held())
        {
            double dx0 = x - Math.floor(x);
            double dy0 = y - Math.floor(y);
        
            double dx1 = dx0 - 1.0;
            double dy1 = dy0 - 1.0;
        
            double x0 = g0[0] * dx0 + g0[1] * dy0;
            double x1 = g1[0] * dx1 + g1[1] * dy0;
            double y0 = smoothstep(x0, x1, dx0);
            double x2 = g2[0] * dx0 + g2[1] * dy1;
            double x3 = g3[0] * dx1 + g3[1] * dy1;
            double y1 = smoothstep(x2, x3, dx0);
            notification(String.format("Noise Value: %s", round(smoothstep(y0, y1, dy0), 6)));
        }
        
        stroke(Color.BLUE);
        weight(2);
        
        line(xScreen, yScreen, xi0Screen, yi0Screen);
        line(xScreen, yScreen, xi1Screen, yi0Screen);
        line(xScreen, yScreen, xi0Screen, yi1Screen);
        line(xScreen, yScreen, xi1Screen, yi1Screen);
        
        stroke(Color.GREEN);
        weight(2);
        
        line(xi0Screen, yi0Screen, xi0Screen + g0[0] * 30, yi0Screen + g0[1] * 30);
        line(xi1Screen, yi0Screen, xi1Screen + g1[0] * 30, yi0Screen + g1[1] * 30);
        line(xi0Screen, yi1Screen, xi0Screen + g2[0] * 30, yi1Screen + g2[1] * 30);
        line(xi1Screen, yi1Screen, xi1Screen + g3[0] * 30, yi1Screen + g3[1] * 30);
        
        stroke(Color.RED);
        weight(5);
        
        point(xi0Screen, yi0Screen);
        point(xi1Screen, yi0Screen);
        point(xi0Screen, yi1Screen);
        point(xi1Screen, yi1Screen);
    }
    
    public static void main(String[] args)
    {
        start(new NoiseDebug());
    }
}
