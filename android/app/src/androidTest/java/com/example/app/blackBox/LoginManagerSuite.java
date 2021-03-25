package com.example.app.blackBox;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)

@Suite.SuiteClasses({CreateManagerTest.class, LoginManagerTest.class,ManagerDeleteTest.class})
public class LoginManagerSuite {
}
