// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.gradle;

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import groovy.io.FileType
import groovy.util.AntBuilder

class KasperHadoopPackageTask extends DefaultTask {

    @TaskAction
    void assemble( ) {

        def stdDeps = [ 
            LIB_ROOT: "/build/extlib", 
            LIB_PIGGYBANK: "/build/extlib/piggybank.jar" 
        ]

        /* Create the build directory */
        def mVersion = new Date().time
        def tmpDir = new File(project.buildDir, "${mVersion}") ; tmpDir.delete() ; tmpDir.mkdir()

        println " --> BUILD in " + tmpDir

        /* Copy MainLauncher dependencies */
        /* FIXME: optimize dependencies filter */
        def compileJars = project.configurations.compile.findAll()

        def libDir = new File(tmpDir, "lib") ; libDir.mkdir()
        compileJars.each { depJar ->
            println " --> COPY launcher dependency : ${depJar.name}"
            project.copy {
                from depJar.absolutePath
                into libDir.absolutePath
            }
        }

        /* Iterate over each Pig script */
        def scriptDir = new File(tmpDir, "script") ; scriptDir.mkdir()
        new File(project.projectDir, project.kasperHadoopConf.pigSourceDir).eachFileRecurse (FileType.FILES) { pigFile ->
            println "==> PIG SCRIPT : ${pigFile.name}"

            /* Retrieve required libs from pig script (PARSE) */
            def libs = []
            pigFile.eachLine { line -> 
                if (line.startsWith('REGISTER')) {
                    def regexp = line =~ /REGISTER (.*)/
                    def lib = regexp[0][1]
                    stdDeps.each { stdDep ->
                        lib = lib.replace('$' + stdDep.key, stdDep.value)
                    }

                    def libFile = new File(project.projectDir, lib)
                    project.copy {
                        from libFile.parent
                        into libDir.absolutePath
                        include libFile.name
                    }

                    println " --> REGISTERED script dependency ${lib}"
                }
            }

            /* Copy the script */
            project.copy {
                from pigFile.parent
                into scriptDir.absolutePath
                include pigFile.name
            }

        }

        /* Iterate over Hive scripts */
        new File(project.projectDir, project.kasperHadoopConf.hiveSourceDir).eachFileRecurse (FileType.FILES) { hiveFile ->
            println "==> HIVE SCRIPT : ${hiveFile.name}"

            project.copy {
                from hiveFile.parent
                into scriptDir.absolutePath
                include hiveFile.name
            }

        }

        /* Copy AVRO schemas */
        def avroDir = new File(tmpDir, project.kasperHadoopConf.avroSchemasDir) ; avroDir.mkdirs()
        project.copy {
            from project.kasperHadoopConf.avroSchemasDir
            into avroDir.absolutePath
            include "*.avsc"
        }

        /* Creates the launch script */
        def launchFile = new File(tmpDir, "launch")
        launchFile.withWriter  { out ->
            out.println "#!/bin/bash"
            out.println "\$JAVA_HOME/bin/java -cp \$(find lib | xargs | sed -e 's/ /:/g'):script:. -Dlog4j.configuration=log4j-debian.xml com.viadeo.kasper.index.hadoop.MainLauncher \$@"
        } 

        /* Build the deb file */
        def ant = new AntBuilder()
        ant.taskdef(name: 'deb', classname:'com.googlecode.ant_deb_task.Deb', classpath: project.buildscript.configurations.classpath.asPath)
        ant.deb( todir: project.buildDir, package: project.name, section: 'devel', version: project.version + "-" + mVersion ) {
            description( synopsis: project.kasperHadoopConf.debianDescription, project.name )
            tarfileset( dir: tmpDir.absolutePath, prefix: project.kasperHadoopConf.debianRootDir ) {
                exclude(name: launchFile.name)
            }
            tarfileset( file: launchFile.absolutePath, prefix: project.kasperHadoopConf.debianRootDir, filemode: '755' )
        }

        /* Delete build directory */
        tmpDir.deleteDir()

    }

}

