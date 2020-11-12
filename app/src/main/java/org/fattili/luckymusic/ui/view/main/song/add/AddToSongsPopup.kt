package org.fattili.luckymusic.ui.view.main.song.add

import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import org.fattili.luckymusic.R
import org.fattili.luckymusic.data.model.play.Songs
import org.fattili.luckymusic.ui.adapter.AddToSongsListAdapter
import org.fattili.luckymusic.ui.adapter.PlayListAdapter
import org.fattili.luckymusic.ui.base.BasePopup
import org.fattili.luckymusic.ui.view.common.CommonDialog

/**
 * 添加到歌单
 */
class AddToSongsPopup(var list: MutableList<Songs>) : BasePopup() {

    var back: ImageView? = null
    private var listview: ListView? = null
    var adapter: AddToSongsListAdapter? = null

    interface PopCallBack {
        fun choice(pos: Int, songs: Songs)
    }

    var callback: PopCallBack? = null

    override val layoutId: Int = R.layout.lm_song_popup_song_add_to_songs

    override fun initView() {
        back = contentView?.findViewById(R.id.lm_song_song_add_to_songs_back_iv)
        listview = contentView?.findViewById(R.id.lm_song_song_add_to_songs_lv)
        back?.setOnClickListener { miss() }

        adapter = context?.let { AddToSongsListAdapter(it, list) }
        listview?.adapter = adapter

        listview?.setOnItemClickListener { parent, view, position, id ->
            miss()
            callback?.choice(position, list[position])
        }
    }
}