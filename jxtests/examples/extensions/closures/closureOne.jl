int main() {
	// simple local closure 
	{ int -> void } printer = #{int x -> printInt(x); };
	
	printer(42);
	return 0;
}