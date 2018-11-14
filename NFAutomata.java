import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * NFA Class
 * 
 * @author bbecer2
 *
 */
public class NFAutomata {

	private Map<Integer, Map<Character, Set<Integer>>> transitionStructure;
	private Set<Integer> states;
	private Set<Character> sigmas;
	private Integer startState;
	private Set<Integer> finalStates;

	public NFAutomata() {
		transitionStructure = new HashMap<Integer, Map<Character, Set<Integer>>>();
		this.states = new HashSet<Integer>();
		this.sigmas = new HashSet<Character>();
		this.startState = new Integer(-1);
		this.finalStates = new HashSet<Integer>();
	}

	public Map<Integer, Map<Character, Set<Integer>>> getTransitionStructure() {
		return transitionStructure;
	}

	public void setTransitionStructure(Map<Integer, Map<Character, Set<Integer>>> transitionStructure) {
		this.transitionStructure = transitionStructure;
	}

	public Set<Integer> getStates() {
		return states;
	}

	public void setStates(Set<Integer> states) {
		this.states = states;
	}

	public Set<Character> getSigmas() {
		return sigmas;
	}

	public void setSigmas(Set<Character> sigmas) {
		this.sigmas = sigmas;
	}

	public Integer getStartState() {
		return startState;
	}

	public void setStartState(Integer startState) {
		this.startState = startState;
	}

	public Set<Integer> getFinalStates() {
		return finalStates;
	}

	public void setFinalStates(Set<Integer> finalStates) {
		this.finalStates = finalStates;
	}

	/**
	 * From the transition datastructure, the the transition states given the target
	 * state and sigma
	 * 
	 * @param state
	 * @param sigma
	 * @return
	 */
	public Set<Integer> transition(Integer state, Character sigma) {
		return this.transitionStructure.get(state).get(sigma);
	}
	
	/**
	 * Overridden {@link Object#toString()} function.
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Sigma:" + sigmas);
		sb.append(System.getProperty("line.separator"));
		sb.append("--------");
		sb.append(System.getProperty("line.separator"));
		for (Map.Entry<Integer, Map<Character, Set<Integer>>> stateEntry : transitionStructure.entrySet()) {
			sb.append(stateEntry.getKey() + ": ");
			for (Map.Entry<Character, Set<Integer>> transitionEntry : stateEntry.getValue().entrySet()) {
				sb.append("(" + transitionEntry.getKey() + "," + transitionEntry.getValue() + ")");
			}
			sb.append(System.getProperty("line.separator"));
		}
		sb.append("--------");
		sb.append(System.getProperty("line.separator"));
		sb.append(startState + ": Initial State");
		sb.append(System.getProperty("line.separator"));
		sb.append(finalStates + ": Accepting State(s)");
		sb.append(System.getProperty("line.separator"));
		return sb.toString();
	}

}
