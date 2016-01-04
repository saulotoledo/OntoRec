package br.com.ufcg.splab.recsys.ontorec.recommender;

import br.com.ufcg.splab.recsys.ontorec.NodeManager;

public interface MappingsProcessor
{
    public void mapAt(NodeManager<String> nm) throws Exception;
}
