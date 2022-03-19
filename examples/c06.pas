program FunctionWithVars;

function Teste(num1, num2:real): integer;
var
    temp: integer;
begin
    Teste := 1;
    writeln(num1);
    writeln(num2);
end;

begin
    Teste(1.0, 2);
    write('sai da função\n');
end.