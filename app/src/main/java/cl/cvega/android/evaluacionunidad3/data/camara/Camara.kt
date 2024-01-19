package cl.cvega.android.evaluacionunidad3.data.camara

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import cl.cvega.android.evaluacionunidad3.R
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.Executor
import java.util.concurrent.Executors


class Camara : AppCompatActivity() {
    private lateinit var vistaPrevia: Preview
    private lateinit var capturaImagen: ImageCapture
    private lateinit var ejecutorCamara: Executor
    private var imagenCedulaDelantera: String? = null
    private var imagenCedulaTrasera: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.vista_camara)

        val pvCamara: PreviewView = findViewById(R.id.pvCamara)
        val capturaFotoBoton: ImageButton = findViewById(R.id.capturaFoto)

        //Configurar CameraX
        vistaPrevia = Preview.Builder().build()
        capturaImagen = ImageCapture.Builder().build()

        //Ejecutor para operaciones de camara
        ejecutorCamara = Executors.newSingleThreadExecutor()

        //Vinculamos la vista previa y la captura a la pvCamara
        vistaPrevia.setSurfaceProvider(pvCamara.surfaceProvider)

        iniciarCamara()
        capturaFotoBoton.setOnClickListener{
            tomarFoto()
        }

    }

    private fun iniciarCamara() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            cameraProvider.unbindAll()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            try {
                // Configurar opciones para la salida del archivo de imagen
                val directorioSalida = getOutputDirectory() // Obtener el directorio de salida
                val formatoFecha = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
                val nombreArchivo = "${formatoFecha.format(Date())}.jpg"
                val outputFile = File(directorioSalida, nombreArchivo)

                val outputOptions = ImageCapture.OutputFileOptions.Builder(outputFile).build()

                // Vincular la cámara a la vida útil de la actividad
                cameraProvider.bindToLifecycle(this, cameraSelector, vistaPrevia, capturaImagen)
            } catch (exc: Exception) {
                Log.e("Camara", "No se pudo vincular el caso de uso de la cámara", exc)
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun tomarFoto() {
        val outputFileOptions = ImageCapture.OutputFileOptions.Builder(getOutputFile()).build()

        capturaImagen.takePicture(
            outputFileOptions, ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(error: ImageCaptureException) {
                    Log.e("Camara", "Error al capturar imagen: ${error.message}", error)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    // Guardar la ubicación de la imagen capturada
                    val savedUri = output.savedUri

                    if (savedUri != null) {
                        guardarImagenEnAlmacenamiento(savedUri)
                        // Puedes procesar la imagen guardada aquí según sea necesario
                    } else {
                        Log.e("Camara", "La URI de la imagen guardada es nula.")
                    }
                }
            }
        )
    }

    /*
    private fun iniciarCamara(){
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            cameraProvider.unbindAll()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            try {
                // Configurar opciones para la salida del archivo de imagen
                val directorioSalida = getOutputDirectory() // Obtener el directorio de salida
                val formatoFecha = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
                val nombreArchivo = "${formatoFecha.format(Date())}.jpg"
                val outputFile = File(directorioSalida, nombreArchivo)

                val outputOptions = ImageCapture.OutputFileOptions.Builder(outputFile).build()

                capturaImagen.takePicture(
                    outputOptions, ContextCompat.getMainExecutor(this),
                    object : ImageCapture.OnImageSavedCallback {
                        override fun onError(error: ImageCaptureException) {
                            Log.e("Camara", "Error al capturar imagen: ${error.message}", error)
                        }

                        override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                            // Guardar la ubicación de la imagen capturada
                            val savedUri = output.savedUri

                            if (savedUri != null){
                                guardarImagenEnAlmacenamiento(savedUri)
                                val tipoFoto = intent.getStringExtra("tipoFoto")

                                if (tipoFoto == "fotoTrasera") {
                                    imagenCedulaTrasera = savedUri.toString()
                                } else if (tipoFoto == "fotoDelantera") {
                                    imagenCedulaDelantera = savedUri.toString()
                                }

                                // Ahora puedes usar imagenCedulaTrasera o imagenCedulaDelantera según sea necesario
                            }else {
                                Log.e("Camara", "La URI de la imagen guardada es nula.")
                            }
                        }
                    }
                )

                cameraProvider.bindToLifecycle(this, cameraSelector, vistaPrevia, capturaImagen)
            } catch (exc: Exception){
                Log.e("Camara", "No se pudo vincular el caso de uso de las camara", exc)
            }
        }, ContextCompat.getMainExecutor(this))
    }

     */

    private fun guardarImagenEnAlmacenamiento(uri: Uri){
        val inputStream = contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(getOutputFile())

        inputStream?.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }
    }

    private fun getOutputFile(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        val formatoFecha = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
        val nombreArchivo = "${formatoFecha.format(Date())}.jpg"

        return if (mediaDir != null && mediaDir.exists()) {
            File(mediaDir, nombreArchivo)
        } else {
            File(filesDir, nombreArchivo)
        }
    }

    private fun getOutputDirectory(): File {
        // Crear el directorio de salida para las imágenes capturadas
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else filesDir
    }


}
