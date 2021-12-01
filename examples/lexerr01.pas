program add(input, output);
 
(*** Simple program to add 2 integer arrays element by element.  ***)

const
  _size = 5;  (* wrong declaration *)
 
type
  intarray = array [1.._size] of integer;
 
var
   i: integer;
   a: intarray;
 
(* ***************************    adder    ********************************** *)
 
procedure adder(var a,b : intarray);
var
   i: integer;
 
begin
  for i := 1 to _size do
     b[i] := a[i] + b[i];
end;
 
(* **************************    main      ********************************** *)
 
begin
  for i := 1 to _size do 
     a[i] := i;
 
  writeln('The array before call to adder:');
  for i := 1 to _size do 
     write (a[i]);
  writeln;
 
  adder(a,a);
 
  writeln('The array after call to adder:');
  for i := 1 to _size do 
     write (a[i]);
  writeln;
end.