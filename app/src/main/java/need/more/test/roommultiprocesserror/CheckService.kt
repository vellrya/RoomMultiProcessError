package need.more.test.roommultiprocesserror

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.util.*
import java.util.concurrent.Executors

class CheckService: Service() {

    var checkJob: Job? = null
    val dispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        manageNotification()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        manageNotification()
        if (intent?.hasExtra("stop")==true) {
            stopCheckService()
        } else {
            startCheckThread()
        }
        return START_STICKY
    }

    fun createNotification(): Notification {
        val NOTIFICATION_CHANNEL_ID = "check.service"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channelName = "Check service"
            val chan = NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_DEFAULT)
            chan.lightColor = Color.BLUE
            chan.setSound(null, null)
            chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            manager.createNotificationChannel(chan)
        }

        val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID).setContentTitle("Check service").setContentText("Check service")
        return notificationBuilder.setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setOnlyAlertOnce(true)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setCategory(Notification.CATEGORY_SERVICE)
            .build()
    }

    @SuppressLint("NotificationId0")
    fun manageNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForeground(999, createNotification())
        } else {
            startForeground(0, Notification())
        }
    }

    override fun onDestroy() {
        checkJob?.cancel()
        checkJob = null
        super.onDestroy()
    }

    fun startCheckThread() {
        if (checkJob!=null) {
            return
        }

        checkJob = App.INSTANCE.applicationScope.launch(dispatcher) {
            while (isActive) {
                delay(1000L)
                App.INSTANCE.database.checkDao().insert(CheckModel(System.currentTimeMillis()))
            }
        }
    }

    private fun stopCheckService() {
        checkJob?.cancel()
        checkJob = null
        if (Build.VERSION.SDK_INT<33) {
            stopForeground(true)
        } else {
            stopForeground(STOP_FOREGROUND_REMOVE)
        }
        stopSelf()
    }

}