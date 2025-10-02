package com.margelo.nitro.pjsipsdk.data

// Account ID
const val ACC_ID_URI    = "sip:C03841717@C0384.talk.worldfone.cloud"
const val ACC_DOMAIN = "C0384.talk.worldfone.cloud"
const val ACC_USER   = "C03841717"
const val ACC_PASSWD = "72419633"
const val ACC_REGISTRAR = "sip:C0384.talk.worldfone.cloud"


// Peer to call
const val CALL_DST_URI  = "sip:3107@C0384.talk.worldfone.cloud"

// Camera ID used for video call.
// Use VidDevManager::enumDev2() to get available cameras & IDs.
const val VIDEO_CAPTURE_DEVICE_ID = -1

// SIP transport listening port
const val SIP_LISTENING_PORT = 0

/* Constants */
const val MSG_UPDATE_CALL_INFO      = 1
const val MSG_SHOW_REMOTE_VIDEO     = 2
const val MSG_SHOW_LOCAL_VIDEO      = 3
