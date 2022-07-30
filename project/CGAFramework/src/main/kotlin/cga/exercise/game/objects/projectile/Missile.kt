package cga.exercise.game.objects.projectile

import cga.exercise.components.geometry.Renderable
import cga.exercise.game.objects.obstacles.Obstacles
import cga.exercise.game.objects.obstacles.ShipType
import cga.framework.ModelLoader
import org.joml.Vector3f

class Missile() {
    val model: Renderable?
    val speed=10f
    init {
        model= ModelLoader.loadModel("assets/models/Missile/Missile.obj", 0f, 0f, 0f)
        model?.scale(Vector3f(0.5f))
    }

    fun isInvisible():Boolean{
        return !(model!!.getWorldPosition()!!.x>=-50
                &&model!!.getWorldPosition()!!.x<=50
                &&model!!.getWorldPosition()!!.y>=-50
                &&model!!.getWorldPosition()!!.y<=50
                &&model!!.getWorldPosition()!!.z>=-50
                &&model!!.getWorldPosition()!!.z<=50)
    }
    fun move(dt:Float){
        model?.translate(Vector3f(0f,0f, -speed * dt))
        model?.rotate(-0.1f*dt,0f, 0f)
    }
}