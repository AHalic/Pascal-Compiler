program Arrays1;

var
    a: integer;
    numeros: array[-20..20] of integer;

begin
    a := -20;

    while a < 0 do
    begin
        numeros[a] := -10 * a;
        a := a + 1;
    end;

    a := -20;

    while a < 0 do
    begin
        writeln(numeros[a]);
        a := a + 1;
    end;

end.