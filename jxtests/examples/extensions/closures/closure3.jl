// closure as an argument
int main() 
{
	int x = 0;
	
	{ -> double } yielder = cast( #{ -> x++ } ); 
	
	printInt(x);
	printDouble( yielder() );
	printInt(x);
	printDouble( yielder() );
	printInt(x);
	printDouble( yielder() );
	printInt(x);
	
	return 0;
}

{-> double} cast( {-> int} f) {		 	
	return #{-> (double)f()};	 
}