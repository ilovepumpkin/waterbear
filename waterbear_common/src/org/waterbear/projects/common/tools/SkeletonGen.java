package org.waterbear.projects.common.tools;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import org.fest.swing.annotation.GUITest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.waterbear.core.annotation.Genable;
import org.waterbear.core.annotation.KeyField;
import org.waterbear.core.exception.AutomationException;
import org.waterbear.core.utils.WBStringUtils;
import org.waterbear.projects.common.appobjs.NewEditDialogAppobjs;

import com.sun.codemodel.JArray;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JForLoop;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JType;
import com.sun.codemodel.JVar;

/**
 * The rules to use SkeletonGen: 1) AppObject classe names end with 'Page' or
 * 'Dialog' 2) The method to return the widget starts with 'get' 3) If the
 * dialog class extends NewEditDialogAppobjs.java, set the annotation KeyField.
 * 4) The getter methods to return widgets have no arguments. 5) Name the method
 * return the progress dialog title as "getPrgsDlgTitle" if you have.
 * 
 * @author shenrui@cn.ibm.com
 * 
 */
public abstract class SkeletonGen {
	private File destDir;
	private MetaData metaData;

	public static final String ARG_SUFFIX = "Arg";
	public static final String ARG_SEPERATOR = ",";

	public static final String[] SUPPORTED_CLASS_SUFFIXS = { "Dialog", "Page" };

	protected SkeletonGen(String qualifiedClassName)
			throws ClassNotFoundException {
		this(Class.forName(qualifiedClassName));
	}

	protected SkeletonGen(Class appobjClazz) {

		if (!isClassSupported(appobjClazz)) {
			throw new AutomationException(appobjClazz
					+ " is not supported. The class name must end with "
					+ WBStringUtils.stringtify(SUPPORTED_CLASS_SUFFIXS));
		}

		this.metaData = new MetaData(appobjClazz);
		destDir = new File(System.getProperty("user.dir"), "toolssrc");

		metaData.setNavTasksClazz(getNavTasksClass());
		metaData.setBaseTestClazz(getBaseTestClass());
		metaData.setBaseTaskClazz(getBaseTaskClass());
	}

	protected static String[] readClassNames() {
		Scanner scanner = new Scanner(System.in);
		System.out
				.print("Input your fully qualified class names (seperated with comma):");
		String str = scanner.nextLine();
		return str.split(",");
	}

	protected abstract Class getNavTasksClass();

	protected abstract Class getBaseTestClass();

	protected abstract Class getBaseTaskClass();

	private boolean isClassSupported(Class clazz) {
		String simpleName = clazz.getSimpleName();
		for (int i = 0; i < SUPPORTED_CLASS_SUFFIXS.length; i++) {
			String suffix = SUPPORTED_CLASS_SUFFIXS[i];
			if (simpleName.endsWith(suffix)) {
				return true;
			}
		}
		return false;
	}

	public void gen() {
		Method[] methods = metaData.getAppobjClazz().getDeclaredMethods();
		for (int i = 0; i < methods.length; i++) {
			Method m = methods[i];

			Class returnType = m.getReturnType();
			if (returnType.isAnnotationPresent(Genable.class)) {
				Genable g = (Genable) returnType.getAnnotation(Genable.class);
				String setDataMethodName = g.setDataMethodName();
				Method[] ms = returnType.getMethods();
				List<FieldMetaData> fieldMetaDataList = metaData
						.getFieldMetaDataList();
				for (int j = 0; j < ms.length; j++) {
					Method theMethod = ms[j];
					if (theMethod.getName().equals(setDataMethodName)) {
						String mName = m.getName();
						if (!mName.startsWith("get")) {
							throw new AutomationException(
									"["
											+ mName
											+ "] The method to retuirn a widget must start with a 'get'.");
						} else {
							mName = mName.replace("get", "");
							mName = mName.substring(0, 1).toLowerCase()
									+ mName.substring(1);
						}

						Class[] paramTypes = theMethod.getParameterTypes();
						String dataTypeName = paramTypes[0].getSimpleName();
						if (paramTypes.length > 1) {
							String argName = mName + ARG_SUFFIX;
							mName = "";
							dataTypeName = "";
							for (int k = 0; k < paramTypes.length; k++) {
								Class clazz = paramTypes[k];
								mName = mName + argName + k;
								dataTypeName = dataTypeName
										+ clazz.getSimpleName();
								if (k != paramTypes.length - 1) {
									mName = mName + ARG_SEPERATOR;
									dataTypeName = dataTypeName + ARG_SEPERATOR;
								}
							}
						}
						FieldMetaData fieldMetaData = new FieldMetaData(mName,
								dataTypeName, setDataMethodName);
						fieldMetaDataList.add(fieldMetaData);
						if (m.isAnnotationPresent(KeyField.class)) {
							metaData.setKeyField(fieldMetaData);
						}

					}
				}
			}
		}

		destDir.mkdirs();
		new VOClassGen(metaData, destDir).gen();
		new TaskClassGen(metaData, destDir).gen();
		new TestClassGen(metaData, destDir).gen();
	}

}

class MetaData {
	private Class appobjClazz;
	private String objName;
	private String packageName;
	private String voClassName;
	private String taskClassName;
	private String testClassName;
	private List<FieldMetaData> fieldMetaDataList = new ArrayList<FieldMetaData>();

	private String voClassVarName;
	private String taskClassVarName;
	private String editMethodName;
	private String createMethodName;
	private String verifyInPageMethodName;
	private String deleteMethodName;

	private String appobjClassVarName;

	private FieldMetaData keyField;

	private Class navTasksClazz;
	private Class baseTestClazz;
	private Class baseTaskClazz;

	public MetaData(Class appobjClazz) {
		this.appobjClazz = appobjClazz;

		objName = genObjName(appobjClazz);
		voClassName = objName + "VO";
		taskClassName = objName + "Tasks";
		testClassName = objName + "Test";
		packageName = this.appobjClazz.getPackage().getName();

		voClassVarName = voClassName.substring(0, 1).toLowerCase()
				+ voClassName.substring(1);
		taskClassVarName = taskClassName.substring(0, 1).toLowerCase()
				+ taskClassName.substring(1);

		editMethodName = "edit" + objName;
		createMethodName = "create" + objName;
		deleteMethodName = "delete" + objName;
		verifyInPageMethodName = "verify" + objName + "InPage";

		String simpleName = appobjClazz.getSimpleName();
		appobjClassVarName = simpleName.substring(0, 1).toLowerCase()
				+ simpleName.substring(1);

	}

	public void setBaseTaskClazz(Class baseTaskClazz) {
		this.baseTaskClazz = baseTaskClazz;
	}

	public Class getBaseTaskClazz() {
		return baseTaskClazz;
	}

	public Class getBaseTestClazz() {
		return baseTestClazz;
	}

	public void setBaseTestClazz(Class baseTestClazz) {
		this.baseTestClazz = baseTestClazz;
	}

	public Class getNavTasksClazz() {
		return navTasksClazz;
	}

	public void setNavTasksClazz(Class navTasksClazz) {
		this.navTasksClazz = navTasksClazz;
	}

	public void setKeyField(FieldMetaData keyField) {
		if (this.keyField != null) {
			throw new AutomationException(
					"KeyField is already set. Looks like you defined two KeyFields in your Appobj Class. Check, please.");
		}
		this.keyField = keyField;
	}

	public FieldMetaData getKeyField() {
		if (keyField == null) {
			throw new AutomationException("The KeyField needs to be defined.");
		}
		return keyField;
	}

	public String getDeleteMethodName() {
		return deleteMethodName;
	}

	public String getVerifyInPageMethodName() {
		return verifyInPageMethodName;
	}

	public String getCreateMethodName() {
		return createMethodName;
	}

	public String getAppobjClassVarName() {
		return appobjClassVarName;
	}

	private String genObjName(Class clazz) {
		String objName = clazz.getSimpleName();
		for (int i = 0; i < SkeletonGen.SUPPORTED_CLASS_SUFFIXS.length; i++) {
			String suffix = SkeletonGen.SUPPORTED_CLASS_SUFFIXS[i];
			objName = objName.replace(suffix, "");
		}
		return objName;
	}

	public Class getAppobjClazz() {
		return appobjClazz;
	}

	public String getObjName() {
		return objName;
	}

	public String getPackageName() {
		return packageName;
	}

	public String getVoClassName() {
		return voClassName;
	}

	public String getTaskClassName() {
		return taskClassName;
	}

	public String getTestClassName() {
		return testClassName;
	}

	public List<FieldMetaData> getFieldMetaDataList() {
		return fieldMetaDataList;
	}

	public String getVoClassVarName() {
		return voClassVarName;
	}

	public String getTaskClassVarName() {
		return taskClassVarName;
	}

	public String getEditMethodName() {
		return editMethodName;
	}

}

class VOClassGen extends BaseClassGen {
	private JDefinedClass c;
	private JCodeModel cm;
	private JMethod constructor;

	VOClassGen(MetaData metaData, File destDir) {
		super(metaData, destDir);
		// TODO Auto-generated constructor stub
	}

	void gen() {
		JCodeModel cm = new JCodeModel();

		try {
			c = cm._class(metaData.getPackageName() + "."
					+ metaData.getVoClassName());

			constructor = c.constructor(JMod.PUBLIC);
			JBlock consBody = constructor.body();

			List<FieldMetaData> fieldMetaDataList = metaData
					.getFieldMetaDataList();
			for (int i = 0; i < fieldMetaDataList.size(); i++) {
				FieldMetaData fieldMetaData = (FieldMetaData) fieldMetaDataList
						.get(i);
				JType fieldType = cm.parseType(fieldMetaData.getDataType());
				String name = fieldMetaData.getName();

				if (name.indexOf(SkeletonGen.ARG_SEPERATOR) == -1) {
					genVOClassStuff(fieldType, name);
				} else {
					String[] names = name.split(SkeletonGen.ARG_SEPERATOR);
					String[] types = fieldMetaData.getDataType().split(
							SkeletonGen.ARG_SEPERATOR);
					for (int j = 0; j < names.length; j++) {
						String theName = names[j];
						JType theFieldType = cm.parseType(types[j]);
						genVOClassStuff(theFieldType, theName);
					}
				}

			}

			cm.build(destDir);
		} catch (Exception e) {
			throw new AutomationException(e);
		}
	}

	private void genVOClassStuff(JType fieldType, String name) {
		JFieldVar fieldVar = c.field(JMod.PRIVATE, fieldType, name);
		JVar consParam = constructor.param(fieldType, name);

		constructor.body().assign(JExpr.refthis(name), consParam);

		JMethod getter = c.method(JMod.PUBLIC, fieldType, "get"
				+ name.substring(0, 1).toUpperCase() + name.substring(1));
		getter.body()._return(fieldVar);
	}

}

class BaseClassGen {
	protected MetaData metaData;
	protected File destDir;

	protected boolean isDialog;
	protected boolean isNewEdit;

	BaseClassGen(MetaData metaData, File destDir) {
		this.metaData = metaData;
		this.destDir = destDir;

		String appobjSimpleName = metaData.getAppobjClazz().getSimpleName();
		if (appobjSimpleName.endsWith("Page")) {
			isDialog = false;
		} else if (appobjSimpleName.endsWith("Dialog")) {
			isDialog = true;
			if (metaData.getAppobjClazz().getSuperclass()
					.equals(NewEditDialogAppobjs.class)) {
				isNewEdit = true;
			}
		} else {
			throw new AutomationException(appobjSimpleName
					+ " is neither Page nor Dialog.");
		}
	}
}

class TestClassGen extends BaseClassGen {
	private JCodeModel cm;
	private JDefinedClass c;

	private JClass hashMapClass;
	private JFieldVar voClassVar;
	private JFieldVar keyVar;
	private JFieldVar taskVar;

	private JType voClassVarType;

	TestClassGen(MetaData metaData, File destDir) {
		super(metaData, destDir);
	}

	void gen() {
		cm = new JCodeModel();
		try {
			hashMapClass = cm.directClass(HashMap.class.getName());

			c = cm._class(metaData.getPackageName() + "."
					+ metaData.getTestClassName());
			c._extends(metaData.getBaseTestClazz());

			voClassVarType = cm.parseType(metaData.getVoClassName());
			if (isNewEditMode()) {
				voClassVarType = cm.parseType(metaData.getVoClassName()
						+ "[][]");
			}
			voClassVar = c.field(JMod.PRIVATE, voClassVarType,
					metaData.getVoClassVarName());

			keyVar = c.field(JMod.PRIVATE + JMod.FINAL + JMod.STATIC,
					cm.parseType("String"), "KEY_" + metaData.getVoClassName(),
					JExpr.lit(metaData.getVoClassVarName()));
			taskVar = c.field(JMod.PRIVATE,
					cm.parseType(metaData.getTaskClassName()),
					metaData.getTaskClassVarName());

			/*
			 * setUp method
			 */
			genSetUpMethods();

			/*
			 * test method
			 */
			genTestMethods();

			/*
			 * PrepareTestData method
			 */
			genPrepareTestDataMethod();

			cm.build(destDir);
		} catch (Exception e) {
			throw new AutomationException(e);
		}

	}

	void genPrepareTestDataMethod() throws ClassNotFoundException {

		JMethod prepTestDataMethod = c.method(JMod.PROTECTED, hashMapClass,
				"prepareTestData");
		prepTestDataMethod.annotate(cm.directClass("Override"));
		JBlock body = prepTestDataMethod.body();
		JVar hashMapVar = body.decl(hashMapClass, "dataMap",
				JExpr._new(hashMapClass));

		JInvocation initVoVar = JExpr._new(cm.parseType(metaData
				.getVoClassName()));
		List<FieldMetaData> fieldMetaDataList = metaData.getFieldMetaDataList();
		for (int i = 0; i < fieldMetaDataList.size(); i++) {
			FieldMetaData fieldMetaData = fieldMetaDataList.get(i);
			String name = fieldMetaData.getName();
			initVoVar.arg(JExpr.ref(name));
		}
		JVar entryVar = null;

		if (isNewEditMode()) {
			JVar vo1CreateVar = body.decl(
					cm.parseType(metaData.getVoClassName()),
					metaData.getVoClassVarName() + "1_create", initVoVar);
			JVar vo1EditVar = body.decl(
					cm.parseType(metaData.getVoClassName()),
					metaData.getVoClassVarName() + "1_edit", initVoVar);
			JVar vo2CreateVar = body.decl(
					cm.parseType(metaData.getVoClassName()),
					metaData.getVoClassVarName() + "2_create", initVoVar);
			JVar vo2EditVar = body.decl(
					cm.parseType(metaData.getVoClassName()),
					metaData.getVoClassVarName() + "2_edit", initVoVar);

			JArray initArray = JExpr.newArray(cm.parseType(metaData
					.getVoClassName() + "[]"));
			initArray.add(JExpr
					.newArray(cm.directClass(metaData.getVoClassName()))
					.add(vo1CreateVar).add(vo1EditVar));
			initArray.add(JExpr
					.newArray(cm.directClass(metaData.getVoClassName()))
					.add(vo2CreateVar).add(vo2EditVar));

			entryVar = body.decl(cm.directClass(metaData.getVoClassName())
					.array().array(), metaData.getVoClassVarName(), initArray);

		} else {
			entryVar = body.decl(cm.parseType(metaData.getVoClassName()),
					metaData.getVoClassVarName(), initVoVar);
		}

		JInvocation putInvoke = body.invoke(hashMapVar, "put");
		putInvoke.arg(keyVar);

		putInvoke.arg(entryVar);

		body._return(hashMapVar);
	}

	void genSetUpMethods() throws ClassNotFoundException {
		JMethod setupMethod = c.method(JMod.PUBLIC, cm.VOID, "setUp");
		setupMethod.annotate(cm.ref(BeforeClass.class));
		JBlock setupMethodBody = setupMethod.body();
		setupMethodBody.assign(taskVar,
				JExpr._new(cm.parseType(metaData.getTaskClassName())));
		JVar dataMapVar = setupMethodBody.decl(hashMapClass, "dataMap", JExpr
				.invoke("loadTestData").arg(JExpr.TRUE));

		setupMethodBody.assign(
				voClassVar,
				JExpr.cast(voClassVarType,
						JExpr.invoke(dataMapVar, "get").arg(keyVar)));

		List<String> gotoMethodNames = getGotoMethodNames();
		if (gotoMethodNames.size() == 0) {
			setupMethodBody
					.directStatement("//TODO: the code to navigate to the necessary page.");
		} else if (gotoMethodNames.size() == 1) {
			setupMethodBody.directStatement("navTasks."
					+ gotoMethodNames.get(0) + "();");
		} else {
			setupMethodBody
					.directStatement("//TODO: Multiple GOTO methods are detected. Make your decision.");
			for (Iterator<String> it = gotoMethodNames.iterator(); it.hasNext();) {
				String mName = (String) it.next();
				setupMethodBody.directStatement("//navTasks." + mName + "();");
			}
		}

	}

	private List<String> getGotoMethodNames() {
		String objName = metaData.getObjName();
		ArrayList<String> names = new ArrayList<String>();
		Class navTasksClazz = metaData.getNavTasksClazz();
		Method[] methods = navTasksClazz.getDeclaredMethods();
		for (int i = 0; i < methods.length; i++) {
			Method method = methods[i];
			if (method.getName().contains(objName)) {
				names.add(method.getName());
			}
		}
		return names;
	}

	void genTestMethods() throws ClassNotFoundException {

		String testMethodName = "test"
				+ metaData.getEditMethodName().substring(0, 1).toUpperCase()
				+ metaData.getEditMethodName().substring(1);
		if (isNewEditMode()) {
			testMethodName = "testCreateEditDelete" + metaData.getObjName()
					+ "s";
		}
		JMethod testMethod = c.method(JMod.PUBLIC, cm.VOID, testMethodName);
		JBlock body = testMethod.body();

		if (isNewEditMode()) {

			JVar keyFieldVar = body.decl(cm.parseType("String[]"), metaData
					.getKeyField().getName() + "s",
					JExpr._new(cm.parseType("String[]")));
			/*
			 * create and edit
			 */
			JForLoop forLoop = body._for();
			JVar iVar = forLoop.init(cm.INT, "i", JExpr.lit(0));
			forLoop.test(iVar.lte(voClassVar.ref("length")));
			forLoop.update(iVar.incr());
			JBlock forLoopBody = forLoop.body();
			JVar tempVar = forLoopBody.decl(
					cm.parseType(metaData.getVoClassName() + "[]"), "temp",
					voClassVar.component(iVar));
			JVar createVOVar = forLoopBody.decl(
					cm.directClass(metaData.getVoClassName()),
					metaData.getVoClassVarName() + "_create",
					tempVar.component(JExpr.lit(0)));
			JVar editVOVar = forLoopBody.decl(
					cm.directClass(metaData.getVoClassName()),
					metaData.getVoClassVarName() + "_edit",
					tempVar.component(JExpr.lit(1)));
			forLoopBody.invoke(taskVar, metaData.getCreateMethodName()).arg(
					createVOVar);
			forLoopBody.invoke(taskVar, metaData.getEditMethodName())
					.arg(editVOVar).arg(createVOVar);

			String keyFieldName = metaData.getKeyField().getName();
			forLoopBody.assign(
					keyFieldVar.component(iVar),
					editVOVar.invoke("get"
							+ keyFieldName.substring(0, 1).toUpperCase()
							+ keyFieldName.substring(1)));

			/*
			 * delete
			 */
			body.invoke(taskVar, metaData.getDeleteMethodName()).arg(
					keyFieldVar);

		} else {
			body.invoke(taskVar, metaData.getEditMethodName()).arg(voClassVar);
		}
		testMethod.annotate(cm.ref(Test.class));
		testMethod.annotate(cm.ref(GUITest.class));
	}

	private boolean isNewEditMode() {
		return isDialog && isNewEdit;
	}
}

class TaskClassGen extends BaseClassGen {
	private JCodeModel cm;
	private JDefinedClass c;

	private JFieldVar pageField;

	TaskClassGen(MetaData metaData, File destDir) {
		super(metaData, destDir);
	}

	private boolean isNewEditMode() {
		return isDialog && isNewEdit;
	}

	private void genPerformMethod() throws ClassNotFoundException {
		JMethod performMethod = c.method(JMod.PRIVATE, cm.VOID, "perform");
		JBlock body = performMethod.body();

		JVar newDataParamVar = performMethod.param(
				cm.parseType(metaData.getVoClassName()), "newData");

		JVar invockerVar = pageField;

		if (isDialog) {
			JVar currDataParamVar = null;
			if (isNewEdit) {
				currDataParamVar = performMethod.param(
						cm.parseType(metaData.getVoClassName()), "currData");
			}
			JVar appobjClassParamVar = performMethod.param(
					cm.ref(metaData.getAppobjClazz()),
					metaData.getAppobjClassVarName());
			if (isNewEdit) {
				JInvocation verifyInvoke = body
						._if(currDataParamVar.ne(JExpr._null()))._then()
						.invoke("verify");

				verifyInvoke.arg(appobjClassParamVar);
				verifyInvoke.arg(currDataParamVar);
			}
			invockerVar = appobjClassParamVar;
		}
		List<FieldMetaData> fieldMetaDataList = metaData.getFieldMetaDataList();
		for (int i = 0; i < fieldMetaDataList.size(); i++) {
			FieldMetaData fieldMetaData = fieldMetaDataList.get(i);
			String name = fieldMetaData.getName();

			boolean hasMultiArgs = false;
			int argCount = 1;

			if (name.indexOf(SkeletonGen.ARG_SEPERATOR) > 1) {
				hasMultiArgs = true;
				String[] temp = name.split(SkeletonGen.ARG_SEPERATOR);
				argCount = temp.length;
				name = temp[0].split(SkeletonGen.ARG_SUFFIX)[0];
			}
			String getterName = "get" + name.substring(0, 1).toUpperCase()
					+ name.substring(1);
			String setDataMethodName = fieldMetaData.getSetDataMethodName();

			JInvocation setDataInvoker = body.invoke(
					invockerVar.invoke(getterName), setDataMethodName);
			if (hasMultiArgs) {
				for (int n = 0; n < argCount; n++) {
					setDataInvoker.arg(newDataParamVar.invoke(getterName
							+ SkeletonGen.ARG_SUFFIX + n));
				}
			} else {
				setDataInvoker.arg(newDataParamVar.invoke(getterName));
			}

		}

		body.directStatement("//TODO: save the UI changes. Change the method \"clickOKWithProgressDialog\" if it does not apply to your case.");
		try {
			metaData.getAppobjClazz().getMethod("getPrgsDlgTitle");
			body.directStatement(metaData.getAppobjClassVarName()
					+ ".clickOKWithProgressDialog("
					+ metaData.getAppobjClassVarName()
					+ ".getPrgsDlgTitle(), 0);");
		} catch (Exception e) {
			body.directStatement("//" + metaData.getAppobjClassVarName()
					+ ".clickOKWithProgressDialog(progressDialogTitle, 0);");
		}

	}

	private void genVerifyMethod() throws ClassNotFoundException {
		JMethod verifyMethod = c.method(JMod.PRIVATE, cm.VOID, "verify");

		JVar invockerVar = pageField;

		if (isDialog) {
			JVar appobjClassParamVar = verifyMethod.param(
					cm.ref(metaData.getAppobjClazz()),
					metaData.getAppobjClassVarName());
			invockerVar = appobjClassParamVar;
		}

		JVar verifyMethodParamVar = verifyMethod.param(
				cm.parseType(metaData.getVoClassName()),
				metaData.getVoClassVarName());
		JBlock verifyMethodBody = verifyMethod.body();
		List<FieldMetaData> fieldMetaDataList = metaData.getFieldMetaDataList();
		for (int i = 0; i < fieldMetaDataList.size(); i++) {
			FieldMetaData fieldMetaData = fieldMetaDataList.get(i);
			String name = fieldMetaData.getName();

			boolean hasMultiArgs = false;
			int argCount = 1;

			if (name.indexOf(SkeletonGen.ARG_SEPERATOR) > 1) {
				hasMultiArgs = true;
				String[] temp = name.split(SkeletonGen.ARG_SEPERATOR);
				argCount = temp.length;
				name = temp[0].split(SkeletonGen.ARG_SUFFIX)[0];
			}

			String getterName = "get" + name.substring(0, 1).toUpperCase()
					+ name.substring(1);

			JInvocation verifyInvoker = verifyMethodBody.invoke(
					invockerVar.invoke(getterName), "verify");
			if (hasMultiArgs) {
				for (int n = 0; n < argCount; n++) {
					verifyInvoker.arg(verifyMethodParamVar.invoke(getterName
							+ SkeletonGen.ARG_SUFFIX + n));
				}
			} else {
				verifyInvoker.arg(verifyMethodParamVar.invoke(getterName));
			}

		}
	}

	private void genEditMethod() throws ClassNotFoundException {
		JMethod editMethod = c.method(JMod.PUBLIC, cm.VOID,
				metaData.getEditMethodName());
		JBlock body = editMethod.body();
		if (isDialog) {
			JVar newDataParamVar = editMethod.param(
					cm.parseType(metaData.getVoClassName()), "newData");
			JVar currDataParamVar = null;
			if (isNewEdit) {
				currDataParamVar = editMethod.param(
						cm.parseType(metaData.getVoClassName()), "currData");
			}

			body.directStatement("//TODO: add the code to launch the dialogue.");
			JInvocation newInvoke = JExpr._new(cm._ref(metaData
					.getAppobjClazz()));
			if (isNewEdit) {
				newInvoke.arg(JExpr.FALSE);
			}
			JVar dlgVar = body.decl(cm._ref(metaData.getAppobjClazz()),
					metaData.getAppobjClassVarName(), newInvoke);

			JInvocation performInvoke = body.invoke("perform");
			performInvoke.arg(newDataParamVar);
			if (isNewEdit) {
				performInvoke.arg(currDataParamVar);
			}
			performInvoke.arg(dlgVar);

			body.invoke(metaData.getVerifyInPageMethodName()).arg(
					newDataParamVar);
		} else {
			JVar editMethodParamVar = editMethod.param(
					cm.parseType(metaData.getVoClassName()),
					metaData.getVoClassVarName());

			body.directStatement("//TODO: add the preparation code here.");
			body.invoke("perform").arg(editMethodParamVar);
			body.invoke("verify").arg(editMethodParamVar);
		}
	}

	void gen() {
		cm = new JCodeModel();
		try {
			c = cm._class(metaData.getPackageName() + "."
					+ metaData.getTaskClassName());
			c._extends(metaData.getBaseTaskClazz());

			if (!isDialog) {
				JType pageClassJType = cm.ref(metaData.getAppobjClazz());
				pageField = c.field(JMod.PRIVATE, pageClassJType,
						metaData.getAppobjClassVarName(),
						JExpr._new(pageClassJType));
			}

			/*
			 * performChanges method
			 */
			genPerformMethod();
			/*
			 * Verify method
			 */
			genVerifyMethod();
			/*
			 * Edit method
			 */
			genEditMethod();

			if (isDialog) {
				/*
				 * verifyInPage method
				 */
				genVerifyInPageMethod();
				if (isNewEdit) {
					/*
					 * Create method
					 */
					genCreateMethod();

					/*
					 * Delete method
					 */
					genDeleteMethod();
				}
			}

			cm.build(destDir);
		} catch (Exception e) {
			throw new AutomationException(e);
		}
	}

	private void genDeleteMethod() throws ClassNotFoundException {
		JMethod deleteMethod = c.method(JMod.PUBLIC, cm.VOID,
				metaData.getDeleteMethodName());
		JBlock body = deleteMethod.body();
		JVar voParamVar = deleteMethod.param(cm.parseType("String[]"), metaData
				.getKeyField().getName() + "s");
		body.directStatement("//TODO: code to delete objects.");
		body.directStatement("//TODO: code to verify objects are really deleted.");
	}

	private void genVerifyInPageMethod() throws ClassNotFoundException {
		JMethod verifyInPageMethod = c.method(JMod.PUBLIC, cm.VOID,
				metaData.getVerifyInPageMethodName());
		JBlock body = verifyInPageMethod.body();
		JVar voParamVar = verifyInPageMethod.param(
				cm.parseType(metaData.getVoClassName()),
				metaData.getVoClassVarName());
		body.directStatement("//TODO: code to verify the creation/modification result in page.");
	}

	private void genCreateMethod() throws ClassNotFoundException {
		JMethod newMethod = c.method(JMod.PUBLIC, cm.VOID,
				metaData.getCreateMethodName());
		JBlock body = newMethod.body();
		if (isDialog) {
			JVar newDataParamVar = newMethod.param(
					cm.parseType(metaData.getVoClassName()), "newData");

			body.directStatement("//TODO: add the code to launch the dialogue.");
			JVar dlgVar = body.decl(
					cm._ref(metaData.getAppobjClazz()),
					metaData.getAppobjClassVarName(),
					JExpr._new(cm._ref(metaData.getAppobjClazz())).arg(
							JExpr.TRUE));

			JInvocation performInvoke = body.invoke("perform");
			performInvoke.arg(newDataParamVar);
			performInvoke.arg(JExpr._null());
			performInvoke.arg(dlgVar);

			body.invoke(metaData.getVerifyInPageMethodName()).arg(
					newDataParamVar);
		}
	}
}

class FieldMetaData {
	private String name;
	private String dataType;
	private String setDataMethodName;

	public FieldMetaData(String name, String dataType, String setDataMethodName) {
		super();
		this.name = name;
		this.dataType = dataType;
		this.setDataMethodName = setDataMethodName;
	}

	public String getSetDataMethodName() {
		return setDataMethodName;
	}

	public void setSetDataMethodName(String setDataMethodName) {
		this.setDataMethodName = setDataMethodName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

}
