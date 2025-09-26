package com.margelo.nitro.pjsipsdk.pjsip

import org.pjsip.pjsua2.LogEntry
import org.pjsip.pjsua2.LogWriter

class MyLogWriter : LogWriter() {
  override fun write(entry: LogEntry) {
    println(entry.msg)
  }
}
