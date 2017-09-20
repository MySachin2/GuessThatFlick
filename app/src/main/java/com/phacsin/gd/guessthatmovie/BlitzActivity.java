package com.phacsin.gd.guessthatmovie;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class BlitzActivity extends AppCompatActivity {
    ImageView next;
    ImageView letter_h,letter_o1,letter_l1,letter_l2,letter_y,letter_w,letter_o2,letter_o3,letter_d;

    ImageView[] letter_images;
    private DatabaseReference mRef;
    SharedPreferences sharedPreferences;
    List<ScoreClass> movie_names = new ArrayList<>();
    private String type;
    DBHandler dbhandler;
    int randomNum;
    private SharedPreferences.Editor editor;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_layout);
        next = (ImageView) findViewById(R.id.next);

        mRef = FirebaseDatabase.getInstance().getReference();
        sharedPreferences = getSharedPreferences("prefs", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        type = getIntent().getStringExtra("type");
        dbhandler = new DBHandler(getApplicationContext());
        if(!sharedPreferences.contains("points"))
        {
            editor.putInt("points",100);
            editor.commit();
        }
        letter_images = new ImageView[]{letter_h,letter_o1,letter_l1,letter_l2,letter_y,letter_w,letter_o2,letter_o3,letter_d};
        progressDialog = new ProgressDialog(BlitzActivity.this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Checking for new Flicks");
        progressDialog.setCancelable(false);
        progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showGame();
                dialog.dismiss();
            }
        });
        progressDialog.show();
        mRef.child("Movie").child(type).child("Blitz").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("DS",dataSnapshot.toString());
                movie_names.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    if(!dbhandler.movieExists(postSnapshot.getKey(),type)) {
                        dbhandler.insertScore(postSnapshot.getKey(),type,postSnapshot.child("letters").getValue(String.class),postSnapshot.child("hint").getValue(String.class));
                    }
                }
                if(progressDialog.isShowing()) {
                    showGame();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    public void showGame()
    {
        for(ScoreClass scoreClass : dbhandler.getAllScores(type))
        {
            movie_names.add(scoreClass);
        }
        progressDialog.dismiss();
        if(movie_names.size()!=0)
        {
            final int notPlayedCount = dbhandler.getNotPlayedMovieCount(type);
            final int completedCount = dbhandler.getCompletedMovieCount(type);

            Log.d("NotPlayedCount",notPlayedCount + "");
            Log.d("CompletedCount",completedCount+"");
            Log.d("PlayedCount",(movie_names.size()-completedCount)+"");
            Collections.reverse(movie_names);
            Random random = new Random();
            for(ScoreClass scoreClass:movie_names)
            {
                Log.d("MOVIE : STATUS",scoreClass.movie_name + scoreClass.status);
            }
            if(completedCount == movie_names.size())
            {
                randomNum = random.nextInt(movie_names.size());
            }
            else
            {
                if(notPlayedCount==0)
                    randomNum = random.nextInt(movie_names.size()-completedCount);
                else
                    randomNum = random.nextInt(notPlayedCount);
            }

            ScoreClass movie =  movie_names.get(randomNum);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            BlitzFragment fragment = new BlitzFragment();
            Bundle bundle = new Bundle();
            bundle.putString("type",type);
            bundle.putSerializable("scoreClass",movie);
            fragment.setArguments(bundle);
            transaction.add(R.id.container,fragment);
            transaction.commit();
            if(!dbhandler.getMoviePlayedStatus(movie.movie_name,type).equals("Completed"))
                dbhandler.updateStatus(movie.movie_name,type,"Played",1);
            if(movie_names.size()==1)
                next.setVisibility(View.GONE);
            else {
                next.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int newNum;
                        movie_names.clear();
                        for (ScoreClass scoreClass : dbhandler.getAllScores(type)) {
                            movie_names.add(scoreClass);
                        }
                        Collections.reverse(movie_names);
                        final int notPlayedCount = dbhandler.getNotPlayedMovieCount(type);
                        final int completedCount = dbhandler.getCompletedMovieCount(type);
                        Log.d("NotPlayedCount", notPlayedCount + "");
                        Log.d("CompletedCount", completedCount + "");
                        Log.d("PlayedCount", (movie_names.size() - completedCount - notPlayedCount) + "");
                        Random random = new Random();
                        int tries = 0;
                        do {
                            tries++;
                            if (completedCount == movie_names.size()) {
                                newNum = random.nextInt(movie_names.size());
                            } else {
                                if (notPlayedCount == 0)
                                    newNum = random.nextInt(movie_names.size() - completedCount);
                                else
                                    newNum = random.nextInt(notPlayedCount);
                            }
                            Log.d("RNG", randomNum + " : " + newNum);
                        } while (randomNum == newNum && tries<10);
                        randomNum = newNum;
                        ScoreClass movie = movie_names.get(randomNum);
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        BlitzFragment fragment = new BlitzFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("type", type);
                        bundle.putSerializable("scoreClass", movie);
                        transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
                        fragment.setArguments(bundle);
                        transaction.replace(R.id.container, fragment);
                        transaction.commit();
                        if (!dbhandler.getMoviePlayedStatus(movie.movie_name, type).equals("Completed"))
                            dbhandler.updateStatus(movie.movie_name, type, "Played", 1);
                    }
                });
            }
        }
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
