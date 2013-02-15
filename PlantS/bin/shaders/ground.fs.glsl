#version 330

/* IN */
in vec3 v_Position;
in vec3 v_Normal;
in vec2 v_Texcoords;

/* UNIFORM */

uniform sampler2D uTex;
uniform mat4 uniform_MV = mat4(1.f);

/* STRUCT */
struct DirectionalLight {
                vec4 dir;
                vec3 intensity;
};

/* PROTO */
vec3 lambertLight(DirectionalLight light, vec3 normal);

out vec4 fFragColor;

/* MAIN */
void main (void) {

	DirectionalLight light;
	light.dir = vec4(-0.5f, -1.0f, 0.5f, 0.0f);
	light.intensity = vec3(1, 1, 1);
	
	vec4 lb = vec4(lambertLight(light, v_Normal), 1.0f);
	vec4 tex = texture(uTex, v_Texcoords);
	
	fFragColor = tex * lb;
	
} ;

vec3 lambertLight(DirectionalLight light, vec3 normal) {
    float cosA = dot(normalize(light.dir), vec4(normalize(normal), 1.0f));
    float A = 0.5*acos(cosA);
    return A * light.intensity;
}