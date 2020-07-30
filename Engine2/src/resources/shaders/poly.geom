#version 460 core

const float EPSILON = 0.0000000001f;

layout(points) in;
layout(triangle_strip, max_vertices = 256) out;

layout(std140, binding = 0) uniform View { mat4 view; };

layout(std430, binding = 1) buffer Vertices { vec2 vertices[]; };

int indices[256];

float wedge(vec2 a, vec2 b)
{
    return a.x * b.y - a.y * b.x;
}

bool valid_triangle(int n, int prev_i, int curr_i, int next_i)
{
    vec2 prev = vertices[prev_i];
    vec2 curr = vertices[curr_i];
    vec2 next = vertices[next_i];
    
    if (wedge(next - curr, prev - curr) < EPSILON) return false;
    for (int p = 0; p < n; p++) {
        if (p == prev_i || p == curr_i || p == next_i) continue;
        if (wedge(curr - prev, vertices[p] - prev) >= EPSILON &&
        wedge(next - curr, vertices[p] - curr) >= EPSILON &&
        wedge(prev - next, vertices[p] - next) >= EPSILON) return false;
    }
    return true;
}

void main(void)
{
    int n = vertices.length();
    
    float a = 0.0;
    for (int p = n - 1, q = 0; q < n; p = q++) {
        a += wedge(vertices[p], vertices[q]);
    }
    if (a > 0.0) {
        for (int i = 0; i < n; i++) indices[i] = i;
    }
    else {
        for (int i = 0; i < n; i++) indices[i] = n - 1 - i;
    }
    
    int i = 0, count = 2 * n;
    while (n >= 3 && count > 0) {
        count--;
        int prev_i = indices[(i + n - 1) % n];
        int curr_i = indices[(i + n + 0) % n];
        int next_i = indices[(i + n + 1) % n];
        if (valid_triangle(n, prev_i, curr_i, next_i)) {
            gl_Position = view * vec4(vertices[prev_i], 0.0, 1.0);
            EmitVertex();
    
            gl_Position = view * vec4(vertices[curr_i], 0.0, 1.0);
            EmitVertex();
    
            gl_Position = view * vec4(vertices[next_i], 0.0, 1.0);
            EmitVertex();
    
            EndPrimitive();
    
            for (int s = i % n, t = i % n + 1; t < n; s = t++) {
                indices[s] = indices[t];
            }
            n--; count = 2 * n;
        }
        else i++;
    }
}
