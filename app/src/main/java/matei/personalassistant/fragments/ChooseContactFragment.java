package matei.personalassistant.fragments;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.ArrayList;

import matei.personalassistant.R;
import matei.personalassistant.utils.FragmentUtils;
import matei.personalassistant.voicerecognition.ListeningActivity;

import static matei.personalassistant.utils.ConstantUtils.CHOOSE_CONTACT;
import static matei.personalassistant.utils.ConstantUtils.CHOOSE_PHONE_NUMBER;
import static matei.personalassistant.utils.ConstantUtils.CONTACT_NAMES;

public class ChooseContactFragment extends ChooseFragment {

    @Override
    protected void action(String option) {
        getPhoneNumber(option, getActivity());
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
        rootView.removeAllViews();
        if (ret == null)
            return "Unsaved";
        else if (numbers.size() == 1) {
            dialPhoneNumber(ret);
            return "Unsaved";
        } else {
            Bundle bundle = new Bundle();
            bundle.putStringArrayList(CONTACT_NAMES, numbers);
            FragmentUtils.addFragment(getActivity().getSupportFragmentManager(),
                    ChoosePhoneNumberFragment.class, R.id.container, bundle, true);

//            ((ListeningActivity)getActivity()).getSr().stopListening();
//            ((ListeningActivity)getActivity()).turnBeepOn();
//            ((ListeningActivity)getActivity()).getTts().speak(CHOOSE_PHONE_NUMBER,
//                    TextToSpeech.QUEUE_ADD, null);
            return "Unsaved";
        }
    }

    public void dialPhoneNumber(String phoneNumber) {
        Log.i("phne number", phoneNumber);
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(intent);
        }
    }
}
