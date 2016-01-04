package br.com.ufcg.splab.recsys.ontorec.recommender;

import java.util.Map;
import java.util.Set;

import br.com.ufcg.splab.recsys.ontorec.NodeManager;
import br.com.ufcg.splab.recsys.ontorec.OWLReader;
import br.com.ufcg.splab.recsys.ontorec.weighting.AbstractNodeWeightingApproach;
import br.com.ufcg.splab.recsys.recommender.Approach;

public class OntoRecApproach extends Approach
{
    private Set<String> selectedFeatures;
    private NodeManager<String> nm;
    private Integer maxHeight;

    public OntoRecApproach(String ontologyFile,
            AbstractNodeWeightingApproach<String> nodeWeightingApproach,
            Boolean ignoreOnlyBegottenFathers, Boolean achieveOtherMappedNodes,
            MappingsProcessor mp) throws Exception
    {
        OWLReader reader = new OWLReader(ontologyFile, nodeWeightingApproach,
                ignoreOnlyBegottenFathers, achieveOtherMappedNodes);

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

    public Set<String> getSelectedFeatures()
    {
        return this.selectedFeatures;
    }

    public void setSelectedFeatures(Set<String> selectedFeatures)
    {
        this.selectedFeatures = selectedFeatures;
    }

    @Override
    public Map<String, Double> getUserProfile() throws Exception
    {
        if (this.getUserProfile() == null) {
            throw new Exception("Invalid selected features set");
        }

        if (this.getMaxHeight() == null) {
            throw new Exception("Invalid max height");
        }

        return this.nm.getFeaturesWeight(this.getSelectedFeatures(),
                this.getMaxHeight());
    }
}
