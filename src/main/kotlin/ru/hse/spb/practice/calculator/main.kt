package ru.hse.spb.practice.calculator

import org.antlr.v4.runtime.*
import ru.hse.spb.practice.calculator.interpreter.Interpreter
import ru.hse.spb.practice.calculator.interpreter.InterpreterException

private const val EXIT_COMMAND = "exit"

fun main() {
    println("This integer numbers calculator supports: +, -, *, /, %, braces, variable declarations")
    println("Input a query or type '$EXIT_COMMAND' to terminate the program")

    val interpreter = Interpreter()
    while (true) {
        val input = readLine()
        if (input == EXIT_COMMAND) {
            println("Program terminated.")
            return
        }
        input?.let {
            val lexer = CalculatorLexer(CharStreams.fromString(input))
            val parser = CalculatorParser(BufferedTokenStream(lexer))

            val exceptionListener = object : BaseErrorListener() {
                override fun syntaxError(
                    recognizer: Recognizer<*, *>?,
                    offendingSymbol: Any?,
                    line: Int,
                    charPositionInLine: Int,
                    msg: String?,
                    e: RecognitionException?
                ) {
                    super.syntaxError(recognizer, offendingSymbol, line, charPositionInLine, msg, e)
                    throw InterpreterException(
                        "Incorrect symbol on line $line at position $charPositionInLine.\n $msg"
                    )
                }
            }
            lexer.removeErrorListener(ConsoleErrorListener.INSTANCE)
            parser.removeErrorListener(ConsoleErrorListener.INSTANCE)
            lexer.addErrorListener(exceptionListener)
            parser.addErrorListener(exceptionListener)

            try {
                interpreter.evaluate(parser.query())?.let { println(it) }
            } catch (e: InterpreterException) {
                println(e.message)
            }
        }
    }
}