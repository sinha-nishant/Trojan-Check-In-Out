package com.example.app;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

// Runs all unit tests.
@RunWith(Suite.class)
@Suite.SuiteClasses({CreateStudentTest.class,
        StudentDeleteTest.class})


public class DeleteSuite {
}
