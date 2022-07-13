package tasklist

data class Task(var date: String, var priority: Priority, var time: String, var content: List<String>)
