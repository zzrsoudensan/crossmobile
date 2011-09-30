/* Copyright (c) 2011 by crossmobile.org
 *
 * CrossMobile is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 2.
 *
 * CrossMobile is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with CrossMobile; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.crossmobile.source.parser;

import java.util.ArrayList;
import java.util.List;

public class PStream {

    private final String buffer;
    private final int size;
    private int location;
    private List<String> tokens = new ArrayList<String>();

    private PStream(String buffer) {
        this.buffer = buffer;
        this.size = buffer.length();
        this.location = 0;

        while (location < size) {
            char c = buffer.charAt(location);
            if (!isSpace(c)) {
            }
        }
    }

    public static List<String> getTokens(String buffer) {
        if (buffer == null || buffer.isEmpty())
            return new ArrayList<String>();
        return new PStream(buffer).tokens;
    }

    private boolean isAlpha() {
        char c = buffer.charAt(location);
        return (c >= 'a' && c <= 'z' && c >= 'A' && c <= 'Z' && c == '_');
    }

    private boolean isAlphaNum() {
        if (isAlpha())
            return true;
        char c = buffer.charAt(location);
        return (c >= 0 && c <= 9);
    }

    private boolean isSpace(char c) {
        return c == ' ' || c == '\n' || c == '\r' || c == '\t' || c == '\f';
    }

    private boolean isOpenParenthesis(char c) {
        return c == '(';
    }

    private boolean isCloseParenthesis(char c) {
        return c == ')';
    }

    private boolean isOpenBracket(char c) {
        return c == '{';
    }

    private boolean isCloseBracket(char c) {
        return c == '}';
    }

    private boolean isSemicolon(char c) {
        return c == ';';
    }

    private boolean isComma(char c) {
        return c == ',';
    }
}
