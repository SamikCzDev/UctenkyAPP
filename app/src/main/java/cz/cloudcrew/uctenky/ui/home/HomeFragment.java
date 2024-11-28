package cz.cloudcrew.uctenky.ui.home;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.cloudcrew.uctenky.MainActivity;
import cz.cloudcrew.uctenky.R;
import cz.cloudcrew.uctenky.adapters.DocumentAdapter;
import cz.cloudcrew.uctenky.req.GetDocuments;
import cz.cloudcrew.uctenky.req.GetInfoFromToken;

public class HomeFragment extends Fragment {

    private ListView listView;
    private List<Map<String, String>> documentList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        listView = rootView.findViewById(R.id.listView);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            String name = documentList.get(position).get("name");
            Toast.makeText(getActivity(), name, Toast.LENGTH_SHORT).show();
        });

        return rootView;
    }
    @Override
    public void onResume() {
        fetchData();
        super.onResume();
    }

    private void fetchData() {
        documentList.clear();
        GetDocuments getInfoFromToken = new GetDocuments(getContext());

        getInfoFromToken.getInfo(MainActivity.authToken, new GetDocuments.DocumentsCallback() {

            @Override
            public void onSuccess(JSONArray jsonArray) throws JSONException {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    Map<String, String> map = new HashMap<>();
                    map.put("name", jsonObject.getString("name"));
                    map.put("expiration", jsonObject.getString("expiration"));
                    map.put("type", jsonObject.getString("type"));
                    documentList.add(map);
                }
                getActivity().runOnUiThread(() -> {
                    DocumentAdapter adapter = new DocumentAdapter(getActivity(), documentList);
                    listView.setAdapter(adapter);
                });
            }

            @Override
            public void onError(String error) {
                Toast.makeText(getContext(), "Error: " + error, Toast.LENGTH_LONG).show();
            }
        });

    }
}