/*
 * Copyright (C) 2020 The Dagger Authors.
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

package dagger.hilt.processor.internal.disableinstallincheck;

import static com.google.testing.compile.CompilationSubject.assertThat;

import com.google.testing.compile.Compilation;
import com.google.testing.compile.JavaFileObjects;
import dagger.testing.compile.CompilerTests;
import javax.tools.JavaFileObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/** Tests for errors generated by {@link DisableInstallInCheckProcessor} */
@RunWith(JUnit4.class)
public class DisableInstallInCheckProcessorErrorsTest {

  @Test
  public void testIllegalCombinationInstallIn() {
    JavaFileObject source =
        JavaFileObjects.forSourceLines(
            "foo.bar",
            "package foo.bar;",
            "",
            "import dagger.hilt.migration.DisableInstallInCheck;",
            "import dagger.hilt.EntryPoint;",
            "",
            "@DisableInstallInCheck",
            "final class NotModule {}",
            "",
            "@DisableInstallInCheck",
            "@EntryPoint",
            "interface FooEntryPoint {}");

    Compilation compilation =
        CompilerTests.compiler()
            .withProcessors(new DisableInstallInCheckProcessor())
            .compile(source);

    assertThat(compilation).failed();
    assertThat(compilation)
        .hadErrorContaining(
            "@DisableInstallInCheck should only be used on modules. However, it was found"
                + " annotating foo.bar.NotModule")
        .inFile(source)
        .onLine(7);
    assertThat(compilation)
        .hadErrorContaining(
            "@DisableInstallInCheck should only be used on modules. However, it was found"
                + " annotating foo.bar.FooEntryPoint")
        .inFile(source)
        .onLine(11);
  }
}
