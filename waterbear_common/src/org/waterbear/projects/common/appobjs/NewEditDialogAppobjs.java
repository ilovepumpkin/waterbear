package org.waterbear.projects.common.appobjs;


public abstract class NewEditDialogAppobjs extends DialogAppobjs {

	private boolean isNew;

	public NewEditDialogAppobjs(boolean isNew) {
		super();
		this.isNew = isNew;
	}

	public boolean isNew() {
		return isNew;
	}
}
