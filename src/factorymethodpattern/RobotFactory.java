package factorymethodpattern;

public class RobotFactory {
    public Robot create(String name) {
        Robot robot = null;
        if(name.equals("Human")) {
            robot = new HumanRobot();
        } else if(name.equals("Animal")) {
            robot = new AnimalRobot();
        }
        return robot;
    }
}
