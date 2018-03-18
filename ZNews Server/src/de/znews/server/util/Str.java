package de.znews.server.util;

import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;

public class Str implements CharSequence, Cloneable, Comparable<CharSequence>, Appendable
{
    
    public static Str copyOf(char[] data)
    {
        return new Str(Arrays.copyOf(data, data.length));
    }
    
    public static Str copyOf(char[] data, int offset, int length)
    {
        return new Str(Arrays.copyOfRange(data, offset, offset + length));
    }
    
    private char[]  buffer;
    private int     length;
    private Builder b;
    
    public Str(String source)
    {
        buffer = source.toCharArray();
        length = buffer.length;
    }
    
    public Str(char[] buffer)
    {
        this(buffer, buffer.length);
    }
    
    public Str(char[] buffer, int length)
    {
        this.buffer = buffer;
        this.length = length;
    }
    
    public Str(byte[] bytes)
    {
        this(new String(bytes));
    }
    
    public Str(byte[] bytes, Charset cs)
    {
        this(new String(bytes, cs));
    }
    
    public Str(int initBufferLen)
    {
        buffer = new char[initBufferLen];
        length = 0;
    }
    
    private Builder b()
    {
        if (b == null)
            b = new Builder();
        return b;
    }
    
    public char[] toCharArray()
    {
        return Arrays.copyOf(buffer, buffer.length);
    }
    
    public char[] getBuffer()
    {
        return buffer;
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
        
        System.arraycopy(chars, charsOffset, buffer, index, charsLen);
        
        return this;
        
    }
    
    public Str setChar(int index, char c)
    {
        ensureCapacity(length + 1);
        buffer[index] = c;
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
                result.add(String.valueOf(buffer, index, search.length));
        }
        else
        {
            result = new ArrayList<>();
            for (; index != -1; index = indexOf(search, index + search.length))
                result.add(String.valueOf(buffer, index, search.length));
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
                result.add(new Str(Arrays.copyOfRange(buffer, index, index + search.length)));
        }
        else
        {
            result = new ArrayList<>();
            for (; index != -1; index = indexOf(search, index + search.length))
                result.add(new Str(Arrays.copyOfRange(buffer, index, index + search.length)));
        }
        return result.toArray(new Str[result.size()]);
    }
    
    public Str shift(int index, int len, int amount)
    {
        // Assume ensureCapacity has already been called
        // Assume we need not care about the characters at the to-be-shifted-away-from positions
        System.arraycopy(buffer, index, buffer, index + amount, len);
        length += amount;
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
            if (search[0] == buffer[i])
            {
                // Match of first char of search at i
                for (int j = 1; j < search.length; j++)
                    if (buffer[i + j] != search[j])
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
        return buffer[index];
    }
    
    @Override
    public CharSequence subSequence(int start, int end)
    {
        // TODO: What would maybe be cool is to return a view of this Str, like List#subList
        //        We'd need to have an offset property in Str as well though... hmm.
        return new Str(Arrays.copyOfRange(buffer, start, end));
    }
    
    public Str ensureCapacity(int minCapacity)
    {
        if (buffer.length < minCapacity)
            setLength(minCapacity);
        return this;
    }
    
    public Str setLength(int newLength)
    {
        buffer = Arrays.copyOf(buffer, newLength);
        return this;
    }
    
    @NotNull
    @Override
    public String toString()
    {
        return new String(buffer);
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
    
    public Str copy()
    {
        return new Str(Arrays.copyOf(buffer, buffer.length), length);
    }
    
    public Str copyRange(int start, int end)
    {
        return new Str(Arrays.copyOfRange(buffer, start, end), length);
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
    
    @Override
    public Str append(CharSequence csq)
    {
        if (csq instanceof Str)
            b().append(((Str) csq).buffer);
        else
            b().append(csq.toString().toCharArray());
        return this;
    }
    
    @Override
    public Str append(CharSequence csq, int start, int end)
    {
        if (csq instanceof Str)
            b().append(((Str) csq).buffer, start, end - start);
        else
            b().append(csq.subSequence(start, end).toString().toCharArray());
        return this;
    }
    
    @Override
    public Str append(char c)
    {
        b().append(c);
        return this;
    }
    
    private class Builder
    {
        int writeIndex;
    
        void append(char c)
        {
            setChar(writeIndex++, c);
        }
    
        void append(char[] chars)
        {
            setChars(writeIndex, chars.length, chars);
            writeIndex += chars.length;
        }
    
        void append(char[] chars, int charsOffset, int charsLen)
        {
            setChars(writeIndex, charsLen, chars, charsOffset, charsLen);
            writeIndex += charsLen;
        }
    }
    
}
