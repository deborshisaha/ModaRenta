package fashiome.android.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;
import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;
import java.util.Random;

import butterknife.Bind;
import butterknife.ButterKnife;
import fashiome.android.R;
import fashiome.android.adapters.UserProfilePagerAdapter;
import fashiome.android.models.User;
import fashiome.android.utils.ImageURLGenerator;
import fashiome.android.utils.Utils;

public class UserDetailsActivity extends AppCompatActivity {

    User user;
    UserProfilePagerAdapter adapter = null;
    ViewPager viewPager = null;
    PagerSlidingTabStrip tabs = null;

    @Bind(R.id.rivImg)
    RoundedImageView rivProfilePicture;

    @Bind(R.id.tvUserFullname)
    TextView tvUserFullname;

    @Bind(R.id.tvUserCity)
    TextView tvUserCity;

    String URLString = null;

    CollapsingToolbarLayout cl;

    String objectId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        cl = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        objectId = getIntent().getStringExtra("objectId");

        Log.i("Current user Id: ",ParseUser.getCurrentUser().getObjectId());
        Log.i("Clicked user Id: ",objectId);
        //Log.i("info", user.getUsername() + user.getEmail() + user.getProfilePictureURL() + user.getObjectId().toString());
        parseCallToGetUser(objectId);

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        viewPager = (ViewPager) findViewById(R.id.viewpager1);
        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);

        LinearLayout mLL1 = (LinearLayout)findViewById(R.id.llRatingBar1);
        int i;
        int temp = new Random().nextInt(4) + 1;
        for(i = 0;i<temp;i++){

            ImageView iv = new ImageView(this);
            iv.setImageResource(R.drawable.ic_star_filled);
            android.view.ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(48,48);
            iv.setLayoutParams(layoutParams);
            mLL1.addView(iv);
        }

        if(i < 5){
            for( ;i<5;i++){
                ImageView iv = new ImageView(this);
                iv.setImageResource(R.drawable.ic_star_empty);
                android.view.ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(48,48);
                iv.setLayoutParams(layoutParams);
                mLL1.addView(iv);
            }

        }


    }

    public void setupViewPagerTabs(){

        if(objectId.equals(ParseUser.getCurrentUser().getObjectId())){
            adapter = new UserProfilePagerAdapter(getSupportFragmentManager(), user, true);
        } else {
            adapter = new UserProfilePagerAdapter(getSupportFragmentManager(), user, false);
        }
        viewPager.setAdapter(adapter);
        // Bind the tabs to the ViewPager
        tabs.setViewPager(viewPager);
        tabs.setIndicatorHeight(8);
        tabs.setIndicatorColor(0xFF55ACEE);
        tabs.setTextColor(0xFF55ACEE);

    }

    public void parseCallToGetUser(String objectId){

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("objectId", objectId);
        query.findInBackground(new FindCallback<ParseUser>() {
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null) {
                    Log.i("info", "found");
                    ParseUser u = objects.get(0);
                    user = (User) objects.get(0);
                    Log.i("info", u.getString("profilePictureUrl"));
                    Log.i("info", user.getProfilePictureURL());
                    //cl.setTitle(user.getUsername());
                    //getSupportActionBar().setTitle(user.getUsername());
                    setupHeader();
                    setupViewPagerTabs();
                    // The query was successful.
                } else {
                    // Something went wrong.
                }
            }
        });
    }

    public void setupHeader(){

        //final User user = (User) User.getCurrentUser();

        if (user != null) {
            URLString = ImageURLGenerator.getInstance(this).URLForFBProfilePicture(user.getFacebookId(), Utils.getScreenWidthInDp(this));
            tvUserFullname.setText(user.getUsername());
            tvUserCity.setText("San Francisco, CA");
        }

        Log.d("DEBUG", URLString);

        if (URLString != null || URLString.length() > 0) {
            Glide.with(this).load(URLString).into(rivProfilePicture);
        }

    }

    /* this method is overridden to prevent the UP/BACK button_hollow from creating a new activity
instead of showing the old activity */
    @Override
    public Intent getSupportParentActivityIntent() {
        finish();
        return null;
    }

}


