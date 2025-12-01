package gal.uvigo.workmanagerdemo

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Canvas
import android.graphics.Paint
import android.net.Uri
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.UUID
import androidx.core.net.toUri
import androidx.core.graphics.createBitmap

// TODO 03: Create the Worker class.
// This class performs the background work. It must extend CoroutineWorker (or Worker).
class ImageWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    // Override doWork() to define the actual work.
    override suspend fun doWork(): Result {
        // Read input data passed to the worker.
        val imageUriString = inputData.getString(KEY_IMAGE_URI) ?: return Result.failure()

        return withContext(Dispatchers.IO) {
            try {
                val resolver = applicationContext.contentResolver
                val imageUri = imageUriString.toUri()
                
                // 1. Read the image
                val bitmap = BitmapFactory.decodeStream(resolver.openInputStream(imageUri))
                
                // 2. Simulate heavy work
                delay(3000)

                // 3. Apply filter
                val grayBitmap = applyGrayscaleFilter(bitmap)

                // 4. Save to file
                val outputUri = saveBitmapToFile(applicationContext, grayBitmap)

                // 5. Return output
                // Create Data object to pass output back to the ViewModel/UI.
                val outputData = workDataOf(KEY_OUTPUT_URI to outputUri.toString())
                Result.success(outputData)

            } catch (e: Exception) {
                e.printStackTrace()
                Result.failure()
            }
        }
    }

    private fun applyGrayscaleFilter(src: Bitmap): Bitmap {
        val width = src.width
        val height = src.height
        val dest = createBitmap(width, height)
        
        val canvas = Canvas(dest)
        val paint = Paint()
        val colorMatrix = ColorMatrix()
        colorMatrix.setSaturation(0f) // Grayscale
        val filter = ColorMatrixColorFilter(colorMatrix)
        paint.colorFilter = filter
        canvas.drawBitmap(src, 0f, 0f, paint)
        
        return dest
    }

    private fun saveBitmapToFile(context: Context, bitmap: Bitmap): Uri {
        val filename = "filter_output_${UUID.randomUUID()}.png"
        val file = File(context.filesDir, filename)
        val out = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        out.flush()
        out.close()
        return Uri.fromFile(file)
    }

    companion object {
        const val KEY_IMAGE_URI = "KEY_IMAGE_URI"
        const val KEY_OUTPUT_URI = "KEY_OUTPUT_URI"
    }
}
