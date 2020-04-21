package engine.gui;

import engine.gui.interfaces.IUIContainerLike;
import engine.gui.util.Rectc;

import java.util.ArrayList;

public class UIContainer extends UIElement implements IUIContainerLike
{
    protected final ArrayList<UIElement> elements = new ArrayList<>();
    
    public UIContainer(Rectc rect, IUIContainerLike parent)
    {
        super(rect, parent);
    }
    
    @Override
    public UIContainer getContainer()
    {
        return this;
    }
    
    public void addElement(UIElement element)
    {
        this.elements.add(element);
        recalculateLayers();
    }
    
    protected void recalculateLayers()
    {
        int n = this.elements.size();
        for (int i = 0; i < n; i++)
        {
            UIElement e1 = this.elements.get(i);
            e1.layer = e1.requestedLayer;
            for (int j = 0; j < i; j++)
            {
                UIElement e2 = this.elements.get(j);
                if (e1.layer >= e2.layer && e2.rect.collide(e1.rect)) e2.layer = e1.layer + 1;
            }
        }
    }
}
