package com.example.fall_detection_3;

public class EmergencyContact {
    private String Ename;
    private String Eemail;
    private String Econtact;

    public EmergencyContact(String ename, String eemail, String econtact) {
        Ename = ename;
        Eemail = eemail;
        Econtact = econtact;
    }
    public EmergencyContact()
    {

    }

    public String getEname() {
        return Ename;
    }

    public String getEemail() {
        return Eemail;
    }

    public String getEcontact() {
        return Econtact;
    }

    public void setEname(String ename) {
        Ename = ename;
    }

    public void setEemail(String eemail) {
        Eemail = eemail;
    }

    public void setEcontact(String econtact) {
        Econtact = econtact;
    }
}

