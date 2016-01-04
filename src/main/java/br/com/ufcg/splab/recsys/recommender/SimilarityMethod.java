package br.com.ufcg.splab.recsys.recommender;

import java.util.Map;

public interface SimilarityMethod
{
    public Double calculate(Map<String, Double> v1, Map<String, Double> v2)
            throws Exception;
}
