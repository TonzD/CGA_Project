import cga.exercise.components.geometry.Renderable
import cga.exercise.game.objects.obstacles.Obstacles
import cga.framework.ModelLoader.loadModel
import org.joml.Vector3f
import kotlin.random.Random

class Plane(val spawnX:Float) : Obstacles() {
    override var isIdle= true
    override val model:Renderable?
    override var nextSpawn= Random.nextInt(0, 5).toFloat()
    override var radius = 4f
    override var speed = Random.nextInt(20,25).toFloat()

    val altitude=20f
    var side=Random.nextInt(0, 100)

    init {
        model=loadModel("assets/models/Plane/WW2-Plane-LowPoly.obj",0f,0f,0f)
    }

    override fun setRandomSpawnSide(){
        side=Random.nextInt(0, 100)
        if(side<=50){
            model?.resetTransformations()
            model?.rotate(0f,Math.toRadians(-90.0).toFloat(),0f)
            model?.translate(Vector3f(-spawnX,altitude,50f))
            model?.scale(Vector3f(1f))
        }
        if(side>50){
            model?.resetTransformations()
            model?.rotate(0f,Math.toRadians(90.0).toFloat(),0f)
            model?.translate(Vector3f(spawnX,altitude,50f))
            model?.scale(Vector3f(1f))
        }
    }

    override fun setRanSpeed() {
        speed=Random.nextInt(20,25).toFloat()
    }
}