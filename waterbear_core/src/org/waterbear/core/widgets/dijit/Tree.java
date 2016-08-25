package org.waterbear.core.widgets.dijit;

import org.waterbear.core.exception.AutomationException;

import net.sf.sahi.client.ElementStub;

public class Tree extends TreeNode {

	public Tree(ElementStub es, Object... stateValues) {
		super(es, stateValues);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Expand the nodes according to the specified tree path.
	 * 
	 * @param path
	 *            Tree path.
	 * @param delimiter
	 *            User specified delimiter.
	 */
	public TreeNode expandTree(String path, String delimiter) {
		String[] nodes = path.substring(delimiter.length()).trim()
				.split(delimiter);
		TreeNode n = node(nodes[0]);
		if (nodes.length == 1) {
			n.click();
			return n;
		} else {
			n.expand();
		}
		for (int i = 1; i < nodes.length; i++) {
			String nodeLabel = nodes[i];
			TreeNode childNode = n.node(nodeLabel);
			if (i == nodes.length - 1) {
				childNode.click();
				return childNode;
			} else {
				childNode.expand();
				n = childNode;
			}
		}
		throw new AutomationException(
				"Failed to expand the tree with the path [" + path + "].");
	}

	/**
	 * 
	 * Expand the nodes according to the specified tree path (formed with "/" as
	 * default delimiter).
	 * 
	 * @param path
	 */
	public TreeNode expandTree(String path) {
		return expandTree(path, "/");
	}
}
