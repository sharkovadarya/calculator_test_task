package ru.hse.spb.practice.calculator

import org.antlr.v4.runtime.BufferedTokenStream
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.ConsoleErrorListener
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Test
import ru.hse.spb.practice.calculator.interpreter.InterpreterException

class LexerTest {
    private val incorrectSymbolAtPosition = "Incorrect symbol at position"

    @Test
    fun testIncorrectBinaryOperation() {
        doTestForError(
                "5 ^ 7",
                "$incorrectSymbolAtPosition 2.\n token recognition error at: '^'"
        )
    }

    @Test
    fun testIncorrectArgument() {
        doTestForError(
                "5 * текст",
                "$incorrectSymbolAtPosition 4.\n token recognition error at: 'т'"
        )
    }

    private fun doTestForError(input: String, errorMessage: String) {
        try {
            val lexer = CalculatorLexer(CharStreams.fromString(input))
            lexer.addErrorListener(calculatorExceptionListener)
            lexer.removeErrorListener(ConsoleErrorListener.INSTANCE)
            val parser = CalculatorParser(BufferedTokenStream(lexer))
            parser.query()
            fail()
        } catch (e: InterpreterException) {
            assertEquals(errorMessage, e.message)
        }
    }
}