package com.PESTControl.States;

import java.util.ArrayList;
import java.util.function.DoubleSupplier;

import com.PESTControl.StateMachine.StateMachine;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


/**
 * The high-level State Class. It is <strong> highly </strong> reccomended to use states from the StateType folder. <p>
 *  If you are looking for high customizability of your States. {@link com.PESTControl.States.StateTypes.CustomState CustomState} may be your best bet.
 */
public abstract class State {
    protected DoubleSupplier currentValueGetter = null;
    protected double currentValue = Double.NaN;
    protected String name;
    protected double target;

    /**
     * All of the states that this state can go to
     */
    protected ArrayList<State> validTargets = new ArrayList<State>();
    /**
     * All of the states that can go to this state
     */
    protected ArrayList<State> validOrigins = new ArrayList<State>();
    
    protected StateMachine boundMachine = null;
    private State dependantState = null;
    private boolean reversable = true;
    private Runnable controlFunction;
    private boolean display;


    //Constructors
    
    
  
    
    
    
    

    /**
     * Initializes a State Object
     * @param name
     * The name of the state, useful for Dashboard debugging
     * @param target
     * The target of the state. As a majority of encoders report values doubles, this value must be a double
     * @param boundMachine
     * The StateMachine for this state to bind to. The binded StateMachine will be the only object in your project that can properly "activate" the state and trigger movements.
     * @param currentValueGetter
     * A DoubleSupplier that tracks the current value of the subsystem relative to its target
     * @param controlFunction
     * A Runnable that will be called in the states move() method. Allows for high customization of how a State moves
     */
    protected State(String name, double target, DoubleSupplier currentValueGetter, Runnable controlFunction) {
        this.name = name;
        this.target = target;
        this.currentValueGetter = currentValueGetter;
        this.controlFunction = controlFunction;
    }
    


    


    //Flow Control
    /**
     * Add an "origin" State. Origin States indicate States that can flow to this state once their target is reached. For example: The "closed" state of a door has an origin state of "open"
     * @param originState 
     * The state to flow to <p>
     * You can have multiple States that this State can flow from
     * Adding an origin state automatically adds this state's object as a target state to each origin
     */
    public void addOrigin(State originState){
        State[] tempArray = {originState};
        addOrigin(tempArray);
    }
     /**
     * Add multiple "origin" States. Origin States indicate States that can flow to this state once their target is reached.
     * @param originStateArray
     * An array of states to add as origins
     * Adding an origin state automatically adds this state's object as a target state to each origin
     */
    public void addOrigin(State[] originStateArray){
        for (State state : originStateArray) {
            if(!validOrigins.contains(state)){
                validOrigins.add(state);
            }
            if(!state.validTargets.contains(this)){
                state.addOrigin(this);
            }
        }
    }

    /**
     * Add a "target" State. Target States indicate States that this State can flow to once reached. For example: when a door is in an "open" state, a valid target would be a "closed" state
     * @param targetState 
     * The state to add as a Target
     * Adding a target state automatically adds this state's object as an origin state to each object
     */
    public void addTarget(State targetState){
        State[] tempArray = {targetState};
        addTarget(tempArray);
    }

    /**
     * Add multiple "target" States. Target States indicate States that this State can flow to once reached.
     * @param targetStateArray 
     * The state to add as a target
     * Adding a target state automatically adds this state's object as an origin state to each object
     */
    public void addTarget(State[] targetStateArray){
        for (State state : targetStateArray) {
            if(!validTargets.contains(state)){
                validTargets.add(state);
            }
            if(!state.validOrigins.contains(this)){
                state.addOrigin(this);
            }
        }
    }

    /**
     * Sets a State's "Dependant State"<p>
     * If the if the dependantState is not the currentState of its bound StateMachine, this state will not call its move() function and will do nothing when activated
     * For example: A door cannot enter a "closed" state until it is at its "open" state
     * @param dependantState The State to wait for before calling move() 
     */
    public void dependsOn(State dependantState){
        this.dependantState = dependantState;
    }
    
    
    /**
     * A getter that tells you if you are accurately at this states target
     * @return True if at this states target, false if otherwise.
     * For PIDControlled States, it is reccomended to have a tolerance passed into the State constructor if your controller is not perfectly tuned
     */
    public boolean at(){
        if (currentValueGetter != null){
            return currentValueGetter.getAsDouble() == target;
        }else{
            return currentValue == target;
        }
    }

    /**
     * Returns whether or not this State can target the passed State
     * @param checkingState The State being checked
     * @return Whether or not this State can target the checkingState
     */
    public boolean hasTargetState(State checkingState){
        return validTargets.contains(checkingState);
    }
    /**
     * Returns whether or not this State can be targeted by the passed State
     * @param checkingState The State being checked
     * @return Whether or not this State can be targeted by the checkingState
     */
    public boolean hasOriginState(State checkingState){
        return validOrigins.contains(checkingState);
    }
    /**
     * Get the array containing all of this states valid targetStates
     * @return An ArrayList contianing every valid targetState
     */
    public ArrayList<State> targetStates(){
        return validTargets;
    }
    /**
     * Get the array containing all of this states valid originState
     * @return An ArrayList contianing every valid originState
     */
    public ArrayList<State> originStates(){
        return validOrigins;
    }
    public String getName() {
        return name;
    }
    /**
     * The StateMachine this state is bound to
     * @return The Bound StateMachine
     */
    public StateMachine boundTo(){
        return boundMachine;
    }
    public boolean equals(State obj) {
        return name==obj.getName();
    }
    public void bindToMachine(StateMachine boundMachine){
        this.boundMachine = boundMachine;
    }
    

    //Subclass Assistors. Help with making State Types
    protected void overrideControlFunction(Runnable controlFunction){
        this.controlFunction = controlFunction;
    }
    



    //Movement and Control

    /**
     * An easy way to set the bound StateMachine's goal state to the State Object this method is being called by.
     */
    public void activate(){
        boundMachine.setGoalState(this);
    }
    /**
     * The implemented method that is responsible for StateMovement
     */ 
    private void move(){
        controlFunction.run();
    }
    /**The actual method responsible for moving to the State. Will run neccesarry checks and calls before calling move()
    */
    public void periodic(){
        if(dependantState != null && !dependantState.at()){
            dependantState.activate();
        }else{
            move();
        }
        
    }
}
