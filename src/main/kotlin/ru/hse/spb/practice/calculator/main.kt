package ru.hse.spb.practice.calculator

import org.antlr.v4.runtime.*
import ru.hse.spb.practice.calculator.interpreter.Interpreter
import ru.hse.spb.practice.calculator.interpreter.InterpreterException

private const val EXIT_COMMAND = "exit"

val calculatorExceptionListener = object : BaseErrorListener() {
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
                "Incorrect symbol at position $charPositionInLine.\n $msg"
        )
    }
}

fun main() {
    println("This integer numbers calculator supports: +, -, *, /, %, braces, variable declarations.")
    println("Input a query or type '$EXIT_COMMAND' to terminate the program.")

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

            lexer.removeErrorListener(ConsoleErrorListener.INSTANCE)
            parser.removeErrorListener(ConsoleErrorListener.INSTANCE)
            lexer.addErrorListener(calculatorExceptionListener)
            parser.addErrorListener(calculatorExceptionListener)

            try {
                interpreter.evaluate(parser.query())?.let { println(it) }
            } catch (e: InterpreterException) {
                println(e.message)
            }
        }
    }
}