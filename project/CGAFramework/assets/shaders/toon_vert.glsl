#version 330 core

layout(location = 0) in vec3 position;
layout(location = 1) in vec2 tc;
layout(location = 2) in vec3 normal;

#define NR_POINT_LIGHTS 8
#define NR_SPOT_LIGHTS 18

//uniforms
uniform mat4 model_matrix;
uniform mat4 view_matrix;
uniform mat4 projection_matrix;
uniform vec2 tcMultiplier;
uniform vec3 pointLightPositions;
uniform vec3 spotLightPositions;
												//uniform vec3 pointLightPos;
												//uniform vec3 spotLightPos;
												//uniform vec3 pointLight2Pos;



out struct VertexData
	{
		vec3 position;
		vec3 normal;
		vec2 tc;
		vec3 viewDir;
} vertexData;

  out vec3 pointLightDirections;

  out vec3 spotLightDirections;
	  out struct PointLightData
	  {
	      vec3 lightDir;
	      vec3 viewDir;
	  }pointLightData;

	  out struct SpotLightData
	  {
	      vec3 lightDir;
	  }spotLightData;

	  void main(){

					 vertexData.tc = tc * tcMultiplier; //Texturen

					 gl_Position = projection_matrix * view_matrix * model_matrix * vec4(position, 1.0f);

																					 //Normale in View space(Camera space) berechnen
					 mat4 normalMatrix = transpose(inverse(view_matrix * model_matrix));

					vec4 col = normalMatrix * vec4(normal, 0.0f);
					vertexData.normal = col.xyz;

					//specular term
					vec4 P = (view_matrix * model_matrix * vec4(position, 1.0f));
					vertexData.viewDir = -P.xyz; //Vector vom Vertex zur Kamera --> Position der Kamera - Position des Vertex, da Kamera im Ursprung einfach -Position Vertex

					for(int i = 0; i < NR_POINT_LIGHTS; i++){
					vec4 lp = view_matrix * vec4(pointLightPositions, 1.0f);
					pointLightDirections = (lp - P).xyz;
					}

					 for(int i = 0; i < NR_SPOT_LIGHTS; i++){
																vec4 lp = view_matrix * vec4(spotLightPositions, 1.0f);
																										  spotLightDirections = (lp - P).xyz;
															}

					 //        x = view * model_matrix * vec4(position, 1.0f);
					 //        spotLightData.viewDir = x.xyz;
				 }