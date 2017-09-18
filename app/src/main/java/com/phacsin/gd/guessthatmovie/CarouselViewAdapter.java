package com.phacsin.gd.guessthatmovie;

import android.content.Context;

import java.util.List;

import fr.rolandl.carousel.CarouselAdapter;
import fr.rolandl.carousel.CarouselItem;

/**
 * Created by GD on 8/27/2017.
 */

public final class CarouselViewAdapter
        extends CarouselAdapter<String>
{

    public CarouselViewAdapter(Context context, List<String> photos)
    {
        super(context, photos);
    }

    @Override
    public CarouselItem<String> getCarouselItem(Context context)
    {
        return new MovieItem(context);
    }

}