/** start_Java_Source_User_Copyright_Notice
 IBM Confidential

 OCO Source Materials

 5639-VC6 5639-VM1

 Copyright IBM Corp. 2010, 2011

 The source code for this program is not published or other-
 wise divested of its trade secrets, irrespective of what has
 been deposited with the U.S. Copyright Office.
 end_Java_Source_User_Copyright_Notice */
package org.waterbear.core.msg;

import java.text.MessageFormat;
import java.util.Locale;

public class FixMessageFormat {

    private static final char CURLY_BRACE_LEFT     = '{';

    private static final char CURLY_BRACE_RIGHT    = '}';

    public static final String       sccsId  = "%Z%%M%       %I%  %W% %G% %U%";

    private static final char SINGLE_QUOTE         = '\''; // @01A3

    private static final int  STATE_INITIAL        = 0;   // @01A4
    private static final int  STATE_LITHERAL_BRACE = 2;
    private static final int  STATE_MSG_ELEMENT    = 3;

    private static final int  STATE_SINGLE_QUOTE   = 1;
    private static String fixPattern(String pattern) { // @01
        StringBuffer buf = new StringBuffer(pattern.length() * 2);
        int state = STATE_INITIAL;
        for (int i = 0, j = pattern.length(); i < j; ++i) {
            char c = pattern.charAt(i);
            switch (state) {
                case STATE_INITIAL:
                    switch (c) {
                        case SINGLE_QUOTE:
                            state = STATE_SINGLE_QUOTE;
                            break;
                        case CURLY_BRACE_LEFT:
                            state = STATE_MSG_ELEMENT;
                            break;
                    }
                    break;
                case STATE_SINGLE_QUOTE:
                    switch (c) {
                        case SINGLE_QUOTE:
                            state = STATE_INITIAL;
                            break;
                        case CURLY_BRACE_LEFT:
                        case CURLY_BRACE_RIGHT:
                            state = STATE_LITHERAL_BRACE;
                            break;
                        default:
                            buf.append(SINGLE_QUOTE);
                            state = STATE_INITIAL;
                    }
                    break;
                case STATE_LITHERAL_BRACE:
                    switch (c) {
                        case SINGLE_QUOTE:
                            state = STATE_INITIAL;
                            break;
                    }
                    break;
                case STATE_MSG_ELEMENT:
                    switch (c) {
                        case CURLY_BRACE_RIGHT:
                            state = STATE_INITIAL;
                            break;
                    }
                    break;
                default: // This will not going to be happen.
            }
            buf.append(c);
        }
        // End of scan
        if (state == STATE_SINGLE_QUOTE) {
            buf.append(SINGLE_QUOTE);
        }
        return new String(buf);
    }
    protected java.text.MessageFormat format_ = null;
    public FixMessageFormat(String pattern, Locale locale) {
        format_ = new MessageFormat(pattern);
        format_.setLocale(locale);
        format_.applyPattern(fixPattern(pattern));
    }

    public final String format(Object[] args) {
        return format_.format(args);
    }
}
