package hi.mobile.papbprojectakhir;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Picasso;
import java.util.List;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.FoodViewHolder> {

    private List<Food> foodList;
    private Context context;

    public FoodAdapter(Context context, List<Food> foodList) {
        this.context = context;
        this.foodList = foodList;
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_food, parent, false);
        return new FoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        Food food = foodList.get(position);
        holder.foodName.setText(food.getName());
        holder.calories.setText("Calories: " + food.getCalories());
        holder.fat.setText("Fat: " + food.getFat());
        holder.protein.setText("Protein: " + food.getProtein());
        holder.sugars.setText("Sugars: " + food.getSugars());

        if (food.getImageUrl() != null) {
            Picasso.get().load(food.getImageUrl()).into(holder.imageViewFood);
        }
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    public void updateFoodList(List<Food> newFoodList) {
        foodList.clear();
        foodList.addAll(newFoodList);
        notifyDataSetChanged();
    }

    static class FoodViewHolder extends RecyclerView.ViewHolder {
        TextView foodName, calories, fat, protein, sugars;
        ImageView imageViewFood;

        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            foodName = itemView.findViewById(R.id.textViewFoodName);
            calories = itemView.findViewById(R.id.textViewCalories);
            fat = itemView.findViewById(R.id.textViewFat);
            protein = itemView.findViewById(R.id.textViewProtein);
            sugars = itemView.findViewById(R.id.textViewSugars);
            imageViewFood = itemView.findViewById(R.id.imageViewFood);
        }
    }
}

