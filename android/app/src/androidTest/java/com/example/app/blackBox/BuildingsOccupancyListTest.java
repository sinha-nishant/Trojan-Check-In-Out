package com.example.app.blackBox;

import android.content.Context;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.rule.ActivityTestRule;
import com.example.app.R;
import com.example.app.building.Building;
import com.example.app.building.BuildingsOccupancyList;
import com.example.app.firebaseDB.FbQuery;
import com.google.firebase.FirebaseApp;
import org.junit.Rule;
import org.junit.Test;
import java.util.List;
import javax.annotation.Nullable;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.hasSibling;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
public class BuildingsOccupancyListTest {

    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    @Rule
    public ActivityTestRule<BuildingsOccupancyList> menuActivityTestRule = new ActivityTestRule<>(BuildingsOccupancyList.class);

    public static List<Building>  post_value;

    @Test
    public void onStart() {

        Context context = ApplicationProvider.getApplicationContext();
        FirebaseApp.initializeApp(context);
        MutableLiveData<List<Building>> builingMLD = new MutableLiveData<>();

        final Observer<List<Building>> observer = new Observer<List<Building>>(){
            @Override
            public void onChanged(@Nullable final List<Building> buildings){
                post_value=buildings;
            }
        };
        builingMLD.observeForever(observer);
        FbQuery.getAllBuildings(builingMLD);

        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for(int i=0;i<post_value.size();i++){
            String occupancymessage = "Occupancy: "+post_value.get(i).getOccupancy().toString()+"/100";
            onView(ViewMatchers.withId(R.id.recyclerList))
                    .perform(RecyclerViewActions
                            .scrollToPosition(i))
                    .check(matches(hasDescendant(allOf(withText(post_value.get(i).getName()), hasSibling(withText(occupancymessage))))));

        }
    }

}