package tasklist

object Controller {
    private val taskManager = TaskManager()

    fun start() {
        while (true) {
            println("Input an action (${Command.values().joinToString(", ")}):")
            println(when (readln().uppercase()) {
                Command.ADD.toString() -> taskManager.addTask() ?: continue
                Command.PRINT.toString() -> taskManager.printTasks()
                Command.EDIT.toString() -> taskManager.editTask()
                Command.DELETE.toString() -> taskManager.deleteTask()
                Command.END.toString() -> taskManager.save().also { println("Tasklist exiting!") }.also { return }
                else -> "The input action is invalid"
            })
        }
    }
}