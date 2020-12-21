#version 460 core

#define PI 3.14159265359

uniform ivec2 resolution;
uniform int frameCount;
uniform float seconds;
uniform float elapsedTime;

uniform vec2 mousePos;
uniform vec2 mouseRel;
uniform bvec4 ButtonLEFT;

out vec4 FragColor;

vec2 FragCoord()
{
    return vec2(gl_FragCoord.x, resolution.y - gl_FragCoord.y);
}

float distanceToLineSegment(vec2 p, vec2 a, vec2 b)
{
    const vec2 l = b - a;
    const float l2 = dot(l, l);
    if (l2 == 0.0) return distance(p, a);
    const float t = max(0, min(1, dot(p - a, b - a) / l2));
    const vec2 projection = a + t * (b - a);
    return distance(p, projection);
}

float axialDistance(vec2 p, vec2 a, vec2 axis1, vec2 axis2)
{
    vec2 ap = p - a;
    return max(abs(dot(ap, axis1) / length(axis1)), abs(dot(ap, axis2) / length(axis2)));
}

float isLeft(vec2 p, vec2 a, vec2 b)
{
    return ((b.x - a.x) * (p.y - a.y) - (b.y - a.y) * (p.x - a.x));
}

int checkWinding(vec2 p, vec2 a, vec2 b)
{
    if (a.y <= p.y) // start y <= p.y
    {
        if (b.y > p.y && isLeft(p, a, b) > 0) // an upward crossing and p left of edge
        {
            return 1; // have a valid up intersect
        }
    }
    else // start y > p.y (no test needed)
    {
        if (b.y <= p.y && isLeft(p, a, b) < 0) // a downward crossing and p right of edge
        {
            return -1; // have a valid down intersect
        }
    }
    return 0;
}

bool pointInQuad(vec2 p, vec2 a, vec2 b, vec2 c, vec2 d)
{
    int wn = 0;
    wn += checkWinding(p, a, b);
    wn += checkWinding(p, b, c);
    wn += checkWinding(p, c, d);
    wn += checkWinding(p, d, a);
    return wn != 0;
}

void line(vec2 a, vec2 b, int type, float thickness, float fuzz, vec4 color)
{
    if (thickness <= 0) return;
    
    thickness *= 0.5;
    fuzz = clamp(fuzz, 0.0, 1.0);
    
    vec2 p = gl_FragCoord.xy;
    vec2 ab = b - a;
    vec2 abNorm = vec2(-ab.y, ab.x);
    
    vec2 normal, a1, a2, b1, b2;
    
    float dist = -1;
    switch (type)
    {
        case 1: // Squared Line that extends 1/2 thickness past endpoints
        vec2 abThick = normalize(ab) * thickness;
        vec2 abNormThick = normalize(abNorm) * thickness;
        a1 = a - abNormThick;
        a2 = a + abNormThick;
        b1 = b + abNormThick;
        b2 = b - abNormThick;
        vec2 a3 = a2 - abThick;
        vec2 a4 = a1 - abThick;
        vec2 b3 = b2 + abThick;
        vec2 b4 = b1 + abThick;
        if (pointInQuad(p, a1, a2, a3, a4))
        {
            dist = axialDistance(p, a, abThick, abNormThick);
        }
        else if (pointInQuad(p, b1, b2, b3, b4))
        {
            dist = axialDistance(p, b, abThick, abNormThick);
        }
        case 0: // Squared Line that stops at endpoints
        if (type == 0)
        {
            vec2 abNormThick = normalize(abNorm) * thickness;
            a1 = a - abNormThick;
            a2 = a + abNormThick;
            b1 = b + abNormThick;
            b2 = b - abNormThick;
        }
        if (pointInQuad(p, a1, a2, b1, b2))
        {
            dist = distanceToLineSegment(p, a, b);
        }
        break;
        case 2: // Rounded line
        dist = distanceToLineSegment(p, a, b);
        break;
    }
    if (dist >= 0 && dist < thickness)
    {
        float mixValue = smoothstep(1.0 - (dist / thickness), 0.0, fuzz);
        FragColor = mix(FragColor, color, mixValue);
    }
}

float plot(vec2 st, float pct)
{
    return smoothstep(pct - 0.005, pct, st.y) - smoothstep(pct, pct + 0.005, st.y);
}

void main(void)
{
    vec2 st = FragCoord() / resolution;
    
    //	FragColor = vec4(scaledPoint, 0.0, 1.0);
    
    //	float y = st.x;
    //	float y = pow(st.x, 5.0);
    //	float y = step(0.5, st.x);
    //	float y = smoothstep(0.1, 0.9, st.x);
    float y = smoothstep(0.2, 0.5, st.x) - smoothstep(0.5, 0.8, st.x) + (sin(seconds * 0.5) * 0.05 * sin(st.x * 200));
    
    vec3 color = vec3(y);
    
    float pct = plot(vec2(st.x, st.y), y);
    color = (1.0 - pct) * color + pct * vec3(0.0, 1.0, 0.0);

    FragColor = vec4(color, 1.0);
    
    // * cos(seconds) + 21
    float fuzzCenter = 1.0 / 32.0;
    line(mousePos, resolution / 2, 1, 40, fuzzCenter * sin(seconds) + fuzzCenter, vec4(0.25, 1.0, 0.5, 1.0));
    
    //	float dist = length(gl_FragCoord.xy - mousePos);
    //	float moved = length(mouseRel) * 2;
    //	if (dist < moved)
    //	{
    //		if (ButtonLEFT.z)
    //		{
    //			FragColor = vec4(mousePos / resolution, 1.0, 1.0);
    //		}
    //		else
    //		{
    //			FragColor = vec4(mousePos / resolution, 0.0, 1.0);
    //		}
    //	}
}
