package engine.render;

/**
 * Determines how an ellipse's parameters are transformed into a drawable ellipse.
 * <p>
 * <p>
 * {@link EllipseMode#CENTER} <p>
 * -- a: The center x coordinate <p>
 * -- b: The center y coordinate <p>
 * -- c: The width along the x axis <p>
 * -- d: The height along the y axis <p>
 * <p>
 * {@link EllipseMode#RADIUS} <p>
 * -- a: The center x coordinate <p>
 * -- b: The center y coordinate <p>
 * -- c: The radius along the x axis <p>
 * -- d: The radius along the y axis <p>
 * <p>
 * {@link EllipseMode#CORNER} <p>
 * -- a: The top left x coordinate <p>
 * -- b: The top left y coordinate <p>
 * -- c: The width along the x axis <p>
 * -- d: The height along the y axis <p>
 * <p>
 * {@link EllipseMode#CORNERS} <p>
 * -- a: The top left x coordinate <p>
 * -- b: The top left y coordinate <p>
 * -- c: The bottom right x coordinate <p>
 * -- d: The bottom right y coordinate <p>
 */
public enum EllipseMode
{
    CENTER, RADIUS, CORNER, CORNERS
}
