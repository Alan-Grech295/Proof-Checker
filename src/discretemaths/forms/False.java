package discretemaths.forms;

public class False extends Atomic{

	public String toString() {
		return toStringHelper("false");
	}
	
	public boolean occursFree(String x)
	{
		return false;
	}

	@Override
	public boolean isFalse(){
		return true;
	}

	@Override
	public boolean equals(Form other) {
		return other.getClass().equals(this.getClass());
	}
}
