package parking

class Parking {
    private lateinit var places: Array<Car?>

    fun createLots(size: Int) {
        places = arrayOfNulls(size)
    }

    fun getStatus(): String {
        if (!this::places.isInitialized) throw ParkingIsNotInitialize()
        var output = ""
        for (index in places.indices) {
            if (places[index] != null) {
                output += if (output == "") "${index + 1} ${places[index].toString()}"
                else "\n${index + 1} ${places[index].toString()}"
            }
        }
        return if (output == "") "Parking lot is empty." else output
    }

    fun addCar(car: Car): Int {
        if (!this::places.isInitialized) throw ParkingIsNotInitialize()
        val freeIndex = places.indexOfFirst { it == null }
        if (freeIndex == -1) throw InvalidParkPlaceException()
        places[freeIndex] = car
        return freeIndex + 1
    }

    fun deleteCar(index: Int): Car {
        return try {
            if (!this::places.isInitialized) throw ParkingIsNotInitialize()
            (places[index - 1] ?: throw EmptyParkPlaceException(index)).also { places[index - 1] = null }
        } catch (e: Exception) {
            throw e
        }
    }

    fun spotByReg(number: String): String {
        if (!this::places.isInitialized) throw ParkingIsNotInitialize()
        val output = mutableListOf<Int>()
        for (index in places.indices) {
            val car = places[index] ?: break
            if (car.number.lowercase() == number.lowercase()) output.add(index + 1)
        }
        return if (output.isNotEmpty()) output.joinToString(", ")
        else "No cars with registration number $number were found."
    }

    fun spotByColor(color: String): String {
        if (!this::places.isInitialized) throw ParkingIsNotInitialize()
        val output = mutableListOf<Int>()
        for (index in places.indices) {
            val car = places[index] ?: continue
            if (car.color.lowercase() == color.lowercase()) output.add(index + 1)
        }
        return if (output.isNotEmpty()) output.joinToString(", ") else "No cars with color $color were found."
    }

    fun regByColor(color: String): String {
        if (!this::places.isInitialized) throw ParkingIsNotInitialize()
        val numbers = places.filter { (it?.color?.lowercase() ?: "") == color.lowercase() }
            .joinToString(", ") { it?.number ?: "" }
        return if (numbers != "") numbers else "No cars with color $color were found."
    }
}