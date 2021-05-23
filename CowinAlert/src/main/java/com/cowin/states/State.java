package com.cowin.states;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class State implements Comparable<State> {

    private Integer state_id;
    private String state_name;

    @Override
    public String toString() {
        return "State ID: " + state_id + (state_id < 10 ? "\t" : "") + "\tState Name: " + state_name;
    }

    @Override
    public int compareTo(State state) {
        return getState_name().compareTo(state.getState_name());
    }

}
