package search

import java.io.File

object Controller {

    fun start(args: Array<String>) {
        val file = File(args[1])
        if (file.exists()) {
            val peopleList = file.readLines()
            val searchEngine = SearchEngine(peopleList)
            while (true) {
                println("=== Menu ===")
                Commands.values().forEach { println(it.command) }
                println(
                    when (readln().toInt()) {
                        1 -> {
                            println("Select a matching strategy: ${Strategy.values().joinToString { ", " }}")
                            val strategy = readln()
                            println("Enter a name or email to search all suitable people.")
                            val result = searchEngine.findByWords(readln().lowercase().split(" "), when (strategy) {
                                Strategy.ALL.toString() -> Strategy.ALL
                                Strategy.ANY.toString() -> Strategy.ANY
                                else -> Strategy.NONE
                            })
                            if (result.isNotEmpty()) result.joinToString(
                                "\n",
                                "\nPeople found:\n"
                            ) else "No matching people found."
                        }
                        2 -> searchEngine.getPeopleList()
                        0 -> return
                        else -> "Incorrect option! Try again."
                    }
                )
            }
        }
    }
}