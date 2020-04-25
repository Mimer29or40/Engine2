#version 460 core

layout(lines) in;
layout(triangle_strip, max_vertices = 4) out;

uniform float thickness;
uniform vec2 viewport;

vec3 toScreenSpace(vec4 v)
{
    return vec3(v.xy / v.w * viewport, v.z / v.w - 0.000);
}

void main(void)
{
    vec3 p0 = toScreenSpace(gl_in[0].gl_Position);
    vec3 p1 = toScreenSpace(gl_in[1].gl_Position);
    
    vec2 dir = normalize(p1.xy - p0.xy);
    vec2 norm = thickness * vec2(-dir.y, dir.x);
    
    gl_Position = vec4((p0.xy + norm) / viewport, p0.z, 1.0);
    EmitVertex();
    
    gl_Position = vec4((p0.xy - norm) / viewport, p0.z, 1.0);
    EmitVertex();
    
    gl_Position = vec4((p1.xy + norm) / viewport, p1.z, 1.0);
    EmitVertex();
    
    gl_Position = vec4((p1.xy - norm) / viewport, p1.z, 1.0);
    EmitVertex();
    
    EndPrimitive();
}
