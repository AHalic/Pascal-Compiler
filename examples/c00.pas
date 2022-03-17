program FunctionWithVars;

function Teste(num1, num2:real): integer;
var
    nome: string;
begin
    nome := 'Gabriel';
    Teste := 12;
end;

var
    num0: integer;
    nomeProgram: string;
begin
    nomeProgram := 'jagunço';
    num0 := Teste(1.0, 2);
end.