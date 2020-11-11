
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
 * 歌单编辑
 */
class SongsEditViewModel(private val songsRepository: SongsRepository) : BaseViewModel() {

    override fun init() {
    }


}