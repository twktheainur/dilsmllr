package org.getalp.lexsema.wsd.experiments;

import org.getalp.lexsema.io.annotresult.SemevalWriter;
import org.getalp.lexsema.io.document.Semeval2007TextLoader;
import org.getalp.lexsema.io.document.TextLoader;
import org.getalp.lexsema.io.resource.LRLoader;
import org.getalp.lexsema.io.resource.dictionary.DictionaryLRLoader;
import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.wsd.configuration.Configuration;
import org.getalp.lexsema.wsd.method.Disambiguator;
import org.getalp.lexsema.wsd.method.SimulatedAnnealing2;
import org.getalp.lexsema.wsd.score.ConfigurationScorer;
import org.getalp.lexsema.wsd.score.SemEval2007Task7PerfectConfigurationScorer;

import java.io.File;

public class SimulatedAnnealingDisambiguation2
{
    public static void main(String[] args)
    {    
        int iterationsNumber = 1000;
        double acceptanceProbability = 0.8;
        double coolingRate = 0.8;
        int convergenceThreshold = 5;
        
        if (args.length >= 1) iterationsNumber = Integer.valueOf(args[0]);
        if (args.length >= 2) acceptanceProbability = Double.valueOf(args[1]);
        if (args.length >= 3) coolingRate = Double.valueOf(args[2]);
        if (args.length >= 4) convergenceThreshold = Integer.valueOf(args[3]);
        
        System.out.println("Parameters value : " + 
                         "<iterations = " + iterationsNumber + "> " +
                         "<acceptance probability = " + acceptanceProbability + "> " +
                         "<cooling rate = " + coolingRate + "> " +
                         "<convergence threshold = " + convergenceThreshold + "> " 
                         );
        
        long startTime = System.currentTimeMillis();

        TextLoader dl = new Semeval2007TextLoader("../data/senseval2007_task7/test/eng-coarse-all-words.xml")
                .loadNonInstances(false);

        LRLoader lrloader = new DictionaryLRLoader(new File("../data/dictionnaires-lesk/dict-adapted-all-relations.xml"));

        ConfigurationScorer scorer = new SemEval2007Task7PerfectConfigurationScorer();

        Disambiguator saDisambiguator = new SimulatedAnnealing2(acceptanceProbability, coolingRate, convergenceThreshold, iterationsNumber, scorer);

        System.out.println("Loading texts...");
        dl.load();

        for (Document d : dl)
        {    
            System.out.println("Starting document " + d.getId());
            
            System.out.println("Loading senses...");
            lrloader.loadSenses(d);

            System.out.println("Disambiguating...");
            Configuration c = saDisambiguator.disambiguate(d);
            
            System.out.println("Writing results...");
            SemevalWriter sw = new SemevalWriter(d.getId() + ".ans");
            sw.write(d, c.getAssignments());
            
            System.out.println("Done!");
        }
        
        saDisambiguator.release();
        
        long endTime = System.currentTimeMillis();
        System.out.println("Total time elapsed in execution of Simulated Annealing is : ");
        System.out.println((endTime - startTime) + " ms.");
        System.out.println(((endTime - startTime) / 1000l) + " s.");
        System.out.println(((endTime - startTime) / 60000l) + " m.");
    }
}
