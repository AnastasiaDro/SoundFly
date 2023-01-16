package com.soundapp.SoundFly

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.soundapp.SoundFly.databinding.ActivityMainBinding
import java.util.jar.Manifest

class MainActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityMainBinding

    private var permissions = arrayOf(android.Manifest.permission.RECORD_AUDIO)
    private var permissionGranted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.btnRecord.setOnClickListener {
            startRecording()
        }
    }

    private fun startRecording() {
        if (checkMicrophoneAppearance()) requestPermission() else Toast.makeText(applicationContext, R.string.no_micro, Toast.LENGTH_SHORT).show()

    }

    private fun checkMicrophoneAppearance(): Boolean {
        return (this.packageManager.hasSystemFeature(PackageManager.FEATURE_MICROPHONE))
    }

    private fun requestPermission() {
        when {
            ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED -> {
                Log.i("Permission", "Granted")
            }
            ActivityCompat.shouldShowRequestPermissionRationale(this, "RECORD_AUDIO") -> {
                requestPermissionLauncher.launch(android.Manifest.permission.RECORD_AUDIO)
            }
            else -> { requestPermissionLauncher.launch(android.Manifest.permission.RECORD_AUDIO) }
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) { Log.i("Permission", "Granted") }
            else { Log.i("permission", "Denied") }
        }
}