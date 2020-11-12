package org.fattili.luckymusic.ui.view.main.play

import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import org.fattili.luckymusic.R
import org.fattili.luckymusic.data.base.BaseBean
import org.fattili.luckymusic.data.constant.MessageType
import org.fattili.luckymusic.data.model.play.PlaySong
import org.fattili.luckymusic.player.PlayManager
import org.fattili.luckymusic.ui.adapter.PlayListAdapter
import org.fattili.luckymusic.ui.base.BasePopup
import org.fattili.luckymusic.util.RxBus
import org.fattili.luckymusic.util.registerInBus

/**
 * 播放列表
 */
class PlayListPopup : BasePopup() {

    var back: ImageView? = null
    var listView: ListView? = null
    var exit: Button? = null
    var noItem: LinearLayout? = null

    var adapter: PlayListAdapter? = null
    var list: List<PlaySong>? = null

    override val layoutId: Int = R.layout.lm_play_popup_list
    override fun initView() {
        back = contentView?.findViewById(R.id.lm_play_list_back_iv)
        listView = contentView?.findViewById(R.id.lm_play_list_lv)
        exit = contentView?.findViewById(R.id.lm_play_list_exit_bt)
        noItem = contentView?.findViewById(R.id.lm_play_list_empty_ll)
        back?.setOnClickListener { miss() }
        exit?.setOnClickListener { miss() }
        list = PlayManager.getInstance().playList
        adapter = context?.let {
            list?.let { playlist ->
                if (playlist.isNotEmpty()) {
                    noItem?.visibility = View.GONE
                }
                PlayListAdapter(it, playlist)
            } ?: PlayListAdapter(it, arrayListOf())
        }

        listView?.adapter = adapter
        listView?.setOnItemClickListener { _, _, position, _ ->
            PlayManager.getInstance().goto(position)
            miss()
        }
        adapter?.setDeleteListener { pos ->
            PlayManager.getInstance().remove(pos)
            adapter?.upData()
        }
        register()
    }

    override fun onDismiss() {
        super.onDismiss()
        RxBus.unRegister(this)
    }

    private fun register() {
        RxBus.observe<BaseBean>()
            .subscribe { t ->
                when (t.messageType) {
                    MessageType.CHANGE_PLAY_SONGS -> {
                        adapter?.upData()
                    }
                }
            }.registerInBus(this)
    }

}