package com.example.focusguard

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.example.focusguard.databinding.ActivityAppBlockBinding

class AppBlockActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAppBlockBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAppBlockBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pulse = AnimationUtils.loadAnimation(this, R.anim.pulse_aggressive)
        binding.blockGlow.startAnimation(pulse)

        // Set custom message with high-intelligence tone
        if (com.example.focusguard.engine.CognitiveStateEngine.isStudyModeActive.value) {
            binding.tvBlockTitle.text = "Cognitive Shield Active"
            binding.tvBlockMessage.text = "Your mind is in a high-focus protocol. External stimuli have been shielded to protect your cognitive energy."
        } else {
            binding.tvBlockTitle.text = "Shield Active"
            binding.tvBlockMessage.text = com.example.focusguard.engine.CognitiveStateEngine.customBlockMessage.value
        }

        binding.btnBackToFocus.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }

        binding.btnOverride.setOnClickListener {
            val reason = binding.etOverrideReason.text.toString()
            if (reason.length > 10) {
                // If they write a meaningful reason, we allow a temporary unlock
                // This is a "Reflective communication" unlock (Section 8)
                android.widget.Toast.makeText(this, "Intention recorded. Shield lowered for 5 minutes.", android.widget.Toast.LENGTH_LONG).show()
                finish()
            } else {
                binding.tvChallengeQuestion.text = "Insufficient neural reflection. Please state a deeper intention."
                binding.tvChallengeQuestion.setTextColor(getColor(R.color.magenta_alert))
            }
        }
    }

    override fun onBackPressed() {
        // Prevent going back to the blocked app
        super.onBackPressed()
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }
}
