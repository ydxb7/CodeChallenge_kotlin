package ai.tomorrow.codechallenge_kotlin

import ai.tomorrow.codechallenge_kotlin.databinding.ActivityMainBinding
import ai.tomorrow.codechallenge_kotlin.viewmodel.MainViewModel
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import org.koin.android.viewmodel.ext.android.viewModel


class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"
    private var mDownloading = false
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this

        viewModel.messages.observe(this, Observer {
            Log.d(TAG, "message.size = ${it.size}")
            Log.d(TAG, "binding.textView = ${binding.mainTv}")
            binding.mainTv.text = it.toString()
        })


    }
}
