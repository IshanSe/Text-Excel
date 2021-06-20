package textExcel;


public class TextCell extends Cell{

	public String toUpperCase(String input)throws Exception{
		if(input == null)
			throw new Exception("Input string is null");
			return input.toUpperCase();
 
	}
}

