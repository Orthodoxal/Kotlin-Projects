package calculator

import java.math.BigInteger
import java.util.*
import kotlin.math.pow
import kotlin.system.exitProcess

val variablesMap: MutableMap<String, BigInteger> = mutableMapOf()
val operands = mutableMapOf("^" to 3, "*" to 2, "/" to 2, "+" to 1, "-" to 1, "(" to 0, ")" to 0)

fun calculateToPostfix(postfix: MutableList<String>): BigInteger {
    // вычисление постфиксного выражения
    val stackResult = Stack<BigInteger>()
    for (token in postfix) {
        try {
            val number = if (variablesMap.contains(token)) variablesMap[token] else token.toBigInteger()
            stackResult.add(number)
        } catch (e: Exception) {
            stackResult.add(
                try {
                    val number1 = stackResult.pop()
                    val number2 = if (stackResult.empty()) null else stackResult.pop()
                    when (token) {
                        "^" -> number2?.toDouble()?.pow(number1.toInt())?.toInt()?.toBigInteger()
                        "*" -> number2?.times(number1)
                        "/" -> number2?.div(number1)
                        "+" -> number2?.plus(number1)
                        "-" -> if (number2 != null) number2 - number1 else -number1
                        else -> throw Exception()
                    }
                } catch (e: Exception) {
                    if (postfix.size == 1) {
                        throw Exception("Unknown variable")
                    }
                    throw Exception("Invalid expression")
                }
            )
        }
    }
    return stackResult.pop()
}

fun infixToPostfix(infix: MutableList<String>): MutableList<String> {
    // перевод из инфиксной формы в постфиксную
    val stack = Stack<String>()
    val postfixList = mutableListOf<String>()
    for (i in infix) {
        if (!operands.contains(i)) {
            postfixList.add(i)
        } else {
            if (i == "(") {
                stack.add(i)
            } else if (i == ")") {
                var good = false
                while (!stack.empty()) {
                    val top = stack.pop()
                    if (top == "(") {
                        good = true
                        break
                    } else postfixList.add(top)
                }
                if (!good) {
                    throw Exception("Invalid expression")
                }
            } else {
                while (!stack.empty() && operands[stack.peek()]!! >= operands[i]!!) {
                    postfixList.add(stack.pop())
                }
                stack.add(i)
            }
        }
    }
    while (!stack.empty()) {
        postfixList.add(stack.pop())
    }
    return postfixList
}

fun stringToInfix(input: String): MutableList<String> {
    // перевод выражения в инфиксную форму
    val inputReplace = input.replace(" ", "")
    val mutableList = mutableListOf<String>()
    var temp = ""
    for (char in inputReplace) {
        if (char.isLetterOrDigit()) {
            temp += char
        } else {
            if (temp == "") {
                if (operands.contains(char.toString())) {
                    if (char == '(' || char == ')' || mutableList.last() == ")") mutableList.add(char.toString())
                    else if (mutableList.last() == char.toString()) {
                        if (char == '+' || char == '-') {
                            if (char == '-') {
                                mutableList[mutableList.lastIndex] = "+"
                            }
                        } else {
                            throw Exception("Invalid expression")
                        }
                    } else if (mutableList.last() == "+" && char == '-') {
                        mutableList[mutableList.lastIndex] = "-"
                    }
                    continue
                }
                throw Exception("Invalid expression")
            } else if (variablesMap.contains(temp)) {
                temp = variablesMap[temp].toString()
            } else {
                try {
                    temp.toBigInteger()
                } catch (e: Exception) {
                    throw Exception("Invalid expression")
                }
            }
            mutableList.add(temp)
            mutableList.add(char.toString())
            temp = ""
        }
    }
    if (temp != "") mutableList.add(temp)
    return mutableList
}

fun calculateSum(input: String): String? {
    return try {
        // передать входное выражение
        // получить инфиксную форму
        val infixList = stringToInfix(input)
        // передать инфиксную форму
        // получить постфиксную форму
        val postfixList = infixToPostfix(infixList)
        // передать постфиксную форму
        // получить и отправить результат
        return calculateToPostfix(postfixList).toString()
    } catch (e: Exception) {
        e.message
    }
}

fun addVariable(input: String): String? {
    val variable: String
    val value: BigInteger
    try {
        var tempExpression = 0
        input.filter {
            if (it == '=') {
                tempExpression++
                true
            } else false
        }
        if (tempExpression != 1) throw Exception("Invalid assignment")
        val expression = input.replace(" ", "")
        val tempVariable = expression.substringBefore('=')
        val tempValue = expression.substringAfter('=')

        val variableRegex = Regex("^[a-zA-Z]+\$")
        if (!variableRegex.matches(tempVariable)) {
            throw Exception("Invalid identifier")
        } else {
            variable = tempVariable
        }
        value = try {
            tempValue.toBigInteger()
        } catch (e: Exception) {
            if (variablesMap.contains(tempValue)) {
                variablesMap.getValue(tempValue)
            } else {
                if (tempValue.contains(Regex("[0-9]"))) {
                    throw Exception("Invalid assignment")
                } else {
                    throw Exception("Unknown variable")
                }
            }
        }
    } catch (e: Exception) {
        return e.message
    }
    variablesMap[variable] = value
    return null
}

fun calculator() {
    do {
        val input = readln()
        val commandRegex = "/.*".toRegex()
        println(
            if (commandRegex.matches(input)) {
                //command
                when (input) {
                    "/exit" -> {
                        break
                    }
                    "/help" -> "The program calculates the sum of numbers"
                    else -> "Unknown command"
                }
            } else if (input.contains('=')) {
                addVariable(input) ?: continue
            } else {
                calculateSum(input) ?: continue
            }
        )
    } while (true)
}

fun main() {
    calculator()
    println("Bye!")
    exitProcess(1)
}
