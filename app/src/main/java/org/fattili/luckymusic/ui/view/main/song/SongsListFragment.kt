package org.fattili.luckymusic.ui.view.main.song

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.lm_song_fragment_songs_list.*
import org.fattili.luckymusic.R
import org.fattili.luckymusic.data.constant.ShowMsg
import org.fattili.luckymusic.data.model.play.PlaySong
import org.fattili.luckymusic.player.PlayManager
import org.fattili.luckymusic.ui.adapter.SongsListAdapter
import org.fattili.luckymusic.ui.base.BaseFragment
import org.fattili.luckymusic.ui.view.main.song.add.AddSongsDialog
import org.fattili.luckymusic.util.InjectorUtil

/**
 * 2020/10/28
 * @author dengzhenli
 * 歌单列表
 */
class SongsListFragment : BaseFragment() {

    private lateinit var itemAdapter: SongsListAdapter

    override val layoutId: Int
        get() = R.layout.lm_song_fragment_songs_list

    private val viewModel by lazy {
        ViewModelProvider(
            this,
            InjectorUtil.getSongsModelFactory()
        ).get(SongsListViewModel::class.java)
    }

    override fun initView() {
        initList()
        lm_song_songs_list_add_bt.setOnClickListener { showAddDialog() }
    }

    override fun initData() {
        viewModel.getSongsList()
        viewModel.dataChanged.observe(this, androidx.lifecycle.Observer {
            itemAdapter.update()
        })
    }

    private fun initList() {
        itemAdapter = SongsListAdapter(viewModel.list)

        itemAdapter.clickListener = {
            var intent = Intent(context, SongListActivity::class.java)
            intent.putExtra("songsId", itemAdapter.getItem(it)?.id)
            startActivity(intent)
        }
        itemAdapter.setMoreClickListener { pos ->
            val id = itemAdapter.getItem(pos)?.id ?: 0
            val pop = SongsListMorePopup(id)
            pop.callback = object : SongsListMorePopup.PopCallBack {
                override fun play() {
                    val songs = viewModel.getSongs(id)
                    val list = songs?.songList
                    list?.let {
                        PlayManager.getInstance()
                            .updatePlayList(PlaySong.cast(it) as MutableList<PlaySong>)
                    }
                }

                override fun delete() {
                    if (viewModel.delete(id) > 0) {
                        showMsg(ShowMsg.msg_delete_ok)
                    } else {
                        showMsg(ShowMsg.msg_delete_fail)
                    }
                }

                override fun prePlay() {
                    val songs = viewModel.getSongs(id)
                    val list = songs?.songList
                    list?.let {
                        PlayManager.getInstance()
                            .addPlayList(PlaySong.cast(it) as MutableList<PlaySong>)
                    }
                    showMsg(ShowMsg.msg_add_playlist_ok)
                }

                override fun edit() {
                }

            }
            pop.popupChoose(activity, view)
        }
        lm_song_songs_list.layoutManager = LinearLayoutManager(context)
        lm_song_songs_list.adapter = itemAdapter

    }

    private fun showAddDialog() {
        var dialog = AddSongsDialog(context!!)
        dialog.dialogResult = object : AddSongsDialog.DialogResult {
            override fun addResult(name: String) {
                val ret = viewModel.add(name)
                if (ret) {
                    showMsg("添加成功")
                    dialog.dismiss()
                } else {
                    showMsg("添加失败")
                }
            }
        }
        dialog.show()
    }


    companion object {
        fun newInstance(): SongsListFragment {
            return SongsListFragment()
        }
    }
}