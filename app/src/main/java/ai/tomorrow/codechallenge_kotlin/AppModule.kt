package ai.tomorrow.codechallenge_kotlin


import ai.tomorrow.codechallenge_kotlin.datasource.MessageDatasource
import ai.tomorrow.codechallenge_kotlin.message.MessageViewModel
import ai.tomorrow.codechallenge_kotlin.repository.MessageRepository
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    // MyViewModel ViewModel
    viewModel { MessageViewModel(get()) }
//    viewModel { SendTransactionViewModel(get()) }
//    viewModel { ImportWalletViewModel(get()) }
//
    single { MessageDatasource(get()) }
    single { MessageRepository(get()) }
//    single { TransactionRepository(get()) }
//
//    single { Web3jDatasource(get()) }
//    single { Web3jRepository(get()) }
//
//    single { WalletDatasource(get()) }
//    single { WalletRepository(get()) }
}