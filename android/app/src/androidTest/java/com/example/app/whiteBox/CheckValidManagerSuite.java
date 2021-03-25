package com.example.app.whiteBox;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)

@Suite.SuiteClasses({FbCreateManagerAccountTest.class, FbCheckUsedManagerEmailTest.class,
        FbDeleteManagerAccountTest.class})

public class CheckValidManagerSuite {
}
