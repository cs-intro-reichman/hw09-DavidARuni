

/** A linked list of character data objects.
 *  (Actually, a list of Node objects, each holding a reference to a character data object.
 *  However, users of this class are not aware of the Node objects. As far as they are concerned,
 *  the class represents a list of CharData objects. Likwise, the API of the class does not
 *  mention the existence of the Node objects). */
public class List {

    // Points to the first node in this list
    private Node first;

    // The number of elements in this list
    private int size;
	
    /** Constructs an empty list. */
    public List() {
        first = null;
        size = 0;
    }
    
    public Node firstNode() {
        return first;
    }

    /** Returns the number of elements in this list. */
    public int getSize() {
 	      return size;
    }

    /** Returns the CharData of the first element in this list. */
    public CharData getFirst() {
        if(first == null){
            return null;
        }
        return first.cp;
    }

    /** GIVE Adds a CharData object with the given character to the beginning of this list. */
    public void addFirst(char chr) {
        Node tmp = first;
        first = new Node( new CharData(chr), tmp );
        size++;
    }
    
    /** GIVE Textual representation of this list. */
    @Override
    public String toString() {
        Node current = first;
        StringBuilder result = new StringBuilder("("); // Start with an opening parenthesis
        while (current != null) {
            result.append(current.cp.toString());
            if (current.next != null) {
                result.append(" "); // Add space between elements
            }
            current = current.next;
        }
        result.append(")"); // End with a closing parenthesis
        return result.toString();
    }

    /** Returns the index of the first CharData object in this list
     *  that has the same chr value as the given char,
     *  or -1 if there is no such object in this list. */
    public int indexOf(char chr) {
        Node current = first;
        int index = 0;
        while (current != null) {
            if (current.cp.equals(chr)) {
                return index;
            }
            current = current.next;
            index++;
        }
        return -1;
    }

    /** If the given character exists in one of the CharData objects in this list,
     *  increments its counter. Otherwise, adds a new CharData object with the
     *  given chr to the beginning of this list. */
    public void update(char chr) {
        Node current = first;
        while (current != null) {
            if (current.cp.equals(chr)) {
                current.cp.count++;
                return;
            }
            current = current.next;
        }
        // If not found, add new CharData at the beginning
        addFirst(chr);
    }

    /** GIVE If the given character exists in one of the CharData objects
     *  in this list, removes this CharData object from the list and returns
     *  true. Otherwise, returns false. */
    public boolean remove(char chr){
        Node dummy = new Node(null, first); 
        Node prev = dummy;
        Node current = first;

        while (current != null) {
            if (current.cp.equals(chr)) {
                prev.next = current.next;   
                // Update the actual list head (in case we removed the first node)
                first = dummy.next; 
                size--;
                return true;
            }
            // Move both pointers forward
            prev = current;
            current = current.next;
        }

        return false;
    }

    /** Returns the CharData object at the specified index in this list. 
     *  If the index is negative or is greater than the size of this list, 
     *  throws an IndexOutOfBoundsException. */
    public CharData get(int index) {
        // Your code goes here
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
        Node current = first;
        int i = 0;
        while (i < index) {
            current = current.next;
            i++;
        }
        return current.cp;
    }

    /** Returns an array of CharData objects, containing all the CharData objects in this list. */
    public CharData[] toArray() {
	    CharData[] arr = new CharData[size];
	    Node current = first;
	    int i = 0;
        while (current != null) {
    	    arr[i++]  = current.cp;
    	    current = current.next;
        }
        return arr;
    }

    /** Returns an iterator over the elements in this list, starting at the given index. */
    public ListIterator listIterator(int index) {
	    // If the list is empty, there is nothing to iterate   
	    if (size == 0) return null;
	    // Gets the element in position index of this list
	    Node current = first;
	    int i = 0;
        while (i < index) {
            current = current.next;
            i++;
        }
        // Returns an iterator that starts in that element
	    return new ListIterator(current);
    }

    public static void main(String[] args) {
        List list = new List();
        list.addFirst('a');
        list.addFirst('b');
        list.addFirst('c');
        // Testing various methods
        // Expected output:
        // List: (c 1 0.0 0.0) (b 1 0.0 0.0) (a 1 0.0 0.0)
        // Size: 3
        // Using toString method
        System.out.println("List: " + list);
        System.out.println("Size: " + list.getSize());
        System.out.println("Index of 'b': " + list.indexOf('b'));
        System.out.println("Get first: " + list.getFirst());
        System.out.println("Get index 1: " + list.get(1));
        list.update('b');
        System.out.println("After updating 'b': " + list);
        list.remove('a');
        System.out.println("After removing 'a': " + list);
        CharData[] arr = list.toArray();
        System.out.print("Array: ");
        for (CharData cd : arr) {
            System.out.print(cd + " ");
        }
        System.out.println();
    }
}