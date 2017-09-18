package com.phacsin.gd.guessthatmovie;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.robinhood.ticker.TickerUtils;
import com.robinhood.ticker.TickerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;


public class GameFragment extends Fragment {
    ImageView back;
    TickerView tickerView,movie_name_textView,available_points;
    int score;
    RecyclerView recyclerView;
    List<String> letters = new ArrayList<>();
    LetterAdapter letterAdapter ;
    ScaleAnimation expand;
    ImageView letter_h,letter_o1,letter_l1,letter_l2,letter_y,letter_w,letter_o2,letter_o3,letter_d;
    TextView leading_letter,hint_text;
    String[] letters_shown,letters_lost;

    ImageView[] letter_images;
    private DatabaseReference mRef;
    SharedPreferences sharedPreferences;
    List<ScoreClass> movie_names = new ArrayList<>();
    int chance = 0;
    String original_movie_name,temp_movie_name;
    boolean flag;
    private String type;
    DBHandler dbhandler;
    ScoreClass movie;
    String letters_shown_str,letters_lost_str;
    private Button show_hint_main,cancel_btn,show_btn;
    SharedPreferences.Editor editor;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_game, container, false);
        mRef = FirebaseDatabase.getInstance().getReference();
        sharedPreferences = getActivity().getSharedPreferences("prefs", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        type = getArguments().getString("type");
        dbhandler = new DBHandler(getActivity());

        tickerView = (TickerView) rootView.findViewById(R.id.tickerView);
        tickerView.setCharacterList(TickerUtils.getDefaultNumberList());
        tickerView.setTextColor(Color.BLACK);
        Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Lato-Black.ttf");
        tickerView.setTypeface(font);
        tickerView.setAnimationDuration(500);
        tickerView.setAnimationInterpolator(new OvershootInterpolator());
        tickerView.setGravity(Gravity.START);

        movie_name_textView = (TickerView) rootView.findViewById(R.id.movie_name);
        movie_name_textView.setCharacterList(TickerUtils.getDefaultNumberList());
        movie_name_textView.setTextColor(Color.BLACK);
        movie_name_textView.setTypeface(font);
        movie_name_textView.setAnimationDuration(500);
        movie_name_textView.setAnimationInterpolator(new OvershootInterpolator());
        movie_name_textView.setGravity(Gravity.START);

        show_hint_main = (Button) getActivity().findViewById(R.id.show_hint);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        int numOfColumns = Utility.calculateNoOfColumns(getActivity());
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(),numOfColumns);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        letterAdapter = new LetterAdapter(letters);
        movie = (ScoreClass) getArguments().getSerializable("scoreClass");
        leading_letter  = (TextView) rootView.findViewById(R.id.leading_letter);
        leading_letter.setText(type.substring(0,1));
        hint_text  = (TextView) rootView.findViewById(R.id.hint);
        if(movie.hint==null)
            show_hint_main.setVisibility(View.GONE);
        else {
            if (dbhandler.isPaid(movie.movie_name, type)) {
                hint_text.setText(movie.hint);
                show_hint_main.setVisibility(View.GONE);
            } else {
                show_hint_main.setVisibility(View.VISIBLE);
            }
        }

        letter_h  = (ImageView) rootView.findViewById(R.id.letter_h);
        letter_o1  = (ImageView) rootView.findViewById(R.id.letter_o1);
        letter_l1  = (ImageView) rootView.findViewById(R.id.letter_l1);
        letter_l2  = (ImageView) rootView.findViewById(R.id.letter_l2);
        letter_y  = (ImageView) rootView.findViewById(R.id.letter_y);
        letter_w  = (ImageView) rootView.findViewById(R.id.letter_w);
        letter_o2 = (ImageView) rootView.findViewById(R.id.letter_o2);
        letter_o3  = (ImageView) rootView.findViewById(R.id.letter_o3);
        letter_d  = (ImageView) rootView.findViewById(R.id.letter_d);

        letter_images = new ImageView[]{letter_h,letter_o1,letter_l1,letter_l2,letter_y,letter_w,letter_o2,letter_o3,letter_d};

        expand = new ScaleAnimation(
                0f, 1f,
                0f, 1f,
                Animation.RELATIVE_TO_PARENT, 0,
                Animation.RELATIVE_TO_PARENT, 0);
        expand.setDuration(500);

        expand.setFillAfter(true);
        expand.setInterpolator(new AccelerateInterpolator(1.0f));
        Log.d("SS","SHOW");
        showAllViews();

        show_hint_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(getActivity());
                dialog.setContentView(R.layout.dialog_points);
                available_points = (TickerView) dialog.findViewById(R.id.available_points);
                available_points.setCharacterList(TickerUtils.getDefaultNumberList());
                available_points.setTextColor(Color.BLACK);
                Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Lato-Black.ttf");
                available_points.setTypeface(font);
                available_points.setAnimationDuration(500);
                available_points.setAnimationInterpolator(new OvershootInterpolator());
                available_points.setGravity(Gravity.START);
                final int available = sharedPreferences.getInt("points",0);
                available_points.setText(String.valueOf(available));
                show_btn = (Button) dialog.findViewById(R.id.show_btn);
                cancel_btn = (Button) dialog.findViewById(R.id.cancel_btn);
                dialog.show();
                show_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(available<50)
                            Toast.makeText(getActivity(),"Not Enough Points",Toast.LENGTH_LONG).show();
                        else
                        {
                            hint_text.setText(movie.hint);
                            editor.putInt("points",available-50);
                            editor.commit();
                            dialog.dismiss();
                            dbhandler.updatePaid(original_movie_name,type,1);
                        }
                    }
                });
                cancel_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

            }
        });

        return rootView;
    }

    private void showAllViews() {
        original_movie_name = movie.movie_name;
        temp_movie_name = original_movie_name;
        letters_shown_str = movie.letters_shown;
        letters_lost_str = movie.letters_lost;
        score = Integer.parseInt(movie.score);
        tickerView.setText(score + " ");
        letters_shown = movie.letters_shown.split(",");
        if(movie.letters_lost != null) {
            if(!movie.letters_lost.equals("null")) {
                    letters_lost = movie.letters_lost.split(",");
                    Log.d("Letters_lost", letters_lost.length + " ");
                    chance = chance + letters_lost.length;
                    for (int i = 0; i < chance; i++) {
                        letter_images[i].setVisibility(View.VISIBLE);
                    }
            }
            else
            {
                letters_lost_str = null;
            }
        }

        for (int i = 0; i < temp_movie_name.length(); i++){
            char c = temp_movie_name.charAt(i);
            flag = false;
            for(int j=0;j<letters_shown.length;j++) {
                if (String.valueOf(c).equals(letters_shown[j]))
                {
                    flag = true;
                    break;
                }
            }
            if(!flag && c!= ' ')
                temp_movie_name = temp_movie_name.replace(c,'_');
        }
        movie_name_textView.setText(temp_movie_name.replace(" ","  "));
        prepareLetters();
        recyclerView.setAdapter(letterAdapter);
        Log.d("TAG",dbhandler.getMovieCount("type") + " ");
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String selected = letters.get(position);
                flag = false;
                for (int i = 0; i < original_movie_name.length(); i++){
                    char c = original_movie_name.charAt(i);
                    if(String.valueOf(c).equals(selected))
                    {
                        char[] myNameChars = temp_movie_name.toCharArray();
                        myNameChars[i] = c;
                        temp_movie_name = String.valueOf(myNameChars);
                        flag = true;
                    }
                }
                if(!flag)
                {
                    if(chance<8)
                    {
                        letter_images[chance].setVisibility(View.VISIBLE);
                        letter_images[chance].startAnimation(expand);
                        chance++;
                        score = score - 10;
                        tickerView.setText(String.valueOf(score));
                        if(letters_lost_str != null)
                            letters_lost_str = letters_lost_str + "," + selected;
                        else
                            letters_lost_str = selected;
                        Log.d("letters_lost",letters_lost_str  + " LETTTeRS");
                        dbhandler.updateScore(original_movie_name,type,String.valueOf(score),letters_shown_str,letters_lost_str);
                    }
                    else if(chance==8) {
                        letter_images[chance].setVisibility(View.VISIBLE);
                        letter_images[chance].startAnimation(expand);
                        score = score - 10;
                        tickerView.setText(String.valueOf(score));
                        chance++;
                        dbhandler.updateScore(original_movie_name,type,String.valueOf(score),letters_shown_str,letters_lost_str);
                        dbhandler.updateStatus(original_movie_name,type,"Completed",2);
                        movie_name_textView.setText(original_movie_name);
                        Toast.makeText(getActivity(), "YOU LOSE!!", Toast.LENGTH_LONG).show();
                    }
                }
                else
                {
                    movie_name_textView.setText(temp_movie_name.replace(" ","  "));
                    letters_shown_str = letters_shown_str + "," + selected;
                    Log.d("letters_shown",letters_shown_str  + " LETTTeRS");
                    Log.d("letters_lost",letters_lost_str  + " LETTTeRS");
                    dbhandler.updateScore(original_movie_name, type, String.valueOf(score),letters_shown_str,letters_lost_str);
                    if(temp_movie_name.equals(original_movie_name)) {
                        dbhandler.updateStatus(original_movie_name,type,"Completed",2);
                        int points = sharedPreferences.getInt("points",0);
                        editor.putInt("points",points + score);
                        editor.commit();
                        Toast.makeText(getActivity(), "YOU WIN!!", Toast.LENGTH_LONG).show();
                    }
                }
                letters.remove(position);
                letterAdapter.lettersList = letters;
                letterAdapter.notifyItemRemoved(position);
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }

        }));

    }

    public void prepareLetters()
    {
        int s = 'A';
        if(letters_lost != null) {
            while (s <= 'Z') {
                if (!Arrays.asList(letters_shown).contains(String.valueOf((char) s)) && !Arrays.asList(letters_lost).contains(String.valueOf((char) s))) {
                    letters.add(String.valueOf((char) s));
                }
                s++;
            }
        }
        else
        {
            while (s <= 'Z') {
                if (!Arrays.asList(letters_shown).contains(String.valueOf((char) s))) {
                    letters.add(String.valueOf((char) s));
                }
                s++;
            }
        }
    }

}
