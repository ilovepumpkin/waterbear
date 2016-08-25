package org.waterbear.projects.common.testcases;

import static org.testng.AssertJUnit.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;

import org.waterbear.core.exception.AutomationException;
import org.waterbear.core.widgets.WebElement;
import org.waterbear.core.widgets.dijit.DojoWidget;


public class BaseAppobjsTest extends BaseTest {

	protected void testAppobjsClasses(Class clazz) {
		Object instance = null;
		Constructor[] constructors = clazz.getConstructors();

		for (int i = 0; i < constructors.length; i++) {
			Constructor constructor = constructors[i];
			Class[] paramTypes = constructor.getParameterTypes();

			try {
				if (paramTypes.length == 0) {
					instance = constructor.newInstance();
				} else if (paramTypes.length == 1) {
					instance = constructor.newInstance(Boolean.TRUE);
				} else {
					throw new AutomationException("The constrctor "
							+ constructor + " cannot be handled.");
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				log.error("Failed to construct " + clazz.getName(), e);
				throw new AutomationException(constructor.getName(), e);
			}
		}

		Method[] methods = clazz.getMethods();
		for (int i = 0; i < methods.length; i++) {
			Method method = methods[i];
			Class superClass = method.getReturnType().getSuperclass();
			if (superClass != null
					&& (superClass.equals(WebElement.class) || superClass
							.equals(DojoWidget.class))) {
				final String classMethodName = method.getName() + " ("
						+ clazz.getSimpleName() + ")";
				log.info(classMethodName);
				Class[] paramTypes = method.getParameterTypes();
				try {
					if (paramTypes.length == 0) {
						WebElement wid = (WebElement) method.invoke(instance);
						assertTrue(classMethodName,
								getBrowser().exists(wid.getElementStub()));
					} else if (paramTypes.length == 1) {
						WebElement wid = (WebElement) method.invoke(instance,
								(Object) null);
						assertTrue(classMethodName,
								getBrowser().exists(wid.getElementStub()));
					} else {
						throw new AutomationException("The method "
								+ classMethodName + " cannot be handled.");
					}
				} catch (Exception e) {
					log.error("Failed to test the method " + classMethodName, e);
					throw new AutomationException(classMethodName, e);
				}
			}
		}

	}

	@Override
	protected HashMap prepareTestData() {
		// TODO Auto-generated method stub
		return null;
	}

}
