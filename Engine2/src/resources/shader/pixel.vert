#version 460 core

layout(location = 0) in vec2 pos;

out vec2 cord;

void main(void)
{
    cord = vec2(pos.x < 0 ? 0.0 : 1.0, pos.y < 0 ? 1.0 : 0.0);
//    cord = vec2(pos.x < 0 ? 0.0 : 1.0, pos.y < 0 ? 0.0 : 1.0);
    gl_Position = vec4(pos, 0.0, 1.0);
}
