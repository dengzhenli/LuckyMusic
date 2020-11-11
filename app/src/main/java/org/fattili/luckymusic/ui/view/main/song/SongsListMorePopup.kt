package org.fattili.luckymusic.ui.view.main.song

import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import org.fattili.luckymusic.R
import org.fattili.luckymusic.data.constant.ConstantParam
import org.fattili.luckymusic.ui.base.BasePopup
import org.fattili.luckymusic.ui.view.common.CommonDialog

/**
 * 歌单操作
 */
class SongsListMorePopup(private val songsId:Long) : BasePopup() {

    var back: ImageView? = null
    var playLayout: LinearLayout? = null
    var deleteLayout: LinearLayout? = null
    var editLayout: LinearLayout? = null
    var prePlayLayout: LinearLayout? = null

    interface PopCallBack {
        fun play();
        fun delete();
        fun prePlay();
        fun edit();
    }

    var callback: PopCallBack? = null

    override val layoutId: Int = R.layout.lm_song_popup_songs_list_more

    override fun initView() {
        back = contentView?.findViewById(R.id.lm_song_songs_list_more_back_iv)
        playLayout = contentView?.findViewById(R.id.lm_song_songs_list_more_play_ll)
        deleteLayout = contentView?.findViewById(R.id.lm_song_songs_list_more_delete_ll)
        editLayout = contentView?.findViewById(R.id.lm_song_songs_list_more_edit_ll)
        prePlayLayout = contentView?.findViewById(R.id.lm_song_songs_list_more_pre_play_ll)

        if (songsId in arrayListOf(ConstantParam.SONGS_ID_LOCAL, ConstantParam.SONGS_ID_MARK)) {
            deleteLayout?.visibility = View.GONE
            editLayout?.visibility = View.GONE
        }

        back?.setOnClickListener { miss() }
        playLayout?.setOnClickListener { play() }
        deleteLayout?.setOnClickListener { delete() }
        editLayout?.setOnClickListener { edit() }
        prePlayLayout?.setOnClickListener { prePlay() }
    }

    private fun play() {
        miss()
        CommonDialog.Builder(context)
            .setMessage(context?.getString(R.string.lm_song_songs_operate_play_measure_message))
            .setNegativeButton(null, null)
            .setPositiveButton(null, View.OnClickListener {
                callback?.play() })
            .show()
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

}