package pdu.admin516100.gopdu.Object;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Customer implements Serializable {

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
    @SerializedName("gender")
    @Expose
    private String Gender;

    public Customer(String name, String phone, String birthdate, String avatar,  String gender) {
        Name = name;
        Phone = phone;
        Birthdate = birthdate;
        Avatar = avatar;
        Gender = gender;
    }

    public Customer() {
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



    public String getGender() {
        return Gender;
    }

    public void setGender(String gender) {
        Gender = gender;
    }

}
