package ai.tomorrow.codechallenge_kotlin

import ai.tomorrow.codechallenge_kotlin.databinding.ActivityMainBinding
import ai.tomorrow.codechallenge_kotlin.viewmodel.MainViewModel
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import org.koin.android.viewmodel.ext.android.viewModel


class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"
    private var mDownloading = false
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        viewModel.messages.observe(this, Observer {
            binding.textView.text = it.toString()
        })
//        val messageDatasource = MessageDatasource(application)


//        messageDatasource.fetchFromNet(200, object : DownloadCallback {
//            override fun getActiveNetworkInfo(): NetworkInfo {
//                val connectivityManager =
//                    getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
//                return connectivityManager.activeNetworkInfo
//            }
//
//            override fun updateFromDownload(result: List<DatabaseMessage>?) {
//                binding.textView.text = result.toString()
//            }
//
//            override fun onProgressUpdate(progressCode: Int?, percentComplete: Int?) {
//                when (progressCode) {
//                    // You can add UI behavior for progress updates here.
//                    DownloadCallback.Progress.ERROR -> {
//                    }
//                    DownloadCallback.Progress.CONNECT_SUCCESS -> {
//                    }
//                    DownloadCallback.Progress.GET_INPUT_STREAM_SUCCESS -> {
//                    }
//                    DownloadCallback.Progress.PROCESS_INPUT_STREAM_IN_PROGRESS -> binding.textView.setText(
//                        "$percentComplete%"
//                    )
//                    DownloadCallback.Progress.PROCESS_INPUT_STREAM_SUCCESS -> {
//                    }
//                }
//            }
//
//            override fun finishDownloading() {
//                mDownloading = false
//                messageDatasource.cancelDownload()
//            }
//
//        })


    }
}
