package robotron

import processing.core.PConstants
import processing.core.PGraphics
import processing.core.PImage
import processing.core.PMatrix2D
import java.io.OutputStreamWriter
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

class RobotronPGraphicsRecorder : PGraphics {
    private var transformCount = 0
    private val transformStack = arrayOfNulls<PMatrix2D>(MATRIX_STACK_DEPTH)
    private var transformMatrix: PMatrix2D = PMatrix2D()

    companion object {
        const val NAME = "robotron.RobotronPGraphicsRecorder"
    }

    private var writer: OutputStreamWriter

    constructor() {
        this.writer = System.out.writer(Charsets.US_ASCII)
    }

    constructor(writer: OutputStreamWriter) {
        this.writer = writer
    }

    fun setWriter(writer: OutputStreamWriter) {
        this.writer = writer
    }

    override fun endDraw() {
        writer.flush()
    }
//
//    override fun backgroundImpl(image: PImage?) {
//
//    }
//
//    override fun backgroundImpl() {
//    }

    override fun point(x: Float, y: Float) {
        val xy = affineXY(x, y)
        listOf(penMoveAbsolute(xy[0], xy[1]), PD(), PU()).plot()
    }

    private fun String.plot() {
        println("CMD: $this")
        writer.appendln(this)
        writer.flush()
    }

    private fun <String> List<String>.plot() {
        this.joinToString("").plot()
    }

    private fun <String> Array<String>.plot() {
        this.joinToString("").plot()
    }

    override fun line(x1: Float, y1: Float, x2: Float, y2: Float) {
        val xy1 = affineXY(x1, y1)
        val xy2 = affineXY(x2, y2)
        robotron.line(xy1[X], xy1[Y], xy2[X], xy2[Y]).plot()
    }

    override fun ellipseImpl(x: Float, y: Float, w: Float, h: Float) {
        val xy = affineXY(x, y)
        val wh = affineXY(w, h)
        val ca = 5.0
        if (abs(wh[X] - wh[Y]) < 0.1) { // draw a circle
            circle(xy[X], xy[Y], wh[X] / 2).plot()
        } else { // draw an ellipse
            val initx: Float = (xy[0] + wh[X] / 2.0 * cos(0.0)).toFloat()
            val inity = (xy[1] + wh[Y] / 2.0 * sin(0.0)).toFloat()
            var _x: Float
            var _y: Float

            val commands = mutableListOf<String>()
            commands.add(penMoveAbsolute(initx, inity))

            var t = ca
            while (t < 360.0) {
                _x = (xy[X] + wh[X] / 2.0 * cos(Math.toRadians(t))).toFloat()
                _y = (xy[Y] + wh[Y] / 2.0 * sin(Math.toRadians(t))).toFloat()
                if (abs(_x) < 0.01) _x = 0.01f
                if (abs(_y) < 0.01) _y = 0.01f

                commands.add(PD())
                commands.add(PA(_x, _y))

                t += ca
            }

            commands.add(PD())
            commands.add(PA(initx, inity))
            commands.add(PU())

            commands.plot()
        }
    }

    override fun beginShape(kind: Int) {
        vertices = Array(
            DEFAULT_VERTICES
        ) { FloatArray(VERTEX_FIELD_COUNT) }
        shape = kind
        vertexCount = 0
    }

    override fun endShape(mode: Int) {
        val xy0 = affineXY(this.vertices[0][PConstants.X], this.vertices[0][PConstants.Y])

        penMoveAbsolute(xy0[X], xy0[Y]).plot()
        PD().plot()

        for (n in 1..vertexCount) {
            val xy = affineXY(this.vertices[n][PConstants.X], this.vertices[n][PConstants.Y])
            PA(xy[X], xy[Y]).plot()
        }

        if (mode == CLOSE) {
            PA(xy0[X], xy0[Y]).plot()
        }
    }


    // MATRIX STACK
    private fun affineXY(x: Float, y: Float): FloatArray {
        return transformMatrix.mult(floatArrayOf(x, y), null)
    }

    override fun pushMatrix() {
        if (transformCount == transformStack.size) {
            throw RuntimeException("pushMatrix() overflow")
        }
        transformStack[transformCount] = this.transformMatrix.get()
        transformCount++
    }

    override fun popMatrix() { //System.out.println("popMatrix");
        if (transformCount == 0) {
            throw RuntimeException("HPGL: matrix stack underrun")
        }
        transformCount--
        this.transformMatrix = PMatrix2D()
        for (i in 0..transformCount) {
            this.transformMatrix.apply(transformStack[i])
        }
    }

    override fun resetMatrix() {
        this.transformMatrix = PMatrix2D()
    }

    override fun translate(x: Float, y: Float) {
        this.transformMatrix.translate(x, y)
    }

    override fun scale(s: Float) {
        this.transformMatrix.scale(s, s)
    }

    override fun scale(x: Float, y: Float) {
        this.transformMatrix.scale(x, y)
    }

    override fun rotate(angle: Float) {
        this.transformMatrix.rotate(angle)
    }


    override fun dispose() {
        writer.flush()
        writer.close()
    }

}



