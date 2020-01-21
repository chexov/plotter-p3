package robotron

import com.fazecast.jSerialComm.SerialPort
import processing.core.PGraphics
import serial8N1
import java.io.File
import java.io.OutputStream
import java.io.OutputStreamWriter
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

class Robotron(val tx: OutputStreamWriter, val tty: SerialPort?) {

    companion object {
        fun robotron(dev: String, init: Robotron.() -> Unit): Robotron {
            val tty = serial8N1(dev)
            tty.clearBreak()
            if (!tty.cts) {
                throw RuntimeException("CTS not ready")
            }

//            val charset = Charset.forName(charset)
            val charset = Charsets.US_ASCII
            val robotron = Robotron(tty.outputStream.writer(charset), tty)
            try {
                robotron.penUp()
                robotron.init()
                robotron.flush()
            } finally {
                tty.closePort()
            }
            return robotron
        }

        fun robotron(hpgl: File, init: Robotron.() -> Unit): Robotron {
            val robotron = Robotron(hpgl.writer(Charsets.US_ASCII), null)
            robotron.init()
            robotron.flush()
            return robotron
        }

        fun robotron(writer: OutputStreamWriter, init: Robotron.() -> Unit): Robotron {
            val robotron = Robotron(writer, null)
            robotron.init()
            robotron.flush()
            return robotron
        }

        fun robotron(printStream: OutputStream, init: Robotron.() -> Unit): Robotron {
            val robotron = Robotron(printStream.writer(Charsets.US_ASCII), null)
            robotron.init()
            robotron.flush()
            return robotron
        }

        fun robotron(init: Robotron.() -> Unit): Robotron {
            val robotron = Robotron(System.out.writer(Charsets.US_ASCII), null)
            robotron.init()
            robotron.flush()
            return robotron
        }

    }

    fun penAbsolute(x: Float, y: Float) {
        PA(x, y).tx()
    }

    fun penDown() {
        PD().tx()
    }

    fun textLabel(label: String) {
        label(label).tx()
    }

    fun flush() {
        tx.flush()
    }

    fun penUp() {
        PU().tx()
    }

    fun reset() {
        IN().tx()
    }

    fun defaults() {
        ";;;\n".tx()
        DF().tx()
    }

    fun penMove(x: Float, y: Float) {
        PA(x, y).tx()
    }

    fun textDirectionY() {
        DI(0, 1).tx()
    }

    fun textDirectionX() {
        DI(1, 0).tx()
    }

    fun identification() {
        OI().tx()
    }

    fun home() {
        penUp()
        penMove(MAX_X, MAX_Y)
    }

    private fun String.tx() {
        tx.appendln(this)
        tx.flush()
    }

    private fun affineXY(x: Float, y: Float): FloatArray {
        return floatArrayOf(x, y)
    }

    fun ellipse(x: Float, y: Float, w: Float, h: Float) {
        val xy = affineXY(x, y)
        val wh = affineXY(w, h)
        val ca = 5.0
        if (abs(wh[PGraphics.X] - wh[PGraphics.Y]) < 0.1) { // draw a circle
            circle(xy[PGraphics.X], xy[PGraphics.Y], wh[PGraphics.X] / 2).tx()
        } else { // draw an ellipse
            val initx: Float = (xy[0] + wh[PGraphics.X] / 2.0 * cos(0.0)).toFloat()
            val inity = (xy[1] + wh[PGraphics.Y] / 2.0 * sin(0.0)).toFloat()
            var _x: Float
            var _y: Float

            val commands = mutableListOf<String>()
            commands.add(penMoveAbsolute(initx, inity))

            var t = ca
            while (t < 360.0) {
                _x = (xy[PGraphics.X] + wh[PGraphics.X] / 2.0 * cos(Math.toRadians(t))).toFloat()
                _y = (xy[PGraphics.Y] + wh[PGraphics.Y] / 2.0 * sin(Math.toRadians(t))).toFloat()
                if (abs(_x) < 0.01) _x = 0.01f
                if (abs(_y) < 0.01) _y = 0.01f

                commands.add(PD())
                commands.add(PA(_x, _y))

                t += ca
            }

            commands.add(PD())
            commands.add(PA(initx, inity))
            commands.add(PU())

            commands.joinToString().tx()
        }
    }

}
