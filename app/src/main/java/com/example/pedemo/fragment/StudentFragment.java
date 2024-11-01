package com.example.pedemo.fragment;

import android.app.Activity;
import android.content.Intent;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager2.widget.ViewPager2;

import com.example.pedemo.LoginActivity;
import com.example.pedemo.MainActivity;
import com.example.pedemo.MapActivity;
import com.example.pedemo.R;
import com.example.pedemo.adapter.StudentAdapter;
import com.example.pedemo.databinding.DialogAddStudentBinding;
import com.example.pedemo.databinding.FragmentStudentBinding;
import com.example.pedemo.model.Major;
import com.example.pedemo.model.Student;
import com.example.pedemo.viewmodel.StudentViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class StudentFragment extends Fragment {
    private Button loginBtn;
    private Button logoutBtn;
    private TextView usernameText;
    private boolean isLoggedIn = false;
    private FragmentStudentBinding binding;
    private StudentViewModel studentViewModel;
    private StudentAdapter studentAdapter;
    private List<Major> majorList;
    private TextView emailText;

    private ActivityResultLauncher<Intent> loginActivityResultLauncher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize the launcher
        loginActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        String username = result.getData().getStringExtra("USERNAME");
                        if (username != null && !username.isEmpty()) {
                            updateUIForLoggedInUser(username);
                        }
                    }
                });
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Initialize data binding
        View view = inflater.inflate(R.layout.fragment_student, container, false);

        usernameText = view.findViewById(R.id.user_name_text);

        Bundle args = getArguments();
        if (args != null) {
            String username = args.getString("username");
            String email = args.getString("email");

            // Set the values to the TextViews
            usernameText.setText("Logged in as: " + username);
            if (emailText != null) {
                emailText.setText("Email: " + email);
            }
        }

        binding = FragmentStudentBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Initialize RecyclerView
        binding.recyclerViewStudents.setLayoutManager(new LinearLayoutManager(getContext()));
        studentAdapter = new StudentAdapter(new ArrayList<>());
        binding.recyclerViewStudents.setAdapter(studentAdapter);

        // Add map click listener
        studentAdapter.setOnStudentMapClickListener(student -> {
            if (student.getAddress() != null && !student.getAddress().isEmpty()) {
                Intent intent = new Intent(getActivity(), MapActivity.class);
                intent.putExtra("ADDRESS", student.getAddress());
                intent.putExtra("STUDENT_NAME", student.getName());
                startActivity(intent);
            } else {
                Toast.makeText(getContext(), "No address available", Toast.LENGTH_SHORT).show();
            }
        });

        // Initialize ViewModel
        studentViewModel = new ViewModelProvider(this).get(StudentViewModel.class);

        // Observe students data
        studentViewModel.getStudentsLiveData().observe(getViewLifecycleOwner(), students -> {
            studentAdapter.updateStudents(students);
        });

        // Observe majors data for spinner
        studentViewModel.getMajorsLiveData().observe(getViewLifecycleOwner(), majors -> {
            majorList = majors;
        });

        // Setup FAB click listener
        binding.fabAddStudent.setOnClickListener(v -> showAddStudentDialog());

        // Fetch initial data
        studentViewModel.fetchMajors();
        studentViewModel.fetchStudents();

        usernameText = binding.userNameText;

        loginBtn = binding.getRoot().findViewById(R.id.login_btn);
        logoutBtn = binding.getRoot().findViewById(R.id.logout_btn);

        // Setup login/logout buttons
        loginBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            loginActivityResultLauncher.launch(intent);
        });

        logoutBtn.setOnClickListener(v -> showLogoutConfirmDialog());

        // Check login state

        studentAdapter.setOnStudentClickListener(this::showUpdateStudentDialog);
        studentAdapter.setOnStudentLongClickListener(student -> {
            showDeleteConfirmDialog(student);
            return true;
        });

        // Setup map button click
        Button mapBtn = binding.mapBtn;
        mapBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MapActivity.class);
            startActivity(intent);
        });

        return binding.getRoot();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) { // Check the request code
            if (resultCode == getActivity().RESULT_OK) { // Check if login was successful
                isLoggedIn = true;
                loginBtn.setVisibility(View.GONE); // Hide the login button
            }
        }
    }

    private void showAddStudentDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

        // Sử dụng binding cho dialog
        DialogAddStudentBinding dialogBinding = DialogAddStudentBinding.inflate(getLayoutInflater());

        // Setup date picker
        dialogBinding.editTextDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    requireContext(),
                    (view, year, month, dayOfMonth) -> {
                        String date = String.format(Locale.getDefault(), "%02d/%02d/%d", dayOfMonth, month + 1, year);
                        dialogBinding.editTextDate.setText(date);
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });

        // Setup major spinner
        if (majorList != null && !majorList.isEmpty()) {
            // Tạo adapter với layout mặc định cho spinner
            ArrayAdapter<Major> majorAdapter = new ArrayAdapter<>(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    majorList);

            // Set layout cho dropdown menu
            majorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            // Set adapter cho spinner
            dialogBinding.spinnerMajor.setAdapter(majorAdapter);
        } else {
        }

        builder.setView(dialogBinding.getRoot())
                .setTitle("Add Student")
                .setPositiveButton("Add", (dialog, which) -> {
                    if (validateInput(dialogBinding)) {
                        Student student = new Student();
                        student.setName(dialogBinding.editTextName.getText().toString().trim());
                        student.setDate(dialogBinding.editTextDate.getText().toString().trim());
                        student.setGender(dialogBinding.radioMale.isChecked() ? "Male" : "Female");
                        student.setEmail(dialogBinding.editTextEmail.getText().toString().trim());
                        student.setAddress(dialogBinding.editTextAddress.getText().toString().trim());

                        Major selectedMajor = (Major) dialogBinding.spinnerMajor.getSelectedItem();
                        student.setMajorId(selectedMajor.getIDMajor());

                        studentViewModel.addStudent(student);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showUpdateStudentDialog(Student student) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        DialogAddStudentBinding dialogBinding = DialogAddStudentBinding.inflate(getLayoutInflater());

        // Pre-fill existing data
        dialogBinding.editTextName.setText(student.getName());
        dialogBinding.editTextDate.setText(student.getDate());
        dialogBinding.editTextEmail.setText(student.getEmail());
        dialogBinding.editTextAddress.setText(student.getAddress());
        if ("Nam".equals(student.getGender())) {
            dialogBinding.radioMale.setChecked(true);
        } else {
            dialogBinding.radioFemale.setChecked(true);
        }

        // Setup date picker
        dialogBinding.editTextDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    requireContext(),
                    (view, year, month, dayOfMonth) -> {
                        String date = String.format(Locale.getDefault(), "%02d/%02d/%d", dayOfMonth, month + 1, year);
                        dialogBinding.editTextDate.setText(date);
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });

        // Setup major spinner
        if (majorList != null && !majorList.isEmpty()) {
            ArrayAdapter<Major> majorAdapter = new ArrayAdapter<>(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    majorList);
            majorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            dialogBinding.spinnerMajor.setAdapter(majorAdapter);

            // Set selected major
            for (int i = 0; i < majorList.size(); i++) {
                if (majorList.get(i).getIDMajor().equals(student.getMajorId())) {
                    dialogBinding.spinnerMajor.setSelection(i);
                    break;
                }
            }
        }

        builder.setView(dialogBinding.getRoot())
                .setTitle("Update Student")
                .setPositiveButton("Update", (dialog, which) -> {
                    if (validateInput(dialogBinding)) {
                        // Update student object
                        student.setName(dialogBinding.editTextName.getText().toString().trim());
                        student.setDate(dialogBinding.editTextDate.getText().toString().trim());
                        student.setGender(dialogBinding.radioMale.isChecked() ? "Male" : "Female");
                        student.setEmail(dialogBinding.editTextEmail.getText().toString().trim());
                        student.setAddress(dialogBinding.editTextAddress.getText().toString().trim());

                        Major selectedMajor = (Major) dialogBinding.spinnerMajor.getSelectedItem();
                        student.setMajorId(selectedMajor.getIDMajor());

                        // Show loading
                        ProgressDialog progressDialog = new ProgressDialog(requireContext());
                        progressDialog.setMessage("Updating...");
                        progressDialog.show();

                        // Call update
                        studentViewModel.updateStudent(student);

                        // Dismiss loading and show success message
                        new Handler(Looper.getMainLooper()).postDelayed(() -> {
                            progressDialog.dismiss();
                            Toast.makeText(requireContext(), "Updated success", Toast.LENGTH_SHORT).show();
                        }, 1000);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showDeleteConfirmDialog(Student student) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Confirm delete Student ")
                .setMessage("Do you want to delete this student?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    // Show loading
                    ProgressDialog progressDialog = new ProgressDialog(requireContext());
                    progressDialog.setMessage("Deleting...");
                    progressDialog.show();

                    // Delete student
                    studentViewModel.deleteStudent(student.getMajorId(), Integer.toString(student.getID()));

                    // Dismiss loading and show success message
                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        progressDialog.dismiss();
                        Toast.makeText(requireContext(), "Deleted Success", Toast.LENGTH_SHORT).show();
                    }, 1000);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private boolean validateInput(DialogAddStudentBinding binding) {
        boolean isValid = true;

        if (binding.editTextName.getText().toString().trim().isEmpty()) {
            binding.editTextName.setError("Please enter name");
            isValid = false;
        }

        if (binding.editTextDate.getText().toString().trim().isEmpty()) {
            binding.editTextDate.setError("Please enter date of birth");
            isValid = false;
        }

        if (binding.editTextEmail.getText().toString().trim().isEmpty()) {
            binding.editTextEmail.setError("Please enter email");
            isValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.editTextEmail.getText().toString()).matches()) {
            binding.editTextEmail.setError("Email unaviable");
            isValid = false;
        }

        if (binding.editTextAddress.getText().toString().trim().isEmpty()) {
            binding.editTextAddress.setError("Please enter Address");
            isValid = false;
        }

        if (binding.spinnerMajor.getSelectedItem() == null) {
            Toast.makeText(requireContext(), "Select Major", Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        return isValid;
    }

    private void showLogoutConfirmDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    performLogout();
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void performLogout() {
        GoogleSignInClient gsc = GoogleSignIn.getClient(
                requireActivity(),
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build());

        gsc.signOut().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                updateUIForLoggedOutUser();
                Toast.makeText(requireContext(), "Logged out successfully", Toast.LENGTH_SHORT).show();

                // Optionally restart the activity to clear all data
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                getActivity().finish();
            } else {
                Toast.makeText(requireContext(), "Logout failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUIForLoggedInUser(String username) {
        isLoggedIn = true;
        usernameText.setText(username);
        loginBtn.setVisibility(View.GONE);
        logoutBtn.setVisibility(View.VISIBLE);
    }

    private void updateUIForLoggedOutUser() {
        isLoggedIn = false;
        usernameText.setText("User Name");
        loginBtn.setVisibility(View.VISIBLE);
        logoutBtn.setVisibility(View.GONE);
    }

    private void showStudentLocation(Student student) {
        // Switch to map tab and focus on student
        if (getActivity() instanceof MainActivity) {
            ViewPager2 viewPager = getActivity().findViewById(R.id.viewPager);
            viewPager.setCurrentItem(2); // Switch to map tab
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
