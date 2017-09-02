package de.znews.server.uri;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

@Getter
@AllArgsConstructor
public class URIFragment
{
	
	public static URIFragment withContent(String content)
	{
		URIFragment result = new URIFragment();
		result.content = content;
		return result;
	}
	
	public static URIFragment fromURI(String uri)
	{
		if (uri.startsWith("/"))
			uri = uri.substring(1);
		return new URIFragment(uri);
	}
	
	protected String      content;
	protected URIFragment previous;
	protected URIFragment next;
	protected URIQuery    query;
	
	private URIFragment()
	{
	}
	
	private URIFragment(String s)
	{
		this(null, s);
	}
	
	private URIFragment(URIFragment previous, String s)
	{
		this.previous = previous;
		
		// The URI may contain a query
		String[] querySplit = s.split("\\?", 2);
		
		// The URI without the query
		s = querySplit[0];
		
		int nextIndex = s.indexOf('/');
		
		if (nextIndex == -1)
		{
			// This is the last fragment in the URI (i.e. no slashes after this)
			content = s;
			if (querySplit.length == 2)
				// There is a query
				query = URIQuery.fromString(querySplit[1]);
		}
		else
		{
			// There are more fragments after this
			content = s.substring(0, nextIndex);
			// We need to append the query string again (we removed it with the querySplit)
			next = new URIFragment(this, s.substring(nextIndex + 1) + (querySplit.length == 1 ? "" : '?' + querySplit[1]));
		}
		
	}
	
	@Deprecated
	public boolean hasNext()
	{
		return next != null;
	}
	
	/**
	 * Converts this URIFragment to a String, including
	 * all following fragments and the query string.
	 *
	 * @return A string representation of this URIFragment
	 */
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		toString(sb);
		return sb.toString();
	}
	
	private void toString(StringBuilder append)
	{
		append.append(content);
		if (next != null)
		{
			append.append('/');
			next.toString(append);
		}
		else if (query != null)
			append.append('?').append(query.toString());
	}
	
	/**
	 * Checks whether this fragment represents a variable.<br>
	 * <br>
	 * More formally, this method returns <code>true</code> if
	 * and only if {@link #getContent()} is enclosed in curly brackets.
	 *
	 * @return <code>content.startsWith("{") && content.endsWith("}")</code>
	 */
	public boolean isParam()
	{
		return content.startsWith("{") && content.endsWith("}");
	}
	
	/**
	 * This method can be used if {@link #isParam()} returns
	 * <code>true</code> to trim the content of the enclosing
	 * curly brackets.<br>
	 * <br>
	 * For example, if the content were <code>"{msg}"</code>, this method
	 * would return <code>"msg"</code>.
	 *
	 * @return {@link #getContent()} without enclosing curly brackets
	 */
	public String getAsParam()
	{
		return content.substring(1, content.length() - 1);
	}
	
	/**
	 * Checks whether this and all following fragments
	 * match a predicate.
	 *
	 * @param filter The filter
	 * @return Whether the filter matches this and all following fragments
	 */
	public boolean allMatch(Predicate<? super URIFragment> filter)
	{
		if (!filter.test(this))
			return false;
		URIFragment fragment = this;
		while ((fragment = fragment.next) != null)
			if (!filter.test(fragment))
				return false;
		return true;
	}
	
	/**
	 * Performs a given action for this and all
	 * following fragments.
	 *
	 * @param action The action
	 */
	public void forEach(Consumer<? super URIFragment> action)
	{
		action.accept(this);
		URIFragment fragment = this;
		while ((fragment = fragment.next) != null)
			action.accept(fragment);
	}
	
	/**
	 * Performs a given action for this and all
	 * following fragments.<br>
	 * In addition to just the fragment,
	 * the action is also called with the current index.
	 *
	 * @param action The action
	 */
	public void forEachIndexed(BiConsumer<? super Integer, ? super URIFragment> action)
	{
		int i = 0;
		action.accept(i, this);
		URIFragment fragment = this;
		while ((fragment = fragment.next) != null)
			action.accept(++i, fragment);
	}
	
	/**
	 * Compares this fragment to another fragment.<br>
	 * If the other fragment is a parameter, the content
	 * is not compared, and it is not ensured that this fragment
	 * is a parameter either.<br>
	 * <br>
	 * Note that this method is NOT symmetric
	 * (i.e. it is not guaranteed that for two non-<code>null</code>
	 * variables <code>x</code> and <code>y</code>, <code>x.equals(y) == y.equals(x)</code>).
	 *
	 * @param other The fragment to compare this one to
	 * @return Whether this fragment "matches" the other
	 */
	public boolean compare(URIFragment other)
	{
		return !(other == null || other.getContent().trim().isEmpty())
				&& (other.isParam()
				    // If this is a param, we don't need to check the content, we don't event need to check if this is a param as well
				    ? other.next == null && next == null || !(other.next == null || next == null) && next.compare(other.next)
				    // Otherwise, we do need to check for content equality
				    : other.content.equals(content) && (other.next == null && next == null || !(other.next == null || next == null) && next.compare(other.next)));
	}
	
	/**
	 * Gets the URIFragment at the specified index, relative
	 * to this fragment.
	 *
	 * @param index The index
	 * @return The URIFragment at <code>index</code>
	 */
	public URIFragment get(int index)
	{
		if (index == 0)
			return this;
		if (index < 0)
			throw new IndexOutOfBoundsException();
		
		URIFragment fragment = this;
		for (int i = 0; i < index; i++)
		{
			if (fragment.next == null)
				throw new IndexOutOfBoundsException();
			fragment = fragment.next;
		}
		
		return fragment;
		
	}
	
	/**
	 * Gets the query of this URIFragment.<br>
	 * If the query of this particular fragment is <code>null</code>
	 * and this is not the last fragment, the query of the next
	 * fragment is returned, etc.
	 *
	 * @return The URIQuery corresponding to this URIFragment
	 */
	public URIQuery getQuery()
	{
		if (query != null)
			return query;
		URIFragment fragment = this;
		while ((fragment = fragment.next) != null)
			if (fragment.query != null)
				return fragment.query;
		return null;
	}
	
	@Override
	public int hashCode()
	{
		return toString().hashCode();
	}
	
	@Override
	public boolean equals(Object obj)
	{
		return obj == this
				|| obj != null && obj.getClass() == getClass()
				&& obj.toString().equals(toString());  // It is sufficient to compare the strings
	}
	
}
