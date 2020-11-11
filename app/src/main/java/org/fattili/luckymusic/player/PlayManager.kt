package org.fattili.luckymusic.player

import org.fattili.luckymusic.data.constant.ShowMsg
import org.fattili.luckymusic.data.model.play.PlaySong
import org.fattili.luckymusic.util.ArrayUtil
import org.fattili.luckymusic.util.Logger
import org.fattili.luckymusic.util.ToastUtil
import kotlin.random.Random


/**
 * 2020/10/28
 * @author dengzhenli
 * 播放管理
 */
class PlayManager private constructor():Player.PlayCallBack {
    var playList: MutableList<PlaySong>? = null
    var playType: PlayType
    var player: Player? = Player()
    private var currentIndex = 0

    var playListener: PlayListener? = null

    var playLooking = false //歌曲观察中，包含暂停与播放

    /**—————————————————————————————————————播放器操作—————————————————————————————————————————————*/
    fun play(): Boolean {
        if (player == null) {
            Logger.e("player is NULL!!")
            ToastUtil.show(ShowMsg.msg_play_fail_app)
            return false
        }
        notifyPlaySongUpdate()
        return if (playLooking) {
            player?.continuePlay()
            true
        } else {
            playLooking = true
            if (player?.play() == true) {
                true
            } else {
                ToastUtil.show(ShowMsg.msg_play_fail)
                false
            }
        }
    }

    fun complete() {
        if (playType == PlayType.LIST && currentIndex == (playList?.size ?: 0) - 1) {
            playListener?.onPlayerPause()
            return
        }
        next()
    }


    fun pause() {
        if (player == null) {
            Logger.e("player is NULL!!")
            return
        }
        player?.pause()
    }

    fun next() {
        if (player == null) {
            Logger.e("player is NULL!!")
            ToastUtil.show(ShowMsg.msg_app)
            return
        }
        if (playList == null) {
            Logger.e("playList is NULL!!")
            ToastUtil.show(ShowMsg.msg_app)
            return
        }
        pause()
        val n = playList?.size ?: 0
        if (n > 0) {
            when (playType) {
                PlayType.SINGLE_LOOP -> {
                }
                PlayType.LIST_LOOP -> {
                    currentIndex++
                    currentIndex %= n
                    if (currentIndex in 0 until n) {
                        player?.playSong = playList!![currentIndex]
                    }
                }
                PlayType.LIST -> {
                    if (currentIndex != n - 1) currentIndex++

                    if (currentIndex in 0 until n) {
                        player?.playSong = playList!![currentIndex]
                    }else{
                        currentIndex = n - 1
                    }
                }

                PlayType.RANDOM -> player?.playSong = playList!![Random(n.toLong()).nextInt()]
            }
        }
        playLooking = false
        play()

    }

    fun previous() {
        if (player == null) {
            Logger.e("player is NULL!!")
            ToastUtil.show(ShowMsg.msg_app)
            return
        }
        if (playList == null) {
            Logger.e("playList is NULL!!")
            ToastUtil.show(ShowMsg.msg_app)
            return
        }
        pause()
        val n = playList?.size?:0
        if (n > 0) {
            when (playType) {
                PlayType.SINGLE_LOOP -> {
                }
                PlayType.LIST_LOOP -> {
                    currentIndex--
                    currentIndex %= n
                    if (currentIndex in 0 until n) {
                        player?.playSong = playList!![currentIndex]
                    }
                }
                PlayType.LIST -> {
                    if (currentIndex != 0) currentIndex--

                    if (currentIndex in 0 until n) {
                        player?.playSong = playList!![currentIndex]
                    }else{
                        currentIndex = 0
                    }
                }
                PlayType.RANDOM -> player?.playSong = playList!![Random(n.toLong()).nextInt()]
            }
        }
        playLooking = false
        play()
    }

    fun goto(index: Int) {
        pause()
        playLooking = false
        currentIndex = index
        player?.playSong = playList?.get(currentIndex)
        play()
    }

    fun seekTo(progress: Int): Boolean {
        if (player == null) {
            Logger.e("player is NULL!!")
            ToastUtil.show(ShowMsg.msg_app)
            return false
        }
        return player?.seekTo(progress) ?: false
    }

    /** —————————————————————————————————————播放回调————————————————————————————————————————————— */
    override fun onCompletion(song: PlaySong?) {
        playLooking = false
        complete()
    }

    override fun onPause(song: PlaySong?) {
        playListener?.onPlayerPause()
    }

    override fun onPlay(song: PlaySong?) {
        playListener?.onPlayerPlay()
    }
    /** —————————————————————————————————————参数获取————————————————————————————————————————————— */

    fun getDuration(): Int {
        return player?.duration ?: 0
    }

    fun getProgress(): Int {
        return player?.progress ?: 0
    }

    fun getPlaySong(): PlaySong? {
        return player?.playSong
    }

    fun getPlaying(): Boolean {
        return player?.playing ?: false
    }

    fun changePlayType(type: PlayType) {
        playType = type
        PlayDataManager.writePlayType(type)
    }

    fun changePlayType(): PlayType {
        changePlayType(playType.next())
        return playType
    }


    fun getCurrentIndex(): Int {
        return currentIndex + 1
    }


    /**—————————————————————————————————————播放队列—————————————————————————————————————————————*/
    fun remove(pos: Int) {
        playList?.removeAt(pos)
        if (pos < currentIndex) currentIndex--
        notifyPlayListUpdate()
    }


    fun updatePlayList(playList: MutableList<PlaySong>) {
        this.playList = playList
        currentIndex = 0
        pause()
        playLooking = false
        play()
        notifyPlayListUpdate()
    }


    fun addPlayList(playList: MutableList<PlaySong>) {
        this.playList?.addAll(playList)
        notifyPlayListUpdate()
    }

    fun addSong(playSong: PlaySong) {
        this.playList?.add(playSong)
        notifyPlayListUpdate()
    }

    fun addSong(playSong: PlaySong, index:Int) {
        this.playList?.add(index, playSong)
        notifyPlayListUpdate()
    }

    /**—————————————————————————————————————管理器—————————————————————————————————————————————*/

    init {
        playType = PlayType.LIST
    }

    fun init(type: PlayType, playList: MutableList<PlaySong>, target: String, progress: Int) {
        this.playList = playList
        val index = PlayDataManager.getIndex(target, playList)
        currentIndex = ArrayUtil.getEnableIndex(index, playList.size)
        playType = type
        if (currentIndex >= 0) {
            player?.init(playList[currentIndex])
        }

        player?.playCallback = this
    }


    fun init() {
        val playData = PlayDataManager.read()
        init(playData.playType, playData.playList, playData.target, playData.progress)
    }


    fun exit() {
        val playData = PlayData()
        playData.target = getPlaySong()?.target ?: ""
        playData.progress = getProgress()
        playData.playType = playType
        playData.playList = playList ?: arrayListOf()
        PlayDataManager.write(playData)
        player?.pause()
    }

    private fun notifyPlayListUpdate(){
        playList?.let { PlayDataManager.writePlayList(it) }
    }

    private fun notifyPlaySongUpdate(){
        getPlaySong()?.target?.let { PlayDataManager.writeTarget(it) }
    }

    companion object {
        private var instance: PlayManager? = null
        fun getInstance(): PlayManager {
            if (instance == null) {
                synchronized(PlayManager::class.java) {
                    if (instance == null) {
                        instance = PlayManager()
                    }
                }
            }
            return instance!!
        }
    }

}