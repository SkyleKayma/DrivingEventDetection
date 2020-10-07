package fr.openium.testdrivingdistraction

import android.app.Application
import fr.openium.testdrivingdistraction.di.Modules
import io.realm.Realm
import io.realm.RealmConfiguration
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class CustomApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        initKoin()
        initRealm()
    }

    private fun initKoin() {
        startKoin {
            androidContext(this@CustomApplication)
            modules(
                listOf(
                    Modules.utilsModule,
                    Modules.repositoryModule,
                    Modules.viewModelModule
                )
            )
        }
    }

    private fun initRealm() {
        Realm.init(this)
        Realm.setDefaultConfiguration(initRealmConfiguration(RealmConfiguration.Builder()).build())
    }

    private fun initRealmConfiguration(builder: RealmConfiguration.Builder): RealmConfiguration.Builder =
        builder.deleteRealmIfMigrationNeeded()
}