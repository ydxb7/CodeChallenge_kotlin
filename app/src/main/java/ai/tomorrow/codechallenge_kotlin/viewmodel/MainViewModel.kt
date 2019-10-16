package ai.tomorrow.codechallenge_kotlin.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import org.koin.core.KoinComponent

class MainViewModel(
    private val application: Application
) : ViewModel(), KoinComponent {
    

}