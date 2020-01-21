import processing.core.PApplet
import processing.core.PConstants
import robotron.RobotronPGraphicsRecorder
import java.io.File
import java.io.OutputStreamWriter


class PlotterApp(val out: OutputStreamWriter) : PApplet() {
    private lateinit var plotter: RobotronPGraphicsRecorder

    companion object {
        fun run(args: Array<String>, out: OutputStreamWriter) {
            val app = PlotterApp(out)

            app.setSize(3700 / 3, 2700 / 3)
            app.runSketch(args)
        }
    }

    override fun setup() {
        plotter = createGraphics(width, height, RobotronPGraphicsRecorder.NAME) as RobotronPGraphicsRecorder
        plotter.setPath("/tmp/robotron.hpgl")
        plotter.setWriter(out)

        plotter.beginDraw()
        beginRecord(plotter)
    }

    override fun draw() {
        noLoop()
        noFill()
        stroke(0)
//        background(255)

        val w = 200f
        val h = 200f
        for (nx in 0..18) {
            for (ny in 0..13) {
                pushMatrix()
                translate(nx * w, ny * h)
                scale(0.9f)

                quad(
                    0f, 0f,
                    0f, h,
                    w, h, w, 0f
                )

//                translate(width / 2f, height / 2f)
//                rotate(45f)

                val padding = 10
//                scale(0.4f)
                popMatrix()
            }
        }
//        hLines()
//        test1()
//        gridEllipses()
//        drawVertexCircles()
//        ellipse(1.0f, 1.0f, 10.0f, 10.0f)
//
//        val radi = 100.0f
//        val numPoints = 100
//        val rot = TWO_PI / numPoints
//        beginShape(PConstants.POLYGON)
//        for (i in 0..numPoints) {
//            val theta = i * rot - HALF_PI
////            vertex(cos(theta) * radi, sin(theta) * radi)
//            vertex(
//                random(0f, width.toFloat()),
//                random(0f, height.toFloat())
//            )
//        }
//        endShape()
//        scale(0.5f)
    }

    private fun hLines() {
        val timeSeed = 24f
        for (j in -100..height * 2 step 5) {
            beginShape();
            val yy = j + (noise(0f, j / 300f, timeSeed) * 1000.0) - 500.0
            vertex(0f, yy.toFloat()); //reset the initial point at the start of each new line
            for (i in 10..width + 40 step 40) { //iterate across the page
                val y = j + (noise(i / 300.0f, j / 300.0f, timeSeed) * 1000.0) - 500.0f
                vertex(i.toFloat(), y.toFloat()); //set the next point in the line
            }
            endShape();
        }
    }

    private fun drawVertexCircles() {
        val diameter = 450f
        val radius = diameter / 2f
        background(255)
        noFill()
//        translate(width / 2.toFloat(), height / 2.toFloat())
        //        strokeWeight(1.5f)

        fun drawCircle(numPoints: Int, radi: Float) {
            val rot = TWO_PI / numPoints
            beginShape(PConstants.POLYGON)
            for (i in 0..numPoints + 1) {
                val theta = i * rot - HALF_PI;
                vertex(cos(theta) * radi, sin(theta) * radi);
            }
            endShape()
        }

        for (i in 3..11) {
            drawCircle(i, radius - i * 10)
        }
        strokeWeight(3f)
        ellipse(0f, 0f, diameter, diameter)
    }

    private fun gridEllipses() {
        (20..420 step 42).forEach { x ->
            (20..290 step 29).forEach { y ->
                //                circle(x.toFloat(), y.toFloat(), 20.toFloat())
                ellipse(
                    x.toFloat(), y.toFloat(),
                    random(1f, 42f), random(1f, 42f)
                )

            }
        }
    }

    private fun test1() {
        (1..100).forEach { _ ->
            ellipse(
                random(0f, 800f), random(0f, 500f),
                random(1f, 60f), random(1f, 60f)
            )
            line(
                random(0f, 1900f), random(0f, 1900f),
                random(0f, 1900f), random(0f, 1900f)
            )
        }
    }

}

fun main(args: Array<String>) {
    val streamWriter = File("out.hpgl").writer(Charsets.US_ASCII)
//    val streamWriter = System.out.writer(Charsets.US_ASCII)
//    val tty = serial8N1("/dev/tty.usbserial")
//    val streamWriter = tty.outputStream.writer(Charsets.US_ASCII)
    PlotterApp.run(args, streamWriter)
//    tty.closePort()
}
