package discretemaths.forms;

public class Exists extends Quantifier{

	public Exists()
	{}
	
	public Exists (String var, String type, Form sub)throws InvalidFormException
	{
		super(var, type, sub);
	}
	
	public String toString()
	{
		return toStringHelper("E" +var + ":" + type + "." + sub.toString());
	}
	
	public boolean equals(Object o)
	{
		if (o.getClass() != Exists.class)
			return false;
		else if (((Exists)o).type.equals(type) && ((Exists)o).sub.equals(sub))
			return true;
		else
			return false;
	}

	@Override
	public boolean equals(Form other) {
		if(other.getClass().equals(this.getClass())){
			Exists cast = (Exists)other;
			return var.equals(cast.var) && type.equals(cast.type) && sub.equals(cast.sub);
		}
		return false;
	}
}
