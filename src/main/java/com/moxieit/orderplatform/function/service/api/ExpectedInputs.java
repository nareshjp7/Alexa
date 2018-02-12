package com.moxieit.orderplatform.function.service.api;
import java.util.ArrayList;
import java.util.List;
public class ExpectedInputs {

    public InputPrompt input_prompt;
    public List<ExpectedIntent> possible_intents;
	public ArrayList<ExpectedIntent> systemIntent;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ExpectedInputs that = (ExpectedInputs) o;

        if (input_prompt != null ? !input_prompt.equals(that.input_prompt) : that.input_prompt != null) return false;
        return possible_intents != null ? possible_intents.equals(that.possible_intents) : that.possible_intents == null;
    }

    @Override
    public int hashCode() {
        int result = input_prompt != null ? input_prompt.hashCode() : 0;
        result = 31 * result + (possible_intents != null ? possible_intents.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ExpectedInputs{" +
                "input_prompt=" + input_prompt +
                ", possible_intents=" + possible_intents +
                '}';
    }
}
