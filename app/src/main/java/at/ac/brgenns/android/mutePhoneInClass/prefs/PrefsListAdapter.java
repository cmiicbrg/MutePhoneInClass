package at.ac.brgenns.android.mutePhoneInClass.prefs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import at.ac.brgenns.android.mutePhoneInClass.R;
import at.ac.brgenns.android.mutePhoneInClass.prefs.model.SoundProfile;

/**
 * Created by Christoph on 18.05.2016.
 */
public class PrefsListAdapter extends ArrayAdapter {
    private final Context context;
    private final List list;

    public PrefsListAdapter(Context context, List list) {
        super(context, -1, list);

        this.list = list;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View eventRow = inflater.inflate(R.layout.event_row, parent, false);
        ImageView ic = (ImageView) eventRow.findViewById(R.id.imageView);
        TextView tv1 = (TextView) eventRow.findViewById(R.id.textView);
        TextView tv2 = (TextView) eventRow.findViewById(R.id.textView2);
        ic.setImageResource(R.drawable.ic_stat_name);
        tv1.setText(((SoundProfile) list.get(position)).getName());
        tv2.setText(Integer.toString(((SoundProfile) list.get(position)).getRingVolume()));

        return eventRow;
    }
}
