package spell;

/**
 * Created by rdmclean on 1/26/18.
 */

public class Trie implements ITrie {

    private int numWords;
    private int numNodes;
    private Node root;

    public Trie() {
        this.root = new Node();
        numNodes = 1;
        numWords = 0;
    }

    /**
     * Adds the specified word to the trie (if necessary) and increments the word's frequency count
     *
     * @param word The word being added to the trie
     */
    public void add(String word){
        add_helper(root,word);
    }

    public void add_helper(Node n, String word){
        //Base Case
        if(word.length() == 0){     // Reached the end of a word
            if(n.getValue() == 0){  // Has that word already been counted?
                numWords++;
            }
            n.frequency++;
            return;                 //Finished that word
        }
        int spot = word.charAt(0) - 'a';
        if(n.children[spot] == null){
            n.children[spot] = new Node();
            numNodes++;
        }
        add_helper(n.children[spot],word.substring(1));
    }

    /**
     * Searches the trie for the specified word
     *
     * @param word The word being searched for
     *
     * @return A reference to the trie node that represents the word,
     * 			or null if the word is not in the trie
     */
    public Node find(String word){
        return find_helper(root,word); //Todo: I changed this from an INode to a Node
    }

    public Node find_helper(Node n, String word){
        if(n==null){                // If n is null,
            return null;            // you are done
        }
        if(word.length() == 0){     //  Base case, if you are at the end of the word,
            if(n.getValue() > 0){   // and a word is stored there
                return n;           // you are done
            }
            return null;            // But, if there is not a word, you fail
        }

        int spot = word.charAt(0) - 'a';
        return find_helper(n.children[spot], word.substring(1)); // Go to next level
    }

    /**
     * Returns the number of unique words in the trie
     *
     * @return The number of unique words in the trie
     */
    public int getWordCount(){ return numWords;}

    /**
     * Returns the number of nodes in the trie
     *
     * @return The number of nodes in the trie
     */
    public int getNodeCount(){return numNodes;}

    /**
     * The toString specification is as follows:
     * For each word, in alphabetical order:
     * <word>\n
     */
    @Override
    public String toString(){
        StringBuilder current = new StringBuilder();
        StringBuilder output = new StringBuilder();
        toString_helper(root,current,output);
        return output.toString();
    }

    public void toString_helper(Node n, StringBuilder current, StringBuilder output){
        if(n.frequency > 0){ // Base case, you have hit the end of a word
              output.append(current.toString() + "\n");
        }
        for(int i = 0; i < 26; i++){ // Walk through all the children
            if(n.children[i] != null){
                current.append(Character.toString((char) (i + 'a'))); // Add the letter to the end of the word
                toString_helper(n.children[i],current,output);
                current.deleteCharAt(current.length()-1);
            }
        }
    }

    @Override
    public int hashCode(){return numNodes * numWords * 31;} // A consistent hashcode

    @Override
    public boolean equals(Object o){
        if(o==null){
            return false;
        }
        if(o==this){
            return true;
        }
        if(o.getClass() != this.getClass()){
            return false;
        }
        if(o.hashCode() != this.hashCode()){
            return false;
        }
        Trie other = (Trie) o;
        if(other.numNodes != this.numNodes || other.numWords != this.numWords){
            return false;
        }
        return equals_helper(other.root,this.root);
    }

    public boolean equals_helper(Node one, Node two) {
        boolean result = true;
        if(one.frequency != two.frequency){
            return false;
        }
        for(int i = 0; i< 26; i++){
            if(one.children != null && two.children != null){
                result = equals_helper(one.children[i],two.children[i]);
            }
            else if(one.children == null && two.children == null){
                ; // Do nothing, just move on
            }
            else {
                    return false; // Because one of them has a child where the other doesn't
            }
            if(result == false){
                return false; // Break out because something was not equal
            }
        }
        return result;
    }

    /**
     * Your trie node class should implement the ITrie.INode interface
     */
    public class Node implements INode{
        private int frequency;
        Node[] children;

        public Node() {
            frequency = 0;
            children = new Node[26];
        }

        /**
         * Returns the frequency count for the word represented by the node
         *
         * @return The frequency count for the word represented by the node
         */
        public int getValue(){return frequency;}
    }
}
