package org.fattili.luckymusic.ui.view.main.song

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
 * 歌曲列表
 */
class SongListViewModel(private val repository: SongRepository,private val songsRepository: SongsRepository) : BaseViewModel() {

    var list: MutableList<Song> = mutableListOf()
    var dataChanged = MutableLiveData<Int>()

    fun getSongList(songsId: Long) {

        launch {
            list.clear()
            list.addAll(repository.getSongList(songsId))
        }
    }

    fun addSong(song: Song) {
        repository.addSong(song)
        list.add(song)
    }

    fun getTitle(songsId: Long): String? {
        var songs = songsRepository.getSongs(songsId)
        return songs?.name
    }

    fun getSong(id: Long): Song? {
        return repository.getSong(id)
    }

    fun delete(id: Long): Boolean {
        val ret = repository.delete(id)
        if (ret) {
            list.remove(list.find { song ->  song.id == id})
            dataChanged.value = dataChanged.value?.plus(1)
        }
        return ret
    }

    private fun launch(block: suspend () -> Unit) = viewModelScope.launch {
        try {
            block()
            dataChanged.value = dataChanged.value?.plus(1)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    override fun init() {
    }


}