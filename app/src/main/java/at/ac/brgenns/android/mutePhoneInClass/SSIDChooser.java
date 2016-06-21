package at.ac.brgenns.android.mutePhoneInClass;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.DialogPreference;

/**
 * Created by User on 30.03.2016.
 */
public class SSIDChooser extends DialogFragment {

    private String[] iwosiwos;
    private SSIDChosenListener dialogListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            dialogListener = (SSIDChosenListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + "must implement SSIDChosenListener");
        }
    }

    @Override
    public android.app.Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder select_wifi = new AlertDialog.Builder(getActivity());
        select_wifi.setTitle(R.string.wifi_select)
                .setItems(iwosiwos, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int i) {
                        dialogListener.onSelectItem(i);
                    }

                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setNeutralButton("More", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogListener.onSelectItem(-1);
                    }
                });

        return select_wifi.create();
    }

    public void setOptions(String[] ssids) {
        iwosiwos = ssids;
    }

    public interface SSIDChosenListener {
        public void onSelectItem(int i);
    }
}