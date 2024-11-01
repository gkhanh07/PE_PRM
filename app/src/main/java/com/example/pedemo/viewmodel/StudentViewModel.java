package com.example.pedemo.viewmodel;

// StudentViewModel.java
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.pedemo.ApiClient;
import com.example.pedemo.model.Major;
import com.example.pedemo.model.Student;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

public class StudentViewModel extends ViewModel {
    private MutableLiveData<List<Student>> studentsLiveData;
    private MutableLiveData<List<Major>> majorsLiveData;
    private List<Major> majorsList;
    private ApiClient apiClient;

    public StudentViewModel() {
        apiClient = new ApiClient();
        studentsLiveData = new MutableLiveData<>();
        majorsLiveData = new MutableLiveData<>();
    }


    // Fetch majors and update LiveData
    public void fetchMajors() {
        apiClient.fetchMajors(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Handle error
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    List<Major> majors = parseMajors(response.body().string());
                    majorsList = majors;
                    majorsLiveData.postValue(majors);
                    List<Student> currentStudents = studentsLiveData.getValue();
                    if (currentStudents != null) {
                        updateStudentsWithMajors(currentStudents);
                    }
                }
            }
        });
    }

    // Fetch students and update LiveData
    public void fetchStudents() {
        this.fetchMajors();
        apiClient.fetchStudents(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    List<Student> students = parseStudents(response.body().string());
                    updateStudentsWithMajors(students);
                }
            }
        });
    }


    private void updateStudentsWithMajors(List<Student> students) {
        if (majorsList != null) {
            for (Student student : students) {
                for (Major major : majorsList) {
                    if (student.getMajorId().equals(major.getIDMajor())) {
                        student.setMajor(major);
                        break;
                    }
                }
            }
        }
        studentsLiveData.postValue(students);
    }

    public void addMajor(String majorName) {
        apiClient.addMajor(majorName, new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Handle error
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    // Refresh majors list
                    fetchMajors();
                }
            }
        });
    }

    public void addStudent(Student student) {
        apiClient.addStudent(student, new okhttp3.Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    fetchStudents(); // Refresh list after adding
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
        });
    }


    public void updateStudent(Student student) {
        apiClient.updateStudent(student, new okhttp3.Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    fetchStudents(); // Refresh list after update
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void updateMajor(Major major) {
        apiClient.updateMajor(major, new okhttp3.Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    fetchMajors(); // Refresh list after update
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void deleteStudent(String majorId, String studentId) {
        apiClient.deleteStudent(majorId, studentId);
        fetchStudents(); // Refresh list after delete
    }

    public boolean isMajorInUse(String majorId) {
        List<Student> students = studentsLiveData.getValue();
        if (students != null) {
            for (Student student : students) {
                if (student.getMajorId().equals(majorId)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void deleteMajor(String majorId) {
        if (isMajorInUse(majorId)) {
            // Không thực hiện xóa nếu major đang được sử dụng
            return;
        }
        apiClient.deleteMajor(majorId);
        fetchMajors();
    }


    public MutableLiveData<List<Student>> getStudentsLiveData() {
        return studentsLiveData;
    }

    public MutableLiveData<List<Major>> getMajorsLiveData() {
        return majorsLiveData;
    }

    // Implement methods to parse JSON data
    private List<Student> parseStudents(String json) {
        Gson gson = new Gson();

        // Tạo kiểu dữ liệu cho List<Student> bằng TypeToken
        Type studentListType = new TypeToken<List<Student>>() {}.getType();

        // Parse JSON thành List<Student>
        List<Student> studentList = gson.fromJson(json, studentListType);

        return studentList;
    }

    private List<Major> parseMajors(String json) {
        Gson gson = new Gson();

        // Tạo kiểu dữ liệu cho List<Major> bằng TypeToken
        Type majorListType = new TypeToken<List<Major>>() {}.getType();

        // Parse JSON thành List<Major>
        List<Major> majorList = gson.fromJson(json, majorListType);

        return majorList;
    }
}
