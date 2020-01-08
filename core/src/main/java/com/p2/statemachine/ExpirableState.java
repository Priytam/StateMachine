package com.p2.statemachine;

import com.p2.statemachine.iface.StateOwner;
import com.p2.statemachine.timetable.ExpiryEvent;

import java.util.Timer;
import java.util.TimerTask;

public abstract class ExpirableState extends State {

    private Timer timer;
    private StateOwner owner;

    protected void performSpecificOnGlobalEntry(StateOwner owner, State fromState) {
        timer = new Timer();
        this.owner = owner;
        schedule(0);
    }

    private void schedule(long millisSpent) {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    handleExpiryEvent(owner);
                } catch (StateException e) {
                    throw new RuntimeException(e);
                }
            }
        }, getExpiryTimeInMillis() - millisSpent);
    }

    private void handleExpiryEvent(StateOwner owner) throws StateException {
        for (Object oState : vecSubStates) {
            State state = (State) oState;
            state.handleEvent(owner, new ExpiryEvent());
        }
        handleEvent(owner, new ExpiryEvent());
    }

    public abstract long getExpiryTimeInMillis();


    public final void performSpecificOnGlobalExit(StateOwner owner, State fromState) {
        timer.cancel();
    }

    public final void resetExpiry() {
        timer.cancel();
        schedule(0);
    }

    public void onRestore(StateOwner owner) {
        super.onRestore(owner);
        try {
            long timeSpent = System.currentTimeMillis() - owner.getStateMachine().getStartTimeInCurrentStateMillis();
            if (getExpiryTimeInMillis() - timeSpent <= 0) {
                handleExpiryEvent(owner);
            }
            schedule(timeSpent);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
