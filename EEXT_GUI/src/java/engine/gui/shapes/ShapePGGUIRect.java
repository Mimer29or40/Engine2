package engine.gui.shapes;

import engine.gui.util.Rectc;
import engine.render.Texture;

import java.util.HashMap;

public class ShapePGGUIRect extends ShapePGGUI
{
    public boolean hasBeenResized = false;
    
    public ShapePGGUIRect(Rectc containingRect, HashMap<String, Object> themeing, String[] states)
    {
        super(containingRect, themeing, states);
        
        fullRebuildOnSizeChange();
    }
    
    @Override
    public void fullRebuildOnSizeChange()
    {
        super.fullRebuildOnSizeChange();
        
        int shadowWidth = Math.min(this.containingRect.width(), this.containingRect.height()) >> 1;
        if (this.shadowWidth > shadowWidth) this.shadowWidth = shadowWidth;
        if (this.shadowWidth < 0) this.shadowWidth = 0;
        
        int borderWidth = (Math.min(this.containingRect.width(), this.containingRect.height()) - (this.shadowWidth * 2)) >> 1;
        if (this.borderWidth > borderWidth) this.borderWidth = borderWidth;
        if (this.borderWidth < 0) this.borderWidth = 0;
        
        if (this.shadowWidth > 0)
        {
            this.clickAreaShape.set(this.containingRect.x() + this.shadowWidth,
                                    this.containingRect.y() + this.shadowWidth,
                                    this.containingRect.width() - (this.shadowWidth << 1),
                                    this.containingRect.height() - (this.shadowWidth << 1));
            // shadow = self.ui_manager.get_shadow(self.containing_rect.size, shadow_width = self.shadow_width, corner_radius = self.shadow_width)
            // if shadow is not None:
            //     self.base_surface = shadow
            // else:
            //     warnings.warn("shape created too small to fit in selected shadow width and corner radius")
            //     self.base_surface = pygame.Surface(self.containing_rect.size, flags = pygame.SRCALPHA, depth = 32)
        }
        else
        {
            this.clickAreaShape.set(this.containingRect);
            this.baseTexture = new Texture(this.containingRect.width(), this.containingRect.height());
        }
        
        computeAlignedTextRect();
        
        this.borderRect.set(this.shadowWidth, this.shadowWidth, this.clickAreaShape.width(), this.clickAreaShape.width());
        this.backgroundRect.set(this.borderWidth + this.shadowWidth,
                                this.borderWidth + this.shadowWidth,
                                this.clickAreaShape.width() - (this.borderWidth << 1),
                                this.clickAreaShape.height() - (this.borderWidth << 1));
    }
    
    @Override
    public void setDimensions(double width, double height)
    {
        if (width == this.containingRect.width() && height == this.containingRect.height()) return;
        this.containingRect.width((int) width);
        this.containingRect.height((int) height);
        this.clickAreaShape.width((int) width - (this.shadowWidth << 1));
        this.clickAreaShape.height((int) height - (this.shadowWidth << 1));
        
        this.hasBeenResized = true;
        
        fullRebuildOnSizeChange();
    }
    
    @Override
    public void redrawState(String state)
    {
        String borderColorState = state + "_border";
        String bgColorState = state + "_bg";
        String textColorState = state + "_text";
        String imageState = state + "_image";
        
        if (!this.themeing.containsKey("filled_bar") && !this.themeing.containsKey("filled_bar_width_percentage"))
        {
        
        }
    }
}
