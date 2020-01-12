package com.juniperuser;

public class ModelData {
    String Nameeng;
    String Typeeng;
    String Qualification;
    int icon;

    public ModelData(String nameeng, String typeeng, String qualification, int icon) {
        Nameeng = nameeng;
        Typeeng = typeeng;
        Qualification = qualification;
        this.icon = icon;
    }

    public String getNameeng() {
        return this.Nameeng;
    }

    public String getTypeeng() {
        return this.Typeeng;
    }

    public String getQualification() {
        return this.Qualification;
    }

    public int getIcon() {
        return this.icon;
    }
}
