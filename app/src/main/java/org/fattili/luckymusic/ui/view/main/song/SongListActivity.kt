package org.fattili.luckymusic.ui.view.main.song

import android.media.MediaMetadataRetriever
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.recyclerview.widget.LinearLayoutManager
import com.jcodecraeer.xrecyclerview.ProgressStyle
import com.jcodecraeer.xrecyclerview.XRecyclerView
import kotlinx.android.synthetic.main.lm_common_title.*
import kotlinx.android.synthetic.main.lm_song_activity_song_list.*
import org.fattili.file_select.ChooseFileMultiple
import org.fattili.luckymusic.R
import org.fattili.luckymusic.data.constant.ShowMsg
import org.fattili.luckymusic.databinding.LmSongActivitySongListBinding
import org.fattili.luckymusic.player.PlayManager
import org.fattili.luckymusic.ui.adapter.SongListAdapter
import org.fattili.luckymusic.ui.base.BaseActivity
import org.fattili.luckymusic.util.InjectorUtil
import org.fattili.luckymusic.util.SongUtil
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

    private var page = 0
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
        //设置显示刷新时间
        lm_song_song_list.defaultRefreshHeaderView.setRefreshTimeVisible(true)
        //设置下拉等待进度条属性
        lm_song_song_list.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader)
        //设置上拉加载更多进度条属性
        lm_song_song_list.setLoadingMoreProgressStyle(ProgressStyle.BallSpinFadeLoader)
        //是否禁用上拉加载更多
        lm_song_song_list.setLoadingMoreEnabled(true)
        //*设定下拉刷新和上拉加载监听
        lm_song_song_list.setLoadingListener(object : XRecyclerView.LoadingListener {
            override fun onLoadMore() {
                viewModel.getSongList(songsId)
            }

            override fun onRefresh() {
                //下拉刷新
                try {
                    page = 1
                    viewModel.getSongList(songsId)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })

        itemAdapter.setPlayClickListener {  pos ->
            val id = itemAdapter.getItem(pos)?.id ?: 0
            val song = viewModel.getSong(id)
            song?.let {
                PlayManager.getInstance().addSong(
                    song.castSongPlay(),
                    PlayManager.getInstance().getCurrentIndex()
                )
            }
            PlayManager.getInstance().pause()
            PlayManager.getInstance().play()

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
                }

            }
            pop.popupChoose(this, lm_song_song_list)
        }

    }


}