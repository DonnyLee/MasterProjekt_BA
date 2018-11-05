package entities;

import java.util.ArrayList;
import com.hsh.Evaluable;

public class Bat extends Evaluable{
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

	public Bat(ArrayList<Integer> startSolution, double a, double r) {

		this.p = new ArrayList<>(startSolution);
		this.v = 0.f;
		A = a;
		this.r = r;
	}

	@Override
	public ArrayList<Integer> getPath() {
		return p;
	}

	//public void addPosition(int p){ this.p.add(p);}

	public double getV() {
		return v;
	}

	public void setV(double v) {
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
		return p.toString() +", velocity="+v+", loudness="+A+", pulse="+r;
	}

	public String toString(boolean no){
	    if(no) {
	        return toString();
        }else{
	        return "velocity="+v+", loudness="+A+", pulse="+r;
        }
    }
	
	

}
