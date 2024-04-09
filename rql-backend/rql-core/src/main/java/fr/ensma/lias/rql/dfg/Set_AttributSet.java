package fr.ensma.lias.rql.dfg;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * @author Gouriou Benjamin
 */
public class Set_AttributSet {

	private ArrayList<AttributSet> m_sos;

	public Set_AttributSet() {
		m_sos = new ArrayList<AttributSet>();
	}

	public Set_AttributSet(Set_AttributSet inSet) {
		this.clone(inSet);
	}

	public final void clone(Object o) {
		if (o instanceof Set_AttributSet) {
			Set_AttributSet obj = (Set_AttributSet) o;
			this.m_sos.clear();
			Iterator<AttributSet> iter = obj.m_sos.iterator();
			while (iter.hasNext())
				this.m_sos.add(new AttributSet(iter.next()));
		}
	}

	public void clear() {
		m_sos.clear();
	}

	boolean empty() {
		return m_sos.isEmpty();
	}

	int size() {
		return m_sos.size();
	}

	void push_back(AttributSet elt) {
		m_sos.add(elt);
	}

	void eraseElmtAt(int index) {
		m_sos.remove(index);
		/*
		 * Iterator<AttributSet> iter = m_sos.iterator(); int icpt=0; while(icpt<=index)
		 * { iter.next(); icpt++; }
		 * 
		 * iter.remove();
		 */
	}

	@Override
	public String toString() {
		StringBuilder tmp = new StringBuilder("{");
		Iterator<AttributSet> iter = this.m_sos.iterator();
		while (iter.hasNext()) {
			tmp.append("(").append(iter.next().toString()).append(") ");
		}
		tmp.append("}\n");
		return tmp.toString();
	}

	/******** Insert un attribut maximal ********/
	void InsertIfThisSetMaximal(AttributSet X) {
		boolean encore = true;
		boolean premier = true;
		int size = m_sos.size();
		for (int i = 0; i < size; i++) {
			AttributSet current = m_sos.get(i);
			if (X.is_subset(current)) {
				encore = false;
				return;
			} else {
				if (current.is_strictsubset(X)) {
					if (premier) {
						m_sos.set(i, X);
						premier = false;
					} else {
						eraseElmtAt(i);
						i--;
						size--;
					}
				}
			}
		}

		if (encore && premier) {
			m_sos.add(X);
		}
	}

	/********
	 * Renvoie true si l'attributSet S est inclus dans un élément du setofSet
	 ********/
	boolean est_inclus(AttributSet S) {
		for (int i = 0; i < m_sos.size(); i++) {
			if (S.is_subset(m_sos.get(i))) {
				return true;
			}
		}
		return false;
	}

	/********
	 * Compte le nb de fois qu'apparait un attributSet S dans le setofSet
	 ********/
	// A REFAIRE EN NE FAISANT QU'UN SEUL PARCOURS DE M_SOS
	int comptage(AttributSet S) {
		int cpt = 0;
		for (int i = 0; i < m_sos.size(); i++) {
			AttributSet current = m_sos.get(i);
			if (S.is_subset(current))
				cpt += current.getM_count();
		}
		return cpt;
	}

	int new_comptage(AttributSet S, int rightPart, MutableInt cptRightPart) {
		int cpt = 0;
		for (int i = 0; i < m_sos.size(); i++) {
			AttributSet current = m_sos.get(i);
			// cpt+=current.containsSubSetAndRightPart(S,rightPart,cptRightPart);
			cpt += current.containsSubSetAndRightPartStd(S, rightPart, cptRightPart);
		}
		return cpt;
	}

	/**** GETTERS AND SETTERS ****/
	public ArrayList<AttributSet> getM_sos() {
		return m_sos;
	}

}
