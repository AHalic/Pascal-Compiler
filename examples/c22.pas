program Arrays1;

var
    numeros0: array[-20..20] of integer;
    numeros1: array[-20..20] of real;
    bools0: array[2..18] of boolean;
    palavras0: array[-1..1] of string;

begin
    numeros0[-10] := 5;
    writeln(numeros0[-10]);

    numeros1[-5] := 5.0;
    writeln(numeros1[-5]);

    bools0[7] := true;
    writeln(bools0[7]);

    palavras0[0] := 'teste';
    writeln(palavras0[0]);
end.