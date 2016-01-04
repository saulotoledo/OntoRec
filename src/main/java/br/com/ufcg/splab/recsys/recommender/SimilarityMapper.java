package br.com.ufcg.splab.recsys.recommender;

import java.util.Map;

public class SimilarityMapper implements Comparable<SimilarityMapper>
{
    private Integer profileId;
    private Map<String, Double> profile;
    private Double similarity;

    public SimilarityMapper(Integer profileId, Map<String, Double> profile,
            Double similarity)
    {
        this.profileId = profileId;
        this.profile = profile;
        this.similarity = similarity;
    }

    public Integer getProfileId()
    {
        return this.profileId;
    }

    public Map<String, Double> getProfile()
    {
        return this.profile;
    }

    public Double getSimilarity()
    {
        return this.similarity;
    }

    @Override
    public int compareTo(SimilarityMapper obj)
    {
        return this.getSimilarity().compareTo(obj.getSimilarity());
    }

    @Override
    public String toString()
    {
        return String.format("%s: [ %s -> %s ]", this.getClass()
                .getSimpleName(), this.getProfileId(), this.getSimilarity());
    }
}
