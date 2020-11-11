package org.fattili.luckymusic.ui.view.main.song.edit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import org.fattili.luckymusic.R
import org.fattili.luckymusic.databinding.LmSongActivitySongListBinding
import org.fattili.luckymusic.databinding.LmSongActivitySongsEditBinding
import org.fattili.luckymusic.ui.base.BaseActivity
import org.fattili.luckymusic.ui.view.main.song.SongListViewModel
import org.fattili.luckymusic.util.InjectorUtil
/**
 * 2020/10/28
 * @author dengzhenli
 * 歌单编辑
 */
class SongsEditActivity : BaseActivity() {
    override val layoutId: Int = R.layout.lm_song_activity_songs_edit
    override val openBind = true

    private val binding by lazy {
        DataBindingUtil.setContentView<LmSongActivitySongsEditBinding>(
            this,
            layoutId
        )
    }

    private val viewModel by lazy {
        ViewModelProvider(
            ViewModelStore(),
            InjectorUtil.getSongsEditModelFactory()
        ).get(SongsEditViewModel::class.java)
    }

    override fun initView() {
        supportActionBar?.hide()
        binding?.viewModel = viewModel
//        setTitleName(getString(R.string.lm_songs_my_list))

    }

    override fun initData() {
    }

}