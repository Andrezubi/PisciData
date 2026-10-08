package ucb.piscidata

import android.app.Application
import ucb.piscidata.di.initKoin

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin()
    }
}
