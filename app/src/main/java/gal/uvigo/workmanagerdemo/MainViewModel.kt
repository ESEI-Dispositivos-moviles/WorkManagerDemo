package gal.uvigo.workmanagerdemo

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.work.Constraints
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.workDataOf

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val workManager = WorkManager.getInstance(application)
    internal val outputWorkInfos: LiveData<List<WorkInfo>> = workManager.getWorkInfosByTagLiveData(TAG_OUTPUT)

    fun applyFilter(imageUri: Uri) {
        val constraints = Constraints.Builder()
            .setRequiresStorageNotLow(true)
            .build()

        val imageWorker = OneTimeWorkRequestBuilder<ImageWorker>()
            .setInputData(workDataOf(ImageWorker.KEY_IMAGE_URI to imageUri.toString()))
            .setConstraints(constraints)
            .addTag(TAG_OUTPUT)
            .build()

        workManager.enqueue(imageWorker)
    }

    companion object {
        const val TAG_OUTPUT = "OUTPUT"
    }
}
