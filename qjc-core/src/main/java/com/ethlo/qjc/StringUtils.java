package com.ethlo.qjc;

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
