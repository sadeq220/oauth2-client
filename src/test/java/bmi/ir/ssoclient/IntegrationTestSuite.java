package bmi.ir.ssoclient;

import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

@Suite
@SuiteDisplayName("integration test suite")
@SelectPackages("bmi.ir.ssoclient.integrationtest")
public class IntegrationTestSuite {
}
