package com.danscal.cluster2;

class FavoriteLocation {
    public String name;
    public String type;
    public String address;
    public double lat;
    public double lng;


    public FavoriteLocation(String name, String type, String address, double lat, double lng){
        this.name = name;
        this.type = type;
        this.address = address;
        this.lat = lat;
        this.lng = lng;
    }
}
