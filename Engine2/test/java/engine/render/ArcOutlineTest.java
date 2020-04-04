package engine.render;

import org.joml.Vector2f;

import static engine.util.Util.*;

public class ArcOutlineTest
{
    public static void main(String[] args)
    {
        Vector2f center = new Vector2f(0, 0);
        Vector2f radius = new Vector2f(200, 200);
        Vector2f bounds = new Vector2f(0, (float) (1.5 * Math.PI));
        int      mode   = 1;
        
        float range    = bounds.y - bounds.x;
        int   segments = max(2, min((int) (max(radius.x, radius.y) * range / (4 * Math.PI)), 34));
        segments = 5;
        float   scale     = range / segments;
        boolean connected = mode == 2 || mode == 3;
        
        Vector2f[] Points = new Vector2f[36];
        
        for (int i = 0; i <= segments; i++)
        {
            float angle = bounds.x + i * scale;
            Points[i] = new Vector2f((float) (center.x + radius.x * Math.cos(angle)), (float) (center.y + radius.y * Math.sin(angle)));
        }
        
        int total = segments + 1;
        if (mode == 3) Points[total++] = new Vector2f(center);
        println(total);
        
        // vec3 p[4];
        for (int i = 0; i < total; i++)
        {
            int p0 = i == 0 && !connected ? 0 : (i + total - 1) % total;
            int p1 = (i + total + 0) % total;
            int p2 = (i + total + 1) % total;
            int p3 = i + 1 == segments && !connected ? i + 1 : (i + total + 2) % total;
            
            if (i >= segments && !connected) return;
            
            println(p0, p1, p2, p3);
            
            // p[0] = toScreenSpace(Points[i == 0 && (mode == 2 || mode == 3) ? (i + total - 1) % total : 0]);
            // //        p[0] = toScreenSpace(Points[(i + total - 1) % total]);
            // p[1] = toScreenSpace(Points[(i + total + 0) % total]);
            // p[2] = toScreenSpace(Points[(i + total + 1) % total]);
            // p[3] = toScreenSpace(Points[(i + total + 2) % total]);
            
            // // Perform Naive Culling
            // vec2 area = viewport * 4;
            // if (p[1].x < -area.x || p[1].x > area.x) return;
            // if (p[1].y < -area.y || p[1].y > area.y) return;
            // if (p[2].x < -area.x || p[2].x > area.x) return;
            // if (p[2].y < -area.y || p[2].y > area.y) return;
            //
            // // Determines the normals for the first two line segments
            // vec2 v0 = p[1].xy - p[0].xy;
            // vec2 v1 = p[2].xy - p[1].xy;
            // vec2 v2 = p[3].xy - p[2].xy;
            //
            // if (length(v1) < 0.000001) v0 = v1;
            //
            // vec2 v0u = normalize(v0);
            // vec2 v1u = normalize(v1);
            // vec2 v2u = normalize(v2);
            //
            // vec2 n0u = vec2(-v0u.y, v0u.x);
            // vec2 n1u = vec2(-v1u.y, v1u.x);
            // vec2 n2u = vec2(-v2u.y, v2u.x);
            //
            // vec2 n0 = thickness * n0u;
            // vec2 n1 = thickness * n1u;
            // vec2 n2 = thickness * n2u;
            //
            // vec2 t1 = normalize(v0u + v1u);
            // vec2 t2 = normalize(v1u + v2u);
            //
            // vec2 m1 = vec2(-t1.y, t1.x);
            // vec2 m2 = vec2(-t2.y, t2.x);
            // m1 *= min(thickness / dot(m1, n1u), min(length(v0), length(v1)));
            // m2 *= min(thickness / dot(m2, n2u), min(length(v1), length(v2)));
            //
            // // Determines location of bevel
            // vec2 bevelP11, bevelP12, bevelP21, miterP1, miterP2;
            // if (dot(v0u, n1u) > 0)
            // {
            //     bevelP11 = p[1].xy + n0;
            //     bevelP12 = p[1].xy + n1;
            //     miterP1  = p[1].xy - m1;
            // }
            // else
            // {
            //     bevelP11 = p[1].xy - n0;
            //     bevelP12 = p[1].xy - n1;
            //     miterP1  = p[1].xy + m1;
            // }
            // if (dot(v1u, n2u) > 0)
            // {
            //     bevelP21 = p[2].xy + n1;
            //     miterP2  = p[2].xy - m2;
            // }
            // else
            // {
            //     bevelP21 = p[2].xy - n1;
            //     miterP2  = p[2].xy + m2;
            // }
            // // Generates Bevel at Joint
            // gl_Position = vec4(bevelP11 / viewport, p[1].z, 1.0);
            // EmitVertex();
            //
            // gl_Position = vec4(bevelP12 / viewport, p[1].z, 1.0);
            // EmitVertex();
            //
            // // This need to be the bottom join point
            // gl_Position = vec4(miterP1 / viewport, p[1].z, 1.0);
            // EmitVertex();
            //
            // EndPrimitive();
            //
            // // Generates Line Strip
            // gl_Position = vec4(bevelP12 / viewport, p[1].z, 1.0);
            // EmitVertex();
            //
            // gl_Position = vec4(miterP1 / viewport, p[1].z, 1.0);
            // EmitVertex();
            //
            // if (dot(v0u, n1u) > 0 ^^ dot(v1u, n2u) > 0) {
            //     gl_Position = vec4(miterP2 / viewport, p[2].z, 1.0);
            //     EmitVertex();
            //
            //     gl_Position = vec4(bevelP21 / viewport, p[2].z, 1.0);
            //     EmitVertex();
            // }
            //     else{
            //     gl_Position = vec4(bevelP21 / viewport, p[2].z, 1.0);
            //     EmitVertex();
            //
            //     gl_Position = vec4(miterP2 / viewport, p[2].z, 1.0);
            //     EmitVertex();
            // }
            // EndPrimitive();
        }
    }
}
