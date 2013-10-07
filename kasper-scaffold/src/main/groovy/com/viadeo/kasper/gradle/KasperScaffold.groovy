// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.gradle;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import com.viadeo.kasper.scaffolding.KasperScaffoldTask

class KasperScaffoldExtension {
    def String domainPrefix = "kviadeo"
    def String basePackage = "com.viadeo.platform"
    def String basePackageDir = "" // overriden
}

class KasperScaffold implements Plugin<Project> {
    void apply(Project project) {
        project.extensions.create("kasperScaffoldConf", KasperScaffoldExtension)

        project.kasperScaffoldConf.basePackageDir = project.kasperScaffoldConf.basePackage.replaceAll('\\.', '/')

        project.tasks.addRule("Pattern: kasffold<TPL>") { String taskName ->
            if (taskName.startsWith("kasffold")) {
                project.task(taskName) << {
                    def String tpl = taskName - 'kasffold'
                    tpl = tpl.replaceAll(/\B[A-Z]/) { '_' + it }.toLowerCase()

                    def KasperScaffoldTask task = new KasperScaffoldTask(project)
                    task.scaffold(tpl)
                }
            }
        }        
    }
}

