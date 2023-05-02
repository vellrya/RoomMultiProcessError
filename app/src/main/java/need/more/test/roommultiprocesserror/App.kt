package need.more.test.roommultiprocesserror

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob


class App : Application() {

    lateinit var database: AppDatabase
    val applicationScope = CoroutineScope(SupervisorJob())

    companion object {
        @Volatile
        lateinit var INSTANCE: App
            private set
    }


    override fun onCreate() {
        INSTANCE = this
        database = AppDatabase.getDatabase(this)

        super.onCreate()
    }
}