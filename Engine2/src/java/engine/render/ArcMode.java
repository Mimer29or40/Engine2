package engine.render;

/**
 * Determines how an arc is drawn and filled to the screen.
 * <p>
 * <p>
 * {@link ArcMode#DEFAULT} <p>
 * -- The outline is only drawn on the edge of the arc <p>
 * -- The arc is filled like a pie <p>
 * <p>
 * {@link ArcMode#OPEN} <p>
 * -- The outline is only drawn on the edge of the arc <p>
 * -- The arc is filled including between the end points <p>
 * <p>
 * {@link ArcMode#CHORD} <p>
 * -- The outline drawn around the entire fill area <p>
 * -- The arc is filled including between the end points <p>
 * <p>
 * {@link ArcMode#PIE} <p>
 * -- The outline drawn around the entire fill area <p>
 * -- The arc is filled like a pie <p>
 */
public enum ArcMode
{
    DEFAULT, OPEN, CHORD, PIE
}
