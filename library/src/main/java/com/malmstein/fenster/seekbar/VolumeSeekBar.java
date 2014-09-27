package com.malmstein.fenster.seekbar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.util.AttributeSet;
import android.widget.SeekBar;

public class VolumeSeekBar extends SeekBar {

    private AudioManager audioManager;
    private Listener volumeListener;
    private BroadcastReceiver volumeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateVolumeProgress();
        }
    };

    public VolumeSeekBar(Context context) {
        super(context);
    }

    public VolumeSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VolumeSeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        registerVolumeReceiver();
    }

    @Override
    protected void onDetachedFromWindow() {
        unregisterVolumeReceiver();
        super.onDetachedFromWindow();
    }

    public void init(final AudioManager audioManager, final Listener volumeListener) {
        this.audioManager = audioManager;
        this.volumeListener = volumeListener;

        this.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        this.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
        this.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int vol, boolean fromUser) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, vol, 0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                volumeListener.onVolumeStartedDragging();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                volumeListener.onVolumeFinishedDragging();
            }
        });
    }

    private void updateVolumeProgress() {
        this.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
    }

    private void registerVolumeReceiver() {
        getContext().registerReceiver(volumeReceiver, new IntentFilter("android.media.VOLUME_CHANGED_ACTION"));
    }

    private void unregisterVolumeReceiver() {
        getContext().unregisterReceiver(volumeReceiver);
    }

    public interface Listener {
        void onVolumeStartedDragging();

        void onVolumeFinishedDragging();
    }

}
