package discretemaths.forms;

public class Not extends Unary{

	public Not()
	{}
	
	public Not (Form sub)throws InvalidFormException
	{
		super(sub);
	}
	
	public String toString()
	{
		return toStringHelper("!" + sub);
	}
	
	public boolean equals(Object o)
	{
		if (o.getClass() != Not.class)
			return false;
		else if (((Not)o).sub.equals(sub))
			return true;
		else
			return false;
	}

	@Override
	public boolean equals(Form other) {
		if(other.getClass().equals(this.getClass())){
			Not cast = (Not)other;
			return sub.equals(cast.getSub());
		}
		return false;
	}
}
