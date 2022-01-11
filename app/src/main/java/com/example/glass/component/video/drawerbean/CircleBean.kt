package com.example.glass.component.video.drawerbean

data class CircleBean(override val type: String) : DataBean(type){
    var x : Long = 0L
    var y : Long = 0L
    var radius : Long = 0L
}
