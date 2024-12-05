package sv.edu.udb.desafio2_ped.model;

public class Edge {
    private String source;
    private String destination;
    private double distance;

    public Edge(String source, String destination, double distance) {
        this.source = source;
        this.destination = destination;
        this.distance = distance;
    }

    public String getSource() {
        return source;
    }

    public String getDestination() {
        return destination;
    }

    public double getDistance() {
        return distance;
    }



}
