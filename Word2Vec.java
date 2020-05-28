package spark;

import org.deeplearning4j.models.embeddings.WeightLookupTable;
import org.deeplearning4j.models.embeddings.inmemory.InMemoryLookupTable;
import org.deeplearning4j.models.word2vec.VocabWord;
import org.deeplearning4j.models.word2vec.wordstore.VocabCache;
import org.deeplearning4j.models.word2vec.wordstore.inmemory.AbstractCache;
import org.deeplearning4j.text.sentenceiterator.BasicLineIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.Scanner;

public class Word2Vec {
    public static void main(String[] args) throws FileNotFoundException {
        String filePath = new File("./", "korean.txt").getAbsolutePath();
        SentenceIterator iter = new BasicLineIterator(filePath);
        TokenizerFactory t = new DefaultTokenizerFactory();
        t.setTokenPreProcessor(new CommonPreprocessor());

        VocabCache<VocabWord> cache = new AbstractCache<>();
        WeightLookupTable<VocabWord> table = new InMemoryLookupTable.Builder<VocabWord>()
                .vectorLength(100)
                .useAdaGrad(false)
                .cache(cache).build();

        org.deeplearning4j.models.word2vec.Word2Vec wvec = new org.deeplearning4j.models.word2vec.Word2Vec.Builder()
                .minWordFrequency(5)
                .iterations(1)
                .layerSize(100)
                .seed(42)
                .windowSize(5)
                .iterate(iter)
                .tokenizerFactory(t)
                .lookupTable(table)
                .vocabCache(cache)
                .build();

        wvec.fit();;

        while(true){
            System.out.print("type to word : ");
            Scanner sc = new Scanner(System.in);
            String word = sc.nextLine();

            if(word.equals("exit")){
                break;
            }else{
                Collection<String> hi = null;
                try{
                    hi = wvec.wordsNearestSum(word, 10);
                }catch(NullPointerException e){
                    System.out.println("error");
                    System.out.println("================");
                }finally {
                    if(hi!=null){
                        System.out.println("result");
                        System.out.println(hi);
                        System.out.println("================================");
                    }
                }
            }
        }
    }
}
