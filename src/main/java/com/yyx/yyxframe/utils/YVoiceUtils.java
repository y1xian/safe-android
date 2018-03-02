package com.yyx.yyxframe.utils;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ScheduledExecutorService;

/**
 * 录音工具类
 */
public class YVoiceUtils {
    //音频录制接口
    private MediaRecorder recorder;
    private ScheduledExecutorService voiceTimer;  //录音时长类

    private YVoiceUtils() {
    }

    public static YVoiceUtils instance = null;

    public static YVoiceUtils getInstance() {
        if (instance == null) {
            instance = new YVoiceUtils();
        }
        return instance;
    }

    /**
     * [开始录制音频]
     *
     * @param mContext 上下文
     * @param savePath 储存位置
     * @param fileName 文件名
     * @return 文件地址
     */
    public String startRecord(Context mContext, String savePath, String fileName) {
        if (recorder == null) {
            //录音名称
            String recordFileName = fileName + ".amr";
            File recordFile = new File(savePath, recordFileName);
            if (!recordFile.exists()) {
                try {
                    recordFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            recorder = new MediaRecorder();
            //声音来源
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            //音频输出格式
            recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            //设置声音编码的格式
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            //设置音频输出
            recorder.setOutputFile(recordFile.getAbsolutePath());

            try {
                recorder.prepare();
                recorder.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return recordFile.getAbsolutePath();
        }
        return null;
    }

    /**
     * [停止录音]
     */
    public void stopRecord() {
        if (recorder != null) {
            recorder.stop();
            recorder.release();
            recorder = null;
        }
    }

    private MediaPlayer mediaPlayer = null;

    /**
     * [播放语音]
     *
     * @param voicePath 语音地址
     */
    public void playVoice(String voicePath) {
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        }
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                try {
                    mediaPlayer.stop();
                    mediaPlayer.prepare();
                    mediaPlayer.reset();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        try {
            //如果当前mediaPlayer正在播放
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
                mediaPlayer.prepare();
                mediaPlayer.reset();
            }
            mediaPlayer.setDataSource(voicePath);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * [销毁]
     */
    public void mediaDistory() {
        if (mediaPlayer != null) {
            try {
                mediaPlayer.stop();
                mediaPlayer.prepare();
                mediaPlayer.reset();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}