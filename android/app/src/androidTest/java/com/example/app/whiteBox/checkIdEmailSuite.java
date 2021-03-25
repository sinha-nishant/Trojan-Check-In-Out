package com.example.app.whiteBox;

import com.example.app.blackBox.BuildingsOccupancyListTest;
import com.example.app.blackBox.CreateStudentTest;
import com.example.app.blackBox.StudentDeleteTest;
import com.example.app.whiteBox.FbCheckUsedEmailTest;
import com.example.app.whiteBox.FbCheckUsedIdTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)

@Suite.SuiteClasses({FbCreateAccountTest.class, FbCheckUsedEmailTest.class, FbCheckUsedIdTest.class,
        FbDeleteAccountTest.class})


public class checkIdEmailSuite {
}
