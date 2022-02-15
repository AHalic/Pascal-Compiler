// Exemplo de programa usando funções

program Functions;

function Somar(add1, add2: integer) : integer;
begin
    Somar := add1 + add2;
end;

function Subtrair(sub1, sub2: integer) : integer;
begin
    Subtrair := sub1 - sub2;
end;

function Nada : real;
begin
    write('Estou no corpo da funcao.')
end;

var
   add1: real;
   add2: real;

begin
    add1 := Somar(1, 1);
    add1 := Subtrair(3, 1);
    write('Estou no corpo do programa principal.')
end.