package com.PESTControl.SystemStateMachines;

import java.util.ArrayList;

import com.PESTControl.StateMachine;
import com.PESTControl.PESTControlUtilities.StateBinder;
import com.PESTControl.States.State;

import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.SubsystemBase;


/**The primary StateMachine for controlling overall Robot movement. 
 * This StateMachine will require RobotStates, which are states that requires other StateMachines to be in specific states
 * The method run() must be put in robotPeriodic
 */
public class RobotStateMachine extends SubsystemBase  {
    public static ArrayList<StateMachine> registeredStateMachines = new ArrayList<StateMachine>();
    private static RobotStateMachine schedulerInstance = new RobotStateMachine();

    protected RobotStateMachine(){

    }


    /**
     * Register a StateMachine with the {@link com.PESTControl.SystemStateMachines.RobotStateMachine RobotStateMachine}.<p>  <strong>All StateMachines MUST be registered in order to work </strong>
     * @param machineToRegister The StateMachine to register
     */
    public static void registerStateMachine(StateMachine machineToRegister){
        if(!registeredStateMachines.contains(machineToRegister)){
            registeredStateMachines.add(machineToRegister);
        }
    }

    //The method that is periodically called to run all registered StateMachines
    private static void run(){
        for (StateMachine stateMachine : registeredStateMachines) {
            stateMachine.moveToTarget();
            stateMachine.display();
        }
    }

    @Override
    public void periodic(){
        run();
        StateBinder.periodic();
    }
    public static void activate(){
        CommandScheduler.getInstance().registerSubsystem(schedulerInstance);
    }
    
}
