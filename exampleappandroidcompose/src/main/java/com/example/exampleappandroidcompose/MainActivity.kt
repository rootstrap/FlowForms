package com.example.exampleappandroidcompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.exampleappandroidcompose.ui.theme.FlowFormsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FlowFormsTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Android")
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Column {
        Text(
            text = "Test1",
            modifier = modifier
        )
        Text(
            text = "Test2",
            modifier = modifier
        )
        Test(modifier = modifier.background(Color.Magenta))
    }
}

@Composable
fun Test(modifier: Modifier) {
    Row(modifier = modifier) {
        Text(
            text = "Test1",
            modifier = Modifier.padding(4.dp).background(Color.Green).padding(2.dp),
            color = Color.Blue
        )
        Text(
            text = "Test2"
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    FlowFormsTheme {
        Greeting("Android")
    }
}