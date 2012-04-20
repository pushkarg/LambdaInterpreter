package edu.uci.ics.inf212.lci.core;
import java.util.ArrayList;

public class Engine {
	public Expression inputExpression;
	public Expression outputExpression;

	public Engine(){
	}

	public Engine(String inputLambdaString){
		//Set input expression
		inputExpression = new Expression(inputLambdaString);
	}

	public String executeExpression(){
		//Preprocessing stage 
		inputExpression.preProcessing();

		for(int i=0;i<26;i++)
			Function.subscript[i] = 0;

		String evaluatedExpression = executeExpressionRec(inputExpression);
		System.out.println("Input Expression : " + inputExpression.getExpression() +"\nFinal value      : " + evaluatedExpression +"\n\n" );
			

		//!!!!!! Check condition where the post expression can have a reducible arg or if the reduced func can take more args from the post expr
		return evaluatedExpression;
	}

	protected String executeExpressionRec(Expression inputExpression){
		//System.out.println("\nInput to recursive func : " + inputExpression.getExpression() );
		//Use further reducible to recursively call betaRedex on lambdaFunction object

		//Parse expression into function body & arguments
		inputExpression.parseExpression();
		if(inputExpression.parseSuccessful()==false){
			//Cannot perform Beta reduction. Return the intial expression as it is
			System.out.println("Parse not successful");
			return inputExpression.getExpression();	
		}

		String preExpression = inputExpression.getPreExpression();
		String postExpression = inputExpression.getPostExpression();
		if(inputExpression.getFunction() == null)
			return preExpression;

		Function functionObject = new Function(inputExpression.getFunction());
		ArrayList<Expression> argumentList = inputExpression.getArguments();
		if(argumentList == null || argumentList.size() == 0){
			if(inputExpression.getFunction().isFurtherReducible()==false){
				return inputExpression.getFunction().getExpression();
			}
		}
		//System.out.println("Argument list : " );
		for(int i=0;i<argumentList.size();i++){
			//System.out.println(argumentList.get(i).getExpression());
		}

		if(postExpression==null)
			postExpression="";
		if(preExpression==null)
			preExpression="";
		//!!!!!!!!!!!!!!! Add additional arguments to the post expression
		//System.out.println("Details : PreExpr : "+ preExpression +" , postExpr : " +postExpression + " , funcBody : " +inputExpression.getFunction().getExpression() );

		
		//Substitute all the args
		int numberOfArgumentsNeededByFunction=functionObject.getArgumentCount();
		if(numberOfArgumentsNeededByFunction<argumentList.size()) {
			for(int prependExtraArgs=numberOfArgumentsNeededByFunction;prependExtraArgs<(argumentList.size());prependExtraArgs++) {
				postExpression=argumentList.get(prependExtraArgs).expressionString+postExpression;
			}
		}
		
		functionObject.alphaRedex();
		
		int minimumNumberOfArgs = numberOfArgumentsNeededByFunction<argumentList.size()?numberOfArgumentsNeededByFunction:argumentList.size();
		for( int i=0;i<minimumNumberOfArgs ;i++){
			//perform Alpha reduction on the function
			//System.out.println("Argument "+i+" is "+argumentList.get(i).expressionString);
			functionObject.betaRedex(argumentList.get(i) , i);
			
		}
		//System.out.println("Str after Beta redex    : " + functionObject.getEvaluatedExpression().getExpression());

		//Logic - 
		//1. Append the pre & post expressions to the reduced function body
		//2. Check if the Beta reduced expression is further reducable
		// Case 1 :  If the betda reduced expr is reducable, remove the (L & ) from this expr & parse it
		//3 Check if the complete expr - pre + Beta reduced func + post is further reducable.
		//4 Case 2 : If Yes, call parse on the entire expr

		Expression finalExpression = functionObject.getEvaluatedExpression();
		String intermediateExpressionStr = finalExpression.getExpression();
		intermediateExpressionStr = removeDoubleBrackets(intermediateExpressionStr);
		//System.out.println("String after beta redex : " + intermediateExpressionStr);

		finalExpression.setExpression(intermediateExpressionStr);
		if(preExpression == null)
			preExpression = "";
		if(postExpression ==null)
			postExpression= "";

		String finalExpressionStr = finalExpression.getExpression();

		if(finalExpressionStr.charAt(0) !='('){
			finalExpressionStr = "(" + finalExpressionStr + ")";
			finalExpression.setExpression( finalExpressionStr );
		}
		//System.out.println("Case 2 ");
		Expression completeExpression = new Expression( preExpression +finalExpression.getExpression()+postExpression);
		//System.out.println("Func evaluated completely : " +functionObject.hasEvaluatedCompletely() +" , post expr : " + postExpression );

		Expression intermediateExpression = new Expression(completeExpression.getExpression() );
		intermediateExpression.parseExpression();
		ArrayList<Expression> argumentListTemp = intermediateExpression.getArguments();


		if(argumentListTemp!=null){
			boolean somethingLeftToReduce = true, caseOne , caseTwo ;
			//Expression completeExpression = new Expression( preExpression +finalExpression.getExpression()+postExpression);
			while(somethingLeftToReduce){
				//Get the complete expression and check if it is reducable
				if( completeExpression.isFurtherReducible() == true ){
					//System.out.println("Case 2 called. Ip : "+ completeExpression.getExpression() );
					String evaluatedExpr = executeExpressionRec( completeExpression);
					completeExpression = new Expression( evaluatedExpr ) ;
				}
					break;
			}
		}

		//System.out.println("calling isFurtherRed on : " +completeExpression.getExpression() );
		if( completeExpression.isFurtherReducible()==true){
			//Case 1 : Remove the "(L" & ")" from the Beta reduced expression and call parse
			//System.out.println("FINAL Expr : " + finalExpression.getExpression() );
			//System.out.println("Case 1  being called on expr :" + finalExpression.getExpression() );

			String functionBodyStr = finalExpression.getExpression();
			//System.out.println("Case 1 input : " +functionBodyStr);
			int startIndexOfFirstFunction = functionBodyStr.indexOf("(L");
			int endIndexOfFirstFunction =0;
			try{
				endIndexOfFirstFunction = findMatchingBracket( functionBodyStr, startIndexOfFirstFunction );
			}catch(Exception e){
				 System.out.println("Exception caught. details : " + e.toString() );
				 System.exit(1);
			}

			String preExpr = functionBodyStr.substring(0, functionBodyStr.indexOf(".") + 1 ) +"(" ;
			String postExpr = ")" +  functionBodyStr.substring(endIndexOfFirstFunction + 1 );
			if(preExpr == null)
				preExpr="";
			if(postExpr == null)
				postExpr="";

			//System.out.println("pre expr : " + preExpr + " , post : "+ postExpr );
			int startOfReducibleFunction = functionBodyStr.indexOf(".");
			String reducibleFunction= functionBodyStr.substring(startOfReducibleFunction + 1 , endIndexOfFirstFunction + 1);
			//System.out.println("Reducible func : "+ reducibleFunction + ", start index : " +startIndexOfFirstFunction + " , functionBodyStr : " + functionBodyStr );

			//System.out.println("Case 1 called " );
			String evaluatedExpr = executeExpressionRec( new Expression( reducibleFunction ) );
			finalExpression = new Expression( preExpr + evaluatedExpr + postExpr);
			//System.out.println("Combining Preex - mine : "+preExpr+"     +     " +evaluatedExpr+ "      +    "+ postExpr);
			completeExpression = finalExpression;

		}
		//The resultant expression can be simplified further

		String completeExpressionStr =  completeExpression.getExpression();
		if(completeExpressionStr.charAt(0) !='(' && completeExpressionStr.charAt( completeExpressionStr.length() -1 )!=')')
			completeExpressionStr = "(" + completeExpressionStr +")";
		//System.out.println("Val returned from rec expr : " + completeExpression.getExpression());

		//Remove double brackets from the return expression
		completeExpressionStr = removeDoubleBrackets(completeExpressionStr);
		return completeExpressionStr;
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

	protected String removeDoubleBrackets(String expression){
		StringBuffer expressionStrBuffer = new StringBuffer(expression);
		int index=0;
		while(index<expressionStrBuffer.length()){
			if(expressionStrBuffer.charAt(index) == '(' && expressionStrBuffer.charAt(index+1)=='('){
				int indexOfMatching1=0 ,indexOfMatching2=0;
				try{
					indexOfMatching1 = findMatchingBracket(expressionStrBuffer.toString(),index);
					indexOfMatching2 = findMatchingBracket(expressionStrBuffer.toString(),index + 1);
				}catch(Exception e){
				}
				if(indexOfMatching2 == indexOfMatching1 -1 ){
					expressionStrBuffer.replace(index,index+1,"");	
					expressionStrBuffer.replace(indexOfMatching1-1,indexOfMatching1,"");	
				}
			}
			index++;
		}
		return expressionStrBuffer.toString();
	}

}
