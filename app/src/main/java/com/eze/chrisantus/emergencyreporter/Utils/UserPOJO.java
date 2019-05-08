package com.eze.chrisantus.emergencyreporter.Utils;

public class UserPOJO {
    private String name, bloodgroup, email, phone, level, dept, matno, gender, guardian_phone_no, authtype;

    public UserPOJO() {
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBloodgroup(String bloodgroup) {
        this.bloodgroup = bloodgroup;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public void setDept(String dept) {
        this.dept = dept;
    }

    public void setMatno(String matno) {
        this.matno = matno;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setGuardian_phone_no(String guardian_phone_no) {
        this.guardian_phone_no = guardian_phone_no;
    }

    public void setAuthtype(String authtype) {
        this.authtype = authtype;
    }

    public String getName() {
        return name;
    }

    public String getBloodgroup() {
        return bloodgroup;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getLevel() {
        return level;
    }

    public String getDept() {
        return dept;
    }

    public String getMatno() {
        return matno;
    }

    public String getGender() {
        return gender;
    }

    public String getGuardian_phone_no() {
        return guardian_phone_no;
    }

    public String getAuthtype() {
        return authtype;
    }
}
