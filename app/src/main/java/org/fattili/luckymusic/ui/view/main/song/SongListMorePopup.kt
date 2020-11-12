package org.fattili.luckymusic.ui.view.main.song

import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import org.fattili.luckymusic.R
import org.fattili.luckymusic.ui.base.BasePopup
import org.fattili.luckymusic.ui.view.common.CommonDialog

/**
 * 歌曲操作
 */
class SongListMorePopup() : BasePopup() {

    var back: ImageView? = null
    var deleteLayout: LinearLayout? = null
    var editLayout: LinearLayout? = null
    var prePlayLayout: LinearLayout? = null
    var addToSongsLayout: LinearLayout? = null

    interface PopCallBack {
        fun delete()
        fun prePlay()
        fun edit()
        fun addToSongs()
    }

    var callback: PopCallBack? = null

    override val layoutId: Int = R.layout.lm_song_popup_song_list_more

    override fun initView() {
        back = contentView?.findViewById(R.id.lm_song_song_list_more_back_iv)
        deleteLayout = contentView?.findViewById(R.id.lm_song_song_list_more_delete_ll)
        editLayout = contentView?.findViewById(R.id.lm_song_song_list_more_edit_ll)
        prePlayLayout = contentView?.findViewById(R.id.lm_song_song_list_more_pre_play_ll)
        addToSongsLayout = contentView?.findViewById(R.id.lm_song_song_list_more_add_to_songs_ll)

        back?.setOnClickListener { miss() }
        deleteLayout?.setOnClickListener { delete() }
        editLayout?.setOnClickListener { edit() }
        prePlayLayout?.setOnClickListener { prePlay() }
        addToSongsLayout?.setOnClickListener { addToSongs() }
    }


    private fun delete() {
        miss()
        CommonDialog.Builder(context)
            .setMessage(context?.getString(R.string.lm_song_songs_operate_delete_measure_message))
            .setNegativeButton(null, null)
            .setPositiveButton(null, View.OnClickListener {
                callback?.delete()
            })
            .show()
    }

    private fun edit() {
        miss()
        callback?.edit()
    }

    private fun prePlay() {
        miss()
        callback?.prePlay()
    }
    private fun addToSongs() {
        miss()
        callback?.addToSongs()
    }

}