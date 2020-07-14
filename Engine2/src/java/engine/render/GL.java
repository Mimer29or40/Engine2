package engine.render;

import static org.lwjgl.opengl.GL43.*;

public enum GL
{
    STREAM_DRAW(GL_STREAM_DRAW),
    STREAM_READ(GL_STREAM_READ),
    STREAM_COPY(GL_STREAM_COPY),
    STATIC_DRAW(GL_STATIC_DRAW),
    STATIC_READ(GL_STATIC_READ),
    STATIC_COPY(GL_STATIC_COPY),
    DYNAMIC_DRAW(GL_DYNAMIC_DRAW),
    DYNAMIC_READ(GL_DYNAMIC_READ),
    DYNAMIC_COPY(GL_DYNAMIC_COPY),
    
    UNSIGNED_BYTE(GL_UNSIGNED_BYTE),
    BYTE(GL_BYTE),
    UNSIGNED_SHORT(GL_UNSIGNED_SHORT),
    SHORT(GL_SHORT),
    UNSIGNED_INT(GL_UNSIGNED_INT),
    INT(GL_INT),
    FLOAT(GL_FLOAT),
    DOUBLE(GL_DOUBLE),
    
    ARRAY_BUFFER(GL_ARRAY_BUFFER),
    ELEMENT_ARRAY_BUFFER(GL_ELEMENT_ARRAY_BUFFER),
    TRANSFORM_FEEDBACK_BUFFER(GL_TRANSFORM_FEEDBACK_BUFFER),
    UNIFORM_BUFFER(GL_UNIFORM_BUFFER),
    ATOMIC_COUNTER_BUFFER(GL_ATOMIC_COUNTER_BUFFER),
    SHADER_STORAGE_BUFFER(GL_SHADER_STORAGE_BUFFER),
    
    POINTS(GL_POINTS),
    LINES(GL_LINES),
    LINE_LOOP(GL_LINE_LOOP),
    LINE_STRIP(GL_LINE_STRIP),
    TRIANGLES(GL_TRIANGLES),
    TRIANGLE_STRIP(GL_TRIANGLE_STRIP),
    TRIANGLE_FAN(GL_TRIANGLE_FAN),
    QUADS(GL_QUADS),
    QUAD_STRIP(GL_QUAD_STRIP),
    POLYGON(GL_POLYGON),
    LINES_ADJACENCY(GL_LINES_ADJACENCY),
    LINE_STRIP_ADJACENCY(GL_LINE_STRIP_ADJACENCY),
    TRIANGLES_ADJACENCY(GL_TRIANGLES_ADJACENCY),
    TRIANGLE_STRIP_ADJACENCY(GL_TRIANGLE_STRIP_ADJACENCY),
    
    POINT(GL_POINT),
    LINE(GL_LINE),
    FILL(GL_FILL),
    
    NONE(GL_NONE),
    FRONT_LEFT(GL_FRONT_LEFT),
    FRONT_RIGHT(GL_FRONT_RIGHT),
    BACK_LEFT(GL_BACK_LEFT),
    BACK_RIGHT(GL_BACK_RIGHT),
    FRONT(GL_FRONT),
    BACK(GL_BACK),
    LEFT(GL_LEFT),
    RIGHT(GL_RIGHT),
    FRONT_AND_BACK(GL_FRONT_AND_BACK),
    AUX0(GL_AUX0),
    AUX1(GL_AUX1),
    AUX2(GL_AUX2),
    AUX3(GL_AUX3),
    
    CURRENT_BIT(GL_CURRENT_BIT),
    POINT_BIT(GL_POINT_BIT),
    LINE_BIT(GL_LINE_BIT),
    POLYGON_BIT(GL_POLYGON_BIT),
    POLYGON_STIPPLE_BIT(GL_POLYGON_STIPPLE_BIT),
    PIXEL_MODE_BIT(GL_PIXEL_MODE_BIT),
    LIGHTING_BIT(GL_LIGHTING_BIT),
    FOG_BIT(GL_FOG_BIT),
    DEPTH_BUFFER_BIT(GL_DEPTH_BUFFER_BIT),
    ACCUM_BUFFER_BIT(GL_ACCUM_BUFFER_BIT),
    STENCIL_BUFFER_BIT(GL_STENCIL_BUFFER_BIT),
    VIEWPORT_BIT(GL_VIEWPORT_BIT),
    TRANSFORM_BIT(GL_TRANSFORM_BIT),
    ENABLE_BIT(GL_ENABLE_BIT),
    COLOR_BUFFER_BIT(GL_COLOR_BUFFER_BIT),
    HINT_BIT(GL_HINT_BIT),
    EVAL_BIT(GL_EVAL_BIT),
    LIST_BIT(GL_LIST_BIT),
    TEXTURE_BIT(GL_TEXTURE_BIT),
    SCISSOR_BIT(GL_SCISSOR_BIT),
    ALL_ATTRIB_BITS(GL_ALL_ATTRIB_BITS),
    ;
    
    private final int ref;
    
    GL(int ref)
    {
        this.ref = ref;
    }
    
    public int ref()
    {
        return this.ref;
    }
}
