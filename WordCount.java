package spark;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.sources.In;
import scala.Tuple2;

import java.util.Arrays;
import java.util.List;

public class WordCount {
    public static void main(String[] args) {

        SparkConf conf = new SparkConf().setAppName("wordCount").setMaster("local");
        JavaSparkContext sc = new JavaSparkContext(conf);
        JavaRDD<String> textFile = sc.textFile("./big.txt");
        JavaPairRDD<String, Integer> counts = textFile
                .flatMap(line -> Arrays.asList(line.split(" ")).iterator())
                .mapToPair(word -> "".equals(word) ? new Tuple2<>(StringReplace(word),0) : new Tuple2<>(StringReplace(word), 1))
                .reduceByKey((a, b) -> a + b)
                .filter(key -> key._2>0)
                .mapToPair(item -> item.swap())
                .sortByKey(false)
                .mapToPair(item -> item.swap());

        List output = counts.collect();

        for(Object outputWord : output){
            System.out.println(outputWord);
        }

        sc.close();
    }
    public static String StringReplace(String str){
        String match = "[^\uAC00-\uD7A3xfe0-9a-zA-Z\\s]";
        str =str.replaceAll(match, "");
        return str;
    }

}
