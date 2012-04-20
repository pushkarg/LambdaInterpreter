package edu.uci.ics.inf212.lci.core;

import java.util.ArrayList;

public class Function {
	protected String argumentList;
	protected Expression functionBody;
	protected Expression result;
	protected int argumentCount;
	static int subscript[]= new int[26];
	String argumentMapping[] =new String[0];
	int betaReductionCount = 0;


	public Function(Expression functionExpression){
		//Logic to extract the function arguments and the body from the string
		String functionExpressionString = functionExpression.getExpression();

		argumentList = functionExpressionString.substring( functionExpressionString.indexOf('L')+1 , functionExpressionString.indexOf('.') );
		//System.out.println("Argument list : " + argumentList );
		argumentCount = argumentList.length();

		String functionBodyString = functionExpressionString.substring( functionExpressionString.indexOf('.') + 1 );
		//System.out.println("Function body  :  " + functionBodyString );
		functionBody = new Expression( functionBodyString);
		argumentMapping = new String[ argumentCount];

	}

	protected void alphaRedex() {
		for(int i=0;i<argumentCount ;i++){
			char arg = argumentList.charAt(i);
			int asciiValue = Character.getNumericValue(arg) -10;
			subscript[asciiValue]++;

			argumentMapping[i] =  argumentList.charAt(i) + Integer.toString( subscript[asciiValue] );
			//System.out.println("mapping: " + argumentMapping[i]);
		}

		StringBuffer functionBodyString = new StringBuffer( functionBody.getExpression());
		//System.out.println("Orig func body : " + functionBodyString);
		
		int index =0;
		while(index<functionBodyString.length()){
			if(functionBodyString.charAt(index) == 'L'){
				try{
					int indexOfMatching = findMatchingBracket( functionBodyString.toString() , index -1 );
					index = indexOfMatching ;
					//System.out.println("matching index : " + index + " , char : " + functionBodyString.charAt(index));
				}catch(Exception e){
					 System.out.println("Exception caught. details : " + e.toString() );
					 System.exit(1);
				}
			}else if(argumentList.indexOf(functionBodyString.charAt(index))>=0){
				//System.out.println("char being replaced : " + functionBodyString.charAt(index) + " , pos: "+ index);
				int asciiValue = Character.getNumericValue(functionBodyString.charAt(index)) -10;
				functionBodyString.replace( index,index+ 1,argumentMapping[ argumentList.indexOf(functionBodyString.charAt(index))] );
			}
			//System.out.println("CUrrent pos : " + index + " , char : " + functionBodyString.charAt(index));
			index++;
		}

		//System.out.println("Str after alpha reduction : " + functionBodyString);
		functionBody = new Expression( functionBodyString.toString() );
	}
	
	protected void betaRedex(Expression expressionString , int argumentNum) {
		String charSequenceToReplace = argumentMapping[argumentNum];
		//System.out.println("Func: "+ functionBody.getExpression() +", expr ; "+  expressionString.getExpression() + " , arg being replaced : " + argumentMapping[argumentNum]);

		String functionBodyStr = functionBody.getExpression();
		functionBodyStr = functionBodyStr.replace(argumentMapping[argumentNum] , expressionString.getExpression());
		functionBody = new Expression( functionBodyStr.toString() );
		betaReductionCount++;
	}

	public Expression getEvaluatedExpression(){
		if(betaReductionCount==argumentCount)
			return this.functionBody;

		//There are fewer inputs available than the number of arguments
		String newArgumentList = argumentList.substring(betaReductionCount);
		String evaluatedExpressionStr = "(L"+newArgumentList+".("+this.functionBody.getExpression() +"))";

		//Perform inverse aplha reduction
		StringBuffer evaluatedExpressionStrBuffer = new StringBuffer( evaluatedExpressionStr);
		for(int i =0;i<evaluatedExpressionStrBuffer.length();i++){
			if( Character.isDigit(evaluatedExpressionStrBuffer.charAt(i) ))
				evaluatedExpressionStrBuffer.replace(i,i+1,"");
		}
		//System.out.println("Inverse alpha reduiced expr : " +evaluatedExpressionStrBuffer +"\naplha reduced expr :          " + evaluatedExpressionStr );


		Expression evaluatedExpression = new Expression(evaluatedExpressionStrBuffer.toString());
		return evaluatedExpression;
		
	}

	public boolean hasEvaluatedCompletely(){
		if(betaReductionCount==argumentCount)
			return true;
		else return false;
	}

	protected int findMatchingBracket(String expression , int index) throws Exception{
		if(expression.charAt(index)!='('){
			throw new Exception("Char at Index passed as argument is not '('");
		}
		int countOfOpenBracket=0;
		int currentChar = index;
		boolean closingBracketFound = false;

		while(currentChar<expression.length()){
			if(expression.charAt(currentChar) == '(' )
				countOfOpenBracket++;
			else if(expression.charAt(currentChar) == ')' )
				countOfOpenBracket--;
			if(countOfOpenBracket==0){
				closingBracketFound=true;
				break;
			}
			currentChar++;
		}
		if(closingBracketFound)
			return currentChar-1;

		
		return index;
	}
	
	public String getArgumentList() {
		return argumentList;
	}
	public Expression getFunctionBody() {
		return functionBody;
	}
	public Expression getResult() {
		return result;
	}
	public int getArgumentCount() {
		return argumentCount;
	}
	public void setArgumentList(String argument) {
		this.argumentList = argument;
	}
	public void setFunctionBody(Expression functionBody) {
		this.functionBody = functionBody;
	}
	public void setResult(Expression result) {
		this.result = result;
	}
}
