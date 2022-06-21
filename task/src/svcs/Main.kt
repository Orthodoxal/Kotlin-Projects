package svcs

import java.io.File
import java.security.MessageDigest
import java.util.*

const val directory = "vcs"
const val commitsDirectory = "vcs/commits"
const val fileNameConfig = "vcs/config.txt"
const val fileNameIndex = "vcs/index.txt"
const val fileNameLog = "vcs/log.txt"

val allCommands =
    "These are SVCS commands:\n" +
            "config     Get and set a username.\n" +
            "add        Add a file to the index.\n" +
            "log        " +
            "Show commit logs.\n" + "commit     " +
            "Save changes.\n" +
            "checkout   Restore a file."

fun checkout(ID: String? = null): String {
    return if (ID == null) "Commit id was not passed." else {
        if (File("$commitsDirectory/$ID").exists() && File("$commitsDirectory/$ID").isDirectory) {
            val files = File(fileNameIndex).readLines()
            files.forEach { if (!File(it).isDirectory) File(it).delete() }

            val commitFiles = File("$commitsDirectory/$ID").listFiles()
            commitFiles?.forEach { it.copyTo(File(it.name)) }
            "Switched to commit $ID."
        } else {
            "Commit does not exist."
        }
    }
}

fun log() {
    val fileText = File(fileNameLog).readLines()
    if (fileText.isNotEmpty()) {
        fileText.forEach { println(it) }
    } else {
        println("No commits yet.")
    }
}

fun getFilesHash(path: String): String {
    val md = MessageDigest.getInstance("SHA-512")
    md.update(File(path).readBytes())
    val digest = md.digest()
    return Arrays.toString(digest)
}

fun getLastCommitID(): String {
    val commits = File(commitsDirectory).listFiles()
    return if (commits != null && commits.isNotEmpty()) {
        val lastCommit = commits.last()
        return lastCommit?.name ?: ""
    } else ""
}

fun comparator(): String {
    val files = File(fileNameIndex).readLines()
    var lastCommit = getLastCommitID()
    if (files.isNotEmpty()) {
        //проверка наличия коммитов
        if (lastCommit != "") {
            //присваивание полного пути
            lastCommit = "$commitsDirectory/$lastCommit"
            if (files.size == (File(lastCommit).listFiles()?.size ?: -1)) {
                //проверка хешей
                var equals = true
                for (file in files) {
                    if (getFilesHash(file) != getFilesHash("$lastCommit/$file")) {
                        equals = false
                        break
                    }
                }
                if (equals) {
                    return ""
                }
            }
        }
        //вычисление названия коммита
        var hash = ""
        for (file in files) {
            hash += getFilesHash(file)
        }
        return hash.hashCode().toString()
    } else {
        return ""
    }
}

fun addToLog(commitName: String, author: String, description: String) {
    val log = File(fileNameLog).readText()
    File(fileNameLog).writeText("commit $commitName\nAuthor: $author\n$description\n\n")
    File(fileNameLog).appendText(log)
}

fun commit(description: String? = null) {
    if (description == null) {
        println("Message was not passed.")
        return
    }
    val commit = comparator()
    if (commit != "") {
        //создание коммита
        val comDir = File("$commitsDirectory/$commit")
        val files = File(fileNameIndex).readLines()
        comDir.mkdir()
        for (file in files) {
            File(file).copyTo(File("$commitsDirectory/$commit/$file"))
        }
        addToLog(commit, File(fileNameConfig).readText(), description ?: "")
        println("Changes are committed.")
    } else {
        println("Nothing to commit.")
    }
}

fun addFile(name: String) {
    if (File(name).exists()) {
        File(fileNameIndex).appendText("$name\n")
        println("The file '$name' is tracked.")
    } else {
        println("Can't find '$name'.")
    }
}

fun showFiles() {
    val files = File(fileNameIndex).readLines()
    if (files.isEmpty()) {
        println("Add a file to the index.")
    } else {
        println("Tracked files:")
        files.forEach { println(it) }
    }
}

fun showConfig(name: String? = null): String {
    // показывает конфиг
    return if (name != null) {
        File(fileNameConfig).writeText(name)
        "The username is $name."
    } else {
        val nameFromFile = File(fileNameConfig).readText()
        if (nameFromFile == "") "Please, tell me who you are." else "The username is $nameFromFile."
    }
}

fun createVCS() {
    val dir = File(directory)
    if (!dir.exists()) {
        dir.mkdir()
        File(fileNameConfig).createNewFile()
        File(fileNameIndex).createNewFile()
        File(fileNameLog).createNewFile()
        val comDir = File(commitsDirectory)
        comDir.mkdir()
    }
}

fun main(args: Array<String>) {
    createVCS()
    if (args.isEmpty()) {
        println(allCommands)
    } else when (args[0]) {
        "config" -> {
            println(if (args.size < 2) showConfig() else showConfig(args[1]))
        }
        "add" -> {
            if (args.size < 2) showFiles() else addFile(args[1])
        }
        "--help" -> println(allCommands)
        "log" -> log()
        "commit" -> if (args.size < 2) commit() else commit(args[1])
        "checkout" -> println(if (args.size < 2) checkout() else checkout(args[1]))
        else -> println("'${args[0]}' is not a SVCS command.")
    }
}