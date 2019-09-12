package ru.hse.spb.practice.calculator

import org.antlr.v4.runtime.BufferedTokenStream
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.ConsoleErrorListener
import org.junit.Assert.*
import org.junit.Test
import ru.hse.spb.practice.calculator.interpreter.InterpreterException

class ParserTest {
    @Test
    fun testUnbalancedBraces() {
        doTestForError("4 * (5 + 7", "Incorrect symbol at position 10.\n missing ')'")
    }

    @Test
    fun testNonIdentifierVariableName() {
        doTestForError("5 * текст", "Incorrect symbol at position 4.")
    }

    @Test
    fun testIncorrectVariableDeclarationDirective() {
        doTestForError(
                "var x = 5 * 4",
                "Incorrect symbol at position 4.\n mismatched input 'x'"
        )
    }


    private fun doTestForError(input: String, errorMessage: String) {
        try {
            val lexer = CalculatorLexer(CharStreams.fromString(input))
            lexer.addErrorListener(calculatorExceptionListener)
            lexer.removeErrorListener(ConsoleErrorListener.INSTANCE)
            val parser = CalculatorParser(BufferedTokenStream(lexer))
            parser.addErrorListener(calculatorExceptionListener)
            parser.removeErrorListener(ConsoleErrorListener.INSTANCE)
            parser.query()

            // an exception must get thrown here; if not, fail
            fail()
        } catch (e: InterpreterException) {
            assertTrue(e.message!!.contains(errorMessage))
        }
    }
}