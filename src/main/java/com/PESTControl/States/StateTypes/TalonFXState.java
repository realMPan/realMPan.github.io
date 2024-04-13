package com.PESTControl.States.StateTypes;

import java.lang.annotation.Target;

import com.PESTControl.StateMachine.StateMachine;
import com.PESTControl.States.State;
import com.ctre.phoenix6.hardware.TalonFX;
import com.revrobotics.CANSparkMax;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import edu.wpi.first.wpilibj.motorcontrol.Talon;


/**An extension of the abstract State class meant for implementing states controlled with a CANSparkMax motor controller <p>
 * Has constructor overloads for FeedForward and PIDController usage for position/velocity-based control.
*/
public class TalonFXState extends State {
    private TalonFX controller;
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
     * @param boundMachine
     * The StateMachine that this state is bound to. The Binded StateMachine is the only thing that will automatically call move()
     * @param controller
     * The CANSparkMax motor controller this State will manipulate for movement.
     * 
     */
    TalonFXState(String name, double target, StateMachine boundMachine, TalonFX controller) {
        super(name, target, boundMachine, () -> {return controller.getPosition().getValueAsDouble();});
        this.controller = controller;
        
    }
    /**
     * Creates a SparkMaxState for positional targeting
     * @param name
     * The name of your state, primarily used for debugging
     * @param target
     * The value that your state will target when activated. Must be a double
     * @param boundMachine
     * The StateMachine that this state is bound to. The Binded StateMachine is the only thing that will automatically call move()
     * @param controller
     * The CANSparkMax motor controller this State will manipulate for movement.
     * @param positionController
     * A PIDController for getting the CANSparkMax to proper position
     * 
     */
    TalonFXState(String name, double target, StateMachine boundMachine, TalonFX controller, PIDController positionController) {
        super(name, target, boundMachine, () -> {return controller.getPosition().getValueAsDouble();});
        controlFunction = () -> {
            controller.set(
                positionController.calculate(controller.getPosition().getValueAsDouble(), target));
        };
        overrideControlFunction(controlFunction);
    }
    /**
     * Creates a SparkMaxState for velocity targeting
     * @param name
     * The name of your state, primarily used for debugging
     * @param target
     * The value that your state will target when activated. Must be a double
     * @param boundMachine
     * The StateMachine that this state is bound to. The Binded StateMachine is the only thing that will automatically call move()
     * @param controller
     * The CANSparkMax motor controller this State will manipulate for movement.
     * @param motorFeedforward
     * A feedforward for reliable controlling the velocity of your motor
     * @param velocityController
     * A PIDController for error correction in the motors velocity
     * 
     */
    TalonFXState(String name, double target, StateMachine boundMachine, TalonFX controller, SimpleMotorFeedforward motorFeedforward, PIDController velocityController) {
        super(name, target, boundMachine, () -> {return controller.getPosition().getValueAsDouble();});
        controlFunction = () -> {
            controller.set(
                motorFeedforward.calculate(target) + velocityController.calculate(controller.getVelocity().getValueAsDouble(), target));
        };
        overrideControlFunction(controlFunction);
    }
    
}
