package com.example.android.nhstest2;

import android.content.Intent;
import android.content.Loader;
import android.app.LoaderManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.app.LoaderManager.LoaderCallbacks;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements LoaderCallbacks<List<ConceptItem>>{

    private ProgressBar pb;

    /** Adapter for the list of Concepts */
    private ConceptItemAdapter mAdapter;

    /** Used query params */
    public static final String edition = "en-edition";
    public static final String release = "v20180731";
    private static String query = "a";
    private static final String mode = "partialMatching";
    private static final String language = "english";
    private static final String semanticFilter = "disorder";
    private static final int returnLimit = 100;

    private static final String NHS_REQUEST_URL =
            "http://browser.ihtsdotools.org/api/v2/snomed/"+edition+"/"+release+"/descriptions";

    public static final String CONCEPT_REQUEST_URL =
            "http://browser.ihtsdotools.org/api/v2/snomed/"+edition+"/"+release+"/concepts/";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find a reference to the {@link ListView} in the layout
        ListView conceptListView = findViewById(R.id.list);
        conceptListView.setEmptyView(findViewById(R.id.empty_view));

        // Create a new {@link ArrayAdapter} of concepts
        mAdapter = new ConceptItemAdapter(this, new ArrayList<ConceptItem>());

        // Init loading spinner
        pb = findViewById(R.id.pbLoading);

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        conceptListView.setAdapter(mAdapter);

        conceptListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Find the current earthquake that was clicked on
                ConceptItem currentConceptItem = mAdapter.getItem(position);

                // Convert the String URL into a URI object (to pass into the Intent constructor)
                Uri guideUri;
                if (currentConceptItem == null)
                    return;
                else
                    guideUri = Uri.parse(CONCEPT_REQUEST_URL + currentConceptItem.getConceptId());

                Intent conceptIntent = new Intent(MainActivity.this, ConceptDetailActivity.class);
                conceptIntent.setData(guideUri);

                // Send the intent to launch a new activity
                startActivity(conceptIntent);
            }
        });

        // Initialize the loader. pass in null for the bundle.
        // Pass in this activity for the LoaderCallbacks parameter (which is valid
        // because this activity implements the LoaderCallbacks interface).
        final LoaderManager loaderManager = getLoaderManager();
        loaderManager.initLoader(0, null, this);

        EditText searchTerm = findViewById(R.id.search_term);

        searchTerm.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!editable.toString().equals("")) {
                    query = editable.toString();
                    loaderManager.restartLoader(0, null,MainActivity.this);
                    pb.setVisibility(ProgressBar.VISIBLE);
                }
            }
        });

    }

    @Override
    public Loader<List<ConceptItem>> onCreateLoader(int i, Bundle bundle) {

        Uri baseUri = Uri.parse(NHS_REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter("query", query);
        uriBuilder.appendQueryParameter("searchMode", mode);
        uriBuilder.appendQueryParameter("lang", language);
        uriBuilder.appendQueryParameter("returnLimit", ""+returnLimit);
        uriBuilder.appendQueryParameter("semanticFilter", semanticFilter);

        return new ConceptLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<ConceptItem>> loader, List<ConceptItem> conceptItems) {
        // If there is a valid list of {@link ConceptItem}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        mAdapter.clear();
        if (conceptItems != null && !conceptItems.isEmpty()) {
            mAdapter.addAll(conceptItems);
        }
        ((TextView) findViewById(R.id.empty_view)).setText(getResources().getString(R.string.empty_view_text));
        pb.setVisibility(ProgressBar.GONE);
    }

    @Override
    public void onLoaderReset(Loader<List<ConceptItem>> loader) {
        // Loader reset, so we can clear out our existing data.
        mAdapter.clear();
        loader.abandon();
    }
}


