package cl.cvega.android.evaluacionunidad3.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "solicitud_table")
class SolicitudNuevaCuenta(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,
    val nombreCompleto: String,
    val rut: String,
    val fechaNacimiento: String,
    val email: String,
    val telefono: String,
    val latitud: Double,
    val longitud: Double,
    val imagenCedulaFrente: String,
    val imagenCedulaTrasera: String,
    val fechaCreacionSolicitud: String
)