package com.jcedar.visinaas.io.model;

/**
 * Created by Afolayan on 13/10/2015.
 */
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Student {

    @Expose
    @SerializedName("name")
    private String name;

    @Expose @SerializedName("gender")
    private String gender;

    @Expose @SerializedName("school")
    private String chapter;

    @Expose @SerializedName("email")
    private String email;

    @Expose @SerializedName("phone_number")
    private String phoneNumber;


    @Expose @SerializedName("course")
    private String course;


    @Expose @SerializedName("date_of_birth")
    private String dateOfBirth;

    @Expose @SerializedName("dobNumber")
    private String dobNumber;

    public String getName() {
        return name;
    }

    public String getGender() {
        return gender;
    }

    public String getChapter() {
        return chapter;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getCourse() {
        return course;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public String getDobNumber() {
        return dobNumber;
    }

    public static Student[] fromJson(String json){
        return new GsonBuilder().excludeFieldsWithoutExposeAnnotation()
                .create().fromJson(json,  Student[].class);
    }


}
