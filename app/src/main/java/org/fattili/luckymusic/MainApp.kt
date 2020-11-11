package org.fattili.luckymusic

import android.app.Application
import android.content.Context
import org.fattili.luckymusic.player.PlayManager
import org.litepal.LitePal

/**
 * 2020/10/28
 * @author dengzhenli
 *
 */
class MainApp :Application(){
    override fun onCreate() {
        super.onCreate()
        context = this
        instance = this

        LitePal.initialize(this)
        initPlay()
    }

    private fun initPlay(){
        PlayManager.getInstance().init()
    }

    fun onExit(){
        PlayManager.getInstance().exit()
    }


    companion object {
        lateinit var context: Context
        lateinit var instance: MainApp
    }

}