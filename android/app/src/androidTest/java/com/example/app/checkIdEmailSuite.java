package com.example.app;

import com.example.app.firebaseDB.checkUsedEmailTest;
import com.example.app.firebaseDB.checkUsedIdTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({CreateStudentTest.class, checkUsedEmailTest.class, checkUsedIdTest.class,
        StudentDeleteTest.class})

public class checkIdEmailSuite {
}
