package bbs.util;

/**
 * 
 * @author nishio
 *
 * @param <First>
 * @param <Second>
 */
public class Pair<First, Second>{

	private First a;
	private Second b;
	
	/**
	 * 
	 * @param a
	 * @param b
	 */
	public Pair(First a, Second b) {
		this.a = a;
		this.b = b;
	}
	
	/**
	 * 
	 * @param a
	 */
	public void setFirst(First a) {
		this.a = a;
	}
	
	/**
	 * 
	 * @param b
	 */
	public void setSecond(Second b) {
		this.b = b;
	}

	/**
	 * 
	 * @return
	 */
	public First getFirst() {
		return a;
	}

	/**
	 * 
	 * @return
	 */
	public Second getSecond() {
		return b;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((a == null) ? 0 : a.hashCode());
		result = prime * result + ((b == null) ? 0 : b.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Pair))
			return false;
		Pair other = (Pair) obj;
		if (a == null) {
			if (other.a != null)
				return false;
		} else if (!a.equals(other.a))
			return false;
		if (b == null) {
			if (other.b != null)
				return false;
		} else if (!b.equals(other.b))
			return false;
		return true;
	}
}

