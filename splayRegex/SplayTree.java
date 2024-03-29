// To import the java utilities package - to use Scanner class.
import java.util.*;

public class SplayTree {
	//class SNode to represent a node in the Splay Tree. 
	//Each node contains a key (a word in our case), value (count of occurrences), and its right and left child. 
	//The constructor initializes these attributes.
	class SNode {
		String key;
		int value;
		SNode left, right;

		SNode(String key, int value) {
			this.key = key;
			this.value = value;
			left = right = null;
		}
	}
	private SNode root ;
	/**
	 * Splay operation to maintain balance
	 * Two parameters: node and key.
	 * Node: represents the current node being considered in the splay operation.
	 * Key (string): Target key that needs to be splayed to the root.
	 */
	private SNode splay(SNode node, String key) {
		/**
		 * If the current node is null or its key matches the target key, 
		 * we've found the node we're looking for or have reached the end of the tree. 
		 * In either case, we return the current node.
		 */
		if (node == null || node.key.equals(key))
			return node;

		//Check for the left subtree
		if (key.compareTo(node.key) < 0) {
			if (node.left == null) 
				return node;
			int var = key.compareTo(node.left.key);
			if (var < 0) {
				node.left.left = splay(node.left.left, key);
				node = rightRotate(node);
			} else if (var > 0) {
				//
				node.left.right = splay(node.left.right, key);
				if (node.left.right != null)
					node.left = leftRotate(node.left);
			}
			return (node.left == null) ? node : rightRotate(node);	

		} 
		else {//check for the right subtree
			if (node.right == null) 
				return node;
			int var = key.compareTo(node.right.key);
			if (var < 0) {
				node.right.left = splay(node.right.left, key);
				if (node.right.left != null)
					node.right = rightRotate(node.right);
			} else if (var > 0) {
				node.right.right = splay(node.right.right, key);
				node = leftRotate(node);
			}
			return (node.right == null) ? node : leftRotate(node);
		}
	}

	// Right rotation
	private SNode rightRotate(SNode node) {
		SNode var = node.left; // storing the target node (left child of the current (root) node).
		node.left = var.right; // If we rotate the current node to right, the current node's left child will be right child of the target node.
		var.right = node; //The current node becomes right node of the target node since we are rotating right. 
		return var;
	}

	// Left rotation
	private SNode leftRotate(SNode node) {
		SNode var = node.right; // storing the target node (right child of the current (root) node).
		node.right = var.left; // If we rotate the current node to left, the current node's right child will be left child of the target node.
		var.left = node; //The current node becomes left node since of the target node we are rotating left. 
		return var;
	}

	// Insert a new word into the Splay Tree
	public void insert(String key) {
		root = insertNode(root, key);
	}

	// Helper method for insertion
	private SNode insertNode(SNode root, String key) {
		/**
		 * 	If the tree is empty, created a new node with its value as 1
		 *  as it would be the first occurrence.
		 */
		if (root == null) {
			SNode newNode = new SNode(key, 1);
			return newNode; 	
		}

		// If not empty, Splay the tree to bring the node containing the key to the root.
		root = splay(root, key);

		// If the keys are equal, increment the value and return the root
		if (key.compareTo(root.key) == 0) {
			root.value++;
			return root;
		}

		// Create a new node with the given key and value as 1
		SNode insertedNode = new SNode(key, 1);
		int var = key.compareTo(root.key);

		// The new node is inserted in an appropriate position
		if (var < 0) {
			insertedNode.right = root;
			insertedNode.left = root.left;
			root.left = null;
		} else {
			insertedNode.left = root;
			insertedNode.right = root.right;
			root.right = null;
		}

		return insertedNode;//Return the new node

	}

	// Delete a word from the Splay Tree
	public void delete(String key) {
		root = deleteNode(root, key);
	}

	// Helper method for deletion
	private SNode deleteNode(SNode root, String key) {

		if (root == null)
			return null;//returns null because there's nothing to delete.

		root = splay(root, key);//Brings the key(word to be deleted) to the root.

		int var = key.compareTo(root.key);

		if (var == 0) {
			if (root.left == null)
				return root.right;
			else if (root.right == null)
				return root.left;
			else {
				SNode min = getMin(root.right);
				root.key = min.key;
				root.value = min.value;
				root.right = deleteNode(root.right, min.key);
			}
		}
		return root;
	}
	
	// Find the minimum node in the subtree
	private SNode getMin(SNode node) {
		while (node.left != null)
			node = node.left;//min node is the leftmost node.
		return node;
	}

	// Get the count of occurrences for a given word
	// This is used to check if the word exist in dictionary. 
	// if Value is > 0, then word exist in dictionary.
	public int get(String key) {
		root = splay(root, key);
		if (root != null && root.key.equals(key))
			return root.value;
		else
			return 0;
	}

	// Suggest corrections for a given word based on edit distance
	public List<String> similarWords(String word, int threshold) {
		List<String> similarWords = new ArrayList<>();
		similarWordsRec(root, word, threshold, similarWords);
		return similarWords;
	}

	// Helper method for suggestion
	/**
	 * `node`: The current node being processed.
	 * `word`: The word for which corrections are being suggested.
	 * `threshold`: The maximum edit distance allowed for a suggested correction.
	 * `similarWords`: The list to store the suggested corrections.
	 */
	private void similarWordsRec(SNode node, String word, int threshold, List<String> similarWords) {
		if (node == null)
			return;
		
		similarWordsRec(node.left, word, threshold, similarWords);
		
		int distance = calculateDistance(node.key, word);

		if (distance <= threshold)
			similarWords.add(node.key);
		
		similarWordsRec(node.right, word, threshold, similarWords);
	}

	// Calculate Edit distance between two strings
	private int calculateDistance(String s1, String s2) {
		//2D array `dp` to store the edit distances for substrings of `s1` and `s2`.
		int[][] dp = new int[s1.length() + 1][s2.length() + 1];

		for (int a = 0; a <= s1.length(); a++)
			dp[a][0] = a;

		for (int b = 0; b <= s2.length(); b++)
			dp[0][b] = b;

		for (int a = 1; a <= s1.length(); a++) {
			for (int b = 1; b <= s2.length(); b++) {
				if (s1.charAt(a - 1) == s2.charAt(b - 1))
					dp[a][b] = dp[a - 1][b - 1];
				else
					dp[a][b] = 1 + Math.min(dp[a - 1][b - 1], Math.min(dp[a - 1][b], dp[a][b - 1]));
			}
		}

		return dp[s1.length()][s2.length()];
	}


	// In-order traversal to list all words in the dictionary
	public void traverse() {
		inorderTraverse(root);
	}

	// Helper method for in-order traversal
	private void inorderTraverse(SNode node) {
		if (node != null) {
			//Traverse through all the left nodes until it has no child
			inorderTraverse(node.left);
			System.out.println(node.key);
			//Starts traversing all the right nodes.
			inorderTraverse(node.right);
		}
	}
}
