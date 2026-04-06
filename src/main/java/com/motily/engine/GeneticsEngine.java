package com.motily.engine;

import com.motily.dna.DnaEncoderDecoder;
import com.motily.dna.DnaService;
import com.motily.human.Human;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.Random;

@ApplicationScoped
public class GeneticsEngine {

    @Inject
    DnaService dnaService;

    public String inheritDNA(String fatherDns, String motherDns, Random rng) {
        int[] fatherFeatures = DnaEncoderDecoder.decode(fatherDns);
        int[] motherFeatures = DnaEncoderDecoder.decode(motherDns);
        
        int crossoverPoints = 1 + rng.nextInt(3);
        int[] crossoverPositions = new int[crossoverPoints];
        for (int i = 0; i < crossoverPoints; i++) {
            crossoverPositions[i] = rng.nextInt(128);
        }
        for (int i = 0; i < crossoverPoints - 1; i++) {
            for (int j = i + 1; j < crossoverPoints; j++) {
                if (crossoverPositions[i] > crossoverPositions[j]) {
                    int temp = crossoverPositions[i];
                    crossoverPositions[i] = crossoverPositions[j];
                    crossoverPositions[j] = temp;
                }
            }
        }
        
        int[] childFeatures = new int[128];
        boolean useFather = true;
        int crossoverIndex = 0;
        
        for (int i = 0; i < 128; i++) {
            while (crossoverIndex < crossoverPoints && i >= crossoverPositions[crossoverIndex]) {
                useFather = !useFather;
                crossoverIndex++;
            }
            
            childFeatures[i] = useFather ? fatherFeatures[i] : motherFeatures[i];
        }
        
        double mutationRate = 0.001;
        for (int i = 0; i < 128; i++) {
            if (rng.nextDouble() < mutationRate) {
                childFeatures[i] = rng.nextInt(4);
            }
        }
        
        return DnaEncoderDecoder.encode(childFeatures);
    }

    public String generateRandomDNA(Random rng) {
        return dnaService.generateRandomDna();
    }

    public String mutateDNA(String dna, double rate, Random rng) {
        int[] featureValues = DnaEncoderDecoder.decode(dna);
        
        for (int i = 0; i < featureValues.length; i++) {
            if (rng.nextDouble() < rate) {
                featureValues[i] = rng.nextInt(4);
            }
        }
        
        return DnaEncoderDecoder.encode(featureValues);
    }

    public int calculateInheritedSocialClass(Human father, Human mother, Random rng) {
        double averageClass = (father.socialClass + mother.socialClass) / 2.0;
        double randomOffset = (rng.nextDouble() - 0.5) * 1.0;
        double result = averageClass + randomOffset;
        
        if (rng.nextDouble() < 0.05) {
            if (rng.nextDouble() < 0.5) {
                result += 2.0;
            } else {
                result -= 2.0;
            }
        }
        
        result = Math.max(1, Math.min(3, result));
        return (int) Math.round(result);
    }

    public int determineChildGender(Random rng) {
        return rng.nextDouble() < 0.51 ? 1 : 0;
    }
}
