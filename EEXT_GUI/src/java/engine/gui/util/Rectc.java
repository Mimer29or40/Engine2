package engine.gui.util;

import org.joml.Vector2ic;

/**
 * Read-Only view into a rectangle. Rect objects to store and manipulate rectangular areas.
 * A Rect can be created from a combination of left, top, width, and height values. Based on pygame.Rect
 */
@SuppressWarnings({"UnusedReturnValue", "unused"})
public interface Rectc
{
    /**
     * @return The position of the left edge of the {@code Rect}. If {@link #width} is negative, then this will be > the right edge.
     */
    int left();
    
    /**
     * @return The position of the top edge of the {@code Rect}. If {@link #height} is negative, then this will be > the bottom edge.
     */
    int top();
    
    /**
     * @return The width of the {@code Rect} in screen space coordinates.
     */
    int width();
    
    /**
     * @return The height of the {@code Rect} in screen space coordinates.
     */
    int height();
    
    /**
     * @return The position of the right edge of the {@code Rect}. If {@link #width} is negative, then this will be < the left edge.
     */
    int right();
    
    /**
     * @return The position of the bottom edge of the {@code Rect}. If {@link #height} is negative, then this will be < the top edge.
     */
    int bottom();
    
    /**
     * @return The left most x value of the {@code Rect}, regardless of the sign of {@link #width}.
     */
    int x1();
    
    /**
     * @return The top most y value of the {@code Rect}, regardless of the sign of {@link #height}.
     */
    int y1();
    
    /**
     * @return The right most x value of the {@code Rect}, regardless of the sign of {@link #width}.
     */
    int x2();
    
    /**
     * @return The bottom most y value of the {@code Rect}, regardless of the sign of {@link #height}.
     */
    int y2();
    
    /**
     * @return The center x value of the {@code Rect}.
     */
    int centerX();
    
    /**
     * @return The center y value of the {@code Rect}.
     */
    int centerY();
    
    /**
     * @return The topLeft corner of the {@code Rect}. This is not the absolute topLeft of the {@code Rect} as if a {@link #size} value is negative.
     */
    Vector2ic topLeft();
    
    /**
     * @return The topRight corner of the {@code Rect}. This is not the absolute topRight of the {@code Rect} as if a {@link #size} value is negative.
     */
    Vector2ic topRight();
    
    /**
     * @return The bottomLeft corner of the {@code Rect}. This is not the absolute bottomLeft of the {@code Rect} as if a {@link #size} value is negative.
     */
    Vector2ic bottomLeft();
    
    /**
     * @return The bottomRight corner of the {@code Rect}. This is not the absolute bottomRight of the {@code Rect} as if a {@link #size} value is negative.
     */
    Vector2ic bottomRight();
    
    /**
     * @return The midLeft point of the {@code Rect}. This is not the absolute midLeft of the {@code Rect} as if a {@link #size} value is negative.
     */
    Vector2ic midLeft();
    
    /**
     * @return The midTop point of the {@code Rect}. This is not the absolute midTop of the {@code Rect} as if a {@link #size} value is negative.
     */
    Vector2ic midTop();
    
    /**
     * @return The midRight point of the {@code Rect}. This is not the absolute midRight of the {@code Rect} as if a {@link #size} value is negative.
     */
    Vector2ic midRight();
    
    /**
     * @return The midBottom point of the {@code Rect}. This is not the absolute midBottom of the {@code Rect} as if a {@link #size} value is negative.
     */
    Vector2ic midBottom();
    
    /**
     * @return The center point of the {@code Rect}.
     */
    Vector2ic center();
    
    /**
     * @return The position of the {@code Rect}.
     */
    Vector2ic pos();
    
    /**
     * @return The size of the {@code Rect}. Can be negative
     */
    Vector2ic size();
    
    /**
     * Copy the {@code Rect}.
     *
     * @param result The object to store the copy.
     * @return The result {@code Rect} having the same position and size as the original.
     */
    Rect copy(Rect result);
    
    /**
     * Copy the {@code Rect}.
     *
     * @return A new {@code Rect} having the same position and size as the original.
     */
    Rect copy();
    
    /**
     * Moves the {@code Rect}.
     *
     * @param x      The x amount to move the {@code Rect} by. Can be negative.
     * @param y      The y amount to move the {@code Rect} by. Can be negative.
     * @param result The object to store the moved {@code Rect}.
     * @return The result.
     */
    Rect move(int x, int y, Rect result);
    
    /**
     * Moves the {@code Rect}.
     *
     * @param amount The vector to move the {@code Rect} by. Can be negative.
     * @param result The object to store the moved {@code Rect}.
     * @return The result.
     */
    Rect move(Vector2ic amount, Rect result);
    
    /**
     * Grow or shrink the {@code Rect} size.
     *
     * @param x      The x amount to change the size by. Can be negative.
     * @param y      The y amount to change the size by. Can be negative.
     * @param result The object to store the resized {@code Rect}.
     * @return The result.
     */
    Rect inflate(int x, int y, Rect result);
    
    /**
     * Grow or shrink the {@code Rect} size.
     *
     * @param amount The vector to change the size by. Can be negative.
     * @param result The object to store the resized {@code Rect}.
     * @return The result.
     */
    Rect inflate(Vector2ic amount, Rect result);
    
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
    Rect clamp(Rectc other, Rect result);
    
    /**
     * Clips a {@code Rect} inside another
     * <p>
     * Calculates the {@code Rect} that is cropped to be completely inside the
     * other {@code Rect}. If the two rectangles do not overlap to begin with,
     * a {@code Rect} with of one point is returned.
     *
     * @param other  The {@code Rect} to clip this {@code Rectc} in.
     * @param result The object to store the clipped {@code Rect}.
     * @return The result.
     */
    Rect clip(Rectc other, Rect result);
    
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
    Rect union(Rectc other, Rect result);
    
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
    Rect fit(Rectc other, Rect result);
    
    /**
     * Correct negative sizes
     * <p>
     * This will flip the width or height of a rectangle if it has a negative size. The rectangle will remain in the same place, with only the sides swapped.
     *
     * @param result The object to store the normalized {@code Rect}.
     * @return The result.
     */
    Rect normalize(Rect result);
    
    /**
     * Test if one {@code Rect} is inside another
     *
     * @param other The other {@code Rect}.
     * @return True if this {@code Rect} is completely inside the other {@code Rect}.
     */
    boolean contains(Rectc other);
    
    /**
     * Test if a point is inside a {@code Rect}.
     *
     * @param x The x point to test.
     * @param y The y point to test.
     * @return True if the given point is inside the {@code Rect}.
     */
    boolean collide(int x, int y);
    
    /**
     * Test if a point is inside a {@code Rect}.
     *
     * @param point The point to test.
     * @return True if the given point is inside the {@code Rect}.
     */
    boolean collide(Vector2ic point);
    
    /**
     * Test if two {@code Rect}s overlap
     *
     * @param other The other {@code Rect}.
     * @return True if any portion of either {@code Rect} overlap
     */
    boolean collide(Rectc other);
}
