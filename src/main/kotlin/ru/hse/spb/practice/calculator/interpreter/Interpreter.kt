package ru.hse.spb.practice.calculator.interpreter

import ru.hse.spb.practice.calculator.CalculatorBaseVisitor
import ru.hse.spb.practice.calculator.CalculatorParser

class Interpreter : CalculatorBaseVisitor<Long>() {
    private val variables: MutableMap<String, Long> = mutableMapOf()

    fun evaluate(ctx: CalculatorParser.QueryContext): Long? {
        return ctx.accept(this)
    }

    override fun visitQuery(ctx: CalculatorParser.QueryContext): Long? {
        return ctx.statement().accept(this)
    }

    override fun visitVariableDeclaration(ctx: CalculatorParser.VariableDeclarationContext): Long? {
        val name = ctx.name.text
        val value = ctx.expression().accept(this)
        variables[name] = value
        return null
    }

    override fun visitAdditionExpr(ctx: CalculatorParser.AdditionExprContext): Long {
        val left = ctx.left.accept(this)
        val right = ctx.right.accept(this)

        return when (ctx.op.text) {
            "+" -> left + right
            "-" -> left - right
            else -> throw InterpreterException("A parsing error occurred while processing.")
        }
    }

    override fun visitMultiplicationExpr(ctx: CalculatorParser.MultiplicationExprContext): Long {
        val left = ctx.left.accept(this)
        val right = ctx.right.accept(this)

        return when (ctx.op.text) {
            "*" -> left * right
            "/" -> if (right != 0L) left / right else throw InterpreterException("Can't perform division by 0.")
            "%" -> if (right != 0L) left % right else throw InterpreterException("Can't perform division by 0.")
            else -> throw InterpreterException("A parsing error occurred while processing.")
        }
    }

    override fun visitLiteralExpr(ctx: CalculatorParser.LiteralExprContext): Long {
        val number = ctx.LITERAL().text
        return try {
            number.toLong()
        } catch (e: NumberFormatException) {
            throw InterpreterException("Unable to handle a number this big: $number.")
        }
    }

    override fun visitIdentifierExpr(ctx: CalculatorParser.IdentifierExprContext): Long {
        val name = ctx.IDENTIFIER().text
        return variables[name] ?: throw InterpreterException("Undefined variable '$name'.")
    }

    override fun visitParenthesisedExpr(ctx: CalculatorParser.ParenthesisedExprContext): Long {
        return ctx.expression().accept(this)
    }

    override fun visitUnaryExpr(ctx: CalculatorParser.UnaryExprContext): Long {
        return when (ctx.op.text) {
            "+" -> ctx.expression().accept(this)
            "-" -> -ctx.expression().accept(this)
            else -> throw InterpreterException("A parsing error occurred while processing.")

        }
    }
}