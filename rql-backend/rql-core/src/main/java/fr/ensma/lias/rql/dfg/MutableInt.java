package fr.ensma.lias.rql.dfg;

/**
 * @author Gouriou Benjamin
 */
public class MutableInt {

	private int value;

	public MutableInt() {
		value = 0;
	}

	public MutableInt(int val) {
		value = val;
	}

	public void increment() {
		value++;
	}

	public void decrement() {
		value--;
	}

	public void add(int delta) {
		value += delta;
	}

	public void minus(int delta) {
		value -= delta;
	}

	public int getValue() {
		return value;
	}

	public boolean estEgal(int val) {
		return val == value;
	}

	@Override
	public String toString() {
		return Integer.toString(value);
	}
}
