package cga.exercise.game


import cga.exercise.components.camera.TronCamera
import cga.exercise.components.geometry.Material
import cga.exercise.components.geometry.Mesh
import cga.exercise.components.geometry.Renderable
import cga.exercise.components.geometry.VertexAttribute
import cga.exercise.components.light.PointLight
import cga.exercise.components.shader.ShaderProgram
import cga.exercise.components.texture.Texture2D
import cga.exercise.game.objects.obstacles.BuffType
import cga.exercise.game.objects.player.PlayerType
import cga.exercise.game.objects.player.Tank
import cga.exercise.game.objects.projectile.Missile
import cga.exercise.game.objects.projectile.Orb
import cga.framework.GLError
import cga.framework.GameWindow
import cga.framework.ModelLoader.loadModel
import cga.framework.OBJLoader
import org.joml.Vector2f
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL11.*
import kotlin.math.floor


/**
 * Created by Fabian on 16.09.2017.
 */
class Scene(private val window: GameWindow) {
    private var staticShader: ShaderProgram
    private val toonShader: ShaderProgram
    private val tronShader: ShaderProgram
    private var renderList: MutableList<Renderable> = mutableListOf()
    private var projectileList: MutableList<Renderable> = mutableListOf()
    val newCam = TronCamera()
    var pointLight1: PointLight
    var pointLight2: PointLight
    var staticColor = Vector3f(1.0f,1f,1f)
    var cXPos = 0.0
    var cYPos = 0.0
    var lastRoundedTime=0f
    val player1 = Tank(PlayerType.PLAYER1)
    val player2 = Tank(PlayerType.PLAYER2)
    var currentPlayer = player1
    var enemyPlayer = player2
    var currentPlayerTurnTime=15f
    val missile= Missile()
    val orb= Orb()
    val spawnManager=SpawnManager()
    var explosionEnd=0f
    var explosionStarted=false
    var tCopy=0f

    //scene setup

    init {

        tronShader = ShaderProgram("assets/shaders/tron_vert.glsl", "assets/shaders/tron_frag.glsl")
        toonShader = ShaderProgram("assets/shaders/toon_vert.glsl", "assets/shaders/toon_frag.glsl")
        staticShader= tronShader
        //initial opengl state
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f); GLError.checkThrow()
//        glEnable(GL_CULL_FACE); GLError.checkThrow()
//        glFrontFace(GL_CCW); GLError.checkThrow()//CCW=CounterClockWise
//        glCullFace(GL_BACK); GLError.checkThrow()
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
        val material = Material(diff, emit, specular, shininess, tcMultiplier, null, false)

        val rBoden = Renderable(mutableListOf(Mesh(vertices, indices, attributes, material)))
        // rBoden.scale(Vector3f(0.03f))
        var angle = Math.toRadians(45.0).toFloat()
        //rBoden.rotate(0f,0f,angle)

      // renderList.add(rBoden)


        //Camera binden
        angle = Math.toRadians(-35.0).toFloat()
        newCam.translate(Vector3f(0f, 4f, 10f))
        newCam.rotate(angle, 0f, 0f)
        newCam.parent = currentPlayer.base

        val scenescale = 0.75
        val spawnY = 6.0 * scenescale
        val spawnZ = 55 * scenescale
        // Erste Lichtquelle
        pointLight1 = PointLight(
            Vector3f(0f, 60f, spawnZ.toFloat()),
            Vector3f(1f, 1f, 1f)
        )
        //pointLight1.rotate(Math.toRadians(90.0).toFloat(), 0f, 0f)

        // zweite Lichtquelle
        pointLight2 = PointLight(
            Vector3f(0f, 60f, -spawnZ.toFloat()),
            Vector3f(1f, 1f, 1f)
        )
      //  spotLight2.parent=currentPlayer.base


        player1.base?.translate(Vector3f(0f, spawnY.toFloat(), spawnZ.toFloat()))
        player2.base?.translate(Vector3f(0f, spawnY.toFloat(), -spawnZ.toFloat()))
        player2.base?.rotate(0f,Math.toRadians(180.0).toFloat(),0f)


        val scene = loadModel("assets/models/scene/gamescene.obj",0f,0f,0f)
//        scene?.meshes!![2].material.tcMultiplier = Vector2f(3f,3f)
      //  scene.meshes[2].material.diff = Texture2D("assets/models/Scene/scene_textures/waterdiff.png",true)
        scene?.meshes!![2].material.normalMap = Texture2D("assets/models/Scene/scene_textures/waternormal.png", true)
        scene.meshes[2].material.renderNormalMap = true
        scene.meshes[2].material.tcMultiplier= Vector2f(2.0f)
        scene.meshes[4].material.tcMultiplier = Vector2f(5f,1f)
        scene.scale(Vector3f(scenescale.toFloat()))
        renderList.add(scene)
    }
    fun render(dt: Float, t: Float) {
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
        staticShader.use()
        newCam.bind(staticShader) // Macht es einen Unterschied, wenn wir die Camera vor den Objekten Binden oder danach?

        for (i in renderList) {
            i.render(staticShader)
        }
        for (i in spawnManager.getRenderList()) {
            i?.render(staticShader)
        }
        for (i in projectileList) {
            i.render(staticShader)
        }
        player1.render(staticShader)
        player2.render(staticShader)
        pointLight1.bind(staticShader,staticColor,0)
   //     pointLight2.bind(staticShader,staticColor,1)

    }

    fun update(dt: Float, t: Float) {
        tCopy=t
        if(currentPlayerTurnTime<t && enemyPlayer.isAlive()){
            currentPlayerTurnTime=floor(t)+15f
            switchPlayer()
        }else{
            if(currentPlayer.shooting||explosionStarted) {
                currentPlayerTurnTime += 1f
            }else{
                if(enemyPlayer.isAlive()) showCountdown(t)}
        }
        currentPlayer.move(window,dt)
        spawnManager.move(dt,t)
        spawnManager.spawn(t)
        spawnManager.removeInvisible(t)
        checkBuffCollision()
        if(currentPlayer.shooting) checkMissileCollision(dt,t)
        endExplosionAnimation(dt,t)
    }
    fun showCountdown(t:Float){
        if(lastRoundedTime< floor(t)){
            lastRoundedTime= floor(t)
            println(currentPlayerTurnTime-lastRoundedTime)
        }
    }
    fun checkMissileCollision(dt:Float,t:Float) {
        if (spawnManager.checkMissileCollision(missile.getScaledRadius(), missile.model!!.getWorldPosition())){
            println("obstacleHit")
            currentPlayer.shooting=false
            startExplosionAnimation(t)
        }
        if (missile.checkCollision(enemyPlayer.getScaledRadius(),enemyPlayer.base!!.getWorldPosition())) {
            enemyPlayer.loseLp(currentPlayer.dmg)
            currentPlayer.dmg=1
            currentPlayer.shooting=false
            startExplosionAnimation(t)
            println("playerHit")
        }
        if(missile.outOfMap()){
            currentPlayer.shooting=false
            startExplosionAnimation(t)
        }
        else {
            missile.move(dt)
        }
    }

    fun checkBuffCollision(){
        if(currentPlayer.checkCollision(spawnManager.buff.radius,spawnManager.buff.model!!.getWorldPosition())){
            if(spawnManager.buff.buffType==BuffType.HEAL) {
                println(currentPlayer.playerType.toString()+" gained a healbuff")
                currentPlayer.gainLp()
                spawnManager.buff.deSpawn=true
            }
            if(spawnManager.buff.buffType==BuffType.DMG){
                println(currentPlayer.playerType.toString()+" gained a damagebuff")
                currentPlayer.dmg=2
                spawnManager.buff.deSpawn=true
            }
        }
    }

    fun startExplosionAnimation(t:Float){
        explosionEnd=t+1.5f
        orb.model!!.parent=missile.model
        orb.model!!.scale(Vector3f(7f))
        projectileList.add(orb.model!!)
        explosionStarted=true

    }
    fun endExplosionAnimation(dt:Float,t:Float){
        if(explosionStarted){
            orb.move(dt)
            if(explosionEnd<=t){
                missile.model!!.resetTransformations()
                orb.model!!.resetTransformations()
                projectileList.clear()
                currentPlayerTurnTime=floor(t)+15f
                switchPlayer()
                explosionStarted=false
            }
        }
    }
    fun onKey(key: Int, scancode: Int, action: Int, mode: Int) {
        if (!explosionStarted && !currentPlayer.shooting && key == GLFW_KEY_E && action == GLFW_PRESS) zoomIn()
        if (key == GLFW_KEY_T && action == GLFW_PRESS) switchShader()
        if (!currentPlayer.shooting &&currentPlayer.aiming && key == GLFW_KEY_SPACE && action == GLFW_PRESS) {
            missile.chargeStart =tCopy
        }
        if (!explosionStarted && !currentPlayer.shooting &&currentPlayer.aiming && key == GLFW_KEY_SPACE && action == GLFW_RELEASE) {
            missile.chargeEnd =tCopy
            missile.calculateCharge()
            shoot()
        }
    }

    fun switchShader(){
        if (staticShader==tronShader){
            staticShader=toonShader
            println("switched to celshader")
        }else{
            staticShader=tronShader
            println("switched to tronshader")
        }
    }

    fun shoot(){
        missile.model!!.parent=currentPlayer.barrel
        missile.model.resetTransformations()
        missile.model.translate(Vector3f(0f,2f,-5f))
        missile.model.scale(Vector3f(0.2f))
        newCam.parent=missile.model
        newCam.resetTransformations()
        newCam.translate(Vector3f(0f,2f,50f))
        newCam.rotate(Math.toRadians(0.0).toFloat(),0f,0f)
        projectileList.add(missile.model)
        currentPlayer.shooting=true
    }

    fun onMouseMove(xpos: Double, ypos: Double) {
        val dXPos = cXPos-xpos
        val dYPos = cYPos-ypos
        if (!currentPlayer.aiming){
            currentPlayer.tower?.rotate(0f,Math.toRadians(dXPos*0.02).toFloat(),0f)
            newCam.rotateAroundPoint(0f,Math.toRadians(dXPos*0.02).toFloat(),0f,Vector3f(0f))
            newCam.rotateAroundPointOwn(Math.toRadians(dYPos*0.02).toFloat(),0f,0f,Vector3f(0f))
        }else if(currentPlayer.shooting){
            newCam.rotateAroundPoint(0f,Math.toRadians(dXPos*0.02).toFloat(),0f,Vector3f(0f))
        }
        cXPos=xpos
        cYPos=ypos
    }

    fun zoomIn(){
        if (currentPlayer.aiming){
            currentPlayer.tower?.resetTransformations()
            newCam.parent=currentPlayer.base
            newCam.resetTransformations()
            newCam.translate(Vector3f(0f,4f,10f))
            newCam.rotate(Math.toRadians(-35.0).toFloat(),0f,0f)
            currentPlayer.aiming=false
        }else{
            newCam.parent=currentPlayer.barrel
            newCam.resetTransformations()
            newCam.translate(Vector3f(0f,3.5f,0.8f))
            newCam.rotate(Math.toRadians(0.0).toFloat(),0f,0f)
            currentPlayer.aiming=true
        }
    }


    fun switchPlayer(){
        currentPlayer.aiming=false
        if(currentPlayer==player1){
            currentPlayer=player2
            enemyPlayer=player1
            println("---"+currentPlayer.playerType.toString()+" Turn ---")
            resetCam()
        }
        else{
            currentPlayer=player1
            enemyPlayer=player2
            println("---"+currentPlayer.playerType.toString()+" Turn ---")
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