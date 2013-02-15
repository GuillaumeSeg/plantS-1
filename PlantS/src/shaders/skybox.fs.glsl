#version 330

in vec3 v_Position;
in vec2 v_Texcoords;

/* UNIFORM */
uniform sampler2D uTex;


/* MAIN */
void main (void) {

	gl_FragColor = texture(uTex, v_Texcoords);
	
} ;
