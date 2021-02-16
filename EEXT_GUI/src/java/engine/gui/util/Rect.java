package engine.gui.util;

import org.joml.Vector2i;
import org.joml.Vector2ic;

import java.util.Objects;

@SuppressWarnings("UnusedReturnValue")
public class Rect implements Rectc
{
    private int x, y, w, h;
    
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
        this.x = this.y = 0;
        this.w = this.h = 1;
    }
    
    public Rect(int x, int y, int width, int height)
    {
        this.x = x;
        this.y = y;
        this.w = width;
        this.h = height;
    }
    
    public Rect(Vector2ic pos, Vector2ic size)
    {
        this.x = pos.x();
        this.y = pos.y();
        this.w = size.x();
        this.h = size.y();
    }
    
    public Rect(Rectc other)
    {
        this.x = other.x();
        this.y = other.y();
        this.w = other.width();
        this.h = other.height();
    }
    
    @Override
    public String toString()
    {
        return "Rect{" + "x=" + this.x + ", y=" + this.y + ", w=" + this.w + ", h=" + this.h + '}';
    }
    
    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rect rect = (Rect) o;
        return this.x == rect.x && this.y == rect.y && this.w == rect.w && this.h == rect.h;
    }
    
    @Override
    public int hashCode()
    {
        return Objects.hash(this.x, this.y, this.w, this.h);
    }
    
    /**
     * @return The x value of the {@code Rect} in screen space coordinates.
     */
    @Override
    public int x()
    {
        return this.x;
    }
    
    /**
     * Sets the x value of the {@code Rect} in screen space coordinates.
     *
     * @param x The new x value.
     * @return This instance for call chaining.
     */
    public Rect x(int x)
    {
        this.x = x;
        return this;
    }
    
    /**
     * @return The y value of the {@code Rect}. in screen space coordinates
     */
    @Override
    public int y()
    {
        return this.y;
    }
    
    /**
     * @return The pos of the {@code Rect}
     */
    @Override
    public Vector2ic pos()
    {
        return this.pos.set(x(), y());
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
        return x(x).y(y);
    }
    
    /**
     * Sets the position of the {@code Rect}.
     *
     * @param pos The new pos value.
     * @return This instance for call chaining.
     */
    public Rect pos(Vector2ic pos)
    {
        return x(pos.x()).y(pos.y());
    }
    
    /**
     * Sets the y value of the {@code Rect} in screen space coordinates.
     *
     * @param y The new y value.
     * @return This instance for call chaining.
     */
    public Rect y(int y)
    {
        this.y = y;
        return this;
    }
    
    /**
     * @return The width of the {@code Rect} in screen space coordinates.
     */
    @Override
    public int width()
    {
        return this.w;
    }
    
    /**
     * Sets the width value of the {@code Rect}
     *
     * @param width The new width value.
     * @return This instance for call chaining.
     */
    public Rect width(int width)
    {
        this.w = width;
        return this;
    }
    
    /**
     * @return The height of the {@code Rect} in screen space coordinates.
     */
    @Override
    public int height()
    {
        return this.h;
    }
    
    /**
     * Sets the height value of the {@code Rect}
     *
     * @param height The new height value.
     * @return This instance for call chaining.
     */
    public Rect height(int height)
    {
        this.h = height;
        return this;
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
     * @return The position of the left edge of the {@code Rect}. If {@link #w} is negative, then this will be > the right edge.
     */
    @Override
    public int left()
    {
        return this.w >= 0 ? this.x : this.x + this.w + 1;
    }
    
    /**
     * Sets the left edge value of the {@code Rect}
     *
     * @param left The new left value.
     * @return This instance for call chaining.
     */
    public Rect left(int left)
    {
        int dif = left - left();
        if (this.w >= 0)
        {
            this.x = left;
            this.w -= dif;
        }
        else
        {
            this.w += dif;
        }
        return this;
    }
    
    /**
     * @return The position of the top edge of the {@code Rect}. If {@link #h} is negative, then this will be > the bottom edge.
     */
    @Override
    public int top()
    {
        return this.h >= 0 ? this.y : this.y + this.h + 1;
    }
    
    /**
     * Sets the top edge value of the {@code Rect}
     *
     * @param top The new top value.
     * @return This instance for call chaining.
     */
    public Rect top(int top)
    {
        int dif = top - top();
        if (this.h >= 0)
        {
            this.y = top;
            this.h -= dif;
        }
        else
        {
            this.h += dif;
        }
        return this;
    }
    
    /**
     * @return The position of the right edge of the {@code Rect}. If {@link #w} is negative, then this will be < the left edge.
     */
    @Override
    public int right()
    {
        return this.w < 0 ? this.x : this.x + this.w - 1;
    }
    
    /**
     * Sets the right edge value of the {@code Rect}
     *
     * @param right The new right value.
     * @return This instance for call chaining.
     */
    public Rect right(int right)
    {
        int dif = right - right();
        if (this.w < 0)
        {
            this.x = right;
            this.w -= dif;
        }
        else
        {
            this.w += dif;
        }
        return this;
    }
    
    /**
     * @return The position of the bottom edge of the {@code Rect}. If {@link #h} is negative, then this will be < the top edge.
     */
    @Override
    public int bottom()
    {
        return this.h < 0 ? this.y : this.y + this.h - 1;
    }
    
    /**
     * Sets the bottom edge value of the {@code Rect}
     *
     * @param bottom The new bottom value.
     * @return This instance for call chaining.
     */
    public Rect bottom(int bottom)
    {
        int dif = bottom - bottom();
        if (this.h < 0)
        {
            this.y = bottom;
            this.h -= dif;
        }
        else
        {
            this.h += dif;
        }
        return this;
    }
    
    /**
     * @return The topLeft corner of the {@code Rect}. This is not the absolute topLeft of the {@code Rect} as if a {@link #size} value is negative.
     */
    @Override
    public Vector2ic topLeft()
    {
        return this.topLeft.set(left(), top());
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
        return this.topRight.set(right(), top());
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
        return this.bottomLeft.set(left(), bottom());
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
        return this.bottomRight.set(right(), bottom());
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
     * @return The center x value of the {@code Rect}.
     */
    @Override
    public int centerX()
    {
        return this.x + (this.w >> 1);
    }
    
    /**
     * Sets the center x value of the {@code Rect}
     *
     * @param centerX The new center x value.
     * @return This instance for call chaining.
     */
    public Rect centerX(int centerX)
    {
        this.x = centerX - (this.w >> 1);
        return this;
    }
    
    /**
     * @return The center y value of the {@code Rect}.
     */
    @Override
    public int centerY()
    {
        return this.y + (this.h >> 1);
    }
    
    /**
     * Sets the center y value of the {@code Rect}
     *
     * @param centerY The new center y value.
     * @return This instance for call chaining.
     */
    public Rect centerY(int centerY)
    {
        this.y = centerY - (this.h >> 1);
        return this;
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
     * Sets the value of this {@code Rect}.
     *
     * @param x      The new left value.
     * @param y      The new top value.
     * @param width  The new width value.
     * @param height The new height value.
     * @return This instance for call chaining.
     */
    public Rect set(int x, int y, int width, int height)
    {
        this.x = x;
        this.y = y;
        this.w = width;
        this.h = height;
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
        this.x = pos.x();
        this.y = pos.y();
        this.w = size.x();
        this.h = size.y();
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
        this.x = other.x();
        this.y = other.y();
        this.w = other.width();
        this.h = other.height();
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
        if (Math.abs(result.width()) < Math.abs(other.width()))
        {
            if (left() < other.left())
            {
                result.x(other.left());
            }
            else if (other.right() < right())
            {
                result.x(other.right() - result.width() + 1);
            }
            else
            {
                result.x(left());
            }
        }
        else
        {
            result.centerX(other.centerX());
        }
        result.height(Math.abs(height()));
        if (Math.abs(result.height()) < Math.abs(other.height()))
        {
            if (top() < other.top())
            {
                result.y(other.top());
            }
            else if (other.bottom() < bottom())
            {
                result.y(other.bottom() - result.height() + 1);
            }
            else
            {
                result.y(top());
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
        result.set(this);
        if (!other.collide(this)) return result.size(0, 0);
        result.left(left() <= other.left() ? other.left() : left());
        result.right(other.right() <= right() ? other.right() : right());
        result.top(top() <= other.top() ? other.top() : top());
        result.bottom(other.bottom() <= bottom() ? other.bottom() : bottom());
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
        result.left(Math.min(left(), other.left()));
        result.top(Math.min(top(), other.top()));
        result.right(Math.max(right(), other.right()));
        result.bottom(Math.max(bottom(), other.bottom()));
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
        double aspect = (double) Math.abs(width()) / (double) Math.abs(height());
        
        int otherWidth  = Math.abs(other.width());
        int otherHeight = Math.abs(other.height());
        
        result.size(otherWidth, (int) (otherWidth / aspect));
        
        if (Math.abs(result.height()) > otherHeight) result.size((int) (otherHeight * aspect), otherHeight);
        
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
            result.width(width());
            result.left(left());
        }
        else
        {
            result.width(-width());
            result.left(right());
        }
        if (height() > 0)
        {
            result.height(height());
            result.top(top());
        }
        else
        {
            result.height(-height());
            result.top(bottom());
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
        return other.left() <= left() && right() <= other.right() && other.top() <= top() && bottom() <= other.bottom();
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
        return left() <= x && x <= right() && top() <= y && y <= bottom();
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
        return (Math.max(left(), other.left()) <= Math.min(right(), other.right()) && Math.max(top(), other.top()) <= Math.min(bottom(), other.bottom()));
    }
    
    protected Rect thisOrNew()
    {
        return this;
    }
}
