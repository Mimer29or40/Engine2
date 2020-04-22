package engine.gui.shapes;

import org.joml.Vector2i;

import java.util.HashMap;

public class Shape
{
    public final Vector2i size = new Vector2i();
    
    public int shadowSize = 0;
    public int borderSize = 0;
    
    public final HashMap<String, String> states = new HashMap<>();
    
    public Shape(int width, int height, String[] states)
    {
        this.size.set(width, height);
        
        for (String state : states) this.states.put(state, state);
        
        
    }
}
