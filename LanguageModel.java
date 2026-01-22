import java.util.HashMap;
import java.util.Random;

public class LanguageModel {

    // The map of this model.
    // Maps windows to lists of charachter data objects.
    HashMap<String, List> CharDataMap;
    
    // The window length used in this model.
    int windowLength;
    
    // The random number generator used by this model. 
	private Random randomGenerator;

    /** Constructs a language model with the given window length and a given
     *  seed value. Generating texts from this model multiple times with the 
     *  same seed value will produce the same random texts. Good for debugging. */
    public LanguageModel(int windowLength, int seed) {
        this.windowLength = windowLength;
        randomGenerator = new Random(seed);
        CharDataMap = new HashMap<String, List>();
    }

    /** Constructs a language model with the given window length.
     * Generating texts from this model multiple times will produce
     * different random texts. Good for production. */
    public LanguageModel(int windowLength) {
        this.windowLength = windowLength;
        randomGenerator = new Random();
        CharDataMap = new HashMap<String, List>();
    }

    /** Builds a language model from the text in the given file (the corpus). */
	public void train(String fileName) {
        String window = "";
        char c;
        In in = new In(fileName);

        // Reads just enough characters to form the first window 
        for (int i = 0; i < windowLength; i++) {
            if (!in.isEmpty()) {
                window += in.readChar();
            }
        }

        // Processes the entire text, one character at a time
        while (!in.isEmpty()) {
            c = in.readChar();

            // Checks if the window is already in the map (Probs can be null)
            List probs = CharDataMap.get(window);
            // If the window was not found in the map
            if (probs == null) {
                // Creates a new empty list, and adds (window,list) to the map
                probs = new List();
                CharDataMap.put(window, probs);
            }

            // Calculates the counts of the current character
            probs.update(c);

            // Advances the window: adds c to end, deletes the first character
            window = window.substring(1) + c;
        }

        // After counting, compute p and cp for all lists in the map
        // The entire file has been processed, and all the characters have been counted.     
        // Proceeds to compute and set the p and cp fields of all the CharData objects    
        // in each linked list in the map.    
        for (List probs : CharDataMap.values()) {      
            calculateProbabilities(probs); 
        }
    } 


    // Computes and sets the probabilities (p and cp fields) of all the
	// characters in the given list. */
    public void calculateProbabilities(List probs) {               
        // 1. Calculate the total number of occurrences for this window
        int totalCounts = 0;
        Node current = probs.firstNode();
        while (current != null) {
            totalCounts += current.cp.count;
            current = current.next;
        }

        // 2. Compute individual probabilities (p) and cumulative probabilities (cp)
        double runningSum = 0.0;
        current = probs.firstNode();
        while (current != null) {
            // p_i = count_i / totalCounts
            current.cp.p = (double) current.cp.count / totalCounts;
            
            // cp_i = cp_{i-1} + p_i
            runningSum += current.cp.p; 
            current.cp.cp = runningSum;
            
            current = current.next;
        }
    }

    // Returns a random character from the given probabilities list.
	public char getRandomChar(List probs) {
		// Using Monte Carlo sampling
        double r = randomGenerator.nextDouble(); // random number between 0 and 1

        // Iterate through the list until we find the character
        Node current = probs.firstNode();
        while (current != null) {
            if (r <= current.cp.cp) {
                return current.cp.chr;
            }
            current = current.next;
        }

        // Should never reach here if probabilities are set correctly
        return '\0'; // return null character as a fallback
	}

    /**
	 * Generates a random text, based on the probabilities that were learned during training. 
	 * @param initialText - text to start with. If initialText's last substring of size numberOfLetters
	 * doesn't appear as a key in Map, we generate no text and return only the initial text. 
	 * @param numberOfLetters - the size of text to generate
	 * @return the generated text
	 */
	public String generate(String initialText, int textLength) {
        if (initialText.length() < windowLength) {
            return initialText;
        }

        StringBuilder generatedText = new StringBuilder(initialText);
        String window = initialText.substring(initialText.length() - windowLength);

        // Continue until the TOTAL length of generatedText matches textLength
        while (generatedText.length() < textLength) {
            List probs = CharDataMap.get(window);
            
            if (probs == null) {
                break; // Stop if we hit an unknown window
            }

            char nextChar = getRandomChar(probs);
            generatedText.append(nextChar);
            
            // Update window using the newly generated character
            window = generatedText.substring(generatedText.length() - windowLength);
        }

        return generatedText.toString();
    }

    /** Returns a string representing the map of this language model. */
    @Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		for (String key : CharDataMap.keySet()) {
			List keyProbs = CharDataMap.get(key);
			str.append(key + " : " + keyProbs + "\n");
		}
		return str.toString();
	}

    public static void main(String[] args) {
        // Parse command-line arguments as per assignment requirements
        int windowLength = Integer.parseInt(args[0]);
        String initialText = args[1];
        int generatedTextLength = Integer.parseInt(args[2]);
        boolean isRandom = args[3].equals("random");
        String fileName = args[4];

        // Initialize the model with the correct constructor
        LanguageModel lm;
        if (isRandom) {
            lm = new LanguageModel(windowLength);
        } else {
            // Use the fixed seed 20 for reproducible testing
            lm = new LanguageModel(windowLength, 20);
        }

        // Train the model on the provided file
        lm.train(fileName);

        // Generate and print the resulting text
        System.out.println(lm.generate(initialText, generatedTextLength));
    }
}