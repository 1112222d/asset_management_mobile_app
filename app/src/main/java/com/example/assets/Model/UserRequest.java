package com.example.assets.Model;

import java.util.Date;

public class UserRequest {
    private String firstName;
    private String lastName;
    private Gender gender;
    private String dateOfBirth;
    private String joinedDate;
    private String type;
    private String email;


    public UserRequest() {
    }

    public UserRequest(String firstName, String lastName, Gender gender, String dateOfBirth, String joinedDate, String type,String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.joinedDate = joinedDate;
        this.type = type;
        this.email=email;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = "ROLE_"+type.toUpperCase();
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getJoinedDate() {
        return joinedDate;
    }

    public void setJoinedDate(String joinedDate) {
        this.joinedDate = joinedDate;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
