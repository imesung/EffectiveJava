package factorymethodpattern;

public class RobotCreator {
    public Robot creator(String name) {
        RobotFactory robotFactory = new RobotFactory();
        Robot robot = robotFactory.create(name);
        return robot;
    }
}
