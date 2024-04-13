package com.PESTControl.PESTControlUtilities;

import java.util.HashMap;
import java.util.Map;

import com.PESTControl.States.State;

import edu.wpi.first.wpilibj.event.BooleanEvent;
import edu.wpi.first.wpilibj.event.EventLoop;
import edu.wpi.first.wpilibj2.command.button.Trigger;

/**An optional utility class for easily Binding conditions to trigger State Changes. Methods exists specifically for xboxController buttons. */
public class StateBinder {
    private static EventLoop loop = new EventLoop();
    private static HashMap<BooleanEvent, State> eventCache  = new HashMap<BooleanEvent,State>();
    /**
     * Bind a Trigger object to a State object. The State will be set at is bound Machine's goalState when the Trigger reads true
     * @param trigger
     * The Trigger object to listen to
     * @param state
     * The State to activate when the Trigger reads true
     */
    public static void bindStateTrigger(Trigger trigger, State state){
        BooleanEvent tempEvent = new BooleanEvent(loop, trigger);
        tempEvent.ifHigh(() -> {state.boundTo().setGoalState(state);});
        eventCache.put(tempEvent, state);

    }
    /**
     * Bind a Trigger object to a State object. The trueState will be activated when the Trigger reads true
     * When the Trigger reads false, the falseState will be activated
     * @param trigger
     * The Trigger object to listen to
     * @param trueState
     * The State to activate when the Trigger reads true
     * @param falseState
     * The State to activate when the Trigger reads false
     */
    public static void bindDualStateTrigger(Trigger trigger, State trueState, State falseState){
        bindStateTrigger(trigger, trueState);
        BooleanEvent tempEvent = new BooleanEvent(loop, trigger);
        tempEvent.negate().ifHigh(() -> {falseState.activate();;});
        eventCache.put(tempEvent, falseState);
        
    }

    /**
     * Bind a Trigger object to a State object. The trueState will be activated when the Trigger reads true and the dependantState is the currentState of the dependants boundMachine
     * @param trigger
     * The Trigger object to listen to
     * @param state
     * The State to activate when the Trigger reads true
     * @param dependantState
     * The State that must be the currentState of its bound StateMachine in order for State to be activated.
     */
    public static void bindDependantStateTrigger(Trigger trigger, State state, State dependantState){
        BooleanEvent tempEvent = new BooleanEvent(loop, trigger.and(() -> {return dependantState.at();}));
        tempEvent.ifHigh(() -> {state.activate();;});
        eventCache.put(tempEvent, state);
    }

    /**In the future, this method will print all existing bindings to all states */
    public static void printBindings(){
        //TODO: figure out how to implement
    }

    /**StateBinder's periodic method. Only public for usage in RobotStateMachine. <strong> Do not use in robot code. </strong> */
    public static void periodic(){
        loop.poll();
    }
}
