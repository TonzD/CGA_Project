package cga.exercise.game.objects.obstacles

import cga.exercise.components.geometry.Renderable
import cga.framework.ModelLoader
import org.joml.Vector3f

class Buff (val type:BuffType){
    val model: Renderable?
    init {
        model= ModelLoader.loadModel("assets/models/Parachute/parachute.obj", 0f, 0f, 0f)
        model?.scale(Vector3f(1f))
    }
}