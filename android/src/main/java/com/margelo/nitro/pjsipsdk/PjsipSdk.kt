package com.margelo.nitro.pjsipsdk
  
import com.facebook.proguard.annotations.DoNotStrip

@DoNotStrip
class PjsipSdk : HybridPjsipSdkSpec() {
  override fun multiply(a: Double, b: Double): Double {
    return a * b
  }
}
