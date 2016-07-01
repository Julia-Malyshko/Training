package model;

public class Item {
	private String mName;
	private double mCost;

	public Item() {
	}

	public String getName() {
		return mName;
	}

	public void setName(final String pName) {
		this.mName = pName;
	}

	public double getCost() {
		return mCost;
	}

	public void setCost(final double pCost) {
		this.mCost = pCost;
	}
}
