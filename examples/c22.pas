program Arrays1;

var
    a: real;
    numeros1: array[1..20, 2..4] of integer;
    numeros2: array[1..20, 2..4] of real;
begin
    numeros1[1, 2] := 2;
    a := numeros1[1, 2];
end.
