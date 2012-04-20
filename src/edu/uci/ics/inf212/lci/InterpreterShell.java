/**
 * 
 */
import edu.uci.ics.inf212.lci.core.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * @author Pushkar
 *
 */
public class InterpreterShell {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String testCases[] = {
		//Case1:
		//OUR CODE:
		"(Laxy.bx(Lab.k(Lx.x)a)xa) (bb) z(b)l",
		//Output - bzkzl
		//LCI:
		//(\a.\x.\y.b x(\a.\b.k(\x.x)a)x a) (b b) z (b) l
		//Output - b z (\a.\b.k (\x.x) a) z bb l

		//Case2:
		//OUR CODE:
		"(Labc.abc)(Lxy.x)m n",
		//Output -
		//LCI:
		//(λa.λb.λc.abc) (λx.λy.x) m n
		//Output - m

		//Case3:
		//OUR CODE:
		"(Labc.abc)(Lxy.y)m n",
		//Output -
		//LCI:
		//(λa.λb.λc.abc) (λx.λy.y) m n
		//Output - n

		//Case4:
		//OUR CODE:
		"(La.a)m",
		//Output - m
		//LCI:
		//(λm.m) n
		//Output - n
		
		"(Laxy.bx(Lab.k(Lx.x))xa) (bb) z(b)",
		"(Laxy.bx(Lab.k(Lx.x)a)xa) (bb) z(b)",
		"(Laxy.bx(Lab.k(Lx.x)a)xa) (bb) z(b)l",

		"(Laxy.bx(Lab.k(Lx.x)a)xap) (bb) z(b)l",

		"(Laxy.bx(Lab.k(Lx.x)a)xap) (bb) z(b)l",


		"(Laxy.bx(Lab.kb)xa) (bb) z(b)",
		"k(Lx.x)a",
 		"(Laxy.bx(Lab.k(Lx.x))xa) (bb) z(b)",
		"(La.b(Lab.k(Lx.x)))(Lc.ab)",
		"(Lwyx.y(wyx))(Las.a(a(s)))", //succ of 2 
		"(Lwyx.y(wyx))(Las.a(s))", //succ of 1 	
		//one + two
		"(Lpq.p(q))(Lwyx.y(wyx))(Las.a(a(s)))",
		"(Lpq.p(q))(Lwyx.y(wyx))(Las.a(a(a(s))))",
		//two + one
		"(Las.a(a(s)))(Lwyx.y(wyx))(Lpq.p(q))",
		"(Lxyz.x(yz)) (Las.a(a(s))) (Las.a(a(s)))"
		};
		/*
		String testCases[] = {"(Las.a(a(s)))(Lwyx.y(wyx))(Lpq.p(q))", "(Lpq.p(q))(Lwyx.y(wyx))(Las.a(a(s)))"};
		*/

		
		for(int cases=0;cases<testCases.length;cases++) {
			Engine	lambdaInterpreter =new Engine();
			String inputString=testCases[cases];							
			//Create the Engine object and start executing the expression in the string 
			//System.out.println("Input : " +inputString );
			Engine lambdaEngine = new Engine(inputString);
			lambdaEngine.executeExpression();
		}
		
		System.out.println("-----------------------------------------------------------------------------------");
		System.out.println("");
		System.out.println("Done testing");
		
		String inputString = "(La.a)m";
		while(!("".equalsIgnoreCase(inputString.trim()))){
			System.out.println("Enter your input String");
			try{
				BufferedReader inputBReader =  new BufferedReader(new InputStreamReader(System.in, "UTF-8"));
				inputString = inputBReader.readLine();
				Engine	lambdaInterpreter = new Engine();
				System.out.println("Input : " +inputString );
				Engine lambdaEngine = new Engine(inputString);
				lambdaEngine.executeExpression();
			}catch(Exception E){
				System.out.println("Caught exception in the input string. Exiting ");
				System.exit(1);
			}
		}
		
	}

}
