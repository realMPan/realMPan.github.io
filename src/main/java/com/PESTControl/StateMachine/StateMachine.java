package com.PESTControl.StateMachine;

import java.util.ArrayList;

import com.PESTControl.States.State;

public class StateMachine {
    private final String name;
    private ArrayList<State> boundStates = new ArrayList<State>();
    private State defaultState;
    private State currenState;
    private State targetState = defaultState;

    /**
     * Constructs a custom PEST_Control StateMachine
     * @param name
     * The name of the state machine, primarily used for throwing errors and passing data to applicable Dashboards
     * @param defaultState
     * The State that this Statemachine should be in upon robot startup. States must be bound to StateMachines in order to be usable.
     */
    StateMachine(String name, State defaultState){
        this.name = name;
        this.defaultState = defaultState;
        boundStates.add(defaultState);
    }


    /**
     * Constructs a custom PEST_Control StateMachine
     * @param name
     * The name of the state machine, primarily used for throwing errors and passing data to applicable Dashboards
     * @param defaultState
     * The State that this Statemachine should be in upon robot startup.
     * @param statesToBind
     * An array of States to bind to this StateMachine if they have already been constructed upon StateMachine creation.
     * <strong>The array should include your defaultState Object<strong>
     */
    StateMachine(String name, State defaultState, State[] statesToBind){
        this.name = name;
        this.defaultState = defaultState;
        for (State state : statesToBind) {
            boundStates.add(state);
        }
    }

    public void setTargetState(State targetState) throws Exception{
        if(!boundStates.contains(targetState)){
            throw new Exception("Attempting to move to State: "+targetState.getName()+", but this State is not bound to this StateMachine.");
        }
        this.targetState = targetState;
    }

    /**
     * The StateMachines periodic method, this should be called in your subsystem periodic method to run your StateMachine
     */
    public void periodic(){
        targetState.move();
    }
    

}
