package engine.gui.shapes;

import engine.render.Texture;

public class ShapeStatePGGUI
{
    public final String id;
    
    public Texture texture;
    
    public boolean freshSurface       = false;
    public String  cachedBackgroundId = null;
    
    public StateTransitionPGGUI transition;
    
    public ShapeStatePGGUI(String id)
    {
        this.id = id;
    }
    
    public String id()
    {
        return this.id;
    }
    
    public Texture texture()
    {
        if (this.transition != null) return this.transition.produceBlendedResult();
        return this.texture;
    }
    
    public void update(double deltaTime)
    {
        if (this.transition != null)
        {
            this.transition.update(deltaTime);
            this.freshSurface = true;
            if (this.transition.finished()) this.transition = null;
        }
    }
}
