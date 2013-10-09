// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.gradle;

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class KasperHadoopDependenciesTask extends DefaultTask {

    @TaskAction
    void copyDependencies( ) {
        def runtimePigJars = project.configurations.pigRuntime.findAll()
        def runtimeHiveJars = project.configurations.hiveRuntime.findAll()
        def runtimeJars = runtimePigJars + runtimeHiveJars

        for (depJar in runtimeJars) {
            println "HADOOP RUNTIME copy : " + depJar.name
            project.copy {
                from depJar.absolutePath
                into project.projectDir.absolutePath + '/build/extlib/'
            }
        }

        project.copy { // PiggyBank is not deployed in Maven repositories
            from project.projectDir.absolutePath + "/lib/piggybank.jar"
            into project.projectDir.absolutePath + '/build/extlib/'
        }
    }

}

