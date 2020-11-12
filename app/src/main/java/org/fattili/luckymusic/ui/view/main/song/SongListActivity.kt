package org.fattili.luckymusic.ui.view.main.song

import android.media.MediaMetadataRetriever
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.recyclerview.widget.LinearLayoutManager
import com.jcodecraeer.xrecyclerview.ProgressStyle
import com.jcodecraeer.xrecyclerview.XRecyclerView
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.lm_common_title.*
import kotlinx.android.synthetic.main.lm_song_activity_song_list.*
import org.fattili.file_select.ChooseFileMultiple
import org.fattili.luckymusic.R
import org.fattili.luckymusic.data.base.BaseBean
import org.fattili.luckymusic.data.constant.MessageType
import org.fattili.luckymusic.data.constant.ShowMsg
import org.fattili.luckymusic.databinding.LmSongActivitySongListBinding
import org.fattili.luckymusic.player.PlayManager
import org.fattili.luckymusic.ui.adapter.SongListAdapter
import org.fattili.luckymusic.ui.base.BaseActivity
import org.fattili.luckymusic.ui.view.main.song.edit.SongEditActivity
import org.fattili.luckymusic.util.InjectorUtil
import org.fattili.luckymusic.util.RxBus
import org.fattili.luckymusic.util.SongUtil
import org.fattili.luckymusic.util.registerInBus
import java.io.File
import java.util.*

/**
 * 2020/10/28
 * @author dengzhenli
 * 歌曲列表
 */
class SongListActivity : BaseActivity() {

    private lateinit var itemAdapter: SongListAdapter
    override val layoutId: Int = R.layout.lm_song_activity_song_list
    override val openBind = true
    private val binding by lazy {
        DataBindingUtil.setContentView<LmSongActivitySongListBinding>(
            this,
            layoutId
        )
    }
    private val viewModel by lazy {
        ViewModelProvider(
            ViewModelStore(),
            InjectorUtil.getSongModelFactory()
        ).get(SongListViewModel::class.java)
    }

    private lateinit var chooseFileMultiple: ChooseFileMultiple
    var songsId: Long = 0
    override fun initView() {
        supportActionBar?.hide()

        binding?.viewModel = viewModel
        setTitleName(getString(R.string.lm_songs_my_list))
        initFileChoice()
        initRcyView()
        lm_common_add_iv.visibility = View.VISIBLE
        lm_common_add_iv.setOnClickListener { view ->
            chooseFileMultiple.popupChoose(
                this@SongListActivity,
                view,
                window,
                layoutInflater,
                true
            )
        }
        register()
    }

    override fun initData() {
        songsId = intent.getLongExtra("songsId", 0)
        viewModel.getTitle(songsId)?.let { setTitleName(it) }
        viewModel.getSongList(songsId)
        viewModel.dataChanged.observe(this, androidx.lifecycle.Observer {
            if (viewModel.list.isEmpty()) {
                lm_song_song_no_item_ll.visibility = View.VISIBLE
                lm_song_song_list.visibility = View.GONE
            } else {
                lm_song_song_no_item_ll.visibility = View.GONE
                lm_song_song_list.visibility = View.VISIBLE
            }
            itemAdapter.update()
            lm_song_song_list.refreshComplete()
        })
    }

    private fun initFileChoice() {
        chooseFileMultiple = ChooseFileMultiple()
        chooseFileMultiple.chooseFileBack = object : ChooseFileMultiple.OnChooseFileBack {
            override fun onChooseBack(paths: ArrayList<String?>?) {
                if (paths != null) {
                    var mmr = MediaMetadataRetriever()
                    for (path in paths) {
                        if (path != null) {
                            val file = File(path)
                            var song = SongUtil.getSong(songsId, mmr, file)
                            viewModel.addSong(song)
                        }
                    }
                    itemAdapter.update()
                }
            }
        }
        chooseFileMultiple.whiteList = arrayListOf("mp3", "mwa")
    }


    private fun initRcyView() {
        itemAdapter = SongListAdapter(viewModel.list)

        itemAdapter.clickListener = {}
        lm_song_song_list.adapter = itemAdapter

        lm_song_song_list.layoutManager = LinearLayoutManager(this)
        lm_song_song_list.defaultRefreshHeaderView.setRefreshTimeVisible(true)//显示刷新时间
        lm_song_song_list.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader) //下拉进度条属性
        lm_song_song_list.setLoadingMoreProgressStyle(ProgressStyle.BallSpinFadeLoader)//上拉进度条属性
        lm_song_song_list.setLoadingMoreEnabled(false)//禁用上拉
        lm_song_song_list.setLoadingListener(object : XRecyclerView.LoadingListener {
            override fun onLoadMore() {}

            override fun onRefresh() {
                //下拉刷新
                try {
                    viewModel.getSongList(songsId)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })

        itemAdapter.setPlayClickListener { pos ->
            val id = itemAdapter.getItem(pos)?.id ?: 0
            if (id == PlayManager.getInstance().getPlaySong()?.songId) {
                if (PlayManager.getInstance().getPlaying()){
                    PlayManager.getInstance().pause()
                }else{
                    PlayManager.getInstance().play()
                }
            } else {
                val song = viewModel.getSong(id)
                song?.let {
                    PlayManager.getInstance().addSong(
                        song.castSongPlay(),
                        PlayManager.getInstance().getCurrentIndex()
                    )
                }
                PlayManager.getInstance().goto(PlayManager.getInstance().getCurrentIndex())
            }

        }

        itemAdapter.setMoreClickListener { pos ->
            val id = itemAdapter.getItem(pos)?.id ?: 0
            val pop = SongListMorePopup()
            pop.callback = object : SongListMorePopup.PopCallBack {
                override fun delete() {
                    if (viewModel.delete(id)) {
                        showMsg(ShowMsg.msg_delete_ok)
                    } else {
                        showMsg(ShowMsg.msg_delete_fail)
                    }
                }

                override fun prePlay() {
                    val song = viewModel.getSong(id)
                    song?.let {
                        PlayManager.getInstance().addSong(
                            song.castSongPlay(),
                            PlayManager.getInstance().getCurrentIndex() + 1
                        )
                    }
                    showMsg(ShowMsg.msg_add_playlist_ok)
                }

                override fun edit() {
                    SongEditActivity.actionStart(this@SongListActivity, id)
                }

            }
            pop.popupChoose(this, lm_song_song_list)
        }

    }

    /**
     * 注册获取到数据
     */
    private fun register() {

        RxBus.observe<BaseBean>()
            .subscribe { t ->
                when (t.messageType) {
                    MessageType.UPDATE_SONG -> {
                        viewModel.getSongList(songsId)
                    }
                    MessageType.CHANGE_PLAY_STATE -> {
                        val songId = t.msg?.toLong()
                        itemAdapter.notifyDataSetChanged()
                    }
                }
            }.registerInBus(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        RxBus.unRegister(this)
    }
}