// non-local closure
int main() 
{
	{ int -> void } printer = getPrinter();
	
	printer(-1);
	printer(100);
	printer(-1);	
	printer(200);
	printer(-1);
	printer(-2);
	
	return 0;
}

{int -> void} getPrinter() {
	int counter = 0;
	double dcount = 0;
	
	return #{int n ->
		if (n == -1) 
			printInt(counter);
		else if (n == -2)
			printDouble(dcount);
		else {
			printInt(n);			
			counter++;
			dcount++;
		}
	};
}  