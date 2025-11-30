package gal.uvigo.workmanagerdemo

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
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


    private lateinit var buttonDownload: Button
    private lateinit var textStatus: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        buttonDownload = findViewById(R.id.buttonDownload)
        textStatus = findViewById(R.id.textStatus)

        buttonDownload.setOnClickListener {
            viewModel.scheduleDownload()
        }



        // TODO 10: Observe Download Status.
        viewModel.downloadWorkInfos.observe(this) { workInfos ->
            if (workInfos.isNullOrEmpty()) {
                return@observe
            }
            val workInfo = workInfos[0]
            
            val progress = workInfo.progress.getInt(DownloadWorker.KEY_PROGRESS, 0)
            
            when (workInfo.state) {
                WorkInfo.State.ENQUEUED -> textStatus.text = "Status: Enqueued (Waiting for constraints)"
                WorkInfo.State.RUNNING -> textStatus.text = "Status: Downloading... $progress%"
                WorkInfo.State.SUCCEEDED -> textStatus.text = "Status: Download Complete!"
                WorkInfo.State.FAILED -> textStatus.text = "Status: Download Failed"
                WorkInfo.State.CANCELLED -> textStatus.text = "Status: Download Cancelled"
                WorkInfo.State.BLOCKED -> textStatus.text = "Status: Blocked"
            }
        }
    }
}