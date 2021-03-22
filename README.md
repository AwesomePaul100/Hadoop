# Hadoop MapReduce FileCounting


## Local (Standalone) Mode


### Seting up Hadoop

1. Linux System/ Virtual Machine
2. Java Must be installed in the system.
3. ssh, sshd and rsync must be installed.

* [Install Hadoop](http://www.apache.org/dyn/closer.cgi/hadoop/common/)

4. Setting Path Names
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
 5. Configurations
 ```shell
$ gedit etc/hadoop/core-site.xml
 ```
 In core-site.xml
<configuration>
    <property>
    <name>fs.defaultFS</name>
    <value>hdfs://localhost:9000</value>
    </property>
</configuration>

In hdfs-site.xml
<configuration>
<property>
<name>dfs.replication</name>
<value>1</value>
</property>

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
### Setting up and runing a simple Hadoop job
9. Executing the program
```shell
$ bin/hadoop com.sun.tools.javac.Main FileCounting.java
$ jar cf filecounting.jar FileCounting*.class
# Copy input/file01, input/file02, and input/file02 of this project and place them inside the input folder of the Hadoop distrubution folder.
$ bin/hadoop jar filecounting.jar FileCounting /user/hadoop/filecount/input /user/hadoop/filecount/output
# And finally to see the output, run the below command:
$ bin/hadoop dfs -cat /user/hadoop/filecount/output/part*
```

This simple Hadoop job, gets three text files from the "input" folder, howerver you could put more or less.
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
Hello hello hello HELLO hello!123
```
And by submitting a Hadoop job and applying Reduce step, it generates an inverted index as below:
```shell
bye    file01: 1,  The total '#' of files this word appears in is: 1
goodbye    file02: 1,  The total # of files this word appears in is: 1
hadoop    file02: 2,  The total # of files this word appears in is: 1
hello    file02: 1, file03: 5, file01: 1,  The total # of files this word appears in is: 3
world    file01: 2, file02: 2,  The total # of files this word appears in is: 2
```
When re-runing this Hadoop code, please make sure you go into the hadoop folder and delete the jar file and anything else related to the program like the '.class' file.
```shell
# Remove the output before re-running the code.
$ bin/hadoop fs -rm /user/hadoop/filecount/output/*
$ bin/hadoop fs -rmdir /user/hadoop/filecount/output
```
## References
* [Hadoop MapReduce documentation](https://hadoop.apache.org/docs/stable/hadoop-mapreduce-client/hadoop-mapreduce-client-core/MapReduceTutorial.html)
* [Hadoop MapReduce setup](https://cse.buffalo.edu/~bina/cse487/spring2016/prj2/TutorialRS.pdf)
