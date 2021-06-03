package com.bignerdranch.android.nerdlauncher

import android.content.Intent
import android.content.pm.ResolveInfo
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bignerdranch.android.nerdlauncher.databinding.ActivityMainBinding
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"
    lateinit var mp: MediaPlayer
    private lateinit var resolvedApplist: List<ResolveInfo>
    lateinit var mainBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        val view = mainBinding.root
        setContentView(view)

        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.apply {
            // Set toolbar title/app title
            title = "Nerd Launcher"

            // Set action bar/toolbar sub title
            subtitle = "Toolbar"
        }

        resolvedApplist = packageManager
                .queryIntentActivities(Intent(Intent.ACTION_MAIN, null)
                        .addCategory(Intent.CATEGORY_LAUNCHER), 0)
        val appList = ArrayList<AppBlock>()

        for (ri in resolvedApplist) {
            if(ri.activityInfo.packageName!=this.packageName) {
                val app = AppBlock(
                        ri.loadLabel(packageManager).toString(),
                        ri.activityInfo.loadIcon(packageManager),
                        ri.activityInfo.packageName
                )
                appList.add(app)
            }
        }
        mainBinding.appList.layoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
        mainBinding.appList.adapter = Adapter(this).also {
            it.passAppList(appList.sortedWith(
                    Comparator<AppBlock> { o1, o2 ->
                        o1?.appName?.compareTo(o2?.appName ?: "", true) ?: 0;
                    }
            ))
        }
    }

    fun playSound() {
        mp = MediaPlayer.create(applicationContext, R.raw.chime)
        mp.start()
        val handler = Handler()
        handler.postDelayed({ mp.stop() }, (5 * 1000).toLong())
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.toolbar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.setting -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
            }
            R.id.exit -> {
                exitProcess(0)
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
