package br.com.ufcg.splab.recsys.recommender;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public abstract class Approach
{
    protected List<Map<String, Double>> itemsProfiles;
    protected Map<String, Double> userProfile;

    public void addItem(Map<String, Double> itemProfile)
    {
        this.itemsProfiles.add(itemProfile);
    }

    public List<Map<String, Double>> getItemsProfiles()
    {
        return this.itemsProfiles;
    }

    public abstract Map<String, Double> getUserProfile() throws Exception;

    public void setUserProfile(Map<String, Double> userProfile)
    {
        this.userProfile = userProfile;
    }

    public List<SimilarityMapper> getOrderedItemsTo(
            SimilarityMethod similarityMethod) throws Exception
    {
        if (this.getUserProfile() == null) {
            throw new Exception("Invalid user profile");
        }

        List<SimilarityMapper> values = new LinkedList<SimilarityMapper>();
        int i = 0;
        for (Map<String, Double> itemProfile : this.getItemsProfiles()) {
            Double similarity = similarityMethod.calculate(
                    this.getUserProfile(), itemProfile);

            values.add(new SimilarityMapper(i, itemProfile, similarity));
            i++;
        }

        Collections.sort(values);

        return values;
    }
}
