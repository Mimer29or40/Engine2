#version 460 core

layout(triangles) in;
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
    vec3 p[3];
    p[0] = toScreenSpace(gl_in[0].gl_Position);
    p[1] = toScreenSpace(gl_in[1].gl_Position);
    p[2] = toScreenSpace(gl_in[2].gl_Position);
    
    // Perform Naive Culling
    vec2 area = viewport * 4;
    if (p[1].x < -area.x || p[1].x > area.x) return;
    if (p[1].y < -area.y || p[1].y > area.y) return;
    if (p[2].x < -area.x || p[2].x > area.x) return;
    if (p[2].y < -area.y || p[2].y > area.y) return;
    
    // Determines the normals for the first two line segments
    vec2 v0 = normalize(p[1].xy - p[0].xy);
    vec2 v1 = normalize(p[2].xy - p[1].xy);
    
    vec2 n0 = thickness * vec2(-v0.y, v0.x);
    vec2 n1 = thickness * vec2(-v1.y, v1.x);
    
    // Determines location of bevel
    vec2 p1, p2;
    if (dot(v0, n1) > 0) {
        p1 = p[1].xy + n0;
        p2 = p[1].xy + n1;
    }
    else {
        p1 = p[1].xy - n1;
        p2 = p[1].xy - n0;
    }
    // Generates Bevel at Joint
    gl_Position = vec4(p1 / viewport, p[1].z, 1.0);
    EmitVertex();
    
    gl_Position = vec4(p2 / viewport, p[1].z, 1.0);
    EmitVertex();
    
    gl_Position = vec4(p[1].xy / viewport, p[1].z, 1.0);
    EmitVertex();
    
    EndPrimitive();
    
    // Generates Line Strip
    gl_Position = vec4((p[1].xy + n1) / viewport, p[1].z, 1.0);
    EmitVertex();
    
    gl_Position = vec4((p[1].xy - n1) / viewport, p[1].z, 1.0);
    EmitVertex();
    
    gl_Position = vec4((p[2].xy + n1) / viewport, p[2].z, 1.0);
    EmitVertex();
    
    gl_Position = vec4((p[2].xy - n1) / viewport, p[2].z, 1.0);
    EmitVertex();
    
    EndPrimitive();
}
