import com.fazecast.jSerialComm.SerialPort
import com.fazecast.jSerialComm.SerialPort.*
import robotron.Robotron
import java.io.File
import java.lang.Thread.sleep


/*
To get CTS flow control install correct PL2303HXD_G_Driver_v2_0_0_20191204 driver
screen /dev/tty.usbserial cs8,-parenb,-cstopb,-clocal,echo,speed 9600,crtscts; reset;
 */


fun main() {
    val out = File("out.hpgl")
//    val dev = System.out
    val dev = "/dev/tty.usbserial"

    val robotron = Robotron.robotron(dev) {
        //        testText()

//        penMove(420.0, 420.0)

        out.useLines { lines ->
            lines.forEach {
                println(it)
                tx.appendln(it)
                tx.flush()
            }
        }
//        ellipses()

        home()
        identification()
        println("identification(): " + tty?.inputStream?.readBytes())
        reset()
    }

    printFile("/dev/tty.usbserial", out)
    println("hello")
}

private fun Robotron.ellipses() {
    val w = 100;
    for (h in 200..400 step 22) {
        ellipse(420.0f, 1200.0f, h.toFloat(), h.toFloat())
    }
    for (h in 200..400 step 22) {
        ellipse(520.0f, 1300.0f, h.toFloat(), h.toFloat())
    }
}

private fun Robotron.testText() {
    penMove(42.0f, 1100.0f)
    textDirectionY()
    textLabel(
        "HP-GL, short for Hewlett-Packard Graphics Language and often written as HPGL,\n" +
                "is a printer control language created by Hewlett-Packard (HP). HP-GL was the\n" +
                "primary printer control language used by HP plotters.^[1] It was introduced\n" +
                "with the plotter HP-8972 in 1977 and became a standard for almost all plotters.\n" +
                "Hewlett-Packard's printers also usually support HP-GL/2 in addition to PCL."
    )
}


private fun printFile(devFile: String, hpglFile: File) {
    serial8N1(devFile) { tty ->
        if (!tty.cts) {
            println("no CTS")
            return@serial8N1
        }

        tty.outputStream.use { outputStream ->
            val out = outputStream.writer(Charsets.US_ASCII)

            hpglFile.useLines { lines ->
                lines.forEach {
                    out.write(it)
                }
            }
        }
    }
}


fun serial8N1(dev: String, use: (SerialPort) -> Unit) {
    val stty = serial8N1(dev)
    try {
        use(stty)
    } catch (e: Throwable) {
        e.printStackTrace()
    } finally {
        stty.closePort()
    }
}

fun serial8N1(dev: String): SerialPort {
    val stty = getCommPort(dev)
    stty.setComPortParameters(9600, 8, ONE_STOP_BIT, NO_PARITY)
    stty.setFlowControl(FLOW_CONTROL_CTS_ENABLED)
    stty.setComPortTimeouts(TIMEOUT_READ_SEMI_BLOCKING, 60_000, 5000)
//    stty.setComPortTimeouts(TIMEOUT_READ_SEMI_BLOCKING, 100, 0)
//    stty.setComPortTimeouts(, 100, 100)
    stty.openPort(200, 512, 512)
//    stty.openPort()
    for (i in 1..10) {
        println("$i try. CTS: ${stty.cts}")
        if (stty.cts) {
            break
        }
        sleep(1000)
    }
    return stty
}
