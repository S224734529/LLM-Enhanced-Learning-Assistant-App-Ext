//package com.example.llm_enhancedlearningassistantapp;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Looper;
//import android.widget.Button;
//import android.widget.TextView;
//import android.widget.Toast;
//import androidx.appcompat.app.AppCompatActivity;
//import org.json.JSONArray;
//import org.json.JSONObject;
//import okhttp3.*;
//import java.io.IOException;
//import java.util.Set;
//
//public class ProfileActivity extends AppCompatActivity {
//    private static final String PROFILE_URL = "http://10.0.2.2:5006/profile/";
//    private final OkHttpClient client = new OkHttpClient();
//    private final Handler mainHandler = new Handler(Looper.getMainLooper());
//
//    private TextView usernameText;
//    private TextView interestsText;
//    private TextView planText;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_profile);
//
//        usernameText = findViewById(R.id.profileUsernameText);
//        interestsText = findViewById(R.id.profileInterestsText);
//        // Using incorrectAnswersText as a placeholder for plan name if not defined in layout
//        planText = findViewById(R.id.incorrectAnswersText);
//        Button shareButton = findViewById(R.id.shareProfileButton);
//
//        String username = AppStore.getUsername(this);
//        fetchProfileFromBackend(username);
//
//        shareButton.setOnClickListener(v -> shareProfile());
//    }
//
//    private void fetchProfileFromBackend(String username) {
//        Request request = new Request.Builder()
//                .url(PROFILE_URL + username)
//                .get()
//                .build();
//
//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                mainHandler.post(() -> {
//                    // Fallback to local data
//                    usernameText.setText(username);
//                    interestsText.setText("Interests: " + AppStore.getInterests(ProfileActivity.this).toString());
//                    planText.setText("Plan: " + AppStore.getPlan(ProfileActivity.this) + " (Offline)");
//                });
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                final String responseData = response.body().string();
//                mainHandler.post(() -> {
//                    if (response.isSuccessful()) {
//                        try {
//                            JSONObject user = new JSONObject(responseData);
//                            usernameText.setText(user.getString("username"));
//
//                            JSONArray interestsArr = user.optJSONArray("interests");
//                            if (interestsArr != null) {
//                                interestsText.setText("Interests: " + interestsArr.toString());
//                            }
//
//                            String plan = user.optString("planName", "Free");
//                            planText.setText("Plan: " + plan);
//                            AppStore.savePlan(ProfileActivity.this, plan);
//
//                        } catch (Exception e) {
//                            Toast.makeText(ProfileActivity.this, "Error parsing profile", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//            }
//        });
//    }
//
//    private void shareProfile() {
//        String username = usernameText.getText().toString();
//        String interests = interestsText.getText().toString();
//        String plan = planText.getText().toString();
//
//        String shareText =
//                "Learning Profile\n\n" +
//                        "Username: " + username + "\n" +
//                        interests + "\n" +
//                        plan + "\n\n" +
//                        "Shared from LLM Enhanced Learning Assistant App.";
//
//        Intent intent = new Intent(Intent.ACTION_SEND);
//        intent.setType("text/plain");
//        intent.putExtra(Intent.EXTRA_SUBJECT, "My Learning Profile");
//        intent.putExtra(Intent.EXTRA_TEXT, shareText);
//
//        startActivity(Intent.createChooser(intent, "Share Profile"));
//    }
//}
package com.example.llm_enhancedlearningassistantapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;

import java.util.Set;

public class ProfileActivity extends AppCompatActivity {

    private ImageView qrImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        TextView usernameText = findViewById(R.id.profileUsernameText);
        TextView interestsText = findViewById(R.id.profileInterestsText);

        TextView totalQuestionsText = findViewById(R.id.totalQuestionsText);
        TextView correctAnswersText = findViewById(R.id.correctAnswersText);
        TextView incorrectAnswersText = findViewById(R.id.incorrectAnswersText);

        Button shareButton = findViewById(R.id.shareProfileButton);
        Button generateQrButton = findViewById(R.id.generateQrButton);

        qrImageView = findViewById(R.id.qrImageView);

        String username = AppStore.getUsername(this);
        Set<String> interests = AppStore.getInterests(this);

        usernameText.setText(username);

        interestsText.setText(
                "Interests: " +
                        (interests.isEmpty()
                                ? "No interests selected"
                                : interests.toString())
        );

        totalQuestionsText.setText("10");
        correctAnswersText.setText("8");
        incorrectAnswersText.setText("2");

        shareButton.setOnClickListener(v ->
                shareProfile(username, interests.toString())
        );

        generateQrButton.setOnClickListener(v ->
                generateQrCode(username, interests.toString())
        );
    }

    private String createProfileText(String username, String interests) {
        return "Learning Profile\n\n" +
                "Username: " + username + "\n" +
                "Interests: " + interests + "\n" +
                "Total Questions: 10\n" +
                "Correct Answers: 8\n" +
                "Incorrect Answers: 2\n\n" +
                "Shared from LLM Enhanced Learning Assistant App.";
    }

    private void shareProfile(String username, String interests) {

        String shareText = createProfileText(username, interests);

        Intent intent = new Intent(Intent.ACTION_SEND);

        intent.setType("text/plain");

        intent.putExtra(Intent.EXTRA_SUBJECT, "My Learning Profile");

        intent.putExtra(Intent.EXTRA_TEXT, shareText);

        startActivity(Intent.createChooser(intent, "Share Profile"));
    }

    private void generateQrCode(String username, String interests) {

        try {

            String qrText = createProfileText(username, interests);

            BitMatrix matrix = new MultiFormatWriter().encode(
                    qrText,
                    BarcodeFormat.QR_CODE,
                    700,
                    700
            );

            int width = matrix.getWidth();
            int height = matrix.getHeight();

            Bitmap bitmap = Bitmap.createBitmap(
                    width,
                    height,
                    Bitmap.Config.RGB_565
            );

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bitmap.setPixel(
                            x,
                            y,
                            matrix.get(x, y)
                                    ? android.graphics.Color.BLACK
                                    : android.graphics.Color.WHITE
                    );
                }
            }

            qrImageView.setImageBitmap(bitmap);

            qrImageView.setVisibility(ImageView.VISIBLE);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}