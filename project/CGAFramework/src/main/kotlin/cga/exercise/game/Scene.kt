package cga.exercise.game


import Ship
import cga.exercise.components.camera.TronCamera
import cga.exercise.components.geometry.Material
import cga.exercise.components.geometry.Mesh
import cga.exercise.components.geometry.Renderable
import cga.exercise.components.geometry.VertexAttribute
import cga.exercise.components.light.PointLight
import cga.exercise.components.light.SpotLight
import cga.exercise.components.shader.ShaderProgram
import cga.exercise.components.texture.Texture2D
import cga.exercise.game.objects.obstacles.Obstacles
import cga.exercise.game.objects.obstacles.ShipType
import cga.exercise.game.objects.player.Tank
import cga.exercise.game.objects.projectile.Missile
import cga.framework.GLError
import cga.framework.GameWindow
import cga.framework.ModelLoader.loadModel
import cga.framework.OBJLoader
import org.joml.Math.sin
import org.joml.Vector2f
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL11.*


/**
 * Created by Fabian on 16.09.2017.
 */
class Scene(private val window: GameWindow) {
    private val staticShader: ShaderProgram
    private var renderList: MutableList<Renderable> = mutableListOf()
    private var obstacleList: MutableList<Obstacles> = mutableListOf()
    private var projectileList: MutableList<Missile> = mutableListOf()
    val newCam = TronCamera()
    var spotLight2: SpotLight
    var staticColor = Vector3f(0f, 1f, 0f)
    var cXPos = 0.0
    val player1 = Tank()
    val player2 = Tank()
    var currentPlayer = player1
    val missile= Missile()
    val spawnManager=SpawnManager()

    //scene setup

    init {
        staticShader = ShaderProgram("assets/shaders/tron_vert.glsl", "assets/shaders/tron_frag.glsl")

        //initial opengl state
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f); GLError.checkThrow()
        glEnable(GL_CULL_FACE); GLError.checkThrow()
        glFrontFace(GL_CCW); GLError.checkThrow()//CCW=CounterClockWise
        glCullFace(GL_BACK); GLError.checkThrow()
        glEnable(GL_DEPTH_TEST); GLError.checkThrow()
        glDepthFunc(GL_LESS); GLError.checkThrow()

        //ObjectLoader
        //load an object and create a mesh

        var objRes = OBJLoader.loadOBJ("assets/models/ground.obj")
        //Get all meshes of the first object
        var objMeshList = objRes.objects[0].meshes[0]

        var vertices = objMeshList.vertexData
        var indices = objMeshList.indexData

        var attributePos = VertexAttribute(3, GL_FLOAT, 32, 0)
        var attributeTex = VertexAttribute(2, GL_FLOAT, 32, 3 * 4)
        var attributeNorm = VertexAttribute(3, GL_FLOAT, 32, 5 * 4)
        var attributes = arrayOf(attributePos, attributeTex, attributeNorm)

        val diff = Texture2D("assets/textures/ground_diff.png", true)
        diff.setTexParams(GL_REPEAT, GL_REPEAT, GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR)
        val emit = Texture2D("assets/textures/ground_emit.png", true)
        emit.setTexParams(GL_REPEAT, GL_REPEAT, GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR)
        val specular = Texture2D("assets/textures/ground_spec.png", true)
        specular.setTexParams(GL_REPEAT, GL_REPEAT, GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR)
        val shininess = 5.0f
        val tcMultiplier = Vector2f(64f, 64f)
        val material = Material(diff, emit, specular, shininess, tcMultiplier)

        val rBoden = Renderable(mutableListOf(Mesh(vertices, indices, attributes, material)))
        // rBoden.scale(Vector3f(0.03f))
        var angle = Math.toRadians(45.0).toFloat()
        //rBoden.rotate(0f,0f,angle)

        renderList.add(rBoden)

        //Camera binden
        angle = Math.toRadians(-35.0).toFloat()
        newCam.translate(Vector3f(0f, 4f, 10f))
        newCam.rotate(angle, 0f, 0f)
        newCam.parent = currentPlayer.base

        // Weitere Lichtquelle
        spotLight2 = SpotLight(
            Vector3f(0f, 5f, 0f),
            Vector3f(1f, 0f, 0f),
            Math.toRadians(20.0).toFloat(),
            Math.toRadians(15.0).toFloat()
        )
        spotLight2.rotateWorld(Math.toRadians(-90.0).toFloat(), 0f, 0f)

        player1.base?.translate(Vector3f(0f, 0f, 10f))
        player2.base?.translate(Vector3f(0f, 0f, -10f))
    }
    fun render(dt: Float, t: Float) {
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
        staticShader.use()
        newCam.bind(staticShader) // Macht es einen Unterschied, wenn wir die Camera vor den Objekten Binden oder danach?
        var newColor = Vector3f(sin(t) * 0.3f, sin(t) * 0.6f, sin(t) * 0.9f)
        // var newColor= Vector3f(0f,1f,0f)
//        pointLight.bind(staticShader,newColor)
//        spotLight1.bind(staticShader,newCam.getCalculateViewMatrix(),1)
        spotLight2.bind(staticShader, newCam.getCalculateViewMatrix(), 2)
        for (i in renderList) {
            i.render(staticShader)
        }
        for (i in spawnManager.getRenderList()) {
            i?.render(staticShader)
        }
        //  motorrad?.render(staticShader,newColor)//,Vector3f(1f,1f,1f))
        player1.render(staticShader)
        player2.render(staticShader)

    }

    fun update(dt: Float, t: Float) {
        playerMovement(dt)
        spawnManager.move(dt)
        spawnManager.spawn(t)
        spawnManager.removeInvisible(t)
        missile.move(dt)
    }
    fun onKey(key: Int, scancode: Int, action: Int, mode: Int) {
        if(key==GLFW_KEY_E&&action==GLFW_PRESS) zoomIn()
        if(key==GLFW_KEY_T&&action==GLFW_PRESS) switchPlayer()
        if(currentPlayer.zoom&& key== GLFW_KEY_SPACE&& action==GLFW_PRESS){
            shoot()
        }
    }
    fun shoot(){
        missile.model!!.parent=currentPlayer.barrel
        newCam.parent=missile.model
        newCam.resetTransformations()
        newCam.translate(Vector3f(0f,2f,10f))
        newCam.rotate(Math.toRadians(-45.0).toFloat(),0f,0f)
        renderList.add(missile.model)
        currentPlayer.shooting=true
    }

    fun onMouseMove(xpos: Double, ypos: Double) {
        val dXPos = cXPos-xpos
        if (!currentPlayer.zoom){
        currentPlayer.tower?.rotate(0f,Math.toRadians(dXPos*0.02).toFloat(),0f)
        newCam.rotateAroundPoint(0f,Math.toRadians(dXPos*0.02).toFloat(),0f,Vector3f(0f))
        }else if(currentPlayer.shooting){
            newCam.rotateAroundPoint(0f,Math.toRadians(dXPos*0.02).toFloat(),0f,Vector3f(0f))
        }
        cXPos=xpos
    }

    fun zoomIn(){
        if (currentPlayer.zoom){
            currentPlayer.tower?.resetTransformations()
            newCam.parent=currentPlayer.base
            newCam.resetTransformations()
            newCam.translate(Vector3f(0f,4f,10f))
            newCam.rotate(Math.toRadians(-35.0).toFloat(),0f,0f)
            currentPlayer.zoom=false
        }else{
            newCam.parent=currentPlayer.barrel
            newCam.resetTransformations()
            newCam.translate(Vector3f(0f,3.5f,0.8f))
            newCam.rotate(Math.toRadians(0.0).toFloat(),0f,0f)
            currentPlayer.zoom=true
        }
    }

    fun playerMovement(dt:Float){
        if(currentPlayer.zoom&&!currentPlayer.shooting){
            val z = 8f
            val angle = Math.toRadians(60.0).toFloat()*dt
            if (window.getKeyState(GLFW_KEY_W)&&currentPlayer.barrelAngle>=0f){
                currentPlayer.barrel?.rotate(-angle, 0f, 0f)
                currentPlayer.barrelAngle-=Math.toDegrees(angle.toDouble()).toFloat()
            }
            if (window.getKeyState(GLFW_KEY_S)&&currentPlayer.barrelAngle<=45f){
                currentPlayer.barrel?.rotate(angle,0f,0f)
                currentPlayer.barrelAngle+=Math.toDegrees(angle.toDouble()).toFloat()
            }
            if (window.getKeyState(GLFW_KEY_D)) currentPlayer.tower?.rotate(0f, -angle, 0f)
            if (window.getKeyState(GLFW_KEY_A)) currentPlayer.tower?.rotate(0f, angle, 0f)
        }
        if(!currentPlayer.zoom&&!currentPlayer.shooting) {
            val z = 8f
            val angle = Math.toRadians(60.0).toFloat() * dt
            if (window.getKeyState(GLFW_KEY_W)) currentPlayer.base?.translate(Vector3f(0f, 0f, -z * dt))
            if (window.getKeyState(GLFW_KEY_S)) currentPlayer.base?.translate(Vector3f(0f, 0f, z * dt))
            if (window.getKeyState(GLFW_KEY_A)) currentPlayer.base?.rotate(0f, angle, 0f)
            if (window.getKeyState(GLFW_KEY_D)) currentPlayer.base?.rotate(0f, -angle, 0f)
        }
    }

    fun switchPlayer(){
        currentPlayer.zoom=false
        if(currentPlayer==player1){
            currentPlayer=player2
            resetCam()
        }
        else{
            currentPlayer=player1
            resetCam()
        }
    }
    fun resetCam(){
        currentPlayer.tower?.resetTransformations()
        newCam.parent=currentPlayer.base
        newCam.resetTransformations()
        newCam.translate(Vector3f(0f,4f,10f))
        newCam.rotate(Math.toRadians(-35.0).toFloat(),0f,0f)
    }
    fun cleanup() {}
}