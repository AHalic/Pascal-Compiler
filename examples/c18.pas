program Arrays1;

var
    a: integer;
    b: integer;
    numeros: array[1..20, 2..4] of integer;
begin
    a := 1;
    b := 2;
    numeros[1, 2] := 2;
    a := numeros[a, b];
end.
