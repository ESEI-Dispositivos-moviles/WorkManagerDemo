package gal.uvigo.workmanagerdemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.work.WorkInfo
import gal.uvigo.workmanagerdemo.ui.theme.WorkManagerDemoTheme

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WorkManagerDemoTheme {
                DownloadScreen(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun DownloadScreen(viewModel: MainViewModel) {
    // TODO 10: Observe Download Status.
    val workInfos by viewModel.downloadWorkInfos.collectAsStateWithLifecycle()

    // We only have one worker in this example, so we check the first one.
    val workInfo = workInfos.firstOrNull()
    val progress = workInfo?.progress?.getInt(DownloadWorker.KEY_PROGRESS, 0) ?: 0
    val statusText = statusTextFor(workInfo, progress)

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Button(onClick = { viewModel.scheduleDownload() }) {
                Text("Download Asset (2GB)")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = statusText,
                style = MaterialTheme.typography.bodyLarge,
            )

            if (workInfo?.state == WorkInfo.State.RUNNING) {
                Spacer(modifier = Modifier.height(16.dp))
                LinearProgressIndicator(
                    progress = { progress / 100f },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

private fun statusTextFor(workInfo: WorkInfo?, progress: Int): String =
    when (workInfo?.state) {
        null -> "Status: Idle"
        WorkInfo.State.ENQUEUED -> "Status: Enqueued (Waiting for constraints)"
        WorkInfo.State.RUNNING -> "Status: Downloading... $progress%"
        WorkInfo.State.SUCCEEDED -> "Status: Download Complete!"
        WorkInfo.State.FAILED -> "Status: Download Failed"
        WorkInfo.State.CANCELLED -> "Status: Download Cancelled"
        WorkInfo.State.BLOCKED -> "Status: Blocked"
    }
