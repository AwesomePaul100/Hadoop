/*
 * FileCounting.java
 * Paul Amoruso
 * EEL 4798
 * HW 2
 */
//  Imports.
import java.io.IOException;
import java.util.StringTokenizer;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
 

//  This is the filecounter class.
public class FileCounting {
         // This the Mapper class to maps input data from the files with the key.
    public static class TokenizerMapper extends Mapper<LongWritable, Text, Text, Text>
         {
             //  hadoop uses 'one' for the number of occurances of a word, and is set to value 1 in the Map process.
             private Text number = new Text("1");
             //  Get the name of each file.
             private String fileName;

            protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException
            {
                String line = value.toString();
                if (line.trim().length() >0)
                {
                    // Split the input sentence.
                    StringTokenizer stringTokenizer = new StringTokenizer(line);
                    while (stringTokenizer.hasMoreTokens())
                    {
                        // Make all the words lowercase, of case-insensitive, and take out non letter characters.
                        String keyForCombiner = stringTokenizer.nextToken().replaceAll("[^a-zA-Z]", "").toLowerCase()+": "+fileName;
                        // Make sure it is a word first.
                        String check = "";
                        if (!(stringTokenizer.nextToken().replaceAll("[^a-zA-Z]", "").toLowerCase().equals(check)))
                        	context.write(new Text(keyForCombiner),number);
                    }
                }
            }
            // Using a setup method that runs once per task, in this case, to get the file name.
            protected void setup(Context context) throws IOException, InterruptedException
            {
                FileSplit inputSplit = (FileSplit)context.getInputSplit();
                fileName = inputSplit.getPath().getName();
            }
         }

    //  This class is a semi-reducer, it accepts the inputs from the Map class and then
    //  passes the output key-value pairs to the Reducer class.
    public static class FileCountingCombiner extends Reducer<Text, Text, Text,Text>
    {
        protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException
        {
            // Count the number of times the word appears in a particular file.
            int sum = 0;
            for (Text item : values){
                sum += Integer.valueOf(item.toString());
            }
            String[] keyArray = key.toString().split(": ");
            context.write(new Text(keyArray[0]),new Text(keyArray[1]+": "+sum));
        }
    }

    //  This reducer method collects the output from the Combiner to organize and sum up the file count.
    public static class IntSumReducer extends Reducer<Text, Text, Text,Text>
    {
        protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException
        {
            StringBuffer theResult = new StringBuffer();
            // Using this to count the number of files the word appears in.
            int counter = 0;
            for (Text item: values)
            {
            	counter ++;
            	// Put commas between all the word counts to organize it a little.
                theResult.append(item+", ");
            }
            String str1 = Integer.toString(counter);
            theResult.append(" The total # of files this word appears in is: " + str1);
            context.write(key,new Text(theResult.toString().substring(0,theResult.toString().length())));
        }
    }

    // The main method.
    public static void main(String[] args) throws Exception
    {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf,"FileCounting");
        job.setJarByClass(FileCounting.class);
        job.setCombinerClass(FileCountingCombiner.class);
        job.setMapperClass(TokenizerMapper.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(Text.class);
        job.setReducerClass(IntSumReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);
        // Get input/output paths.
        FileInputFormat.addInputPath(job,new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}



