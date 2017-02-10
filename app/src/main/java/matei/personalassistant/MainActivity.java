package matei.personalassistant;

/*
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.widget.Button;
import android.widget.TextView;
import java.util.ArrayList;
import android.util.Log;

public class MainActivity extends AppCompatActivity implements OnClickListener
{

    private TextView mText;
    private SpeechRecognizer sr;
    private static final String TAG = "MyStt3Activity";
    @Override

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button speakButton = (Button) findViewById(R.id.start_button);
        mText = (TextView) findViewById(R.id.text_rec);
        speakButton.setOnClickListener(this);
        sr = SpeechRecognizer.createSpeechRecognizer(this);
        sr.setRecognitionListener(new listener());
    }

    class listener implements RecognitionListener
    {
        public void onReadyForSpeech(Bundle params)
        {
            Log.d(TAG, "onReadyForSpeech");
        }
        public void onBeginningOfSpeech()
        {
            Log.d(TAG, "onBeginningOfSpeech");
        }
        public void onRmsChanged(float rmsdB)
        {
            Log.d(TAG, "onRmsChanged");
        }
        public void onBufferReceived(byte[] buffer)
        {
            Log.d(TAG, "onBufferReceived");
        }
        public void onEndOfSpeech()
        {
            Log.d(TAG, "onEndofSpeech");
        }
        public void onError(int error)
        {
            Log.d(TAG,  "error " +  error);
            mText.setText("error " + error);
        }
        public void onResults(Bundle results)
        {
            ArrayList data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            mText.setText("results: "+ data.get(0));
        }
        public void onPartialResults(Bundle partialResults)
        {
            Log.d(TAG, "onPartialResults");
        }
        public void onEvent(int eventType, Bundle params)
        {
            Log.d(TAG, "onEvent " + eventType);
        }
    }
    public void onClick(View v) {
        if (v.getId() == R.id.start_button)
        {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Please start speaking");
            intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,"matei.personalassistant");

            intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,1);
            sr.startListening(intent);
            Log.i("111111","11111111");
        }
    }
}
*/

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.provider.AlarmClock;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.speech.RecognizerIntent;
import android.widget.TextView;
import android.support.v4.app.FragmentManager;
import android.widget.Toast;

import matei.personalassistant.utils.*;

import static matei.personalassistant.utils.ConstantUtils.*;

/*
public class MainActivity extends AppCompatActivity implements OnClickListener {

    public Button speakButton;
    public TextView message;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        speakButton = (Button) findViewById(R.id.start_button);
        message = (TextView) findViewById(R.id.text_rec);
        speakButton.setOnClickListener(this);
    }


    public void startVoiceRecognitionActivity(int code) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
//        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
//                "Speech recognition demo");
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);
        startActivityForResult(intent, code);
    }

    public void onClick(View v) {
        startVoiceRecognitionActivity(VOICE_RECOGNITION_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode == RESULT_OK) {
            // Fill the list view with the strings the recognizer thought it
            // could have heard
            ArrayList matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

            if (isSpeechRecognitionActivityPresented(this) == true) {
                Log.i("myAPP", "da");
            } else {
                Log.i("myAPP", "nu");
            }
            Log.wtf("matches: ", matches.toString());
            if (matches.size() == 0) {
                Log.wtf("matches", " 0 matches");
            }
            String bestMatch = matches.get(0).toString();
            Log.e("text", bestMatch);
            message.setText(bestMatch);
            // matches is the result of voice input. It is a list of what the
            // user possibly said.
            // Using an if statement for the keyword you want to use allows the
            // use of any activity if keywords match
            // it is possible to set up multiple keywords to use the same
            // activity so more than one word will allow the user
            // to use the activity (makes it so the user doesn't have to
            // memorize words from a list)
            // to use an activity from the voice input information simply use
            // the following format;
            // if (matches.contains("keyword here") { startActivity(new
            // Intent("name.of.manifest.ACTIVITY")

            if (bestMatch.startsWith("call")) {
                try {
                    String toCall = bestMatch.substring(5);
                    String phoneNumber;
                    if (toCall.matches(".*\\d.*")) {
                        dialPhoneNumber(toCall);
                    } else {
                        phoneNumber = getPhoneNumber(toCall, this);
                        if (!phoneNumber.equals("Unsaved")) {
                            dialPhoneNumber(phoneNumber);
                        }
                    }
                } catch (Exception e) {
                    Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
                }
            } else if (bestMatch.contains("alarm")) {
                String hour = bestMatch.replaceAll("\\D+", "");
                System.out.println(bestMatch);
                int hourPositionInString = bestMatch.indexOf(hour);
                String period = bestMatch.substring(hourPositionInString + hour.length() + 1);
                if (period.contains("a.m.")) {
                    createAlarm("My Custom alarm", Integer.parseInt(hour), 0);
                } else if (period.contains("p.m.")) {
                    createAlarm("My Custom alarm", Integer.parseInt(hour) + 12, 0);
                }
            }
        }
    }

    public void dialPhoneNumber(String phoneNumber) {
        Log.i("phne number", phoneNumber);
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    public String getPhoneNumber(String name, Context context) {
        String ret = null;
        String selection = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " like'%" + name + "'";
        String[] projection = new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER};
        Cursor c = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projection, selection, null, null);
        ArrayList<String> numbers = new ArrayList<>();
        while (c.moveToNext()) {
            ret = c.getString(0);
            numbers.add(ret);
            Log.e("phone number", ret);
        }
        c.close();
        if (ret == null)
            return "Unsaved";
        else if (numbers.size() == 1) {
            return ret;
        } else {
            Bundle bundle = new Bundle();
            bundle.putStringArrayList(PHONE_NUMBERS, numbers);
            FragmentUtils.replaceFragment(getSupportFragmentManager(), ChooseFragment.class,
                    R.id.activity_main, bundle);
            //startVoiceRecognitionActivity(VOICE_RECOGNITION_REQUEST_CODE_DIAL_NUMBER);
            return "Unsaved";
        }
    }

    public void createAlarm(String message, int hour, int minutes) {
        Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM)
                .putExtra(AlarmClock.EXTRA_MESSAGE, message)
                .putExtra(AlarmClock.EXTRA_SKIP_UI, true)
                .putExtra(AlarmClock.EXTRA_HOUR, hour)
                .putExtra(AlarmClock.EXTRA_MINUTES, minutes);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }
}
*/
public class MainActivity{

}