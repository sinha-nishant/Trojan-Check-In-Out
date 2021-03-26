package com.example.app.blackBox;

import com.example.app.whiteBox.QRScanCheckInTest;
import com.example.app.whiteBox.QRScanCheckOutTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({CreateStudentTest.class, QRScanCheckInTest.class, QRScanCheckOutTest.class,
        StudentDeleteTest.class})
public class CheckOutStudentSuite {
}
