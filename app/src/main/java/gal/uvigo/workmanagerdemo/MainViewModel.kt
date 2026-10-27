package gal.uvigo.workmanagerdemo

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Constraints
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.workDataOf
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

// TODO 04: Create the ViewModel.
// This class manages the WorkManager requests and exposes the work status.
class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val workManager = WorkManager.getInstance(application)

    // Observe the work status by Tag. This allows the UI to react to changes.
    // A StateFlow is the Compose-friendly counterpart of the previous LiveData.
    internal val outputWorkInfos: StateFlow<List<WorkInfo>> =
        workManager.getWorkInfosByTagFlow(TAG_OUTPUT)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList(),
            )

    fun applyFilter(imageUri: Uri) {

        // Define constraints (optional).
        val constraints = Constraints.Builder()
            .setRequiresStorageNotLow(true)
            .build()

        // Create the WorkRequest.
        // Pass input data and constraints here.
        val imageWorker = OneTimeWorkRequestBuilder<ImageWorker>()
            .setInputData(workDataOf(ImageWorker.KEY_IMAGE_URI to imageUri.toString()))
            .setConstraints(constraints)
            .addTag(TAG_OUTPUT) // Tag used to observe the work status
            .build()

        // Enqueue the work.
        workManager.enqueue(imageWorker)
    }

    companion object {
        const val TAG_OUTPUT = "OUTPUT"
    }
}
