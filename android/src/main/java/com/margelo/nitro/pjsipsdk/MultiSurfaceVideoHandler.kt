package com.margelo.nitro.pjsipsdk

import android.view.SurfaceHolder
import org.pjsip.pjsua2.VideoWindow
import org.pjsip.pjsua2.VideoWindowHandle
import java.lang.ref.WeakReference

class MultiSurfaceVideoHandler : SurfaceHolder.Callback {
  private val holders = mutableSetOf<SurfaceHolder>()
  private var videoWindow: WeakReference<VideoWindow>? = null
  private var active = false

  fun setVideoWindow(vw: VideoWindow) {
    videoWindow = WeakReference(vw)
    active = true
    setSurfaceHolder(holders.lastOrNull())
    holders.forEach { setSurfaceHolder(it) }
  }

  fun resetVideoWindow() {
    setSurfaceHolder(null)
    active = false
    videoWindow = null
  }

  fun attach(holder: SurfaceHolder) {
    holders.add(holder)
    holder.addCallback(this)
    if (active) {
      setSurfaceHolder(holder)
    }

  }

  fun detach(holder: SurfaceHolder) {
    holders.remove(holder)
    holder.removeCallback(this)
    if (active) {
      setSurfaceHolder(holders.lastOrNull())
    }
  }

  private fun setSurfaceHolder(holder: SurfaceHolder?) {
    if (!active) return
    try {
      val wh = VideoWindowHandle()
      wh.handle.setWindow(holder?.surface)
      videoWindow?.get()?.setWindow(wh)
    } catch (e: Exception) {
      println("Error setting surface holder: $e")
    }
  }


  override fun surfaceCreated(holder: SurfaceHolder) {
    // TODO: gắn stream PJSIP vào holder
  }

  override fun surfaceDestroyed(holder: SurfaceHolder) {
    holders.remove(holder)
    setSurfaceHolder(null)
  }

  override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
    // handle resize nếu cần
    setSurfaceHolder(holder)
  }
}
