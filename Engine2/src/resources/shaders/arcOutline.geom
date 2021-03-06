#version 460 core

const float PI = 3.141592653;

layout(points) in;
layout(triangle_strip, max_vertices = 256) out;// 36 * 7

layout(std140, binding = 0) uniform View { mat4 view; };

uniform vec2 radius;
uniform ivec2 viewport;
uniform float thickness;
uniform vec2 bounds;
uniform int mode;

in vec2 position[1];

vec3 toScreenSpace(vec4 v)
{
    return vec3(v.xy / v.w * viewport, (v.z - 0.001) / v.w);
}

void main(void)
{
    float range = bounds.y - bounds.x;
    int segments = max(2, min(int(max(radius.x, radius.y) * range / (4 * PI)), 34));
    float scale = range / segments;
    bool connected = mode == 2 || mode == 3;
    
    vec4 Points[36];
    
    for (int i = 0; i <= segments; i++) {
        float angle = bounds.x + i * scale;
        Points[i] = view * vec4(position[0].xy + radius * vec2(cos(angle), sin(angle)), 0.0, 1.0);
    }
    
    int total = segments + 1;
    if (mode == 0 || mode == 3) Points[total++] = view * vec4(position[0].xy, 0.0, 1.0);
    
    vec3 p[4];
    for (int i = 0; i < total; i++)
    {
        p[0] = toScreenSpace(Points[i == 0 && !connected ? 0 : (i + total - 1) % total]);
        p[1] = toScreenSpace(Points[(i + total + 0) % total]);
        p[2] = toScreenSpace(Points[(i + total + 1) % total]);
        p[3] = toScreenSpace(Points[i + 1 == segments && !connected ? i + 1 : (i + total + 2) % total]);
        
        if (i >= segments && !connected) return;
        
        // Perform Naive Culling
        vec2 area = viewport * 4;
        if (p[1].x < -area.x || p[1].x > area.x) return;
        if (p[1].y < -area.y || p[1].y > area.y) return;
        if (p[2].x < -area.x || p[2].x > area.x) return;
        if (p[2].y < -area.y || p[2].y > area.y) return;
        
        // Determines the normals for the first two line segments
        vec2 v0 = p[1].xy - p[0].xy;
        vec2 v1 = p[2].xy - p[1].xy;
        vec2 v2 = p[3].xy - p[2].xy;
        
        if (length(v0) < 0.0001) v0 = v1;
        if (length(v2) < 0.0001) v2 = v1;
        
        vec2 v0u = normalize(v0);
        vec2 v1u = normalize(v1);
        vec2 v2u = normalize(v2);
        
        vec2 n0u = vec2(-v0u.y, v0u.x);
        vec2 n1u = vec2(-v1u.y, v1u.x);
        vec2 n2u = vec2(-v2u.y, v2u.x);
        
        vec2 n0 = thickness * n0u;
        vec2 n1 = thickness * n1u;
        vec2 n2 = thickness * n2u;
        
        vec2 t1 = normalize(v0u + v1u);
        vec2 t2 = normalize(v1u + v2u);
        
        vec2 m1 = vec2(-t1.y, t1.x);
        vec2 m2 = vec2(-t2.y, t2.x);
        m1 *= min(thickness / dot(m1, n1u), min(length(v0), length(v1)));
        m2 *= min(thickness / dot(m2, n2u), min(length(v1), length(v2)));
        
        // Determines location of bevel
        vec2 bevelP11, bevelP12, bevelP21, miterP1, miterP2;
        if (dot(v0u, n1u) >= 0) {
            bevelP11 = p[1].xy + n0;
            bevelP12 = p[1].xy + n1;
            miterP1 = p[1].xy - m1;
        }
        else {
            bevelP11 = p[1].xy - n0;
            bevelP12 = p[1].xy - n1;
            miterP1 = p[1].xy + m1;
        }
        if (dot(v1u, n2u) >= 0) {
            bevelP21 = p[2].xy + n1;
            miterP2 = p[2].xy - m2;
        }
        else {
            bevelP21 = p[2].xy - n1;
            miterP2 = p[2].xy + m2;
        }
        // Generates Bevel at Joint
        gl_Position = vec4(bevelP11 / viewport, p[1].z, 1.0);
        EmitVertex();
        
        gl_Position = vec4(bevelP12 / viewport, p[1].z, 1.0);
        EmitVertex();
        
        // This need to be the bottom join point
        gl_Position = vec4(miterP1 / viewport, p[1].z, 1.0);
        EmitVertex();
        
        EndPrimitive();
        
        // Generates Line Strip
        gl_Position = vec4(bevelP12 / viewport, p[1].z, 1.0);
        EmitVertex();
        
        gl_Position = vec4(miterP1 / viewport, p[1].z, 1.0);
        EmitVertex();
        
        if (dot(v0u, n1u) > 0 ^^ dot(v1u, n2u) > 0) {
            gl_Position = vec4(miterP2 / viewport, p[2].z, 1.0);
            EmitVertex();
            
            gl_Position = vec4(bevelP21 / viewport, p[2].z, 1.0);
            EmitVertex();
        }
        else {
            gl_Position = vec4(bevelP21 / viewport, p[2].z, 1.0);
            EmitVertex();
            
            gl_Position = vec4(miterP2 / viewport, p[2].z, 1.0);
            EmitVertex();
        }
        EndPrimitive();
    }
}
