vec4 vec4_splat(float input)
{
	return vec4(input, input, input, input);
}

void main()
{
	gl_FragColor = vec4(1, 0, 0, 1);
}
