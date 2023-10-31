package com.ethlo.qjc;

/*-
 * #%L
 * qjc-core
 * %%
 * Copyright (C) 2018 - 2020 Morten Haraldsen (ethlo)
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.util.Collection;
import java.util.Iterator;

public class StringUtils
{
    public static String collectionToCommaDelimitedString(final Collection<?> collection)
    {
        return collectionToDelimitedString(collection, ",");
    }

    public static boolean isEmpty(final String value)
    {
        return value == null || value.trim().length() == 0;
    }

    public static String collectionToDelimitedString(final Collection<?> collection, final String separator)
    {
        final StringBuilder sb = new StringBuilder();
        final Iterator<?> it = collection.iterator();

        while (it.hasNext())
        {
            sb.append(it.next());
            if (it.hasNext())
            {
                sb.append(separator);
            }
        }
        return sb.toString();
    }
}
