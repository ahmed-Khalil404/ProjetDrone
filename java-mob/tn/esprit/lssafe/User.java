package tn.esprit.lssafe;

public class User {

    public String fullName,phone, email,ceinture ,serial,role;

    public User(){
    }
    public User(String fullName, String phone, String email, String ceinture, String serial, String role) {
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
        this.ceinture = ceinture;
        this.serial = serial;
        this.role = role;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCeinture() {
        return ceinture;
    }

    public void setCeinture(String ceinture) {
        this.ceinture = ceinture;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}