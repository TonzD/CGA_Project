import cga.exercise.components.geometry.Renderable
import cga.exercise.game.objects.obstacles.Obstacles
import cga.framework.ModelLoader.loadModel
import org.joml.Vector3f
import kotlin.random.Random

class Ship(val spawnX: Float) : Obstacles () {
    override var isIdle= true
    override var nextSpawn= Random.nextInt(0, 5).toFloat()
    override val model:Renderable?
    override var radius = 2.5f

    override var speed=Random.nextInt(5,10).toFloat()
    var side=Random.nextInt(0, 100)
    init {
            model=loadModel("assets/models/CargoShip/ship.obj",0f,Math.toRadians(180.0).toFloat(),0f)
    }

    override fun move(dt:Float){
            model?.translate(Vector3f(0f, 0f, -speed * dt))
    }

    override fun setRandomSpawnSide(){
        side=Random.nextInt(0, 100)
        if(side<=50){
            model?.resetTransformations()
            model?.rotate(0f,Math.toRadians(-90.0).toFloat(),0f)
            model?.translate(Vector3f(spawnX,0f,50f))
            model?.scale(Vector3f(4f))
        }
        if(side>50){
            model?.resetTransformations()
            model?.rotate(0f,Math.toRadians(90.0).toFloat(),0f)
            model?.translate(Vector3f(spawnX,0f,50f))
            model?.scale(Vector3f(4f))
        }
    }
    override fun setRanSpeed(){
        speed=Random.nextInt(5,10).toFloat()
    }
}