package com.example.pedemo;
// ApiClient.java
import android.util.Log;

import com.example.pedemo.model.Major;
import com.example.pedemo.model.Student;

import okhttp3.*;
import java.io.IOException;

public class ApiClient {
    private static final String TAG = "ApiClient";
    private static final String BASE_URL = "https://66fbd2ec8583ac93b40d5d59.mockapi.io/";
    private OkHttpClient client;

    public ApiClient() {
        client = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request request = chain.request();
                    Log.d(TAG, "Making request to: " + request.url());
                    Response response = chain.proceed(request);
                    Log.d(TAG, "Got response from: " + request.url() + " with code " + response.code());
                    return response;
                })
                .build();
    }

    // Fetch students from API
    public void fetchStudents(Callback callback) {
        Request request = new Request.Builder()
                .url(BASE_URL + "student")
                .build();

        Log.d(TAG, "Fetching students from: " + BASE_URL + "student");

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Failed to fetch students: " + e.getMessage(), e);
                callback.onFailure(call, e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body().string();
                Log.d(TAG, "Students response: " + responseBody);
                callback.onResponse(call, response.newBuilder().body(ResponseBody.create(response.body().contentType(), responseBody)).build());
            }
        });
    }

    // Fetch majors from API
    public void fetchMajors(Callback callback) {
        Request request = new Request.Builder()
                .url(BASE_URL + "major")
                .build();

        client.newCall(request).enqueue(callback);
    }

    // Add a new Major
    public void addMajor(String nameMajor, Callback callback) {
        RequestBody body = new FormBody.Builder()
                .add("nameMajor", nameMajor)
                .build();

        Request request = new Request.Builder()
                .url(BASE_URL + "major")
                .post(body)
                .build();

        client.newCall(request).enqueue(callback);
    }

    // Add a new Student
    public void addStudent(Student student, Callback callback) {
        RequestBody body = new FormBody.Builder()
                .add("name", student.getName())
                .add("date", student.getDate())
                .add("gender", student.getGender())
                .add("email", student.getEmail())
                .add("address", student.getAddress())
                .add("majorId", student.getMajorId())
                .build();

        Request request = new Request.Builder()
                .url(BASE_URL + "student")
                .post(body)
                .build();

        client.newCall(request).enqueue(callback);
    }

    public void updateStudent(Student student, Callback callback) {
        RequestBody body = new FormBody.Builder()
                .add("name", student.getName())
                .add("date", student.getDate())
                .add("gender", student.getGender())
                .add("email", student.getEmail())
                .add("address", student.getAddress())
                .add("majorId", student.getMajorId())
                .build();

        String url = BASE_URL + "student/" + student.getID();
        Request request = new Request.Builder()
                .url(url)
                .put(body)
                .build();

        Log.d(TAG, "Updating student with ID " + student.getID() + " at: " + url);

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Failed to update student: " + e.getMessage(), e);
                callback.onFailure(call, e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body().string();
                Log.d(TAG, "Update student response: " + responseBody);
                callback.onResponse(call, response.newBuilder()
                        .body(ResponseBody.create(response.body().contentType(), responseBody))
                        .build());
            }
        });
    }

    public void updateMajor(Major major, Callback callback) {
        RequestBody body = new FormBody.Builder()
                .add("nameMajor", major.getNameMajor())
                .build();

        String url = BASE_URL + "major/" + major.getIDMajor();
        Request request = new Request.Builder()
                .url(url)
                .put(body)
                .build();

        Log.d(TAG, "Updating major with ID " + major.getIDMajor() + " at: " + url);

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Failed to update major: " + e.getMessage(), e);
                callback.onFailure(call, e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body().string();
                Log.d(TAG, "Update major response: " + responseBody);
                callback.onResponse(call, response.newBuilder()
                        .body(ResponseBody.create(response.body().contentType(), responseBody))
                        .build());
            }
        });
    }

    public void deleteStudent(String majorId, String studentId) {
        String url = BASE_URL + "major/" + majorId + "/student/" + studentId;
        Request request = new Request.Builder()
                .url(url)
                .delete()
                .build();

        Log.d(TAG, "Deleting student with ID " + studentId + " from major " + majorId + " at: " + url);

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Failed to delete student: " + e.getMessage(), e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body().string();
                Log.d(TAG, "Delete student response: " + responseBody);
            }
        });
    }

    // Delete Major (không thay đổi)
    public void deleteMajor(String majorId) {
        String url = BASE_URL + "major/" + majorId;
        Request request = new Request.Builder()
                .url(url)
                .delete()
                .build();

        Log.d(TAG, "Deleting major with ID " + majorId + " at: " + url);

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Failed to delete major: " + e.getMessage(), e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body().string();
                Log.d(TAG, "Delete major response: " + responseBody);
            }
        });
    }

}
