package com.example.calculator

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity

class MainActivity : ComponentActivity() {
    private lateinit var screen: TextView
    private var current = "0"
    private var pendingOp: Char? = null
    private var accumulator: Double? = null
    private var lastInputWasOp = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        screen = findViewById(R.id.screen)

        fun digit(b: Int) { pushDigit(b.toString()) }
        fun op(c: Char) { pushOp(c) }

        mapOf(
            R.id.btn0 to { digit(0) }, R.id.btn1 to { digit(1) }, R.id.btn2 to { digit(2) },
            R.id.btn3 to { digit(3) }, R.id.btn4 to { digit(4) }, R.id.btn5 to { digit(5) },
            R.id.btn6 to { digit(6) }, R.id.btn7 to { digit(7) }, R.id.btn8 to { digit(8) },
            R.id.btn9 to { digit(9) },
            R.id.btnDot to { if (!current.contains('.')) append(".") },
            R.id.btnAdd to { op('+') },
            R.id.btnSub to { op('-') },
            R.id.btnMul to { op('*') },
            R.id.btnDiv to { op('/') },
            R.id.btnEq  to { equalsPress() },
            R.id.btnC   to { clearAll() },
            R.id.btnDel to { backspace() }
        ).forEach { (id, action) ->
            findViewById<Button>(id).setOnClickListener { action() }
        }

        render()
    }

    private fun pushDigit(d: String) {
        if (current == "0" || lastInputWasOp) current = d else current += d
        lastInputWasOp = false
        render()
    }

    private fun append(s: String) {
        if (lastInputWasOp) current = "0"
        current += s
        lastInputWasOp = false
        render()
    }

    private fun pushOp(op: Char) {
        computePending()
        pendingOp = op
        lastInputWasOp = true
    }

    private fun equalsPress() {
        computePending()
        pendingOp = null
        lastInputWasOp = true
    }

    private fun computePending() {
        val x = current.toDoubleOrNull() ?: 0.0
        if (accumulator == null) {
            accumulator = x
        } else if (pendingOp != null) {
            accumulator = when (pendingOp) {
                '+' -> (accumulator ?: 0.0) + x
                '-' -> (accumulator ?: 0.0) - x
                '*' -> (accumulator ?: 0.0) * x
                '/' -> if (x == 0.0) Double.NaN else (accumulator ?: 0.0) / x
                else -> x
            }
        }
        current = trim(accumulator ?: x)
        render()
    }

    private fun backspace() {
        current = if (lastInputWasOp) current else if (current.length > 1) current.dropLast(1) else "0"
        render()
    }

    private fun clearAll() {
        current = "0"
        accumulator = null
        pendingOp = null
        lastInputWasOp = false
        render()
    }

    private fun trim(v: Double): String {
        val s = v.toString()
        return if (s.contains(".0")) s.trimEnd('0').trimEnd('.') else s
    }

    private fun render() {
        screen.text = current
    }
}