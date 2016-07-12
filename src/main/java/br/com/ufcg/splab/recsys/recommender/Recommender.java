package br.com.ufcg.splab.recsys.recommender;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Recommender
{
    private Approach approach;

    public Recommender(Approach approach)
    {
        this.approach = approach;
        this.approach.clearItems();
    }

    public void addItem(Map<String, Double> itemProfile)
    {
        this.approach.addItem(itemProfile);
    }

    public List<SimilarityMapper> recommendTo(Map<String, Double> userProfile,
            Integer numItems) throws Exception
    {
        this.approach.setUserProfile(userProfile);
        List<SimilarityMapper> values = this.approach.getOrderedItems();
        List<SimilarityMapper> result = this.filterTopNFrom(values, numItems);

        return result;
    }

    private List<SimilarityMapper> filterTopNFrom(
            List<SimilarityMapper> values, Integer numItems)
    {
        List<SimilarityMapper> result = new LinkedList<SimilarityMapper>();
        numItems = Math.max(numItems, values.size());

        for (int i = 0; i < numItems; i++) {
            result.add(values.get(i));
        }

        return result;
    }
}
