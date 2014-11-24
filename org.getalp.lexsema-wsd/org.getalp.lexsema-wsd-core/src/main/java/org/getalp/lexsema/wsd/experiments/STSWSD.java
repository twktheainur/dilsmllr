package org.getalp.lexsema.wsd.experiments;

import com.wcohen.ss.ScaledLevenstein;
import org.getalp.lexsema.io.Sentence;
import org.getalp.lexsema.io.resource.WordnetLoader;
import org.getalp.lexsema.io.segmentation.SpaceSegmenter;
import org.getalp.lexsema.io.sentences.STS2013SentencePairLoader;
import org.getalp.lexsema.similarity.SimilarityMeasure;
import org.getalp.lexsema.similarity.TverskiIndexSimilarityMeasureBuilder;
import org.getalp.lexsema.util.ValueScale;
import org.getalp.lexsema.wsd.configuration.Configuration;
import org.getalp.lexsema.wsd.method.Disambiguator;
import org.getalp.lexsema.wsd.method.sequencial.SimplifiedLesk;
import org.getalp.lexsema.wsd.method.sequencial.parameters.SimplifiedLeskParameters;
import org.getalp.lexsema.wsd.score.ConfigurationPairScoreInput;
import org.getalp.optimization.functions.Function;
import org.getalp.optimization.functions.setfunctions.submodular.Sum;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;

@SuppressWarnings("all")
public class STSWSD {
    public STSWSD() {
    }

    public static void main(String[] args) throws FileNotFoundException {

        //VisualVMTools.delayUntilReturn();

        WordnetLoader lrloader = new WordnetLoader("../data/wordnet/2.1/dict", true, true);
        STS2013SentencePairLoader spl = new STS2013SentencePairLoader("STS.input.headlines.txt", lrloader);
        PrintWriter outputFile = new PrintWriter("STS.headlines.output.txt");
        SimilarityMeasure similarityMeasure;

        similarityMeasure = new TverskiIndexSimilarityMeasureBuilder()
                .distance(new ScaledLevenstein())
                .computeRatio(true)
                .alpha(1d)
                .beta(0.5d)
                .gamma(0.5d)
                .fuzzyMatching(true)
                .quadraticWeighting(false)
                .extendedLesk(false)
                .randomInit(false)
                .regularizeOverlapInput(false)
                .optimizeOverlapInput(false)
                .regularizeRelations(false)
                .optimizeRelations(false)
                .isDistance(false)
                .build();

        /*WindowedLeskParameters algorithmParameters = new WindowedLeskParameters()
                .setFallbackFS(false)
                .setMinimize(true);
        Disambiguator disambiguator = new WindowedLesk(3, similarityMeasure, algorithmParameters, 2);*/

        SimplifiedLeskParameters algorithmParameters = new SimplifiedLeskParameters()
                .setMinimize(false);

        Disambiguator disambiguator = new SimplifiedLesk(1000, similarityMeasure, algorithmParameters, 2);


        //Disambiguator sl = new LegacySimplifiedLesk(10,sim_lr_hp,);
        //WindowedLeskParameters wlp = new WindowedLeskParameters(false,false);
        //Disambiguator sl = new WindowedLesk(6, sim_lr_hp, wlp, 4);
        System.err.println("Loading texts");
        spl.load();


        for (List<Sentence> sp : spl.getSentencePairs()) {
            Sentence sentence1 = sp.get(0);
            Sentence sentence2 = sp.get(1);
            System.err.println("Processing sentence pair: ");
            System.err.println("\t" + sentence1);
            System.err.println("\t" + sentence2);
            System.err.println("\tLoading senses...");

            System.err.println("\tDisambiguating sentence 1... ");
            Configuration c1 = disambiguator.disambiguate(sentence1);
            System.err.println("\tDisambiguating sentence 2... ");
            Configuration c2 = disambiguator.disambiguate(sentence2);

            ConfigurationPairScoreInput pairScore = new ConfigurationPairScoreInput(c1, sentence1,
                    c2, sentence2,
                    similarityMeasure);
            Function f = new Sum(1);
            double wsdScore = f.F(pairScore);
            wsdScore /= Math.max(sentence1.getLexicalEntries().size(), sentence2.getLexicalEntries().size());

            SpaceSegmenter seg = new SpaceSegmenter();
            double stringScore = similarityMeasure.compute(seg.segment(sentence1.toString()),
                    seg.segment(sentence2.toString()));
            stringScore = ValueScale.scaleValue(0, 1, 0, 5, stringScore);
            if (Double.isNaN(stringScore)) {
                stringScore = 0;
            }
            int unassigned1 = c1.countUnassigned();
            int unassigned2 = c2.countUnassigned();
            double unassignedRatio = (double) (Math.min(unassigned1, unassigned2) + 0.0001d) / ((double) Math.max(unassigned1, unassigned2) + 0.0001d);
            outputFile.println(2 * (wsdScore * stringScore) / (wsdScore + stringScore) + "\t" + ((1 - unassignedRatio) * 200));
            outputFile.flush();
            System.err.println("done!");
        }
        outputFile.close();
        disambiguator.release();
    }
}