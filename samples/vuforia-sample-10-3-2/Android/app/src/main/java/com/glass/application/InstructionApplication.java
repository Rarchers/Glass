package com.glass.application;

import android.app.Application;

import com.rokid.glass.instruct.VoiceInstruction;


public class InstructionApplication extends Application {

    /**
     * 设置ManagerBasicSkill的context
     */
    @Override
    public void onCreate() {
        super.onCreate();

        // 插件独立控制模式
        VoiceInstruction.init(this);

        // 插件和百灵鸟自动指令共用模式
//        VoiceInstruction.init(this, false);

//        VoiceInstruction.getInstance().addGlobalInstruct(
//                new InstructEntity()
//                        .setGlobal(true)
//                        .addEntityKey(new EntityKey("", "li kai"))
//                        .addEntityKey(new EntityKey(EntityKey.Language.en, "back last page"))
//                        .setCallback(new IInstructReceiver() {
//                            @Override
//                            public void onInstructReceive(Activity act, String key, InstructEntity instruct) {
//                                try {
//                                    if (act != null) {
//                                        act.finish();
//                                    }
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                        })
//        );

    }
}
