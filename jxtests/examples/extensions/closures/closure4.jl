// multiply closures
int main() 
{	
	{int -> int} l;
	
	l = emptyList();
	printList(l);
		
	for(int i=0; i < 10; i++) {
		l = append(l, i);		
	}
	
	printList(l);
	
	return 0;
}

void printList({int->int} list) 
{
	{int->int} x = list;
	printString("[");
	while(! isEmpty(x) ) {
		printInt( head(x) );
		x = tail(x);
	}
	printString("]");
}

{int -> int} emptyList() {
	return #{int n -> -1};
}

int head({int -> int} list) {
	return list(0);
}

boolean isEmpty({int -> int} list) {
	if( head(list) == -1 )
		return true;
	return false;
}

{int -> int} tail({int -> int} list) {	
	return #{int n -> list(n+1) }; 
}
	
{int->int} append({int->int} list, int x) 
{
	return #{int n ->
		int ret; 
		if (n == 0) ret = x; else ret = list(n-1);
		ret
	};
}