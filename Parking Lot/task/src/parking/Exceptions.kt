package parking

class InvalidParkPlaceException: Exception("Sorry, the parking lot is full.")

class EmptyParkPlaceException(index: Int): Exception("There is no car in spot $index.")

class ParkingIsNotInitialize(): Exception("Sorry, a parking lot has not been created.")