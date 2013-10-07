// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.scaffolding;

import org.gradle.api.Project;
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class KasperScaffoldTask {

    def project

    KasperScaffoldTask(Project project) {
        this.project = project
    }

    void scaffold(String tpl) {

        // TODO: copy md5, check md5, unzip if necessary

        if (tpl == 'help') {
            println "Kasper Scaffolding plugin\n"
            // TODO: list tasks
        }

    }

}
