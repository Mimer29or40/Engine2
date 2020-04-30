package engine.gui.elment;

import engine.gui.UIElement;
import engine.gui.interfaces.IUIContainerLike;
import engine.gui.util.Rectc;

/**
 * A vertical scroll bar allows users to position a smaller visible area within a vertically
 * larger area.
 */
public class UIVerticalScrollBar extends UIElement
{
    
    public UIVerticalScrollBar(Rectc rect, IUIContainerLike container, UIElement parent, String objectID, String elementID)
    {
        super(rect, container, parent, objectID, elementID);
    }
}
