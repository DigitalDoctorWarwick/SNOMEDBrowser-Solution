package com.example.android.nhstest2;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class ConceptItemAdapter extends ArrayAdapter<ConceptItem> {

    ConceptItemAdapter(@NonNull Context context, @NonNull List<ConceptItem> objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null){
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        ConceptItem currentConceptItem = getItem(position);

        if (currentConceptItem != null) {

            TextView termView = listItemView.findViewById(R.id.term);
            termView.setText(currentConceptItem.getTerm());

            TextView fsnView = listItemView.findViewById(R.id.fsn);
            fsnView.setText(currentConceptItem.getFsn());

            TextView conceptView = listItemView.findViewById(R.id.concept);
            conceptView.setText(String.valueOf(currentConceptItem.getConceptId()));

        }
        return listItemView;
    }
}
