package hi.mobile.papbprojectakhir;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class RecommendationFragment extends Fragment {

    private RecyclerView recyclerViewRecommendations;
    private FoodAdapter foodAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recommendation, container, false);

        recyclerViewRecommendations = view.findViewById(R.id.recyclerViewRecommendations);
        recyclerViewRecommendations.setLayoutManager(new LinearLayoutManager(getActivity()));

        foodAdapter = new FoodAdapter(getActivity(), new ArrayList<>());
        recyclerViewRecommendations.setAdapter(foodAdapter);

        return view;
    }

    public void updateRecommendations(List<Food> foodList) {
        foodAdapter.updateFoodList(foodList);
    }
}
