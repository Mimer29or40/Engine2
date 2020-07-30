#version 460 core

const float PI = 3.141592653;

layout(points) in;
layout(triangle_strip, max_vertices = 256) out;

layout(std140, binding = 0) uniform View { mat4 view; };

uniform vec2 radius;
uniform vec2 bounds;
uniform int mode;

in vec2 position[1];

void main(void)
{
    float range = bounds.y - bounds.x;
    int segments = max(2, int(min(int(max(radius.x, radius.y)) >> 1, 48) * range / (2 * PI)));
    float scale = range / segments;
    
    int start = 0;
    vec2 origin = position[0].xy;
    if (mode == 1 || mode == 2)
    {
        start = 1;
        origin += vec2(radius.x, 0);
    }
    
    for (int i = start; i <= segments; i++) {
        float angle = bounds.x + i * scale;
    
        if (i > 0 && (i & 1) == 0)
        {
            gl_Position = view * vec4(origin, 0.0, 1.0);
            EmitVertex();
        }
    
        gl_Position = view * vec4(position[0].xy + radius * vec2(cos(angle), sin(angle)), 0.0, 1.0);
        EmitVertex();
    }
    EndPrimitive();
}
