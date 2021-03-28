package com.example.app.blackBox;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({CreateStudentTest.class, BuildingUpdateCheckInOutTest.class,
        StudentDeleteTest.class})
public class BuildingUpdateCheckInOutSuite {
}
