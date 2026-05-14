package com.example.focusguard.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.example.focusguard.R
import java.util.*

class NeuralParticlesView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val particles = mutableListOf<Particle>()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val random = Random()
    private var particleColor = context.getColor(R.color.cyan_glow)
    private var isAnimating = false

    // Squared threshold avoids a sqrt per pair during the broad-phase filter
    private val connectionDist = 100f
    private val connectionDistSq = connectionDist * connectionDist

    data class Particle(
        var x: Float,
        var y: Float,
        var vx: Float,
        var vy: Float,
        var size: Float,
        var alpha: Int
    )

    init {
        setLayerType(LAYER_TYPE_HARDWARE, null)
        post {
            for (i in 0 until 20) { // 20 particles is plenty for the effect
                particles.add(createParticle())
            }
            startAnimation()
        }
    }

    private fun createParticle(): Particle {
        return Particle(
            x = random.nextFloat() * width,
            y = random.nextFloat() * height,
            vx = (random.nextFloat() - 0.5f) * 2f,
            vy = (random.nextFloat() - 0.5f) * 2f,
            size = random.nextFloat() * 8f + 2f,
            alpha = random.nextInt(100) + 50
        )
    }

    fun setParticleColor(color: Int) {
        particleColor = color
        invalidate()
    }

    private fun startAnimation() {
        if (!isAnimating) {
            isAnimating = true
            postInvalidateOnAnimation()
        }
    }

    private fun stopAnimation() {
        isAnimating = false
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        startAnimation()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stopAnimation()
    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)
        if (visibility == VISIBLE) startAnimation() else stopAnimation()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        paint.color = particleColor

        // FILL for dot bodies; stroke settings configured once before connection drawing
        paint.style = Paint.Style.FILL

        for (i in particles.indices) {
            val p = particles[i]

            // Move particle
            p.x += p.vx
            p.y += p.vy

            // Boundary checks
            if (p.x < 0 || p.x > width) p.vx *= -1
            if (p.y < 0 || p.y > height) p.vy *= -1

            // Draw particle
            paint.alpha = p.alpha
            canvas.drawCircle(p.x, p.y, p.size, paint)
        }

        // Draw connections — stroke style set once outside the loops
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 1f

        for (i in particles.indices) {
            val p = particles[i]

            // Only check each pair once (j > i)
            for (j in i + 1 until particles.size) {
                val other = particles[j]
                val dx = p.x - other.x
                val dy = p.y - other.y
                val distSq = dx * dx + dy * dy

                if (distSq < connectionDistSq) {
                    val dist = Math.sqrt(distSq.toDouble()).toFloat()
                    paint.alpha = ((1f - dist / connectionDist) * 50).toInt()
                    canvas.drawLine(p.x, p.y, other.x, other.y, paint)
                }
            }
        }

        // Schedule next frame in sync with display VSync — only while visible
        if (isAnimating) {
            postInvalidateOnAnimation()
        }
    }
}
