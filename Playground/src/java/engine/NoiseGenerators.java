package engine;

import engine.color.Color;
import engine.color.Colorc;
import engine.render.Texture;
import engine.util.Noise;
import engine.util.PerlinNoise;
import engine.util.ValueNoise;
import engine.util.WorleyNoise;

import java.util.function.Function;

import static engine.util.Util.map;
import static engine.util.Util.println;

public class NoiseGenerators extends Engine
{
    @SuppressWarnings("SameParameterValue")
    Texture genTexture(Noise noise, double xMin, double xMax, int xCount, double yMin, double yMax, int yCount)
    {
        Texture texture = new Texture(xCount, yCount, 3);
        
        Color color = new Color();
        for (int j = 0; j < texture.height(); j++)
        {
            for (int i = 0; i < texture.width(); i++)
            {
                double x = map(i, 0, texture.width() - 1, xMin, xMax);
                double y = map(j, 0, texture.height() - 1, yMin, yMax);
                
                color.set(noise.noise(x, y) * 0.5 + 0.5);
                
                texture.setPixel(i, j, color);
            }
        }
        
        return texture;
    }
    
    @SuppressWarnings("SameParameterValue")
    void tintTexture(Texture texture, Color tint)
    {
        Colorc textureColor;
        Color  color = new Color();
        for (int j = 0; j < texture.height(); j++)
        {
            for (int i = 0; i < texture.width(); i++)
            {
                textureColor = texture.getPixel(i, j);
                
                color.r(textureColor.r() * tint.r() / 255);
                color.g(textureColor.g() * tint.g() / 255);
                color.b(textureColor.b() * tint.b() / 255);
                
                texture.setPixel(i, j, color);
            }
        }
    }
    
    @SuppressWarnings("SameParameterValue")
    void sectionTexture(Texture texture, int sections)
    {
        double s = 256.0 / sections;
        
        Colorc textureColor;
        Color  color = new Color();
        for (int j = 0; j < texture.height(); j++)
        {
            for (int i = 0; i < texture.width(); i++)
            {
                textureColor = texture.getPixel(i, j);
                
                color.r((int) (textureColor.r() / (int) s * s));
                color.g((int) (textureColor.g() / (int) s * s));
                color.b((int) (textureColor.b() / (int) s * s));
                
                texture.setPixel(i, j, color);
            }
        }
    }
    
    @SuppressWarnings("SameParameterValue")
    void brightenTexture(Texture texture, double factor)
    {
        Colorc textureColor;
        Color  color = new Color();
        for (int j = 0; j < texture.height(); j++)
        {
            for (int i = 0; i < texture.width(); i++)
            {
                textureColor = texture.getPixel(i, j);
                
                color.r(textureColor.rf() * factor);
                color.g(textureColor.gf() * factor);
                color.b(textureColor.bf() * factor);
                
                texture.setPixel(i, j, color);
            }
        }
    }
    
    @Override
    public void setup()
    {
        size(100, 100);
        
        int index = 0;
        
        Noise   noise;
        Texture texture;
        Color   tint = new Color();
        
        noise = new PerlinNoise();
        noise.setSeed(1337);
        texture = genTexture(noise, 0, 1, 512, 0, 1, 512);
        texture.saveImage("noise/texture" + index++);
        
        noise = new PerlinNoise();
        noise.setSeed(1337);
        texture = genTexture(noise, 0, 4, 512, 0, 4, 512);
        tintTexture(texture, tint.set(100, 200, 50));
        sectionTexture(texture, 8);
        texture.saveImage("noise/texture" + index++);
        
        noise = new ValueNoise();
        noise.setSeed(1337);
        noise.octaves(4);
        texture = genTexture(noise, 0, 4, 512, 0, 4, 512);
        tintTexture(texture, tint.set(100, 200, 50));
        sectionTexture(texture, 16);
        texture.saveImage("noise/texture" + index++);
        
        noise = new WorleyNoise();
        noise.setSeed(1337);
        noise.setProperty("distanceFunction", (Function<double[], Double>) arr -> arr[1] - arr[0]);
        texture = genTexture(noise, 0, 4, 512, 0, 4, 512);
        brightenTexture(texture, 2);
        tintTexture(texture, tint.set(100, 200, 50));
        sectionTexture(texture, 16);
        texture.saveImage("noise/texture" + index++);
        
        println("%s images created", index);
        
        stop();
    }
    
    public static void main(String[] args)
    {
        start(new NoiseGenerators());
    }
}