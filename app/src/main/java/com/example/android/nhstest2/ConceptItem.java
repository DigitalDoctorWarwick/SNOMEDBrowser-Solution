package com.example.android.nhstest2;

public class ConceptItem {

    private final String mTerm;
    private final long mConceptId;
    private final String mFsn;

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
