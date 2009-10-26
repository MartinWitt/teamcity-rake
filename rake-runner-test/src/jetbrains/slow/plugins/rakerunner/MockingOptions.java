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

import jetbrains.buildServer.agent.AgentBuildParameters;

/**
 * Created by IntelliJ IDEA.
 * User: romeo
 * Date: Jul 3, 2009
 * Time: 8:07:52 PM
 * To change this template use File | Settings | File Templates.
 */
public enum MockingOptions {
  FAKE_TIME(":fake_time"),
  FAKE_STACK_TRACE(":fake_stacktrace"),
  FAKE_ERROR_MSG(":fake_error_msg"),
  FAKE_LOCATION_URL(":fake_location_url");

  ////////////
  private static final String TEAMCITY_RAKERUNNER_DEBUG_OPTIONS_ENV = "TEAMCITY_RAKERUNNER_DEBUG_OPTIONS";

  private String myOptionName;

  MockingOptions(final String optionName) {
    myOptionName = optionName;
  }

  public String getOptionName() {
    return myOptionName;
  }

  public static void addToBuildParams(final MockingOptions[] options,
                                      final AgentBuildParameters buildParameters) {
    final StringBuilder buff = new StringBuilder();
    for (MockingOptions option : options) {
      buff.append(option.getOptionName()).append(' ');
    }
    buildParameters.addBuildEnvironmnentVariable(TEAMCITY_RAKERUNNER_DEBUG_OPTIONS_ENV,
                                                 buff.toString());
  }

}