package de.znews.server.util;

import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;

public class Str implements CharSequence, Cloneable, Comparable<CharSequence>
{
    
    public static Str copyOf(char[] data)
    {
        return new Str(Arrays.copyOf(data, data.length));
    }
    
    public static Str copyOf(char[] data, int offset, int length)
    {
        return new Str(Arrays.copyOfRange(data, offset, offset + length));
    }
    
    private char[] data;
    private int    length;
    
    public Str(String source)
    {
        data = source.toCharArray();
        length = data.length;
    }
    
    public Str(char[] mut)
    {
        this(mut, mut.length);
    }
    
    public Str(char[] mut, int length)
    {
        this.data = mut;
        this.length = length;
    }
    
    public Str replaceOnce(CharSequence search, CharSequence replacement)
    {
        return replaceOnce(search.toString().toCharArray(), replacement.toString().toCharArray());
    }
    
    public Str replaceOnce(char[] search, char[] replacement)
    {
        int index = indexOf(search);
        if (index != -1)
            setChars(index, search.length, replacement);
        return this;
    }
    
    public Str replace(CharSequence search, CharSequence replacement)
    {
        return replace(search, replacement, -1);
    }
    
    public Str replace(CharSequence search, CharSequence replacement, int limit)
    {
        return replace(search.toString().toCharArray(), replacement.toString().toCharArray(), limit);
    }
    
    /**
     * Replaces <i>search</i> with <i>replacement</i> up to <i>limit</i> times in this Str.<br>
     * This replace method operates roughly 5 times faster than {@link String#replace(CharSequence, CharSequence)}.
     *
     * @param search      The string to be replaced
     * @param replacement The string to replace <i>search</i>
     * @param limit       The maximum number of replacements. Set to <i>-1</i> for no limit
     * @return This Str
     */
    public Str replace(char[] search, char[] replacement, int limit)
    {
        if (limit > 0)
        {
            // We know how many replacements we will perform at most,
            // so we can ensure a reasonable capacity, assuming limit
            // is not just a randomly chosen number
            int incr = replacement.length - search.length;
            if (incr < 0)
                incr = 0;
            ensureCapacity(length + incr * limit);
            
            int index, offset = 0, i = 0;
            while (i++ <= limit && (index = indexOf(search, offset)) != -1)
                setChars(index, search.length, replacement);
        }
        else if (limit != 0)
        {
            int index, offset = 0;
            while ((index = indexOf(search, offset)) != -1)
                setChars(index, search.length, replacement);
        }
        return this;
    }
    
    public Str setChars(int index, int len, CharSequence chars)
    {
        return setChars(index, len, chars.toString().toCharArray());
    }
    
    public Str setChars(int index, int len, char[] chars)
    {
        return setChars(index, len, chars, 0, chars.length);
    }
    
    public Str setChars(int index, int len, char[] chars, int charsOffset, int charsLen)
    {
        ensureCapacity(length - len + charsLen);
        
        if (charsLen != len)
        {
            // Shift characters outside len right by (charsLen - len)
            // This also works for charsLen < len, btw
            shift(index + len,  /* To the right of index + len */
                    length() - (index + len),  /* All characters from that point */
                    charsLen - len  /* Shift by the difference between charsLen and len to make room */);
        }
        
        System.arraycopy(chars, charsOffset, data, index, charsLen);
        
        return this;
        
    }
    
    
    
    public String[] splitString(char[] search, int limit)
    {
        // Assume split.length >= 1
        // Assume limit is a sanely chosen value
        
        int index = indexOf(search);
        if (index == -1)
            // No match
            return new String[] { this.toString() };
        
        ArrayList<String> result;
        if (limit >= 0)
        {
            result = new ArrayList<>(limit);
            for (int i = 0; index != -1 && i++ < limit; index = indexOf(search, index + search.length))
                result.add(String.valueOf(data, index, search.length));
        }
        else
        {
            result = new ArrayList<>();
            for (; index != -1; index = indexOf(search, index + search.length))
                result.add(String.valueOf(data, index, search.length));
        }
        
        return result.toArray(new String[result.size()]);
        
    }
    
    public Str[] split(char[] search, int limit)
    {
        // Assume split.length >= 1
        // Assume limit is a sanely chosen value
    
        int index = indexOf(search);
        if (index == -1)
            // No match
            return new Str[] { this.clone() };
        
        ArrayList<Str> result;
        if (limit >= 0)
        {
            result = new ArrayList<>(limit);
            for (int i = 0; index != -1 && i++ < limit; index = indexOf(search, index + search.length))
                result.add(new Str(Arrays.copyOfRange(data, index, index + search.length)));
        }
        else
        {
            result = new ArrayList<>();
            for (; index != -1; index = indexOf(search, index + search.length))
                result.add(new Str(Arrays.copyOfRange(data, index, index + search.length)));
        }
        return result.toArray(new Str[result.size()]);
    }
    
    public Str shift(int index, int len, int amount)
    {
        // Assume ensureCapacity has already been called
        // Assume we need not care about the characters at the to-be-shifted-away-from positions
        System.arraycopy(data, index, data, index + amount, len);
        return this;
    }
    
    public int indexOf(CharSequence search)
    {
        return indexOf(search.toString().toCharArray(), 0);
    }
    
    public int indexOf(char[] search)
    {
        return indexOf(search, 0);
    }
    
    public int indexOf(char[] search, int startSearchAt)
    {
        outer:
        for (int i = startSearchAt; i < length - search.length + 1; i++)
        {
            if (search[0] == data[i])
            {
                // Match of first char of search at i
                for (int j = 1; j < search.length; j++)
                    if (data[i + j] != search[j])
                        continue outer;
                // Everything matches!
                return i;
            }
        }
        return -1;
    }
    
    @Override
    public int length()
    {
        return length;
    }
    
    @Override
    public char charAt(int index)
    {
        return data[index];
    }
    
    @Override
    public CharSequence subSequence(int start, int end)
    {
        // TODO: What would maybe be cool is to return a view of this Str, like List#subList
        //        We'd need to have an offset property in Str as well though... hmm.
        return new Str(Arrays.copyOfRange(data, start, end));
    }
    
    public Str ensureCapacity(int minCapacity)
    {
        if (data.length < minCapacity)
            setLength(minCapacity);
        return this;
    }
    
    public Str setLength(int newLength)
    {
        data = Arrays.copyOf(data, newLength);
        return this;
    }
    
    @NotNull
    @Override
    public String toString()
    {
        return new String(data);
    }
    
    public StringBuilder toStringBuilder()
    {
        return new StringBuilder(toString());
    }
    
    @Override
    public int compareTo(@Nonnull CharSequence o)
    {
        return toString().compareTo(o.toString());
    }
    
    public int compareToIgnoreCase(@Nonnull CharSequence o)
    {
        return toString().compareToIgnoreCase(o.toString());
    }
    
    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public Str clone()
    {
        return new Str(toString());
    }
    
    @Override
    public int hashCode()
    {
        return toString().hashCode() + 23;
    }
    
    @Override
    public boolean equals(Object obj)
    {
        return obj == this
                || obj != null && obj.getClass() == getClass() && obj.toString().equals(toString());
    }
    
}
