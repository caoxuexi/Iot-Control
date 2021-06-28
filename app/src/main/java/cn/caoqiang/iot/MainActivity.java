package cn.caoqiang.iot;


import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.ui.RecognizerDialogListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import cn.caoqiang.framework.config.HttpConst;
import cn.caoqiang.framework.gson.DHTBean;
import cn.caoqiang.framework.gson.VoiceBean;
import cn.caoqiang.framework.manager.VoiceManager;
import cn.caoqiang.framework.utils.LogUtils;
import cn.caoqiang.iot.button.FButton;
import cn.caoqiang.iot.event.DHTMessageEvent;
import cn.caoqiang.iot.event.VoiceMessageEvent;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private OkHttpClient okHttpClient;
    private RadioGroup redChooseGroup;
    private RadioGroup greenChooseGroup;
    private FButton measure;
    private TextView measure_result;
    private FButton speak;
    private TextView voiceResult;
    final String ledUrl = HttpConst.IOT_SITE + HttpConst.IOT_LED;
    private VoiceMessageEvent voiceMessageEvent = new VoiceMessageEvent(); //声音请求事件
    Gson gson = new Gson();//json对象解释器
    Timer timer = new Timer(); //定时器
    private boolean hasrefuse; //判断权限是否之前被拒绝过

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        radioGroupSet();
        EventBus.getDefault().register(this);
    }

    private void radioGroupSet() {
        redChooseGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int radioButtonId = group.getCheckedRadioButtonId();
                RadioButton rb = (RadioButton) findViewById(radioButtonId);
                String radioButtonLabel = rb.getText().toString();
                if (radioButtonLabel.equals("开")) {
                    ledThread(ledUrl, HttpConst.RED_ON);
                } else {
                    ledThread(ledUrl, HttpConst.RED_OFF);
                }
            }
        });

        greenChooseGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int radioButtonId = group.getCheckedRadioButtonId();
                RadioButton rb = (RadioButton) findViewById(radioButtonId);
                String radioButtonLabel = rb.getText().toString();
                if (radioButtonLabel.equals("开")) {
                    ledThread(ledUrl, HttpConst.GREEN_ON);
                } else {
                    ledThread(ledUrl, HttpConst.GREEN_OFF);
                }
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onVoiceMessageEvent(VoiceMessageEvent voiceMessageEvent) {
        int lineCount = voiceResult.getLineCount();
        if (lineCount >= 10) {
            voiceResult.setText("");
            voiceResult.append(voiceMessageEvent.getResult());
        } else {
            voiceResult.append(voiceMessageEvent.getResult());
        }
        /* Do something */
    }

    ;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDTHMessageEvent(DHTMessageEvent dHTMessageEvent) {
        DHTBean dhtBean = dHTMessageEvent.getDhtBean();
        measure_result.setText("");
        measure_result.append("温度：");
        measure_result.append(dhtBean.getTemperature().toString());
        measure_result.append("℃，");
        measure_result.append("湿度：");
        measure_result.append(dhtBean.getHumidity().toString());
        measure_result.append("%");
    }

    ;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.measure:
                if (measure.getText().equals("开始检测")) {
                    TimerTask timerTask = new TimerTask() {
                        @Override
                        public void run() {
                            dHTThread(HttpConst.IOT_SITE + HttpConst.IOT_DHT);
                        }
                    };
                    timer.schedule(timerTask, 0, 2000); //两秒检测一次
                    measure.setText("停止检测");
                } else {
                    timer.cancel(); //关闭定时器
                    measure.setText("开始检测");
                    timer = new Timer();
                }

                break;
            case R.id.speak:
                if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED ||
                   checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                ) {
                    hasrefuse = shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO);
                    hasrefuse = shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    if(hasrefuse) {
                        //当拒绝了授权后，为提升用户体验，可以以弹窗的方式引导用户到设置中去进行设置
                        new AlertDialog.Builder(MainActivity.this)
                                .setMessage("需要开启权限才能使用此功能")
                                .setPositiveButton("设置", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //引导用户到设置中去进行设置
                                        Intent intent = new Intent();
                                        intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                                        intent.setData(Uri.fromParts("package", getPackageName(), null));
                                        startActivity(intent);
                                        finish();
                                    }
                                })
                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                })
                                .create()
                                .show();
                    }else{
                        requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                    }
                    break;
                } else {
                    VoiceManager.getInstance(this).startSpeak(new RecognizerDialogListener() {
                        @Override
                        public void onResult(RecognizerResult recognizerResult, boolean b) {
                            String result = recognizerResult.getResultString();
                            if (!TextUtils.isEmpty(result)) {
                                LogUtils.i("result:" + result);
                                VoiceBean voiceBean = new Gson().fromJson(result, VoiceBean.class);
                                if (voiceBean.isLs()) {
                                    StringBuffer sb = new StringBuffer();
                                    for (int i = 0; i < voiceBean.getWs().size(); i++) {
                                        VoiceBean.WsBean wsBean = voiceBean.getWs().get(i);
                                        String sResult = wsBean.getCw().get(0).getW();
                                        sb.append(sResult);
                                    }
                                    LogUtils.i("result:" + sb.toString());
                                    judgeAndLed(sb.toString());
                                    System.out.println(sb.toString());
                                }
                            }
                        }

                        @Override
                        public void onError(SpeechError speechError) {
                            LogUtils.e("speechError:" + speechError.toString());
                        }
                    });
                }
                break;
        }
    }

    private void judgeAndLed(String voiceResult) {
        System.out.println("judgeAndLed:" + voiceResult);
        if (voiceResult.equals("打开红灯。")) {
            ledThread(ledUrl, HttpConst.RED_ON);
            voiceMessageEvent.setResult("识别成功:打开红灯\n");
            EventBus.getDefault().post(voiceMessageEvent);
        } else if (voiceResult.equals("关闭红灯。")) {
            ledThread(ledUrl, HttpConst.RED_OFF);
            voiceMessageEvent.setResult("识别成功:关闭红灯\n");
            EventBus.getDefault().post(voiceMessageEvent);
        } else if (voiceResult.equals("打开绿灯。")) {
            ledThread(ledUrl, HttpConst.GREEN_ON);
            voiceMessageEvent.setResult("识别成功:打开绿灯\n");
            EventBus.getDefault().post(voiceMessageEvent);
        } else if (voiceResult.equals("关闭绿灯。")) {
            ledThread(ledUrl, HttpConst.GREEN_OFF);
            voiceMessageEvent.setResult("识别成功:关闭绿灯\n");
            EventBus.getDefault().post(voiceMessageEvent);
        }
    }

    private void initView() {
        redChooseGroup = (RadioGroup) findViewById(R.id.redChooseGroup);
        redChooseGroup.setOnClickListener(this);
        greenChooseGroup = (RadioGroup) findViewById(R.id.greenChooseGroup);
        greenChooseGroup.setOnClickListener(this);
        measure = (FButton) findViewById(R.id.measure);
        measure.setOnClickListener(this);
        measure_result = (TextView) findViewById(R.id.measure_result);
        measure_result.setOnClickListener(this);
        speak = (FButton) findViewById(R.id.speak);
        speak.setOnClickListener(this);
        voiceResult = (TextView) findViewById(R.id.voiceResult);
        voiceResult.setOnClickListener(this);
        okHttpClient = new OkHttpClient();
    }

    public String post(String url, String param) throws IOException {
        Request request;
        if (param != null) {
            String[] split = param.split("=");
            FormBody formBody = new FormBody.Builder().add(split[0], split[1]).build();
            request = new Request.Builder()
                    .url(url)
                    .post(formBody)
                    .build();
        } else {
            request = new Request.Builder()
                    .url(url)
                    .build();
        }

        Response response = okHttpClient.newCall(request).execute();
        return response.body().string();
    }

    public String get(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = okHttpClient.newCall(request).execute();
        return response.body().string();
    }

    public void ledThread(final String url, final String param) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    post(url, param);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void dHTThread(final String url) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String DHTResult = get(url);
                    if (DHTResult.contains("temperature")) {
                        DHTBean dhtBean = gson.fromJson(DHTResult, DHTBean.class);
                        DHTMessageEvent dhtMessageEvent = new DHTMessageEvent();
                        dhtMessageEvent.setDhtBean(dhtBean);
                        EventBus.getDefault().post(dhtMessageEvent);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }
}