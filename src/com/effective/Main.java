package com.effective;

import factorymethodpattern.RobotCreator;

public class Main {

    public static void main(String[] args) {
	    //Fectory Method Pattern Main
        RobotCreator robotCreator = new RobotCreator();
        robotCreator.creator("Human");
        robotCreator.creator("Animal");
    }
}
