program Arrays1;

var
    a: integer;
    b: integer;
    c: integer;
    numeros: array[1..20, 2..4, 1..10] of integer;
begin
    a := 1;
    b := 2;
    c := 3;
    numeros[1, 2, 3] := 23;
    a := numeros[a, b, c];
    writeln(a);
end.
