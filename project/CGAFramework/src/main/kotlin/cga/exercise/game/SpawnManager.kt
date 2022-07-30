package cga.exercise.game

import Plane
import Ship
import cga.exercise.components.geometry.Renderable
import cga.exercise.game.objects.obstacles.Buff
import cga.exercise.game.objects.obstacles.BuffType
import cga.exercise.game.objects.obstacles.Obstacles
import cga.exercise.game.objects.obstacles.ShipType
import kotlin.random.Random

class SpawnManager() {
    val obstacleList= mutableListOf<Obstacles>()

    val shipS1=Ship(ShipType.SAIL,-10f)
    val shipS2=Ship(ShipType.SAIL,10f)
    val shipL=Ship(ShipType.CARGO,0f)
    val plane1=Plane(-10f)
    val plane2=Plane(10f)
    val buff=Buff()


    fun spawn(t:Float){
//        if(shipS1.spawn(t))obstacleList.add(shipS1)
//        if(shipS2.spawn(t))obstacleList.add(shipS2)
//        if(shipL.spawn(t))obstacleList.add(shipL)
        if(plane1.spawn(t))obstacleList.add(plane1)
//        if(plane2.spawn(t))obstacleList.add(plane2)
//        if(buff.spawn(t))obstacleList.add(buff)
    }

    fun move(dt:Float){
        shipS1.move(dt)
        shipS2.move(dt)
        shipL.move(dt)
        plane1.move(dt)
        plane2.move(dt)
        buff.move(dt)
    }

    fun removeInvisible(t:Float){
        if(obstacleList.isNotEmpty()) {
            for (i in obstacleList) {
                if (i.isInvisible()) {
                    i.isIdle = true
                    i.nextSpawn = t + Random.nextInt(0, 5).toFloat()
                    obstacleList.remove(i)
                    removeInvisible(t)
                    break
                }
            }
        }
    }


    fun getRenderList():MutableList<Renderable?>{
        val renderlist=mutableListOf<Renderable?>()
        for (i in obstacleList){
            renderlist.add(i.model)
        }
        return renderlist
    }
}