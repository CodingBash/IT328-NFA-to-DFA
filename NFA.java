import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * NFA Main Runner Class
 * 
 * @author bbecer2
 *
 */
public class NFA {

	/**
	 * Main function
	 * 
	 * @param args
	 * @throws FileNotFoundException
	 */
	public static void main(String[] args) throws FileNotFoundException {
		/*
		 * FOR TESTING PURPOSES ON LOCAL MACHINE List<String> filenames =
		 * Arrays.asList("res/nfa1", "res/nfa2", "res/nfa3", "res/nfa4"); List<String>
		 * inputList = retrieveInputList("res/inputStrings.txt");
		 * 
		 * for (String filename : filenames) {
		 * System.out.println("NOTE: \'~\' represents a lambda transition"); NFAutomata
		 * nfa = readNfa(filename); System.out.println(nfa);
		 * System.out.println("To DFA:"); DFAutomata dfa = convertNfaToDfa(nfa);
		 * System.out.println(dfa); testDfa(dfa, inputList); break; }
		 */

		String nfaFilename = args[0];
		String inputFilename = args[1];

		List<String> inputList = retrieveInputList(inputFilename);
		System.out.println("NOTE: \'~\' represents a lambda transition");
		NFAutomata nfa = readNfa(nfaFilename);
		System.out.println(nfa);
		System.out.println("To DFA:");
		DFAutomata dfa = convertNfaToDfa(nfa);
		System.out.println(dfa);
		testDfa(dfa, inputList);
	}

	/**
	 * Read input string list from file
	 * 
	 * @param filename
	 * @return
	 * @throws FileNotFoundException
	 */
	public static List<String> retrieveInputList(String filename) throws FileNotFoundException {
		List<String> inputList = new ArrayList<String>();
		try {
			Scanner s = new Scanner(new File(filename));

			while (s.hasNext()) {
				inputList.add(s.next());
			}
			s.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		}
		return inputList;
	}

	/**
	 * Test the DFA on the input string list
	 * 
	 * @param dfa
	 * @param inputList
	 */
	public static void testDfa(DFAutomata dfa, List<String> inputList) {
		System.out.println("The following strings are accepted: ");

		/*
		 * For each input
		 */
		for (String input : inputList) {
			List<Character> sigmaTransitionOrder = new ArrayList<Character>();
			for (char c : input.toCharArray()) {
				sigmaTransitionOrder.add(c);
			}

			// Set current state as the DFA start state
			Set<Integer> currentState = dfa.getStartState();

			/*
			 * Loop state variables valid: is the string valid broke: did the loop break out
			 */
			boolean valid = true;
			boolean broke = false;

			/*
			 * For each sigma transition in the input string
			 */
			for (Character sigma : sigmaTransitionOrder) {
				/*
				 * Is the current state empty? If yes, break out and invalidate string. Cannot
				 * do transition on null state
				 */
				if (currentState.isEmpty()) {
					valid = false;
					broke = true;
					break;
				}

				/*
				 * Set current state as the transition state for sigma and the lambda transition
				 */
				Set<Integer> transitionStates = dfa.transition(currentState, sigma);
				transitionStates.addAll(dfa.transition(currentState, '~'));
				currentState = transitionStates;
			}

			/*
			 * If not invalid from short input, check if last state is on a final state
			 */
			if (!broke) {
				valid = dfa.getFinalStates().contains(currentState);
			}

			if (valid)
				System.out.println(input);
		}
	}

	/**
	 * Root function for converting the NFA to DFA
	 * 
	 * Calls {@link NFA#recursiveConversion(NFAutomata, Character, Set)}
	 * 
	 * @param nfa
	 * @return
	 */
	public static DFAutomata convertNfaToDfa(NFAutomata nfa) {
		Set<Integer> currentStates = new HashSet<Integer>();
		currentStates.add(nfa.getStartState());
		DFAutomata convertedAutomata = recursiveConversion(nfa, '~', currentStates);
		return convertedAutomata;
	}

	/**
	 * Recursive function for converting NFA to DFA
	 * 
	 * @param nfa
	 * @param incomingSigma
	 * @param incomingTransitionState
	 * @return
	 */
	public static DFAutomata recursiveConversion(NFAutomata nfa, Character incomingSigma,
			Set<Integer> incomingTransitionState) {

		/*
		 * If the transition state is empty, we've reach the end. Return null as base
		 * case
		 */
		if (incomingTransitionState.isEmpty()) {
			return null;
		}

		/*
		 * Create root DFA from the incoming transition state and the NFA as context
		 */
		DFAutomata rootDfa = initializeDFAutomata(incomingTransitionState, nfa);

		/*
		 * Get all possible sigma transitions
		 */
		Set<Character> sigmas = nfa.getSigmas();

		/*
		 * For each possible sigma transition
		 */
		for (Character sigma : sigmas) {
			/*
			 * Get possible transition states for the target sigma
			 */
			Set<Integer> transitionStates = getTransitionStates(nfa, sigma, incomingTransitionState);

			/*
			 * If the possible transition state equals previous transition state
			 * 
			 * Then set transition to itself in the DFA (this also prevents recursion stack
			 * overflow.
			 * 
			 * Else recursively call the conversion on the new transition state and attach
			 * the result to the root DFA
			 */
			if (!incomingTransitionState.equals(transitionStates)) {
				DFAutomata recursiveAutomata = recursiveConversion(nfa, sigma, transitionStates);
				rootDfa.attachDFAutomata(incomingTransitionState, sigma, recursiveAutomata);
			} else {
				rootDfa.addTransition(incomingTransitionState, sigma, transitionStates);
			}

		}

		return rootDfa;
	}

	/**
	 * Initializes a base DFA given only a start state
	 * 
	 * @param startState
	 * @param nfa
	 * @return
	 */
	public static DFAutomata initializeDFAutomata(Set<Integer> startState, NFAutomata nfa) {
		DFAutomata rootDfa = new DFAutomata();
		Set<Set<Integer>> rootDfaStates = new HashSet<Set<Integer>>();
		rootDfaStates.add(startState);
		rootDfa.setStartState(startState);
		rootDfa.setSigmas(nfa.getSigmas());
		rootDfa.setStates(rootDfaStates);

		Set<Integer> intersection = new HashSet<Integer>(nfa.getFinalStates());
		intersection.retainAll(startState);
		if (!intersection.isEmpty()) {
			Set<Set<Integer>> finalStates = new HashSet<Set<Integer>>();
			finalStates.add(startState);
			rootDfa.setFinalStates(finalStates);
		}

		return rootDfa;
	}

	/**
	 * Helper function to get transition states given sigma
	 * 
	 * @param nfa
	 * @param sigma
	 * @param states
	 * @return
	 */
	public static Set<Integer> getTransitionStates(NFAutomata nfa, Character sigma, Set<Integer> states) {
		Set<Integer> transitionStates = new HashSet<Integer>();
		for (Integer state : states) {
			transitionStates.addAll(nfa.transition(state, sigma));
		}

		return transitionStates;
	}

	/**
	 * File input function to read the NFA from file
	 * 
	 * @param filename
	 * @return
	 */
	public static NFAutomata readNfa(String filename) {
		NFAutomata automata = new NFAutomata();
		File file = new File(filename);
		try {
			Scanner sc = new Scanner(file);

			/*
			 * Below are some state variables for initializing the NFA
			 */
			String sigmaLine = "";
			List<String> transitionLines = new ArrayList<String>();
			Integer initialState = -1;
			Set<Integer> finalStates = new HashSet<Integer>();

			/*
			 * Below are some state variables for the scanner
			 */
			boolean firstLine = true;
			boolean secondLine = false;
			boolean retrievedInitialState = false;
			boolean retrievedFinalState = false;
			int stateSize = -1;
			
			/*
			 * For each line in the file
			 */
			while (sc.hasNextLine()) {
				if (firstLine) {
					/*
					 * Get the amount of states
					 */
					stateSize = Integer.parseInt(sc.nextLine());
					firstLine = false;
					secondLine = true;
				} else if (secondLine) {
					/*
					 * Get the sigma line
					 */
					sigmaLine = sc.nextLine();
					secondLine = false;
				} else {
					
					if (stateSize > 0) {
						/*
						 * Add the state transitions to a list
						 */
						transitionLines.add(sc.nextLine());
						stateSize--;
					} else if (!retrievedInitialState) {
						/*
						 * Get the initial state
						 */
						initialState = Integer.parseInt(sc.nextLine());
						retrievedInitialState = true;
					} else if (!retrievedFinalState) {
						/*
						 * Get the final states
						 */
						String finalStatesLine = sc.nextLine();
						List<String> finalStatesStrings = Arrays
								.asList(finalStatesLine.replace("{", "").replace("}", "").split(","));
						for (String finalStatesString : finalStatesStrings) {
							finalStates.add(Integer.parseInt(finalStatesString));
						}
						retrievedFinalState = true;
					} else {
						/*
						 * Extra line in the file . . . break.
						 */
						break;
					}
				}
			}
			/*
			 * Parse the sigma line
			 */
			sigmaLine = sigmaLine.trim().replace("\t", "").replaceAll("  ", " ");
			List<String> sigmaStrings = Arrays.asList(sigmaLine.split(" "));
			Set<Character> sigmas = new HashSet<Character>();
			for (String sigmaString : sigmaStrings) {
				sigmas.add(sigmaString.toCharArray()[0]);
			}
			sigmas.add('~'); // Represents lambda string
			List<Character> sigmasList = new ArrayList<Character>(sigmas);

			/*
			 * Parse the sigma transition lines
			 */
			Map<Integer, Map<Character, Set<Integer>>> transitionStructure = new HashMap<Integer, Map<Character, Set<Integer>>>();
			for (String transitionLine : transitionLines) {
				StringTokenizer transitionLineTokenizer = new StringTokenizer(transitionLine, ": ", false);

				boolean stateRead = false;
				Integer state = -1;

				Map<Character, Set<Integer>> stateTransition = new HashMap<Character, Set<Integer>>();

				/*
				 * String Tokenizer to parse the transition line
				 */
				int sigmasListIndex = 0;
				while (transitionLineTokenizer.hasMoreTokens()) {
					if (!stateRead) {
						state = Integer.parseInt(transitionLineTokenizer.nextToken());
						stateRead = true;
					} else {
						String transitionLineTokenizerString = transitionLineTokenizer.nextToken().replaceAll("\t", "")
								.trim();
						List<String> sigmaTransitionStateList = Arrays
								.asList(transitionLineTokenizerString.replace("{", "").replace("}", "").split(","));
						Set<Integer> transitionStates = new HashSet<Integer>();
						for (String sigmaTransitionState : sigmaTransitionStateList) {
							if (!sigmaTransitionState.isEmpty()) {
								transitionStates.add(Integer.parseInt(sigmaTransitionState));
							}
						}

						stateTransition.put(sigmasList.get(sigmasListIndex), transitionStates);
						sigmasListIndex++;
					}
				}
				transitionStructure.put(state, stateTransition);
			}
			
			/*
			 * Add information to the NFA object
			 */
			automata.setTransitionStructure(transitionStructure);
			automata.setSigmas(sigmas);
			automata.setStartState(initialState);
			automata.setFinalStates(finalStates);
			sc.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		return automata;
	}
}
