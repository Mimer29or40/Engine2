package engine;

import engine.color.Color;

import static rutils.StringUtil.println;

public class ColorTest extends Engine
{
    /**
     * This method is called once the engine's environment is fully setup. This is only called once.
     * <p>
     * This is where you call {@link #size} to enable rendering. At which point you can create textures and render to them.
     */
    @Override
    public void setup()
    {
        
    }
    
    /**
     * This method is called once per frame.
     *
     * @param elapsedTime The time in seconds since the last frame.
     */
    @Override
    public void draw(double elapsedTime)
    {
        
    }
    
    /**
     * This method is called after the render loop has exited for any reason, exception or otherwise. This is only called once.
     */
    @Override
    public void destroy()
    {
        
    }
    
    public static void main(String[] args)
    {
        int  colorInt  = Color.WHITE.toInt();
        long colorLong = colorInt & 0x00000000FFFFFFFFL;
    
        println(colorInt, colorLong);
        // x &= 0x00000000FFFFFFFFL;
        // return set(x, x >> 8, x >> 16, x >> 24);
        println((colorLong) & 0xFF, (colorLong >> 8) & 0xFF, (colorLong >> 16) & 0xFF, (colorLong >> 24) & 0xFF);
        
        Color color = new Color().fromInt(Color.WHITE.toInt());
        
        println(color.toInt(), Color.WHITE.toInt());
        println(color, Color.WHITE);
    }
}
