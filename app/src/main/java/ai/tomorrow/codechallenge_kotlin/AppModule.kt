package ai.tomorrow.codechallenge_kotlin


import ai.tomorrow.codechallenge_kotlin.datasource.MessageDatasource
import ai.tomorrow.codechallenge_kotlin.viewmodel.MainViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    // MyViewModel ViewModel
    viewModel { MainViewModel(get()) }
//    viewModel { SendTransactionViewModel(get()) }
//    viewModel { ImportWalletViewModel(get()) }
//
    single { MessageDatasource(get()) }
//    single { TransactionRepository(get()) }
//
//    single { Web3jDatasource(get()) }
//    single { Web3jRepository(get()) }
//
//    single { WalletDatasource(get()) }
//    single { WalletRepository(get()) }
}