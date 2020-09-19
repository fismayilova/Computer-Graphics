#version 430 

in vec3 position;
in vec3 normal;

uniform mat4 mv_matrix;
uniform mat4 proj_matrix;

uniform vec3 light;

uniform vec3 ambient;
uniform vec3 diffuse;
uniform vec3 specularL;
uniform float shininess;

out vec3 color;

void main(void) {

    gl_Position = proj_matrix * mv_matrix * vec4(position, 1.0);

		vec3 posI = vec3(mv_matrix * vec4(position, 1.0));
		vec3 L = normalize(light.xyz - posI);
		vec3 E = normalize(-posI);
		vec3 H = normalize(L + E);
		vec3 N = (normalize(mv_matrix * vec4(normal, 0.0))).xyz;

	color = ambient;

		float ln = max(dot(L, N),0.0);
		if (ln > 0.0) {
			float s = pow(max(dot(N, H), 0.0), shininess);
 color = ambient + (ln * diffuse) + (s * specularL);
    }
}
