package br.com.ufcg.splab.recsys.recommender;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Approach
{
    /**
     * The application logger.
     */
    protected static final Logger LOGGER = LoggerFactory
        .getLogger(Approach.class);

    protected List<Map<String, Double>> itemsProfiles;
    protected Map<String, Double> userProfile;
    protected SimilarityMethod similarityMethod;

    public Approach(SimilarityMethod similarityMethod)
    {
        this.itemsProfiles = new LinkedList<Map<String, Double>>();
        this.similarityMethod = similarityMethod;
    }

    // TODO: This should not be here. Rethink the system architecture:
    public void clearItems()
    {
        LOGGER.debug(
            String.format("%s approach: Removing all items for this approach",
                this.getClass().getSimpleName()));

        this.itemsProfiles = new LinkedList<Map<String, Double>>();
    }

    public void addItem(Map<String, Double> itemProfile)
    {
        LOGGER.debug(String.format(
            "%s approach: Adding the following item for this approach: %s",
            this.getClass().getSimpleName(), itemProfile));

        this.itemsProfiles.add(itemProfile);
    }

    public List<Map<String, Double>> getItemsProfiles()
    {
        return this.itemsProfiles;
    }

    public abstract Map<String, Double> getUserProfile() throws Exception;

    public void setUserProfile(Map<String, Double> userProfile) throws Exception
    {
        LOGGER
            .debug(String.format("%s approach: Setting the user profile to %s",
                this.getClass().getSimpleName(), userProfile));

        this.userProfile = userProfile;
    }

    public List<SimilarityMapper> getOrderedItems() throws Exception
    {
        // TODO: Using this.getUserProfile() here breaks OntoRecApproach. Fix
        // it!
        if (this.userProfile == null) {
            throw new Exception("Invalid user profile");
        }

        List<SimilarityMapper> values = new LinkedList<SimilarityMapper>();
        for (Map<String, Double> itemProfile : this.getItemsProfiles()) {

            String diaramInfoKeyName = this.getDiaramInfoKeyName(itemProfile);

            Integer profileId = itemProfile.get(diaramInfoKeyName).intValue();
            Map<String, Double> itemProfileFeaturesOnly = this
                .getProfileFeaturesOnly(diaramInfoKeyName, itemProfile);

            Double similarity = this.similarityMethod
                .calculate(this.userProfile, itemProfileFeaturesOnly);

            values
                .add(new SimilarityMapper(profileId, itemProfile, similarity));

        }

        Collections.sort(values);
        Collections.reverse(values);

        LOGGER.debug(String.format("%s approach: The ordered result is '%s'",
            this.getClass().getSimpleName(), values));

        return values;
    }

    // TODO: This should not be needed. Refactor!
    protected String getDiaramInfoKeyName(Map<String, Double> itemProfile)
    {
        for (String key : itemProfile.keySet()) {
            if (key.contains(":ID")) {
                return key;
            }
        }
        return null;
    }

    // TODO: This should not be needed. Refactor!
    protected Map<String, Double> getProfileFeaturesOnly(
        String diaramInfoKeyName, Map<String, Double> itemProfile)
    {
        Map<String, Double> itemProfileFeaturesOnly = new HashMap<String, Double>(
            itemProfile);
        itemProfileFeaturesOnly.remove(diaramInfoKeyName);

        return itemProfileFeaturesOnly;
    }
}
