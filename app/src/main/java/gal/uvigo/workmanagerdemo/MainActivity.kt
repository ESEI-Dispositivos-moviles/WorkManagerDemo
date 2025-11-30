package gal.uvigo.workmanagerdemo

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.work.WorkInfo
import coil.load
import java.io.File

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()
    private var currentImageUri: Uri? = null

    private lateinit var imageView: ImageView
    private lateinit var resultImageView: ImageView
    private lateinit var progressBar: ProgressBar
    private lateinit var buttonPick: Button
    private lateinit var buttonFilter: Button

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            currentImageUri = it
            imageView.load(it)
            resultImageView.visibility = View.GONE
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        imageView = findViewById(R.id.imageView)
        resultImageView = findViewById(R.id.resultImageView)
        progressBar = findViewById(R.id.progressBar)
        buttonPick = findViewById(R.id.buttonPick)
        buttonFilter = findViewById(R.id.buttonFilter)

        buttonPick.setOnClickListener {
            pickImage.launch("image/*")
        }

        buttonFilter.setOnClickListener {
            currentImageUri?.let { uri ->
                viewModel.applyFilter(uri)
            }
        }

        // TODO 06: Observe the WorkInfo.
        // This allows us to update the UI based on the state of the background work.
        viewModel.outputWorkInfos.observe(this) { workInfos ->
            if (workInfos.isNullOrEmpty()) {
                return@observe
            }

            // We only have one worker in this example, so we check the first one.
            val workInfo = workInfos[0]

            if (workInfo.state.isFinished) {
                progressBar.visibility = View.GONE
                if (workInfo.state == WorkInfo.State.SUCCEEDED) {
                    // Get the output data (the URI of the filtered image).
                    val outputUri = workInfo.outputData.getString(ImageWorker.KEY_OUTPUT_URI)
                    if (!outputUri.isNullOrEmpty()) {
                        resultImageView.visibility = View.VISIBLE
                        resultImageView.load(Uri.parse(outputUri))
                    }
                }
            } else {
                progressBar.visibility = View.VISIBLE
            }
        }
    }
}