package ru.hse.spb.practice.calculator.interpreter

import ru.hse.spb.practice.calculator.CalculatorBaseVisitor
import ru.hse.spb.practice.calculator.CalculatorParser

class Interpreter : CalculatorBaseVisitor<Int>() {
    private val variables: MutableMap<String, Int> = mutableMapOf()

    fun evaluate(ctx: CalculatorParser.QueryContext): Int? {
        return ctx.accept(this)
    }

    override fun visitQuery(ctx: CalculatorParser.QueryContext): Int? {
        return ctx.statement().accept(this)
    }

    override fun visitVariableDeclaration(ctx: CalculatorParser.VariableDeclarationContext): Int? {
        val name = ctx.name.text
        val value = ctx.expression().accept(this)
        variables[name] = value
        return null
    }

    override fun visitAdditionExpr(ctx: CalculatorParser.AdditionExprContext): Int {
        val left = ctx.left.accept(this)
        val right = ctx.right.accept(this)

        return when (ctx.op.text) {
            "+" -> left + right
            "-" -> left - right
            else -> throw InterpreterException("U")
        }
    }

    override fun visitMultiplicationExpr(ctx: CalculatorParser.MultiplicationExprContext): Int {
        val left = ctx.left.accept(this)
        val right = ctx.right.accept(this)

        return when (ctx.op.text) {
            "*" -> left * right
            "/" -> if (right != 0) left / right else throw InterpreterException("Can't perform division by 0.")
            "%" -> if (right != 0) left % right else throw InterpreterException("Can't perform division by 0.")
            else -> throw Exception()
        }
    }

    override fun visitLiteralExpr(ctx: CalculatorParser.LiteralExprContext): Int {
        return ctx.LITERAL().text.toInt()
    }

    override fun visitIdentifierExpr(ctx: CalculatorParser.IdentifierExprContext): Int {
        val name = ctx.IDENTIFIER().text
        return variables[name] ?: throw InterpreterException("Undefined variable $name.")
    }

    override fun visitParenthesisedExpr(ctx: CalculatorParser.ParenthesisedExprContext): Int {
        return ctx.expression().accept(this)
    }

    override fun visitUnaryExpr(ctx: CalculatorParser.UnaryExprContext): Int {
        return when (ctx.op.text) {
            "+" -> ctx.expression().accept(this)
            "-" -> -ctx.expression().accept(this)
            else -> throw Exception()

        }
    }
}