#version 460 core

uniform vec4 color;
uniform sampler2D tex;

in vec2 position;
in vec2 texCord;

out vec4 FragColor;

void main(void)
{
    float pixel = texture(tex, texCord).r;
    if (pixel == 0) discard;
    FragColor = pixel * color;
}
