package com.example.android.nhstest2;

import java.util.ArrayList;

public class Concept {
    private final String fullySpecifiedName;
    private final String preferredTerm;
    private final String active;
    private final String conceptId;

    private final ArrayList<String> descriptionCodes;
    private final ArrayList<String> descriptionTerms;

    private final ArrayList<String> childCodes;
    private final ArrayList<String> childTerms;

    private final ArrayList<String> parentCodes;
    private final ArrayList<String> parentTerms;

    Concept(String fullySpecifiedName, String preferredTerm, String active, String conceptId) {
        this.fullySpecifiedName = fullySpecifiedName;
        this.preferredTerm = preferredTerm;
        this.active = active;
        this.conceptId = conceptId;

        descriptionCodes = new ArrayList<>();
        descriptionTerms = new ArrayList<>();

        childCodes = new ArrayList<>();
        childTerms = new ArrayList<>();

        parentCodes = new ArrayList<>();
        parentTerms = new ArrayList<>();
    }

    public String getFullySpecifiedName() {
        return fullySpecifiedName;
    }

    public String getPreferredTerm() {
        return preferredTerm;
    }

    public String getActive() {
        return active;
    }

    public String getConceptId() {
        return conceptId;
    }

    public ArrayList<String> getDescription(int index) {
        ArrayList<String> description = new ArrayList<>();
        description.add(descriptionCodes.get(index));
        description.add(descriptionTerms.get(index));
        return description;
    }

    public void addDescription(String id, String term){
        descriptionCodes.add(id);
        descriptionTerms.add(term);
    }

    public int getDescriptionSize(){
        return descriptionCodes.size();
    }

    public ArrayList<String> getChild(int index) {
        ArrayList<String> child = new ArrayList<>();
        child.add(childCodes.get(index));
        child.add(childTerms.get(index));
        return child;
    }

    public void addChild(String id, String term){
        childCodes.add(id);
        childTerms.add(term);
    }

    public int getChildSize(){
        return childCodes.size();
    }

    public ArrayList<String> getParent(int index) {
        ArrayList<String> parent = new ArrayList<>();
        parent.add(parentCodes.get(index));
        parent.add(parentTerms.get(index));
        return parent;
    }

    public void addParent(String id, String term){
        parentCodes.add(id);
        parentTerms.add(term);
    }

    public int getParentSize(){
        return parentCodes.size();
    }
}
