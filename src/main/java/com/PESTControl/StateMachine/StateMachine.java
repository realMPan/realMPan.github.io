package com.PESTControl.StateMachine;

import java.nio.file.Path;
import java.util.ArrayList;

import com.PESTControl.States.State;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;


/**
 * A class that contains all States and is responsible for moving to and from them accordingly. Also contains the methods nessecary for pathfinding between States
 */
public class StateMachine {
    private final String name;
    private ArrayList<State> boundStates = new ArrayList<State>();
    private State defaultState;
    private State currentState;
    private State targetState;
    private State goalState;
    private ArrayList<State> pathToGoal;
    private boolean bestPathFound;
    ArrayList<ArrayList<State>> allPathsToGoal = new ArrayList<ArrayList<State>>();
    private boolean display = false;



    /**
     * Constructs a custom PEST_Control StateMachine. This constructor will need you to set a defaultState later on
     * @param name
     * The name of the state machine, primarily used for throwing errors and passing data to applicable Dashboards
     * 
     */
    public StateMachine(String name){
        this.name = name;
    }
    /**
     * Constructs a custom PEST_Control StateMachine
     * @param name
     * The name of the state machine, primarily used for throwing errors and passing data to applicable Dashboards
     * @param defaultState
     * The State that this Statemachine should be in upon robot startup. States must be bound to StateMachines in order to be usable.
     */
    public StateMachine(String name, State defaultState){
        this.name = name;
        this.defaultState = defaultState;
        goalState = defaultState;
        currentState = defaultState;
        boundStates.add(defaultState);
        defaultState.bindToMachine(this);
        goalState = defaultState;
    }


    /**
     * Constructs a custom PEST_Control StateMachine
     * @param name
     * The name of the state machine, primarily used for throwing errors and passing data to applicable Dashboards
     * @param defaultState
     * The State that this Statemachine should be in upon robot startup.
     * @param statesToBind
     * An array of States to bind to this StateMachine if they have already been constructed upon StateMachine creation. <p>
     * <strong>The array should include your defaultState Object </strong>
     */
    public StateMachine(String name, State defaultState, ArrayList<State> statesToBind){
        this.name = name;
        this.defaultState = defaultState;
        goalState = defaultState;
        currentState = defaultState;
        for (State state : statesToBind) {
            if(!boundStates.contains(state)){
                boundStates.add(state);
            }
            state.bindToMachine(this);
        }
    }


    /**
     * Set the state that this subsystem will go for
     * @param goalState The State the StateMachine will attempt to reach <p>
     * <strong> Make absolutely sure that your desired State is already bound to this StateMachine </strong>
     */
    public void setGoalState(State goalState){
        if(!boundStates.contains(goalState)){
            DriverStation.reportWarning("Attempted to set goalState to a State not bound to this StateMachine", true);
        }else{
            this.goalState = goalState;
        }
        

    }
    /**
     * Set the DefaultState of the StateMachine. Every call will reset the goalState, so try to call only once
     * @param defaultState The State to set as DefaultState
     */
    public void setDefaultState(State defaultState){
        this.defaultState = defaultState;
        goalState = defaultState;
        if(!boundStates.contains(defaultState)){
            boundStates.add(defaultState);
        }
        defaultState.bindToMachine(this);
    }


    //State Pathplanning
    private void planPathToGoal(){
        allPathsToGoal.clear();
        ArrayList<State> existingPath = new ArrayList<>();
        shortestPath(currentState, goalState, existingPath);
        prune();
    }

    private void shortestPath(State checkingState, State targetState, ArrayList<State> existingPath){
        //makes a copy of the passed path to prevent egregious manipulation of the existing paths and proper copying in recusrive iterations 
        ArrayList<State> tempPath = new ArrayList<State>(existingPath);
        tempPath.add(checkingState);
        for (State state : checkingState.targetStates()) {
            //Recursively runs this method for each valid target state
            if (tempPath.contains(state)){
                //Does not recursively run on this iterated state if the state is already in the temp path
                //Prevents infinite loops
                continue;
            }
            if(state.equals(goalState)){
                //add the state to the end of the temp path, add it to the path list, and force stop the recursion. 
                //If a valid target has been found, then there is no way a faster path could be found through continued recursion
                tempPath.add(state);
                allPathsToGoal.add(tempPath);
                break;
            }
            shortestPath(state, checkingState, tempPath);
        }
        if (tempPath.contains(goalState)){
            //Only valid paths are added to the goalPath container object
            allPathsToGoal.add(tempPath);
            System.out.println("VALID PATH FOUND");
        }
        
    }
    /**
     * Determines whether to display diagnostic info for this StateMachine
     * @param display
     * Whether to put StateMachine info to ShuffleBoard, false by default
     */
    public void setDisplay(boolean display){
        this.display = display;
    }

    private void prune(){
        pathToGoal = new ArrayList<State>();
        for (ArrayList<State> arrayList : allPathsToGoal) {
                if(pathToGoal.size() == 0 || pathToGoal.size() > arrayList.size()){
                    pathToGoal = arrayList;
                }
            } 
    }

    /**
     * The StateMachines periodic method which will attempt to reach the goal state when it is called. This method is always called by {@link com.PESTControl.SystemStateMachines.RobotStateMachine RobotStateMachine}, so ensure goalState changes only happen when nessecary<p>
     * This method does nothing if the StateMachine is already at its goalState
     */
    public void moveToTarget(){
        //Checks if the Path to Goal is valid and we arent already moving to/at the goalState
        if (pathToGoal == null || (!pathToGoal.contains(goalState) && !currentState.equals(goalState))){
            //Replans the path if not
            planPathToGoal();
        }else{
            //Checks if we are at/moving directly to the goalState
            if(currentState.equals(goalState)){
                //runs the state's periodic method to keep it in place
                currentState.periodic();
                //Otherwise, it runs the path
            }else{
                //Checks if we are already at the current state
                if(!currentState.at()){
                    //If not, lets us reach the current state before moving to the goalState
                    currentState.periodic();
                }else{
                    //If we are at the current state, it changes our targetState to the next step in the path
                    currentState = pathToGoal.get(pathToGoal.indexOf(currentState)+1);
                }
            }  
        }
    }
                
    /**
     * The method called by the StateMachine to display pertinent statistics on Shuffleboard. Only runs if display reads true
     */
    public void display(){
        //Display only occurs when the displaying of StateMachine Statistics is desired
        if(display){
            //Current State of the Machine
            Shuffleboard.getTab(name+ " StateMachine").add("Current State", currentState.getName());
            //Default State of the Machine
            Shuffleboard.getTab(name+ " StateMachine").add("Default State", defaultState.getName());
            //Goal State of the Machine
            Shuffleboard.getTab(name+ " StateMachine").add("Goal State", goalState.getName());
            //Path to the Goal State of the StateMachine
            Shuffleboard.getTab(name+ " StateMachine").add("State Path to Goal State", currentState.getName());
        }
        




    }
    
    

}
