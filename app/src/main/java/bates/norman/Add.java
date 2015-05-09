package bates.norman;

import android.content.Context;
import android.database.DataSetObserver;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.location.places.AddPlaceRequest;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.GeoDataApi;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.PlaceTypes;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;

public class Add extends ActionBarActivity
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private final LatLngBounds GLOBAL_BOUNDS = new LatLngBounds.Builder()
            .include(new LatLng(85, -180))
            .include(new LatLng(-85, 180))
            .build();

    private AutoCompleteTextView placeTextView;
    private PlaceAdapter placeAdapter = new PlaceAdapter(this,
            android.R.layout.simple_dropdown_item_1line);

    private AutoCompleteTextView contactTextView;
    private Spinner repeatSpinner;

    private GoogleApiClient apiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        apiClient = new GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addOnConnectionFailedListener(this)
                .addConnectionCallbacks(this)
                .build();

        placeTextView = (AutoCompleteTextView)findViewById(R.id.place);
        placeTextView.setAdapter(placeAdapter);
        placeTextView.setThreshold(1);

        contactTextView = (AutoCompleteTextView)findViewById(R.id.contact);

        repeatSpinner = (Spinner)findViewById(R.id.repeat_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
            R.array.repeat_options, R.layout.add_spinner);
        adapter.setDropDownViewResource(R.layout.add_spinner);
        repeatSpinner.setAdapter(adapter);
    }
    @Override
    protected void onStart() {
        super.onStart();
        apiClient.connect();
        Log.v("Norman", "attempting to connect");
    }

    @Override
    protected void onStop() {
        apiClient.disconnect();
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_default) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.v("Norman", "connected");
        updatePlaceAutocomplete();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.v("Norman", "suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.v("Norman", "connection failed");
    }

    public void updatePlaceAutocomplete() {
        placeAdapter.add("yo");
        placeAdapter.add("hey");
        placeAdapter.add("wassup");
    }

    private class PlaceAdapter implements ListAdapter, Filterable {

        private ArrayList<String> places = new ArrayList<>();
        private Context context;
        private int resource;
        private ArrayList<DataSetObserver> observers = new ArrayList<>();

        PlaceAdapter(Context context, int resource) {
            this.context = context;
            this.resource = resource;
        }

        public void add(String place) {
            places.add(place);
            notifyChange();
        }

        public void clear() {
            places.clear();
            notifyChange();
        }

        protected void notifyChange() {
            for (DataSetObserver observer : observers) {
                observer.onChanged();
            }
        }

        @Override
        public Filter getFilter() {
            return new EverythingFilter();
        }

        @Override
        public boolean areAllItemsEnabled() {
            return true;
        }

        @Override
        public boolean isEnabled(int position) {
            return true;
        }

        @Override
        public void registerDataSetObserver(DataSetObserver observer) {
            observers.add(observer);
        }

        @Override
        public void unregisterDataSetObserver(DataSetObserver observer) {
            observers.remove(observer);
        }

        @Override
        public int getCount() {
            return places.size();
        }

        @Override
        public Object getItem(int position) {
            return places.get(position);
        }

        @Override
        public long getItemId(int position) {
            return (long)places.get(position).hashCode();
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getView(int position, @Nullable View convertView, ViewGroup parent) {
            if (convertView != null) {
                TextView v = (TextView)convertView;
                v.setText(places.get(position));
                return v;
            }
            TextView result = (TextView)LayoutInflater.from(context).inflate(resource, parent,
                    false);
            result.setText(places.get(position));
            return result;
        }

        @Override
        public int getItemViewType(int position) {
            return 0;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public boolean isEmpty() {
            return places.size() == 0;
        }

        private class EverythingFilter extends Filter {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                return new FilterResults();
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                notifyChange();
            }

        }

    }

}
