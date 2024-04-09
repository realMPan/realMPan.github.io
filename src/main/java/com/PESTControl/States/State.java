package com.PESTControl.States;

import java.util.function.DoubleSupplier;

import com.PESTControl.StateExceptions;
import com.PESTControl.StateMachine.StateMachine;

import edu.wpi.first.math.controller.PIDController;

public abstract class State {
    protected DoubleSupplier currentValueGetter = null;
    protected double currentValue = Double.NaN;
    protected String name;
    protected double target;
    protected State flowState;
    protected State fromState;

    protected PIDController stateController = null;
    protected StateMachine boundMachine = null;

    
    
  /**
     * Initializes a State Object
     * @param name
     * The name of the state, useful for Dashboard debugging
     * @param target
     * The target of the state. As a majority of encoders report values doubles, this value must be a double
     * @param boundMachine
     * The StateMachine for this state to bind to. The binded StateMachine will be the only object in your project that can properly "activate" the state and trigger movements.
     */
    protected void commonInits(String name, double target, StateMachine boundMachine){
        this.name = name;
        this.target = target;
    }
    
    
    /**
     * Initializes a State Object
     * @param name
     * The name of the state, useful for Dashboard debugging
     * @param target
     * The target of the state. As a majority of encoders report values doubles, this value must be a double
     * @param trackerFunction
     * A lambda function that tracks the current value of the moving mechanism. This is used to determine how close to the target the StateMachine is.
     */
    State(String name, double target, StateMachine boundMachine, DoubleSupplier currentValueGetter){
        commonInits(name, target, boundMachine);
        this.currentValueGetter = currentValueGetter;
    }
    
    
    /**
     * Specify a State that this State can flow to. For example, a flywheel in a "rest" state will flow to an "active" state
     * @param flowState 
     * The state to flow to
     */
    public void to(State flowState){
        this.flowState = flowState;
    }

    /**
     * Specify a State that this State can flow from. For example, a flywheel in an "active" state will get there from a "rest" state
     * @param flowState 
     * The state to flow to 
     */
    public void from(State fromState){
        this.fromState = fromState;
    }

    public boolean at(){
        if (currentValueGetter != null){
            return currentValueGetter.getAsDouble() == target;
        }else{
            return currentValue == target;
        }
    }

    /**
     * Called every loop and triggers robot movements
     */ 
    public abstract void move();
    public double getTarget(){
        return target;
    }



    public String getName() {
        // TODO Auto-generated method stub
        return name;
    }
}
