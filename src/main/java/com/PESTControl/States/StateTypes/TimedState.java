package com.PESTControl.States.StateTypes;

import java.util.function.DoubleSupplier;
import edu.wpi.first.wpilibj.Timer;

import com.PESTControl.States.State;

/**
 * A state made solely for testing purposes in simulation. Not meant for usage on a robot, but can prove useful when working with PEST_Control in simulation
 */
public class TimedState extends State {
    Timer timer = new Timer();
    private boolean active = false;
    double target;
    
    /**
     * Create a TimedState, a state that will run for a set amount of time before its at() function returns true
     * @param name
     * The name of the state
     * @param target
     * The value the state is trying to reach. In this case, it would be an amount of elapsed time.
     */
    public TimedState(String name, double target) {
        super(name, target, null, null);
        this.target = target;
        overrideControlFunction(() ->{this.runTimer();});
        overrideGetterFunction(() -> {return this.getTime();});

        
    }
    private double getTime(){
        return timer.get();
    }
    private void runTimer(){
        if(getTime() > target || !active){
            timer.restart();
            active = true;
        }
    }   
    
}
