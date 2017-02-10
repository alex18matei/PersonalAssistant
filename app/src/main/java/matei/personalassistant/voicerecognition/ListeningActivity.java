package matei.personalassistant.voicerecognition;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.util.Locale;

public abstract class ListeningActivity extends AppCompatActivity implements IVoiceControl {

    protected SpeechRecognizer sr;
    protected Context context;
    protected AudioManager audioManager;
    private int mStreamVolume;
    protected boolean beepOff;
    protected TextToSpeech tts;

    public TextToSpeech getTts() {
        return tts;
    }

    public SpeechRecognizer getSr() {
        return sr;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    tts.setLanguage(Locale.UK);
                } else {
                    Log.e("TTS on init", "errror");
                }
            }
        });
    }

    // starts the service
    public void startListening() {
        try {
            audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            //mStreamVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC); // getting system volume into var for later un-muting

            initSpeech();
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
            if (!intent.hasExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE)) {
                intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
                        "com.dummy");
            }
            turnBeepOff();
            sr.startListening(intent);

        } catch (Exception ex) {
            Log.d("SpeechRecognitionServ", "exception");
        }
    }

    protected void initSpeech() {
        if (sr == null) {
            sr = SpeechRecognizer.createSpeechRecognizer(this);
            if (!SpeechRecognizer.isRecognitionAvailable(context)) {
                Toast.makeText(context, "Speech Recognition is not available",
                        Toast.LENGTH_LONG).show();
                finish();
            }
            sr.setRecognitionListener(VoiceRecognitionListener.getInstance());
        }
    }

    // stops the service
    protected void stopListening() {
        //turnBeepOn();
        if (sr != null) {
            sr.stopListening();
            sr.cancel();
            sr.destroy();
        }
        turnBeepOn();
        sr = null;
    }

    @Override
    public void finish() {
        stopListening();
        super.finish();
    }

    @Override
    protected void onPause() {
        stopListening();
        tts.stop();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        tts.shutdown();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        Log.wtf("Listening Activity", "onResume called");
        startListening();
        super.onResume();
    }

    //is abstract so the inheriting classes need to implement it. Here you put your code which should be executed once a command was found
    @Override
    public abstract void processVoiceCommands(String... voiceCommands);

    @Override
    public void restartListeningService() {
        stopListening();
        startListening();
    }

    public void turnBeepOff() {
        if (!beepOff) {
            audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
            audioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
            beepOff = true;
        }
    }

    public void turnBeepOn() {
        if (beepOff) {
            audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
            beepOff = false;
        }
    }
}
