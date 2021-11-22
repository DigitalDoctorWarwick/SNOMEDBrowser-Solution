package com.example.android.nhstest2;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.GridLayout;
import android.widget.TextView;

import java.net.URL;
import java.util.ArrayList;

public class ConceptDetailActivity extends AppCompatActivity {

    private Uri mConceptUri;
    private Concept mConcept;

    public static final int CHILD_CODE = 1;
    public static final int PARENT_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_concept_detail);
        mConceptUri = getIntent().getData();
        //getActionBar().setDisplayHomeAsUpEnabled(true);

        ConceptAsyncTask conceptAsyncTask = new ConceptAsyncTask();
        conceptAsyncTask.execute();
    }

    private void updateUi(Concept concept){
        TextView preferredTerm = findViewById(R.id.preferred_term);
        preferredTerm.setText(concept.getPreferredTerm());

        TextView fullySpecifiedName = findViewById(R.id.fully_specified_name);
        fullySpecifiedName.setText(concept.getFullySpecifiedName());

        TextView conceptId = findViewById(R.id.concept_id);
        conceptId.setText(concept.getConceptId());

        TextView active = findViewById(R.id.active);
        active.setText(concept.getActive());

        GridLayout descriptionsLayout = findViewById(R.id.descriptions);
        for (int i = 0; i < concept.getDescriptionSize(); i++) {
            ArrayList<String> description = concept.getDescription(i);
            getLayoutInflater().inflate(R.layout.description_id, descriptionsLayout);
            getLayoutInflater().inflate(R.layout.description_text, descriptionsLayout);
            ((TextView) descriptionsLayout.getChildAt(descriptionsLayout.getChildCount()-2)).setText(description.get(0));
            ((TextView) descriptionsLayout.getChildAt(descriptionsLayout.getChildCount()-1)).setText(description.get(1));
        }
    }

    public void getNewConcept(View view){
        TextView tv = (TextView) view;
        Uri conceptUri = Uri.parse(MainActivity.CONCEPT_REQUEST_URL + tv.getText());
        Intent conceptIntent = new Intent(this, ConceptDetailActivity.class);
        conceptIntent.setData(conceptUri);
        startActivity(conceptIntent);
    }

    private class ConceptAsyncTask extends AsyncTask<URL, Void, Concept> {

        @Override
        protected Concept doInBackground(URL... urls) {
            return QueryUtils.fetchConceptData(mConceptUri.toString());
        }

        @Override
        protected void onPostExecute(Concept concept) {
            if (concept == null) {
                return;
            }

            mConcept = concept;
            updateUi(concept);

            String relatedQueryURL =
            //        "http://browser.ihtsdotools.org/api/v2/snomed/"+MainActivity.edition+"/"+MainActivity.release+"/concepts/";
                    "https://snowstorm.ihtsdotools.org/snowstorm/snomed-ct/browser/MAIN/concepts/";
            Uri baseUri = Uri.parse(relatedQueryURL);

            Uri.Builder childUriBuilder = baseUri.buildUpon();
            childUriBuilder.appendPath(concept.getConceptId());
            childUriBuilder.appendPath("children");
            childUriBuilder.appendQueryParameter("form","inferred");
            RelatedConceptAsyncTask childConceptAsyncTask = new RelatedConceptAsyncTask();
            Log.i("ConceptDetailActivity", childUriBuilder.build().toString());
            childConceptAsyncTask.execute(childUriBuilder.build().toString(), String.valueOf(CHILD_CODE));

            Uri.Builder parentUriBuilder = baseUri.buildUpon();
            parentUriBuilder.appendPath(concept.getConceptId());
            parentUriBuilder.appendPath("parents");
            parentUriBuilder.appendQueryParameter("form","inferred");
            RelatedConceptAsyncTask parentConceptAsyncTask = new RelatedConceptAsyncTask();
            Log.i("ConceptDetailActivity", parentUriBuilder.build().toString());
            parentConceptAsyncTask.execute(parentUriBuilder.build().toString(), String.valueOf(PARENT_CODE));
        }

    }

    private class RelatedConceptAsyncTask extends AsyncTask<String, Void, Concept> {

        int type;

        @Override
        protected Concept doInBackground(String... urls) {
            type = Integer.parseInt(urls[1]);
            return QueryUtils.fetchRelatedConceptData(urls[0],mConcept,type);
        }

        @Override
        protected void onPostExecute(Concept concept) {
            if (type == CHILD_CODE) {
                GridLayout childLayout = findViewById(R.id.children);
                for (int i = 0; i < concept.getChildSize(); i++){
                    ArrayList<String> child = concept.getChild(i);
                    getLayoutInflater().inflate(R.layout.description_id_clickable, childLayout);
                    getLayoutInflater().inflate(R.layout.description_text, childLayout);
                    ((TextView) childLayout.getChildAt(childLayout.getChildCount()-2)).setText(child.get(0));
                    ((TextView) childLayout.getChildAt(childLayout.getChildCount()-1)).setText(child.get(1));
                }
            } else if (type == PARENT_CODE) {
                GridLayout parentLayout = findViewById(R.id.parents);
                for (int i = 0; i < concept.getParentSize(); i++){
                    ArrayList<String> parent = concept.getParent(i);
                    getLayoutInflater().inflate(R.layout.description_id_clickable, parentLayout);
                    getLayoutInflater().inflate(R.layout.description_text, parentLayout);
                    ((TextView) parentLayout.getChildAt(parentLayout.getChildCount()-2)).setText(parent.get(0));
                    ((TextView) parentLayout.getChildAt(parentLayout.getChildCount()-1)).setText(parent.get(1));
                }
            }
        }
    }
}
