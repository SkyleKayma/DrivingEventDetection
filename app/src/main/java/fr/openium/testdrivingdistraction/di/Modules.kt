package fr.openium.testdrivingdistraction.di

import com.google.gson.Gson
import fr.openium.testdrivingdistraction.repository.TripRepository
import fr.openium.testdrivingdistraction.ui.detail.ViewModelDetail
import fr.openium.testdrivingdistraction.ui.detailList.ViewModelDetailList
import fr.openium.testdrivingdistraction.ui.fakeEvents.ViewModelFakeEvents
import fr.openium.testdrivingdistraction.ui.home.ViewModelHome
import fr.openium.testdrivingdistraction.ui.settings.ViewModelSettings
import fr.openium.testdrivingdistraction.utils.DateUtils
import fr.openium.testdrivingdistraction.utils.PermissionsUtils
import fr.openium.testdrivingdistraction.utils.PreferencesUtils
import fr.openium.testdrivingdistraction.utils.ShareUtils
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


/**
 * Created by Openium on 20/03/2018.
 */

object Modules {

    val utilsModule = module {
        single {
            DateUtils(androidContext())
        }

        single {
            PermissionsUtils(androidContext())
        }

        single {
            ShareUtils(androidContext())
        }

        single {
            PreferencesUtils(androidContext())
        }

        single {
            Gson()
        }
    }

    val repositoryModule = module {
        factory {
            TripRepository(get())
        }
    }

    val viewModelModule = module {
        viewModel { ViewModelHome(get()) }
        viewModel { ViewModelFakeEvents(get()) }
        viewModel { ViewModelDetailList(get()) }
        viewModel { ViewModelDetail(get()) }
        viewModel { ViewModelSettings(get()) }
    }
}