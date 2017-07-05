package com.panzersoft.antiaircraft

import com.panzersoft.antiaircraft.orders.*
import java.util.concurrent.ThreadLocalRandom


/**
 * Created by mdozturk on 7/4/17.
 */
enum class Operands {
    NUM,
    AIM,
    WAIT,
    FIRE,
    RELOAD
}

class ExpressionTree {
    internal var top: StackNode? = null

    data class TreeNode(val op: Operands, val left: TreeNode?, val right: TreeNode?) {
        constructor(op: Operands) : this(op, null, null)
        constructor(op: Operands, l: TreeNode) : this(op, l, null)

        fun generateOrder() : Order {
            if (isNullaryOp(op)) {
                return when (op) {
                    Operands.FIRE -> FireOrder()
                    Operands.RELOAD -> ReloadOrder()
                    else -> {
                        throw IllegalArgumentException("Unsupported nullary operator")
                    }
                }
            } else if (isUnaryOp(op)) {
                if (left == null) {
                    throw InvalidExpressionException("The number associated with the unary op is not available")
                } else {
                    if (left.op != Operands.NUM) {
                        throw InvalidExpressionException("The op associated with the unary op must be a number")
                    }
                    return when (op) {
                        Operands.AIM -> AimOrder(NumOrder(ThreadLocalRandom.current().nextDouble(0.0, 180.0)))
                        Operands.WAIT -> WaitOrder(NumOrder(ThreadLocalRandom.current().nextDouble(0.0, 3.0)))
                        else -> {
                            throw IllegalArgumentException("Unsupported unary operator")
                        }
                    }
                }
            } else {
                throw InvalidExpressionException("Unsupported arity")
            }
        }
    }

    data class StackNode(val treeNode: TreeNode, val next: StackNode?) {
        constructor(treeNode: TreeNode) : this(treeNode, null)
    }

    class InvalidExpressionException(str: String): Exception(str)

    fun clear() { top = null }

    internal fun push(ptr: TreeNode) {
        if (top == null) {
            top = StackNode(ptr)
        } else {
            val nptr = StackNode(ptr, top)
            top = nptr
        }
    }

    internal fun pop() : TreeNode {
        val t = top
        if (t == null) {
            throw RuntimeException("Underflow")
        } else {
            val ptr = t.treeNode
            top = t.next
            return ptr
        }
    }

    internal fun peek(): TreeNode? {
        return top?.treeNode ?: null
    }

    internal fun insert(op: Operands) {
        try {
            if (op == Operands.NUM) {
                val tn = TreeNode(op)
                push(tn)
            } else if (isNullaryOp(op)) {
                val tn = TreeNode(op)
                push(tn)
            } else if (isUnaryOp(op)) {
                val tn = TreeNode(op, pop())
                push(tn)
            } else {
                throw NotImplementedError("need to handle binary ops")
            }
        }
        catch (exp: RuntimeException) {
            throw InvalidExpressionException("Failed to convert expression to expression tree")
        }
    }

    fun buildTree(expression: List<Operands>) {
        expression.forEach {
            insert(it)
        }
    }

    fun generateOrder() : Order {
        val treeNode: TreeNode = peek() ?: throw InvalidExpressionException("Failed when generating order")
        return treeNode.generateOrder()
    }
}

fun isNullaryOp(op: Operands) : Boolean {
    return when (op) {
        Operands.FIRE, Operands.RELOAD -> true
        else -> false
    }
}

fun isUnaryOp(op: Operands) : Boolean {
    return when (op) {
        Operands.AIM, Operands.WAIT -> true
        else -> false
    }
}

fun isNumberOp(op: Operands) : Boolean {
    return when (op) {
        Operands.NUM -> true
        else -> false
    }
}

fun isMutable(op: Operands) : Boolean {
    return isUnaryOp(op)
}