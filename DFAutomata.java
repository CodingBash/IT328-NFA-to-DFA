import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * DFA Class
 * 
 * @author bbecer2
 *
 */
public class DFAutomata {

	private Map<Set<Integer>, Map<Character, Set<Integer>>> transitionStructure;
	private Set<Set<Integer>> states;
	private Set<Character> sigmas;
	private Set<Integer> startState;
	private Set<Set<Integer>> finalStates;

	public DFAutomata() {
		transitionStructure = new HashMap<Set<Integer>, Map<Character, Set<Integer>>>();
		this.states = new HashSet<Set<Integer>>();
		this.sigmas = new HashSet<Character>();
		this.startState = new HashSet<Integer>();
		this.finalStates = new HashSet<Set<Integer>>();
	}

	public Map<Set<Integer>, Map<Character, Set<Integer>>> getTransitionStructure() {
		return transitionStructure;
	}

	public void setTransitionStructure(Map<Set<Integer>, Map<Character, Set<Integer>>> transitionStructure) {
		this.transitionStructure = transitionStructure;
	}

	public Set<Set<Integer>> getStates() {
		return states;
	}

	public void setStates(Set<Set<Integer>> states) {
		this.states = states;
	}

	public Set<Character> getSigmas() {
		return sigmas;
	}

	public void setSigmas(Set<Character> sigmas) {
		this.sigmas = sigmas;
	}

	public Set<Integer> getStartState() {
		return startState;
	}

	public void setStartState(Set<Integer> startState) {
		this.startState = startState;
	}

	public Set<Set<Integer>> getFinalStates() {
		return finalStates;
	}

	public void setFinalStates(Set<Set<Integer>> finalStates) {
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
	public Set<Integer> transition(Set<Integer> state, Character sigma) {
		Set<Integer> transitionState =  this.getTransitionStructure().get(state).get(sigma);
		if(transitionState == null) {
			transitionState = new HashSet<Integer>();
		}
		return transitionState;
	}

	/**
	 * Add a transition item to the transition datastructure
	 * 
	 * @param state
	 * @param sigma
	 * @param transitionState
	 */
	public void addTransition(Set<Integer> state, Character sigma, Set<Integer> transitionState) {
		if (this.transitionStructure.containsKey(state)) {
			this.transitionStructure.get(state).put(sigma, transitionState);
		} else {
			Map<Character, Set<Integer>> transitionMap = new HashMap<Character, Set<Integer>>();
			transitionMap.put(sigma, transitionState);
			this.transitionStructure.put(state, transitionMap);
		}
	}

	/**
	 * Attach a DFA to this DFA
	 * 
	 * @param state - state to attach to
	 * @param sigma - sigma transition between state and initial state of new DFA
	 * @param automataToAttach - the DFA to attach
	 * @return
	 */
	public DFAutomata attachDFAutomata(Set<Integer> state, Character sigma, DFAutomata automataToAttach) {
		if (automataToAttach != null) {
			// Add the transition from specified state to the current state of the attached
			// automata
			addTransition(state, sigma, automataToAttach.getStartState());
			// Transfer all transitions to the current transitionStructure
			this.transitionStructure.putAll(automataToAttach.transitionStructure);
			
			// Add final states
			this.finalStates.addAll(automataToAttach.finalStates);
		}
		return this;
	}

	/**
	 * Overridden {@link Object#toString()} function.
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("NOTE:" + "[x, y, z, ...] indicates a single state that contained the entries from the original NFA.");
		sb.append(System.getProperty("line.separator"));
		sb.append(String.format("%15s", "Sigma: "));
		for(Character sigma : sigmas)
			sb.append(String.format("%15s", sigma));
		sb.append(System.getProperty("line.separator"));
		sb.append("---------------------------------------------------------------------------------------");
		sb.append(System.getProperty("line.separator"));
		for (Map.Entry<Set<Integer>, Map<Character, Set<Integer>>> stateEntry : transitionStructure.entrySet()) {
			sb.append(String.format("%15s", stateEntry.getKey() + ":"));
			for (Map.Entry<Character, Set<Integer>> transitionEntry : stateEntry.getValue().entrySet()) {
				sb.append(String.format("%15s", transitionEntry.getValue()));
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
