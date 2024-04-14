package com.PESTControl.States.StateTypes;

import java.lang.annotation.Target;

import com.PESTControl.StateMachine;
import com.PESTControl.States.State;
import com.revrobotics.CANSparkMax;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.SimpleMotorFeedforward;

/**An extension of the abstract State class meant for implementing states controlled with a CANSparkMax motor controller <p>
 * Has constructor overloads for FeedForward and PIDController usage for position/velocity-based control.
*/
public class SparkMaxState extends State {
    private CANSparkMax controller;
    private PIDController positionController = null;
    private double currentOutput = 0;
    private SimpleMotorFeedforward motorFeedforward = null;
    private PIDController velocityController;
    private Runnable controlFunction;


    /**
     * Creates a SparkMaxState
     * @param name
     * The name of your state, primarily used for debugging
     * @param target
     * The value that your state will target when activated. Must be a double
     * @param controller
     * The CANSparkMax motor controller this State will manipulate for movement.
     * 
     */
    SparkMaxState(String name, double target, CANSparkMax controller) {
        super(name, target, () -> {return controller.getAbsoluteEncoder().getPosition();});
        this.controller = controller;
        controlFunction = () -> controller.set(target);
        overrideControlFunction(controlFunction);
    }
    /**
     * Creates a SparkMaxState for positional targeting
     * @param name
     * The name of your state, primarily used for debugging
     * @param target
     * The value that your state will target when activated. Must be a double
     * @param controller
     * The CANSparkMax motor controller this State will manipulate for movement. Its AbsoluteEncoder will be used for position tracking
     * @param positionController
     * A PIDController for getting the CANSparkMax to proper position.
     * 
     */
    SparkMaxState(String name, double target, StateMachine boundMachine, CANSparkMax controller, PIDController positionController) {
        super(name, target, () -> {return controller.getAbsoluteEncoder().getPosition();});
        controlFunction = () -> {
            controller.set(
                positionController.calculate(controller.getAbsoluteEncoder().getPosition(), 
                target));
        };
        overrideControlFunction(controlFunction);
    }
    /**
     * Creates a SparkMaxState for velocity targeting
     * @param name
     * The name of your state, primarily used for debugging
     * @param target
     * The value that your state will target when activated. Must be a double
     * @param controller
     * The CANSparkMax motor controller this State will manipulate for movement. Its AbsoluteEncoder will be used for velocity tracking
     * @param motorFeedforward
     * A feedforward for reliable controlling the velocity of your motor. Note that the Feedforward will always assume zero acceleration
     * @param velocityController
     * A PIDController for error correction in the motors velocity
     * 
     */
    SparkMaxState(String name, double target, CANSparkMax controller, SimpleMotorFeedforward motorFeedforward, PIDController velocityController) {
        super(name, target, () -> {return controller.getAbsoluteEncoder().getVelocity();});
        controlFunction = () -> {
            controller.set(
                motorFeedforward.calculate(controller.getAbsoluteEncoder().getVelocity() + velocityController.calculate(controller.getAbsoluteEncoder().getVelocity(), 
                target))
            );
        };
        overrideControlFunction(controlFunction);  
    }
    
}
