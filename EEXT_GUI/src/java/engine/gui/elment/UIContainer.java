package engine.gui.elment;

import engine.gui.UIElement;
import engine.gui.util.Rectc;

public class UIContainer extends UIElement
{
    public UIContainer(Rectc rect, UIElement parent, UIElement themeParent, String objectID)
    {
        super(rect, parent, themeParent, objectID, "container", new String[] {});
    }
    
    /**
     * Rebuilds the necessary textures and sub elements of the element.
     */
    @Override
    public void rebuild()
    {
    
    }
}
