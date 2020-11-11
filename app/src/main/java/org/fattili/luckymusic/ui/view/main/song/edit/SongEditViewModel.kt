
package org.fattili.luckymusic.ui.view.main.song.edit

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.fattili.luckymusic.data.SongRepository
import org.fattili.luckymusic.data.SongsRepository
import org.fattili.luckymusic.data.model.play.Song
import org.fattili.luckymusic.ui.base.BaseViewModel

/**
 * 2020/10/28
 * @author dengzhenli
 * 歌曲编辑
 */
class SongEditViewModel(private val repository: SongRepository) : BaseViewModel() {

    override fun init() {
    }


}