package io.github.tuanictu97.codedynamic

class CodeLoadLoadingController {
    companion object {
      init {
         System.loadLibrary("main")
      }
    }

    external fun getDemoCodeLoading() : String?
}