package tasklist

import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.io.File
import java.time.LocalDate
import java.time.LocalTime

const val TABLE_HORIZONTAL_LINE = "+----+------------+-------+---+---+--------------------------------------------+\n"
const val TABLE_HEADER = "| N  |    Date    | Time  | P | D |                   Task                     |\n"

class TaskManager {
    private val tasks: MutableList<Task> = if (File("tasklist.json").exists()) {
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
        val type = Types.newParameterizedType(List::class.java, Task::class.java)
        val taskListAdapter = moshi.adapter<List<Task>>(type)
        taskListAdapter.fromJson(File("tasklist.json").readText())?.toMutableList() ?: mutableListOf()
    } else mutableListOf()

    private fun addZero(string: String): String {
        return if (string.length == 1 && string.toInt() < 10) {
            "0${string}"
        } else string
    }

    private fun setDate(): String {
        while (true) {
            println("Input the date (yyyy-mm-dd):")
            try {
                val tempDate = readln().split("-").toMutableList()
                tempDate[1] = addZero(tempDate[1])
                tempDate[2] = addZero(tempDate[2])
                return LocalDate.parse(tempDate.joinToString("-")).toString()
            } catch (_: Exception) {
                println("The input date is invalid")
            }
        }
    }

    private fun setTime(): String {
        while (true) {
            println("Input the time (hh:mm):")
            try {
                val tempTime = readln().split(":").toMutableList()
                tempTime[0] = addZero(tempTime[0])
                tempTime[1] = addZero(tempTime[1])
                return LocalTime.parse(tempTime.joinToString(":")).toString()
            } catch (_: Exception) {
                println("The input time is invalid")
            }
        }
    }

    private fun setPriority(): Priority {
        while (true) {
            println("Input the task priority (C, H, N, L):")
            val tempTaskPriority = readln().uppercase()
            for (priority in Priority.values()) {
                if (priority.toString() == tempTaskPriority) {
                    return priority
                }
            }
        }
    }

    private fun setTaskContent(): List<String> {
        val content = mutableListOf<String>()
        println("Input a new task (enter a blank line to end):")
        while (true) {
            val taskLine = readln().trim()
            if (taskLine == "") break
            content.add(taskLine)
        }
        return content
    }

    private fun getTask(): Task {
        println(printTasks())
        while (true) {
            try {
                println("Input the task number (1-${tasks.size}):")
                val index = readln().toInt()
                return tasks[index - 1]
            } catch (e: Exception) {
                println("Invalid task number")
            }
        }
    }

    private fun getTag(task: Task): Due {
        val taskDate = LocalDate.parse(task.date)
        val curDate = LocalDate.now()
        return if (taskDate == curDate) Due.T else if (taskDate > curDate) Due.I else Due.O
    }

    private fun getContentLine(content: String): String {
        return if (content.length >= 44) content.substring(0, 44) else {
            var result = content
            repeat(44 - result.length) {
                result += ' '
            }
            result
        }
    }

    private fun addLine(index: Int = -1, task: Task? = null, content: MutableList<String> = mutableListOf()): String {
        var result = ""
        result += if (task != null) {
            val fullContent = task.content.toMutableList()
            "| ${
                if (index < 9) "$index " else {
                    "$index"
                }
            } | ${task.date} | ${task.time} | ${task.priority.color} | ${getTag(task).color} |${
                getContentLine(
                    fullContent[0]
                )
            }|\n"
        } else {
            "|    |            |       |   |   |${getContentLine(content[0])}|\n"
        }
        val fullContent = task?.content?.toMutableList() ?: content
        if (fullContent[0].length > 44) {
            fullContent[0] = fullContent[0].substring(44)
            result += addLine(content = fullContent)
        } else if (fullContent.size > 1) {
            fullContent.removeAt(0)
            result += addLine(content = fullContent)
        }
        return result
    }

    private fun createTable(): String {
        var resultTable = TABLE_HORIZONTAL_LINE + TABLE_HEADER + TABLE_HORIZONTAL_LINE
        tasks.withIndex().forEach { resultTable += addLine(it.index + 1, it.value) + TABLE_HORIZONTAL_LINE }
        return resultTable
    }

    fun addTask(): String? {
        val priority = setPriority()
        val date = setDate()
        val time = setTime()
        val content = setTaskContent()
        return if (content.isNotEmpty()) {
            tasks.add(Task(date, priority, time, content))
            null
        } else "The task is blank"
    }

    fun editTask(): String {
        return if (tasks.isEmpty()) "No tasks have been input" else {
            val task = getTask()
            while (true) {
                println("Input a field to edit (${TaskField.values().joinToString(", ")}):")
                when (readln().uppercase()) {
                    TaskField.PRIORITY.toString() -> {
                        task.priority = setPriority(); break
                    }
                    TaskField.DATE.toString() -> {
                        task.date = setDate(); break
                    }
                    TaskField.TIME.toString() -> {
                        task.time = setTime(); break
                    }
                    TaskField.TASK.toString() -> {
                        task.content = setTaskContent(); break
                    }
                    else -> println("Invalid field")
                }
            }
            "The task is changed"
        }
    }

    fun deleteTask(): String {
        return if (tasks.isEmpty()) "No tasks have been input" else {
            tasks.remove(getTask())
            "The task is deleted"
        }
    }

    fun printTasks(): String {
        return if (tasks.isNotEmpty()) {
            createTable()
        } else {
            "No tasks have been input"
        }
    }

    fun save() {
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
        val type = Types.newParameterizedType(List::class.java, Task::class.java)
        val taskListAdapter = moshi.adapter<List<Task?>>(type)
        val info = taskListAdapter.toJson(tasks)
        File("tasklist.json").writeText(info)
    }
}