package engine.gui.util;

import engine.color.Color;
import engine.color.Colorc;

public class ColorGradient
{
    public final int angle;
    
    public final Color color1 = new Color();
    public final Color color2 = new Color();
    public final Color color3;
    
    public ColorGradient(int angle, Colorc color1, Colorc color2, Colorc color3)
    {
        this.angle = angle;
        this.color1.set(color1);
        this.color2.set(color2);
        
        if (color3 == null)
        {
            this.color3 = null;
        }
        else
        {
            this.color3 = new Color(color3);
        }
        
        // TODO - Find a good way to do this. This may need to be Renderer thing.
    }
}
