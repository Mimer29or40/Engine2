#version 460 core

const float PI = 3.141592653;

layout(points) in;
layout(triangle_strip, max_vertices = 48) out;

layout(std140, binding = 0) uniform View { mat4 view; };

uniform vec2 radius;

in vec2 position[1];

void main(void)
{
    int segments = max(16, min(int(max(radius.x, radius.y)) >> 1, 48));
    float scale = 2 * PI / float(segments);
    
    for (int i = 0; i < segments; i++) {
        float angle = ceil(i / 2.0) * scale * ((i & 1) == 0 ? 1 : -1);
        gl_Position = view * vec4(position[0].xy + radius * vec2(cos(angle), sin(angle)), 0.0, 1.0);
        EmitVertex();
    }
    EndPrimitive();
}
