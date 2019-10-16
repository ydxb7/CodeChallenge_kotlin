package ai.tomorrow.codechallenge_kotlin

import ai.tomorrow.codechallenge_kotlin.datasource.MessageDatasource
import ai.tomorrow.codechallenge_kotlin.utils.DownloadCallback
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"
    private var mDownloading = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val messageDatasource = MessageDatasource(application)

        val textView = findViewById<TextView>(R.id.textView)

        messageDatasource.fetchFromNet(object : DownloadCallback {
            override fun getActiveNetworkInfo(): NetworkInfo {
                val connectivityManager =
                    getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                return connectivityManager.activeNetworkInfo
            }

            override fun updateFromDownload(result: String?) {
                textView.text = result
            }

            override fun onProgressUpdate(progressCode: Int?, percentComplete: Int?) {
                when (progressCode) {
                    // You can add UI behavior for progress updates here.
                    DownloadCallback.Progress.ERROR -> {
                    }
                    DownloadCallback.Progress.CONNECT_SUCCESS -> {
                    }
                    DownloadCallback.Progress.GET_INPUT_STREAM_SUCCESS -> {
                    }
                    DownloadCallback.Progress.PROCESS_INPUT_STREAM_IN_PROGRESS -> textView.setText("$percentComplete%")
                    DownloadCallback.Progress.PROCESS_INPUT_STREAM_SUCCESS -> {
                    }
                }
            }

            override fun finishDownloading() {
                mDownloading = false
                messageDatasource.cancelDownload()
            }

        })


    }
}
