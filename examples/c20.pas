// Exemplo de programa usando função sem parâmetro

program Functions;

function Something: real;
var
    temp: integer;
begin
    Something := 1;
    temp := 1;
    if (temp >= 1) then
        write('I did something');
    temp := temp + 1
end;

var
    a: real;
begin
    // Something;
    write('I will do something');
end.