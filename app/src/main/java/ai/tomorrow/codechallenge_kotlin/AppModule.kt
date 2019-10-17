package ai.tomorrow.codechallenge_kotlin


import ai.tomorrow.codechallenge_kotlin.datasource.MessageDatasource
import ai.tomorrow.codechallenge_kotlin.message.MessageViewModel
import ai.tomorrow.codechallenge_kotlin.repository.MessageRepository
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    viewModel { MessageViewModel(get()) }
    single { MessageDatasource(get()) }
    single { MessageRepository(get()) }
}