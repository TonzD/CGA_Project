#version 330 core

layout(location = 0) in vec3 pos;
layout(location = 1) in vec2 tc;
layout(location = 2) in vec3 nor;

#define NR_POINT_LIGHTS 8
#define NR_SPOT_LIGHTS 18

//uniforms
uniform mat4 model_matrix;
uniform mat4 view_matrix;
uniform mat4 projection;
uniform vec2 tcMultiplier;
uniform vec3 pointPos[NR_POINT_LIGHTS];
uniform vec3 spotPos[NR_SPOT_LIGHTS];

out struct VertexData
{
    vec3 pos;
    vec3 nor;
    vec2 tc;
} vertexData;

out vec3 toCamera;
out vec3 pointLightDirections[NR_POINT_LIGHTS];

out vec3 spotLightDirections[NR_SPOT_LIGHTS];

void main(){

    vertexData.tc = tc * tcMultiplier; //Texturkoordinaten anpassen

    gl_Position = projection * view_matrix * model_matrix * vec4(pos, 1.0f);

    //Normale nach Transformationen zum Viewspace korrigieren
    mat4 normalMatrix = transpose(inverse(view_matrix * model_matrix));
    vertexData.nor = (normalMatrix * vec4(nor, 0.0f)).xzy;

    //specular term
    vec4 P = (view_matrix * model_matrix * vec4(pos, 1.0f)); // Position des Vertex im Viewspace
    toCamera=-P.xyz;    // Da die Kamera sich im Ursprung befindet ist der Vektor die negative Vertex Position

    for(int i = 0; i < NR_POINT_LIGHTS; i++){
        vec4 lp = view_matrix * vec4(pointPos[i], 1.0f);
        pointLightDirections[i] = (lp - P).xyz;
    }

    for(int i = 0; i < NR_SPOT_LIGHTS; i++){
        vec4 lp = view_matrix * vec4(spotPos[i], 1.0f);
        spotLightDirections[i] = (lp - P).xyz;
    }

    //        x = view * model_matrix * vec4(position, 1.0f);
    //        spotLightData.viewDir = x.xyz;
}