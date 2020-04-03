#version 460 core

layout(location = 0) in vec2 aPosition;
layout(location = 1) in vec2 aTexCord;

uniform mat4 pv;

out vec2 position;
out vec2 texCord;

void main()
{
    position = aPosition;
    texCord = aTexCord;
    gl_Position = pv * vec4(aPosition, 0.0, 1.0);
}
