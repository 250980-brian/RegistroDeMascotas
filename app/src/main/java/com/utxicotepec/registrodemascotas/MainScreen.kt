package com.utxicotepec.registrodemascotas

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.utxicotepec.registrodemascotas.ui.componentes.AntTextField
import com.utxicotepec.registrodemascotas.ui.componentes.AntTfDate
import com.utxicotepec.registrodemascotas.ui.componentes.DatePickerModal
import com.utxicotepec.registrodemascotas.ui.theme.RegistroDeMascotasTheme
import com.utxicotepec.registrodemascotas.ui.theme.Typography
import kotlinx.coroutines.launch

@Preview(showBackground = true)
@Composable
fun MainPreview() {
    RegistroDeMascotasTheme() {
        com.utxicotepec.registrodemascotas.MainView(
            Modifier.padding(top = 24.dp),
            onCleaned = {},
            onError = {},
        ) { }
    }
}


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MainView(
    modifier: Modifier,
    isClean: Boolean = false,
    onCleaned: () -> Unit,
    onError: (String) -> Unit,
    onSave: (Mascotas) -> Unit
) {
    val scope = rememberCoroutineScope()

    var nombreValue by remember { mutableStateOf("") }
    var apellidosValue by remember { mutableStateOf("") }
    var pesoValue by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    var dateValue by remember { mutableStateOf<Long?>(null) }
    var antecedentesValue by remember { mutableStateOf("") }
    var isAgree by remember { mutableStateOf(false) }
    var inProgress by remember { mutableStateOf(false) }

    // Opciones de especie (Perro, Gato, Otro) para el menú desplegable
    val profiles = stringArrayResource(R.array.profiles).toList()
    var selectedOption by remember {
        mutableStateOf(profiles.first())
    }

    val context = LocalContext.current
    val isShowKeyboard = WindowInsets.isImeVisible
    val focus = LocalFocusManager.current

    val saveFun = {
        focus.clearFocus()
        inProgress = true

        scope.launch {
            val errors = foundErrors(
                context,
                nombreValue,
                apellidosValue,
                pesoValue
            )

            if (errors == null) {
                kotlinx.coroutines.delay(2000L)

                val mascotas = Mascotas(
                    nombreValue,
                    apellidosValue,
                    pesoValue.toInt(),
                    dateValue ?: 0,
                    selectedOption,
                    antecedentesValue
                )

                onSave(mascotas)
            } else {
                onError(errors)
            }

            inProgress = false
        }
    }

    if (isClean) {
        nombreValue = ""
        apellidosValue = ""
        pesoValue = ""
        dateValue = null
        selectedOption = profiles.first()
        antecedentesValue = ""
        isAgree = false
        onCleaned()
    }

    Box(modifier.fillMaxWidth()) {

        Column(
            Modifier.padding(
                horizontal = dimensionResource(R.dimen.common_padding_default)
            )
        ) {

            Row(verticalAlignment = Alignment.CenterVertically) {

                Text(
                    stringResource(R.string.form_title),
                    modifier = Modifier.weight(1f),
                    style = Typography.titleLarge
                )

                val saveAlpha = if (isShowKeyboard) 1f else 0f

                Button(
                    onClick = { saveFun() },
                    modifier = Modifier.alpha(saveAlpha),
                    enabled = isAgree
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_check),
                        contentDescription = null
                    )
                }
            }

            Column(
                Modifier
                    .imePadding()
                    .verticalScroll(rememberScrollState())
            ) {

                // Nombre de la mascota
                AntTextField(
                    labelRes = R.string.hint_name,
                    iconRes = R.drawable.ic_person,
                    maxLengthRes = R.integer.name_max_length,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        imeAction = ImeAction.Next
                    ),
                    isClean = isClean,
                    onValueChange = { nombreValue = it }
                )

                // Nombre del dueño
                AntTextField(
                    labelRes = R.string.hint_surname,
                    iconRes = R.drawable.ic_person,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        imeAction = ImeAction.Next
                    ),
                    isClean = isClean,
                    onValueChange = { apellidosValue = it }
                )

                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(
                            top = dimensionResource(R.dimen.common_padding_min)
                        ),
                    horizontalArrangement = Arrangement.spacedBy(
                        dimensionResource(R.dimen.common_padding_default)
                    )
                ) {

                    // Peso
                    AntTextField(
                        modifier = Modifier.weight(40f),
                        labelRes = R.string.hint_height,
                        iconRes = R.drawable.ic_height,
                        maxLengthRes = R.integer.height_max_length,
                        minValue = integerResource(R.integer.height_min_value),
                        errorRes = R.string.error_min_height_valid,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        isClean = isClean,
                        paddingTop = dimensionResource(R.dimen.common_padding_none),
                        onValueChange = { pesoValue = it }
                    )

                    // Fecha de nacimiento
                    AntTfDate(
                        modifier = Modifier.weight(60f),
                        labelRes = R.string.hint_birth_date,
                        selectedDate = dateValue
                    ) {
                        showDatePicker = true
                    }

                    if (showDatePicker) {
                        DatePickerModal(
                            onDateSelected = { dateValue = it },
                            onDismiss = { showDatePicker = false }
                        )
                    }
                }

                // Menú desplegable de especie
                AntDropdown(
                    labelRes = R.string.section_occupation,
                    options = profiles,
                    selectedOption = selectedOption,
                    onOptionSelected = { selectedOption = it }
                )

                // Señas particulares
                AntTextField(
                    labelRes = R.string.hint_notes,
                    iconRes = R.drawable.ic_notes,
                    maxLengthRes = R.integer.notes_max_length,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done
                    ),
                    singleLine = false,
                    isRequired = false,
                    isClean = isClean,
                    paddingTop = dimensionResource(R.dimen.common_padding_none),
                    onValueChange = { antecedentesValue = it }
                )

                // Aviso de privacidad
                Row(verticalAlignment = Alignment.CenterVertically) {

                    Text(
                        stringResource(R.string.cb_agree),
                        modifier = Modifier.clickable {
                            isAgree = !isAgree
                        }
                    )

                    Checkbox(
                        checked = isAgree,
                        onCheckedChange = { isAgree = it }
                    )
                }

                // Botón Registrar
                val saveAlphaReversed =
                    if (!isShowKeyboard) 1f else 0f

                Button(
                    onClick = { saveFun() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .alpha(saveAlphaReversed)
                        .padding(
                            vertical = dimensionResource(
                                R.dimen.common_padding_default
                            )
                        ),
                    enabled = isAgree
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_check),
                        contentDescription = null
                    )

                    Text(stringResource(R.string.btn_register))
                }
            }
        }

        // Indicador de carga
        if (inProgress) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(
                        colorResource(R.color.progress_background)
                    )
                    .clickable(
                        interactionSource = null,
                        indication = null
                    ) {},
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}

/**
 * Menú desplegable (ExposedDropdownMenuBox de Material 3) que sustituye
 * al grupo de opciones (RadioButton). Muestra la opción seleccionada en un
 * campo de solo lectura y despliega la lista al tocarlo.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AntDropdown(
    modifier: Modifier = Modifier,
    labelRes: Int,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
            .fillMaxWidth()
            .padding(top = dimensionResource(R.dimen.common_padding_default))
    ) {
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
            readOnly = true,
            value = selectedOption,
            onValueChange = {},
            label = { Text(stringResource(labelRes)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }
        }
    }
}