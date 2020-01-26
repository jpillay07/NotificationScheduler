package com.codelabs.notificationscheduler;

import androidx.appcompat.app.AppCompatActivity;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    RadioGroup networkOptions;
    private JobScheduler mScheduler;
    private static final int JOB_ID = 0;
    private Switch mDeviceIdle;
    private Switch mDeviceCharging;
    private SeekBar mSeekbar;
    private boolean mSeekbarSet;
    private int seekBarInteger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDeviceIdle = findViewById(R.id.idleSwitch);
        mDeviceCharging = findViewById(R.id.chargingSwitch);
        mSeekbar = findViewById(R.id.seekBar);
        final TextView seekbarProgress = findViewById(R.id.seekBarProgress);

        mSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(i > 0){

                    seekBarInteger = mSeekbar.getProgress();
                    mSeekbarSet = seekBarInteger > 0;
                    seekbarProgress.setText(i + " seconds");
                }
                else{
                    seekbarProgress.setText("Not Set");
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public void scheduleJob(View view) {
        networkOptions = findViewById(R.id.networkOptions);
        int selectedNetworkId = networkOptions.getCheckedRadioButtonId();
        int selectedNetworkOption = JobInfo.NETWORK_TYPE_NONE;

        switch(selectedNetworkId){
            case R.id.noNetwork:
                selectedNetworkOption = JobInfo.NETWORK_TYPE_NONE;
                break;
            case R.id.anyNetwork:
                selectedNetworkOption = JobInfo.NETWORK_TYPE_ANY;
                break;
            case R.id.wifi:
                selectedNetworkOption = JobInfo.NETWORK_TYPE_UNMETERED;
        }

        mScheduler = (JobScheduler)getSystemService(JOB_SCHEDULER_SERVICE);

        ComponentName serviceName = new ComponentName(getPackageName(), NotificationJobService.class.getName());

        JobInfo.Builder builder = new JobInfo.Builder(JOB_ID, serviceName)
                .setRequiredNetworkType(selectedNetworkOption)
                .setRequiresDeviceIdle(mDeviceIdle.isChecked())
                .setRequiresCharging(mDeviceCharging.isChecked());

        if(mSeekbarSet){
            builder.setOverrideDeadline(seekBarInteger * 1000);
        }

        Boolean constraintSet = (selectedNetworkOption != JobInfo.NETWORK_TYPE_NONE) || mDeviceCharging.isChecked() || mDeviceIdle.isChecked() || mSeekbarSet;

        if(constraintSet){
            JobInfo myJobInfo = builder.build();
            mScheduler.schedule(myJobInfo);

            Toast.makeText(this, "Job has been scheduled and will run when constraints are met",
                    Toast.LENGTH_SHORT).show();

        }
        else{
            Toast.makeText(this, "Please select at least one constraint", Toast.LENGTH_SHORT).show();
        }


    }

    public void cancelJobs(View view) {
        if(mScheduler != null){
            mScheduler.cancelAll();
            mScheduler = null;
            Toast.makeText(this, "Jobs have been cancelled.", Toast.LENGTH_SHORT).show();
        }
    }
}
