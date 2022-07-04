package parking

data class Car(val number: String, val color: String) {
    override fun toString(): String {
        return "$number $color"
    }
}