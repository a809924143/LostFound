package com.example.lastfighter.lostf;

public class ItemInfo {
    private String Briefdescription;
    private String User_name;
    private String Image_Url;
    private String infoid;
    public ItemInfo(String briefdescription, String User_name, String Image_Url,String infoid){
        this.Briefdescription = briefdescription;
        this.User_name = User_name;
        this.Image_Url = Image_Url;
        this.infoid = infoid;
    }

    public String getBriefdescription(){
        return Briefdescription;
    }
    public String get_Username(){
        return User_name;
    }
    public String getImage_Url(){
        return Image_Url;
    }
    public String getInfoid(){
        return infoid;
    }
}
