package org.waterbear.core.widgets.dijit;

import static org.testng.AssertJUnit.assertTrue;
import static org.waterbear.core.widgets.WidgetFinder.byIndex;
import static org.waterbear.core.widgets.WidgetFinder.byLabel;
import static org.waterbear.core.widgets.WidgetFinder.webElem;
import static org.waterbear.core.widgets.WidgetFinder.widget;

import org.waterbear.core.widgets.ET;
import org.waterbear.core.widgets.WebElement;

import net.sf.sahi.client.ElementStub;

public class TreeNode extends DojoWidget {

	public TreeNode(ElementStub es, Object... stateValues) {
		super(es, stateValues);
		// TODO Auto-generated constructor stub
	}

	public String label() {
		return getBrowser().span("/dijitTreeLabel.*/").in(es).getText();
	}

	public void click() {
		getBrowser().div("rowNode").in(es).click();
	}

	public void expand() {
		if (isClosed()) {
			expandoNode().click();
		}
		assertTrue("The node is not expanded.", isOpened());
	}

	public WebElement expandoNode() {
		return webElem("expandoNode", ET.SPAN, es);
	}

	public boolean isLeaf() {
		return expandoNode().getWidgetClasses()
				.endsWith("dijitTreeExpandoLeaf");
	}

	public boolean isOpened() {
		return expandoNode().getWidgetClasses().endsWith(
				"dijitTreeExpandoOpened");
	}

	public boolean isClosed() {
		return expandoNode().getWidgetClasses().endsWith(
				"dijitTreeExpandoClosed");
	}

	public void assertOpened() {
		assertTrue(isOpened());
	}

	public void assertClosed() {
		assertTrue(isClosed());
	}

	public TreeNode node(String nodeLabel) {
		return (TreeNode) byLabel(nodeLabel, TreeNode.class, containerNode());
	}

	public WebElement containerNode() {
		return webElem("containerNode", ET.DIV, es);
	}

	public TreeNode node(int index) {
		return (TreeNode) byIndex(index, TreeNode.class, es);
	}

	public TreeNode nodeByIconClass(String iconClass) {
		WebElement iconWE = webElem("dijitInline dijitIcon dijitTreeIcon "
				+ iconClass, ET.SPAN, es);
		return (TreeNode) widget(iconWE, TreeNode.class, es);
	}

	public void assertNodeOnTree(String nodeLabel) {
		assertTrue("Node [" + nodeLabel + "] is not on the tree.",
				node(nodeLabel).exists());
	}

	public void clickLink() {
		getBrowser().link(0).in(es).click();
	}
}
