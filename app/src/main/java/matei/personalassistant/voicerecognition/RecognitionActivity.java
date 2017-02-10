package matei.personalassistant.voicerecognition;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.hardware.Camera;
import android.media.AudioManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.AlarmClock;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.RunnableFuture;

import matei.personalassistant.SettingsPrefferenceActivity;
import matei.personalassistant.fragments.ChooseContactFragment;
import matei.personalassistant.fragments.ChooseFragment;
import matei.personalassistant.R;
import matei.personalassistant.fragments.ChoosePhoneNumberFragment;
import matei.personalassistant.utils.FragmentUtils;

import static java.net.Proxy.Type.HTTP;
import static matei.personalassistant.utils.ConstantUtils.*;

public class RecognitionActivity extends ListeningActivity {

    private LinearLayout content;
    private String command = "none";
    private boolean isAssitantCalled = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        content = (LinearLayout) findViewById(R.id.commands);

        // The following 3 lines are needed in every onCreate method of a ListeningActivity
        context = getApplicationContext(); // Needs to be set
        VoiceRecognitionListener.getInstance().setListener(this); // Here we set the current listener
        startListening(); // starts listening
    }

    // Here is where the magic happens
    @Override
    public void processVoiceCommands(String... voiceCommands) {
        showVoiceCommandAsTextView(voiceCommands[0]);
        //getContactName("Roxana", this);
        if (isAssitantCalled) {
            String bestMatch = voiceCommands[0];
            if (isCustomUserGeneralCommand(bestMatch)) {
                Log.wtf("restarted ", "again");
                bestMatch = processGeneralCustomCommand();
            }
            if (bestMatch.contains("alarm")) {
                processAlarmCommand(bestMatch);
            } else if (bestMatch.startsWith("call")) {
                processCallCommand(bestMatch);
            } else if (bestMatch.contains("take a picture")) {
                capturePhoto();
            } else if (bestMatch.contains("record a video")) {
                recordVideo();
            } else if (bestMatch.startsWith("search for")) {
                processSearch(bestMatch.substring(10));
            } else if (bestMatch.startsWith("browse")) {
                openWebPage("https:" + bestMatch.substring(6).trim());
            } else if (bestMatch.startsWith("play")) {
                playSearchArtist(bestMatch.substring(4).trim());
            } else if (bestMatch.contains("turn") && bestMatch.contains("Wi-Fi")) {
                processWiFiSettings(bestMatch.substring(5, 7));
            } else if (bestMatch.contains("turn") && bestMatch.contains("flashlight")) {
                processFlashlightSettings(bestMatch.substring(5, 7));
            }
//            } else if (bestMatch.startsWith("send message to")) {
//                processTextMessageCommand(bestMatch.substring(15));
//            }

            //isAssitantCalled = false;
        } else {
            if (voiceCommands[0].toLowerCase().equals(getPreference("trigger_command"))) {
                turnBeepOn();
                Toast.makeText(context, "begin command", Toast.LENGTH_SHORT).show();
                isAssitantCalled = true;
            } else {
                Toast.makeText(context, "not start command", Toast.LENGTH_SHORT).show();
                isAssitantCalled = false;
            }
        }
        restartListeningService();
    }

    private void processTextMessageCommand(String toSend) {
        if (!getContactName(toSend, this)) {
            //turnBeepOn();
            Log.i("There is no phone ", toSend);
                    /*tts.speak("There is no phone number for " + toCall,
                            TextToSpeech.QUEUE_FLUSH, null);*/
            HashMap<String, String> params = new HashMap<String, String>();
            params.put(TextToSpeech.Engine.KEY_PARAM_VOLUME, "1.0");
            tts.speak("There is no phone number for " + toSend,
                    TextToSpeech.QUEUE_ADD, params);
        }
    }

    public void sendSMS(String phoneNo, String msg) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, msg, null, null);
            Toast.makeText(getApplicationContext(), "Message Sent",
                    Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(),ex.getMessage().toString(),
                    Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }

    private Camera cam = null;
    private void processFlashlightSettings(String substring) {
        if (substring.equals("on")) {
            cam = Camera.open();
            Camera.Parameters p = cam.getParameters();
            p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            cam.setParameters(p);
            cam.startPreview();
        } else {
            if (cam != null) {
                cam.stopPreview();
                cam.release();
            }
        }
    }

    private void processWiFiSettings(String substring) {

        WifiManager wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        if (substring.equals("on")) {
            wifiManager.setWifiEnabled(true);
        } else {
            wifiManager.setWifiEnabled(false);
        }
    }

    private String processGeneralCustomCommand() {
        Log.wtf("processGeneralCustomCommand",getPreference("general_command"));
        return getPreference("general_command");
    }

    private boolean isCustomUserGeneralCommand(String bestMatch) {
        Log.wtf("isCustomUserGeneralCommand",
                getPreference("general_user_command").trim().toLowerCase());
        return getPreference("general_user_command").trim().toLowerCase().contains(bestMatch.toLowerCase());
    }

    public void playSearchArtist(String artist) {
        Intent intent = new Intent(MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH);
        intent.putExtra(MediaStore.EXTRA_MEDIA_FOCUS,
                MediaStore.Audio.Artists.ENTRY_CONTENT_TYPE);
        intent.putExtra(MediaStore.EXTRA_MEDIA_ARTIST, artist);
        intent.putExtra(SearchManager.QUERY, artist);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }


    public void openWebPage(String url) {

        Uri webpage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        if (intent.resolveActivity(getPackageManager()) != null) {
            Log.i("openWebPage", webpage.toString());
            startActivity(intent);
        }
    }

    private void processSearch(String substring) {
        if (substring.toLowerCase().endsWith("on youtube")) {
            searchOnYoutube(substring.substring(0, substring.length() - 11));
        } else if (substring.toLowerCase().endsWith("on facebook")) {
            searchOnFacebook(substring.substring(0, substring.length() - 12));
        } else {
            searchWeb(substring);
        }
    }

    private void showVoiceCommandAsTextView(String voiceCommand) {
        content.removeAllViews();
        TextView txt = new TextView(getApplicationContext());
        txt.setText(voiceCommand);
        txt.setTextSize(20);
        txt.setTextColor(Color.BLACK);
        txt.setGravity(Gravity.CENTER);
        content.addView(txt);
    }

    public void searchWeb(String query) {
        Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
        intent.putExtra(SearchManager.QUERY, query);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    public void searchOnYoutube(String query) {
        Intent intent = new Intent(Intent.ACTION_SEARCH);
        intent.setPackage("com.google.android.youtube");
        intent.putExtra(SearchManager.QUERY, query);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    public void searchOnFacebook(String query) {
//        try {
//            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://search/"));
//            startActivity(intent);
//        } catch (Exception e) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/search/top/?q=" + query)));
//        }
    }

    public void capturePhoto() {
        Intent intent = new Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    public void recordVideo() {
        Intent intent = new Intent(MediaStore.INTENT_ACTION_VIDEO_CAMERA);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private void processAlarmCommand(String bestMatch) {
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

    private void processCallCommand(String bestMatch) {
        try {
            String toCall = bestMatch.substring(5);
            if (toCall.matches(".*\\d.*")) {
                dialPhoneNumber(toCall);
            } else {
                if (!getContactName(toCall, this)) {
                    //turnBeepOn();
                    Log.i("There is no phone ", toCall);
                    /*tts.speak("There is no phone number for " + toCall,
                            TextToSpeech.QUEUE_FLUSH, null);*/
                    HashMap<String, String> params = new HashMap<String, String>();
                    params.put(TextToSpeech.Engine.KEY_PARAM_VOLUME, "1.0");
                    tts.speak("There is no phone number for " + toCall,
                            TextToSpeech.QUEUE_ADD, params);
                }
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
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

    public boolean getContactName(String name, Context context) {
        String ret = null;
        String selection = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " like'%" + name + "%'";
        String[] projection = new String[]{ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME};
        Cursor c = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projection, selection, null, null);
        ArrayList<String> contacts = new ArrayList<>();
        while (c.moveToNext()) {
            ret = c.getString(0);
            contacts.add(ret);
            Log.e("phone number", ret);
        }
        c.close();
        if (ret == null) {
            return false;
        } else {

            Bundle bundle = new Bundle();
            bundle.putStringArrayList(CONTACT_NAMES, contacts);
            FragmentUtils.addFragment(getSupportFragmentManager(), ChooseContactFragment.class,
                    R.id.container, bundle, true);

            return true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_recognition_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.toolbar_settings:
                startPreferenceActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public String getPreference(String key) {
        final SharedPreferences mSharedPreference = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());
        return mSharedPreference.getString(key, "").toLowerCase();
    }

    private void startPreferenceActivity() {
        Intent intent = new Intent(RecognitionActivity.this, SettingsPrefferenceActivity.class);
        startActivity(intent);
    }
}
