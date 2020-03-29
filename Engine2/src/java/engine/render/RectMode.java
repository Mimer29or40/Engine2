package engine.render;

/**
 * Determines how a rectangle's parameters are transformed into a drawable rectangle.
 * <p>
 * <p>
 * {@link RectMode#CORNER} <p>
 * -- a: The top left x coordinate <p>
 * -- b: The top left y coordinate <p>
 * -- c: The width along the x axis <p>
 * -- d: The height along the y axis <p>
 * <p>
 * {@link RectMode#CORNERS} <p>
 * -- a: The top left x coordinate <p>
 * -- b: The top left y coordinate <p>
 * -- c: The bottom right x coordinate <p>
 * -- d: The bottom right y coordinate <p>
 * <p>
 * {@link RectMode#CENTER} <p>
 * -- a: The center x coordinate <p>
 * -- b: The center y coordinate <p>
 * -- c: The width along the x axis <p>
 * -- d: The height along the y axis <p>
 * <p>
 * {@link RectMode#RADIUS} <p>
 * -- a: The center x coordinate <p>
 * -- b: The center y coordinate <p>
 * -- c: The half width along the x axis <p>
 * -- d: The half height along the y axis <p>
 */
public enum RectMode
{
    CORNER, CORNERS, RADIUS, CENTER
}
