program Arrays1;

var
    a: integer;
    numeros: array[1..20, 2..4] of integer;
begin
    numeros[1, 2] := 2;
    a := numeros[1, 2];
end.
