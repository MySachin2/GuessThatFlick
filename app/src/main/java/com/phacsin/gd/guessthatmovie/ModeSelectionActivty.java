package com.phacsin.gd.guessthatmovie;

import android.annotation.TargetApi;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.azoft.carousellayoutmanager.CarouselLayoutManager;
import com.azoft.carousellayoutmanager.CarouselZoomPostLayoutListener;
import com.azoft.carousellayoutmanager.CenterScrollListener;

import java.util.List;

public class ModeSelectionActivty extends AppCompatActivity {
    RecyclerView recyclerView;
    CarouselLayoutManager layoutManager;
    String type;
    private TextView main_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mode);
        type = getIntent().getStringExtra("type");
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        main_text = (TextView) findViewById(R.id.main_text);
        main_text.setText(type);
        layoutManager = new CarouselLayoutManager(CarouselLayoutManager.HORIZONTAL);
        layoutManager.setPostLayoutListener(new CarouselZoomPostLayoutListener());
        layoutManager.setMaxVisibleItems(1);
        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addOnScrollListener(new CenterScrollListener());
        recyclerView.setHasFixedSize(true);
        String[] modes = new String[]{"Casual","Blitz"};
        recyclerView.setAdapter(new ModeAdapter(modes));
    }

    public class ModeAdapter extends RecyclerView.Adapter<ModeAdapter.MyViewHolder> {

        private String[] modesList;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView title;

            public MyViewHolder(View view) {
                super(view);
                title = (TextView) view.findViewById(R.id.name);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int position = getLayoutPosition();
                        Intent intent;
                        switch (modesList[position])
                        {
                            case "Casual":
                                intent = new Intent(ModeSelectionActivty.this,GameActivity.class);
                                intent.putExtra("type",type);
                                startActivity(intent);
                                break;
                            case "Blitz":
                                intent = new Intent(ModeSelectionActivty.this,BlitzActivity.class);
                                intent.putExtra("type",type);
                                startActivity(intent);
                                break;
                        }

                    }
                });
            }
        }


    public ModeAdapter(String[] modesList) {
            this.modesList = modesList;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.card_mode, parent, false);

            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            String mode = modesList[position];
            holder.title.setText(mode);
        }

        @Override
        public int getItemCount() {
            return modesList.length;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Transition slide = TransitionInflater.from(this).inflateTransition(R.transition.slide_transition);
        getWindow().setEnterTransition(slide);
    }
}
