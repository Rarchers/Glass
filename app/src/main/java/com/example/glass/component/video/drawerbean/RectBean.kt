package com.example.glass.component.video.drawerbean

data class RectBean(override val type: String) : DataBean(type = type){
    var minX : Long = 0L
    var minY : Long = 0L //左上角的点
    var maxX : Long = 0L
    var maxY : Long = 0L // 右下角的点
}
