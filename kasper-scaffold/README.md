## Kasper scaffolding

This Kasper module is a scaffolding module which can be used to initialize a platform, create domains and other Kasper components.

*Only gradle is currently supported as launcher, as a dedicated task.*

### How it works ?

1. Install the plugin

    ```yaml
        // Apply the plugin to your project
        apply plugin: 'kasper-scaffold'

        // Add a dependency to kasper-scaffold in your buildscript
        buildscript {
            dependencies {
                classpath 'com.viadeo.kasper:kasper-scaffold:${KASPER_VERSION}'
            }
        }

        // Optionally configure some plugin properties
        kasperScaffoldConf {
            dependenciesHolder = "librairies"
        }
    ```
2. Launch scaffolding tasks
    ```
        $ gradle kasffoldAddDomain -Pdomain.name=test
    ```
    (you will be prompted for parameters if not specified on command line)

### Use the scaffolding tasks

#### List available tasks

    $ gradle kasffoldList

NOT IMPLEMENTED

TODO

#### Show details about a task

    $ gradle kasffoldShowAddDomain

NOT IMPLEMENTED 

TODO

#### Available tasks

* **kasffoldAddDomain** : add a new domain

### Kasper developers: Add a new template

Add your new template hierarchy in the root **templates/** directory :

* Create a new directory for you task, using pythonic name : 
    ```
        $ cd templates
        $ mkdir add_domain
        $ cd add_domain
    ```
* Create a config.json file in this new directory :
    ```javascript
        {
            "name": "Add a new domain",
            "params": {
                "domain": {
                    "name": {
                        "value_ask": "Name of the domain",
                    }
                }
            }
        }
    ```
* Create your directories hierarchy optionally using dynamic variables :
    ```
    $ mkdir -p '${project.domainPrefix}-${domain.name}/src/main/java/${project.basePackageDir}'
    ```
* Add your files using dynamic variables :
    ```
    $ echo 'this a file of the domain ${domain.name}' > '${project.domainPrefix}-${domain.name}/README'
    ```
* Use your new template in any dependent Kasper project :
    ```
    $ gradle kasffoldAddDomain
    ```

### Reference

**Available properties**

These properties can be defined in Kasper projects using the **kasperScaffoldConf** holder and are available inside
your templates under the **project.** key.

| name                  | description                                                                          |
| --------------------- | ------------------------------------------------------------------------------------ |
| domainPrefix          | The prefix used when naming your domains modules (default: 'kviadeo')                |
| basePackage           | The base Java package of your platform (default: 'com.viadeo.platform'               |
| basePackageDir        | OVERRIDEN: basePackage transl. into a directory way (default: 'com/viadeo/platform') |
| dependenciesHolder    | Gradle: the prefix to be used when defining dependencies (default: 'libraries')      |


