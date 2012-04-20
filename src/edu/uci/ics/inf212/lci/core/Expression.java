package edu.uci.ics.inf212.lci.core;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Expression {
	//Before parsing a string is input. Use same string till final expressionString is obtained and isFurtherReducible returns false
	protected String expressionString;
	protected String preExpression;
	protected String postExpression;
	//After parsing a string is converted to leftmost-outermost lambdaFunction with list of argumentList which can be applied to the lambdaFunction	
	protected Expression lambdaFunction;
	protected ArrayList<Expression> argumentList;
	private boolean parsed=false;
	private boolean furtherReducible=false;
	
	//Constructor
	public Expression(){		
	}

	public Expression(String expString ){
		this.expressionString = expString;
		//System.out.println("Expr "+ this.expressionString);		
	}

	protected void parseExpression() {
		//Generates lambdaFunction and argumentList from expressionString 
		//Exp = (Labc.Exp) (...)
		
		String func = "(\\(L\\w+\\.\\S+\\))";
		String arg = "((\\w+|(\\(\\S+\\)))+)";		
		//This is for outer expression reducibility
		CharSequence cs = this.expressionString.subSequence(0, this.expressionString.length());		
		
		Pattern functionFinder = Pattern.compile("(.*?)"+func+arg+"(.*?)");		
		Matcher matchFunction = functionFinder.matcher(cs);
		
		String finalTest = this.expressionString;
		int functionStartingPoint = finalTest.indexOf("(L");
		
		if(functionStartingPoint!=-1){
			if(functionStartingPoint!=0)
			preExpression=cs.subSequence(0, functionStartingPoint).toString();			
			//System.out.println("PreExpression is "+preExpression);
			//System.out.println("Found function "+cs.subSequence(matchFunction.start(), matchFunction.end()));
			//System.out.println("TRYING "+fmat.matches());
			try {
				int openBracketCounter = 0;
				//int index=matchFunction.start();
				int index = functionStartingPoint;
				int previndex=index-1;				
				int i = index;
				int numberOfArguments=0;
				this.argumentList=null;
				for(;i<this.expressionString.length();i++){
					if(this.expressionString.charAt(i)=='(') {		
						openBracketCounter++;
					}
					else if(this.expressionString.charAt(i)==')') { 
						openBracketCounter--;
						if(openBracketCounter==0) {
							if(numberOfArguments==0){
								//System.out.println("Function is "+this.expressionString.substring(previndex+1, i+1));
								this.lambdaFunction = new Expression(this.expressionString.substring(previndex+1, i+1));		
							}
							else {
								if(numberOfArguments==1)
									argumentList = new ArrayList<Expression>();
								//System.out.println("Arg "+numberOfArguments+ " is "+this.expressionString.substring(previndex+1, i+1));
								this.argumentList.add(numberOfArguments-1, new Expression(this.expressionString.substring(previndex+1, i+1)));															
							}
							previndex=i;
							numberOfArguments++;								
						}
					}
					else if(openBracketCounter==0 && (this.expressionString.charAt(i)!=')' || this.expressionString.charAt(i)!='(')){					
						String sub = this.expressionString.substring(i);
						//System.out.println("sunb is "+sub);
						int k=0;
						if(Character.isLetter(sub.charAt(k))){
							k++;
							while(k<sub.length()&&Character.isDigit(sub.charAt(k)))
									k++;				
						}
						String sym=sub.substring(0, k);
						//System.out.println("TEst "+sym);						
						if(numberOfArguments==1)
							argumentList = new ArrayList<Expression>();
						this.argumentList.add(numberOfArguments-1, new Expression(sym));
						i+=(k-1);
						previndex=i;
						numberOfArguments++;
					}							
					if(openBracketCounter<0){
						postExpression=cs.subSequence(i, this.expressionString.length()).toString();
						//System.out.println("PostExpression is "+postExpression);
						break;
					}
				}				
				parsed=true;				
			} catch(Exception e) {
				parsed=false;
			}				
			
			this.lambdaFunction.expressionString=removeAdditionalBraces(this.lambdaFunction.expressionString);
			//System.out.println("Final lambda function is "+this.lambdaFunction.expressionString);
			if(this.argumentList!=null) {
				for(Expression e:this.argumentList){
					//e.expressionString=removeAdditionalBraces(e.expressionString);
					//System.out.println("Argument list is "+e.expressionString);
				}
			}
		} else {
			parsed=true;
			//Might have to pass the string to removeAdditionalBraces method before doing this
			preExpression=cs.toString();
			//System.out.println("Function not found. Entire thing in preExpression");
		}		
		if(preExpression!=null&&postExpression!=null){				
			int lengthPre=preExpression.length();
			int lengthPost=postExpression.length();
			int p=0;			
			while(((lengthPre-p-1)>=0&&preExpression.charAt(lengthPre-p-1)=='(')&&((p<lengthPost)&&postExpression.charAt(p)==')')) {
				p++;
			}
			preExpression=preExpression.substring(0, lengthPre-p);
			postExpression=postExpression.substring(p, lengthPost);
		}
		//System.out.println("pre "+preExpression+ " post "+postExpression);
		if(preExpression!=null&&postExpression!=null){				
			int lengthPre=preExpression.length();
			int lengthPost=postExpression.length();
			int p=0;
			while((p<lengthPre&&preExpression.charAt(p)=='(')&&((lengthPost-p-1>=0)&&postExpression.charAt(lengthPost-p-1)==')')) {
				p++;
			}
			preExpression=preExpression.substring(p, lengthPre);
			postExpression=postExpression.substring(0, lengthPost-p);
		}
		//System.out.println("pre "+preExpression+ " post "+postExpression);
		//System.out.println("PreFinal is "+preExpression);
		//System.out.println("PostFinal is "+postExpression);			
	}
	
	public boolean parseSuccessful(){
		return parsed;
	}
	
	protected boolean isFurtherReducible(){
		//Checks if argumentList is null or not
		String func = "(\\(L\\w+\\.\\S+\\))";
		String arg = "((\\w+|(\\(\\S+\\)))+)";
		String regex = "(.*?)"+func+arg+"(.*?)";
		//This is for complete expression reducibility
		Pattern pat = Pattern.compile(regex);
		CharSequence cs = this.expressionString.subSequence(0, this.expressionString.length());		
		Matcher mat = pat.matcher(cs);				
		furtherReducible = mat.matches();		
		return furtherReducible;		
	}
	
	protected void preProcessing() {
		this.removeWhiteSpaces();
	}
	
	private void removeWhiteSpaces() {
		//Removing space, tab and newline
		String whitespace = "\\s+";
		this.expressionString = this.expressionString.trim();
		this.expressionString = this.expressionString.replaceAll(whitespace, "");		
	}
	
	private String removeAdditionalBraces(String withBraces){
		String withoutBraces=withBraces;
		int length = withoutBraces.length();
		int i;
		for(i=0;i<length;i++){
			if(withBraces.charAt(i)=='(' && withBraces.charAt(length-i-1)==')')
				;
			else
				break;
		}
		withoutBraces=withBraces.substring(i, length-i);
		//System.out.println();
		return withoutBraces;
	}
	
	//Accessor methods
	public ArrayList<Expression> getArguments() {
		return argumentList;
	}
	public String getExpression() {
		return expressionString;
	}
	public Expression getFunction() {
		return lambdaFunction;
	}
	public void setArguments(ArrayList<Expression> argumentList) {
		this.argumentList = argumentList;
	}
	public void setExpression(String expressionString) {
		this.expressionString = expressionString;
	}
	public void setFunction(Expression lambdaFunction) {
		this.lambdaFunction = lambdaFunction;
	}
	public String getPostExpression() {
		return postExpression;
	}
	public String getPreExpression() {
		return preExpression;
	}
	
	//Dummy main function to test parseExpression
	public static void main(String args[]) {
		System.out.println("-----------------------------------------------------------------------------");
		String es = "(La.b(Lab.k(Lx.x)))(Lc.ab)(Lan.m(Ly.y)n)";
		System.out.println("Expression is "+es);
		Expression e = new Expression(es);
		e.removeWhiteSpaces();
		e.parseExpression();		
		System.out.println(e.isFurtherReducible());
		System.out.println("-----------------------------------------------------------------------------");
		
		String es2 = "(La.b(Lab.k(Lx.x)))z";
		System.out.println("Expression is "+es2);
		Expression e2 = new Expression(es2);
		e2.removeWhiteSpaces();
		e2.parseExpression();		
		System.out.println(e2.isFurtherReducible());
		System.out.println("-----------------------------------------------------------------------------");
		
		String all[] = {"(La.b(Lab.k(Lx.x)))(Lc.ab)(Lan.m(Ly.y)n)","(La.a)z(La.ab)(z)","(La.b(Lab.k(Lx.x)))z","(La.b(Lab.k(Lx.x)))(z)","(La.b(Lab.k(Lx.x)))z(x)","(La.b(Lab.k(Lx.x))z)","(La.b(Lab.k(Lx.x)z))","(La.b(Lab.k(Lx.x)))(Lc.ab)(Lan.m(Ly.y)n)","(La.b(Lab.k(Lx.x)))(Lc.ab)(Lan.m(Ly.y))","(La.a)","(La.ab)","(La.b(Lab.k(Lx.x)))","(La.ab)x","(La.ab)(x)","k((La.ab)(x))x)","(k((La.ab)(x))x))","k(((La.ab)(x)))x)","(Laxy.bx(Lab.k(Lx.x))xa)(bb)z(b)","k(La.a)x"};
		for(String s: all) {
			System.out.println("Expression is "+s);
			Expression eall = new Expression(s);
			eall.preProcessing();
			eall.parseExpression();
			System.out.println(eall.isFurtherReducible());
			System.out.println("-----------------------------------------------------------------------------");
		}
	}
	
	public ArrayList<Expression> getReducableExpressions() {
		// TODO Auto-generated method stub
		return null;
	}
}
