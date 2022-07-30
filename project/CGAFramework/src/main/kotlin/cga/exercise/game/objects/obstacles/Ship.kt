import cga.exercise.components.geometry.Renderable
import cga.exercise.game.objects.obstacles.IObstacles
import cga.exercise.game.objects.obstacles.ShipType
import cga.framework.ModelLoader.loadModel
import org.joml.Vector3f

class Ship(val type: ShipType) : IObstacles {
    val model:Renderable?
    init {
        model=loadModel(type.toString(),0f,0f,0f)
        model?.scale(Vector3f(1f))
    }

}