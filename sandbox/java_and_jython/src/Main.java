import jsr.PyCaller;


public class Main {

    public static void main(String[] args) {
	// write your code here

        PyCaller caller = new PyCaller();
        String fname = "scripts/test_caller.py";
        String function_name = "get_string";
        String arg = "None";

        String result = (String)caller.py_call_with_return(fname, function_name, arg);
        System.out.println(result);
    }
}
