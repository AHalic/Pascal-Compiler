// Redefinição de identificador (Não case sensitive)

program Functions;

function Acumulado(add1, add2: real) : real;
begin
    Acumulado := add1 + add2;
end;

var
   acumulado: real;

begin
    acumulado := Acumulado(1, 1);
end.