package ir.amirab.downloader.exception

abstract class DownloadValidationException(msg:String):Exception(msg){
    abstract fun isCritical():Boolean
}