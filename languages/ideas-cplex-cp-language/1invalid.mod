using CP;

range r = 0..100;

// Decision variables:
dvar int InputErrors in r;

// Constraints:
subject to {  
	InputErrors <= 2;
	InputErrors > 1;
}