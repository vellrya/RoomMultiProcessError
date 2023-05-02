package need.more.test.roommultiprocesserror

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.*
import need.more.test.roommultiprocesserror.databinding.ActivityMainBinding
import java.text.SimpleDateFormat

class MainActivity : AppCompatActivity() {
    var dateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss.SSS")

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch(Dispatchers.IO) {
            while (isActive) {
                val manualModel = App.INSTANCE.database.checkDao().getLatestManual()
                withContext(Dispatchers.Main) {
                    binding.manualTv.setText("Manual: "+dateFormat.format(manualModel?.ts ?: 0L))
                }
                delay(300L)
            }
        }

        App.INSTANCE.database.checkDao().getLatestLiveData().observe(this) {
            binding.liveDataTv.setText("LiveData: "+dateFormat.format(it?.ts ?: 0L))
        }

        binding.startServiceBtn.setOnClickListener {
            ContextCompat.startForegroundService(App.INSTANCE.applicationContext, Intent(App.INSTANCE.applicationContext, CheckService::class.java))
        }

        binding.stopServiceBtn.setOnClickListener {
            ContextCompat.startForegroundService(App.INSTANCE.applicationContext, Intent(App.INSTANCE.applicationContext, CheckService::class.java).apply {
                putExtra("stop", true)
            })
        }

        binding.killCurrentProcessBtn.setOnClickListener {
            android.os.Process.killProcess( android.os.Process.myPid() )
        }
    }
}