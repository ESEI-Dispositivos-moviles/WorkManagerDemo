package gal.uvigo.workmanagerdemo

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import kotlinx.coroutines.delay
import java.io.File
import java.io.FileOutputStream

// TODO 03: Create the DownloadWorker.
// This worker simulates a large file download and demonstrates how to handle constraints and interruptions.
class DownloadWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val filename = "game_asset.dat"
        val file = File(applicationContext.filesDir, filename)
        
        // TODO 04: Handle Resuming.
        // Check if the file already exists to resume from where we left off.
        var currentSize = if (file.exists()) file.length() else 0L
        val totalSize = 100 * 1024 * 1024L // 100MB simulated size (reduced from 2GB for demo speed)

        if (currentSize >= totalSize) {
            return Result.success(workDataOf(KEY_PROGRESS to 100))
        }

        try {
            val outputStream = FileOutputStream(file, true) // Append mode
            
            // Simulate downloading in chunks
            while (currentSize < totalSize) {
                // TODO 05: Check for Cancellation.
                // If the constraints are no longer met (e.g. charger unplugged), isStopped will be true.
                // We should stop immediately so WorkManager can reschedule us.
                if (isStopped) {
                    outputStream.close()
                    return Result.retry() // Retry later
                }

                // Simulate network delay and writing data
                delay(100) 
                val chunkSize = 1024 * 1024L // 1MB chunk
                outputStream.write(ByteArray(chunkSize.toInt())) // Write dummy data
                currentSize += chunkSize

                // TODO 06: Update Progress.
                // This allows the UI to show a progress bar.
                val progress = ((currentSize.toDouble() / totalSize) * 100).toInt()
                setProgress(workDataOf(KEY_PROGRESS to progress))
            }

            outputStream.close()
            return Result.success(workDataOf(KEY_PROGRESS to 100))

        } catch (e: Exception) {
            e.printStackTrace()
            return Result.failure()
        }
    }

    companion object {
        const val KEY_PROGRESS = "KEY_PROGRESS"
    }
}
