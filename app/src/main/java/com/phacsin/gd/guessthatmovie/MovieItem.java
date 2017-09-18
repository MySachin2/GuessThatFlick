package com.phacsin.gd.guessthatmovie;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import fr.rolandl.carousel.CarouselItem;

/**
 * Created by GD on 8/27/2017.
 */

public final class MovieItem extends CarouselItem<String>
{

    private TextView name_text;

    public MovieItem(Context context)
    {
        super(context, R.layout.carousel_item);
    }

    @Override
    public void extractView(View view)
    {
        name_text = (TextView) view.findViewById(R.id.name);
    }

    @Override
    public void update(String name)
    {
        name_text.setText(name);
    }

}