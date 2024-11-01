package com.example.pedemo;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.pedemo.adapter.StudentAdapter;
import com.example.pedemo.databinding.ActivityMainBinding;
import com.example.pedemo.fragment.MajorFragment;
import com.example.pedemo.fragment.MapFragment;
import com.example.pedemo.fragment.StudentFragment;
import com.example.pedemo.model.Student;
import com.example.pedemo.viewmodel.StudentViewModel;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    Button loginBtn;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        // Set up the adapter
        viewPager.setAdapter(new ViewPagerAdapter(this));

        // Attach TabLayout with ViewPager2
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Students");
                    break;
                case 1:
                    tab.setText("Majors");
                    break;

            }
        }).attach();
    }

    // ViewPagerAdapter to switch between fragments
    private static class ViewPagerAdapter extends FragmentStateAdapter {
        private final FragmentActivity activity;

        public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
            this.activity = fragmentActivity;
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0:
                    StudentFragment studentFragment = new StudentFragment();
                    Bundle args = new Bundle();
                    args.putString("USERNAME", activity.getIntent().getStringExtra("USERNAME"));
                    args.putString("EMAIL", activity.getIntent().getStringExtra("EMAIL"));
                    studentFragment.setArguments(args);
                    return studentFragment;
                case 1:
                    return new MajorFragment();

                default:
                    return new StudentFragment();
            }
        }

        @Override
        public int getItemCount() {
            return 3; // Updated to include map tab
        }
    }

}