package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

// --- PALETA DE COLORES MODERNA Y NEÓN SOFT ---
val VerdePrincipal = Color(0xFF00897B)
val VerdeOscuro = Color(0xFF004D40)
val VerdeClaroContraste = Color(0xFFE0F2F1)
val PurpuraAdmin = Color(0xFF6A1B9A)
val PurpuraClaroAdmin = Color(0xFFF3E5F5)

// Estatus Colores
val ColorProgramada = Color(0xFF1976D2)
val ColorEnProceso = Color(0xFFFB8C00)
val ColorCompletada = Color(0xFF2E7D32)
val ColorCancelada = Color(0xFFD32F2F)
val ColorUrgente = Color(0xFFE53935)

// 1. Modelo de datos con Fecha y Hora
data class Mascota(
    val id: Int,
    val nombre: String,
    val especie: String,
    val dueno: String,
    val motivoConsulta: String,
    val fechaCita: String,
    val horaCita: String,
    var estatus: String,
    val esUrgente: Boolean = false
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFFF8F9FA)
                ) {
                    NavegacionVeterinaria()
                }
            }
        }
    }
}

// 2. Control de navegación
@Composable
fun NavegacionVeterinaria() {
    var sesionIniciada by remember { mutableStateOf(false) }
    var usuarioActual by remember { mutableStateOf("") }
    var esAdmin by remember { mutableStateOf(false) }

    if (!sesionIniciada) {
        PantallaLogin(
            onLoginExitoso = { usuario, adminStatus ->
                usuarioActual = usuario
                esAdmin = adminStatus
                sesionIniciada = true
            }
        )
    } else {
        VeterinariaApp(
            nombreUsuario = usuarioActual,
            esAdmin = esAdmin,
            onCerrarSesion = {
                sesionIniciada = false
                usuarioActual = ""
                esAdmin = false
            }
        )
    }
}

// 3. Login
@Composable
fun PantallaLogin(onLoginExitoso: (String, Boolean) -> Unit) {
    var numeroUsuario by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var mensajeError by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(VerdePrincipal, VerdeOscuro)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(16.dp),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "🏥 VetSmart 🐾",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Black,
                    color = VerdeOscuro
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Sistema de Gestión e Innovación Médica",
                    fontSize = 12.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(28.dp))

                OutlinedTextField(
                    value = numeroUsuario,
                    onValueChange = {
                        numeroUsuario = it
                        mensajeError = ""
                    },
                    label = { Text("Número de usuario / ID") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = contrasena,
                    onValueChange = {
                        contrasena = it
                        mensajeError = ""
                    },
                    label = { Text("Contraseña (admin123 = Admin)") },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true
                )

                if (mensajeError.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = mensajeError,
                        color = ColorCancelada,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (numeroUsuario.isNotBlank() && contrasena.isNotBlank()) {
                            val esAdministrador = contrasena == "admin123"
                            onLoginExitoso(numeroUsuario, esAdministrador)
                        } else {
                            mensajeError = "Ingresa usuario y contraseña"
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = VerdePrincipal),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Text("Ingresar al Sistema", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}

// 4. Panel Principal con Selección de Fecha y Hora
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VeterinariaApp(
    nombreUsuario: String,
    esAdmin: Boolean,
    onCerrarSesion: () -> Unit
) {
    val listaMascotas = remember { mutableStateListOf<Mascota>() }
    var contadorId by remember { mutableStateOf(1) }

    // Campos del formulario
    var nombreMascota by remember { mutableStateOf("") }
    var especie by remember { mutableStateOf("") }
    var nombreDueno by remember { mutableStateOf("") }
    var motivo by remember { mutableStateOf("") }
    var fechaCitaSeleccionada by remember { mutableStateOf("") }
    var horaCitaSeleccionada by remember { mutableStateOf("") }
    var esUrgente by remember { mutableStateOf(false) }

    // Opciones de estatus
    val opcionesEstatus = listOf("Programada", "En Proceso", "Completada", "Cancelada")
    var estatusSeleccionado by remember { mutableStateOf(opcionesEstatus[0]) }
    var menuEstatusExpandido by remember { mutableStateOf(false) }

    // Buscador y Filtros
    var textoBusqueda by remember { mutableStateOf("") }
    var filtroEstatusSeleccionado by remember { mutableStateOf("Todos") }

    // Control de diálogos de Fecha y Hora
    var mostrarCalendario by remember { mutableStateOf(false) }
    var mostrarReloj by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState()
    val timePickerState = rememberTimePickerState(
        initialHour = 9,
        initialMinute = 0,
        is24Hour = false
    )

    // Diálogo DatePicker (Fecha)
    if (mostrarCalendario) {
        DatePickerDialog(
            onDismissRequest = { mostrarCalendario = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                            formatter.timeZone = TimeZone.getTimeZone("UTC")
                            fechaCitaSeleccionada = formatter.format(Date(millis))
                        }
                        mostrarCalendario = false
                    }
                ) {
                    Text("Aceptar", color = VerdePrincipal, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarCalendario = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // Diálogo TimePicker (Hora) CORREGIDO para evitar bloque negro
    if (mostrarReloj) {
        AlertDialog(
            onDismissRequest = { mostrarReloj = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val cal = Calendar.getInstance().apply {
                            set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                            set(Calendar.MINUTE, timePickerState.minute)
                        }
                        val formatter = SimpleDateFormat("hh:mm a", Locale.getDefault())
                        horaCitaSeleccionada = formatter.format(cal.time)
                        mostrarReloj = false
                    }
                ) {
                    Text("Aceptar", color = VerdePrincipal, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarReloj = false }) {
                    Text("Cancelar")
                }
            },
            text = {
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.wrapContentSize()
                ) {
                    Box(
                        modifier = Modifier
                            .padding(16.dp)
                            .wrapContentSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        TimePicker(state = timePickerState)
                    }
                }
            }
        )
    }

    // Filtro dinámico
    val mascotasFiltradas = listaMascotas.filter { mascota ->
        val coincideBusqueda = mascota.nombre.contains(textoBusqueda, ignoreCase = true) ||
                mascota.dueno.contains(textoBusqueda, ignoreCase = true) ||
                mascota.especie.contains(textoBusqueda, ignoreCase = true)

        val coincideFiltro = if (filtroEstatusSeleccionado == "Todos") true else mascota.estatus == filtroEstatusSeleccionado

        coincideBusqueda && coincideFiltro
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (esAdmin) "👑 Panel Admin VetSmart" else "👨‍⚕️ Panel Doctor VetSmart",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(
                            text = "Médico ID: $nombreUsuario",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 12.sp
                        )
                    }
                },
                actions = {
                    Button(
                        onClick = onCerrarSesion,
                        colors = ButtonDefaults.buttonColors(containerColor = ColorCancelada),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text("Salir", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if (esAdmin) PurpuraAdmin else VerdePrincipal
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF4F6F8))
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Dashboard resumido solo Admin
            if (esAdmin) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = PurpuraClaroAdmin),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("📊 Resumen en Tiempo Real", fontWeight = FontWeight.Bold, color = PurpuraAdmin, fontSize = 16.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Totales: ${listaMascotas.size}", fontWeight = FontWeight.Medium)
                                Text("Urgencias: ${listaMascotas.count { it.esUrgente }}", color = ColorUrgente, fontWeight = FontWeight.Bold)
                                Text("Completadas: ${listaMascotas.count { it.estatus == "Completada" }}", color = ColorCompletada, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Formulario Inteligente
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "⚡ Registro Rápido de Mascota",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (esAdmin) PurpuraAdmin else VerdeOscuro
                        )

                        OutlinedTextField(
                            value = nombreMascota,
                            onValueChange = { nombreMascota = it },
                            label = { Text("Nombre de la mascota") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = especie,
                                onValueChange = { especie = it },
                                label = { Text("Especie") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            OutlinedTextField(
                                value = nombreDueno,
                                onValueChange = { nombreDueno = it },
                                label = { Text("Dueño") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }

                        OutlinedTextField(
                            value = motivo,
                            onValueChange = { motivo = it },
                            label = { Text("Motivo de consulta") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        // Selector de Fecha y Hora en paralelo
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = fechaCitaSeleccionada,
                                onValueChange = { },
                                readOnly = true,
                                label = { Text("Fecha") },
                                placeholder = { Text("dd/mm/aaaa") },
                                trailingIcon = {
                                    Button(
                                        onClick = { mostrarCalendario = true },
                                        colors = ButtonDefaults.buttonColors(containerColor = VerdeClaroContraste),
                                        contentPadding = PaddingValues(horizontal = 6.dp)
                                    ) {
                                        Text("📅", fontSize = 12.sp)
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            )

                            OutlinedTextField(
                                value = horaCitaSeleccionada,
                                onValueChange = { },
                                readOnly = true,
                                label = { Text("Hora") },
                                placeholder = { Text("hh:mm AM") },
                                trailingIcon = {
                                    Button(
                                        onClick = { mostrarReloj = true },
                                        colors = ButtonDefaults.buttonColors(containerColor = VerdeClaroContraste),
                                        contentPadding = PaddingValues(horizontal = 6.dp)
                                    ) {
                                        Text("⏰", fontSize = 12.sp)
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }

                        // Switch Urgencia Médica
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 4.dp)
                        ) {
                            Text(
                                "🚨 ¿Es Urgencia Médica?",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = if (esUrgente) ColorUrgente else Color.Gray
                            )
                            Switch(
                                checked = esUrgente,
                                onCheckedChange = { esUrgente = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = ColorUrgente,
                                    checkedTrackColor = ColorUrgente.copy(alpha = 0.3f)
                                )
                            )
                        }

                        // Desplegable Estatus
                        ExposedDropdownMenuBox(
                            expanded = menuEstatusExpandido,
                            onExpandedChange = { menuEstatusExpandido = !menuEstatusExpandido }
                        ) {
                            OutlinedTextField(
                                value = estatusSeleccionado,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Estatus Inicial") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = menuEstatusExpandido) },
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )
                            ExposedDropdownMenu(
                                expanded = menuEstatusExpandido,
                                onDismissRequest = { menuEstatusExpandido = false }
                            ) {
                                opcionesEstatus.forEach { opcion ->
                                    DropdownMenuItem(
                                        text = { Text(opcion) },
                                        onClick = {
                                            estatusSeleccionado = opcion
                                            menuEstatusExpandido = false
                                        }
                                    )
                                }
                            }
                        }

                        Button(
                            onClick = {
                                if (nombreMascota.isNotBlank() && especie.isNotBlank() && fechaCitaSeleccionada.isNotBlank()) {
                                    listaMascotas.add(
                                        Mascota(
                                            id = contadorId++,
                                            nombre = nombreMascota,
                                            especie = especie,
                                            dueno = nombreDueno,
                                            motivoConsulta = motivo,
                                            fechaCita = fechaCitaSeleccionada,
                                            horaCita = if (horaCitaSeleccionada.isBlank()) "Sin hora" else horaCitaSeleccionada,
                                            estatus = estatusSeleccionado,
                                            esUrgente = esUrgente
                                        )
                                    )
                                    nombreMascota = ""
                                    especie = ""
                                    nombreDueno = ""
                                    motivo = ""
                                    fechaCitaSeleccionada = ""
                                    horaCitaSeleccionada = ""
                                    esUrgente = false
                                    estatusSeleccionado = opcionesEstatus[0]
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (esAdmin) PurpuraAdmin else VerdePrincipal
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Guardar y Agendar Cita", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                    }
                }
            }

            // Sección de Búsqueda y Filtros
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = textoBusqueda,
                        onValueChange = { textoBusqueda = it },
                        placeholder = { Text("🔍 Buscar por mascota, dueño o especie...") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        )
                    )

                    // Chips de filtro interactivos
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        val filtros = listOf("Todos", "Programada", "En Proceso", "Completada", "Cancelada")
                        items(filtros) { filtro ->
                            val seleccionado = filtroEstatusSeleccionado == filtro
                            FilterChip(
                                selected = seleccionado,
                                onClick = { filtroEstatusSeleccionado = filtro },
                                label = { Text(filtro, fontWeight = if (seleccionado) FontWeight.Bold else FontWeight.Normal) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = if (esAdmin) PurpuraAdmin else VerdePrincipal,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }
            }

            item {
                Text(
                    text = "📋 Citas Registradas (${mascotasFiltradas.size})",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = VerdeOscuro
                )
            }

            // Lista filtrada con animación
            items(mascotasFiltradas, key = { it.id }) { mascota ->
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    TarjetaMascotaInnovadora(
                        mascota = mascota,
                        esAdmin = esAdmin,
                        opcionesEstatus = opcionesEstatus,
                        onEstatusCambiado = { nuevoEstatus ->
                            val index = listaMascotas.indexOf(mascota)
                            if (index != -1) {
                                listaMascotas[index] = mascota.copy(estatus = nuevoEstatus)
                            }
                        },
                        onEliminar = { listaMascotas.remove(mascota) }
                    )
                }
            }
        }
    }
}

// 5. Tarjeta con Visualización de Fecha y Hora
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TarjetaMascotaInnovadora(
    mascota: Mascota,
    esAdmin: Boolean,
    opcionesEstatus: List<String>,
    onEstatusCambiado: (String) -> Unit,
    onEliminar: () -> Unit
) {
    var menuCambioEstatus by remember { mutableStateOf(false) }

    val colorEstatus = when (mascota.estatus) {
        "Programada" -> ColorProgramada
        "En Proceso" -> ColorEnProceso
        "Completada" -> ColorCompletada
        "Cancelada" -> ColorCancelada
        else -> VerdePrincipal
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "🐾 ${mascota.nombre}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = VerdeOscuro
                    )
                    Text(
                        text = " (${mascota.especie})",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }

                // Dropdown interactivo de estatus
                ExposedDropdownMenuBox(
                    expanded = menuCambioEstatus,
                    onExpandedChange = { menuCambioEstatus = !menuCambioEstatus }
                ) {
                    Surface(
                        color = colorEstatus.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.menuAnchor()
                    ) {
                        Text(
                            text = "${mascota.estatus} ▾",
                            color = colorEstatus,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                    ExposedDropdownMenu(
                        expanded = menuCambioEstatus,
                        onDismissRequest = { menuCambioEstatus = false }
                    ) {
                        opcionesEstatus.forEach { opcion ->
                            DropdownMenuItem(
                                text = { Text(opcion, fontWeight = FontWeight.Medium) },
                                onClick = {
                                    onEstatusCambiado(opcion)
                                    menuCambioEstatus = false
                                }
                            )
                        }
                    }
                }
            }

            // Tag de URGENCIA MEDICA
            if (mascota.esUrgente) {
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    color = ColorUrgente,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "🚨 URGENCIA MÉDICA",
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = Color(0xFFEEEEEE))
            Spacer(modifier = Modifier.height(10.dp))

            // Caja con Fecha y Hora de la Cita
            Surface(
                color = VerdeClaroContraste.copy(alpha = 0.5f),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("📅 Fecha: ${mascota.fechaCita}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = VerdeOscuro)
                    Text("⏰ Hora: ${mascota.horaCita}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = VerdeOscuro)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text("👤 Dueño: ${mascota.dueno}", fontSize = 13.sp, color = Color.DarkGray)
            if (mascota.motivoConsulta.isNotBlank()) {
                Text("📝 Motivo: ${mascota.motivoConsulta}", fontSize = 13.sp, color = Color.DarkGray)
            }

            if (esAdmin) {
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = onEliminar,
                    colors = ButtonDefaults.buttonColors(containerColor = ColorCancelada.copy(alpha = 0.1f)),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Eliminar Registro", color = ColorCancelada, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}