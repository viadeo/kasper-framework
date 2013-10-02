
Indexation with Hadoop
======================

Developing the indexation part of the platform query area consists into transforming
domain events into denormalized indexes available for querying by services.

But you will need to create new indexes from scratch on existing data/events for several
reasons : full indexation time to time is safe, you'll need to rebuild indexes after a
crash, you'll need to initialize indexes before being able to incrementally maintain them
from events, etc..

If you hosts some of your data or your events on an Hadoop cluster, there are good chances
you'll need to develop map-reduce jobs, either directly using the Hadoop java API or by
the way of some M/R abstraction tools like Pig or Hive.

The **kasper-hadoop** module proposes a simple way to develop this kind of jobs, it comes
with the following features :

* A gradle plugin
* Some standard directories for your M/R implementations
* A Debian packaging
* The injection of dependencies to your scripts
* The automated copy of your file dependencies to your cluster before jobs launches
* A detection of runtime dependencies presence before jobs launches
* Specialized test fixtures
* Some AVRO files generation helpers
* Some launchers for your jobs allowing to decouple the jobs execution from their implementation

To come :

* Pig UDFs
* Cascading, basic M/R
* Import of the output AVRO files to your indexes (ES, HBase, ..)

Check the project `kasper-hadoop-sample <https://github.com/viadeo/kasper-hadoop-sample>`_ for a sample implementation.

.. contents::

The big picture
---------------

**kasper-hadoop** takes several assumptions about the way your indexation process should work.

An indexation job as seen by **kasper-hadoop** :

- depends on one or several AVRO input schemas
- depends on one or several AVRO input files
- generates **one** AVRO output file
- depends on **one** AVRO output schema
- can depends on one or several Java jars

The general process when developing an indexation job will then be to :

1. add your input AVRO schemas (*.avsc*) in **src/main/avro**
2. add your job output AVRO schema in **src/main/avro**
3. develop your Hadoop job and put it in the expected directory :
    * **src/main/pig** for Pig scripts, terminated by the **.pig** extension
    * **src/main/hive** for Hive scripts, terminated by the **.hive** extension
4. add a test using the provided test fixtures
5. build the Debian package using `gradle build`, deploy it and run/schedule your job

Initialize your project (Gradle)
--------------------------------

Edit your project's **build.gradle** description file :

.. code-block:: yaml

    apply plugin: 'kasper-hadoop'

    buildscript {
        dependencies {
            classpath 'com.viadeo.kasper:kasper-hadoop:${KASPER_VERSION}'
        }
    }

    dependencies {
        compile 'com.viadeo.kasper:kasper-hadoop:${KASPER_VERSION}'
    }

Add your AVRO schemas
---------------------

Put the AVRO schemas of your input files and the one of your output format in **src/main/avro**, with `.avsc`
extension.

**Hint**: To extract a schema from an existing AVRO file, you can use the following method :

- `hadoop fs -copyToLocal /cascading/input/members/Member/part-m-00000.avro /tmp/member.avro`
- `java -jar ~/avro-tools-1.7.5.jar getschema /tmp/member.avro> src/main/avro/member.avsc`
- check namespace is defined in the schema, in the form `"namespace" : "com.acme.avro"`

Develop the M/R job
-------------------

Using Pig or Hive
^^^^^^^^^^^^^^^^^

Add your script in the corresponding directory :

- **Pig** : `src/main/pig` with `.pig` extension
- **Hive** : `src/main/hive` with `.hive` extension

In order to allow the **MainLauncher** to discover the needed
AVRO files, you'll need to add them as dependencies in your script :

.. code-block::
    -- REQUIRE AVRO MemberInfo
    -- REQUIRE AVRO Position
    -- REQUIRE AVRO Member
    -- REQUIRE AVRO SkillMemberScore


For each AVRO dependency, the launcher will pass a **DATA_AVRO_** TABLENAME argument holding the full path location of
the targeted AVRO file and a **SCHEMA_AVRO_** TABLENAME argument holding the full path location of the AVRO file schema
that you can use in your Pig or Hive scripts.

If you only need the corresponding schema and does not plan to read the associated file (for output for instance),
add the schema dependency, only the **SCHEMA_AVRO_** TABLENAME argument will be set up :

``-- REQUIRE SCHEMA SeoRelevantMember``

In addition the launcher will provides you with the following standard script arguments :

* Only used by Pig scripts :
    * **LIB_PIGGYBANK** to be used as `REGISTER $LIB_PIGGYBANK`
    * **DATA_AVRO_OUTPUT** which holds the full path location of the output file, to be used as `STORE MYDATA INTO '$DATA_AVRO_OUTPUT'`
    * **LIB_ROOT** to be used as `REGISTER $LIB_ROOT/avro-*.jar`

* For Pig and Hive scripts :
    * **DATA_AVRO_OUTPUT_DIR** which holds the full path location of the output file directory

You can then launch the scripts using the **MainLauncher** (com.viadeo.kasper.index.hadoop.MainLaunche)
as Java application in your IDE, adding for instance `-jobName SeoHadoopPigTest` as program argument.

The launcher will check before starting job that all AVRO files are present on remote filesystem, failing if any
is missing, and will also ensure that all dependencies are present, uploading them if required.

See below about running your jobs for the list of recognized arguments of the **MainLauncher**.

Test your job
-------------

**Pig or Hive** :

.. code-block:: java

    import com.viadeo.kasper.index.hadoop.tests.AvroTestComparator;
    import com.viadeo.kasper.index.hadoop.tests.AvroWriter;
    import com.viadeo.kasper.index.hadoop.tests.pig.PigTestFixture;

    import com.acme.avro.InputObject; /* Generated AVRO class from schema */

    ...

    final File tmpDir = new File(System.getProperty("java.io.tmpdir"), Long.toString(System.nanoTime()));

    // Given
    final List<InputObject> testInput = new List<>() {{
        add(new InputObject("foo", "bar"));
    }};
    final File testInputFile = new AvroWriter(InputObject.class, InputObject.SCHEMA$).write(testInput, tmpDir);

    // When
    final File outputAvro = PigTestFixture
                .forScript(new File("src/main/pig/myscript.pig"))
                .withOutputDir(tmpDirFile)
                .withOutputFileName("output.avro")
                .withOutputSchemaName("myoutput")
                .withAvroDependencies(testInputFile)
                .runForOutput();

    // Then
    final List<Map<String, String>> expectedData = ...;
    AvroTestComparator
            .forFile(outputAvro)
            .withTestData(expectedData)
            .proceed();


For Hive tests, just replace **PigTestFixture** by **HiveTestFixture**


**Note:** Use of Cucumber (or other BDD tool) is highly recommended for indexation testing, at least for better
expressiveness of your input and expected output data.

Package and run your jobs on the cluster
----------------------------------------

`gradle assemble` (or `gradle build`) will generate a Debian package in your **build/** folder.

After installation, a **/usr/local/<project_name>** directory will be created.

It contains all needed dependencies (AVRO schemas, libraries, launchers) needed to execute your jobs.

A **launch** script is available at the root of this directory.

.. code-block:: sh

    /usr/local/seo-hadoop-tests $ ./launch --help
    usage: HadoopLauncher
    -hadoopConfDir <hadoopConfDir>         The path to the local Hadoop configuration (/etc/hadoop/conf)
    -help                                  Print this message
    -hiveHost                              The Hive database host (127.0.0.1)
    -hivePath                              The Hive database path (default)
    -hivePort                              The Hive database port (10000)
    -jobName <jobName>                     The name of the job to launch
    -list                                  List all available jobs
    -overwriteOutput                       If set, the output file will be overwritten (false)
    -remoteAvroDir <remoteAvroDir>         The path to the remote AVRO files directory (/tmp)
    -remoteLibDir <remoteLibDir>           The path to the remote dependencies directory (/tmp)
    -remoteOutputFile <remoteOutputFile>   The path to the remote output file to be generated ({remoteAvroDir}/output.avro)
    -remoteSchemaDir <remoteSchemaDir>     The path to the remote AVRO schema files directory (/tmp)


.. code-block:: sh

   /usr/local/seo-hadoop-tests $ ./launch -jobName SeoHadoopPigTest -overwriteOutput


.. code-block:: sh

    /usr/local/seo-hadoop-tests $ ./launch --list
    List of embedded jobs :
       => Pig
         -> SeoHadoopPigTest
       => Hive
         -> SeoHadoopHiveTest



Configure the gradle plugin
---------------------------

Several configuration keys can be changed, below is an override sample of all available recognized properties
by their default values :

.. code-block:: yaml

    kasperHadoopConf {
        pigSourceDir = 'src/main/pig'
        hiveSourceDir = 'src/main/hive'
        avroSchemasDir = 'src/main/avro'
        avroSourceDir = 'generated-sources/avro'
        debianRootDir = 'usr/local/'
        debianDescription = 'Indexation jobs for my awesome project'
    }

Known bugs and limitations
--------------------------

- TODO: Seems to have too many dependencies in the resulting debian package, generating a quite heavy archive
- Compiled against a specific version of Hadoop ecosystem (**2.0.0-mr1-cdh4.3.0**)
- Required to compile hive 0.13.0-SNAPSHOT (as 0.13.0-VIADEO-SNAPSHOT) and hive-test 4.0.0 (as 4.0.1-VIADEO-SNAPSHOT) in order to correctly manage AVRO files and schemas
    - https://github.com/viadeo/hive_test
    - https://github.com/viadeo/hive


