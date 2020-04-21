package engine.gui.util;

import org.joml.Vector2i;
import org.joml.Vector2ic;

public class Rect implements Rectc
{
    private int left, top, width, height;
    
    private final Vector2i topLeft     = new Vector2i();
    private final Vector2i topRight    = new Vector2i();
    private final Vector2i bottomLeft  = new Vector2i();
    private final Vector2i bottomRight = new Vector2i();
    private final Vector2i midLeft     = new Vector2i();
    private final Vector2i midRight    = new Vector2i();
    private final Vector2i midTop      = new Vector2i();
    private final Vector2i midBottom   = new Vector2i();
    private final Vector2i center      = new Vector2i();
    private final Vector2i pos         = new Vector2i();
    private final Vector2i size        = new Vector2i();
    
    public Rect()
    {
        this.left   = 0;
        this.top    = 0;
        this.width  = 1;
        this.height = 1;
    }
    
    public Rect(int left, int top, int width, int height)
    {
        this.left   = left;
        this.top    = top;
        this.width  = width;
        this.height = height;
    }
    
    public Rect(Vector2ic pos, Vector2ic size)
    {
        this.left   = pos.x();
        this.top    = pos.y();
        this.width  = size.x();
        this.height = size.y();
    }
    
    public Rect(Rectc other)
    {
        this.left   = other.left();
        this.top    = other.top();
        this.width  = other.width();
        this.height = other.height();
    }
    
    /**
     * @return The position of the left edge of the {@code Rect}. If {@link #width} is negative, then this will be > the right edge.
     */
    @Override
    public int left()
    {
        return this.left;
    }
    
    /**
     * Sets the left edge value of the {@code Rect}
     *
     * @param left The new left value.
     * @return This instance for call chaining.
     */
    public Rect left(int left)
    {
        this.left = left;
        return this;
    }
    
    /**
     * @return The position of the top edge of the {@code Rect}. If {@link #height} is negative, then this will be > the bottom edge.
     */
    @Override
    public int top()
    {
        return this.top;
    }
    
    /**
     * Sets the top edge value of the {@code Rect}
     *
     * @param top The new top value.
     * @return This instance for call chaining.
     */
    public Rect top(int top)
    {
        this.top = top;
        return this;
    }
    
    /**
     * @return The width of the {@code Rect} in screen space coordinates.
     */
    @Override
    public int width()
    {
        return this.width;
    }
    
    /**
     * Sets the width value of the {@code Rect}
     *
     * @param width The new width value.
     * @return This instance for call chaining.
     */
    public Rect width(int width)
    {
        this.width = width;
        return this;
    }
    
    /**
     * @return The height of the {@code Rect} in screen space coordinates.
     */
    @Override
    public int height()
    {
        return this.height;
    }
    
    /**
     * Sets the height value of the {@code Rect}
     *
     * @param height The new height value.
     * @return This instance for call chaining.
     */
    public Rect height(int height)
    {
        this.height = height;
        return this;
    }
    
    /**
     * @return The position of the right edge of the {@code Rect}. If {@link #width} is negative, then this will be < the left edge.
     */
    @Override
    public int right()
    {
        return this.left + this.width - 1;
    }
    
    /**
     * Sets the right edge value of the {@code Rect}
     *
     * @param right The new right value.
     * @return This instance for call chaining.
     */
    public Rect right(int right)
    {
        this.width = right - this.left + 1;
        return this;
    }
    
    /**
     * @return The position of the bottom edge of the {@code Rect}. If {@link #height} is negative, then this will be < the top edge.
     */
    @Override
    public int bottom()
    {
        return this.top + this.height - 1;
    }
    
    /**
     * Sets the bottom edge value of the {@code Rect}
     *
     * @param bottom The new bottom value.
     * @return This instance for call chaining.
     */
    public Rect bottom(int bottom)
    {
        this.height = bottom - this.top + 1;
        return this;
    }
    
    /**
     * @return The left most x value of the {@code Rect}, regardless of the sign of {@link #width}.
     */
    @Override
    public int x1()
    {
        return Math.min(left(), right());
    }
    
    /**
     * @return The top most y value of the {@code Rect}, regardless of the sign of {@link #height}.
     */
    @Override
    public int y1()
    {
        return Math.min(top(), bottom());
    }
    
    /**
     * @return The right most x value of the {@code Rect}, regardless of the sign of {@link #width}.
     */
    @Override
    public int x2()
    {
        return Math.max(left(), right());
    }
    
    /**
     * @return The bottom most y value of the {@code Rect}, regardless of the sign of {@link #height}.
     */
    @Override
    public int y2()
    {
        return Math.max(top(), bottom());
    }
    
    /**
     * @return The center x value of the {@code Rect}.
     */
    @Override
    public int centerX()
    {
        return left() + (width() >> 1);
    }
    
    /**
     * Sets the center x value of the {@code Rect}
     *
     * @param centerX The new center x value.
     * @return This instance for call chaining.
     */
    public Rect centerX(int centerX)
    {
        this.left = centerX - (this.width >> 1);
        return this;
    }
    
    /**
     * @return The center y value of the {@code Rect}.
     */
    @Override
    public int centerY()
    {
        return top() + (height() >> 1);
    }
    
    /**
     * Sets the center y value of the {@code Rect}
     *
     * @param centerY The new center y value.
     * @return This instance for call chaining.
     */
    public Rect centerY(int centerY)
    {
        this.top = centerY - (this.height >> 1);
        return this;
    }
    
    /**
     * @return The topLeft corner of the {@code Rect}. This is not the absolute topLeft of the {@code Rect} as if a {@link #size} value is negative.
     */
    @Override
    public Vector2ic topLeft()
    {
        return this.topLeft.set(top(), left());
    }
    
    /**
     * Sets the top and left edges of the {@code Rect}.
     *
     * @param left The new left value.
     * @param top  The new top value.
     * @return This instance for call chaining.
     */
    public Rect topLeft(int left, int top)
    {
        return left(left).top(top);
    }
    
    /**
     * Sets the top and left edges of the {@code Rect}.
     *
     * @param topLeft The new topLeft value.
     * @return This instance for call chaining.
     */
    public Rect topLeft(Vector2ic topLeft)
    {
        return left(topLeft.x()).top(topLeft.y());
    }
    
    /**
     * @return The topRight corner of the {@code Rect}. This is not the absolute topRight of the {@code Rect} as if a {@link #size} value is negative.
     */
    @Override
    public Vector2ic topRight()
    {
        return this.topRight.set(top(), right());
    }
    
    /**
     * Sets the top and right edges of the {@code Rect}.
     *
     * @param right The new right value.
     * @param top   The new top value.
     * @return This instance for call chaining.
     */
    public Rect topRight(int right, int top)
    {
        return right(right).top(top);
    }
    
    /**
     * Sets the top and right edges of the {@code Rect}.
     *
     * @param topRight The new topRight value.
     * @return This instance for call chaining.
     */
    public Rect topRight(Vector2ic topRight)
    {
        return right(topRight.x()).top(topRight.y());
    }
    
    /**
     * @return The bottomLeft corner of the {@code Rect}. This is not the absolute bottomLeft of the {@code Rect} as if a {@link #size} value is negative.
     */
    @Override
    public Vector2ic bottomLeft()
    {
        return this.bottomLeft.set(bottom(), left());
    }
    
    /**
     * Sets the bottom and left edges of the {@code Rect}.
     *
     * @param left   The new left value.
     * @param bottom The new bottom value.
     * @return This instance for call chaining.
     */
    public Rect bottomLeft(int left, int bottom)
    {
        return left(left).bottom(bottom);
    }
    
    /**
     * Sets the bottom and left edges of the {@code Rect}.
     *
     * @param bottomLeft The new bottomLeft value.
     * @return This instance for call chaining.
     */
    public Rect bottomLeft(Vector2ic bottomLeft)
    {
        return left(bottomLeft.x()).bottom(bottomLeft.y());
    }
    
    /**
     * @return The bottomRight corner of the {@code Rect}. This is not the absolute bottomRight of the {@code Rect} as if a {@link #size} value is negative.
     */
    @Override
    public Vector2ic bottomRight()
    {
        return this.bottomRight.set(bottom(), right());
    }
    
    /**
     * Sets the bottom and right edges of the {@code Rect}.
     *
     * @param right  The new right value.
     * @param bottom The new bottom value.
     * @return This instance for call chaining.
     */
    public Rect bottomRight(int right, int bottom)
    {
        return right(right).bottom(bottom);
    }
    
    /**
     * Sets the bottom and right edges of the {@code Rect}.
     *
     * @param bottomRight The new bottomRight value.
     * @return This instance for call chaining.
     */
    public Rect bottomRight(Vector2ic bottomRight)
    {
        return right(bottomRight.x()).bottom(bottomRight.y());
    }
    
    /**
     * @return The midLeft point of the {@code Rect}. This is not the absolute midLeft of the {@code Rect} as if a {@link #size} value is negative.
     */
    @Override
    public Vector2ic midLeft()
    {
        return this.midLeft.set(left(), centerY());
    }
    
    /**
     * Sets the left edge and center y values of the {@code Rect}.
     *
     * @param left    The new left value.
     * @param centerY The new centerY value.
     * @return This instance for call chaining.
     */
    public Rect midLeft(int left, int centerY)
    {
        return left(left).centerY(centerY);
    }
    
    /**
     * Sets the left edge and center y values of the {@code Rect}.
     *
     * @param midLeft The new midLeft value.
     * @return This instance for call chaining.
     */
    public Rect midLeft(Vector2ic midLeft)
    {
        return left(midLeft.x()).centerY(midLeft.y());
    }
    
    /**
     * @return The midTop point of the {@code Rect}. This is not the absolute midTop of the {@code Rect} as if a {@link #size} value is negative.
     */
    @Override
    public Vector2ic midTop()
    {
        return this.midTop.set(centerX(), top());
    }
    
    /**
     * Sets the top edge and center x values of the {@code Rect}.
     *
     * @param centerX The new centerX value.
     * @param top     The new top value.
     * @return This instance for call chaining.
     */
    public Rect midTop(int centerX, int top)
    {
        return centerX(centerX).top(top);
    }
    
    /**
     * Sets the top edge and center x values of the {@code Rect}.
     *
     * @param midTop The new top value.
     * @return This instance for call chaining.
     */
    public Rect midTop(Vector2ic midTop)
    {
        return centerX(midTop.x()).top(midTop.y());
    }
    
    /**
     * @return The midRight point of the {@code Rect}. This is not the absolute midRight of the {@code Rect} as if a {@link #size} value is negative.
     */
    @Override
    public Vector2ic midRight()
    {
        return this.midRight.set(right(), centerY());
    }
    
    /**
     * Sets the right edge and center y values of the {@code Rect}.
     *
     * @param right   The new right value.
     * @param centerY The new centerY value.
     * @return This instance for call chaining.
     */
    public Rect midRight(int right, int centerY)
    {
        return right(right).centerY(centerY);
    }
    
    /**
     * Sets the right edge and center y values of the {@code Rect}.
     *
     * @param midRight The new centerY value.
     * @return This instance for call chaining.
     */
    public Rect midRight(Vector2ic midRight)
    {
        return right(midRight.x()).centerY(midRight.y());
    }
    
    /**
     * @return The midBottom point of the {@code Rect}. This is not the absolute midBottom of the {@code Rect} as if a {@link #size} value is negative.
     */
    @Override
    public Vector2ic midBottom()
    {
        return this.midBottom.set(centerX(), bottom());
    }
    
    /**
     * Sets the bottom edge and center x values of the {@code Rect}.
     *
     * @param centerX The new centerX value.
     * @param bottom  The new bottom value.
     * @return This instance for call chaining.
     */
    public Rect midBottom(int centerX, int bottom)
    {
        return centerX(centerX).bottom(bottom);
    }
    
    /**
     * Sets the bottom edge and center x values of the {@code Rect}.
     *
     * @param midBottom The new bottom value.
     * @return This instance for call chaining.
     */
    public Rect midBottom(Vector2ic midBottom)
    {
        return centerX(midBottom.x()).bottom(midBottom.y());
    }
    
    /**
     * @return The center point of the {@code Rect}.
     */
    @Override
    public Vector2ic center()
    {
        return this.center.set(centerX(), centerY());
    }
    
    /**
     * Sets the center values of the {@code Rect}.
     *
     * @param centerX The new centerX value.
     * @param centerY The new centerY value.
     * @return This instance for call chaining.
     */
    public Rect center(int centerX, int centerY)
    {
        return centerX(centerX).centerY(centerY);
    }
    
    /**
     * Sets the center values of the {@code Rect}.
     *
     * @param center The new centerY value.
     * @return This instance for call chaining.
     */
    public Rect center(Vector2ic center)
    {
        return centerX(center.x()).centerY(center.y());
    }
    
    /**
     * @return The pos of the {@code Rect}
     */
    @Override
    public Vector2ic pos()
    {
        return this.pos.set(left(), top());
    }
    
    /**
     * Sets the position of the {@code Rect}.
     *
     * @param x The new x value.
     * @param y The new y value.
     * @return This instance for call chaining.
     */
    public Rect pos(int x, int y)
    {
        return left(x).top(y);
    }
    
    /**
     * Sets the position of the {@code Rect}.
     *
     * @param pos The new pos value.
     * @return This instance for call chaining.
     */
    public Rect pos(Vector2ic pos)
    {
        return left(pos.x()).top(pos.y());
    }
    
    /**
     * @return The size of the {@code Rect}. Can be negative
     */
    @Override
    public Vector2ic size()
    {
        return this.size.set(width(), height());
    }
    
    /**
     * Sets the size of the {@code Rect}.
     *
     * @param width  The new width value.
     * @param height The new height value.
     * @return This instance for call chaining.
     */
    public Rect size(int width, int height)
    {
        return width(width).height(height);
    }
    
    /**
     * Sets the size of the {@code Rect}.
     *
     * @param size The new size value.
     * @return This instance for call chaining.
     */
    public Rect size(Vector2ic size)
    {
        return width(size.x()).height(size.y());
    }
    
    /**
     * Sets the value of this {@code Rect}.
     *
     * @param left   The new left value.
     * @param top    The new top value.
     * @param width  The new width value.
     * @param height The new height value.
     * @return This instance for call chaining.
     */
    public Rect set(int left, int top, int width, int height)
    {
        this.left   = left;
        this.top    = top;
        this.width  = width;
        this.height = height;
        return this;
    }
    
    /**
     * Sets the value of this {@code Rect}.
     *
     * @param pos  The new position.
     * @param size The new size.
     * @return This instance for call chaining.
     */
    public Rect set(Vector2ic pos, Vector2ic size)
    {
        this.left   = pos.x();
        this.top    = pos.y();
        this.width  = size.x();
        this.height = size.y();
        return this;
    }
    
    /**
     * Sets the value of this {@code Rect}.
     *
     * @param other The {@code Rect} to copy.
     * @return This instance for call chaining.
     */
    public Rect set(Rectc other)
    {
        this.left   = other.left();
        this.top    = other.top();
        this.width  = other.width();
        this.height = other.height();
        return this;
    }
    
    /**
     * Copy the {@code Rect}.
     *
     * @param result The object to store the copy.
     * @return The result {@code Rect} having the same position and size as the original.
     */
    @Override
    public Rect copy(Rect result)
    {
        return result.set(this);
    }
    
    /**
     * Copy the {@code Rect}.
     *
     * @return A new {@code Rect} having the same position and size as the original.
     */
    @Override
    public Rect copy()
    {
        return new Rect(this);
    }
    
    /**
     * Moves the {@code Rect}.
     *
     * @param x      The x amount to move the {@code Rect} by. Can be negative.
     * @param y      The y amount to move the {@code Rect} by. Can be negative.
     * @param result The object to store the moved {@code Rect}.
     * @return The result.
     */
    @Override
    public Rect move(int x, int y, Rect result)
    {
        result.left(left() + x);
        result.top(top() + y);
        return result;
    }
    
    /**
     * Moves this {@code Rect}.
     *
     * @param x The x amount to move this {@code Rect} by. Can be negative.
     * @param y The y amount to move this {@code Rect} by. Can be negative.
     * @return This instance for call chaining.
     */
    public Rect move(int x, int y)
    {
        return move(x, y, thisOrNew());
    }
    
    /**
     * Moves the {@code Rect}.
     *
     * @param amount The vector to move the {@code Rect} by. Can be negative.
     * @param result The object to store the moved {@code Rect}.
     * @return The result.
     */
    @Override
    public Rect move(Vector2ic amount, Rect result)
    {
        return move(amount.x(), amount.y(), result);
    }
    
    /**
     * Moves this {@code Rect}.
     *
     * @param amount The vector to move the {@code Rect} by. Can be negative.
     * @return This instance for call chaining.
     */
    public Rect move(Vector2ic amount)
    {
        return move(amount.x(), amount.y(), thisOrNew());
    }
    
    /**
     * Grow or shrink the {@code Rect} size.
     *
     * @param width  The amount to change the width by. Can be negative.
     * @param height The amount to change the height by. Can be negative.
     * @param result The object to store the resized {@code Rect}.
     * @return The result.
     */
    @Override
    public Rect inflate(int width, int height, Rect result)
    {
        result.width(width() + width);
        result.height(height() + height);
        return result;
    }
    
    /**
     * Grow or shrink this {@code Rect} size.
     *
     * @param width  The amount to change the width by. Can be negative.
     * @param height The amount to change the height by. Can be negative.
     * @return This instance for call chaining.
     */
    public Rect inflate(int width, int height)
    {
        return inflate(width, height, thisOrNew());
    }
    
    /**
     * Grow or shrink the {@code Rect} size.
     *
     * @param amount The vector to change the size by. Can be negative.
     * @param result The object to store the resized {@code Rect}.
     * @return The result.
     */
    @Override
    public Rect inflate(Vector2ic amount, Rect result)
    {
        return inflate(amount.x(), amount.y(), result);
    }
    
    /**
     * Grow or shrink this {@code Rect} size.
     *
     * @param amount The vector to change the size by. Can be negative.
     * @return This instance for call chaining.
     */
    public Rect inflate(Vector2ic amount)
    {
        return inflate(amount.x(), amount.y(), thisOrNew());
    }
    
    /**
     * Moves the {@code Rect} inside another.
     * <p>
     * Calculates the {@code Rect} that is moved to be completely inside the other {@code Rect}.
     * If the {@code Rect} is too large to fit inside, it is centered inside the
     * other {@code Rect}, but its size is not changed.
     *
     * @param other  The {@code Rect} to clamp this {@code Rectc} in.
     * @param result The object to store the clamped {@code Rect}.
     * @return The result.
     */
    @Override
    public Rect clamp(Rectc other, Rect result)
    {
        result.width(Math.abs(width()));
        if (result.width() < other.width())
        {
            if (x1() < other.x1())
            {
                result.left(other.x1());
            }
            else if (other.x2() < x2())
            {
                result.left(other.x2() - result.width() + 1);
            }
            else
            {
                result.left(x1());
            }
        }
        else
        {
            result.centerX(other.centerX());
        }
        result.height(Math.abs(height()));
        if (result.height() < other.height())
        {
            if (y1() < other.y1())
            {
                result.top(other.y1());
            }
            else if (other.y2() < y2())
            {
                result.top(other.y2() - result.height() + 1);
            }
            else
            {
                result.top(y1());
            }
        }
        else
        {
            result.centerY(other.centerY());
        }
        return result;
    }
    
    /**
     * Clips a {@code Rect} inside another
     * <p>
     * Calculates the {@code Rect} that is cropped to be completely inside the
     * other {@code Rect}. If the two rectangles do not overlap to begin with,
     * a {@code Rect} of area 1 is returned.
     *
     * @param other  The {@code Rect} to clip this {@code Rectc} in.
     * @param result The object to store the clipped {@code Rect}.
     * @return The result.
     */
    @Override
    public Rect clip(Rectc other, Rect result)
    {
        if (!other.collide(this)) return result.set(left(), top(), 1, 1);
        result.left(x1() < other.x1() ? other.x1() : x1());
        result.right(other.x2() < x2() ? other.x2() : x2());
        result.top(y1() < other.y1() ? other.y1() : y1());
        result.bottom(other.y2() < y2() ? other.y2() : y2());
        return result;
    }
    
    /**
     * Joins two {@code Rect}s into one
     * <p>
     * Calculates the {@code Rect} that completely covers the area of the two provided {@code Rect}s.
     * There may be area inside the new Rect that is not covered by the originals.
     *
     * @param other  The {@code Rect} to union this {@code Rectc} with.
     * @param result The object to store the union {@code Rect}.
     * @return The result.
     */
    @Override
    public Rect union(Rectc other, Rect result)
    {
        result.left(Math.min(x1(), other.x1()));
        result.top(Math.min(y1(), other.y1()));
        result.right(Math.max(x2(), other.x2()));
        result.bottom(Math.max(y2(), other.y2()));
        return result;
    }
    
    /**
     * Resize and move a {@code Rect} with aspect ratio
     * <p>
     * Calculates the {@code Rect} that is moved and resized to fit another.
     * The aspect ratio of the original {@code Rect} is preserved, so the new
     * rectangle may be smaller than the target in either width or height.
     *
     * @param other  The {@code Rect} to fit this {@code Rectc} within.
     * @param result The object to store the fitted {@code Rect}.
     * @return The result.
     */
    @Override
    public Rect fit(Rectc other, Rect result)
    {
        int aspect = (int) width() / (int) height();
        result.size(other.width(), (int) (other.width() / aspect));
        if (result.height() > other.height()) result.size((int) (other.height() * aspect), other.height());
        
        result.center(other.center());
        return result;
    }
    
    /**
     * Correct negative sizes
     * <p>
     * This will flip the width or height of a rectangle if it has a negative size. The rectangle will remain in the same place, with only the sides swapped.
     *
     * @param result The object to store the normalized {@code Rect}.
     * @return The result.
     */
    @Override
    public Rect normalize(Rect result)
    {
        if (width() > 0)
        {
            result.left(left());
            result.width(width());
        }
        else
        {
            result.left(right());
            result.width(-width());
        }
        if (height() > 0)
        {
            result.top(top());
            result.height(height());
        }
        else
        {
            result.top(bottom());
            result.height(-height());
        }
        return result;
    }
    
    /**
     * Test if one {@code Rect} is inside another
     *
     * @param other The other {@code Rect}.
     * @return True if this {@code Rect} is completely inside the other {@code Rect}.
     */
    @Override
    public boolean contains(Rectc other)
    {
        return other.x1() <= x1() && x2() <= other.x2() && other.y1() <= y1() && y2() <= other.y2();
    }
    
    /**
     * Test if a point is inside a {@code Rect}.
     *
     * @param x The x point to test.
     * @param y The y point to test.
     * @return True if the given point is inside the {@code Rect}.
     */
    @Override
    public boolean collide(int x, int y)
    {
        return x1() <= x && x <= x2() && y1() <= y && y <= y2();
    }
    
    /**
     * Test if a point is inside a {@code Rect}.
     *
     * @param point The point to test.
     * @return True if the given point is inside the {@code Rect}.
     */
    @Override
    public boolean collide(Vector2ic point)
    {
        return collide(point.x(), point.y());
    }
    
    /**
     * Test if two {@code Rect}s overlap
     *
     * @param other The other {@code Rect}.
     * @return True if any portion of either {@code Rect} overlap
     */
    @Override
    public boolean collide(Rectc other)
    {
        return (Math.max(x1(), other.x1()) <= Math.min(x2(), other.x2()) && Math.max(y1(), other.y1()) <= Math.min(y2(), other.y2()));
    }
    
    protected Rect thisOrNew()
    {
        return this;
    }
}
