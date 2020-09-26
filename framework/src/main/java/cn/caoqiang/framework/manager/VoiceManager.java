package cn.caoqiang.framework.manager;

import android.content.Context;

import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;

import cn.caoqiang.framework.utils.LogUtils;


/**
 * FileName: TTSManager
 * Founder: CaoQiang
 * Profile: 语音管理
 */
public class VoiceManager {

    private static volatile VoiceManager mInstance = null;

    private RecognizerDialog mIatDialog;

    private VoiceManager(Context mContext) {
        SpeechUtility.createUtility(mContext, SpeechConstant.APPID + "=5f6cbd33");

        mIatDialog = new RecognizerDialog(mContext, new InitListener() {
            @Override
            public void onInit(int i) {
                LogUtils.i("InitListener:" + i);
            }
        });

        //清空所有属性
        mIatDialog.setParameter(SpeechConstant.CLOUD_GRAMMAR, null);
        mIatDialog.setParameter(SpeechConstant.SUBJECT, null);
        //设置返回格式
        mIatDialog.setParameter(SpeechConstant.RESULT_TYPE, "json");
        //设置在线引擎
        mIatDialog.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
        //设置语言
        mIatDialog.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        //设置结果返回语言
        mIatDialog.setParameter(SpeechConstant.ACCENT, "mandarin");
        //4s没说话默认此次操作结束
        mIatDialog.setParameter(SpeechConstant.VAD_BOS, "4000");
        //静音超时
        mIatDialog.setParameter(SpeechConstant.VAD_EOS, "1000");
        mIatDialog.setParameter(SpeechConstant.ASR_PTT, "1");
    }

    public static VoiceManager getInstance(Context mContext) {
        if (mInstance == null) {
            synchronized (VoiceManager.class) {
                if (mInstance == null) {
                    mInstance = new VoiceManager(mContext);
                }
            }
        }
        return mInstance;
    }

    /**
     * 开始说话
     *
     * @param listener
     */
    public void startSpeak(RecognizerDialogListener listener) {
        mIatDialog.setListener(listener);
        mIatDialog.show();
    }
}
