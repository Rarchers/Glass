package com.example.glass.application;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.rokid.glass.instruct.VoiceInstruction;
import com.rokid.glass.instruct.entity.EntityKey;
import com.rokid.glass.instruct.entity.IInstructReceiver;
import com.rokid.glass.instruct.entity.InstructEntity;


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

        VoiceInstruction.getInstance().addGlobalInstruct(
                new InstructEntity()
                        .setGlobal(true)
                        .addEntityKey(new EntityKey("离开", "li kai"))
                        .addEntityKey(new EntityKey(EntityKey.Language.en, "back last page"))
                        .setCallback(new IInstructReceiver() {
                            @Override
                            public void onInstructReceive(Activity act, String key, InstructEntity instruct) {
                                try {
                                    if (act != null) {
                                        act.finish();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        })
        );

    }
}
