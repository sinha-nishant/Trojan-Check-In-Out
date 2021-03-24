package com.example.app.whiteBox;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import static org.junit.Assert.assertEquals;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        FbCreateAccountTest.class,
        FbSearchTest.class,
        FbDeleteAccountTest.class
})
public class FbCreateSearchDeleteTestSuite {


}