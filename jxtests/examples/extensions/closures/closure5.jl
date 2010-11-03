{int -> {int -> int}} curry({int, int -> int} f) 
{
	return #{int n -> #{int m -> f(n, m)} };
} 

{int, int -> int} uncurry({int -> {int -> int}} f)
{
	return #{ int n, int m ->
				{int -> int} g = f(n);
				g(m)
			};
}

{int -> string} apply(int x, {int, int -> string} f) {
	return #{int n ->  f x n };
}



int main() {
	{int, int -> int} foo = #{int n, int m -> n * m};
	{int -> {int -> int}} bar = curry(foo);
	{int, int -> int} zoozle = uncurry(bar);	
	printInt( zoozle(2, 3) );
	
	return 0;	
}