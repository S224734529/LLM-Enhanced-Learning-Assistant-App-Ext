package com.example.llm_enhancedlearningassistantapp;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONObject;
import okhttp3.*;
import java.io.IOException;

public class UpgradeActivity extends AppCompatActivity {
    private static final String PURCHASE_URL = "http://10.0.2.2:5006/purchase";
    private final OkHttpClient client = new OkHttpClient();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upgrade);

        Button starterButton = findViewById(R.id.starterPurchaseButton);
        Button intermediateButton = findViewById(R.id.intermediatePurchaseButton);
        Button advancedButton = findViewById(R.id.advancedPurchaseButton);

        starterButton.setOnClickListener(v -> purchasePlan("Starter", 9.99));
        intermediateButton.setOnClickListener(v -> purchasePlan("Intermediate", 19.99));
        advancedButton.setOnClickListener(v -> purchasePlan("Advanced", 29.99));
    }

    private void purchasePlan(String planName, double amount) {
        // Save locally
        AppStore.savePlan(this, planName);
        
        // Sync with MongoDB
        try {
            JSONObject json = new JSONObject();
            json.put("username", AppStore.getUsername(this));
            json.put("planName", planName);
            json.put("amount", amount);

            RequestBody body = RequestBody.create(json.toString(), MediaType.get("application/json; charset=utf-8"));
            Request request = new Request.Builder().url(PURCHASE_URL).post(body).build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    mainHandler.post(() -> Toast.makeText(UpgradeActivity.this, "Purchase synced locally, backend failed.", Toast.LENGTH_SHORT).show());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    mainHandler.post(() -> {
                        if (response.isSuccessful()) {
                            Toast.makeText(UpgradeActivity.this, planName + " plan purchased and synced!", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(UpgradeActivity.this, "Purchase successful locally.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        } catch (Exception e) {
            Toast.makeText(this, planName + " plan purchased successfully.", Toast.LENGTH_LONG).show();
        }
    }
}