package br.com.ufcg.splab.recsys.ontorec.recommender;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import br.com.ufcg.splab.recsys.ontorec.NodeManager;
import br.com.ufcg.splab.recsys.ontorec.OWLReader;
import br.com.ufcg.splab.recsys.ontorec.weighting.NodeWeightingApproach;
import br.com.ufcg.splab.recsys.recommender.Approach;
import br.com.ufcg.splab.recsys.recommender.SimilarityMethod;

public class OntoRecApproach extends Approach
{
    // private Set<String> selectedFeatures;
    private NodeManager<String> nm;
    private Integer maxHeight;

    public OntoRecApproach(String ontologyFile,
        NodeWeightingApproach<String> nodeWeightingApproach,
        Boolean lambda, Boolean upsilon,
        MappingsProcessor mp, SimilarityMethod similarityMethod)
        throws Exception
    {
        super(similarityMethod);

        OWLReader reader = new OWLReader(ontologyFile, nodeWeightingApproach,
            lambda, upsilon);

        this.nm = reader.getNodeManager();
        mp.mapAt(this.nm);
    }

    public Integer getMaxHeight()
    {
        return this.maxHeight;
    }

    public void setMaxHeight(Integer maxHeight)
    {
        this.maxHeight = maxHeight;
    }

    @Override
    public void setUserProfile(Map<String, Double> userProfile) throws Exception
    {
        // This method should not be needed.
        super.setUserProfile(userProfile);
        super.setUserProfile(this.getUserProfile());
    }

    public Set<String> getSelectedFeatures()
    {
        Set<String> result = new HashSet<String>();
        for (String key : this.userProfile.keySet()) {
            if (this.userProfile.get(key).equals(1d)) {
                result.add(key);
            }
        }
        return result;
    }

    /*
     * public void setSelectedFeatures(Set<String> selectedFeatures) {
     * this.selectedFeatures = selectedFeatures; }
     */
    @Override
    public Map<String, Double> getUserProfile() throws Exception
    {
        if (this.getSelectedFeatures() == null) {
            throw new Exception("Invalid selected features set");
        }

        if (this.getMaxHeight() == null) {
            throw new Exception("Invalid max height");
        }

        // TODO: This behavior should not be rewritten (it should be at setter
        // method):
        this.userProfile = this.nm.getFeaturesWeight(this.getSelectedFeatures(),
            this.getMaxHeight());

        return this.userProfile;
    }
}
