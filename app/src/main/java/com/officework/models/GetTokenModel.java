package com.officework.models;

public class GetTokenModel {

    String UserName,Password,grant_type;

   public GetTokenModel(String userName,String password,String grant_type){
        this.UserName=userName;
        this.Password=password;
        this.grant_type=grant_type;
    }
}
