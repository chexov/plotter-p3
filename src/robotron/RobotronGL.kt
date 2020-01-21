package robotron

/*
Robotron REISS K6418 Spec
A3: 297mm x420mm
Work space:
  X=370mm, Y=270mm
step:0.1mm
feed:240mm/sec; 120mm/sec
buffer: 512b
 */

const val MAX_X = 3700.0f
const val MAX_Y = 2700.0f

/**
 * Pen Absolute
 *
 * PA x,y,...,xn,yn;
 */
fun PA(vararg xy: Float): String {
    return "PA " + xy.map { String.format("%.1f", it) }.joinToString(separator = ",") + ";"
}

/**
 * Pen UP
 *
 * PU;
 */
fun PU(): String {
    return "PU;"
}

/**
 * Pen down
 *
 * PD;
 */
fun PD(): String {
    return "PD;"
}

/**
 * Line Type
 *
 * LT number(1,2,3,4,5,6),length
 *
 * 1 -------
 *
 * 2 - - - -
 *
 * 3 -- - -- - --
 */
fun LT(number: Int, length: Float): String {
    return String.format("LT %d,%.1f;", number, length)
}

fun SM(char: Char) {}
fun TL(tp: Int, tl: Int) {}
fun XT() {}
fun YT() {}

/**
 * Char plot
 */
fun CP(space: Int, lines: Int): String {
    return String.format("CP %d,%d;", space, lines)
}

/**
 * Direction Instruction
 */
fun DR(runX: Int, riseY: Int): String {
    return String.format("DR %d %d;", runX, riseY)
}

fun DI(runX: Int, riseY: Int): String {
    return String.format("DI %d %d;", runX, riseY)
}

/**
 * Acr absolute
 */
fun AA(x: Float, y: Float, phi: Float): String {
    return String.format("AA %.1f,%.1f,%.1f", x, y, phi)
}

/**
 * Acr relative
 */
fun AR(x: Float, y: Float, phi: Float): String {
    return String.format("AR %.1f,%.1f,%.1f", x, y, phi)
}

fun CI(radius: Float): String {
    return String.format("CI %.1f;", radius)
}

/**
 * Char size
 * char width, char height
 */
fun SR(width: Int, height: Int): String {
    return String.format("SR %.1f,%.1f;", width, height)
}

/**
 * Char size
 */
fun SI(width: Int, height: Int): String {
    return String.format("SI %.1f,%.1f;", width, height)
}

/**
 * Initialize, Reset;
 */
fun IN(): String {
    return "IN;"
}

/**
 * Defaults;
 * step width = 0.1mm
 *
 */
fun DF(): String {
    return "DF;"
}

/**
 * Input Window
 */
fun IW(x1: Float, y1: Float, x2: Float, y2: Float): String {
    return String.format("IW %.1f,%.1f,%.1f,%.1f;", x1, y1, x2, y2)
}

/**
 * Max velocity
 * n=1  120mm/sec
 * n=2  240mm/sec
 */
fun VS(n: Int): String {
    return String.format("VS %d;", n)
}

/**
 * Step Width
 * set step width to 0.025mm;
 * After set all length params are dividing to 4.
 * DF; or IN; will reset this
 */
fun SW(): String {
    return "SW;"
}

/**
 * Rotate axis
 * x' = x cos - y sin
 * y' = x sin + y cos
 */
fun RO(alpha: Float): String {
    return String.format("RO %.1f;", alpha)
}

/**
 * Sets velocity to 60mm/sec
 */
fun SS(): String {
    return "SS;"
}

/**
 * Not ready.
 * Puts Robotron in offline mode. Ends plotting.
 */
fun NR(): String {
    return "NR;"
}

/**
 * Output identification
 * commands showing firmware version
 */
fun OI(): String {
    return "OI;"
}

/**
 * Must be terminated with 0x03 ETX
 */
fun LB_ETX(): Char {
    val etx = 0x03
    return etx.toChar()
}

fun LB(label: String): String {
    return "LB $label"
}

fun penMoveAbsolute(x: Float, y: Float): String {
    return listOf(
        PU(), PA(x, y)
    ).joinToString("")
}

fun line(x1: Float, y1: Float, x2: Float, y2: Float): String {
    return listOf(
        penMoveAbsolute(x1, y1), PD(), PA(x2, y2), PU()
    ).joinToString("")
}

fun circle(x: Float, y: Float, radius: Float): String {
    return listOf(
        penMoveAbsolute(x, y), CI(radius)
    ).joinToString("")
}

fun label(label: String): String {
    return listOf(
        LB(label), LB_ETX()
    ).joinToString("")
}
