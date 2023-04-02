package dev.kietyo.scrap.activities

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.registerForActivityResult
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import dev.kietyo.scrap.log
import dev.kietyo.scrap.utils.STRING_ACTIVITY_RESULT

class SettingsActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            log("SettingsActivity: Got activity result")
            log(it.resultCode)
            log(it.data)
        }

        setContent {
            SettingsContent {
                val intent = Intent()
                intent.putExtra(STRING_ACTIVITY_RESULT, it)
//                launcher.launch(intent)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }
    }
}

@Composable
fun SettingsContent(onSaveButtonClick: (String) -> Unit) {
    Column {
        var currentText by remember {
            mutableStateOf("Enter here")
        }
        Text(text = "Hello world")
        TextField(value = currentText, onValueChange = {
            currentText = it
        })
        TextButton(
            onClick = { onSaveButtonClick(currentText) }) {
            Text(text = "Save")
        }
    }

}