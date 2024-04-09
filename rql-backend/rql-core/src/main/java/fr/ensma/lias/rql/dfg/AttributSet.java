package fr.ensma.lias.rql.dfg;

import java.util.Iterator;
import java.util.TreeSet;

/**
 * @author Benjamin GOURIOU
 */
public class AttributSet {

	private TreeSet<Integer> m_at;

	private int m_count;

	public AttributSet() {
		this.m_count = 1;
	}

	public AttributSet(TreeSet<Integer> at) {
		this.m_count = 1;
		m_at = at;
	}

	public AttributSet(String line) {
		m_count = 1;
		String val[] = line.split(" ");
		m_at = new TreeSet<Integer>();
		for (String curr : val)
			m_at.add(Integer.valueOf(curr));
	}

	public AttributSet(AttributSet inAttr) {
		this.m_count = inAttr.m_count;
		this.m_at = (TreeSet<Integer>) inAttr.m_at.clone();
	}

	public void clear() {
		m_at.clear();
	}

	public void insert(int x) {
		m_at.add(x);
	}

	public boolean empty() {
		return m_at.isEmpty();
	}

	public int size() {
		return m_at.size();
	}

	public boolean find(int valeur) {
		return (m_at.contains(valeur));
	}

	public void erase(int v) {
		m_at.remove(v);
	}

	public void incr() {
		m_count++;
	}

	@Override
	public String toString() {
		StringBuilder tmp = new StringBuilder("{");
		int size = m_at.size();
		if (size > 0) {
			/*
			 * for (int i=0; i<size; i++) tmp.append(m_at.get(i)).append(",");
			 */
			Iterator<Integer> iter = m_at.iterator();
			while (iter.hasNext())
				tmp.append(iter.next()).append(",");
			tmp.replace(tmp.length() - 1, tmp.length(), "}, ");
		} else
			tmp.append("}, ");

		tmp.append(this.m_count);
		return tmp.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof AttributSet) {
			AttributSet var = (AttributSet) obj;
			return this.is_equal(var);
		}
		return false;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 17 * hash + (this.m_at != null ? this.m_at.hashCode() : 0);
		hash = 17 * hash + this.m_count;
		return hash;
	}

	/********
	 * Renvoie true si l'attributSet X est un sous-ensemble strict Y
	 ********/
	boolean is_strictsubset(AttributSet Y) {
		if ((m_at.size()) >= Y.size())
			return false;
		else
			return (Y.m_at.containsAll(this.m_at));
	}

	/******** Renvoie true si l'attributSet X est inclus dans Y ********/
	boolean is_subset(AttributSet Y) {
		return (Y.m_at.containsAll(this.m_at));
	}

	int containsSubSet(AttributSet Y) {
		// int curAttr=0;
		Iterator<Integer> iter = m_at.iterator();
		Iterator<Integer> iterY = Y.m_at.iterator();
		int curr = -1, currY = iterY.next();
		while (iterY.hasNext()) {
			while (iter.hasNext() && ((curr = iter.next()) < currY)) {
			}

			if (currY != curr)
				return 0;
			else
				iterY.next();
		}
		return 1;
	}

	/* don't use, prefer the function based on the std methods, below */
	int containsSubSetAndRightPart(AttributSet Y, int rhs, MutableInt countRightPart) {
		boolean foundRightPart = false;
		Iterator<Integer> iter = m_at.iterator();
		Iterator<Integer> iterY = Y.m_at.iterator();
		int curr = -1, currY;
		while (iterY.hasNext()) {
			currY = iterY.next();

			while (iter.hasNext() && ((curr = iter.next()) < currY))
				if (curr == rhs)
					foundRightPart = true;

			if (currY != curr)
				return 0;
			else if (curr == rhs)
				foundRightPart = true;
		}
		if (foundRightPart)
			countRightPart.add(this.m_count); // update of right part cpt if
		// found
		else // if not found maybe the right part is greater than the last value
				// checked in the loop
		{
			while (iter.hasNext() && ((curr = iter.next()) < rhs)) {
			} // so check the rest
			if (curr == rhs)
				countRightPart.add(this.m_count);
		}

		return m_count;
	}

	/*
	 * Check that the attribuSet contains or not the attributset Y. Return m_count
	 * if it contains it nor 0
	 */
	int containsSubSetAndRightPartStd(AttributSet Y, int rhs, MutableInt countRightPart) {
		boolean foundleftPart = this.m_at.containsAll(Y.m_at);
		if (foundleftPart) {
			if (this.m_at.contains(rhs))
				countRightPart.add(this.m_count);
			return m_count;
		}

		return 0;
	}

	/******** Renvoie true si l'attributSet X contient Y ********/
	boolean is_superset(AttributSet Y) {
		return (this.m_at.containsAll(Y.m_at));
	}

	/******** Renvoie true si l'attributSet X égal Y ********/
	boolean is_equal(AttributSet Y) {
		int size = 0;
		if ((size = this.m_at.size()) == Y.size()) {
			/*
			 * for (int i=0; i<size; i++) if (!Y.m_at.contains(this.m_at.get(i)))
			 */
			Iterator<Integer> iter = m_at.iterator();
			while (iter.hasNext())
				if (!Y.m_at.contains(iter.next()))
					return false;
		} else
			return false;

		return true;
	}

	/*** GETTERS AND SETTERS ***/
	public int getM_count() {
		return m_count;
	}

	public void setM_count(int m_count) {
		this.m_count = m_count;
	}

	public TreeSet<Integer> getM_at() {
		return m_at;
	}

	public void setM_at(TreeSet<Integer> m_at) {
		this.m_at = m_at;
	}

}
