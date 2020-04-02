#version 460 core

uniform sampler2D text;

in vec2 cord;
out vec4 color;

void main(void)
{
    color = texture(text, cord);
}
