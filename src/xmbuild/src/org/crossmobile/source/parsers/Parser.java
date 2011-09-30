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

package org.crossmobile.source.parsers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Parser<P> {

    public String parse(P parent, String entry) {
        Matcher m = getPattern().matcher(entry.toString());
        while (m.find())
            match(parent, m);
        return m.replaceAll("");
    }

    protected abstract Pattern getPattern();

    protected abstract void match(P parent, Matcher m);
}
