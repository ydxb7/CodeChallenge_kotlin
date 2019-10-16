package ai.tomorrow.codechallenge_kotlin.viewmodel

import ai.tomorrow.codechallenge_kotlin.datasource.MessageDatasource
import ai.tomorrow.codechallenge_kotlin.model.DatabaseMessage
import ai.tomorrow.codechallenge_kotlin.utils.DownloadCallback
import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import androidx.lifecycle.ViewModel
import org.koin.core.KoinComponent
import org.koin.core.inject

class MainViewModel(
    private val application: Application
) : ViewModel(), KoinComponent {

    val messageDatasource: MessageDatasource by inject()

    val messages = messageDatasource.messages

    init {
        messageDatasource.fetchFromNet(200, object : DownloadCallback {
            override fun getActiveNetworkInfo(): NetworkInfo {
                val connectivityManager =
                    application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                return connectivityManager.activeNetworkInfo
            }

            override fun updateFromDownload(result: List<DatabaseMessage>?) {
//                binding.textView.text = result.toString()
            }

            override fun onProgressUpdate(progressCode: Int?, percentComplete: Int?) {
                when (progressCode) {
                    // You can add UI behavior for progress updates here.
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
                }
            }

            override fun finishDownloading() {
//                mDownloading = false
                messageDatasource.cancelDownload()
            }

        })
    }

}