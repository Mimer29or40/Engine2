#version 460 core

const float PI = 3.141592653;

layout(points) in;
layout(triangle_strip, max_vertices = 16) out;

uniform float thickness;
uniform ivec2 viewport;

vec3 toScreenSpace(vec4 v)
{
    return vec3(v.xy / v.w * viewport, v.z / v.w - 0.000);
}

void main(void)
{
    const int segments = 16;
    
    vec3 point = toScreenSpace(gl_in[0].gl_Position);
    
    for (int i = 0; i < segments; i++) {
        float angle = ceil(i / 2.0) * 2.0 * PI / segments;
        angle *= (i % 2 == 0 ? 1. : -1.);
        
        vec2 off = thickness * vec2(cos(angle), sin(angle));
        gl_Position = vec4((point.xy + off) / viewport, point.z, 1.0);
        EmitVertex();
    }
    EndPrimitive();
}
