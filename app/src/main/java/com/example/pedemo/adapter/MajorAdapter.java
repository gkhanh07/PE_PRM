package com.example.pedemo.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pedemo.databinding.ItemMajorBinding;
import com.example.pedemo.databinding.ItemStudentBinding;
import com.example.pedemo.model.Major;
import com.example.pedemo.model.Student;

import java.util.ArrayList;
import java.util.List;

public class MajorAdapter extends RecyclerView.Adapter<MajorAdapter.MajorViewHolder> {

    private List<Major> majorList = new ArrayList<>();
    private OnMajorClickListener listener;
    private OnMajorLongClickListener longClickListener;

    public interface OnMajorClickListener {
        void onMajorClick(Major major);
    }

    public void setOnMajorClickListener(OnMajorClickListener listener) {
        this.listener = listener;
    }

    public interface OnMajorLongClickListener {
        boolean onMajorLongClick(View view, Major major);
    }

    public void setOnMajorLongClickListener(OnMajorLongClickListener listener) {
        this.longClickListener = listener;
    }


    public MajorAdapter(List<Major> majorList) {
        if (majorList != null) {
            this.majorList = majorList;
        }
    }

    public void updateMajors(List<Major> Majors) {
        this.majorList = Majors != null ? Majors : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MajorAdapter.MajorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemMajorBinding binding = ItemMajorBinding.inflate(inflater, parent, false);
        return new MajorAdapter.MajorViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MajorAdapter.MajorViewHolder holder, int position) {
        Major major = majorList.get(position);
        holder.bind(major);
    }

    @Override
    public int getItemCount() {
        return majorList.size();
    }

    public class MajorViewHolder extends RecyclerView.ViewHolder {
        private final ItemMajorBinding binding;

        public MajorViewHolder(ItemMajorBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onMajorClick(majorList.get(position));
                }
            });

            itemView.setOnLongClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && longClickListener != null) {
                    return longClickListener.onMajorLongClick(v, majorList.get(position));
                }
                return false;
            });
        }

        public void bind(Major major) {
            binding.MajorName.setText(major.getNameMajor());

        }
    }
}
