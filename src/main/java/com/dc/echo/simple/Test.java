package com.dc.echo.simple;

import java.awt.AWTException;
import java.awt.Robot;

public class Test {
    public static void main(String[] args) throws Exception {
        Robot myRobot = new Robot();
        myRobot.mouseMove(50,50);
    }
}
