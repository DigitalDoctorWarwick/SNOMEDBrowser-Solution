package com.example.android.nhstest2;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

/**
 * Loads a list of concepts by using an AsyncTask to perform the
 * network request to the given URL.
 */
public class ConceptLoader extends AsyncTaskLoader<List<ConceptItem>> {

    /** Query URL */
    private final String mUrl;

    /**
     * Constructs a new {@link ConceptLoader}.
     *
     * @param context of the activity
     * @param url to load data from
     */
    ConceptLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    /**
     * This is on a background thread.
     */
    @Override
    public List<ConceptItem> loadInBackground() {
        if (mUrl == null) {
            return null;
        }

        // Perform the network request, parse the response, and extract a list of concepts.
        return QueryUtils.fetchConceptItemData(mUrl);
    }
}