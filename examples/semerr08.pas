// Declaração de array com intervalos inconsistentes (Negativo)

program Functions;

var
   numeros: array[1..10,-5..-10] of real;

begin
    numeros[1, 1] := 42.0;
end.