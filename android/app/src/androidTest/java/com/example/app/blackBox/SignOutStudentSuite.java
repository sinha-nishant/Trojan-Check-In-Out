package com.example.app.blackBox;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({CreateStudentTest.class, SignOutStudent.class,
        StudentDeleteTest.class})
public class SignOutStudentSuite {

}
