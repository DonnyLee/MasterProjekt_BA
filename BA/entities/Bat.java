package entities;

import java.util.ArrayList;

public class Bat {
	private ArrayList<Integer> p;
	private double v;
	private double A;
	private double r;
	
	public Bat() {
		this.p = new ArrayList<Integer>();
		this.v = 0.f;
		A = 0.f;
		this.r = 0.f;

	}

	public Bat(int startPosition, double a, double r) {

		this.p = new ArrayList<Integer>();
		this.addPosition(startPosition);
		this.v = 0.f;
		A = a;
		this.r = r;
	}
	
	public ArrayList<Integer> getP() {
		return p;
	}

	public void addPosition(int p){ this.p.add(p);}

	public double getV() {
		return v;
	}

	public void setV(float v) {
		this.v = v;
	}

	public double getA() {
		return A;
	}

	public void setA(float a) {
		A = a;
	}

	public double getR() {
		return r;
	}

	public void setR(float r) {
		this.r = r;
	}

	@Override
	public String toString(){
		return p.toString()+", velocity="+v+", loudness="+A+", pulse="+r;
	}
	
	

}
