package ru.hse.spb.practice.calculator

import org.antlr.v4.runtime.BufferedTokenStream
import org.antlr.v4.runtime.CharStreams
import org.junit.Assert.assertEquals
import org.junit.Test
import ru.hse.spb.practice.calculator.interpreter.Interpreter
import ru.hse.spb.practice.calculator.interpreter.InterpreterException

class InterpreterTest {
    private val interpreter = Interpreter()

    // correct cases tests

    @Test
    fun testLiteralExpression() {
        doTest("42", 42)
        doTest("0", 0)
    }

    @Test
    fun testUnaryExpression() {
        doTest("+42", 42)
        doTest("-42", -42)
        doTest("+0", 0)
        doTest("-0", 0)
    }

    @Test
    fun testAdditionExpression() {
        doTest("5 + 7", 12)
        doTest("5 - 7", -2)
        doTest("5 - 5", 0)
        doTest("5 - 0", 5)
        doTest("0 - 7", -7)
    }

    @Test
    fun testMultiplicationExpression() {
        doTest("5 * 7", 35)
        doTest("5 * 0", 0)
        doTest("35 / 7", 5)
        doTest("0 / 7", 0)
        doTest("7 % 5", 2)
        doTest("0 % 5", 0)
    }

    @Test
    fun testParenthesisedExpression() {
        doTest("(5 + 7)", 12)
        doTest("10 * (5 + 7)", 120)
    }

    @Test
    fun testExpressionWithNestedParentheses() {
        doTest("((3 + 6) * 7 + 20 / (9 - 5) - (20 - (12 + 8) / (2 * (2)))", 53)
    }

    @Test
    fun testVariableDeclaration() {
        doTest("let x = 2 * 7", null)
        doTest("x + 1", 15)
    }

    @Test
    fun testVariableRedeclaration() {
        doTest("let x = 2 * 7", null)
        doTest("let x = x / 5", null)
        doTest("x", 2)
    }

    @Test
    fun testLongNumbers() {
        doTest("4611686018427387901 + 4611686018427387902", 9223372036854775803)
    }

    @Test(expected = InterpreterException::class)
    fun testOverlyLongNumbers() {
        doTest("9223372036854775808", null)
    }

    // incorrect cases tests

    @Test(expected = InterpreterException::class)
    fun testDivideByZero() {
        doTest("15 / ((3 * 6 - 3) - (2 * 6 + 3))", null)
    }

    @Test(expected = InterpreterException::class)
    fun testGetReminderDivisionByZero() {
        doTest("15 % ((3 * 6 - 3) - (2 * 6 + 3))", null)
    }

    @Test(expected = InterpreterException::class)
    fun testUndefinedVariable() {
        doTest("undefined_variable / 15", null)
    }

    @Test
    fun testAdditionExpressionsWithNegativeNumbers() {
        doTest("-30-40", -70)
        doTest("(-30)-40", -70)
        doTest("-30-(-40)", 10)
    }

    private fun getContextForString(s: String): CalculatorParser.QueryContext {
        val lexer = CalculatorLexer(CharStreams.fromString(s))
        val parser = CalculatorParser(BufferedTokenStream(lexer))
        return parser.query()
    }

    private fun doTest(s: String, result: Long?) {
        assertEquals(result, interpreter.evaluate(getContextForString(s)))
    }
}