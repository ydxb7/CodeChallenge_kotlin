package ai.tomorrow.codechallenge_kotlin.datasource

import ai.tomorrow.codechallenge_kotlin.utils.DownloadCallback
import android.app.Application
import android.net.ConnectivityManager
import android.os.AsyncTask
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.URL
import javax.net.ssl.HttpsURLConnection

//const val CODE_CHALLENGE_URL = "https://codechallenge.secrethouse.party/"
const val CODE_CHALLENGE_URL = "https://www.google.com"

class MessageDatasource(application: Application) {

    private var mDownloadTask: DownloadTask? = null

    fun fetchFromNet(callback: DownloadCallback) {
        cancelDownload()
        mDownloadTask = DownloadTask(callback)
        (mDownloadTask as DownloadTask).execute(CODE_CHALLENGE_URL)
    }

    fun cancelDownload() {
        if (mDownloadTask != null) {
            (mDownloadTask as DownloadTask).cancel(true)
            mDownloadTask = null
        }
    }

    private inner class DownloadTask(val mCallback: DownloadCallback) :
        AsyncTask<String, Int, DownloadTask.Result>() {

        internal inner class Result {
            var mResultValue: String? = null
            var mException: Exception? = null

            constructor(resultValue: String) {
                mResultValue = resultValue
            }

            constructor(exception: Exception) {
                mException = exception
            }
        }

        override fun onPreExecute() {
            if (mCallback != null) {
                val networkInfo = mCallback.getActiveNetworkInfo()
                if (networkInfo == null || !networkInfo!!.isConnected() ||
                    networkInfo!!.getType() != ConnectivityManager.TYPE_WIFI && networkInfo!!.getType() != ConnectivityManager.TYPE_MOBILE
                ) {
                    // If no connectivity, cancel task and update Callback with null data.
                    mCallback.updateFromDownload(null)
                    cancel(true)
                }
            }
        }

        override fun doInBackground(vararg urls: String): Result? {
            var result: Result? = null
            if (!isCancelled && urls != null && urls.size > 0) {
                val urlString = urls[0]
                try {
                    val url = URL(urlString)
                    val resultString = downloadUrl(url)
                    if (resultString != null) {
                        result = Result(resultString)
                    } else {
                        throw IOException("No response received.")
                    }
                } catch (e: Exception) {
                    result = Result(e)
                }

            }
            return result
        }

        override fun onProgressUpdate(vararg values: Int?) {
            super.onProgressUpdate(*values)
            if (values.size >= 2) {
                mCallback.onProgressUpdate(values[0], values[1])
            }
        }

        override fun onPostExecute(result: Result?) {
            if (result != null && mCallback != null) {
                if (result.mException != null) {
                    mCallback.updateFromDownload(result.mException!!.message)
                } else if (result.mResultValue != null) {
                    mCallback.updateFromDownload(result.mResultValue)
                }
                mCallback.finishDownloading()
            }
        }

        override fun onCancelled(result: Result) {}

        @Throws(IOException::class)
        private fun downloadUrl(url: URL): String? {
            var stream: InputStream? = null
            var connection: HttpsURLConnection? = null
            var result: String? = null
            try {
                connection = url.openConnection() as HttpsURLConnection
                // Timeout for reading InputStream arbitrarily set to 3000ms.
                connection.readTimeout = 3000
                // Timeout for connection.connect() arbitrarily set to 3000ms.
                connection.connectTimeout = 3000
                // For this use case, set HTTP method to GET.
                connection.requestMethod = "GET"
                // Already true by default but setting just in case; needs to be true since this request
                // is carrying an input (response) body.
                connection.doInput = true
                // Open communications link (network traffic occurs here).
                connection.connect()
                publishProgress(DownloadCallback.Progress.CONNECT_SUCCESS)
                val responseCode = connection.responseCode
                if (responseCode != HttpsURLConnection.HTTP_OK) {
                    throw IOException("HTTP error code: $responseCode")
                }
                // Retrieve the response body as an InputStream.
                stream = connection.inputStream
                publishProgress(DownloadCallback.Progress.GET_INPUT_STREAM_SUCCESS, 0)
                if (stream != null) {
                    // Converts Stream to String with max length of 500.
                    result = readStream(stream, 500)
                    publishProgress(DownloadCallback.Progress.PROCESS_INPUT_STREAM_SUCCESS, 0)
                }
            } finally {
                // Close Stream and disconnect HTTPS connection.
                stream?.close()
                connection?.disconnect()
            }
            return result
        }

        @Throws(IOException::class)
        private fun readStream(stream: InputStream, maxLength: Int): String? {
            var result: String? = null
            // Read InputStream using the UTF-8 charset.
            val reader = InputStreamReader(stream, "UTF-8")
            // Create temporary buffer to hold Stream data with specified max length.
            val buffer = CharArray(maxLength)
            // Populate temporary buffer with Stream data.
            var numChars = 0
            var readSize = 0
            while (numChars < maxLength && readSize != -1) {
                numChars += readSize
                val pct = 100 * numChars / maxLength
                publishProgress(DownloadCallback.Progress.PROCESS_INPUT_STREAM_IN_PROGRESS, pct)
                readSize = reader.read(buffer, numChars, buffer.size - numChars)
            }
            if (numChars != -1) {
                // The stream was not empty.
                // Create String that is actual length of response body if actual length was less than
                // max length.
                numChars = Math.min(numChars, maxLength)
                result = String(buffer, 0, numChars)
            }
            return result
        }
    }
}

