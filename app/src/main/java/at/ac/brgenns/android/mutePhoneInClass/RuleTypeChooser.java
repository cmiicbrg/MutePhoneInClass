package at.ac.brgenns.android.mutePhoneInClass;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Created by User on 30.03.2016.
 */
public class RuleTypeChooser extends DialogFragment {

    private String[] iwosiwos;
    private RuleTypeChosenListener dialogListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            dialogListener = (RuleTypeChosenListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + "must implement RuleTypeChosenListener");
        }
    }

    @Override
    public android.app.Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder select_wifi = new AlertDialog.Builder(getActivity());
        select_wifi.setTitle(R.string.add_rule)
                .setItems(R.array.rule_types, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int i) {
                        dialogListener.onSelectRuleType(i);
                    }

                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });


        return select_wifi.create();
    }

    public void setOptions(String[] ssids) {
        iwosiwos = ssids;
    }

    public interface RuleTypeChosenListener {
        public void onSelectRuleType(int i);
    }
}