package org.fattili.luckymusic.ui.view.main.play

import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import kotlinx.android.synthetic.main.lm_play_fragment_play.*
import org.fattili.luckymusic.R
import org.fattili.luckymusic.databinding.LmPlayFragmentPlayBinding
import org.fattili.luckymusic.player.PlayManager
import org.fattili.luckymusic.player.PlayType
import org.fattili.luckymusic.ui.base.BaseFragment
import org.fattili.luckymusic.util.InjectorUtil
import org.fattili.luckymusic.util.TimeUtils


/**
 * 2020/10/28
 * @author dengzhenli
 * 播放页面
 */
class PlayFragment : BaseFragment() {


    override val layoutId: Int
        get() = R.layout.lm_play_fragment_play

    private val viewModel by lazy {
        ViewModelProvider(
            ViewModelStore(),
            InjectorUtil.getPlayModelFactory()
        ).get(PlayViewModel::class.java)
    }

    override fun initView() {
        val binding = view?.let { DataBindingUtil.bind<LmPlayFragmentPlayBinding>(it) }
        binding?.viewModel = viewModel

        setPlayModule(viewModel.getPlayType())
        val pop = PlayListPopup()
        lm_play_control_list_bt.setOnClickListener { pop.popupChoose(activity, it) }
        lm_play_gramophone_view.setOnClickListener { viewModel.changePlay() }
        lm_play_control_start_bt.setOnClickListener { viewModel.changePlay() }
        lm_play_control_pre_bt.setOnClickListener { viewModel.previous() }
        lm_play_control_next_bt.setOnClickListener { viewModel.next() }
        lm_play_control_mode_bt.setOnClickListener { setPlayModule(viewModel.changePlayType()) }
        lm_play_control_mark_bt.setOnClickListener { viewModel.changeMark() }

        lm_play_progress_seek_bar.setOnSeekBarChangeListener(object :
            OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                viewModel.playProgressStr.set(TimeUtils.formatDuration(progress))
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                viewModel.seekTo(seekBar.progress)
            }
        })

        viewModel.markState.observe(this, androidx.lifecycle.Observer { setMark(it) })
        viewModel.playState.observe(this, androidx.lifecycle.Observer { changePlayState(it) })
    }


    /**
     * 更新收藏状态
     */
    private fun setMark(mark: Boolean) {
        if (mark) {
            lm_play_control_mark_bt.setImageResource(R.drawable.lm_play_ic_favorite_yes)
        } else {
            lm_play_control_mark_bt.setImageResource(R.drawable.lm_play_ic_favorite_no)
        }
    }


    /**
     * 更新播放状态
     */
    private fun changePlayState(play: Boolean) {
        lm_play_gramophone_view.playing = play
        if (play) {
            lm_play_control_start_bt.setImageResource(R.drawable.lm_play_ic_pause)
        } else {
            lm_play_control_start_bt.setImageResource(R.drawable.lm_play_ic_play)
        }
    }

    /**
     * 更新播放模式
     */
    private fun setPlayModule(playType: PlayType) {
        when (playType) {
            PlayType.LIST -> lm_play_control_mode_bt.setImageResource(R.drawable.lm_play_ic_play_mode_list)
            PlayType.LIST_LOOP -> lm_play_control_mode_bt.setImageResource(R.drawable.lm_play_ic_play_mode_loop)
            PlayType.RANDOM -> lm_play_control_mode_bt.setImageResource(R.drawable.lm_play_ic_play_mode_random)
            PlayType.SINGLE_LOOP -> lm_play_control_mode_bt.setImageResource(R.drawable.lm_play_ic_play_mode_single)
        }
    }


    override fun onResume() {
        super.onResume()
        viewModel.setSongState()
    }

    override fun initData() {
    }


    companion object {
        fun newInstance(): PlayFragment {
            return PlayFragment()
        }
    }
}