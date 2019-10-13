package pdu.admin516100.gopdu.Object;

import com.google.android.gms.maps.model.LatLng;

public class HistoryCustomer {
    private String destination;
    private String pickupName;
    private Double distance;
    private LatLng pickupMarker;
    private LatLng destinationMarker;
    private Double price;
    private Double ratting;
    private Long timestmap;

    public HistoryCustomer() {
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getPickupName() {
        return pickupName;
    }

    public void setPickupName(String pickupName) {
        this.pickupName = pickupName;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public LatLng getPickupMarker() {
        return pickupMarker;
    }

    public void setPickupMarker(LatLng pickupMarker) {
        this.pickupMarker = pickupMarker;
    }

    public LatLng getDestinationMarker() {
        return destinationMarker;
    }

    public void setDestinationMarker(LatLng destinationMarker) {
        this.destinationMarker = destinationMarker;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getRatting() {
        return ratting;
    }

    public void setRatting(Double ratting) {
        this.ratting = ratting;
    }

    public Long getTimestmap() {
        return timestmap;
    }

    public void setTimestmap(Long timestmap) {
        this.timestmap = timestmap;
    }
}
