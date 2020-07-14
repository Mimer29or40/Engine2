package engine.gui.elment;

import engine.Engine;
import engine.color.Colorc;
import engine.gui.EEXT_GUI;
import engine.gui.EEXT_GUI;
import engine.gui.UIElement;
import engine.gui.util.Rectc;
import engine.render.Font;
import engine.render.TextAlign;
import engine.render.Texture;
import engine.util.Logger;
import org.joml.Vector2i;
import org.joml.Vector2ic;

import static engine.Engine.*;

/**
 * A label lets us display a single line of text with a single font style. It's a quick to
 * rebuild and simple alternative to the text box element.
 */
public class UILabel extends UIElement
{
    private static final Logger LOGGER = new Logger();
    
    public UILabel(String text, Rectc rect, UIElement parent, UIElement themeParent, String objectID)
    {
        super(rect, parent, themeParent, objectID, "label", new String[] {});
        
        text(text);
        rebuildTheme();
    }
    
    /**
     * Rebuilds the necessary textures and sub elements of the element.
     */
    @Override
    public void rebuild()
    {
        this.texture = new Texture(rect().width(), rect().height(), 3);
        
        redraw();
    }
    
    // ----------------------
    // ----- PROPERTIES -----
    // ----------------------
    
    private String text = "";
    
    /**
     * @return The text in the label.
     */
    public String text()
    {
        return this.text;
    }
    
    /**
     * Sets the text in the label.
     *
     * @param text The new text.
     */
    public void text(String text)
    {
        if (text != null && !this.text.equals(text))
        {
            String prev = this.text;
            this.text = text;
            textChanged(prev, this.text);
        }
    }
    
    /**
     * Called whenever the label text is changed.
     *
     * @param prevText The previous text.
     * @param text     The new text.
     */
    protected void textChanged(String prevText, String text)
    {
        redraw();
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
    
    /**
     * @return The current font to use in the label.
     */
    public Font font()
    {
        return this.font;
    }
    
    /**
     * @return The color of the text in the label.
     */
    public Colorc textColor()
    {
        return this.textColor;
    }
    
    /**
     * @return The color of the background text.
     */
    public Colorc bgColor()
    {
        return this.bgColor;
    }
    
    /**
     * @return The color of the shadow behind the text.
     */
    public Colorc textShadowColor()
    {
        return this.textShadowColor;
    }
    
    /**
     * @return If the text shadow is drawn.
     */
    public boolean enableTextShadow()
    {
        return this.enableTextShadow;
    }
    
    /**
     * @return The size of the shadow.
     */
    public int textShadowSize()
    {
        return this.textShadowSize;
    }
    
    /**
     * @return The offset of the text shadow.
     */
    public Vector2ic textShadowOffset()
    {
        return this.textShadowOffset;
    }
    
    /**
     * Rebuild all theme information. If anything changed, then redraw the states.
     */
    @Override
    public void rebuildTheme()
    {
        boolean anyChanged = false;
        
        Font font = EEXT_GUI.theme().getFont(this.objectIDs, this.elementIDs);
        if (this.font == null || !this.font.equals(font))
        {
            this.font  = font;
            anyChanged = true;
        }
        
        Colorc textColor = EEXT_GUI.theme().getColor(this.objectIDs, this.elementIDs, "normal_text");
        if (this.textColor == null || !this.textColor.equals(textColor))
        {
            this.textColor = textColor;
            anyChanged     = true;
        }
        
        Colorc bgColor = EEXT_GUI.theme().getColor(this.objectIDs, this.elementIDs, "dark_bg");
        if (this.bgColor == null || !this.bgColor.equals(bgColor))
        {
            this.bgColor = bgColor;
            anyChanged   = true;
        }
        
        Colorc textShadowColor = EEXT_GUI.theme().getColor(this.objectIDs, this.elementIDs, "text_shadow");
        if (this.textShadowColor == null || !this.textShadowColor.equals(textShadowColor))
        {
            this.textShadowColor = textShadowColor;
            anyChanged           = true;
        }
        
        String enableTextShadowString = EEXT_GUI.theme().getMiscData(this.objectIDs, this.elementIDs, "text_shadow");
        if (enableTextShadowString != null)
        {
            boolean enableTextShadow = Integer.parseInt(enableTextShadowString) == 1;
            if (this.enableTextShadow != enableTextShadow)
            {
                this.enableTextShadow = enableTextShadow;
                anyChanged            = true;
            }
        }
        
        String textShadowSizeString = EEXT_GUI.theme().getMiscData(this.objectIDs, this.elementIDs, "text_shadow_size");
        if (textShadowSizeString != null)
        {
            int textShadowSize = Integer.parseInt(textShadowSizeString);
            if (this.textShadowSize != textShadowSize)
            {
                this.textShadowSize = textShadowSize;
                anyChanged          = true;
            }
        }
        
        String textShadowOffsetString = EEXT_GUI.theme().getMiscData(this.objectIDs, this.elementIDs, "text_shadow_offset");
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
    
    /**
     * Draws the element and sub elements. This is called and past along to its children as long as it is alive.
     * <p>
     * Can be overridden.
     *
     * @param elapsedTime The amount of time in seconds since the last update.
     * @param mouseX      The x position of the mouse.
     * @param mouseY      The y position of the mouse.
     */
    @Override
    protected void drawElement(double elapsedTime, double mouseX, double mouseY)
    {
        int textWidth  = (int) Math.ceil(font().getStringWidth(text()));
        int textHeight = (int) Math.ceil(font().getStringHeight(text()));
        
        if (textWidth > rect().width() || textHeight > rect().height())
        {
            int widthOverlap  = rect().width() - textWidth;
            int heightOverlap = rect().height() - textHeight;
            UILabel.LOGGER.warning("Label is too small for text: %s Overlap(%s,%s)", text(), widthOverlap, heightOverlap);
        }
        
        target(this.texture);
        
        clear(bgColor());
        
        textFont(font());
        textAlign(TextAlign.CENTER);
        if (enableTextShadow())
        {
            stroke(textShadowColor());
            for (int i = -textShadowSize(); i <= textShadowSize(); i++)
            {
                Engine.text(text(), rect().centerX() + textShadowOffset().x() + i, rect().centerY() + textShadowOffset().y());
                Engine.text(text(), rect().centerX() + textShadowOffset().x(), rect().centerY() + textShadowOffset().y() + i);
                Engine.text(text(), rect().centerX() + textShadowOffset().x() + i, rect().centerY() + textShadowOffset().y() + i);
                Engine.text(text(), rect().centerX() + textShadowOffset().x() - i, rect().centerY() + textShadowOffset().y() + i);
            }
        }
        stroke(textColor());
        Engine.text(text(), rect().centerX(), rect().centerY());
    }
}
