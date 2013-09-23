/*
 * Copyright 2000-2013 JetBrains s.r.o.
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

import com.google.common.collect.Sets;
import com.intellij.openapi.util.SystemInfo;
import jetbrains.buildServer.agent.rakerunner.utils.FileUtil2;
import jetbrains.buildServer.agent.rakerunner.utils.OSUtil;
import jetbrains.buildServer.agent.ruby.rvm.InstalledRVM;
import jetbrains.buildServer.agent.ruby.rvm.detector.impl.RVMDetectorForUNIX;
import jetbrains.buildServer.util.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.testng.annotations.DataProvider;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.util.*;

/**
 * @author Vladislav.Rassokhin
 */
public class RubyVersionsDataProvider {
  @DataProvider(name = "ruby-versions")
  public static Iterator<Object[]> getRubyVersionsDP() {
    return getCartesianProductIterator(getRubyVersionsSet());
  }

  @DataProvider(name = "ruby-versions-linux")
  public static Iterator<Object[]> getRubyVersionsLinuxDP() {
    return getCartesianProductIterator(getRubyVersionsLinuxSet());
  }

  @DataProvider(name = "ruby-versions-windows")
  public static Iterator<Object[]> getRubyVersionsWindowsDP() {
    return getCartesianProductIterator(getRubyVersionsWindowsSet());
  }

  @NotNull
  public static Set<String> getRubyVersionsSet() {
    if (SystemInfo.isWindows) {
      return getRubyVersionsWindowsSet();
    }
    if (SystemInfo.isUnix) {
      return getRubyVersionsLinuxSet();
    }
    throw new IllegalStateException("Unsupported OS type " + System.getProperty("os.name").toLowerCase());
  }

  @NotNull
  public static Set<String> getRubyVersionsLinuxSet() {
    final String property = System.getProperty("ruby.testing.versions", null);
    if (property != null) {
      final List<String> rubies = StringUtil.split(property, " ");
      return new HashSet<String>(rubies);
    }
    if (StringUtil.isTrue(System.getProperty("rake.runnner.tests.use.all.rvm.interpreters"))) {
      final InstalledRVM rvm = new RVMDetectorForUNIX().detect(System.getenv());
      if (rvm != null) {
        final SortedSet<String> rubies = rvm.getInstalledRubies();
        // Use latest patchversion
        final Map<String, String> m = new HashMap<String, String>();
        for (String ruby : rubies) {
          final String s = ruby.replaceAll("\\-p\\d+", "");
          if (VersionComparatorUtil.compare(m.get(s), ruby) < 0) {
            m.put(s, ruby);
          }
        }
        return new HashSet<String>(m.values());
      }
    }
    return new HashSet<String>() {
      {
        add("ruby-1.8.7");
        add("ruby-1.9.2");
        add("jruby");
      }
    };
  }

  @NotNull
  public static Set<String> getRubyVersionsWindowsSet() {
    final String storage = System.getProperty(RakeRunnerTestUtil.INTERPRETERS_STORAGE_PATH_PROPERTY);
    if (storage != null && FileUtil2.checkIfDirExists(storage)) {
      final File[] interpreters = new File(storage).listFiles(new FileFilter() {
        public boolean accept(@NotNull final File file) {
          return file.isDirectory() && isInterpreterDirectory(file);
        }
      });
      if (interpreters != null && interpreters.length > 0) {
        return CollectionsUtil.convertSet(Arrays.asList(interpreters), new Converter<String, File>() {
          public String createFrom(@NotNull final File source) {
            return source.getName();
          }
        });
      }
    }
    return new HashSet<String>() {
      {
        add("ruby-1.8.7");
        add("ruby-1.9.2");
        add("jruby-1.6.4");
      }
    };
  }

  @Contract("null -> false")
  private static boolean isInterpreterDirectory(@Nullable final File directory) {
    if (directory == null || !directory.exists() || !directory.isDirectory()) {
      return false;
    }
    final File bin = new File(directory, "bin");
    if (!bin.exists() || !bin.isDirectory()) {
      return false;
    }
    final HashSet<String> probablyNames = new HashSet<String>() {{
      if (SystemInfo.isWindows) {
        add(OSUtil.RUBY_EXE_WIN);
        add(OSUtil.RUBY_EXE_WIN_BAT);
        add(OSUtil.JRUBY_EXE_WIN);
        add(OSUtil.JRUBY_EXE_WIN_BAT);
      } else {
        add(OSUtil.JRUBY_EXE_UNIX);
        add(OSUtil.RUBY_EXE_UNIX);
      }
    }};
    final File[] files = FileUtil.listFiles(bin, new FilenameFilter() {
      public boolean accept(@NotNull final File dir, @NotNull final String name) {
        return probablyNames.contains(name);
      }
    });
    return files.length > 0;
  }

  @NotNull
  public static Iterator<Object[]> getCartesianProductIterator(@NotNull final Set<String>... sources) {
    final Set<List<String>> cartesian = Sets.cartesianProduct(sources);
    final List<Object[]> list = CollectionsUtil.convertCollection(cartesian, new Converter<Object[], List<String>>() {
      public Object[] createFrom(@NotNull final List<String> source) {
        return source.toArray(new Object[source.size()]);
      }
    });
    return list.iterator();
  }

}
