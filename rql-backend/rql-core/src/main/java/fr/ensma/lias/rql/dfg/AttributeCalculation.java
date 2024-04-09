package fr.ensma.lias.rql.dfg;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

import fr.ensma.lias.rql.dfg.KnownException;
import fr.ensma.lias.rql.dfg.Set_AttributSet;
import fr.ensma.lias.rql.dfg.ThreadRunException;

/**
 * @author Benjamin GOURIOU
 */
public class AttributeCalculation implements Runnable {

	private Set_AttributSet m_inf; // Inf

	private Set_AttributSet m_cinf; // Complementaires de Inf

	private Set_AttributSet m_lhs; // lhs

	private AttributSet m_rhs; // rhs

	private Dfg m_appli;

	private int m_supp[];

	private Set_AttributSet m_base;

	private int m_A;

	private File m_fileCinf = null;

	private File m_fileRet = null;

	private BufferedReader m_reader = null;

	private BufferedWriter m_writer = null;

	private Process m_process = null;

	private long calculInfTime = 0;

	private long concatRulesCGLTime = 0;

	private long calculCInfTime = 0;

	private long calculLhsUnoTime = 0;

	private long writeRulesCCTime = 0;

	private double supportSeuil = 0;

	private double confidenceSeuil = 0;

	public AttributeCalculation(Dfg appli, int A, double support, double confidence) {
		m_inf = new Set_AttributSet();
		m_cinf = new Set_AttributSet();
		m_lhs = new Set_AttributSet();
		this.supportSeuil = support;
		this.confidenceSeuil = confidence;
		m_appli = appli;
		m_supp = appli.m_supp;
		m_base = appli.m_base;
		m_A = A;
	}

	@Override
	public void run() {
		try {
			CalculRegles();
			m_appli.addTimes(calculInfTime, concatRulesCGLTime, calculCInfTime, calculLhsUnoTime, writeRulesCCTime);
		} catch (Exception ex) {
			ex.printStackTrace();
			ThreadRunException exception = new ThreadRunException(ex, this.m_A);
			this.m_appli.setThreadException(exception);
			throw exception;
		}
	}

	/**
	 * Calcul des regles pour chaque attribut central A
	 * 
	 * @throws Exception
	 */
	void CalculRegles() throws Exception {
		long start = System.currentTimeMillis();
		calculInf();
		long end = System.currentTimeMillis();
		calculInfTime = end - start;
		start = end;

		// insertion des regles approximatives
		StringBuilder builder = concatRulesCGL();
		m_appli.writeInStream(builder);
		builder = null;
		end = System.currentTimeMillis();
		concatRulesCGLTime = end - start;
		start = end;

		// System.out.println("Calcul de CInf(A) : complémentaires de Inf(A)");
		StringBuilder sbCinf = calculCInf();
		m_inf.clear();
		end = System.currentTimeMillis();
		calculCInfTime = end - start;
		start = end;

		// System.out.println("Calcul de lhs(A) : transmin, parties gauches des
		// règles");
		/*
		 * double t1 = System.currentTimeMillis();
		 */
		try {
			this.calculLhsUnoTakeaki(sbCinf);
		} catch (KnownException ex) {
			throw ex;
		} catch (Exception ex) {
			throw new Exception("Lhs_Uno : Unknown exception caught caused the fail of left parts rules generation.");
		}
		end = System.currentTimeMillis();
		calculLhsUnoTime = end - start;
		start = end;
		this.writeRulesCC();
		end = System.currentTimeMillis();
		writeRulesCCTime = end - start;
		start = end;

	}

	/********
	 * Calcul de Inf(A) : plus grds elements de "m_base" ne contenant pas A
	 ********/
	void calculInf() {
		ArrayList<AttributSet> tmpArray = m_base.getM_sos();
		for (int i = 0; i < tmpArray.size(); i++) {
			AttributSet current = tmpArray.get(i);
			if (!current.find(m_A))
				m_inf.InsertIfThisSetMaximal(current);
		}
	}

	/******** FIN ********/

	/******** Calcul de CInf(A) : complémentaires de Inf(A) ********/
	private StringBuilder calculCInf() {

		StringBuilder builder = new StringBuilder("");

		ArrayList<Integer> complementaire = new ArrayList<Integer>();
		int nbattr = m_appli.getM_nbattr();
		for (int i = 0; i < nbattr; i++) {
			complementaire.add(i);
		}

		// R1 : RESTRICTION pour éviter les règles du type A -> A
		complementaire.remove(new Integer(m_A));

		// Pour tout élément de m_inf
		for (int i = 0; i < m_inf.size(); i++) {
			TreeSet<Integer> tmpAl = new TreeSet<Integer>();
			TreeSet<Integer> current = m_inf.getM_sos().get(i).getM_at();

			for (int iComp = 0; iComp < complementaire.size(); iComp++) {
				Integer curr = complementaire.get(iComp);
				if (!current.contains(curr))
					tmpAl.add(curr);
			}

			Iterator<Integer> iter = tmpAl.iterator();
			while (iter.hasNext())
				builder.append(iter.next()).append(' ');
			builder.append('\n');
		}

		return builder;
	}

	/******** FIN ********/

	/******** Calcul de lhs(A) : transmin, parties gauches des règles ********/
	void calculLhsUnoTakeaki(StringBuilder sbuild) throws Exception {

		AttributSet transversal;

		m_lhs.clear();

		if (sbuild.length() != 0) {
			long thread = Thread.currentThread().getId();

			m_fileCinf = new File(new File(m_appli.getM_fileOut()).getParentFile().getCanonicalPath() + File.separator
					+ "temp_mcinf_" + thread + ".dat");
			String fileOut = new File(m_appli.getM_fileOut()).getParentFile().getCanonicalPath() + File.separator
					+ "retLHS_" + thread + ".txt";

			m_fileRet = new File(fileOut);
			FileWriter fwrit = null;
			try {
				fwrit = new FileWriter(m_fileCinf, false);
				m_writer = new BufferedWriter(fwrit);
			} catch (IOException ex) {
				throw new KnownException("Lhs_Uno : Impossible to create the cinf file, incorrect path.");
			}

			try {
				m_writer.write(sbuild.toString());
				m_writer.flush();
				m_writer.close();
				fwrit.close();
			} catch (IOException ex) {
				m_fileCinf.delete();
				throw new KnownException("Lhs_Uno : Error during the writing of cinf data in the file.");
			}

			try {
				String params[] = { m_appli.getM_exePath(), "0", m_fileCinf.getAbsolutePath(), fileOut, "base",
						m_appli.getM_fileIn(), (m_appli.isM_lineNbInFile() ? "" : ("" + m_appli.getM_nbligne())) };

				System.out.println(params);
				m_process = Runtime.getRuntime().exec(params);

				m_process.waitFor();

			} catch (IOException ex) {
				throw new KnownException("shd : Execution of shd has failed, error during the execution.");
			}

			FileReader fread = null;
			try {
				fread = new FileReader(fileOut);
				m_reader = new BufferedReader(fread);

				String line;
				while ((line = m_reader.readLine()) != null) {
					AttributSet atr = new AttributSet(line);
					m_lhs.push_back(atr);
				}

				m_reader.close();
				fread.close();
				m_fileRet.delete();
				m_fileCinf.delete();

			} catch (IOException ex) {
				throw new KnownException("Lhs_Uno : Error during the reading of shd's left parts rules.");
			}

		} else {
			transversal = new AttributSet(new TreeSet<Integer>());
			m_lhs.push_back(transversal);
		}

	}

	/******** Calcul de rhs(A) : parties droites des règles ********/
	void calculRhs() {
		// Intersection des éléments de la base qui contiennent A
		// this.m_rhs.clear();
		boolean first = true;

		for (int it1 = 0; it1 < m_base.getM_sos().size(); it1++) {
			AttributSet curIt1 = m_base.getM_sos().get(it1);
			// Si l'attribut A est inclu dans l'element de la base
			if (curIt1.find(m_A)) {
				// Premier élément trouvé
				if (first == true) {
					Iterator<Integer> iter = curIt1.getM_at().iterator();
					while (iter.hasNext()) {
						Integer val = iter.next();
						// si l'attribut n'a pas déjà été traité
						if (!(m_appli.m_centr[val] == true))
							m_rhs.insert(val);
					}
					first = false;
				} else {
					// On insert l'element de la base, dans m_union_rhs
					TreeSet<Integer> tmpArray = new TreeSet<Integer>();
					for (Integer currentInt : m_rhs.getM_at()) {
						if (curIt1.getM_at().contains(currentInt))
							tmpArray.add(currentInt);
					}
					m_rhs.clear();
					for (Integer currentInt : tmpArray)
						m_rhs.insert(currentInt);
				}
			}
		}
		// R2 : RESTRICTION pour éviter les règles du type A -> A
		m_rhs.erase(m_A);
	}

	public StringBuilder concatRulesCGL() {
		// CGL
		StringBuilder sbRules = new StringBuilder("");
		int supA = m_supp[m_A];
		int nbligne = m_appli.getM_nbligne();
		int taille_max = m_appli.getM_taille_max();

		for (int iMinf = 0; iMinf < m_inf.size(); iMinf++) {
			AttributSet curMinf = m_inf.getM_sos().get(iMinf);
			if (curMinf.size() == 0) {
				// sbRules.append("-1\t").append(m_A).append('\t');
				sbRules.append("-1 =>\t").append(m_A).append('\t');
				sbRules.append(nbligne).append('\t').append(nbligne).append('\t');
				sbRules.append(supA).append('\t').append(supA).append('\n');
			} else if (curMinf.size() <= taille_max) {
				AttributSet lhs_rhs = new AttributSet(curMinf);
				lhs_rhs.insert(m_A);

				int supXY = m_base.comptage(lhs_rhs);
				int supX = m_base.comptage(curMinf);
				double confidenceXY;
				if (supX != 0) {
					confidenceXY = (double) supXY / (double) supX;
				} else {
					confidenceXY = 0;
				}
				System.out.println("confidence:" + confidenceXY);

				// R4 : RESTRICTION aux parties gauches telles que suppX > 0 // supX != 0
				if (supXY != 0 && ((double) supXY / nbligne) >= supportSeuil && confidenceXY != 0
						&& confidenceXY >= confidenceSeuil) {
					for (Integer curVal : curMinf.getM_at())
						sbRules.append(curVal).append(' ');
					sbRules.append("=>\t").append(m_A).append('\t').append(nbligne).append('\t');
					sbRules.append(supX).append('\t').append(supA).append('\t').append(supXY).append('\n');
				}
			}
		}

		return sbRules;
	}

	public void writeRulesCC() throws Exception {
		// CC
		// lhs => A
		StringBuilder sbRules = new StringBuilder("");
		int supA = m_supp[m_A];
		int cpt = 50000;
		int nbligne = m_appli.getM_nbligne();
		int taille_max = m_appli.getM_taille_max();

		for (int iMlhs = 0; iMlhs < m_lhs.size(); iMlhs++) {
			AttributSet curMlhs = m_lhs.getM_sos().get(iMlhs);
			if (curMlhs.size() == 0) {
				// sbRules.append("-1\t").append(m_A).append('\t');
				sbRules.append("-1 =>\t").append(m_A).append('\t');
				sbRules.append(nbligne).append('\t').append(nbligne).append('\t');
				sbRules.append(supA).append('\t').append(supA).append('\n');
				cpt--;
			} else if (curMlhs.size() <= taille_max) {
				/*
				 * avec calcul du support
				 */
				MutableInt supXY = new MutableInt();
				int supX = m_base.new_comptage(curMlhs, m_A, supXY);
				double confidenceXY;
				if (supX != 0) {
					confidenceXY = (double) supXY.getValue() / (double) supX;
				} else {
					confidenceXY = 0;
				}
				System.out.println("confidenceExact:" + confidenceXY);
				/*
				 * sans calcul du support int supXY = 2; int supX = 2;
				 */
				// R4 : RESTRICTION aux parties gauches telles que suppX > 0
				if (supXY.getValue() != 0 && (double) supXY.getValue() / nbligne >= supportSeuil && confidenceXY != 0
						&& confidenceXY >= confidenceSeuil) {
					for (Integer curVal : curMlhs.getM_at())
						sbRules.append(curVal).append(' ');
					// sbRules.append('\t').append(m_A).append('\t').append(nbligne).append('\t');
					sbRules.append("=>\t").append(m_A).append('\t').append(nbligne).append('\t');
					sbRules.append(supX).append('\t').append(supA).append('\t').append(supXY).append('\n');
					cpt--;
				}
			}

			if (cpt <= 0) {
				m_appli.writeInStream(sbRules);
				cpt = 50000;
				sbRules = new StringBuilder("");
			}

		}

		// A => rhs
		// Calculs inutiles si tous les attributs sont centraux
		if (m_appli.getM_nbcentral() < m_appli.getM_nbattr()) {
			// for (int iMrhs=0; iMrhs<m_rhs.size(); iMrhs++)
			Iterator<Integer> it = m_rhs.getM_at().iterator();
			while (it.hasNext()) {
				Integer curVal = it.next();
				TreeSet<Integer> tmpArray = new TreeSet<Integer>();
				tmpArray.add(m_A);
				tmpArray.add(curVal);
				AttributSet lhs_rhs = new AttributSet(tmpArray);
				int supXY = m_base.comptage(lhs_rhs); // supXY : support partie
				// gauche + partie droite

				int supY = m_supp[curVal]; // supX : support partie gauche
				// sbRules.append(m_A).append('\t').append(curVal).append('\t');
				sbRules.append(m_A).append(" =>\t").append(curVal).append('\t');
				sbRules.append(nbligne).append('\t').append(supA).append('\t').append(supY);
				sbRules.append('\t').append(supXY).append('\n');
				cpt--;
			}
		}

		if (cpt < 50000)
			m_appli.writeInStream(sbRules);

	}

	/******** FIN ********/

	/******** Renvoie true si l'intersection est vide ********/
	boolean is_intersection_empty(AttributSet F, AttributSet E) {
		if (F.size() < E.size()) {
			for (Integer curVal : F.getM_at())
				if (E.find(curVal))
					return false;
			return true;
		} else {
			for (Integer curVal : E.getM_at())
				if (F.find(curVal))
					return false;
			return true;
		}
	}

	/******** FIN ********/

	/********
	 * Permet de supprimer les fichiers textes utiles pour UNOLHS
	 ********/
	void cleanUnoTempFiles() {
		if (m_process != null) {
			m_process.destroy();
		}

		try {
			Thread.sleep(200);
		} catch (InterruptedException ex) {
			/* ignore exception */
			ex.printStackTrace();
		}

		try {
			if (m_reader != null) {
				m_reader.close();
			}
			if (m_writer != null) {
				m_writer.close();
			}
			if (this.m_fileCinf != null && this.m_fileCinf.exists()) {
				m_fileCinf.delete();
			}
			if (this.m_fileRet != null && this.m_fileRet.exists()) {
				m_fileRet.delete();
			}
		} catch (IOException ex) {
			/* ignore exception */
			ex.printStackTrace();
		}
	}

	public Set_AttributSet getM_inf() {
		return m_inf;
	}

	public Set_AttributSet getM_cinf() {
		return m_cinf;
	}

	public Set_AttributSet getM_lhs() {
		return m_lhs;
	}

	public AttributSet getM_rhs() {
		return m_rhs;
	}

}
