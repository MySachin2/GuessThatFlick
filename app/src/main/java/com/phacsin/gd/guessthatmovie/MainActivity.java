package com.phacsin.gd.guessthatmovie;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import fr.rolandl.carousel.Carousel;
import fr.rolandl.carousel.CarouselAdapter;
import fr.rolandl.carousel.CarouselBaseAdapter;

public class MainActivity extends AppCompatActivity {
    Carousel carousel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        carousel = (Carousel) findViewById(R.id.carousel);
        final List<String> movies = new ArrayList<>();
        movies.add("Hollywood");
        movies.add("Bollywood");
        movies.add("Tollywood");
        movies.add("Mollywood");
        final CarouselAdapter adapter  =  new CarouselViewAdapter(this, movies);
        carousel.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        final Intent intent = new Intent(MainActivity.this,GameActivity.class);
        carousel.setOnItemClickListener(new CarouselBaseAdapter.OnItemClickListener()
        {

            @Override
            public void onItemClick(CarouselBaseAdapter<?> carouselBaseAdapter, View view, int position, long id)
            {
                carousel.scrollToChild(position);
                switch (position)
                {
                    case 0:
                        intent.putExtra("type","Hollywood");
                        startActivity(intent);
                        break;
                    case 1:
                        intent.putExtra("type","Bollywood");
                        startActivity(intent);
                        break;
                    case 2:
                        intent.putExtra("type","Tollywood");
                        startActivity(intent);
                        break;
                    case 3:
                        intent.putExtra("type","Mollywood");
                        startActivity(intent);
                        break;
                }
            }

        });
    }

    public void onMoreClick(View view) {
        startActivity(new Intent(MainActivity.this,AboutMe.class));
    }
}
