package matei.personalassistant.voicerecognition;

import java.util.ArrayList;

import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;

public class VoiceRecognitionListener implements RecognitionListener {

    private static VoiceRecognitionListener instance = null;
    private int volume;
    IVoiceControl listener; // This is the running activity (we will initialize it later)

    private VoiceRecognitionListener() {
        this.volume = volume;
    }

    public static VoiceRecognitionListener getInstance() {
        if (instance == null) {
            instance = new VoiceRecognitionListener();
        }
        return instance;
    }

    public void setListener(IVoiceControl listener) {
        this.listener = listener;
    }


    //### Override RecognitionListener methods ####

    // This method will be executed when voice commands were found
    public void onResults(Bundle data) {
        ArrayList<String> matches = data.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        String[] commands = new String[matches.size()];
        for (String command : matches) {
            System.out.println(command);
        }
        commands = matches.toArray(commands);
        listener.processVoiceCommands(commands);
    }

    // User starts speaking
    public void onBeginningOfSpeech() {
        System.out.println("Starting to listen");
    }

    public void onBufferReceived(byte[] buffer) {
    }

    // User finished speaking
    public void onEndOfSpeech() {
        System.out.println("Waiting for result...");
    }

    // If the user said nothing the service will be restarted
    public void onError(int error) {
        if (listener != null) {
            listener.restartListeningService();
        }
    }

    public void onEvent(int eventType, Bundle params) {
    }

    public void onPartialResults(Bundle partialResults) {
    }

    public void onReadyForSpeech(Bundle params) {
        ((ListeningActivity)listener).turnBeepOn();
    }

    public void onRmsChanged(float rmsdB) {
    }
}