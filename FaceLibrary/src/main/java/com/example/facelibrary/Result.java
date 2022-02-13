package com.example.facelibrary;

public class Result {
    public String error;
    public Double score;
    public Boolean isTrue;

    @Override
    public String toString() {
        return "Similarity score: "+ score + "\n" + "Result: "+ isTrue + "\n" + "Error: "+ error;
    }

}
