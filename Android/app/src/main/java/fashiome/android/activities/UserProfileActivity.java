package fashiome.android.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;
import com.parse.FindCallback;
import com.parse.ParseException;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import fashiome.android.R;
import fashiome.android.models.SellerReview;
import fashiome.android.models.User;
import fashiome.android.utils.ImageURLGenerator;
import fashiome.android.utils.Utils;

/**
 * Created by dsaha on 3/11/16.
 */
public class UserProfileActivity extends AppCompatActivity {

    @Bind(R.id.rivImg)
    RoundedImageView rivProfilePicture;

    @Bind(R.id.tvUserFullname)
    TextView tvUserFullname;

    @Bind(R.id.tvUserCity)
    TextView tvUserCity;

    private String URLString = null;
    private User mUser = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_user_profile_v1);

        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final User user = (User) User.getCurrentUser();

        if (user != null) {
            URLString = ImageURLGenerator.getInstance(this).URLForFBProfilePicture(user.getFacebookId(), Utils.getScreenWidthInDp(this));
            tvUserFullname.setText(user.getUsername());
            tvUserCity.setText("San Francisco, CA");
        }

        Log.d("DEBUG", URLString);

        if (URLString != null || URLString.length() > 0) {
            Glide.with(this).load(URLString).into(rivProfilePicture);
        }

        /* Demo the call should be like this */
        SellerReview.fetchSellerReview(user, new FindCallback<SellerReview>() {
            @Override
            public void done(List<SellerReview> objects, ParseException e) {

            }
        });
    }


    /*
    private void initViewPagerAndTabs() {
        ViewPager viewPager = (ViewPager) findViewById(R.id.userProductsRecyclerViewContainer);
        PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager());
        pagerAdapter.addFragment(PartThreeFragment.createInstance(20), getString(R.string.tab_1));
        pagerAdapter.addFragment(PartThreeFragment.createInstance(4), getString(R.string.tab_2));
        viewPager.setAdapter(pagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /* this method is overridden to prevent the UP/BACK button_hollow from creating a new activity
instead of showing the old activity */
    @Override
    public Intent getSupportParentActivityIntent() {
        finish();
        return null;
    }

}
