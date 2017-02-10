package matei.personalassistant.fragments;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;

public class ChoosePhoneNumberFragment extends ChooseFragment {
    @Override
    protected void action(String option) {
        dialPhoneNumber(option);
    }

    public void dialPhoneNumber(String phoneNumber) {
        Log.i("phne number", phoneNumber);
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().onBackPressed();
    }
}
