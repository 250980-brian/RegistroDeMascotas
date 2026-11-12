package com.utxicotepec.registrodemascotas

import android.content.Context
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

fun mascotasFormatter(mascotas: Mascotas): String {
    val result = StringBuilder()
    result.append("Nombre de la mascota: ${mascotas.nombre} \n")
    result.append("Nombre del dueño: ${mascotas.apellidos} \n")
    result.append("Peso: ${mascotas.peso} kg\n")
    result.append("Fecha de nacimiento: ${convertMillisToDate(mascotas.fechaNacimiento)}\n")
    result.append("Especie: ${mascotas.tipoDonacion}\n")
    result.append("Señas Particulares: ${mascotas.antecedentes}")

    return result.toString()
}

fun convertMillisToDate(millis: Long): String {
    val dateFormatter = DateTimeFormatter
        .ofLocalizedDate(FormatStyle.SHORT)
        .withLocale(Locale.getDefault())

    return LocalDateTime.ofInstant(
        Instant.ofEpochMilli(millis),
        ZoneId.of("UTC")
    ).format(dateFormatter)
}

fun foundErrors(context: Context,
                nombre: String,
                apellido: String,
                peso: String): String? {
    return if (nombre.isBlank() || apellido.isBlank() ||
        (peso.toIntOrNull() ?: 0) < context.resources.getInteger(R.integer.height_min_value)) {
        context.getString(R.string.form_invalid)
    } else {
        return null
    }
}