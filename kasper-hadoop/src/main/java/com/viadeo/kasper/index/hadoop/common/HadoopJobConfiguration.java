package com.viadeo.kasper.index.hadoop.common;

import org.apache.hadoop.fs.FileSystem;

public class HadoopJobConfiguration {

    public static final String DEFAULT_LIB_DIR = "/tmp";
    public static final String DEFAULT_AVRO_DIR = "/tmp";
    public static final String DEFAULT_SCHEMA_DIR = "/tmp";
    public static final String DEFAULT_HADOOP_CONFDIR = "/etc/hadoop/conf";
    public static final String DEFAULT_OUTPUT_FILENAME = "output.avro";

    public String jobName;
    public String outputFile;
    public FileSystem fileSystem;
    public HadoopDependencies hadoopDependencies;

    public String libDir = HadoopJobConfiguration.DEFAULT_LIB_DIR;
    public String avroDir = HadoopJobConfiguration.DEFAULT_AVRO_DIR;
    public String schemaDir = HadoopJobConfiguration.DEFAULT_SCHEMA_DIR;
    public String hadoopConfDir = HadoopJobConfiguration.DEFAULT_HADOOP_CONFDIR;

    public String hiveHost = "127.0.0.1";
    public Integer hivePort = 10000;
    public String hivePath = "default";
}
