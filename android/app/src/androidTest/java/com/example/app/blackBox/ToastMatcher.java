package com.example.app.blackBox;

import android.os.IBinder;
import android.provider.DocumentsContract;
import android.view.WindowManager;

import androidx.test.espresso.Root;
import androidx.test.rule.ActivityTestRule;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
//Code from: http://www.qaautomated.com/2016/01/how-to-test-toast-message-using-espresso.html
public class ToastMatcher extends TypeSafeMatcher<Root> {

    @Override    public void describeTo(Description description) {
        description.appendText("is toast");
    }

    @Override    public boolean matchesSafely(Root root) {
        int type = root.getWindowLayoutParams().get().type;
        if ((type-5 == WindowManager.LayoutParams.FIRST_SYSTEM_WINDOW)) {
            IBinder windowToken = root.getDecorView().getWindowToken();
            IBinder appToken = root.getDecorView().getApplicationWindowToken();
            if (windowToken == appToken) {
                return true;
            }
        }
        return false;
    }
}
