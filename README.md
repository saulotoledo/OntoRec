# OntoRec

## General idea

This is a Java implementation of the OntoRec (Ontology based Recommendations) algorithm proposed in Saulo Toledo's master thesis. The algorithm uses an ontology file and some configurations to expand a profile vector.

The main idea of OntoRec is to use an ontology to discover related concepts that may be of interest of the user. Suppose the following user vector, in which each component is related to a feature in your problem domain:

```
u = (1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0)
```

By using the provided ontology and some parameters, OntoRec discover some related concepts, giving them values bigger than zero. For instance:

```
u = (1, 0.9, 0.3, 0, 0, 0.7, 1, 0, 0.01, 0, 0)
```

The result vector can be used in your recommender system to discover related items.


## OntoRec parameters

OntoRec needs some adjustment parameters to work. Please consult the related master thesis to have detailed information.
