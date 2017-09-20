package com.phacsin.gd.guessthatmovie;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

public class AboutMe extends AppCompatActivity {
    String version;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        View aboutPage = new AboutPage(this)
                .isRTL(false)
                .setImage(R.drawable.small_icon)
                .setDescription("An app by Sachin Giridhar\nCTO Phacsin")
                .addItem(new Element().setTitle("Version " + version))
                .addGroup("Connect with us")
                .addEmail("sachingiridhar@gmail.com")
                .addWebsite("https://www.phacsin.com/")
                .addFacebook("phacsindevs")
                .addTwitter("phacsindevs")
                .addInstagram("phacsindevs")
                .addYoutube("kEa4eE1uFdlZnhGuU6aixg")
                .addGitHub("MySachin2")
                .create();
        setContentView(aboutPage);
    }
}
