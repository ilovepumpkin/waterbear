/** start_Java_Source_User_Copyright_Notice
 IBM Confidential

 OCO Source Materials

 5639-VC6 5639-VM1

 Copyright IBM Corp. 2009, 2011

 The source code for this program is not published or other-
 wise divested of its trade secrets, irrespective of what has
 been deposited with the U.S. Copyright Office.
 end_Java_Source_User_Copyright_Notice */
package org.waterbear.core.msg;

import java.util.Locale;
import java.util.ResourceBundle;

import org.waterbear.core.msg.Bundles.Bundle;


public class TextResource {
    
    private Object[] arguments;
    
    private Bundle bundle;
    
    private String key;
    
    public TextResource(Bundle bundle, String key, Object ... arguments) {
        this.bundle = bundle;
        this.key = key;
        this.arguments = arguments;
    }
    
    @Override public String toString() {
        return this.toString(Locale.getDefault());
    }
    
    public String toString(Locale locale) {    
        String result = "";
        ResourceBundle b = Bundles.getBundle(bundle, locale);
        result = b.getString(key);
        if(arguments != null && arguments.length > 0) {
            FixMessageFormat fmf = new FixMessageFormat(result, locale);            
            result = fmf.format(arguments);
        }
        return result;
    }
}
