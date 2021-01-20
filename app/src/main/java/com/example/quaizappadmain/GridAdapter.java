package com.example.quaizappadmain;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class GridAdapter extends BaseAdapter {
    private final String category;
    public int sets = 0;
    private GridListener listener;

    public GridAdapter(int sets, String category, GridListener listener) {
        this.sets = sets;
        this.category = category;
        this.listener = listener;
    }



    @Override
    public int getCount() {
        return sets + 1;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.set_item, parent, false);
        } else {
            view = convertView;
        }
        if (position == 0) {
            ((TextView) view.findViewById(R.id.text_view)).setText("+");
        } else {
            ((TextView) view.findViewById(R.id.text_view)).setText(String.valueOf(position));
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position == 0) {
                    listener.addSets();
                } else {





                Intent questionIntent = new Intent(parent.getContext(), QuastionsActivity.class);
                questionIntent.putExtra("category", category);
                questionIntent.putExtra("setNo", position);
                parent.getContext().startActivity(questionIntent);


                }
            }
        });

        return view;
    }

    public interface GridListener {
        public void addSets();
    }
}
