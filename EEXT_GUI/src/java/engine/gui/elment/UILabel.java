package engine.gui.elment;

import engine.Engine;
import engine.color.Colorc;
import engine.gui.GUI;
import engine.gui.UIElement;
import engine.gui.interfaces.IUIContainerLike;
import engine.gui.util.Rectc;
import engine.render.Font;
import engine.render.TextAlign;
import engine.render.Texture;
import engine.util.Logger;
import org.joml.Vector2i;
import org.joml.Vector2ic;

import static engine.Engine.*;

public class UILabel extends UIElement
{
    private static final Logger LOGGER = new Logger();
    
    public UILabel(String text, Rectc rect, IUIContainerLike container, UIElement parent, String objectID)
    {
        super(rect, container, parent, objectID, "label");
        
        this.text = text;
        rebuildTheme();
    }
    
    @Override
    public void rebuild()
    {
        this.texture = new Texture(this.rect.width(), this.rect.height(), 4);
    }
    
    // ----------------
    // ----- TEXT -----
    // ----------------
    
    private String text = "";
    
    public String text()
    {
        return this.text;
    }
    
    public void text(String text)
    {
        if (text != null && !this.text.equals(text))
        {
            String prev = this.text;
            this.text = text;
            textChanged(prev, this.text);
        }
    }
    
    protected void textChanged(String prevText, String newText)
    {
        // rebuild();
    }
    
    // --------------------
    // ----- THEMEING -----
    // --------------------
    
    private Font font;
    
    private Colorc textColor;
    private Colorc bgColor;
    
    private       Colorc   textShadowColor;
    private       boolean  enableTextShadow = false;
    private       int      textShadowSize   = 1;
    private final Vector2i textShadowOffset = new Vector2i();
    
    public Font font()
    {
        return this.font;
    }
    
    public Colorc textColor()
    {
        return this.textColor;
    }
    
    public Colorc bgColor()
    {
        return this.bgColor;
    }
    
    public Colorc textShadowColor()
    {
        return this.textShadowColor;
    }
    
    public boolean enableTextShadow()
    {
        return this.enableTextShadow;
    }
    
    public int textShadowSize()
    {
        return this.textShadowSize;
    }
    
    public Vector2ic textShadowOffset()
    {
        return this.textShadowOffset;
    }
    
    @Override
    public void rebuildTheme()
    {
        boolean anyChanged = false;
        
        Font font = GUI.theme().getFont(this.objectIDs, this.elementIDs);
        if (this.font == null || !this.font.equals(font))
        {
            this.font  = font;
            anyChanged = true;
        }
        
        Colorc textColor = GUI.theme().getColor(this.objectIDs, this.elementIDs, "normal_text");
        if (this.textColor == null || !this.textColor.equals(textColor))
        {
            this.textColor = textColor;
            anyChanged     = true;
        }
        
        Colorc bgColor = GUI.theme().getColor(this.objectIDs, this.elementIDs, "dark_bg");
        if (this.bgColor == null || !this.bgColor.equals(bgColor))
        {
            this.bgColor = bgColor;
            anyChanged   = true;
        }
        
        Colorc textShadowColor = GUI.theme().getColor(this.objectIDs, this.elementIDs, "text_shadow");
        if (this.textShadowColor == null || !this.textShadowColor.equals(textShadowColor))
        {
            this.textShadowColor = textShadowColor;
            anyChanged           = true;
        }
        
        String enableTextShadowString = GUI.theme().getMiscData(this.objectIDs, this.elementIDs, "text_shadow");
        if (enableTextShadowString != null)
        {
            boolean enableTextShadow = Integer.parseInt(enableTextShadowString) == 1;
            if (this.enableTextShadow != enableTextShadow)
            {
                this.enableTextShadow = enableTextShadow;
                anyChanged            = true;
            }
        }
        
        String textShadowSizeString = GUI.theme().getMiscData(this.objectIDs, this.elementIDs, "text_shadow_size");
        if (textShadowSizeString != null)
        {
            int textShadowSize = Integer.parseInt(textShadowSizeString);
            if (this.textShadowSize != textShadowSize)
            {
                this.textShadowSize = textShadowSize;
                anyChanged          = true;
            }
        }
        
        String textShadowOffsetString = GUI.theme().getMiscData(this.objectIDs, this.elementIDs, "text_shadow_offset");
        if (textShadowOffsetString != null)
        {
            String[] offsets = textShadowOffsetString.split(",");
            int      x, y;
            if (!this.textShadowOffset.equals(x = Integer.parseInt(offsets[0]), y = Integer.parseInt(offsets[1])))
            {
                this.textShadowOffset.set(x, y);
                anyChanged = true;
            }
        }
        
        if (anyChanged) rebuild();
    }
    
    // -------------------
    // ----- Drawing -----
    // -------------------
    
    @Override
    protected void drawElement(double elapsedTime, double mouseX, double mouseY)
    {
        int textWidth  = (int) Math.ceil(this.font.getStringWidth(this.text));
        int textHeight = (int) Math.ceil(this.font.getStringHeight(this.text));
        
        if (textWidth > this.rect.width() || textHeight > this.rect.height())
        {
            int widthOverlap  = this.rect.width() - textWidth;
            int heightOverlap = this.rect.height() - textHeight;
            UILabel.LOGGER.warning("Label is too small for text: %s Overlap(%s,%s)", this.text, widthOverlap, heightOverlap);
        }
        
        target(this.texture);
        
        clear(this.bgColor);
        
        textAlign(TextAlign.CENTER);
        if (this.enableTextShadow)
        {
            stroke(this.textShadowColor);
            for (int i = -this.textShadowSize; i <= this.textShadowSize; i++)
            {
                Engine.text(this.text, this.rect.centerX() + this.textShadowOffset.x + i, this.rect.centerY() + this.textShadowOffset.y);
                Engine.text(this.text, this.rect.centerX() + this.textShadowOffset.x, this.rect.centerY() + this.textShadowOffset.y + i);
                Engine.text(this.text, this.rect.centerX() + this.textShadowOffset.x + i, this.rect.centerY() + this.textShadowOffset.y + i);
                Engine.text(this.text, this.rect.centerX() + this.textShadowOffset.x - i, this.rect.centerY() + this.textShadowOffset.y + i);
            }
        }
        stroke(this.textColor);
        Engine.text(this.text, this.rect.centerX(), this.rect.centerY());
    }
}
