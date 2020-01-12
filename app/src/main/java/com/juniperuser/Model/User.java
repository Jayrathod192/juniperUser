package com.juniperuser.Model;

public class User {

    private String rates,name, phone,imagUrl,type;


    public User() {
    }

    public User(String rates, String name, String phone, String imagUrl) {
        this.rates = rates;
        this.name = name;
        this.phone = phone;
        this.imagUrl = imagUrl;
    }

    public String getRates() {
        return rates;
    }

    public void setRates(String rates) {
        this.rates = rates;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getImagUrl() {
        return imagUrl;
    }

    public void setImagUrl(String imagUrl) {
        this.imagUrl = imagUrl;
    }
}
