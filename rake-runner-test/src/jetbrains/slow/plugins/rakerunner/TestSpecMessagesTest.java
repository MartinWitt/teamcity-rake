/*
 * Copyright 2000-2009 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jetbrains.slow.plugins.rakerunner;

import java.io.IOException;
import java.util.Map;
import jetbrains.buildServer.RunBuildException;
import jetbrains.buildServer.agent.rakerunner.SupportedTestFramework;
import static jetbrains.slow.plugins.rakerunner.MockingOptions.*;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;

/**
 * @author Roman Chernyatchik
 */
@Test(groups = {"all","slow"})
public class TestSpecMessagesTest extends AbstractRakeRunnerTest {
  public TestSpecMessagesTest(String s) {
    super(s);
  }

  @BeforeMethod
  @Override
  protected void setUp() throws Exception {
    super.setUp();
    myShouldTranslateMessages = false;
  }

  protected void appendRunnerSpecificRunParameters(Map<String, String> runParameters) throws IOException, RunBuildException {
    setWorkingDir(runParameters, "app_testspec");
    // enable test-spec
    SupportedTestFramework.TEST_SPEC.activate(runParameters);
  }

  public void testLocation()  throws Throwable {
    //TODO implement test location for test-spec!
    setPartialMessagesChecker();

    setMockingOptions(FAKE_TIME, FAKE_STACK_TRACE);
    initAndDoTest("stat:general", "_location", false, "app_testspec");
  }
}