package fashiome.android.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import fashiome.android.R;
import fashiome.android.fragments.OnboardingCategorySelectionFragment;
import fashiome.android.fragments.OnboardingGenderSelectionFragment;

public class OnboardingWizardActivity extends AppCompatActivity {

    @Bind(R.id.skip)
    TextView mSkip;

    @Bind(R.id.next)
    TextView mNext;

    FragmentManager fm = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding_wizard);
        ButterKnife.bind(this);

        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        OnboardingGenderSelectionFragment f1 = new OnboardingGenderSelectionFragment();
        fragmentTransaction.replace(R.id.LinearLayout1, f1);
        fragmentTransaction.commit();

        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("info","Clicked");
                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                OnboardingCategorySelectionFragment f2 = new OnboardingCategorySelectionFragment();
                fragmentTransaction.replace(R.id.LinearLayout1, f2);
                fragmentTransaction.commit();
            }
        });


/*
        // get the display mode
        int displaymode = getResources().getConfiguration().orientation;
        if (displaymode == 1) { // it portrait mode
            OnboardingGenderSelectionFragment f1 = new OnboardingGenderSelectionFragment();
            fragmentTransaction.replace(R.id.LinearLayout1, f1);
        } else {// its landscape
            OnboardingCategorySelectionFragment f2 = new OnboardingCategorySelectionFragment();
            fragmentTransaction.replace(R.id.LinearLayout1, f2);
        }
        fragmentTransaction.commit();
*/

    }

}