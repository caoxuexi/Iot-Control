package cn.caoqiang.framework.utils;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import cn.caoqiang.framework.BuildConfig;

public class LogUtils {
    
    private static SimpleDateFormat mSimpleDateFormat =
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static void i(String text){
        if(BuildConfig.LOG_DEBUG){
            if(!TextUtils.isEmpty(text)){
                Log.i(BuildConfig.LOG_TAG,text);
                writeToFile(text);
            }
        }
    }

    public static void e(String text){
        if(BuildConfig.LOG_DEBUG){
            if(!TextUtils.isEmpty(text)){
                Log.e(BuildConfig.LOG_TAG,text);
                writeToFile(text);
            }
        }
    }

    //封装到文件的方法
    public static void writeToFile(String text){

        String fileName= Environment.getExternalStorageDirectory().getAbsolutePath()+"/Iot/Iot.log";
        //时间+等级+内存
        String log= mSimpleDateFormat.format(new Date())+" "+text+"\n";
        //检查父路径
        File fileGroup =new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Iot");
        if(!fileGroup.exists()){
            boolean ok=fileGroup.mkdirs();
        }
        //开始写入
        FileOutputStream fileOutputStream=null;
        BufferedWriter bufferedWriter=null;
        try{
            fileOutputStream = new FileOutputStream(fileName,true);
            //编码问题 GBK 正确传入中文
            bufferedWriter = new BufferedWriter(
                    new OutputStreamWriter(fileOutputStream, Charset.forName("gbk")));
            bufferedWriter.write(log);
        }catch (FileNotFoundException e){
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(bufferedWriter!=null){
                try {
                    bufferedWriter.close();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
    }
}
