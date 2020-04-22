#version 460 core

uniform vec4 color;
uniform vec4 tint;

out vec4 FragColor;

void main(void)
{
    FragColor = color * tint;
}
