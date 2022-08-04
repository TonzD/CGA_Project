package cga.exercise.game.objects.player

import cga.exercise.components.geometry.Transformable
import cga.exercise.components.shader.ShaderProgram
import cga.framework.GameWindow
import cga.framework.ModelLoader.loadModel
import org.joml.Matrix4f
import org.joml.Vector3f
import org.joml.Vector4f
import org.lwjgl.glfw.GLFW

class Tank(val playerType: PlayerType){
    var lifePoints=3
    var base= loadModel("assets/models/Tank/toon_tank_base.obj",0f,0f,0f)
    var tower= loadModel("assets/models/Tank/toon_tank_tower.obj",0f,0f,0f)
    var barrel= loadModel("assets/models/Tank/toon_tank_barrel.obj",0f,0f,0f)
    var barrelAngle=0f
    var aiming=false
    var shooting=false
    var radius=2f
    var dmg=1


    init{
        tower?.parent= base
        barrel?.parent=tower
    }

    fun getScaledRadius():Float=radius*base!!.scaleFactor
    fun checkCollision(targetRadius:Float, targetWorldPosition:Vector3f):Boolean{
        val ownWorldPosition=base!!.getWorldPosition()
        val distance = Math.sqrt(
                        (ownWorldPosition.x - targetWorldPosition.x) * (ownWorldPosition.x - targetWorldPosition.x) +
                        (ownWorldPosition.y - targetWorldPosition.y) * (ownWorldPosition.y - targetWorldPosition.y) +
                        (ownWorldPosition.z - targetWorldPosition.z) * (ownWorldPosition.z - targetWorldPosition.z).toDouble())
        return distance < (getScaledRadius() + targetRadius)
    }
    fun loseLp(dmg:Int){
        lifePoints -= dmg
        println(playerType.toString() +" current LP: "+lifePoints)
        if (lifePoints<=0){
            println(playerType.toString() +" is dead!")
        }
    }

    fun gainLp(){
        if (lifePoints<3){
            lifePoints++
            println(playerType.toString() +" current LP: "+lifePoints)
        } else println("already MAX Lp")
    }

  //  fun isDead():Boolean=lifePoints<=0

    fun move(window:GameWindow,dt:Float){
        if(aiming && !shooting){
            val angle = Math.toRadians(45.0).toFloat()*dt
            if (window.getKeyState(GLFW.GLFW_KEY_W)&&barrelAngle>=0f){
                barrel?.rotate(-angle, 0f, 0f)
                barrelAngle-=Math.toDegrees(angle.toDouble()).toFloat()
            }
            if (window.getKeyState(GLFW.GLFW_KEY_S)&&barrelAngle<=45f){
                barrel?.rotate(angle,0f,0f)
                barrelAngle+=Math.toDegrees(angle.toDouble()).toFloat()
            }
            if (window.getKeyState(GLFW.GLFW_KEY_D)) tower?.rotate(0f, -angle, 0f)
            if (window.getKeyState(GLFW.GLFW_KEY_A)) tower?.rotate(0f, angle, 0f)
        }
        if(!aiming&&!shooting) {
            val z = 8f
            val angle = Math.toRadians(60.0).toFloat() * dt
            if (window.getKeyState(GLFW.GLFW_KEY_W)) {
                base?.translate(Vector3f(0f, 0f, -z * dt))
                if(outOfMap()){
                    base?.translate(Vector3f(0f, 0f, z * dt))
                }
            }
            if (window.getKeyState(GLFW.GLFW_KEY_S)){
                base?.translate(Vector3f(0f, 0f, z * dt))
                if(outOfMap()){
                    base?.translate(Vector3f(0f, 0f, -z * dt))
                }
            }
            if (window.getKeyState(GLFW.GLFW_KEY_A)){
                base?.rotate(0f, angle, 0f)
                if(outOfMap()){
                    base?.translate(Vector3f(0f, -angle, 0f))
                }
            }
            if (window.getKeyState(GLFW.GLFW_KEY_D)){
                base?.rotate(0f, -angle, 0f)
                if(outOfMap()){
                    base?.translate(Vector3f(0f, +angle, 0f))
                }
            }
        }
    }

    fun outOfMap():Boolean{
        if(playerType==PlayerType.PLAYER1) {
            return !(base!!.getWorldPosition().x <= 50 && base!!.getWorldPosition().x >= -50 &&
                    base!!.getWorldPosition().y <= 50 && base!!.getWorldPosition().y >= 4 &&
                    base!!.getWorldPosition().z <= 50 && base!!.getWorldPosition().z >= 33)
        }
        else{
            return !(base!!.getWorldPosition().x <= 50 && base!!.getWorldPosition().x >= -50 &&
                    base!!.getWorldPosition().y <= 50 && base!!.getWorldPosition().y >= 4 &&
                    base!!.getWorldPosition().z <= -33 && base!!.getWorldPosition().z >= -50)
        }
    }

    fun render(shaderProgram: ShaderProgram){
        base?.render(shaderProgram)
        tower?.render(shaderProgram)
        barrel?.render(shaderProgram)
    }

}