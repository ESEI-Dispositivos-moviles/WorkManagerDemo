package gal.uvigo.workmanagerdemo

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.work.WorkInfo
import coil.compose.AsyncImage
import gal.uvigo.workmanagerdemo.ui.theme.WorkManagerDemoTheme

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WorkManagerDemoTheme {
                WorkManagerDemoScreen(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun WorkManagerDemoScreen(viewModel: MainViewModel) {
    // The image picked from the gallery. rememberSaveable keeps it across configuration changes.
    var currentImageUri by rememberSaveable { mutableStateOf<Uri?>(null) }

    // registerForActivityResult is how you launch something that returns a result.
    // In this case the image picked from the photo gallery.
    val pickImage = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            currentImageUri = uri
        }
    }

    // TODO 06: Observe the WorkInfo.
    // This allows us to update the UI based on the state of the background work.
    val workInfos by viewModel.outputWorkInfos.collectAsStateWithLifecycle()

    // We only have one worker in this example, so we check the first one.
    val workInfo = workInfos.firstOrNull()
    val isRunning = workInfo != null && !workInfo.state.isFinished
    val outputUri = remember(workInfo) {
        workInfo
            ?.takeIf { it.state == WorkInfo.State.SUCCEEDED }
            ?.outputData
            ?.getString(ImageWorker.KEY_OUTPUT_URI)
            ?.takeIf { it.isNotEmpty() }
            ?.toUri()
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Input image.
            ImagePane(
                imageUri = currentImageUri,
                placeholder = "Pick an image to filter",
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            )

            Spacer(modifier = Modifier.size(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                // This is how we let the user access the photo gallery to pick a picture.
                Button(onClick = { pickImage.launch("image/*") }) {
                    Text("Pick Image")
                }
                Spacer(modifier = Modifier.width(16.dp))
                Button(
                    onClick = { currentImageUri?.let(viewModel::applyFilter) },
                    enabled = currentImageUri != null && !isRunning,
                ) {
                    Text("Apply Filter")
                }
            }

            Spacer(modifier = Modifier.size(16.dp))

            // Result image, with a progress indicator while the worker is running.
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color(0x80000000)),
                contentAlignment = Alignment.Center,
            ) {
                when {
                    isRunning -> CircularProgressIndicator()
                    outputUri != null -> AsyncImage(
                        model = outputUri,
                        contentDescription = "Result Image",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize(),
                    )
                    else -> Text(
                        text = "Filtered image will appear here",
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }
    }
}

@Composable
private fun ImagePane(
    imageUri: Uri?,
    placeholder: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center,
    ) {
        if (imageUri != null) {
            AsyncImage(
                model = imageUri,
                contentDescription = "Selected Image",
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize(),
            )
        } else {
            Text(text = placeholder)
        }
    }
}
