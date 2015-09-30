package edu.illinois.cs.cogcomp.ilp;


public class SetCoverSolver {
    public static void main(String[] args) {

        containsStationConstrained classifier = new containsStationConstrained();

        for (String file : args) {
            City c = new City(file);
            for (Neighborhood n : c.getNeighborhoods()) {
                System.out.println(n.getNumber() + ": " + classifier.classify(n));
            }
        }
    }
}


