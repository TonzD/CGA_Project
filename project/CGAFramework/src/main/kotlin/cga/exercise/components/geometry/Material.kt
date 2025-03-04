package cga.exercise.components.geometry

import cga.exercise.components.shader.ShaderProgram
import cga.exercise.components.texture.Texture2D
import org.joml.Vector2f

class Material(
    var diff: Texture2D,
    var emit: Texture2D,
    var specular: Texture2D,
    var shininess: Float = 50.0f,
    var tcMultiplier: Vector2f = Vector2f(1.0f),
    var normalMap: Texture2D?,
    var renderNormalMap: Boolean = false
){

    fun bind(shaderProgram: ShaderProgram) {
        emit.bind(0)
        diff.bind(1)
        specular.bind(2)
        shaderProgram.setUniform("emit",0)
        shaderProgram.setUniform("diff",1)
        shaderProgram.setUniform("spec",2)
        if (normalMap != null && renderNormalMap){
            normalMap?.bind(3)
            shaderProgram.setUniform("normalMap",3)
            shaderProgram.setUniform("renderNormalMap",1)
        } else{
            shaderProgram.setUniform("renderNormalMap",0);
        }
        shaderProgram.setUniform("shininess",shininess)
        shaderProgram.setUniform("tcMultiplier",tcMultiplier)
    }
}