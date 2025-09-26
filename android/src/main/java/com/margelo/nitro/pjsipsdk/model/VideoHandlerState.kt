package com.margelo.nitro.pjsipsdk.model

import com.margelo.nitro.pjsipsdk.MultiSurfaceVideoHandler

data class VideoHandlerState(
  val localVideoHandler: MultiSurfaceVideoHandler = MultiSurfaceVideoHandler(),
  val remoteVideoHandler: MultiSurfaceVideoHandler = MultiSurfaceVideoHandler()
)
