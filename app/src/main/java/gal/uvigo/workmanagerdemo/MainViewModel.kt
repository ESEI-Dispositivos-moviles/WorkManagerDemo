package gal.uvigo.workmanagerdemo

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.workDataOf

// TODO 07: Create the ViewModel.
// This class manages the WorkManager requests and exposes the work status.
class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val workManager = WorkManager.getInstance(application)
    internal val downloadWorkInfos: LiveData<List<WorkInfo>> = workManager.getWorkInfosByTagLiveData(TAG_DOWNLOAD)

    fun scheduleDownload() {
        // TODO 08: Define Strict Constraints.
        val constraints = Constraints.Builder()
            // 1. "Don't cost me money" - Require unmetered network (Wi-Fi).
            .setRequiredNetworkType(NetworkType.UNMETERED)
            // 2. "Don't kill my phone" - Require charging.
            .setRequiresCharging(true)
            // 3. "Don't crash the system" - Check storage.
            .setRequiresStorageNotLow(true)
            .build()

        // TODO 09: Create the Download Request.
        val downloadRequest = OneTimeWorkRequestBuilder<DownloadWorker>()
            .setConstraints(constraints)
            .addTag(TAG_DOWNLOAD)
            .build()

        workManager.enqueue(downloadRequest)
    }

    companion object {
        const val TAG_DOWNLOAD = "DOWNLOAD"
    }
}
