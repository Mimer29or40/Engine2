#version 460 core

uniform vec4 tint;
uniform float interpolate;
uniform sampler2D tex1;
uniform sampler2D tex2;

in vec2 position;
in vec2 texCord;

out vec4 FragColor;

void main(void)
{
    if (interpolate <= 0)
    {
        FragColor = texture(tex1, texCord) * tint;
    }
    else if (interpolate >= 1)
    {
        FragColor = texture(tex2, texCord) * tint;
    }
    else
    {
        FragColor = mix(texture(tex1, texCord), texture(tex2, texCord), interpolate) * tint;
    }
}
