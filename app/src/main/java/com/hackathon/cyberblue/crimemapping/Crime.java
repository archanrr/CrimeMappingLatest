package com.hackathon.cyberblue.crimemapping;

/**
 * Created by ARCHAN on 27-03-2018.
 */

public class Crime {
    public String name,about,state,crime_type,date,email,phone,reliable;

    public location location;
    Crime()
    {
    }
    Crime(String s1,String s2,String s3,location loca1,String description,String name,String reliable)
    {
        this.state=s1;
        this.crime_type=s2;
        this.date=s3;
        this.about=description;
        this.name=name;
        this.location=loca1;
        this.reliable=reliable;
    }
    Crime(String s1,String s2,String s3,String s4,String s5,location location)
    {
        this.state=s1;
        this.name=s2;
        this.about=s3;
        this.email=s4;
        this.phone=s5;
        this.location=location;
    }

}
