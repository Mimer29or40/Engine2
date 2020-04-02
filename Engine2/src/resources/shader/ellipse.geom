#version 460 core

const float PI = 3.141592653;

layout(points) in;
layout(triangle_strip, max_vertices = 64) out;

uniform mat4 pv;
uniform vec2 radius;

in vec2 position[1];

void main(void)
{
    const int segments = max(16, min(int(max(radius.x, radius.y)) >> 1, 64));
//    const int segments = 64;
    
    for (int i = 0; i < segments; i++) {
        float angle = ceil(i / 2.0) * 2.0 * PI / float(segments);
        angle *= (i % 2 == 0 ? 1. : -1.);
        
        vec2 off = radius * vec2(cos(angle), sin(angle));
        gl_Position = pv * vec4(position[0].xy + off, 0.0, 1.0);
        EmitVertex();
    }
    EndPrimitive();
}
