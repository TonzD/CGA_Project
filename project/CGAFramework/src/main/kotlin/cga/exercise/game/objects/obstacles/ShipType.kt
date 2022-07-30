package cga.exercise.game.objects.obstacles

enum class ShipType {
    CARGO,SAIL;

    override fun toString(): String {
        val string=when(this){
            CARGO->"assets/models/CargoShip/ship.obj"
            SAIL ->"assets/models/LowPolyWoodSail/LowPolyWoodSail.obj"
        }
        return string
    }
}