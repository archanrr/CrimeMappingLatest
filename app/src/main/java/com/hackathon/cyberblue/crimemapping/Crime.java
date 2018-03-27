package com.hackathon.cyberblue.crimemapping;

/**
 * Created by ARCHAN on 27-03-2018.
 */

public class Crime {
    public String name,description,state,crimeType,locat;
    public Double lat,lon;
    public String loca;
    Crime(String s1,String s2,String loca1,String description,String name)
    {
        this.state=s1;
        this.crimeType=s2;
        this.description=description;
        this.name=name;
        this.loca=loca1;
    }
    Crime(Double lat,Double lon)
    {
        this.lat=lat;
        this.lon=lon;
    }
}
