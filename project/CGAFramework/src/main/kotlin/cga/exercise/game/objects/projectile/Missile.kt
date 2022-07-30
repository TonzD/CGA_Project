package cga.exercise.game.objects.projectile

import cga.exercise.components.geometry.Renderable
import cga.framework.ModelLoader
import org.joml.Vector3f

class Missile {
    val model: Renderable?
    init {
        model= ModelLoader.loadModel("assets/models/Missile/Missile.obj", 0f, 0f, 0f)
        model?.scale(Vector3f(1f))
    }
}