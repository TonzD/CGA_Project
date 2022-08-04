package cga.exercise.game.objects.projectile

import cga.exercise.components.geometry.Renderable
import cga.framework.ModelLoader
import org.joml.Vector3f


class Orb {
    val model: Renderable?

    init {
        model= ModelLoader.loadModel("assets/models/Orb.obj", 0f, 0f, 0f)
    }
    fun move(dt:Float){
        model?.scale(Vector3f(1f+0.5f*dt))
    }
}