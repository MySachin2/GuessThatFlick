package com.phacsin.gd.guessthatmovie;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class LetterAdapter extends RecyclerView.Adapter<LetterAdapter.MyViewHolder> {

    public List<String> lettersList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView letter_text;

        public MyViewHolder(View view) {
            super(view);
            letter_text = (TextView) view.findViewById(R.id.letter_text);
        }
    }


    public LetterAdapter(List<String> lettersList) {
        this.lettersList = lettersList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.letter_layout, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        String letter = lettersList.get(position);
        holder.letter_text.setText(letter);
    }

    @Override
    public int getItemCount() {
        return lettersList.size();
    }
}