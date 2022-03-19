program Arrays1;

var
    a: integer;
    numeros: array[0..20] of integer;

begin
    a := 0;

    while a < 20 do
    begin
        numeros[a] := 10 * a;
        a := a + 1;
    end;

    a := 0;

    while a < 20 do
    begin
        writeln(numeros[a]);
        a := a + 1;
    end;

end.