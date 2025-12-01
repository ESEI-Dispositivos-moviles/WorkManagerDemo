package gal.uvigo.workmanagerdemo

import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.work.WorkInfo
import coil.load
import androidx.core.net.toUri
import android.view.View
import gal.uvigo.workmanagerdemo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()
    private var currentImageUri: Uri? = null

    private lateinit var binding: ActivityMainBinding

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            currentImageUri = it
            binding.imageView.load(it)
            binding.resultImageView.visibility = View.GONE
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.buttonPick.setOnClickListener {
            pickImage.launch("image/*")
        }

        binding.buttonFilter.setOnClickListener {
            currentImageUri?.let { uri ->
                viewModel.applyFilter(uri)
            }
        }

        // TODO 06: Observe the WorkInfo.
        // This allows us to update the UI based on the state of the background work.
        viewModel.outputWorkInfos.observe(this) { workInfos ->
            if (workInfos.isNullOrEmpty()) {
                // If there is no information yet we exit the observer.
                // This line exits the lambda specifically
                return@observe
            }

            // We only have one worker in this example, so we check the first one.
            val workInfo = workInfos[0]

            if (workInfo.state.isFinished) {
                binding.progressBar.visibility = View.GONE
                if (workInfo.state == WorkInfo.State.SUCCEEDED) {
                    // Get the output data (the URI of the filtered image).
                    val outputUri = workInfo.outputData.getString(ImageWorker.KEY_OUTPUT_URI)
                    if (!outputUri.isNullOrEmpty()) {
                        binding.resultImageView.visibility = View.VISIBLE
                        binding.resultImageView.load(outputUri.toUri())
                    }
                }
            } else {
                binding.progressBar.visibility = View.VISIBLE
            }
        }
    }
}
