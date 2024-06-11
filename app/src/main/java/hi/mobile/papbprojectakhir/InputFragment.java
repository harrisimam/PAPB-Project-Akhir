package hi.mobile.papbprojectakhir;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class InputFragment extends Fragment {

    private EditText editTextCalories;
    private Spinner spinnerFat, spinnerProtein, spinnerSugars;
    private OnPreferencesSubmittedListener callback;

    public interface OnPreferencesSubmittedListener {
        void onPreferencesSubmitted(double calories, String fatOption, String proteinOption, String sugarsOption);
    }

    public InputFragment(OnPreferencesSubmittedListener callback) {
        this.callback = callback;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_input, container, false);

        editTextCalories = view.findViewById(R.id.editTextCalories);
        spinnerFat = view.findViewById(R.id.spinnerFat);
        spinnerProtein = view.findViewById(R.id.spinnerProtein);
        spinnerSugars = view.findViewById(R.id.spinnerSugars);
        Button buttonRecommend = view.findViewById(R.id.buttonRecommend);

        // Set up adapters for the spinners
        ArrayAdapter<CharSequence> fatAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.fat_options, android.R.layout.simple_spinner_item);
        fatAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFat.setAdapter(fatAdapter);

        ArrayAdapter<CharSequence> proteinAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.protein_options, android.R.layout.simple_spinner_item);
        proteinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerProtein.setAdapter(proteinAdapter);

        ArrayAdapter<CharSequence> sugarsAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.sugars_options, android.R.layout.simple_spinner_item);
        sugarsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSugars.setAdapter(sugarsAdapter);

        buttonRecommend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String caloriesInput = editTextCalories.getText().toString();
                if (caloriesInput.isEmpty()) {
                    Toast.makeText(getActivity(), "Please enter the target calories", Toast.LENGTH_SHORT).show();
                    return;
                }

                double targetCalories;
                try {
                    targetCalories = Double.parseDouble(caloriesInput);
                } catch (NumberFormatException e) {
                    Toast.makeText(getActivity(), "Invalid input. Please enter a number.", Toast.LENGTH_SHORT).show();
                    return;
                }

                String fatOption = spinnerFat.getSelectedItem().toString();
                String proteinOption = spinnerProtein.getSelectedItem().toString();
                String sugarsOption = spinnerSugars.getSelectedItem().toString();

                if (fatOption.equals("Select Fat") || proteinOption.equals("Select Protein") || sugarsOption.equals("Select Sugars")) {
                    Toast.makeText(getActivity(), "Please select valid options for fat, protein, and sugars", Toast.LENGTH_SHORT).show();
                    return;
                }

                callback.onPreferencesSubmitted(targetCalories, fatOption, proteinOption, sugarsOption);
            }
        });

        return view;
    }
}
