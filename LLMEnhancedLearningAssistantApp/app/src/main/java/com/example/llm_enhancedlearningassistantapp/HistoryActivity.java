package com.example.llm_enhancedlearningassistantapp;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONObject;
import okhttp3.*;
import java.io.IOException;

public class HistoryActivity extends AppCompatActivity {
    private static final String HISTORY_URL = "http://10.0.2.2:5006/history/";
    private final OkHttpClient client = new OkHttpClient();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private TextView historyText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        historyText = findViewById(R.id.historyText);
        String username = AppStore.getUsername(this);

        fetchHistoryFromBackend(username);
    }

    private void fetchHistoryFromBackend(String username) {
        Request request = new Request.Builder()
                .url(HISTORY_URL + username)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mainHandler.post(() -> {
                    historyText.setText("Failed to load history from MongoDB.\nShowing last local result:\n\n" + AppStore.getLastResult(HistoryActivity.this));
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseData = response.body().string();
                mainHandler.post(() -> {
                    if (response.isSuccessful()) {
                        displayHistory(responseData);
                    } else {
                        historyText.setText("No history found in MongoDB.");
                    }
                });
            }
        });
    }

    private void displayHistory(String jsonArrayString) {
        try {
            JSONArray array = new JSONArray(jsonArrayString);
            if (array.length() == 0) {
                historyText.setText("No tasks completed yet.");
                return;
            }

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                sb.append(i + 1).append(". ").append(obj.optString("topic", "General")).append("\n");
                sb.append("Prompt: ").append(obj.optString("prompt")).append("\n");
                String resp = obj.optString("response");
                if (resp.length() > 100) resp = resp.substring(0, 97) + "...";
                sb.append("AI: ").append(resp).append("\n\n");
            }
            historyText.setText(sb.toString());
        } catch (Exception e) {
            historyText.setText("Error parsing history data.");
        }
    }
}