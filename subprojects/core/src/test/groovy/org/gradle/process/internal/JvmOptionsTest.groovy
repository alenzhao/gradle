/*
 * Copyright 2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.process.internal

import org.gradle.api.internal.file.IdentityFileResolver
import spock.lang.Specification

/**
 * by Szczepan Faber, created at: 2/13/12
 */
class JvmOptionsTest extends Specification {
    
    def "reads options from String"() {
        expect:
        JvmOptions.fromString("") == []
        JvmOptions.fromString("-Xmx512m") == ["-Xmx512m"]
        JvmOptions.fromString("\t-Xmx512m\n") == ["-Xmx512m"]
        JvmOptions.fromString(" -Xmx512m   -Dfoo=bar\n-XDebug  ") == ["-Xmx512m", "-Dfoo=bar", "-XDebug"]
    }

    def "reads quoted options from String"() {
        expect:
        JvmOptions.fromString("-Dfoo=bar -Dfoo2=\"hey buddy\" -Dfoo3=baz") ==
                ["-Dfoo=bar", "-Dfoo2=hey buddy", "-Dfoo3=baz"]

        JvmOptions.fromString("  -Dfoo=\" bar \"  " ) == ["-Dfoo= bar "]
        JvmOptions.fromString("  -Dx=\"\"  -Dy=\"\n\" " ) == ["-Dx=", "-Dy=\n"]
        JvmOptions.fromString(" \"-Dx= a b c \" -Dy=\" x y z \" ") == ["-Dx= a b c ", "-Dy= x y z "]
    }
    
    def "understands quoted system properties"() {
        def opts = new JvmOptions(new IdentityFileResolver())

        when:
        opts.jvmArgs(JvmOptions.fromString("  -Dfoo=\" hey man! \"  " ))

        then:
        opts.getSystemProperties().get("foo") == " hey man! "
    }
}
