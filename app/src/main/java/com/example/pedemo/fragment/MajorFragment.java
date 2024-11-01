package com.example.pedemo.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.pedemo.adapter.MajorAdapter;
import com.example.pedemo.adapter.StudentAdapter;
import com.example.pedemo.databinding.DialogAddMajorBinding;
import com.example.pedemo.databinding.FragmentMajorBinding;
import com.example.pedemo.databinding.FragmentStudentBinding;
import com.example.pedemo.model.Major;
import com.example.pedemo.model.Student;
import com.example.pedemo.viewmodel.StudentViewModel;

import java.util.ArrayList;
import java.util.List;

public class MajorFragment  extends Fragment {

    private FragmentMajorBinding binding;
    private StudentViewModel studentViewModel;
    private MajorAdapter majorAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Initialize data binding
        binding = FragmentMajorBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Initialize RecyclerView
        binding.recyclerViewMajors.setLayoutManager(new LinearLayoutManager(getContext()));
        majorAdapter = new MajorAdapter(new ArrayList<>());  // Pass an empty list initially
        binding.recyclerViewMajors.setAdapter(majorAdapter);

        // Initialize ViewModel
        studentViewModel = new ViewModelProvider(this).get(StudentViewModel.class);

        // Observe studentsLiveData
        studentViewModel.getMajorsLiveData().observe(getViewLifecycleOwner(), new Observer<List<Major>>() {
            @Override
            public void onChanged(List<Major> majors) {
                // Update adapter's data
                majorAdapter.updateMajors(majors);
            }
        });

        // Fetch data from ViewModel
        studentViewModel.fetchMajors();

        binding.fabAddMajor.setOnClickListener(v -> showAddMajorDialog());
        majorAdapter.setOnMajorClickListener(this::showUpdateMajorDialog);

        majorAdapter.setOnMajorLongClickListener((view, major) -> {
            showDeleteConfirmDialog(major);
            return true;
        });

        return binding.getRoot();
    }

    private void showAddMajorDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

        // Sử dụng binding cho dialog
        DialogAddMajorBinding dialogBinding = DialogAddMajorBinding.inflate(getLayoutInflater());

        builder.setView(dialogBinding.getRoot())
                .setTitle("Add Major")
                .setPositiveButton("Add", (dialog, which) -> {
                    String majorName = dialogBinding.editTextMajorName.getText().toString();
                    if (!majorName.isEmpty()) {
                        studentViewModel.addMajor(majorName);
                    }
                })
                .setNegativeButton("Cancle", null)
                .show();
    }

    private void showUpdateMajorDialog(Major major) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        DialogAddMajorBinding dialogBinding = DialogAddMajorBinding.inflate(getLayoutInflater());

        // Pre-fill existing data
        dialogBinding.editTextMajorName.setText(major.getNameMajor());

        builder.setView(dialogBinding.getRoot())
                .setTitle("Update Major")
                .setPositiveButton("Update", (dialog, which) -> {
                    String majorName = dialogBinding.editTextMajorName.getText().toString().trim();
                    if (!majorName.isEmpty()) {
                        // Show loading
                        ProgressDialog progressDialog = new ProgressDialog(requireContext());
                        progressDialog.setMessage("Updating...");
                        progressDialog.show();

                        major.setNameMajor(majorName);
                        studentViewModel.updateMajor(major);

                        // Dismiss loading and show success message
                        new Handler(Looper.getMainLooper()).postDelayed(() -> {
                            progressDialog.dismiss();
                            Toast.makeText(requireContext(), "Update success", Toast.LENGTH_SHORT).show();
                        }, 1000);
                    } else {
                        dialogBinding.editTextMajorName.setError("Input Major name");
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showDeleteConfirmDialog(Major major) {
        // Kiểm tra xem major có đang được sử dụng không
        if (studentViewModel.isMajorInUse(major.getIDMajor())) {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Can not delete")
                    .setMessage("This major is in some student!")
                    .setPositiveButton("Cancle", null)
                    .show();
            return;
        }

        // Nếu không được sử dụng, hiển thị dialog xác nhận xóa
        new AlertDialog.Builder(requireContext())
                .setTitle("Confirm delete")
                .setMessage("Do you want to delete this major?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    // Show loading
                    ProgressDialog progressDialog = new ProgressDialog(requireContext());
                    progressDialog.setMessage("Deleting...");
                    progressDialog.show();

                    // Delete major
                    studentViewModel.deleteMajor(major.getIDMajor());

                    // Dismiss loading and show success message
                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        progressDialog.dismiss();
                        Toast.makeText(requireContext(), "Deleted success", Toast.LENGTH_SHORT).show();
                    }, 1000);
                })
                .setNegativeButton("cancel", null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

