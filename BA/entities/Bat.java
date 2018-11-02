package entities;

import java.util.ArrayList;

public class Bat {
	private ArrayList<Integer> p;
	private float v;
	private float A;
	private float r;
	
	public Bat() {
		this.p = new ArrayList<Integer>();
		this.v = 0.f;
		A = 0.f;
		this.r = 0.f;
	}

	public Bat(float v, float a, float r) {
		
		this.p = new ArrayList<Integer>();
		this.v = v;
		A = a;
		this.r = r;
	}
	
	public ArrayList<Integer> getP() {
		return p;
	}

	public float getV() {
		return v;
	}

	public void setV(float v) {
		this.v = v;
	}

	public float getA() {
		return A;
	}

	public void setA(float a) {
		A = a;
	}

	public float getR() {
		return r;
	}

	public void setR(float r) {
		this.r = r;
	}
	
	
	

}
