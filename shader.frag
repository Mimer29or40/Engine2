#version 460 core

uniform ivec2 resolution;
uniform int frameCount;
uniform float seconds;
uniform float elapsedTime;

uniform vec2 mousePos;
uniform vec2 mouseRel;
uniform bvec4 ButtonLEFT;

out vec4 FragColor;

void main(void)
{
	float dist = length(gl_FragCoord.xy - mousePos);
	float moved = length(mouseRel) * 2;
	if (dist < moved)
	{
		if (ButtonLEFT.z)
		{
			FragColor = vec4(mousePos / resolution, 1.0, 1.0);
		}
		else
		{
			FragColor = vec4(mousePos / resolution, 0.0, 1.0);
		}
	}
}
