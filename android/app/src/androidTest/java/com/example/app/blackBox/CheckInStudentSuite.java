package com.example.app.blackBox;

import com.example.app.blackBox.CreateStudentTest;
import com.example.app.blackBox.QRScanTest;
import com.example.app.blackBox.StudentDeleteTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({CreateStudentTest.class, QRScanTest.class,
        StudentDeleteTest.class})
public class CheckInStudentSuite {
}
