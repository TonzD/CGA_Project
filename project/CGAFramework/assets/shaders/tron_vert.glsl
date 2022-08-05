#version 330 core

layout(location = 0) in vec3 position;
layout(location = 1) in vec2 tc;
layout(location = 2) in vec3 normal;


// translation object to world
// translation Uniforms
uniform mat4 model_matrix;
uniform mat4 view_matrix;
uniform mat4 projection_matrix;

// Texture Uniforms
uniform vec2 tcMultiplier;

out struct VertexData
{
    vec3 position;
    vec2 tc;
    vec3 normal;
} vertexData;

//main
void main(){

    vec4 fragPos= model_matrix* vec4(position, 1.0f); // Position des Vertex im Worldspace
    gl_Position = projection_matrix*view_matrix* fragPos; // Position des Vertex im CameraSpace

    // Nach Tranformationen Normale korrigieren
    mat4 normalMatrix=transpose(inverse(model_matrix));  // Transponierende Inverse der Modelmatrix
    vertexData.normal = (normalMatrix * vec4(normal, 1.0f)).xyz; //multipliziert mit der Normale zur korrektur

    //Filling Outstruck with new Data
    vertexData.tc=tc*tcMultiplier; // Komponentenweise Multiplikation
    vertexData.position=(fragPos).xyz;// Position des Vertex im Worldspace
}