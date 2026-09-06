package com.example

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.ui.NexusMainScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.NexusViewModel
import java.util.Locale

class MainActivity : ComponentActivity(), TextToSpeech.OnInitListener {
  private val viewModel: NexusViewModel by viewModels()
  private var textToSpeech: TextToSpeech? = null
  private var isTtsReady = false

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    textToSpeech = TextToSpeech(this, this)

    setContent {
      MyApplicationTheme {
        NexusMainScreen(
          viewModel = viewModel,
          onSpeak = { text -> speakText(text) },
          onStopSpeak = { stopSpeech() },
          modifier = Modifier.fillMaxSize()
        )
      }
    }
  }

  override fun onInit(status: Int) {
    if (status == TextToSpeech.SUCCESS) {
      val result = textToSpeech?.setLanguage(Locale.US)
      if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
        isTtsReady = true
      }
      textToSpeech?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
        override fun onStart(utteranceId: String?) {
          runOnUiThread { viewModel.setSpeaking(true) }
        }

        override fun onDone(utteranceId: String?) {
          runOnUiThread { viewModel.setSpeaking(false) }
        }

        override fun onError(utteranceId: String?) {
          runOnUiThread { viewModel.setSpeaking(false) }
        }
      })
    }
  }

  private fun speakText(rawText: String) {
    if (!isTtsReady) {
      viewModel.showToast("Speech engine initializing...")
      return
    }

    val cleanText = sanitizeForSpeech(rawText)
    textToSpeech?.stop()
    val params = Bundle()
    textToSpeech?.speak(cleanText, TextToSpeech.QUEUE_FLUSH, params, "NEXUS_SPEECH_ID")
    viewModel.setSpeaking(true)
  }

  private fun stopSpeech() {
    textToSpeech?.stop()
    viewModel.setSpeaking(false)
  }

  private fun sanitizeForSpeech(text: String): String {
    return text
      .replace(Regex("```[a-zA-Z]*"), " code block ")
      .replace("```", "")
      .replace(Regex("[#*`_-]"), " ")
      .replace(Regex("\\s+"), " ")
      .trim()
  }

  override fun onDestroy() {
    textToSpeech?.stop()
    textToSpeech?.shutdown()
    textToSpeech = null
    super.onDestroy()
  }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
  Text(text = "Hello $name!", modifier = modifier)
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
  MyApplicationTheme { Greeting("Android") }
}

