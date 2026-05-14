package com.example.focusguard

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import com.example.focusguard.databinding.ActivityAppBlockBinding

class AppBlockActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAppBlockBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAppBlockBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Prevent going back to the blocked app — route to the home screen instead
        onBackPressedDispatcher.addCallback(this) {
            val intent = Intent(Intent.ACTION_MAIN)
            intent.addCategory(Intent.CATEGORY_HOME)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }

        val pulse = AnimationUtils.loadAnimation(this, R.anim.pulse_aggressive)
        binding.blockGlow.startAnimation(pulse)

        val prefs = getSharedPreferences("FocusGuardPrefs", android.content.Context.MODE_PRIVATE)
        val quote = prefs.getString("anchor_quote", "I build for my family's better tomorrow.")
        val photoUri = prefs.getString("anchor_photo", null)

        binding.tvEmotionalQuote.text = "\"$quote\""
        if (photoUri != null) {
            try {
                binding.ivEmotionalAnchor.setImageURI(android.net.Uri.parse(photoUri))
            } catch (e: Exception) {
                // Fallback handled via layout default
            }
        } else {
            binding.ivEmotionalAnchor.setBackgroundColor(getColor(R.color.card_dark))
        }

        binding.btnBackToFocus.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }

        binding.btnContinueAnyway.setOnClickListener {
            // Apply XP penalty or just unlock
            android.widget.Toast.makeText(this, "Shield lowered. Focus broken.", android.widget.Toast.LENGTH_LONG).show()
            finish()
        }
    }

    @Deprecated("Deprecated in Java")
    @Suppress("MissingSuperCall")
    override fun onBackPressed() {
        // Handled by OnBackPressedDispatcher callback registered in onCreate()
        onBackPressedDispatcher.onBackPressed()
    }
}
