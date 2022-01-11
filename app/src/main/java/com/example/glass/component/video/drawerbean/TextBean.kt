package com.example.glass.component.video.drawerbean


data class TextBean(override val type: String) : DataBean(type) {
    var x : Long = 0L
    var y : Long = 0L
    var text : String = ""
}