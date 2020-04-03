#version 460 core

layout(lines_adjacency) in;
layout(triangle_strip, max_vertices = 7) out;

uniform mat4 pv;
uniform vec2 viewport;
uniform float thickness;

vec3 toScreenSpace(vec4 v)
{
    return vec3(v.xy / v.w * viewport, (v.z - 0.001) / v.w);
}

void main(void)
{
    vec3 p[4];
    p[0] = toScreenSpace(gl_in[0].gl_Position);
    p[1] = toScreenSpace(gl_in[1].gl_Position);
    p[2] = toScreenSpace(gl_in[2].gl_Position);
    p[3] = toScreenSpace(gl_in[3].gl_Position);
    
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
    
    vec2 v0u = normalize(p[1].xy - p[0].xy);
    vec2 v1u = normalize(p[2].xy - p[1].xy);
    vec2 v2u = normalize(p[3].xy - p[2].xy);
    
    vec2 n0u = vec2(-v0u.y, v0u.x);
    vec2 n1u = vec2(-v1u.y, v1u.x);
    vec2 n2u = vec2(-v2u.y, v2u.x);
    
    vec2 n0 = thickness * vec2(-v0u.y, v0u.x);
    vec2 n1 = thickness * vec2(-v1u.y, v1u.x);
    vec2 n2 = thickness * vec2(-v2u.y, v2u.x);
    
    vec2 t1 = normalize(v0u + v1u);
    vec2 t2 = normalize(v1u + v2u);
    
    vec2 m1 = vec2(-t1.y, t1.x);
    vec2 m2 = vec2(-t2.y, t2.x);
    m1 *= min(thickness / dot(m1, n1u), min(length(v0), length(v1)));
    m2 *= min(thickness / dot(m2, n2u), min(length(v1), length(v2)));
    
    // Determines location of bevel
    vec2 bevelP11, bevelP12, bevelP21, miterP1, miterP2;
    if (dot(v0u, n1u) > 0) {
        bevelP11 = p[1].xy + n0;
        bevelP12 = p[1].xy + n1;
        miterP1 = p[1].xy - m1;
    }
    else {
        bevelP11 = p[1].xy - n0;
        bevelP12 = p[1].xy - n1;
        miterP1 = p[1].xy + m1;
    }
    if (dot(v1u, n2u) > 0) {
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
