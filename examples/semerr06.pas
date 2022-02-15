// Exemplo de programa usando funções

program Functions;

function Somar(add1, add2: real) : real;
begin
    Somar := add1 + add2;
end;

var
   a: real;

begin
    a := Somar(1);
end.