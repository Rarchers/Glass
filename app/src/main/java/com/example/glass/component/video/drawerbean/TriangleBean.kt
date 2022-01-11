package com.example.glass.component.video.drawerbean

data class TriangleBean(override val type: String) : DataBean(type){
    var tlx : Long = 0L
    var tly : Long = 0L
    var blx : Long = 0L
    var bly : Long = 0L
    var brx : Long = 0L
    var bry : Long = 0L
}
