package spark;

import org.deeplearning4j.models.embeddings.WeightLookupTable;
import org.deeplearning4j.models.embeddings.inmemory.InMemoryLookupTable;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.VocabWord;
import org.deeplearning4j.models.word2vec.Word2Vec;
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

public class Word2VecTrain {
    public static void main(String[] args) throws FileNotFoundException {
        String filePath = new File("./", "korean.txt").getAbsolutePath();
        String filePath2 = new File("./", "korean2.txt").getAbsolutePath();
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

        wvec.fit();

        System.out.print("type to word : ");
        Scanner sc = new Scanner(System.in);
        String word = sc.nextLine();
        Collection<String> hi = null;
        try {
            hi = wvec.wordsNearestSum(word, 20);
        } catch (NullPointerException e) {
            System.out.println("error");
            System.out.println("================");
        } finally {
            if (hi != null) {
                System.out.println("first result");
                System.out.println(hi);
                System.out.println("================================");
            }
        }
        WordVectorSerializer.writeWord2VecModel(wvec, "model.txt");
        Word2Vec wvec2 = WordVectorSerializer.readWord2VecModel("model.txt");
        SentenceIterator iter2 = new BasicLineIterator(filePath2);
        TokenizerFactory t2 = new DefaultTokenizerFactory();
        t2.setTokenPreProcessor(new CommonPreprocessor());

        wvec2.setTokenizerFactory(t2);
        wvec2.setSentenceIterator(iter2);
        wvec2.fit();

        Collection<String> hi2 = wvec2.wordsNearestSum(word, 20);
        System.out.println("second result");
        System.out.println(hi2);
        System.out.println("==============================");

    }
}
