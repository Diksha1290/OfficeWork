package com.officework.testing_profiles.Model;

public class DeclarationScreenModel {
    int img;
    String name;
    String Description;
    int CheckImage;
    private boolean isSelected = false;
    
   
    
    public int getImg() {
        return img;
    }
    
    public void setImg(int img) {
        this.img = img;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return Description;
    }
    
    public void setDescription(String description) {
        Description = description;
    }
    
    public int getCheckImage() {
        return CheckImage;
    }
    
    public void setCheckImage(int checkImage) {
        CheckImage = checkImage;
    }
    
    public boolean isSelected() {
        return isSelected;
    }
    
    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
