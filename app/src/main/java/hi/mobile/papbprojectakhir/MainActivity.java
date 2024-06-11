package hi.mobile.papbprojectakhir;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements InputFragment.OnPreferencesSubmittedListener {

    private static final String TAG = "MainActivity";
    private static final String UNSPLASH_ACCESS_KEY = "yYO-wKct28pqLYPA3G49rt4UKxPCogLhskDFVEXd9iY"; // Replace with your Unsplash Access Key
    private List<Food> foodData;
    private RecommendationFragment recommendationFragment;
    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize ExecutorService
        executorService = Executors.newSingleThreadExecutor();

        // Load food data from JSON file
        foodData = loadFoodData();

        if (savedInstanceState == null) {
            InputFragment inputFragment = new InputFragment(this);
            recommendationFragment = new RecommendationFragment();

            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.fragment_container_input, inputFragment);
            fragmentTransaction.add(R.id.fragment_container_recommendation, recommendationFragment);
            fragmentTransaction.commit();
        }
    }

    private List<Food> loadFoodData() {
        List<Food> foodList = new ArrayList<>();
        try {
            InputStream is = getAssets().open("food_data.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String json = new String(buffer, "UTF-8");

            // Replace "NULL" values with "0"
            json = json.replace("\"NULL\"", "0");

            Gson gson = new Gson();
            FoodData foodData = gson.fromJson(json, FoodData.class);
            foodList = foodData.getFoods();
        } catch (IOException e) {
            Log.e(TAG, "Error loading food data: " + e.getMessage(), e);
            Toast.makeText(this, "Error loading food data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } catch (JsonSyntaxException e) {
            Log.e(TAG, "JSON syntax error: " + e.getMessage(), e);
            Toast.makeText(this, "JSON syntax error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "Unexpected error: " + e.getMessage(), e);
            Toast.makeText(this, "Unexpected error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return foodList;
    }

    @Override
    public void onPreferencesSubmitted(double calories, String fatOption, String proteinOption, String sugarsOption) {
        executorService.submit(new GeneticAlgorithmTask(calories, fatOption, proteinOption, sugarsOption));
    }

    private class GeneticAlgorithmTask implements Runnable {

        private double targetCalories;
        private String fatOption;
        private String proteinOption;
        private String sugarsOption;

        public GeneticAlgorithmTask(double targetCalories, String fatOption, String proteinOption, String sugarsOption) {
            this.targetCalories = targetCalories;
            this.fatOption = fatOption;
            this.proteinOption = proteinOption;
            this.sugarsOption = sugarsOption;
        }

        @Override
        public void run() {
            try {
                GeneticAlgorithm ga = new GeneticAlgorithm(foodData, targetCalories);
                List<Food> bestIndividual = ga.run();

                // Apply additional filters based on fat, protein, and sugars
                List<Food> filteredIndividual = filterFoods(bestIndividual, fatOption, proteinOption, sugarsOption);

                // Fetch images for each food
                for (Food food : filteredIndividual) {
                    food.setImageUrl(searchFoodImage(food.getName()));
                }

                // Update UI on the main thread
                runOnUiThread(() -> {
                    if (filteredIndividual != null) {
                        recommendationFragment.updateRecommendations(filteredIndividual);
                        Log.d(TAG, "Number of recommendations: " + filteredIndividual.size());
                    } else {
                        Toast.makeText(MainActivity.this, "Error in generating recommendations", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                Log.e(TAG, "Error in GeneticAlgorithmTask: " + e.getMessage(), e);
            }
        }

        private List<Food> filterFoods(List<Food> foods, String fatOption, String proteinOption, String sugarsOption) {
            List<Food> filteredFoods = new ArrayList<>();

            for (Food food : foods) {
                if (matchesOption(fatOption, food.getFat()) &&
                        matchesOption(proteinOption, food.getProtein()) &&
                        matchesOption(sugarsOption, food.getSugars())) {
                    filteredFoods.add(food);
                }
            }

            return filteredFoods;
        }

        private boolean matchesOption(String option, double value) {
            switch (option) {
                case "≤ 20":
                    return value <= 20;
                case "20-50":
                    return value > 20 && value <= 50;
                case "> 50":
                    return value > 50;
                default:
                    return true;
            }
        }

        private String searchFoodImage(String query) {
            try {
                UnsplashApiService apiService = UnsplashApiClient.getClient().create(UnsplashApiService.class);
                String searchQuery = query + " food";
                Call<UnsplashResponse> call = apiService.searchPhotos(searchQuery, UNSPLASH_ACCESS_KEY, 1, null);

                Response<UnsplashResponse> response = call.execute();
                if (response.isSuccessful() && response.body() != null) {
                    List<UnsplashResponse.Photo> photos = response.body().getResults();
                    if (!photos.isEmpty()) {
                        return photos.get(0).getUrls().getRegular();
                    }
                }
            } catch (IOException e) {
                Log.e(TAG, "Error in searchFoodImage: " + e.getMessage(), e);
            }
            return null;
        }
    }
}
