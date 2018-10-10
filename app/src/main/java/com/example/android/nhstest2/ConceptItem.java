package com.example.android.nhstest2;

public class ConceptItem {

    private String mTerm;
    private long mConceptId;
    private String mFsn;

    ConceptItem(String term, long conceptId, String fsn){
        mTerm = term;
        mConceptId = conceptId;
        mFsn = fsn;
    }

    public String getTerm() {
        return mTerm;
    }

    public long getConceptId() {
        return mConceptId;
    }

    public String getFsn() {
        return mFsn;
    }
}
