package de.thm.ap.leistungen;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import de.thm.ap.leistungen.model.Record;

public class RecordArrayAdapter extends ArrayAdapter<Record> {
    private final static int VIEW_RESOURCE = R.layout.record_list_item;

    public RecordArrayAdapter(Context ctx, List<Record> records) {
        super(ctx, VIEW_RESOURCE, records);
    }

    @Override
    public View getView(int pos, View view, ViewGroup parent) {
        if (view == null) {
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            view = vi.inflate(VIEW_RESOURCE, null);
        }

        Record r = getItem(pos);
        TextView name = (TextView) view.findViewById(R.id.name);
        name.setText(r.getModuleName());
        TextView mark = (TextView) view.findViewById(R.id.mark);
        if(r.getMark() != null) mark.setText(r.getMark() + "%");
        TextView subtext = (TextView) view.findViewById(R.id.subtext);
        subtext.setText(r.getModuleNum() + " " + r.getCrp() + "crp");
        // TODO Viewholder optimierung nur 9 stück auf dem Bild auf einmal

        return view;
    }
}
