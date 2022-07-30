package cga.exercise.game.objects.player

import cga.exercise.components.geometry.Transformable
import cga.exercise.components.shader.ShaderProgram
import cga.framework.ModelLoader.loadModel

class Tank{
    var lifePoints=5
    var base= loadModel("assets/models/Tank/toon_tank_base.obj",0f,0f,0f)
    var tower= loadModel("assets/models/Tank/toon_tank_tower.obj",0f,0f,0f)
    var barrel= loadModel("assets/models/Tank/toon_tank_barrel.obj",0f,0f,0f)
    var barrelAngle=0f
    var zoom=false
    var shooting=false

    init{
        tower?.parent= base
        barrel?.parent=tower
    }

    fun loseLp(){
        lifePoints--
        if (lifePoints<=0){
            println("u dead son")
        }
    }

    fun gainLp(){
        if (lifePoints<5){
            lifePoints++
        } else println("u're cheating!")
    }

    fun isDead():Boolean=lifePoints<=0

    fun render(shaderProgram: ShaderProgram){
        base?.render(shaderProgram)
        tower?.render(shaderProgram)
        barrel?.render(shaderProgram)
    }

}