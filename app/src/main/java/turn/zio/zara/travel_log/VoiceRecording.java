package turn.zio.zara.travel_log;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.File;

public class VoiceRecording extends Activity {
    String RECORDED_FILE;
    String RECORDED_FILENAME;
    String path;

    int recordtime = 0;
    int playtime = 0;
    MediaPlayer player;
    MediaRecorder recorder;

    ToggleButton recording_startstopBtn;
    Button playing_closeBtn;

    ImageButton playing_startBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.voice_recording_activity);


        playing_closeBtn = (Button) findViewById(R.id.recording_cancelBtn);
        recording_startstopBtn = (ToggleButton) findViewById(R.id.recording_startstopBtn);
        playing_startBtn = (ImageButton) findViewById(R.id.playing_startBtn);


        recording_startstopBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (recording_startstopBtn.isChecked()) {
                    if (path != null) {
                        File files = new File(path);
                        files.delete();
                    }
                    RECORDED_FILE = Environment.getExternalStorageDirectory().getAbsolutePath() + "/travelLog/voice/";
                    RECORDED_FILENAME = System.currentTimeMillis() + "voice.mp4";
                    path = RECORDED_FILE + RECORDED_FILENAME;

                    if (recorder != null) {
                        recorder.stop();
                        recorder.release();
                        recorder = null;
                    }// TODO Auto-generated method stub
                    File file = new File(RECORDED_FILE);
                    if (!file.exists())
                        file.mkdirs();
                    recordtime = 0;
                    playing_startBtn.setClickable(false);

                    recorder = new MediaRecorder();
                    recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                    recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                    recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
                    recorder.setOutputFile(RECORDED_FILE + RECORDED_FILENAME);
                    try {
                        recorder.prepare();
                        recorder.start();
                    } catch (Exception ex) {
                        Log.e("SampleAudioRecorder", "Exception : ", ex);
                    }
                } else {
                    playing_startBtn.setClickable(true);
                    if (recorder == null)
                        return;
                    recorder.stop();
                    recorder.release();
                    recorder = null;

                    // TODO Auto-generated method stub
                }
            }
        });
        playing_startBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                try {
                    File file = new File(path);
                    if (file.exists()) {
                        recordtime = 0;
                        playing_startBtn.setClickable(false);
                        playAudio(path);
                    } else {
                        Toast.makeText(getApplicationContext(), "녹화를 먼저 하세요", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


    }

    private void playAudio(String url) throws Exception {
        killMediaPlayer();

        player = new MediaPlayer();
        player.setDataSource(url);
        player.prepare();
        player.start();
    }

    protected void onDestroy() {
        super.onDestroy();
        killMediaPlayer();
    }

    private void killMediaPlayer() {
        if (player != null) {
            try {
                player.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    protected void onPause() {
        if (recorder != null) {
            recorder.release();
            recorder = null;
        }
        if (player != null) {
            player.release();
            player = null;
        }

        super.onPause();

    }

    public void dialogDismissing(View view) {
        if (path != null) {
            File file = new File(path);
            file.delete();
        }
        finish();
    }

    public void addBtn(View view) {

        Intent intent = new Intent();
        intent.putExtra("VoicePath", path);
        setResult(RESULT_OK, intent);
        finish();
    }
}
