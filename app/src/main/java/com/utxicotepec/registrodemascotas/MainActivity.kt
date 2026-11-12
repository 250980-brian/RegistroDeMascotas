package com.utxicotepec.registrodemascotas

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.utxicotepec.registrodemascotas.ui.theme.RegistroDeMascotasTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.utxicotepec.registrodemascotas.ui.componentes.AntDialogInfo
import kotlinx.coroutines.launch
/**
 *Practica correspondiente a la semana 8 de la unidad 2
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RegistroDeMascotasTheme {
                val snackbarHostState = remember { SnackbarHostState() }
                val scope = rememberCoroutineScope()

                var openDialog by remember { mutableStateOf(false) }
                var cleanForm by remember { mutableStateOf(false) }
                var mascotasFilled: Mascotas? = null

                Scaffold(modifier = Modifier.fillMaxSize(),
                    snackbarHost = { SnackbarHost(snackbarHostState) }) { innerPadding ->
                    MainView(Modifier.padding(innerPadding),
                        isClean = cleanForm,
                        onCleaned = { cleanForm = false },
                        onError = { error ->
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    message = error,
                                    actionLabel = getString(R.string.dialog_ok),
                                    duration = SnackbarDuration.Long
                                )
                            }
                        }){ mascotas ->
                        Log.i("CEAA", "onCreate: $mascotas")
                        mascotasFilled = mascotas
                        openDialog = true
                    }

                    if (openDialog) {
                        mascotasFilled?.let { mascotas ->
                            AntDialogInfo(info = mascotasFormatter(mascotas),
                                titleRes = R.string.dialog_title,
                                confirmRes = R.string.dialog_clean) { clean ->
                                cleanForm = clean
                                openDialog = false
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun LocalPreview() {
    MainPreview()
}