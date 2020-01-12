package com.juniperuser.Model;

public class Engineer {

    private String name, phone,rate,type,address,distance,imageUrl;


    public Engineer() {
    }

    public Engineer(String name, String phone,String rate,String type,String address,String distance,String imageUrl) {
        this.imageUrl=imageUrl;
        this.name = name;
        this.phone = phone;
        this.rate=rate;
        this.type=type;
        this.address=address;
        this.distance=distance;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getRate(){return rate; }

    public void setRate(String rate){this.rate=rate;}

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


}
