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

import groovy.text.SimpleTemplateEngine
import groovy.json.JsonSlurper

class KasperScaffoldTask {

    def project
    def kasperScaffoldDir = new File(System.getProperty("user.home"), ".kasper-scaffold")
    def kasperScaffoldTplDir = new File(kasperScaffoldDir, "templates")

    // ========================================================================

    KasperScaffoldTask(Project project) {
        this.project = project
        kasperScaffoldTplDir.mkdirs()
    }

    // ========================================================================

    def scaffold(String tpl) {

        updateTemplates()

        // kasffold help ------------------------------------------------------
        if (tpl == 'help') {
            println "Kasper Scaffolding plugin\n"
            // TODO: list tasks
            return
        }

        // search for template ------------------------------------------------
        def tplDir = new File(kasperScaffoldTplDir, tpl)
        if (!tplDir.exists() || !tplDir.isDirectory()) {
            println "!! no template found for " + tpl
            return
        }

        // read configuration -------------------------------------------------
        def tplName = tpl
        def params = [ ]

        def configFile = new File(tplDir, "config.json")
        if (configFile.exists()) {
            def slurper = new JsonSlurper()
            def config = slurper.parseText(configFile.text)
            if (config.containsKey('name')) {
                tplName = config.name
            }
            if (config.containsKey('params')) {
                ask_params(config.params)
                params = config.params
                println params
            }
        }

        params.project = project.kasperScaffoldConf

        // apply scaffolding --------------------------------------------------
        println "** Applying scaffolding for : " + tplName
        apply(project.rootDir.absolutePath + "/", tplDir, project.rootDir, params)
    }

    // ========================================================================

    def ask_params(params) {
        _ask_params(null, null, params)
    }

    def _ask_params(parent, name, value) {
        if ((null != parent) && value.containsKey("value_ask")) {

            BufferedReader br = new BufferedReader(new InputStreamReader(System.in))
            println value.value_ask + ": "
            def newValue = br.readLine()
            parent[name] = newValue

        } else {
            value.each { k, v -> 
                _ask_params(value, k, v)
            }
        }
    }

    // ========================================================================

    def apply(rootDirName, tplDir, outDir, params) {
        def engine = new SimpleTemplateEngine()

        tplDir.eachFile { it ->
            if (it.name == "config.json") {
                return false
            }

            def tplName
            try {
                tplName = engine.createTemplate(it.name).make(params).toString()
            } catch (e) {
                def tplFullPath = it.absolutePath - rootDirName
                throw new RuntimeException(":: tpl " + tplFullPath + "\n", e)
            }
            def newOutFile = new File(outDir, tplName)

            if (it.isDirectory()) {
                println ":: mkdir " + (newOutFile.absolutePath - rootDirName)

                newOutFile.mkdirs()
                apply(rootDirName, it, newOutFile, params)

            } else if (it.isFile()) {
                println ":: mkfile " + (newOutFile.absolutePath - rootDirName)

                def tplContents = engine.createTemplate(it.text).toString()
                newOutFile.withOutputStream { fos ->
                    fos << tplContents
                }

            }
        }
    }

    // ========================================================================

    void updateTemplates() {
        println "** Check templates cache"
        def md5Stream = KasperScaffoldTask.class.getResourceAsStream("/kasper-scaffold-templates.zip.MD5")
        def md5Package = md5Stream.getText()
        def md5File = new File(kasperScaffoldDir, "templates.md5")
        
        def unpack = true
        if (md5File.exists()) {
            def md5Current = md5File.text
            if (md5Current == md5Package) {
                unpack = false
            }
        }

        if (unpack) {
            println "  -> updating.."

            def tplFile = new File(kasperScaffoldDir, "templates.zip")
            this.getClass().getResource( '/kasper-scaffold-templates.zip' ).withInputStream { ris ->
              tplFile.withOutputStream { fos ->
                fos << ris
              }
            }

            kasperScaffoldTplDir.deleteDir()
            kasperScaffoldTplDir.mkdirs()

            project.ant.unzip(
                src: tplFile.absolutePath,
                dest: kasperScaffoldTplDir.absolutePath,
                overwrite: "true" 
            )

        } else {
            println "  -> up-to-date"
        }

        md5File.withOutputStream { fos ->
            fos << md5Package
        }
    }

}
