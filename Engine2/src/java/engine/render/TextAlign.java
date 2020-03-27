package engine.render;

public enum TextAlign
{
    TOP_LEFT(-1, -1),
    TOP(0, -1),
    TOP_RIGHT(1, -1),
    LEFT(-1, 0),
    CENTER(0, 0),
    RIGHT(1, 0),
    BOTTOM_LEFT(-1, 1),
    BOTTOM(0, 1),
    BOTTOM_RIGHT(1, 1),
    ;
    
    private final int horizontal, vertical;
    
    TextAlign(int horizontal, int vertical)
    {
        this.horizontal = horizontal;
        this.vertical   = vertical;
    }
    
    public int getHorizontal()
    {
        return this.horizontal;
    }
    
    public int getVertical()
    {
        return this.vertical;
    }
}
