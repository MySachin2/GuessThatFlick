package com.phacsin.gd.guessthatmovie;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
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
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.plattysoft.leonids.ParticleSystem;
import com.robinhood.ticker.TickerUtils;
import com.robinhood.ticker.TickerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TimerTask;

import static android.content.Context.MODE_PRIVATE;


public class BlitzFragment extends Fragment {
    ImageView back;
    View rootView;
    TickerView movie_name_textView,available_points;
    int score;
    RecyclerView recyclerView;
    List<String> letters = new ArrayList<>();
    LetterAdapter letterAdapter ;
    ScaleAnimation expand;
    ImageView letter_h,letter_o1,letter_l1,letter_l2,letter_y,letter_w,letter_o2,letter_o3,letter_d;
    TextView leading_letter,hint_text,text_win;
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
    TextView timer;
    ImageView next;
    long MillisecondTime, StartTime, TimeBuff, UpdateTime = 0L ;

    Handler handler;

    int Seconds, Minutes, MilliSeconds ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_blitz, container, false);
        mRef = FirebaseDatabase.getInstance().getReference();
        sharedPreferences = getActivity().getSharedPreferences("prefs", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        type = getArguments().getString("type");
        dbhandler = new DBHandler(getActivity());

        Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Lato-Black.ttf");


        movie_name_textView = (TickerView) rootView.findViewById(R.id.movie_name);
        movie_name_textView.setCharacterList(TickerUtils.getDefaultNumberList());
        movie_name_textView.setTextColor(Color.BLACK);
        movie_name_textView.setTypeface(font);
        movie_name_textView.setAnimationDuration(500);
        movie_name_textView.setAnimationInterpolator(new OvershootInterpolator());
        movie_name_textView.setGravity(Gravity.START);

        show_hint_main = (Button) getActivity().findViewById(R.id.show_hint);
        show_hint_main.setVisibility(View.VISIBLE);
        next = (ImageView) getActivity().findViewById(R.id.next);
        timer = (TextView) rootView.findViewById(R.id.timer);

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
        text_win  = (TextView) rootView.findViewById(R.id.text_win);

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
        next.setVisibility(View.GONE);

        return rootView;
    }

    private void showAllViews() {
        original_movie_name = movie.movie_name;
        temp_movie_name = original_movie_name;
        letters_shown_str = movie.letters_shown;
        score = Integer.parseInt(movie.score);
        letters_shown = movie.letters_shown.split(",");

        //Initial Movie Checking
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
        runTimer();
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
                        Log.d("letters_lost",letters_lost_str  + " LETTTeRS");
                    }
                    else if(chance==8) {
                        handler.removeCallbacks(runnable);
                        letter_images[chance].setVisibility(View.VISIBLE);
                        letter_images[chance].startAnimation(expand);
                        score = 0;
                        chance++;
                        String time_taken = timer.getText().toString();
                        dbhandler.updateBlitzScore(original_movie_name,type,String.valueOf(score),time_taken);
                        dbhandler.updateStatus(original_movie_name,type,"Completed",2);
                        movie_name_textView.setText(original_movie_name);
                        text_win.setVisibility(View.VISIBLE);
                        text_win.setText("GAME OVER!");
                        recyclerView.setVisibility(View.INVISIBLE);
                        show_hint_main.setVisibility(View.GONE);
                        next.setVisibility(View.VISIBLE);
                    }
                }
                else
                {
                    movie_name_textView.setText(temp_movie_name.replace(" ","  "));
                    letters_shown_str = letters_shown_str + "," + selected;
                    Log.d("letters_shown",letters_shown_str  + " LETTTeRS");
                    Log.d("letters_lost",letters_lost_str  + " LETTTeRS");
                    String time = timer.getText().toString();
                    if(temp_movie_name.equals(original_movie_name)) {
                        handler.removeCallbacks(runnable);
                        dbhandler.updateBlitzScore(original_movie_name, type, String.valueOf(score),time);
                        if(Seconds<=15)
                            score = 150 - chance*10;
                        else if(Seconds<=25)
                            score = 120 - chance*10;
                        else
                            score = 90 - chance*10;

                        dbhandler.updateStatus(original_movie_name,type,"Completed",2);
                        int points = sharedPreferences.getInt("points",0);
                        editor.putInt("points",points + score);
                        editor.commit();
                        Animation animation = AnimationUtils.loadAnimation(getActivity(),R.anim.scale_anim);
                        animation.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                                text_win.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });


                        text_win.setText("YOU WIN!!\nScore : " + score);
                        text_win.setAnimation(AnimationUtils.loadAnimation(getActivity(),R.anim.scale_anim));
                        text_win.startAnimation(animation);
                        recyclerView.setVisibility(View.INVISIBLE);
                        show_hint_main.setVisibility(View.GONE);
                        ParticleSystem ps = new ParticleSystem(getActivity(), 100, R.drawable.confetti, 800);
                        ps.setScaleRange(0.7f, 1.3f);
                        ps.setSpeedRange(0.1f, 0.25f);
                        ps.setRotationSpeedRange(90, 180);
                        ps.setFadeOut(200, new AccelerateInterpolator());
                        ps.oneShot(rootView.findViewById(R.id.firework_layout), 70);

                        ParticleSystem ps2 = new ParticleSystem(getActivity(), 100, R.drawable.confetti_white, 800);
                        ps2.setScaleRange(0.7f, 1.3f);
                        ps2.setSpeedRange(0.1f, 0.25f);
                        ps.setRotationSpeedRange(90, 180);
                        ps2.setFadeOut(200, new AccelerateInterpolator());
                        ps2.oneShot(rootView.findViewById(R.id.firework_layout), 70);
                        next.setVisibility(View.VISIBLE);
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

    private void runTimer() {
        timer.setVisibility(View.VISIBLE);
        handler = new Handler();
        StartTime = SystemClock.uptimeMillis();
        handler.postDelayed(runnable, 0);
    }

    public Runnable runnable = new Runnable() {

        public void run() {
            MillisecondTime = SystemClock.uptimeMillis() - StartTime;

            UpdateTime = TimeBuff + MillisecondTime;

            Seconds = (int) (UpdateTime / 1000);

            Seconds = Seconds % 60;

            MilliSeconds = (int) (UpdateTime % 1000);

            timer.setText(String.format("%02d", Seconds) + ":"
                    + String.format("%02d", MilliSeconds));

            handler.postDelayed(this, 100);
        }

    };

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
