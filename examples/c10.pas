// Exemplo de programa usando funções

program Functions;

function Somar(add1, add2: integer) : real;
begin
    Somar := add1 + add2;
end;

function Subtrair(sub1, sub2: integer) : integer;
begin
    Subtrair := sub1 - sub2;
end;

function Nada : real;
begin
    writeln('Estou no corpo da funcao.')
end;

function Hello(person: string) : string;
begin
    writeln('Olá ' + person + ': O famoso cabeção!');
end;

var
   add1: real;
   add2: real;

begin
    add1 := Somar(10, 1);
    writeln(add1);

    add1 := Subtrair(0, 1);
    writeln(add1);

    Nada;
    Hello('Marquinho do playson');
    writeln('Estou no corpo do programa principal.')
end.