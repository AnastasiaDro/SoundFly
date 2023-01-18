package com.soundapp.soundfly

import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaRecorder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.soundapp.soundfly.databinding.ActivityMainBinding
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity(), Timer.OnTimerClickListener {

    private lateinit var viewBinding: ActivityMainBinding

    private var permissions = arrayOf(android.Manifest.permission.RECORD_AUDIO)
    private var permissionGranted = false

    private lateinit var recorder: MediaRecorder
    private var dirPath = ""
    private var fileName = ""
    private var isRecording = false
    private var isPaused = false
    private lateinit var timer: Timer

    private lateinit var vibrator: Vibrator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        timer = Timer(this)
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        viewBinding.btnRecord.setOnClickListener {
            when {
                isPaused -> resumeRecorder()
                isRecording -> pauseRecorder()
                else -> startRecording()
            }

            vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
        }
    }

    private fun pauseRecorder() {
        recorder.pause()
        isPaused = true
        viewBinding.btnRecord.setImageResource(R.drawable.ic_record)
        timer.pause()
    }

    private fun resumeRecorder() {
        recorder.resume()
        isPaused = false
        viewBinding.btnRecord.setImageResource(R.drawable.ic_pause)
        timer.start()
    }

    private fun startRecording() {
        if (checkMicrophoneAppearance()) requestPermission() else Toast.makeText(applicationContext, R.string.no_micro, Toast.LENGTH_SHORT).show()

        recorder = MediaRecorder()
        dirPath = "${externalCacheDir?.absolutePath}/"

        var simpleDateFormat = SimpleDateFormat("yyyy.MM.DD_hh.mm.ss")
        var date = simpleDateFormat.format(Date())
        fileName = "audio_record_$date"

        recorder.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile("$dirPath$fileName.mp3")
            try {
                prepare()
            } catch (e:IOException) {}
            start()
        }
        viewBinding.btnRecord.setImageResource(R.drawable.ic_pause)
        isRecording = true
        isPaused = false

        timer.start()
    }

    private fun stopRecorder(){
        timer.stop()
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

    override fun onTimerTick(duration: String) {
        viewBinding.tvTimer.text = duration
    }
}