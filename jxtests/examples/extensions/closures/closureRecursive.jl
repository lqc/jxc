int main() {
	{int -> int} factorial;
	
	factorial = #{ int n -> 
						int res;
						if (n <= 1)  
							res = 1;
						else 
							res = n * factorial(n-1);
						res
				};
				
	printInt(factorial(10)); 
	
	return 0;	
}