package cl.cvega.android.evaluacionunidad3.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import cl.cvega.android.evaluacionunidad3.data.entity.SolicitudNuevaCuenta

@Dao
interface SolicitudDao {

    @Query("SELECT * FROM solicitud_table ORDER BY fechaCreacionSolicitud DESC")
    fun getTodasSolicitudes(): List<SolicitudNuevaCuenta>

    @Query("SELECT * FROM solicitud_table WHERE id = :id")
    fun getSolicitudPorId(id: Int): SolicitudNuevaCuenta?

    @Insert
    fun insertSolicitud(solicitud: SolicitudNuevaCuenta)

    @Delete
    fun deleteSolicitud(solicitud: SolicitudNuevaCuenta)

    @Update
    fun updateSolicitud(solicitud: SolicitudNuevaCuenta)
}
