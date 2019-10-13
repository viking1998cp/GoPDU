package pdu.admin516100.gopdu.Object;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Driver implements Serializable {
    @SerializedName("name")
    @Expose
    private String Name;
    @SerializedName("phone")
    @Expose
    private String Phone;
    @SerializedName("birthdate")
    @Expose
    private String Birthdate;
    @SerializedName("avatar")
    @Expose
    private String Avatar;
    @SerializedName("service")
    @Expose
    private int Service;
    @SerializedName("gender")
    @Expose
    private String Gender;

    public Driver(String name, String phone, String birthdate, String avatar, int service, String gender) {
        Name = name;
        Phone = phone;
        Birthdate = birthdate;
        Avatar = avatar;
        Service = service;
        Gender = gender;
    }

    public Driver() {
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getBirthdate() {
        return Birthdate;
    }

    public void setBirthdate(String birthdate) {
        Birthdate = birthdate;
    }

    public String getAvatar() {
        return Avatar;
    }

    public void setAvatar(String avatar) {
        Avatar = avatar;
    }

    public int getService() {
        return Service;
    }

    public void setService(int service) {
        Service = service;
    }

    public String getGender() {
        return Gender;
    }

    public void setGender(String gender) {
        Gender = gender;
    }
}
