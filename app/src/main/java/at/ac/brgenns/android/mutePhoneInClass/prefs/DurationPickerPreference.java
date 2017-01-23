package at.ac.brgenns.android.mutePhoneInClass.prefs;

import android.content.Context;
import android.content.Intent;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.NumberPicker;

import at.ac.brgenns.android.mutePhoneInClass.MutePhoneService;
import at.ac.brgenns.android.mutePhoneInClass.R;

/**
 * Created by Christoph on 20.06.2016.
 */
public class DurationPickerPreference extends DialogPreference
        implements NumberPicker.OnValueChangeListener {

    private NumberPicker durationPicker;
    private NumberPicker timeIntervalPicker;

    private static final String[] timeIntervals = {"min", "hours", "days", "forever"};
    private static final String[] minutes = {"15", "30", "45"};
    private Integer initialValue;

    public DurationPickerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        setDialogLayoutResource(R.layout.numberpicker_dialog);
        setPositiveButtonText(android.R.string.ok);
        setNegativeButtonText(android.R.string.cancel);

    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
        durationPicker = (NumberPicker) view.findViewById(R.id.durationPicker);
        timeIntervalPicker = (NumberPicker) view.findViewById(R.id.timeIntervalPicker);

        timeIntervalPicker.setDisplayedValues(timeIntervals);
        timeIntervalPicker.setMinValue(0);
        timeIntervalPicker.setMaxValue(timeIntervals.length - 1);
        timeIntervalPicker.setWrapSelectorWheel(true);
        timeIntervalPicker.setOnValueChangedListener(this);

        durationPicker.setWrapSelectorWheel(true);
        durationPicker.setOnValueChangedListener(this);

        setValue(getValue());
    }

    private void setValue(int value) {
        initialValue = value;

        if (initialValue >= 60 * 24) {
            durationPicker.setDisplayedValues(null);
            durationPicker.setMinValue(1);
            durationPicker.setMaxValue(31);
            timeIntervalPicker.setValue(2);
            durationPicker.setValue(initialValue / (60 * 24) - 1);
        } else if (initialValue >= 60) {
            durationPicker.setDisplayedValues(null);
            durationPicker.setMinValue(1);
            durationPicker.setMaxValue(23);
            timeIntervalPicker.setValue(1);
            durationPicker.setValue(initialValue / 60 - 1);
        } else {
            durationPicker.setDisplayedValues(minutes);
            durationPicker.setMinValue(0);
            durationPicker.setMaxValue(minutes.length - 1);
            durationPicker.setValue(initialValue / 15 - 1);
            if (initialValue == 0) {
                durationPicker.setEnabled(false);
                timeIntervalPicker.setValue(3);
            } else {
                timeIntervalPicker.setValue(0);
            }
        }
        persistInt(value);
    }

    private void updateTitle() {
        String disableFor = getContext().getResources().getString(R.string.disable_for);
        if (getValue() == 0) {
            setTitle(disableFor + getContext().getResources().getString(R.string.ever));
        } else {
            int duration = getValue();
            int interval = 0;
            if (duration >= 60) {
                duration = duration / 60 - 1;
                interval = 1;
            } else if (duration >= 60 * 24) {
                duration = duration / (60 * 24) - 1;
                interval = 2;
            }
            setTitle(disableFor + " " + duration + timeIntervals[interval]);
        }
    }

    private int getValue() {
        return initialValue != null ? initialValue : 0;
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        int def = (defaultValue instanceof Number) ? (Integer) defaultValue
                : (defaultValue != null) ? Integer.parseInt(defaultValue.toString()) : 0;
        if (restorePersistedValue) {
            this.initialValue = getPersistedInt(def);
        } else {
            this.initialValue = (Integer) defaultValue;
        }
        updateTitle();
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            persistInt(initialValue);
            updateTitle();
        }
        Intent mutePhoneService = new Intent(this.getContext(), MutePhoneService.class);
        this.getContext().startService(mutePhoneService);
    }

    public void show() {
        showDialog(null);
    }

    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
        int durationVal = newVal + 1;
        if (picker.getId() == R.id.timeIntervalPicker) {
            durationVal = durationPicker.getValue() + 1;
            switch (newVal) {
                case 0:
                    durationPicker.setEnabled(true);
                    durationPicker.setMinValue(0);
                    durationPicker.setMaxValue(minutes.length - 1);
                    durationPicker.setDisplayedValues(minutes);
                    initialValue = durationVal * 15;
                    break;
                case 1:
                    durationPicker.setEnabled(true);
                    durationPicker.setDisplayedValues(null);
                    durationPicker.setMinValue(1);
                    durationPicker.setMaxValue(23);
                    initialValue = durationVal * 60;
                    break;
                case 2:
                    durationPicker.setEnabled(true);
                    durationPicker.setDisplayedValues(null);
                    durationPicker.setMinValue(1);
                    durationPicker.setMaxValue(31);
                    initialValue = durationVal * 60 * 24;
                    break;
                case 3:
                    durationPicker.setEnabled(false);
                    initialValue = 0;
            }
        } else {
            switch (timeIntervalPicker.getValue()) {
                case 0:
                    initialValue = durationVal * 15;
                    break;
                case 1:
                    initialValue = durationVal * 60;
                    break;
                case 2:
                    initialValue = durationVal * 60 * 24;
                    break;
            }
        }

    }
}
