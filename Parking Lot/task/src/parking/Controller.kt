package parking

import kotlin.system.exitProcess

object Controller {
    private val parking = Parking()
    fun start() {
        while (true) {
            val command = readln().split(" ")
            try {
                println(
                    when (command[0]) {
                        Commands.CREATE.commandName -> {
                            parking.createLots(command[1].toInt())
                            "Created a parking lot with ${command[1]} spots."
                        }
                        Commands.STATUS.commandName -> parking.getStatus()
                        Commands.PARK.commandName -> "${command[2]} car parked in spot ${parking.addCar(Car(command[1], command[2]))}."
                        Commands.LEAVE.commandName -> {
                            parking.deleteCar(command[1].toInt())
                            "Spot ${command[1]} is free."
                        }
                        Commands.REG_BY_COLOR.commandName -> parking.regByColor(command[1])
                        Commands.SPOT_BY_COLOR.commandName -> parking.spotByColor(command[1])
                        Commands.SPOT_BY_REG.commandName -> parking.spotByReg(command[1])
                        Commands.EXIT.commandName -> exitProcess(1)
                        else -> continue
                    }
                )
            } catch (e: Exception) {
                println(e.message)
            }
        }
    }
}