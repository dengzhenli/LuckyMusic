package org.fattili.luckymusic.ui.view.main.play

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.fattili.luckymusic.data.SongRepository

/**
 * 2020/10/28
 * @author dengzhenli
 * 播放页面
 */
class PlayModelFactory(private val repository: SongRepository) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return PlayViewModel(
            repository
        ) as T
    }
}