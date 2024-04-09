package com.PESTControl.States;

import java.lang.annotation.Target;

import com.PESTControl.StateMachine.StateMachine;
import com.revrobotics.CANSparkMax;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.SimpleMotorFeedforward;

/**An implementation of the default State class meant for implementing states controlled with a CANSparkMax motor controller 
 * Has optional Position and Velocity controller implementation
*/
public class SparkMaxState extends State {
    private CANSparkMax controller;
    private PIDController positionController = null;
    private double currentOutput = 0;
    private SimpleMotorFeedforward motorFeedforward = null;
    private PIDController velocityController;


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
    SparkMaxState(String name, double target, StateMachine boundMachine, CANSparkMax controller) {
        super(name, target, boundMachine, () -> {return controller.getAbsoluteEncoder().getPosition();});
        this.controller = controller;
    }
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
    SparkMaxState(String name, double target, StateMachine boundMachine, CANSparkMax controller, PIDController positionController) {
        super(name, target, boundMachine, () -> {return controller.getAbsoluteEncoder().getPosition();});
        this.controller = controller;
        this.positionController = positionController;
        positionController.setSetpoint(target);
    }
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
    SparkMaxState(String name, double target, StateMachine boundMachine, CANSparkMax controller, SimpleMotorFeedforward motorFeedforward, PIDController velocityController) {
        super(name, target, boundMachine, () -> {return controller.getAbsoluteEncoder().getVelocity();});
        this.controller = controller;
        this.motorFeedforward = motorFeedforward;
        this.velocityController = velocityController;
        velocityController.setSetpoint(target);
    }


    @Override
    public void move() {
        if(positionController == null && velocityController == null){
          controller.set(target);
        }else if(velocityController == null){
            currentOutput = positionController.calculate(currentValueGetter.getAsDouble(), target);
        }else{
            currentOutput = motorFeedforward.calculate(target)+velocityController.calculate(currentValueGetter.getAsDouble());
        }
        controller.set(currentOutput);
    }
    
}
