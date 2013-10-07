// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.scaffolding

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import groovy.util.GroovyTestCase
import com.viadeo.kasper.gradle.KasperScaffoldExtension

class KasperScaffoldTaskTest extends GroovyTestCase {

    void test_AddDomain() {

        // Given
        def project = getProject()
        project.ext["domain.name"] = "test"
        def task = new KasperScaffoldTask(project)

        try {

            // When
            task.scaffold("add_domain")

            // Then
            assertTrue(new File(project.rootDir, "kviadeo-test").exists())

        } finally {

            // Finish
            project.rootDir.deleteDir()
        }
    }

    // ------------------------------------------------------------------------

    Project getProject() {
        def tmpDir = File.createTempFile('kasper-scaffold', '.tmp')
        tmpDir.delete()
        tmpDir.mkdir();
        def project = ProjectBuilder.builder().withProjectDir(tmpDir).build()
        project.kasperScaffoldConf = new KasperScaffoldExtension()
        return project
    }

}
