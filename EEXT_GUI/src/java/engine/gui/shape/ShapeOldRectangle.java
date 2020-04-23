package engine.gui.shape;

import engine.color.Color;
import engine.gui.GUI;
import engine.gui.util.Rect;
import engine.render.Texture;

import java.util.HashMap;

public class ShapeOldRectangle extends ShapeOld
{
    private boolean hasBeenResized;
    
    public ShapeOldRectangle(int width, int height, HashMap<String, Object> themes, String[] states)
    {
        super(width, height, themes, states);
        
        this.hasBeenResized = false;
        
        fullRebuildOnSizeChange();
    }
    
    @Override
    public void fullRebuildOnSizeChange()
    {
        super.fullRebuildOnSizeChange();
        
        int shadowSize = Math.min(this.size.x(), this.size.y()) >> 1;
        if (this.shadowSize > shadowSize) this.shadowSize = shadowSize;
        if (this.shadowSize < 0) this.shadowSize = 0;
        
        int borderSize = (Math.min(this.size.x(), this.size.y()) - (this.shadowSize << 1)) >> 1;
        if (this.borderSize > borderSize) this.borderSize = borderSize;
        if (this.borderSize < 0) this.borderSize = 0;
        
        if (this.shadowSize > 0)
        {
            this.clickAreaRect.set(this.shadowSize, this.shadowSize, this.size.x() - (this.shadowSize << 1), this.size.y() - (this.shadowSize << 1));
            // TODO Generate shadows
            // shadow = self.ui_manager.get_shadow(self.containing_rect.size, shadow_width = self.shadow_width, corner_radius = self.shadow_width)
            // if shadow is not None:
            //     self.base_surface = shadow
            // else:
            //     warnings.warn("shape created too small to fit in selected shadow width and corner radius")
            //     self.base_surface = pygame.Surface(self.containing_rect.size, flags = pygame.SRCALPHA, depth = 32)
        }
        else
        {
            this.clickAreaRect.set(0, 0, this.size.x(), this.size.y());
            this.baseTexture = new Texture(this.size.x(), this.size.y(), 4);
        }
        
        computeAlignedTextRect();
        
        this.borderRect.set(this.clickAreaRect);
        this.backgroundRect.set(this.borderRect.x() + this.borderSize,
                                this.borderRect.y() + this.borderSize,
                                this.borderRect.width() - (this.borderSize >> 1),
                                this.borderRect.height() - (this.borderSize >> 1));
        
        redrawAllStates();
    }
    
    @Override
    public boolean collidePoint(int x, int y)
    {
        return this.clickAreaRect.collide(x, y);
    }
    
    @Override
    public void setDimensions(int width, int height)
    {
        if (width == this.size.x() && height == this.size.y()) return;
        
        this.size.set(width, height);
        
        this.hasBeenResized = true;
        fullRebuildOnSizeChange();
    }
    
    @Override
    public void redrawState(String state)
    {
        String borderColorState = state + "_border";
        String bgColorState     = state + "_bg";
        String textColorState   = state + "_text";
        String imageState       = state + "_image";
        
        StateOld currentState = this.states.get(state);
        
        Texture foundTexture = null;
        String  textureId    = null;
        // if (!this.themes.containsKey("filled_bar") && !this.themes.containsKey("filled_bar_width_percentage"))
        // {
        //     textureId = this.shapeCache.buildCacheId("rectangle", this.size, this.shadowSize, this.borderSize, this.themes.get(borderColorState), this.themes.get(bgColorState));
        //     foundTexture = this.shapeCache.findTextureInCache(textureId);
        // }
        if (foundTexture != null)
        {
            currentState.texture = foundTexture.copy();
        }
        else
        {
            GUI.INSTANCE.renderer().push();
            GUI.INSTANCE.renderer().target(currentState.texture = this.baseTexture.copy());
            
            GUI.INSTANCE.renderer().noStroke();
            if (this.borderSize > 0)
            {
                GUI.INSTANCE.renderer().fill((Color) this.themes.get(borderColorState));
                GUI.INSTANCE.renderer().rect(this.borderRect.x(), this.borderRect.y(), this.borderRect.width(), this.borderRect.height());
            }
            
            GUI.INSTANCE.renderer().fill((Color) this.themes.get(bgColorState));
            GUI.INSTANCE.renderer().rect(this.borderRect.x(), this.borderRect.y(), this.borderRect.width(), this.borderRect.height());
            
            if (this.themes.containsKey("filled_bar") && this.themes.containsKey("filled_bar_width_percentage"))
            {
                Rect barRect = new Rect(this.backgroundRect.x(),
                                        this.backgroundRect.y(),
                                        (int) this.themes.get("filled_bar_width_percentage"),
                                        this.backgroundRect.height());
                GUI.INSTANCE.renderer().fill((Color) this.themes.get("filled_bar"));
                GUI.INSTANCE.renderer().rect(barRect.x(), barRect.y(), barRect.width(), barRect.height());
            }
            
            // if (currentState.cachedBackground != null) this.shapeCache.removeUserFromCacheItem(currentState.cachedBackground);
            // if (!this.hasBeenResized && this.size.x() * this.size.y() < 40000 && textureId != null && currentState.texture.width() <= 1024 && currentState.texture.height() <= 1024)
            // {
            //     this.shapeCache.addTextureToCache(currentState.texture.copy());
            //     currentState.cachedBackground = textureId;
            // }
            GUI.INSTANCE.renderer().pop();
        }
        
        rebuildImageAndText(imageState, state, textColorState);
        this.hasFreshSurface = true;
    }
}
