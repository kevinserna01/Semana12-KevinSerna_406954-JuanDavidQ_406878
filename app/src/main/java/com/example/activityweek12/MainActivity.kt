package com.example.activityweek12

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.activityweek12.ui.theme.ActivityWeek12Theme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ActivityWeek12Theme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    NameGradeForm()
                }
            }
        }
    }
}

@Composable
fun NameGradeForm() {
    val context = LocalContext.current
    val client = remember { OkHttpClient() }
    val scope = rememberCoroutineScope()
    val baseUrl = remember { "http://10.0.2.2:3000" } // Para emulador Android accediendo al host

    var nombre by remember { mutableStateOf(TextFieldValue("")) }
    var email by remember { mutableStateOf(TextFieldValue("")) }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Registro", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(24.dp))

        // Campo Nombre
        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Campo Email
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )
        Spacer(modifier = Modifier.height(24.dp))

        // Botón Submit
        Button(
            onClick = {
                if (nombre.text.isBlank() || email.text.isBlank()) {
                    Toast.makeText(context, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                isLoading = true
                scope.launch {
                    val json = "{" +
                            "\"nombre\":\"${nombre.text}\"," +
                            "\"email\":\"${email.text}\"" +
                            "}"
                    val mediaType = "application/json; charset=utf-8".toMediaType()
                    val body: RequestBody = json.toRequestBody(mediaType)
                    val request = Request.Builder()
                        .url("$baseUrl/api/records")
                        .post(body)
                        .build()
                    val result = withContext(Dispatchers.IO) {
                        runCatching { client.newCall(request).execute() }
                    }
                    result.onSuccess { response ->
                        if (response.isSuccessful) {
                            Toast.makeText(context, "Registro enviado a la API", Toast.LENGTH_SHORT).show()
                            nombre = TextFieldValue("")
                            email = TextFieldValue("")
                        } else {
                            Toast.makeText(context, "Error ${response.code}", Toast.LENGTH_SHORT).show()
                        }
                        response.close()
                        isLoading = false
                    }.onFailure { e ->
                        Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                        isLoading = false
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Submit")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewForm() {
    ActivityWeek12Theme {
        NameGradeForm()
    }
}
