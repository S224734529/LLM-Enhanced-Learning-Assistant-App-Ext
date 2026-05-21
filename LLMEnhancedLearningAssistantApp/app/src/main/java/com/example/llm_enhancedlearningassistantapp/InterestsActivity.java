package com.example.llm_enhancedlearningassistantapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONObject;
import okhttp3.*;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class InterestsActivity extends AppCompatActivity {
    private final Set<String> selected = new HashSet<>();
    private final String[] topics = {
            "Algorithms", "Data Structures",
            "Web Development", "Testing",
            "Cloud Computing", "Databases",
            "Cyber Security", "Android",
            "AI Basics", "Project Management"
    };

    private static final String UPDATE_INTERESTS_URL = "http://10.0.2.2:5006/update-interests";
    private final OkHttpClient client = new OkHttpClient();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interests);

        GridLayout topicGrid = findViewById(R.id.topicGrid);
        Button nextButton = findViewById(R.id.nextButton);

        for (String topic : topics) {
            Button chip = new Button(this);
            chip.setText(topic);
            chip.setTextSize(12);
            chip.setBackgroundResource(R.drawable.chip_bg);
            chip.setAllCaps(false);
            chip.setContentDescription("Select topic " + topic);

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            params.setMargins(6, 6, 6, 6);
            chip.setLayoutParams(params);

            chip.setOnClickListener(v -> {
                if (selected.contains(topic)) {
                    selected.remove(topic);
                    chip.setAlpha(1.0f);
                } else {
                    if (selected.size() >= 10) {
                        Toast.makeText(this, "You can select up to 10 topics.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    selected.add(topic);
                    chip.setAlpha(0.55f);
                }
            });

            topicGrid.addView(chip);
        }

        nextButton.setOnClickListener(v -> {
            if (selected.isEmpty()) {
                Toast.makeText(this, "Select at least one topic.", Toast.LENGTH_SHORT).show();
                return;
            }
            AppStore.saveInterests(this, selected);
            syncInterestsWithBackend();
        });
    }

    private void syncInterestsWithBackend() {
        try {
            JSONObject json = new JSONObject();
            json.put("username", AppStore.getUsername(this));
            json.put("interests", new JSONArray(selected));

            RequestBody body = RequestBody.create(json.toString(), MediaType.get("application/json; charset=utf-8"));
            Request request = new Request.Builder().url(UPDATE_INTERESTS_URL).post(body).build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    mainHandler.post(() -> proceed());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    mainHandler.post(() -> proceed());
                }
            });
        } catch (Exception e) {
            proceed();
        }
    }

    private void proceed() {
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }
}