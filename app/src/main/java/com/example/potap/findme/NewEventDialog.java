package com.example.potap.findme;

import android.annotation.SuppressLint;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.potap.findme.firebase.DataManager;
import com.example.potap.findme.model.Event;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;

import java.io.IOException;
import java.util.List;

import static com.example.potap.findme.activity.MapActivity.events;

@SuppressLint("ValidFragment")
public class NewEventDialog extends DialogFragment {

    public NewEventDialog(GoogleMap map, LatLng latLng, Geocoder geocoder) {
        this.map = map;
        this.latLng = latLng;
        this.geocoder = geocoder;
    }

    private static final String TAG = "NewEventDialog";

    private GoogleMap map;
    private LatLng latLng;
    private Geocoder geocoder;

    private EditText titleText;
    private EditText descriptionText;
    private Button btSave;
    private ImageButton btCancel;

    private DatabaseReference eventsReference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_adding_event, container, false);
        titleText = view.findViewById(R.id.event_title_edit_text);
        descriptionText = view.findViewById(R.id.description_edit_text);
        btSave = view.findViewById(R.id.bt_save_event);
        btCancel = view.findViewById(R.id.bt_cancel);

        eventsReference = DataManager.getInstance().getEventsReference();

        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: closing NewEventDialog");
                getDialog().dismiss();
            }
        });

        btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = titleText.getText().toString();
                String description = descriptionText.getText().toString();
                int id = events.isEmpty()? 1: events.size()+1;
                DatabaseReference eventRef = eventsReference.child(Integer.toString(id));

                Log.d(TAG, "onClick: save new event");

                if (title.isEmpty() || description.isEmpty()) {
                    Toast.makeText(getContext(), "Title or description is empty", Toast.LENGTH_SHORT).show();
                } else {
                    List<Address> address = null;

                    try {
                        address = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    String addStr = address.get(0).getAddressLine(0);
                    Event event = new Event(
                            id,
                            title,
                            description,
                            latLng,
                            addStr,
                            1,
                            DataManager.getInstance().getFirebaseAuth().getUid());

                     eventRef.setValue(event);

                    events.add(event);

                    MarkerOptions options = new MarkerOptions().position(latLng).title(title);
                    map.addMarker(options);


                    getDialog().dismiss();
                }
            }
        });

        return view;
    }
}
