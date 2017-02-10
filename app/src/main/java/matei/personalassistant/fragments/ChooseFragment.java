package matei.personalassistant.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import matei.personalassistant.PhoneNumbersListViewAdapter;
import matei.personalassistant.R;
import matei.personalassistant.voicerecognition.ListeningActivity;
import matei.personalassistant.voicerecognition.RecognitionActivity;
import matei.personalassistant.voicerecognition.VoiceRecognitionListener;

import static matei.personalassistant.utils.ConstantUtils.CHOOSE_PHONE_NUMBER;
import static matei.personalassistant.utils.ConstantUtils.CONTACT_NAMES;

public abstract class ChooseFragment extends Fragment {

    protected ViewGroup rootView;
    private ListView phoneNumbersListView;
    private PhoneNumbersListViewAdapter adapter;
    private ArrayList<String> phoneNumbers;
    private SpeechRecognizer sr;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    private void receiveNumbers() {

        Bundle args = getArguments();
        if (args != null) {
            phoneNumbers = args.getStringArrayList(CONTACT_NAMES);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_choose_phone_number, container, false);
        phoneNumbersListView = (ListView) rootView.findViewById(R.id.phone_numbers_fragment_lv);

        receiveNumbers();
        adapter = new PhoneNumbersListViewAdapter(getActivity(), R.layout.phone_number_item, phoneNumbers);
        phoneNumbersListView.setAdapter(adapter);
        phoneNumbersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                action(phoneNumbers.get(position));
            }
        });



        if (phoneNumbers.size() == 1) {
            action(phoneNumbers.get(0));
        } else {
            /*((ListeningActivity)getActivity()).getTts().speak(CHOOSE_PHONE_NUMBER,
                    TextToSpeech.QUEUE_ADD, null);*/
            sr = ((RecognitionActivity) getActivity()).getSr();
            sr.stopListening();
            sr.setRecognitionListener(new FragmentRecognitionListener());
            startVoiceRecognitionActivity();
        }

        return rootView;
    }

    @Override
    public void onPause() {
        if(sr!=null){
            sr.stopListening();
            sr.setRecognitionListener(VoiceRecognitionListener.getInstance());
            ((ListeningActivity)getActivity()).startListening();
        }

        super.onPause();
    }

    public void startVoiceRecognitionActivity() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
        sr.startListening(intent);
    }

    class FragmentRecognitionListener implements RecognitionListener {
        public void onReadyForSpeech(Bundle params) {
            ((ListeningActivity)getActivity()).turnBeepOn();
        }

        public void onBeginningOfSpeech() {
        }

        public void onRmsChanged(float rmsdB) {
        }

        public void onBufferReceived(byte[] buffer) {
        }

        public void onEndOfSpeech() {
        }

        public void onError(int error) {
            Log.d("RecognitionListener err", "error " + error);
        }

        public void onResults(Bundle results) {
            Log.wtf("merge", "ceva ? ");
            ArrayList data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            //mText.setText("results: "+ data.get(0));
            String response = data.get(0).toString();
            processResponse(response);
            //sr.stopListening();
        }

        public void onPartialResults(Bundle partialResults) {
        }

        public void onEvent(int eventType, Bundle params) {
        }
    }

    private void processResponse(String response) {
        String option = null;
        if (response.contains("1") || response.contains("first")) {
            option = phoneNumbers.get(0);
        } else if (response.contains("2") || response.contains("second")) {
            option = phoneNumbers.get(1);
        } else if (response.contains("3") || response.contains("third")) {
            option = phoneNumbers.get(2);
        } else if (response.contains("4") || response.contains("fourth")) {
            option = phoneNumbers.get(3);
        } else {
            option = phoneNumbers.get(0);
            //Toast.makeText(getActivity(), response, Toast.LENGTH_SHORT).show();
        }
        action(option);
    }

    protected abstract void action(String option);
}