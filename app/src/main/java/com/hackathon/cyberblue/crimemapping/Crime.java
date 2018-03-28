package com.hackathon.cyberblue.crimemapping;

/**
 * Created by ARCHAN on 27-03-2018.
 */

public class Crime {
    public String name,description,state,crimeType,date;

    public location loca;
    Crime()
    {
    }
    Crime(String s1,String s2,String s3,location loca1,String description,String name)
    {
        this.state=s1;
        this.crimeType=s2;
        this.date=s3;
        this.description=description;
        this.name=name;
        this.loca=loca1;
    }

}
