package com.example.app.whiteBox;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)

@Suite.SuiteClasses({FbCreateStudentAccountTest.class, FbCheckUsedIdTest.class,
        FbDeleteStudentAccountTest.class})
public class checkStudentIdSuite {
}
