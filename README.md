# Hadoop MapReduce FileCounting
This file counting program prints the words (case-insensitive with no special characters or numbers) with the name of the file it was in
and the number of times it occurs in that file, and then the total number of files that word appears in.

## Local (Standalone) Mode


### Setting up Hadoop

1. Linux System/ Virtual Machine
2. Java Must be installed in the system.
3. ssh, sshd and rsync must be installed.

* [Install Hadoop](http://www.apache.org/dyn/closer.cgi/hadoop/common/)

4. Setting Path Names by performing the following commands:
```shell
echo $JAVA_HOME
echo $HADOOP_CLASSPATH
# get default Java path
readlink -f /usr/bin/java | sed "s:bin/java::"
# Output for me was:
# /usr/lib/jvm/java-11-openjdk-amd64/
# Then add these lines to the hadoop-env.sh or set them in terminal
export JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64/
export PATH=${JAVA_HOME}/bin:${PATH}
export HADOOP_CLASSPATH=$JAVA_HOME/lib/tools.jar

 ```
Then try the following command:
 ```shell
$ bin/hadoop
 ```
 5. Configurations, open the following files to configure them:
 ```shell
$ gedit etc/hadoop/core-site.xml
 ```
 In core-site.xml
```shell
<configuration>
    <property>
    <name>fs.defaultFS</name>
    <value>hdfs://localhost:9000</value>
    </property>
</configuration>
```
In hdfs-site.xml

```shell
<configuration>
<property>
<name>dfs.replication</name>
<value>1</value>
</property>
</configuration>
```

6. Check ssh to localhost
```shell
$ ssh localhost
```
7. Format the filesystem
```shell
$ bin/hdfs namenode –format
```
8. Run the daemons
(This part was tricky for me)
```shell
$ sbin/start-dfs.sh
# The daemons can be stopped by typing the following command
$ sbin/stop-dfs.sh
```
### Setting up and running a simple Hadoop job
9. Executing the program by performing the following commands:
```shell
$ bin/hadoop com.sun.tools.javac.Main FileCounting.java
$ jar cf filecounting.jar FileCounting*.class
# Copy input/file01, input/file02, and input/file02 of this project and place them inside the input folder of the Hadoop distrubution folder.
$ bin/hadoop jar filecounting.jar FileCounting /user/hadoop/filecount/input /user/hadoop/filecount/output
# And finally to see the output, run the below command:
$ bin/hadoop dfs -cat /user/hadoop/filecount/output/part*
```

This simple Hadoop job, gets three text files from the "input" folder, howerver you could put more or less.

But first you need to make a input folder:
```shell
bin/hdfs dfs –mkdir /user/hadoop/filecount/input
```
Then you need to copy the files from its directory like a folder on desktop, to the hadoop input directory:
```shell
bin/hadoop fs -copyFromLocal ../filecounter/input/file0* /user/hadoop/filecount/input
```
The following commands prints the text output to terminal:
```shell
#file01
$ bin/hadoop dfs -cat /user/hadoop/filecount/input/file01
Hello World BYE World
```
```shell
#file02
$ bin/hadoop dfs -cat /user/hadoop/filecount/input/file02
Hello World Hadoop Goodbye World HADOOP
```
```shell
#file03
$ bin/hadoop dfs -cat /user/hadoop/filecount/input/file03
Hello hello hello HELLO hello! 12345
```
And by submitting a Hadoop job, the java code has a mapper class that maps input from the files with the words and the files they appear in (from the setup method that uses FileSplit), then there is a semi-reducer class that counts the number of times the word appears in the particular file.  Then, applying the Reduce step to attach the string to and the value of the counter that displays in output the total # of files the word appears in. 
It generates the output an below:

(There is a # in the output, so that is not a comment like it looks like in GitHub.)
```shell
bye    file01: 1,  The total # of files this word appears in is: 1
goodbye    file02: 1,  The total # of files this word appears in is: 1
hadoop    file02: 2,  The total # of files this word appears in is: 1
hello    file02: 1, file03: 5, file01: 1,  The total # of files this word appears in is: 3
world    file01: 2, file02: 2,  The total # of files this word appears in is: 2
```
When reruning this Hadoop code, please make sure you go into the hadoop folder and delete the jar file and anything else related to the program like the '.class' file.
```shell
# Remove the output before re-running the code.
$ bin/hadoop fs -rm /user/hadoop/filecount/output/*
$ bin/hadoop fs -rmdir /user/hadoop/filecount/output
```
## References
* [Hadoop MapReduce documentation](https://hadoop.apache.org/docs/stable/hadoop-mapreduce-client/hadoop-mapreduce-client-core/MapReduceTutorial.html)
* [Hadoop MapReduce setup](https://cse.buffalo.edu/~bina/cse487/spring2016/prj2/TutorialRS.pdf)
