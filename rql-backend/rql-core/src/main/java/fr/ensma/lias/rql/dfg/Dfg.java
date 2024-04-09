package fr.ensma.lias.rql.dfg;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

import fr.ensma.lias.rql.dfg.MyUncaughtExceptionHandler;
import fr.ensma.lias.rql.dfg.NotifyingBlockingThreadPoolExecutor;
import fr.ensma.lias.rql.dfg.Set_AttributSet;
import fr.ensma.lias.rql.dfg.ThreadRunException;

/**
 * @author Gouriou Benjamin
 */
public class Dfg {

	private int m_progress = 0;

	private int m_taille_max; // taille maximale de la partie gauche

	private int m_nbattr; // nb d'attributs dans le trie

	private int m_nbligne = 0; // nb de lignes

	private int m_nbcentral; // nb d'attributs centraux

	private ArrayList<Integer> m_central; // Attributs centraux

	boolean m_centr[]; // pour chaque attribut, true si il est central

	int m_supp[]; // Supports des attributs

	Set_AttributSet m_base; // Base du système de fermeture

	private String m_fileIn;

	private String m_fileOut;

	private String m_execpath;

	private BufferedWriter m_buffer = null;

	private FileWriter m_writer = null;

	private boolean m_lineNbInFile = false;

	private final Lock m_lock = new ReentrantLock();

	private ThreadRunException _threadException = null;

	static final int THREAD_POOL_SIZE = 4;

	static final int THREAD_QUEUE_SIZE = 8;

	static final int THREAD_KEEPALIVE = 15;

	static final TimeUnit THREAD_KEEPALIVE_UNIT = TimeUnit.SECONDS;

	public long loadTime = 0;

	public long calculInfTime = 0;

	public long concatRulesCGLTime = 0;

	public long calculCInfTime = 0;

	public long calculLhsUnoTime = 0;

	public long writeRulesCCTime = 0;

	public long writeTime = 0;

	public double supportSeuil = 0;
	public double confidenceSeuil = 0;

	public Dfg(String fileIn, String fileOut, double support, double confidence, int tailleMax, String m_execpath) {
		m_base = new Set_AttributSet();
		// *******************************
		this.m_taille_max = tailleMax;
		this.m_fileOut = fileOut;
		this.m_fileIn = fileIn;
		this.m_execpath = m_execpath;
		this.supportSeuil = support;
		this.confidenceSeuil = confidence;
	}

	/*
	 * void init(int nbOcc, int[] agr) { TreeSet<Integer> tmpArray = new
	 * TreeSet<Integer>(); for (int cur : agr) { tmpArray.add(cur); } AttributSet ag
	 * = new AttributSet(); ag.setM_count(nbOcc); ag.setM_at(tmpArray);
	 * m_base.push_back(ag); }
	 */

	/******** Chargement de m_base, m_central et m_supp ********/
	int charger() throws Exception {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(m_fileIn));
		} catch (FileNotFoundException ex) {
			throw new Exception("DFGApp : Specified entry base file does not exist.");
		}

		String line = "";
		try {
			line = reader.readLine();
			m_nbcentral = Integer.valueOf(line);
			System.out.println("m_nbcentral : " + m_nbcentral);
			m_central = new ArrayList<Integer>(m_nbcentral);
		} catch (IOException ex) {
			reader.close();
			throw new Exception("DFGApp : Can't get central genes number. File format is incorrect.");
		}

		try {
			line = reader.readLine();
			String tmp[] = line.split(" ");
			for (String curr : tmp)
				m_central.add(Integer.valueOf(curr));
		} catch (NumberFormatException e) {
			reader.close();
			throw new Exception("DFGApp : Invalide caracter in the list of central gene.");
		} catch (IOException e) {
			reader.close();
			throw new Exception("DFGApp : Error during the reading of central genes list.");
		}

		try {
			line = reader.readLine();
			String tmp[] = line.split(" ");
			m_nbattr = Integer.valueOf(tmp[0]);
			m_nbligne = Integer.valueOf(tmp[1]);
		} catch (IOException ex) {
			reader.close();
			throw new Exception("DFGApp : Error during the reading of attributes and lines number.");
		}

		m_centr = new boolean[m_nbattr];
		m_supp = new int[m_nbattr];
		System.out.println("Nombre d'attributs : " + m_nbattr);

		if (m_nbligne != 0)
			m_lineNbInFile = true;

		for (int i = 0; i < m_nbcentral; i++)
			m_centr[m_central.get(i)] = true;

		// Chargement de la base (a partir de la 3eme ligne)
		int numAttribut;
		int countElt;
		AttributSet ag = null;
		TreeSet<Integer> tmpArray;
		try {
			while ((line = reader.readLine()) != null) {
				ag = new AttributSet();
				tmpArray = new TreeSet<Integer>();
				String tmp[] = line.split("  ", 2);
				countElt = Integer.parseInt(tmp[0]);
				if (!m_lineNbInFile)
					m_nbligne += countElt;
				ag.setM_count(countElt);
				if (tmp.length == 2) {
					String val[] = tmp[1].split(" ");
					for (String curr : val) {
						numAttribut = Integer.parseInt(curr);
						tmpArray.add(numAttribut);
						m_supp[numAttribut] += countElt;
					}
				}
				ag.setM_at(tmpArray);
				m_base.push_back(ag);
			}
			for (int i = 0; i < m_supp.length; i++) {
				System.out.println(m_supp[i]);
			}
		} catch (IOException ex) {
			throw new Exception("DFGApp : Error during the reading of base lines in the file.");
		}

		System.out.println("Nombre de lignes : " + m_nbligne);

		reader.close();

		return 0;
	}

	public int execute() throws Exception {
		/*
		 * double t1, t2; t1=System.currentTimeMillis();
		 */
		Thread.setDefaultUncaughtExceptionHandler(new MyUncaughtExceptionHandler());
		File file = new File(m_fileIn);

		if (!file.exists() || m_taille_max <= 0)
			throw new Exception("DFGApp: Error in entry parameters. Rules generation aborted.");

		if (charger() != 0)
			throw new Exception("DFGApp: Error during the reading of the file. Rules generation aborted.");

		/*
		 * String path = System.getenv("WebNetwork"); //Windows if
		 * (System.getProperty("os.name").contains("Windows")) m_exePath = path +
		 * "\\shd.exe"; else m_exePath = path + "shd";
		 */

		// Mac
		// m_exePath = path + "shd";

		// System.out.println("execution: "+m_exePath);

		// *****************************************************************
		// ***************************************** Parallel calculation
		// *****************************************************************

		// -----------------------------------------------------------------
		// NotifyingBlockingThreadPoolExecutor initialization parameters

		// -----------------------------------------------------------------
		// Create the NotifyingBlockingThreadPoolExecutor
		NotifyingBlockingThreadPoolExecutor m_threadPoolExecutor = new NotifyingBlockingThreadPoolExecutor(
				THREAD_POOL_SIZE, THREAD_QUEUE_SIZE, THREAD_KEEPALIVE, THREAD_KEEPALIVE_UNIT);

		// -----------------------------------------------------------------
		// Stack and execute the tasks
		for (int i = 0; i < this.m_nbcentral && _threadException == null; i++) {
			AttributeCalculation currentAttr = new AttributeCalculation(this, m_central.get(i), supportSeuil,
					confidenceSeuil);
			System.out.println("checkpoint");
			m_threadPoolExecutor.execute(currentAttr);
			// this.m_progress=m_threadPoolExecutor.getTasksCompleted();
			this.m_progress = i;
			System.out.println((i * 100 / this.m_nbcentral) + "%");
		}

		if (_threadException != null) {
			this.closeStream();
			throw _threadException;
		}

		// -----------------------------------------------------------------
		// Wait for the last tasks to be done
		try {
			boolean done = false;
			do {
				// System.out.println("waiting for the last tasks to finish");
				if (!m_threadPoolExecutor.isShutdown())
					done = m_threadPoolExecutor.await(5, TimeUnit.SECONDS);
				else
					done = true;
				// this.m_progress=m_threadPoolExecutor.getTasksCompleted();
			} while (!done);
		} catch (InterruptedException e) {
			throw new Exception("DFGApp : Unknown error during the execution of the process!");
		}

		if (_threadException != null) {
			this.closeStream();
			throw _threadException;
		}

		m_threadPoolExecutor.shutdown();
		/*
		 * t2 = System.currentTimeMillis(); System.out.println("Temps d'execution : "+
		 * (t2 - t1));
		 */
		this.closeStream();

		return 0;
	}

	/*
	 * public static void main(String[] args) { if (args.length<3) {
	 * System.err.println("Erreur nombre de paramètres incorrects"); return; }
	 * 
	 * Dfg app = new Dfg(args[0],args[1],Integer.valueOf(args[2])); try {
	 * app.execute(); } catch (Exception ex) {
	 * //Logger.getLogger(Appli.class.getName()).log(Level.SEVERE, null, ex);
	 * System.err.println(ex.getMessage()); } }
	 */

	public BufferedWriter getStream() {
		if (m_buffer == null) {
			File fOut = new File(this.m_fileOut);
			if (fOut.exists())
				fOut.delete();
			try {
				this.m_writer = new FileWriter(m_fileOut, true);
				this.m_buffer = new BufferedWriter(m_writer);
			} catch (IOException ex) {
				ex.printStackTrace();
				Logger.getLogger(Dfg.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		return m_buffer;
	}

	public synchronized void writeInStream(StringBuilder builder) throws Exception {
		m_lock.lock();
		BufferedWriter wr = this.getStream();
		try {
			wr.write(builder.toString());
			wr.flush();
		} catch (IOException ex) {
			throw new Exception("DFGApp : Error during rules writing in file.");
		} finally {
			m_lock.unlock();
		}

	}

	public synchronized void closeStream() {
		try {
			m_buffer.close();
			m_writer.close();
		} catch (IOException ex) {
			ex.printStackTrace();
			System.err.println("Erreur à la fermeture du fichier de regles.");
		}
	}

	public int getProgress() {
		return (int) Math.ceil((this.m_progress / ((float) this.m_nbcentral)) * 100);
	}

	/*
	 * public int killExecution() { try {
	 * m_threadPoolExecutor.setExecutionKilled(true); this.setThreadException(new
	 * ThreadRunException(new Exception("User killed execution"),-1));
	 * m_threadPoolExecutor.shutdownNow(); if
	 * (!m_threadPoolExecutor.awaitTermination(3, TimeUnit.SECONDS))
	 * System.err.println("Pool did not terminate"); } catch (InterruptedException
	 * ex) { ex.printStackTrace();
	 * Logger.getLogger(Dfg.class.getName()).log(Level.SEVERE, null, ex); return -1;
	 * } return 0; }
	 */

	/***** GETTERS AND SETTERS *****/

	public int getM_taille_max() {
		return m_taille_max;
	}

	public void setM_taille_max(int m_taille_max) {
		this.m_taille_max = m_taille_max;
	}

	public int getM_nbattr() {
		return m_nbattr;
	}

	public int getM_nbligne() {
		return m_nbligne;
	}

	public int getM_nbcentral() {
		return m_nbcentral;
	}

	public ArrayList<Integer> getM_central() {
		return m_central;
	}

	public Set_AttributSet getM_base() {
		return m_base;
	}

	public boolean[] getM_centr() {
		return m_centr;
	}

	public int[] getM_supp() {
		return m_supp;
	}

	public String getM_fileIn() {
		return m_fileIn;
	}

	public String getM_fileOut() {
		return m_fileOut;
	}

	public String getM_exePath() {
		return m_execpath;
	}

	public boolean isM_lineNbInFile() {
		return m_lineNbInFile;
	}

	public Exception getThreadException() {
		return _threadException;
	}

	public synchronized void setThreadException(ThreadRunException threadException) {
		this._threadException = threadException;
	}

	public synchronized void addTimes(long calculInf, long concatRulesCGL, long calculCInf, long calculLhsUno,
			long writeRulesCC) {
		calculInfTime += calculInf;
		concatRulesCGLTime += concatRulesCGL;
		calculCInfTime += calculCInf;
		calculLhsUnoTime += calculLhsUno;
		writeRulesCCTime += writeRulesCC;
	}

}
