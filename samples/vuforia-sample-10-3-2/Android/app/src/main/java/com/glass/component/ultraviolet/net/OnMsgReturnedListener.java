package com.glass.component.ultraviolet.net;

public interface OnMsgReturnedListener {
    void onMsgReturned(Object msg);
    void onError(Exception ex);
    void onStateMsg(String state);
}
