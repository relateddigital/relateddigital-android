package com.relateddigital.relateddigital_android_core.inapp.inappmessages

import android.animation.FloatEvaluator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.annotation.IntDef
import java.util.*
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

abstract class BaseRating : View {
    protected var SMILES_LIST = intArrayOf(TERRIBLE, BAD, OKAY, GOOD, GREAT)

    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    @IntDef(TERRIBLE, BAD, OKAY, GOOD, GREAT, NONE)
    annotation class Smiley

    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    @IntDef(POINT_1, POINT_2, CONTROL_POINT_1, CONTROL_POINT_2)
    annotation class Coordinate {}

    constructor(context: Context?) : super(context) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {}
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}

    protected open class Smileys private constructor(private val mWidth: Int, private val mHeight: Int) {
        private val mCenterY: Float = mHeight / 2f + mHeight / 5f
        protected var mCenterSmile: Float = mHeight / 2f
        private val mEyes: MutableMap<Int, Eye> = HashMap()
        private val mSmileys: MutableMap<Int, Smile> = HashMap()
        fun getSmile(@Smiley smiley: Int): Smile? {
            return mSmileys[smiley]
        }

        fun getEye(@Eye.EyeSide eye: Int): Eye {
            var e = mEyes[eye]
            if (e == null) {
                e = Eye()
                e.eyeSide = eye
                mEyes[eye] = e
            }
            return e
        }

        private fun createSmile(smileCenter: Point, curveControl1: Point?, curveControl2: Point?,
                                point1: Point?, point2: Point?, @Smile.Mode fillMode: Int,
                                @Smiley smile: Int, width: Float, angle: Float, length: Float) {
            if (Smile.MIRROR == fillMode) {
                createMirrorSmile(smileCenter,
                        curveControl1, curveControl2, point1, point2, smile)
            } else if (Smile.MIRROR_INVERSE == fillMode) {
                createMirrorInverseSmile(smileCenter,
                        curveControl1, curveControl2, point1, point2, smile)
            } else if (Smile.STRAIGHT == fillMode) {
                createStraightSmile(smileCenter, width, angle, length, smile)
            }
        }

        private fun createMirrorInverseSmile(smileCenter: Point, curveControl1: Point?,
                                             curveControl2: Point?, point1: Point?,
                                             point2: Point?, smileType: Int) {
            val centerX = smileCenter.x
            val centerY = smileCenter.y
            // Switching x
            var temp = curveControl1!!.x
            curveControl1.x = curveControl2!!.x
            curveControl2.x = temp
            temp = point1!!.x
            point1.x = point2!!.x
            point2.x = temp

            // Inverse the y axis of input
            inversePointY(centerY, point1, point2)
            inversePointY(centerY, curveControl1, curveControl2)

            // Generate all points by reflecting given inputs
            val smile = Smile()
            smile.START_POINT = point1
            smile.BOTTOM_CURVE[2] = point2
            smile.LEFT_CURVE[0] = curveControl2
            smile.LEFT_CURVE[1] = curveControl1
            smile.LEFT_CURVE[2] = point1
            fillReflectionPoints(centerX, smile)
            mSmileys[smileType] = smile
        }

        private fun createMirrorSmile(smileCenter: Point, curveControl1: Point?, curveControl2: Point?,
                                      point1: Point?, point2: Point?, @Smiley smileType: Int) {
            val centerX = smileCenter.x
            val centerY = smileCenter.y
            val smile = Smile()
            smile.START_POINT = point1
            smile.BOTTOM_CURVE[2] = point2
            smile.LEFT_CURVE[0] = curveControl2
            smile.LEFT_CURVE[1] = curveControl1
            smile.LEFT_CURVE[2] = point1
            fillReflectionPoints(centerX, smile)
            mSmileys[smileType] = smile
        }

        private fun createStraightSmile(smileCenter: Point, width: Float,
                                        angle: Float, length: Float, smileType: Int) {
            val centerX = smileCenter.x
            val centerY = smileCenter.y
            var start = getPointByAngle(smileCenter, roundDegreeOfAngle(angle - 180), length / 2)
            val smile = Smile()
            smile.LEFT_CURVE[0] = getPointByAngle(start, roundDegreeOfAngle(angle - 270), width)
            smile.LEFT_CURVE[1] = getPointByAngle(start, roundDegreeOfAngle(angle - 90), width)
            start = getPointByAngle(start, angle, length / 6f)
            smile.START_POINT = getPointByAngle(start, roundDegreeOfAngle(angle - 90), width)
            smile.BOTTOM_CURVE[2] = getPointByAngle(start, roundDegreeOfAngle(angle - 270), width)
            smile.LEFT_CURVE[2] = smile.START_POINT
            fillInverseReflectionPoints(centerX, centerY, smile)
            //            smile.START_POINT = BaseRating.getPointByAngle(smileCenter, roundDegreeOfAngle(angle - 180), length / 2);
            mSmileys[smileType] = smile
        }

        private fun fillInverseReflectionPoints(centerX: Float, centerY: Float, smile: Smile) {
            // Generate all points by reflecting given inputs
            smile.TOP_CURVE[0] = getNextPoint(smile.LEFT_CURVE[1], smile.START_POINT, Point())
            smile.TOP_CURVE[1] = getReflectionPointX(centerX, smile.TOP_CURVE[0])
            smile.TOP_CURVE[2] = getReflectionPointX(centerX, smile.START_POINT)
            smile.RIGHT_CURVE[0] = getReflectionPointX(centerX, smile.LEFT_CURVE[1])
            smile.RIGHT_CURVE[1] = getReflectionPointX(centerX, smile.LEFT_CURVE[0])
            smile.RIGHT_CURVE[2] = getReflectionPointX(centerX, smile.BOTTOM_CURVE[2])
            smile.BOTTOM_CURVE[1] = getNextPoint(smile.LEFT_CURVE[0], smile.BOTTOM_CURVE[2], Point())
            smile.BOTTOM_CURVE[0] = getReflectionPointX(centerX, smile.BOTTOM_CURVE[1])
            switchX(smile.TOP_CURVE[1], smile.BOTTOM_CURVE[0])
            inversePointY(centerY, smile.TOP_CURVE[1], smile.BOTTOM_CURVE[0])
            switchX(smile.TOP_CURVE[2], smile.RIGHT_CURVE[2])
            inversePointY(centerY, smile.TOP_CURVE[2], smile.RIGHT_CURVE[2])
            switchX(smile.RIGHT_CURVE[0], smile.RIGHT_CURVE[1])
            inversePointY(centerY, smile.RIGHT_CURVE[0], smile.RIGHT_CURVE[1])
        }

        private fun fillReflectionPoints(centerX: Float, smile: Smile) {
            // Generate all points by reflecting given inputs
            smile.TOP_CURVE[0] = getNextPoint(smile.LEFT_CURVE[1], smile.START_POINT, Point())
            smile.TOP_CURVE[1] = getReflectionPointX(centerX, smile.TOP_CURVE[0])
            smile.TOP_CURVE[2] = getReflectionPointX(centerX, smile.START_POINT)
            smile.RIGHT_CURVE[0] = getReflectionPointX(centerX, smile.LEFT_CURVE[1])
            smile.RIGHT_CURVE[1] = getReflectionPointX(centerX, smile.LEFT_CURVE[0])
            smile.RIGHT_CURVE[2] = getReflectionPointX(centerX, smile.BOTTOM_CURVE[2])
            smile.BOTTOM_CURVE[1] = getNextPoint(smile.LEFT_CURVE[0], smile.BOTTOM_CURVE[2], Point())
            smile.BOTTOM_CURVE[0] = getReflectionPointX(centerX, smile.BOTTOM_CURVE[1])
        }

        private fun switchX(p1: Point?, p2: Point?) {
            val temp = p1!!.x
            p1.x = p2!!.x
            p2.x = temp
        }

        private fun inversePointY(centerY: Float, p1: Point?, p2: Point?) {
            val temp = centerY - p1!!.y
            p1.y = centerY - (p2!!.y - centerY)
            p2.y = centerY + temp
        }

        private fun createGreatSmile() {
            val div = 0.10f
            val f = FloatEvaluator()
            createSmile(Point(mCenterSmile, mCenterY),
                    Point(f.evaluate(div, mCenterSmile * 0.295, mCenterSmile), f.evaluate(div, mCenterY - mCenterSmile * 0.23, mCenterY)),  // Top control
                    Point(f.evaluate(div, mCenterSmile * 0.295, mCenterSmile), f.evaluate(div, mCenterY - mCenterSmile * 0.088, mCenterY)),  // Bottom control
                    Point(f.evaluate(div, mCenterSmile * 0.591, mCenterSmile), f.evaluate(div, mCenterY - mCenterSmile * 0.23, mCenterY)),  // Top Point
                    Point(f.evaluate(div, mCenterSmile * 0.591, mCenterSmile), f.evaluate(div, mCenterY + mCenterSmile * 0.118, mCenterY)),  // Bottom point
                    Smile.MIRROR, GREAT, -1f, -1f, -1f)
        }

        private fun createGoodSmile() {
            val div = 0.20f
            val f = FloatEvaluator()
            createSmile(Point(mCenterSmile, mCenterY),
                    Point(f.evaluate(div, mCenterSmile * 0.414, mCenterSmile), f.evaluate(div, mCenterY - mCenterSmile * 0.24, mCenterY)),  // Top control
                    Point(f.evaluate(div, mCenterSmile * 0.355, mCenterSmile), f.evaluate(div, mCenterY - mCenterSmile * 0.029, mCenterY)),  // Bottom control
                    Point(f.evaluate(div, mCenterSmile * 0.65, mCenterSmile), f.evaluate(div, mCenterY - mCenterSmile * 0.118, mCenterY)),  // Top Point
                    Point(f.evaluate(div, mCenterSmile * 0.591, mCenterSmile), f.evaluate(div, mCenterY + mCenterSmile * 0.118, mCenterY)),  // Bottom point
                    Smile.MIRROR, GOOD, -1f, -1f, -1f)
        }

        private fun createOkaySmile() {
            createSmile(Point(mCenterSmile, mCenterY), null, null, null, null,
                    Smile.STRAIGHT, OKAY, mCenterSmile * 0.094f, 350f, mCenterSmile * 0.798f /*75 + 75*/)
        }

        private fun createBadSmile() {
            val div = 0.20f
            val f = FloatEvaluator()
            createSmile(Point(mCenterSmile, mCenterY),
                    Point(f.evaluate(div, mCenterSmile * 0.414, mCenterSmile), f.evaluate(div, mCenterY - mCenterSmile * 0.24, mCenterY)),  // Top control
                    Point(f.evaluate(div, mCenterSmile * 0.355, mCenterSmile), f.evaluate(div, mCenterY - mCenterSmile * 0.029, mCenterY)),  // Bottom control
                    Point(f.evaluate(div, mCenterSmile * 0.65, mCenterSmile), f.evaluate(div, mCenterY - mCenterSmile * 0.118, mCenterY)),  // Top Point
                    Point(f.evaluate(div, mCenterSmile * 0.591, mCenterSmile), f.evaluate(div, mCenterY + mCenterSmile * 0.118, mCenterY)),  // Bottom point
                    Smile.MIRROR_INVERSE, BAD, -1f, -1f, -1f)
        }

        private fun createTerribleSmile() {
            val div = 0.20f
            val f = FloatEvaluator()
            createSmile(Point(mCenterSmile, mCenterY),
                    Point(f.evaluate(div, mCenterSmile * 0.414, mCenterSmile), f.evaluate(div, mCenterY - mCenterSmile * 0.24, mCenterY)),  // Top control
                    Point(f.evaluate(div, mCenterSmile * 0.355, mCenterSmile), f.evaluate(div, mCenterY - mCenterSmile * 0.029, mCenterY)),  // Bottom control
                    Point(f.evaluate(div, mCenterSmile * 0.65, mCenterSmile), f.evaluate(div, mCenterY - mCenterSmile * 0.118, mCenterY)),  // Top Point
                    Point(f.evaluate(div, mCenterSmile * 0.591, mCenterSmile), f.evaluate(div, mCenterY + mCenterSmile * 0.118, mCenterY)),  // Bottom point
                    Smile.MIRROR_INVERSE, TERRIBLE, -1f, -1f, -1f)
        }

        private fun getReflectionPointX(centerX: Float, source: Point?): Point {
            val point = Point()
            getNextPoint(source, Point(centerX, source!!.y), point)
            return point
        }

        private fun getReflectionPointY(centerY: Float, source: Point): Point {
            val point = Point()
            getNextPoint(source, Point(source.x, centerY), point)
            return point
        }

        companion object {
            fun newInstance(w: Int, h: Int): Smileys {
                return Smileys(w, h)
            }
        }

        init {
            createGreatSmile()
            createGoodSmile()
            createOkaySmile()
            createBadSmile()
            createTerribleSmile()
        }
    }

    protected object EyeEmotion {
        private const val BAD_START_ANGLE = -90f
        private const val BAD_SWEEP_ANGLE = 270f
        private const val TERRIBLE_START_ANGLE = -35f
        private const val TERRIBLE_SWEEP_ANGLE = 280f
        private const val OTHER_START_ANGLE = -135f
        private const val OTHER_SWEEP_ANGLE = 360f
        fun prepareEye(eye: Eye, evaluator: FloatEvaluator, fraction: Float, @Smiley smile: Int): Eye {
            if (TERRIBLE == smile) {
                val startAngle = evaluator.evaluate(fraction, TERRIBLE_START_ANGLE, BAD_START_ANGLE)
                val sweepAngle = evaluator.evaluate(fraction, TERRIBLE_SWEEP_ANGLE, BAD_SWEEP_ANGLE)
                if (eye.eyeSide == Eye.LEFT) {
                    eye.startAngle = startAngle
                    eye.sweepAngle = sweepAngle
                } else {
                    mirrorEye(eye, startAngle, sweepAngle)
                }
            } else if (BAD == smile) {
                val startAngle = evaluator.evaluate(fraction, BAD_START_ANGLE, OTHER_START_ANGLE)
                val sweepAngle = evaluator.evaluate(fraction, BAD_SWEEP_ANGLE, OTHER_SWEEP_ANGLE)
                if (eye.eyeSide == Eye.LEFT) {
                    eye.startAngle = startAngle
                    eye.sweepAngle = sweepAngle
                } else {
                    mirrorEye(eye, startAngle, sweepAngle)
                }
            } else {
                eye.startAngle = OTHER_START_ANGLE
                eye.sweepAngle = OTHER_SWEEP_ANGLE
            }
            return eye
        }

        private fun mirrorEye(eye: Eye, startAngle: Float, sweepAngle: Float) {
            var startAngleLoc = startAngle
            val d2 = startAngleLoc + sweepAngle - 180
            startAngleLoc = -d2
            eye.startAngle = startAngleLoc
            eye.sweepAngle = sweepAngle
        }
    }

    protected class Eye {
        @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
        @IntDef(LEFT, RIGHT)
        annotation class EyeSide

        var startAngle = 0f
        var sweepAngle = 0f
        var center: Point? = Point()

        @EyeSide
        var eyeSide = 0
        var radius = 0f
        private val eyePosition = RectF()
        private fun getEyePosition(): RectF {
            if (center != null) {
                eyePosition[center!!.x - radius, center!!.y - radius, center!!.x + radius] = center!!.y + radius
            }
            return eyePosition
        }

        fun fillPath(path: Path?): Path {
            var pathLoc = path
            if (pathLoc == null) {
                pathLoc = Path()
            }
            pathLoc.addArc(getEyePosition(), startAngle, sweepAngle)
            return pathLoc
        }

        companion object {
            const val LEFT = 0
            const val RIGHT = 1
        }
    }

    protected class Smile @JvmOverloads constructor(@param:Mode var mMode: Int = MIRROR) {
        @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
        @IntDef(LEFT, RIGHT, ALL)
        annotation class Side

        @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
        @IntDef(MIRROR, INDEPENDENT, MIRROR_INVERSE, STRAIGHT)
        annotation class Mode

        var START_POINT: Point? = null
        var TOP_CURVE = arrayOfNulls<Point>(3)
        var RIGHT_CURVE = arrayOfNulls<Point>(3)
        var BOTTOM_CURVE = arrayOfNulls<Point>(3)
        var LEFT_CURVE = arrayOfNulls<Point>(3)
        fun transform(@Side side: Int, x: Float, y: Float) {
            if (ALL == side) {
                transformLeft(x, y)
                transformRight(x, y)
            } else if (RIGHT == side) {
                transformRight(x, y)
            } else if (LEFT == side) {
                transformLeft(x, y)
            }
        }

        private fun transformLeft(x: Float, y: Float) {
            START_POINT!!.trans(x, y)
            LEFT_CURVE[0]!!.trans(x, y)
            LEFT_CURVE[1]!!.trans(x, y)
            BOTTOM_CURVE[2]!!.trans(x, y)
            BOTTOM_CURVE[1]!!.trans(x, y)
            TOP_CURVE[0]!!.trans(x, y)
        }

        private fun transformRight(x: Float, y: Float) {
            TOP_CURVE[1]!!.trans(x, y)
            TOP_CURVE[2]!!.trans(x, y)
            RIGHT_CURVE[0]!!.trans(x, y)
            RIGHT_CURVE[1]!!.trans(x, y)
            RIGHT_CURVE[2]!!.trans(x, y)
            BOTTOM_CURVE[0]!!.trans(x, y)
        }

        fun fillPath(path: Path): Path {
            var pathLoc = path
            pathLoc.reset()
            pathLoc.moveTo(START_POINT!!.x, START_POINT!!.y)
            pathLoc = cube(path, TOP_CURVE)
            pathLoc = cube(path, RIGHT_CURVE)
            pathLoc = cube(path, BOTTOM_CURVE)
            pathLoc = cube(path, LEFT_CURVE)
            pathLoc.close()
            return pathLoc
        }

        private fun cube(path: Path, curve: Array<Point?>): Path {
            path.cubicTo(
                    curve[0]!!.x, curve[0]!!.y,
                    curve[1]!!.x, curve[1]!!.y,
                    curve[2]!!.x, curve[2]!!.y
            )
            return path
        }

        fun drawPoints(canvas: Canvas, paint: Paint) {
            drawPoint(START_POINT, canvas, paint)
            drawPointArray(TOP_CURVE, canvas, paint)
            drawPointArray(RIGHT_CURVE, canvas, paint)
            drawPointArray(BOTTOM_CURVE, canvas, paint)
            drawPointArray(LEFT_CURVE, canvas, paint)
            /*drawPoint(LEFT_CURVE[1], canvas, paint);
            drawPoint(START_POINT, canvas, paint);*/
        }

        private fun drawPointArray(points: Array<Point?>, canvas: Canvas, paint: Paint) {
            for (point in points) {
                drawPoint(point, canvas, paint)
            }
        }

        private fun drawPoint(point: Point?, canvas: Canvas, paint: Paint) {
            if (point == null) return
            Log.i(TAG, point.toString())
            canvas.drawCircle(point.x, point.y, 6f, paint)
        }

        companion object {
            const val LEFT = 0
            const val RIGHT = 1
            const val ALL = 2
            const val MIRROR = 0
            const val INDEPENDENT = 1
            const val MIRROR_INVERSE = 2
            const val STRAIGHT = 3
        }
    }

    class Point {
        var x = 0f
        var y = 0f

        constructor() {}
        constructor(x: Float, y: Float) {
            this.x = x
            this.y = y
        }

        fun trans(x: Float, y: Float) {
            this.x += x
            this.y += y
        }

        override fun toString(): String {
            return "Point{" +
                    "x=" + x +
                    ", y=" + y +
                    '}'
        }
    }

    protected class Line {
        var start: Point? = null
        var end: Point? = null

        constructor() {}
        constructor(start: Point?, end: Point?) {
            this.start = start
            this.end = end
        }

        fun draw(canvas: Canvas, paint: Paint?) {
            canvas.drawLine(start!!.x, start!!.y, end!!.x, end!!.y, paint!!)
        }

        override fun toString(): String {
            return "Line{" +
                    "start=" + start +
                    ", end=" + end +
                    '}'
        }
    }

    protected fun translateSmile(smile: Smile, x: Float, y: Float) {
        translatePoint(smile.START_POINT, x, y)
        translatePoints(smile.TOP_CURVE, x, y)
        translatePoints(smile.RIGHT_CURVE, x, y)
        translatePoints(smile.BOTTOM_CURVE, x, y)
        translatePoints(smile.LEFT_CURVE, x, y)
    }

    private fun translatePoints(points: Array<Point?>, x: Float, y: Float) {
        for (point in points) {
            translatePoint(point, x, y)
        }
    }

    private fun translatePoint(point: Point?, x: Float, y: Float) {
        point!!.x += x
        point!!.y += y
    }

    protected fun transformSmile(trans: Float, fraction: Float, path: Path, s1: Smile, s2: Smile, evaluator: FloatEvaluator): Path {
        path.reset()
        path.moveTo(
                evaluator.evaluate(fraction, s1.START_POINT!!.x, s2.START_POINT!!.x) + trans,
                evaluator.evaluate(fraction, s1.START_POINT!!.y, s2.START_POINT!!.y)
        )
        path.cubicTo(
                evaluator.evaluate(fraction, s1.TOP_CURVE[0]!!.x, s2.TOP_CURVE[0]!!.x) + trans,
                evaluator.evaluate(fraction, s1.TOP_CURVE[0]!!.y, s2.TOP_CURVE[0]!!.y),
                evaluator.evaluate(fraction, s1.TOP_CURVE[1]!!.x, s2.TOP_CURVE[1]!!.x) + trans,
                evaluator.evaluate(fraction, s1.TOP_CURVE[1]!!.y, s2.TOP_CURVE[1]!!.y),
                evaluator.evaluate(fraction, s1.TOP_CURVE[2]!!.x, s2.TOP_CURVE[2]!!.x) + trans,
                evaluator.evaluate(fraction, s1.TOP_CURVE[2]!!.y, s2.TOP_CURVE[2]!!.y)
        )
        path.cubicTo(
                evaluator.evaluate(fraction, s1.RIGHT_CURVE[0]!!.x, s2.RIGHT_CURVE[0]!!.x) + trans,
                evaluator.evaluate(fraction, s1.RIGHT_CURVE[0]!!.y, s2.RIGHT_CURVE[0]!!.y),
                evaluator.evaluate(fraction, s1.RIGHT_CURVE[1]!!.x, s2.RIGHT_CURVE[1]!!.x) + trans,
                evaluator.evaluate(fraction, s1.RIGHT_CURVE[1]!!.y, s2.RIGHT_CURVE[1]!!.y),
                evaluator.evaluate(fraction, s1.RIGHT_CURVE[2]!!.x, s2.RIGHT_CURVE[2]!!.x) + trans,
                evaluator.evaluate(fraction, s1.RIGHT_CURVE[2]!!.y, s2.RIGHT_CURVE[2]!!.y)
        )
        path.cubicTo(
                evaluator.evaluate(fraction, s1.BOTTOM_CURVE[0]!!.x, s2.BOTTOM_CURVE[0]!!.x) + trans,
                evaluator.evaluate(fraction, s1.BOTTOM_CURVE[0]!!.y, s2.BOTTOM_CURVE[0]!!.y),
                evaluator.evaluate(fraction, s1.BOTTOM_CURVE[1]!!.x, s2.BOTTOM_CURVE[1]!!.x) + trans,
                evaluator.evaluate(fraction, s1.BOTTOM_CURVE[1]!!.y, s2.BOTTOM_CURVE[1]!!.y),
                evaluator.evaluate(fraction, s1.BOTTOM_CURVE[2]!!.x, s2.BOTTOM_CURVE[2]!!.x) + trans,
                evaluator.evaluate(fraction, s1.BOTTOM_CURVE[2]!!.y, s2.BOTTOM_CURVE[2]!!.y)
        )
        path.cubicTo(
                evaluator.evaluate(fraction, s1.LEFT_CURVE[0]!!.x, s2.LEFT_CURVE[0]!!.x) + trans,
                evaluator.evaluate(fraction, s1.LEFT_CURVE[0]!!.y, s2.LEFT_CURVE[0]!!.y),
                evaluator.evaluate(fraction, s1.LEFT_CURVE[1]!!.x, s2.LEFT_CURVE[1]!!.x) + trans,
                evaluator.evaluate(fraction, s1.LEFT_CURVE[1]!!.y, s2.LEFT_CURVE[1]!!.y),
                evaluator.evaluate(fraction, s1.LEFT_CURVE[2]!!.x, s2.LEFT_CURVE[2]!!.x) + trans,
                evaluator.evaluate(fraction, s1.LEFT_CURVE[2]!!.y, s2.LEFT_CURVE[2]!!.y)
        )
        path.close()
        return path
    }

    companion object {
        private const val TAG = "BaseSmile"
        const val NONE = -1
        const val TERRIBLE = 0
        const val BAD = 1
        const val OKAY = 2
        const val GOOD = 3
        const val GREAT = 4
        const val POINT_1 = 0
        const val POINT_2 = 1
        const val CONTROL_POINT_1 = 2
        const val CONTROL_POINT_2 = 3
        protected fun getNextPoint(start: Point?, end: Point?, point: Point): Point {
            val len = getDistance(start, end)
            val ratio = if (len < 0) -1f else 1f
            point.x = end!!.x + ratio * (end.x - start!!.x)
            point.y = end.y + ratio * (end.y - start.y)
            return point
        }

        protected fun getDistance(p1: Point?, p2: Point?): Float {
            return sqrt((
                    (p1!!.x - p2!!.x) * (p1.x - p2.x) +
                            (p1.y - p2.y) * (p1.y - p2.y)
                    ).toDouble()).toFloat()
        }

        protected fun getPointByAngle(source: Point, angle: Float, width: Float): Point {
            val endX = (source.x + cos(Math.toRadians(angle.toDouble())) * width).toFloat()
            val endY = (source.y + sin(Math.toRadians(angle.toDouble())) * width).toFloat()
            return Point(endX, endY)
        }

        fun roundDegreeOfAngle(angle: Float): Float {
            if (angle < 0) {
                return roundDegreeOfAngle(angle + 360)
            } else if (angle >= 360) {
                return angle % 360
            }
            return angle + 0.0f
        }
    }
}