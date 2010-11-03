int main() {
	{int, int -> boolean} cmp = compareInt();

	print(cmp(1, 1));

	return 0;
}

{int, int -> boolean} compareInt() {
	return #{int x, int y -> (x >= y)} ;
}

