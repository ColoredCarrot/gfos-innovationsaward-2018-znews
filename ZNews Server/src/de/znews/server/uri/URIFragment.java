package de.znews.server.uri;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.function.Consumer;
import java.util.function.Predicate;

@AllArgsConstructor
public class URIFragment
{
	
	public static URIFragment fromFragment(String fragment)
	{
		URIFragment result = new URIFragment();
		result.fragment = fragment;
		return result;
	}
	
	private String fragment;
	
	@Getter
	
	private URIFragment previous;
	@Getter
	
	public  URIFragment next;
	
	private URIQuery query;
	
	public URIFragment()
	{
	}
	
	public URIFragment(String s)
	{
		this(null, s);
	}
	
	public URIFragment(URIFragment previous, String s)
	{
		this.previous = previous;
		
		String[] querySplit = s.split("\\?", 2);
		
		s = querySplit[0];
		
		int nextIndex = s.indexOf('/');
		
		if (nextIndex == -1)
		{
			fragment = s;
			if (querySplit.length == 2)
				query = new URIQuery(querySplit[1]);
		}
		else
		{
			fragment = s.substring(0, nextIndex);
			next = new URIFragment(this, s.substring(nextIndex + 1) + (querySplit.length == 1 ? "" : '?' + querySplit[1]));
		}
		
	}
	
	public String get()
	{
		return fragment;
	}
	
	public boolean hasNext()
	{
		return next != null;
	}
	
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		toString(sb);
		return sb.toString();
	}
	
	private void toString(StringBuilder append)
	{
		append.append(fragment);
		if (next != null)
		{
			append.append('/');
			next.toString(append);
		}
		else if (query != null)
			append.append(query.toString());
	}
	
	public boolean isParam()
	{
		return fragment.startsWith("{") && fragment.endsWith("}");
	}
	
	
	public String getAsParam()
	{
		return fragment.substring(1, fragment.length() - 1);
	}
	
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
	
	public void forEach(Consumer<? super URIFragment> action)
	{
		action.accept(this);
		URIFragment fragment = this;
		while ((fragment = fragment.next) != null)
			action.accept(fragment);
	}
	
	public boolean compare(URIFragment other)
	{
		return !(other == null || other.get().trim().isEmpty()) && (other.isParam() ? other.next == null && next == null || !(other.next == null || next == null) && next.compare(other.next) : other.fragment
				.equals(fragment) && (other.next == null && next == null || !(other.next == null || next == null) && next.compare(other.next)));
	}
	
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
	
}
